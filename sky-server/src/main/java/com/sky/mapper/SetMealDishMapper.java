package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
	/**
	 * 根据菜品名查询套餐id
	 * @return
	 */
	List<Long> getSetMealIdByDishIds(List<Long>dishIds);
}
