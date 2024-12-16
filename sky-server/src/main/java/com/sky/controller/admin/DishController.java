package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Target;
import java.util.List;
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

	/**
	 * 1.新增菜品
	 * @param dishDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO) {
		log.info("新增菜品：{}", dishDTO);
		dishService.saveWithFavor(dishDTO);
		return Result.success();
	}

	/**
	 * 2.分页查询菜品
	 * @param pageQueryDTO
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("菜品分页查询")
	public Result <PageResult>page(DishPageQueryDTO pageQueryDTO){
		log.info("菜品分页查询");
		PageResult pageResult=dishService.pageQuery(pageQueryDTO);
		return Result.success(pageResult);
	}

	/**
	 * 菜品删除
	 * @return
	 */
	@DeleteMapping
	@ApiOperation("菜品删除")
	public Result delete(@RequestParam List<Long>ids){
		dishService.deleteBatch(ids);
		return Result.success();
	}

}
