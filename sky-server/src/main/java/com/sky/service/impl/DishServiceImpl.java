package com.sky.service.impl;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName DishServiceImpl
 * @Author iove
 * @Date 2024/12/15 下午9:04
 * @Version 1.0
 * @Description TODO
 **/
@Service
@Slf4j
public class DishServiceImpl implements DishService {
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private DishFlavorMapper dishFlavorMapper;
	@Override
	public void saveWithFavor(DishDTO dishDTO) {
		Dish dish=new Dish();
		BeanUtils.copyProperties(dishDTO,dish);
		dishMapper.insert(dish);
		Long dishId = dish.getId();
		log.info("dish回显的id={}",dishId);
		List<DishFlavor> flavors=dishDTO.getFlavors();
		if(flavors!=null||flavors.size()!=0){
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);
			});
			dishFlavorMapper.insertBath(flavors);
		}
	}

}