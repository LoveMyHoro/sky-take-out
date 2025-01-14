package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName ReportServiceImpl
 * @Author iove
 * @Date 2025/1/5 下午11:22
 * @Version 1.0
 * @Description TODO
 **/
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private WorkspaceService workspaceService;
	@Override
	public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
		List<LocalDate> dateList=new ArrayList<>();
		dateList.add(begin);
		//1.查询日期列表
		while(!begin.equals(end)){
			begin=begin.plusDays(1);
			dateList.add(begin);
		}
		List<Double>turnoverList=new ArrayList<>();
		//2.查询当日营业额
		for (LocalDate localDate : dateList) {
			LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
			LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
			Map map=new HashMap();
			map.put("begin",beginTime);
			map.put("end",endTime);
			map.put("status",5);
			Double turnover=orderMapper.sumByMap(map);
			turnover=turnover==null?0.0:turnover;
			turnoverList.add(turnover);
		}
		return TurnoverReportVO.builder()
				.dateList(StringUtils.join(dateList,','))
				.turnoverList(StringUtils.join(turnoverList,','))
				.build();
	}

	@Override
	public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
		List<LocalDate> dateList=new ArrayList<>();
		dateList.add(begin);
		//1.查询日期列表
		while(!begin.equals(end)){
			begin=begin.plusDays(1);
			dateList.add(begin);
		}
		//2.查询员工数目
		List<Integer>totalUserList=new ArrayList<>();
		List<Integer>newUserList=new ArrayList<>();

		for (LocalDate localDate : dateList) {
			LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
			LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
			Map map=new HashMap();

			map.put("end",endTime);
			Integer totalUser=userMapper.sumByMap(map);
			totalUser=totalUser==null?0:totalUser;
			totalUserList.add(totalUser);

			map.put("begin",beginTime);
			Integer totalNewUser=userMapper.sumByMap(map);
			totalNewUser=totalNewUser==null?0:totalNewUser;
			newUserList.add(totalNewUser);

		}
		return UserReportVO.builder()
				.dateList(StringUtils.join(dateList,","))
				.newUserList(StringUtils.join(newUserList,","))
				.totalUserList(StringUtils.join(totalUserList,","))
				.build();
	}

	@Override
	public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
		List<LocalDate> dateList=new ArrayList<>();
		dateList.add(begin);
		//1.查询日期列表
		while(!begin.equals(end)){
			begin=begin.plusDays(1);
			dateList.add(begin);
		}
		//2.根据日期查询订单总数和有效订单总数
		List<Integer>OrderCountList=new ArrayList<>();
		List<Integer>validOrderCountList=new ArrayList<>();
		for (LocalDate localDate : dateList) {
			LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
			LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
			Map map=new HashMap();

			map.put("begin",beginTime);
			map.put("end",endTime);
			Integer totalOrder=orderMapper.countByMap(map);
			map.put("status",5);
			Integer validOrder=orderMapper.countByMap(map);
			OrderCountList.add(totalOrder);
			validOrderCountList.add(validOrder);
		}
		//3.利用Stream流计算订单总数
		Integer totalOrder = OrderCountList.stream().reduce(Integer::sum).get();
		Integer totalValidOrder = validOrderCountList.stream().reduce(Integer::sum).get();
		log.info("订单集合:{}",OrderCountList);
		log.info("有效订单集合:{}",validOrderCountList);
		//4.计算订单有效率
		Double valid=totalOrder==0?0.0:totalValidOrder*1.0/totalOrder;
		return OrderReportVO.builder()
				.dateList(StringUtils.join(dateList,","))
				.orderCountList(StringUtils.join(OrderCountList,","))
				.validOrderCountList(StringUtils.join(validOrderCountList,","))
				.totalOrderCount(totalOrder)
				.validOrderCount(totalValidOrder)
				.orderCompletionRate(valid)
				.build();

	}

	@Override
	public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
		LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
		LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
		Map map=new HashMap();
		map.put("begin",beginTime);
		map.put("end",endTime);
		List<GoodsSalesDTO> goodsSalesDTOList=orderMapper.getSalesTop10(map);
		//使用stream流
		List<String> orderNameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
		List<Integer> orderNumberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
		return SalesTop10ReportVO.builder()
				.nameList(StringUtils.join(orderNameList,","))
				.numberList(StringUtils.join(orderNumberList,","))
				.build();
	}

	@Override
	public void exportBusinessData(HttpServletResponse response) throws IOException {
		LocalDate dateBegin = LocalDate.now().minusDays(30);//这个表示30天前的日期
		LocalDate dateEnd = LocalDate.now().minusDays(1);//这个表示昨天
		LocalDateTime begin = LocalDateTime.of(dateBegin, LocalTime.MIN);
		LocalDateTime end = LocalDateTime.of(dateEnd, LocalTime.MAX);
		//1.查询数据库获取营业数据
		BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
		//2.通过POI将数据写入Excel中
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
		XSSFWorkbook excel=new XSSFWorkbook(in);
		XSSFSheet sheet = excel.getSheet("Sheet1");
		sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);
		//第4行的数据写入
		XSSFRow row = sheet.getRow(3);
		row.getCell(2).setCellValue(businessDataVO.getTurnover());
		row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
		row.getCell(6).setCellValue(businessDataVO.getNewUsers());
		//第5行的数据写入
		row=sheet.getRow(4);
		row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
		row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
		//明细数据
		for (int i = 0; i < 30; i++) {
			LocalDate date=dateBegin.plusDays(i);
			BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

			row= sheet.getRow(7 + i);
			row.getCell(1).setCellValue(date.toString());
			row.getCell(2).setCellValue(businessData.getTurnover());
			row.getCell(3).setCellValue(businessData.getValidOrderCount());
			row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
			row.getCell(5).setCellValue(businessData.getUnitPrice());
			row.getCell(6).setCellValue(businessData.getNewUsers());
		}
		//3.通过输出流将Excel文件下载到客户端浏览器
		ServletOutputStream out = response.getOutputStream();
		excel.write(out);
		out.close();
		excel.close();

	}
}
