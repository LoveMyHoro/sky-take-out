package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.entity.Dish;

public interface DishService {
	void saveWithFavor(DishDTO dishDTO);
}
