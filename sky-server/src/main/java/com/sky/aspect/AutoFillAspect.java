package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @ClassName AutoFillAspect
 * @Author iove
 * @Date 2024/12/14 下午8:57
 * @Version 1.0
 * @Description TODO
 **/
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
	/**
	 * 定义切入点
	 */
	@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
	public void autoFillPointcut(){}
	/**
	 * 定义切入的时机
	 */
	@Before("autoFillPointcut()")
	public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		log.info("Auto fill pointcut");
		//1.获取当前被拦截的方法上的数据库操作类型
		MethodSignature signature=(MethodSignature)joinPoint.getSignature();//方法签名对象
		AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
		OperationType operationType=autoFill.value();//获得数据库操作类型
		//2.获取到当前拦截方法的参数--实体对象
		Object []args=joinPoint.getArgs();
		if(args==null && args.length==0){
			return;
		}
		Object entity=args[0];
		log.info("当前拦截方法的参数--实体对象="+entity.toString());
		//3.准备赋值的数据
		LocalDateTime now=LocalDateTime.now();
		Long currentId= BaseContext.getCurrentId();
		//4.根据当前不同的操作类型,为对应的属性通过反射来赋值
		if(operationType==OperationType.INSERT){
			//4.1.通过反射获得方法
			Method setCreatTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
			Method setCreatUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
			Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
			Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
			//4.2.通过方法赋值
			setCreatTime.invoke(entity,now);
			setCreatUser.invoke(entity,currentId);
			setUpdateTime.invoke(entity,now);
			setUpdateUser.invoke(entity,currentId);
		}else if(operationType==OperationType.UPDATE){
			Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
			Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

			setUpdateTime.invoke(entity,now);
			setUpdateUser.invoke(entity,currentId);
		}
	}
}
