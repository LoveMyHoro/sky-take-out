package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Target;
import java.util.Random;

/**
 * @ClassName DishController
 * @Author iove
 * @Date 2024/12/15 下午8:56
 * @Version 1.0
 * @Description TODO
 **/
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
	@Autowired
	private DishService dishService;

	@PostMapping
	public Result save(@RequestBody DishDTO dishDTO) {
		//1.插入菜品
		log.info("新增菜品：{}", dishDTO);
		dishService.saveWithFavor(dishDTO);
		return Result.success();
	}

}
