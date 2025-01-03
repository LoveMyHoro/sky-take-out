package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController
 * @Author iove
 * @Date 2024/12/27 下午10:02
 * @Version 1.0
 * @Description TODO
 **/
@RestController("userOrderController")
@Slf4j
@RequestMapping("/user/order")
@Api(tags = "用户端订单相关接口")
public class OrderController {
	@Autowired
	private OrderService orderService;

	/**
	 * 用户下单接口
	 * @param ordersSubmitDTO
	 * @return
	 */
	@PostMapping("/submit")
	@ApiOperation("用户下单")
	public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
		log.info("用户订单信息：{}", ordersSubmitDTO);
		OrderSubmitVO orderSubmitVO=orderService.submitOrder(ordersSubmitDTO);
		return Result.success(orderSubmitVO);
	}
	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	@PutMapping("/payment")
	@ApiOperation("订单支付")
	public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		log.info("订单支付：{}", ordersPaymentDTO);
		OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
		log.info("生成预支付交易单：{}", orderPaymentVO);
		String orderNumber=ordersPaymentDTO.getOrderNumber();
		orderService.paySuccess(orderNumber);
		return Result.success(orderPaymentVO);
	}

	/**
	 * 查看历史订单记录
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/historyOrders")
	public Result<PageResult> historyOrders(int page, int pageSize,Integer status) {
		log.info("历史订单分页查询：{},page:{},pageSize:{}", status,page,pageSize);
		PageResult pageResult=orderService.queryOrders(page,pageSize,status);
		log.info("查询到的订单分页为：{}", pageResult);
		return Result.success(pageResult);
	}

	/**
	 * 根据id查询订单详情
	 * @param id
	 * @return
	 */
	@GetMapping("/orderDetail/{id}")
	public Result orderDetail(@PathVariable Long id) {
		log.info("根据id查询订单详情：{}",id);
		OrderVO orderVO=orderService.details(id);
		log.info("返回的订单详情为：{}",orderVO);
		return Result.success(orderVO);
	}

	/**
	 * 根据id取消订单
	 * @param id
	 * @return
	 */
	@PutMapping("/cancel/{id}")
	public Result cancel(@PathVariable Long id) throws Exception {
		orderService.cancelById(id);
		return Result.success();
	}

	/**
	 * 再来一单
	 * @param id
	 * @return
	 */
	@PostMapping("/repetition/{id}")
	public Result repetition(@PathVariable Long id) {
		//未完成
		log.info("再次下单的订单id为：{}",id);
		orderService.repetition(id);
		return Result.success();
	}

}
