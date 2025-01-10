package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
	OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
	/**
	 * 订单支付
	 * @param ordersPaymentDTO
	 * @return
	 */
	OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

	/**
	 * 支付成功，修改订单状态
	 * @param outTradeNo
	 */
	void paySuccess(String outTradeNo);

	PageResult queryOrders(int pageNum,int pageSize,Integer status);


	void cancelById(Long id) throws Exception;

	void repetition(Long id);

	PageResult pageQuery(OrdersPageQueryDTO pageQueryDTO);

	OrderStatisticsVO sum();


	OrderVO details(Long id);

	void confirm(OrdersConfirmDTO ordersConfirmDTO);

	void rejection(OrdersRejectionDTO ordersRejectionDTO);

	void cancel(OrdersCancelDTO ordersCancelDTO);

	void delivery(Long id);

	void complete(Long id);

	void reminder(Long id);
}
