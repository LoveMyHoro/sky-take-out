package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Author iove
 * @Date 2024/12/27 下午10:06dish
 * @Version 1.0
 * @Description TODO
 **/
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderDetailMapper orderDetailMapper;
	@Autowired
	private AddressBookMapper addressBookMapper;
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private WeChatPayUtil weChatPayUtil;

	/**
	 * 1.提交订单
	 * @param ordersSubmitDTO
	 * @return
	 */
	@Override
	@Transactional
	public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
		//处理各种业务异常
		AddressBook addressBook=addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
		if(addressBook==null){
			throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
		}
		//查询用户当前购物车数据
		Long userId= BaseContext.getCurrentId();

		ShoppingCart shoppingCart=new ShoppingCart();
		shoppingCart.setUserId(userId);
		List<ShoppingCart>shoppingCartList= shoppingCartMapper.list(shoppingCart);
		if(shoppingCartList==null||shoppingCartList.size()==0){
			throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
		}
		//向表中插入一条数据
		Orders orders=new Orders();
		BeanUtils.copyProperties(ordersSubmitDTO,orders);
		orders.setOrderTime(LocalDateTime.now());
		//支付状态
		orders.setPayStatus(Orders.UN_PAID);
		//订单状态
		orders.setStatus(Orders.PENDING_PAYMENT);
		orders.setNumber(String.valueOf(System.currentTimeMillis()));
		orders.setPhone(addressBook.getPhone());
		orders.setConsignee(addressBook.getConsignee());
		orders.setUserId(userId);
		orders.setAddress(addressBook.getDetail());
		orderMapper.insert(orders);
		//向订单明细表中插入n条数据
		List<OrderDetail> orderDetailList=new ArrayList<>();
		for(ShoppingCart cart : shoppingCartList){
			OrderDetail orderDetail=new OrderDetail();
			BeanUtils.copyProperties(cart,orderDetail);
			orderDetail.setOrderId(orders.getId());
			orderDetailList.add(orderDetail);
		}
		orderDetailMapper.insertBatch(orderDetailList);
		//清口购物车
		shoppingCartMapper.deleteByUserId(userId);
		//封装vo返回数据
		OrderSubmitVO submitVO = OrderSubmitVO.builder()
				.id(orders.getId())
				.orderTime(orders.getOrderTime())
				.orderNumber(orders.getNumber())
				.orderAmount(orders.getAmount())
				.build();
		return submitVO;
	}
	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		// 当前登录用户id
		Long userId = BaseContext.getCurrentId();
		User user = userMapper.getById(userId);

