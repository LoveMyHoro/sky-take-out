package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MyTask
 * @Author iove
 * @Date 2025/1/4 下午10:26
 * @Version 1.0
 * @Description 自定义定时任务类
 **/
@Component
@Slf4j
public class OrderTask {
	@Autowired
	private OrderMapper orderMapper;
	/**
	 * 每分钟处理超时订单
	 */
	@Scheduled(cron = "0 5 * * * ?")
	public void processTimeoutOrder() {
		log.info("处理超时订单{}", LocalDateTime.now());
		LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);
		List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, orderTime);
		if(ordersList!=null && ordersList.size()>0) {
			for(Orders order : ordersList) {
				order.setStatus(Orders.CANCELLED);
				order.setCancelReason("订单已超时！");
				order.setCancelTime(LocalDateTime.now());
				orderMapper.update(order);
			}
		}
	}

	/**
	 * 处理一直处于派送中的订单
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void processDeliveryOrder(){
		log.info("定时处理派送中的订单{}", LocalDateTime.now());
		LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-60);
		List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, orderTime);
		if(ordersList!=null && ordersList.size()>0) {
			for(Orders order : ordersList) {
				order.setStatus(Orders.COMPLETED);
				orderMapper.update(order);
			}
		}
	}
}
