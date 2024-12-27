package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName ShoppingCartController
 * @Author iove
 * @Date 2024/12/27 下午3:11
 * @Version 1.0
 * @Description TODO
 **/
@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {
	@Autowired
	private ShoppingCartService shoppingCartService;
	@PostMapping("/add")
	public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
		log.info("将商品信息添加到购物车，商品信息为：{}", shoppingCartDTO);
		shoppingCartService.addShoppingCart(shoppingCartDTO);
		return Result.success();
	}
	@GetMapping("/list")
	public Result list(){
		List<ShoppingCart> list=shoppingCartService.showShoppingCart();
		return Result.success(list);
	}
	@DeleteMapping("/clean")
	public Result clear(){
		shoppingCartService.clearShoppingCart();
		return Result.success();
	}
	@PostMapping("/sub")
	public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
		log.info("删除购物车中的一个物品");
		shoppingCartService.subShoppingCart(shoppingCartDTO);
		return Result.success();
	}
}
