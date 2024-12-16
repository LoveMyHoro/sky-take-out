package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OssConfiguration
 * @Author iove
 * @Date 2024/12/15 下午7:59
 * @Version 1.0
 * @Description TODO 配置类用于创建AliOss对象
 **/
@Configuration
@Slf4j
public class OssConfiguration {
	/**
	 * 用于创建ali工具类对象
	 * @param aliOssProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean//当没有这种Bean时再创建
	public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
		log.info("创建阿里云工具类对象");
		return new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());

	}
}
