package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
	void saveWithFavor(DishDTO dishDTO);

	PageResult pageQuery(DishPageQueryDTO pageQueryDTO);

	void deleteBatch(List<Long> ids);
	/**
	 * 根据id查询菜品和对应的口味数据
	 *
	 * @param id
	 * @return
	 */
	DishVO getByIdWithFlavor(Long id);
	/**
	 * 根据id修改菜品基本信息和对应的口味信息
	 *
	 * @param dishDTO
	 */
	void updateWithFlavor(DishDTO dishDTO);
}
