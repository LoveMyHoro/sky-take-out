package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @ClassName ReportController
 * @Author iove
 * @Date 2025/1/5 下午11:14
 * @Version 1.0
 * @Description TODO
 **/
@RestController
@RequestMapping("admin/report")
@Slf4j
@Api(tags = "数据统计相关接口")
public class ReportController {
	@Autowired
	private ReportService reportService;

	/**
	 * 统计1指定时间之间的营业额数据
	 * @param begin
	 * @param end
	 * @return
	 */
	@GetMapping("/turnoverStatistics")
	public Result<TurnoverReportVO>turnoverStatistics(
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate end) {
		log.info("查询的日期是：{}到{}",begin,end);
		TurnoverReportVO turnoverStatistics = reportService.getTurnoverStatistics(begin, end);
		log.info("返回的日期列表为：{}", turnoverStatistics.getDateList());
		return Result.success(turnoverStatistics);
	}

	/**
	 * 查询员工总数和新员工数目
	 * @param begin
	 * @param end
	 * @return
	 */
	@GetMapping("/userStatistics")
	public Result<UserReportVO>userStatics(
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate end){
		log.info("查询的日期为：{}到{}",begin,end);
		UserReportVO userStatics=reportService.getUserStatistics(begin, end);
		log.info("员工总数：{}", userStatics.getTotalUserList());
		log.info("新员工总数：{}",userStatics.getNewUserList());
		return Result.success(userStatics);
	}

	/**
	 * 查询订单统计信息
	 * @param begin
	 * @param end
	 * @return
	 */
	@GetMapping("/ordersStatistics")
	public Result<OrderReportVO>ordersStatics(
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate end){
		log.info("查询的日期为：{}到{}",begin,end);
		OrderReportVO orderStatics=reportService.getOrderStatistics(begin, end);
		return Result.success(orderStatics);
	}
	/**
	 * 查询订单排行top10
	 * @param begin
	 * @param end
	 * @return
	 */
	@GetMapping("/top10")
	public Result<SalesTop10ReportVO> getSalesTop10(
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate begin,
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate end){
		log.info("查询的日期为：{}到{}",begin,end);
		SalesTop10ReportVO salesTop10 =reportService.getSalesTop10(begin, end);
		return Result.success(salesTop10);
	}

	/**
	 * 导出Excel运营数据报表
	 * @param response
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		reportService.exportBusinessData(response);
	}

}
