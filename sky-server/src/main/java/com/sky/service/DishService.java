package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {
	void saveWithFavor(DishDTO dishDTO);

	PageResult pageQuery(DishPageQueryDTO pageQueryDTO);

	void deleteBatch(List<Long> ids);
}