//		//调用微信支付接口，生成预支付交易单
//		JSONObject jsonObject = weChatPayUtil.pay(
//				ordersPaymentDTO.getOrderNumber(), //商户订单号
//				new BigDecimal(0.01), //支付金额，单位 元
//				"苍穹外卖订单", //商品描述
//				user.getOpenid() //微信用户的openid
//		);



		return null;
	}

	/**
	 * 支付成功，修改订单状态
	 *
	 * @param outTradeNo
	 */
	public void paySuccess(String outTradeNo) {

		// 根据订单号查询订单
		Orders ordersDB = orderMapper.getByNumber(outTradeNo);

		// 根据订单id更新订单的状态、支付方式、支付状态、结账时间
		Orders orders = Orders.builder()
				.id(ordersDB.getId())
				.status(Orders.TO_BE_CONFIRMED)
				.payStatus(Orders.PAID)
				.checkoutTime(LocalDateTime.now())
				.build();

		orderMapper.update(orders);
	}

	@Override
	public PageResult queryOrders(int pageNum,int pageSize,Integer status) {
		Long userId = BaseContext.getCurrentId();
		//分页查询
		PageHelper.startPage(pageNum,pageSize);
		OrdersPageQueryDTO ordersPageQueryDTO = OrdersPageQueryDTO.builder()
				.status(status)
				.userId(userId)
				.build();

		Page<Orders> page=orderMapper.pageQuery(ordersPageQueryDTO);
		List<OrderVO>list=new ArrayList();
		if (page!=null && page.getTotal()>0){
			for(Orders orders:page){
				Long orderId=orders.getId();
				List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(orderId);
				OrderVO orderVO=new OrderVO();
				BeanUtils.copyProperties(orders,orderVO);
				orderVO.setOrderDetailList(orderDetails);
				list.add(orderVO);
			}
		}
		return new PageResult(page.getTotal(),list);
	}

	@Override
	public OrderVO details(Long id) {
		Orders orders=orderMapper.getById(id);
		List<OrderDetail> orderDetailsList = orderDetailMapper.getByOrderId(orders.getId());
		OrderVO orderVO=new OrderVO();
		BeanUtils.copyProperties(orders,orderVO);
		orderVO.setOrderDetailList(orderDetailsList);
		return orderVO;
	}



	@Override
	public void cancelById(Long id) throws Exception {
		// 根据id查询订单
		Orders ordersDB = orderMapper.getById(id);

		// 校验订单是否存在
		if (ordersDB == null) {
			throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
		}

		//订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
		if (ordersDB.getStatus() > 2) {
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}

		Orders orders = new Orders();
		orders.setId(ordersDB.getId());

		// 订单处于待接单状态下取消，需要进行退款
		if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
			//调用微信支付退款接口
//			weChatPayUtil.refund(
//					ordersDB.getNumber(), //商户订单号
//					ordersDB.getNumber(), //商户退款单号
//					new BigDecimal(0.01),//退款金额，单位 元
//					new BigDecimal(0.01));//原订单金额

			//支付状态修改为 退款
			orders.setPayStatus(Orders.REFUND);
		}

		// 更新订单状态、取消原因、取消时间
		orders.setStatus(Orders.CANCELLED);
		orders.setCancelReason("用户取消");
		orders.setCancelTime(LocalDateTime.now());
		orderMapper.update(orders);
	}

	@Override
	public void repetition(Long id) {
		Orders orders = orderMapper.getById(id);
		List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
		for (OrderDetail orderDetail : orderDetails) {
			ShoppingCart shoppingCart=new ShoppingCart();
			BeanUtils.copyProperties(orders,shoppingCart);
			BeanUtils.copyProperties(orderDetail,shoppingCart);
			shoppingCartMapper.insert(shoppingCart);
		}


	}

	@Override
	public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
		PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
		Page<Orders>page=orderMapper.pageQuery(ordersPageQueryDTO);
		List<OrderVO>orderVOList=getOrderVOList(page);
		return new PageResult(page.getTotal(),orderVOList);
	}



	/**
	 * 通过orders获得orderVO
	 * @param page
	 * @return
	 */
	public List<OrderVO> getOrderVOList(Page<Orders> page){
		List<Orders> ordersList = page.getResult();
		List<OrderVO> orderVOList=new ArrayList<>();
		if (ordersList!=null && ordersList.size()>0){
			for (Orders orders : ordersList) {
				OrderVO orderVO=new OrderVO();
				BeanUtils.copyProperties(orders,orderVO);
				String orderDishStr = getOrderDishStr(orders);
				orderVO.setOrderDishes(orderDishStr);
				orderVOList.add(orderVO);
			}
		}
		return orderVOList;
	}

	/**
	 * 获取菜品名字
	 * @param orders
	 * @return
	 */
	public String getOrderDishStr(Orders orders){
		// 查询订单菜品详情信息（订单中的菜品和数量）
		List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

		// 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
		List<String> orderDishList = orderDetailList.stream().map(x -> {
			String orderDish = x.getName() + "*" + x.getNumber() + ";";
			return orderDish;
		}).collect(Collectors.toList());

		// 将该订单对应的所有菜品信息拼接在一起
		return String.join("", orderDishList);
	}

	/**
	 * 统计各个状态的订单的数量
	 * @return
	 */
	@Override
	public OrderStatisticsVO sum() {
		//待接单数量
		Integer toBeConfirmed=orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
		//待派送数量
		Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
		//派送中数量
		Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
		OrderStatisticsVO orderStatisticsVO = OrderStatisticsVO.builder()
				.toBeConfirmed(toBeConfirmed)
				.confirmed(confirmed)
				.deliveryInProgress(deliveryInProgress)
				.build();
		return orderStatisticsVO;
	}

	@Override
	public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
		Orders orders = orderMapper.getById(ordersConfirmDTO.getId());
		orders.setStatus(Orders.CONFIRMED);
		orders.setId(ordersConfirmDTO.getId());
		orderMapper.update(orders);
	}

	@Override
	public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
		Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());
		// 订单只有存在且状态为2（待接单）才可以拒单
		if (ordersDB == null||!ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}
		//支付状态
		Integer payStatus = ordersDB.getPayStatus();
		if (payStatus == Orders.PAID) {
//			//用户已支付，需要退款
//			String refund = weChatPayUtil.refund(
//					ordersDB.getNumber(),
//					ordersDB.getNumber(),
//					new BigDecimal(0.01),
//					new BigDecimal(0.01));
//			log.info("申请退款：{}", refund);
		}
		Orders orders = Orders.builder()
				.id(ordersDB.getId())
				.status(Orders.CANCELLED)
				.rejectionReason(ordersRejectionDTO.getRejectionReason())
				.cancelTime(LocalDateTime.now())
				.build();
		orderMapper.update(orders);
	}

	@Override
	public void cancel(OrdersCancelDTO ordersCancelDTO) {
		Orders ordersDB=orderMapper.getById(ordersCancelDTO.getId());
		if(ordersDB==null||!ordersDB.getStatus().equals(Orders.CONFIRMED)){
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}
		Orders orders = Orders.builder()
				.id(ordersDB.getId())
				.status(Orders.CANCELLED)
				.rejectionReason(ordersCancelDTO.getCancelReason())
				.cancelTime(LocalDateTime.now())
				.build();
		orderMapper.update(orders);
	}

	@Override
	public void delivery(Long id) {
		Orders ordersDB=orderMapper.getById(id);
		if(ordersDB==null||!ordersDB.getStatus().equals(Orders.CONFIRMED)){
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}
		ordersDB.setStatus(Orders.DELIVERY_IN_PROGRESS);
		orderMapper.update(ordersDB);
	}

	@Override
	public void complete(Long id) {
		Orders ordersDB=orderMapper.getById(id);
		if(ordersDB==null||!ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}
		//更新订单状态
		ordersDB.setStatus(Orders.COMPLETED);
		ordersDB.setDeliveryTime(LocalDateTime.now());
		orderMapper.update(ordersDB);
	}


}
