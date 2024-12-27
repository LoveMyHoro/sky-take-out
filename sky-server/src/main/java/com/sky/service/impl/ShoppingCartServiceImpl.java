package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ShoppingCartServiceImpl
 * @Author iove
 * @Date 2024/12/27 下午3:13
 * @Version 1.0
 * @Description TODO
 **/
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;
	/**
	 * 向购物车中加入新增菜品
	 * @param shoppingCartDTO
	 */
	@Override
	public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
		//将ShoppingCartDto转化为ShoppingCart
		ShoppingCart shoppingCart=new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
		Long userId = BaseContext.getCurrentId();
		shoppingCart.setUserId(userId);
		//先查一查数据库中有没有这个菜品
		List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
		if(list!=null&&list.size()>0){
			ShoppingCart cart = list.get(0);
			cart.setNumber(cart.getNumber()+1);
			shoppingCartMapper.updateNumberById(cart);
		}else{
			if(shoppingCart.getDishId()!=null){
				Dish dish = dishMapper.getById(shoppingCart.getDishId());
				shoppingCart.setName(dish.getName());
				shoppingCart.setImage(dish.getImage());
				shoppingCart.setAmount(dish.getPrice());

			}else {
				Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
				shoppingCart.setName(setmeal.getName());
				shoppingCart.setImage(setmeal.getImage());
				shoppingCart.setAmount(setmeal.getPrice());
			}
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartMapper.insert(shoppingCart);
		}
	}

	@Override
	public List<ShoppingCart> showShoppingCart() {
		Long userId= BaseContext.getCurrentId();
		ShoppingCart cart = ShoppingCart.builder().userId(userId).build();
		List<ShoppingCart> list = shoppingCartMapper.list(cart);
		return list;
	}

	@Override
	public void clearShoppingCart() {
		Long userId= BaseContext.getCurrentId();
		shoppingCartMapper.deleteByUserId(userId);
	}

	@Override
	public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
		ShoppingCart shoppingCart=new ShoppingCart();
		BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
		Long userId = BaseContext.getCurrentId();
		shoppingCart.setUserId(userId);
		List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
		if(list!=null&&list.size()>0){
			ShoppingCart cart = list.get(0);
			cart.setNumber(cart.getNumber()-1);
			if(cart.getNumber()>0){
				shoppingCartMapper.updateNumberById(cart);
			}else{
				shoppingCartMapper.deleteById(cart.getId());
			}
		}
	}
}
