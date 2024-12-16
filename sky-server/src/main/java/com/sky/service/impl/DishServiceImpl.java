package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.tools.ISupportsMessageContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
	@Autowired
	private HttpMessageConverters messageConverters;
	@Autowired
	private SetMealDishMapper setMealDishMapper;

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

	@Override
	public PageResult pageQuery(DishPageQueryDTO pageQueryDTO) {
		PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
		Page<DishVO> page=dishMapper.pageQuery(pageQueryDTO);
		return new PageResult(page.getTotal(),page.getResult());
	}
	@Transactional
	@Override
	public void deleteBatch(List<Long> ids) {
		log.info("待删除的菜品id:{}",ids);
		//1.判断当前菜品的售卖状态
		for (Long id : ids) {
			Dish dish=dishMapper.getById(id);
			if(dish.getStatus()== StatusConstant.ENABLE){
				//当前菜品正在售卖
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
			}
		}
		//2.判断当前菜品是否包含在套餐内
		List<Long>setMealDishIds=setMealDishMapper.getSetMealIdByDishIds(ids);
		if(setMealDishIds!=null&&setMealDishIds.size()>0){
			throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}
		log.info("无关联套餐：{}",setMealDishIds);
		//3.删除菜品和菜品关联的口味
		for (Long id : ids) {
			dishMapper.deleteById(id);
			dishFlavorMapper.deleteByDishId(id);
		}

	}

}
