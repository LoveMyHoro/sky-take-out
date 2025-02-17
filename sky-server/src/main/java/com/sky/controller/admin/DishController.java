package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
	@Autowired
	private RedisTemplate redisTemplate;

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
		clearCache("dish_"+dishDTO.getCategoryId());
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
	 * 菜品批量删除
	 * @return
	 */
	@DeleteMapping
	@ApiOperation("菜品删除")
	public Result delete(@RequestParam List<Long>ids){
		dishService.deleteBatch(ids);
		clearCache("dish_*");
		return Result.success();
	}
	/**
	 * 根据id查询菜品
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询菜品")
	public Result<DishVO> getById(@PathVariable Long id) {
		log.info("根据id查询菜品：{}", id);
		DishVO dishVO = dishService.getByIdWithFlavor(id);//后绪步骤实现
		return Result.success(dishVO);
	}
	/**
	 * 修改菜品
	 *
	 * @param dishDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("修改菜品")
	public Result update(@RequestBody DishDTO dishDTO) {
		log.info("修改菜品：{}", dishDTO);
		dishService.updateWithFlavor(dishDTO);
		clearCache("dish_*");
		return Result.success();
	}
	/**
	 * 根据分类id查询菜品
	 * @param categoryId
	 * @return
	 */
	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<Dish>> list(Long categoryId){
		List<Dish> list = dishService.list(categoryId);
		return Result.success(list);
	}
	/**
	 * 菜品起售停售
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("菜品起售停售")
	public Result<String> startOrStop(@PathVariable Integer status, Long id){
		dishService.startOrStop(status,id);
		clearCache("dish_*");
		return Result.success();
	}
	private void clearCache(String Pattern){
		Set keys=redisTemplate.keys(Pattern);
		redisTemplate.delete(keys);
	}
}
