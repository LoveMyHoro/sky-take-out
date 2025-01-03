package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName OrderController
 * @Author iove
 * @Date 2025/1/3 下午3:52
 * @Version 1.0
 * @Description TODO
 **/
@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {
	@Autowired
	private OrderService orderService;

	/**
	 * 订单搜索image
	 * @param pageQueryDTO
	 * @return
	 */
	@GetMapping("/conditionSearch")
	public Result conditionSearch(OrdersPageQueryDTO pageQueryDTO) {
		PageResult pageResult=orderService.pageQuery(pageQueryDTO);
		log.info("返回的用户订单数据为：{}",pageResult.getRecords());
		return Result.success(pageResult);
	}

	/**
	 * 统计每个订单分类的数量
	 * @return
	 */
	@GetMapping("/statistics")
	public Result statistics(){
		OrderStatisticsVO orderStatisticsVO=orderService.sum();
		return Result.success(orderStatisticsVO);
	}

	/**
	 * 获取订单详情
	 * @param id
	 * @return
	 */
	@GetMapping("/details/{id}")
	public Result details(@PathVariable Long id){
		OrderVO details = orderService.details(id);
		return Result.success(details);
	}

	/**
	 * 接单
	 * @param ordersConfirmDTO
	 * @return
	 */
	@PutMapping("confirm")
	public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
		orderService.confirm(ordersConfirmDTO);
		return Result.success();
	}

	/**
	 * 拒单
	 * @param ordersRejectionDTO
	 * @return
	 */
	@PutMapping("rejection")
	public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
		orderService.rejection(ordersRejectionDTO);
		return Result.success();
	}

	/**
	 * 取消订单
	 * @param ordersCancelDTO
	 * @return
	 */
	@PutMapping("cancel")
	public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
		orderService.cancel(ordersCancelDTO);
		return Result.success();
	}

	/**
	 * 派送订单
	 * @param id
	 * @return
	 */
	@PutMapping("/delivery/{id}")
	public Result delivery(@PathVariable Long id){
		orderService.delivery(id);
		return Result.success();
	}
	@PutMapping("/complete/{id}")
	public Result complete(@PathVariable Long id){
		orderService.complete(id);
		return Result.success();
	}
}
