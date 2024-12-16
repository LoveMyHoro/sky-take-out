package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName CommonController
 * @Author iove
 * @Date 2024/12/15 下午7:56
 * @Version 1.0
 * @Description TODO
 **/
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
	@Autowired
	private AliOssUtil aliOssUtil;
	/**
	 * 文件上传
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ApiOperation("文件上传")
	public Result<String>upload(MultipartFile file) {
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String Url= null;
		try {
			Url = aliOssUtil.upload(file.getBytes(), UUID.randomUUID()+extension);
			return Result.success(Url);
		} catch (IOException e) {
			log.info("文件上传失败，错误信息：{}",e.getMessage());
	}
		return Result.error(MessageConstant.UPLOAD_FAILED);
}
}
