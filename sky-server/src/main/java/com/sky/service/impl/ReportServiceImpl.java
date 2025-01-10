package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
		List<Double>totalUserList=new ArrayList<>();
		List<Double>newUserList=new ArrayList<>();

		for (LocalDate localDate : dateList) {
			LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
			LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
			Map map=new HashMap();

			map.put("end",endTime);
			Double totalNewUser=userMapper.sumByMap(map);
			totalNewUser=totalNewUser==null?0.0:totalNewUser;
			newUserList.add(totalNewUser);


			map.put("begin",beginTime);
			Double totalUser=userMapper.sumByMap(map);
			totalUser=totalUser==null?0.0:totalUser;
			totalUserList.add(totalUser);
		}
		return UserReportVO.builder()
				.dateList(StringUtils.join(newUserList,","))
				.newUserList(StringUtils.join(newUserList,","))
				.totalUserList(StringUtils.join(totalUserList,","))
				.build();
	}
}
