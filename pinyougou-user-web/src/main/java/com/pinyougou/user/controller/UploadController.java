package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Reference
    private UserService userService;
	
//	@Value("${FILE_SERVER_URL}")
	private String file_server_url="http://192.168.25.133/";
	
	@RequestMapping("/uploadFile")
	public Result uploadFile(MultipartFile file){
		
		try {
			// 获得文件名:
            System.out.println(file);
			String fileName = file.getOriginalFilename();
			// 获得文件的扩展名:
			String extName = fileName.substring( fileName.lastIndexOf(".")+1 );
			// 创建工具类
			FastDFSClient client = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
			
			String path = client.uploadFile(file.getBytes(), extName); // group1/M00/
			
			String url = file_server_url + path;

            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                userService.updateHeadImg(name,url);
                return new Result(true, url);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "上传失败！");
            }

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败！");
		}
		
		
	}
}
