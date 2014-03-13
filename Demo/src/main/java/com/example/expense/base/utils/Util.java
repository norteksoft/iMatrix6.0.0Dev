package com.example.expense.base.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;


public class Util {
	public static String treeAttrBefore="\"id\" : \"";
	public static String treeAttrMiddle="\" ,\"rel\":\"";
	public static String treeAttrAfter="\"";
	public static String folder="NortekSoft";
	
	/**
	 * 读取properties文件
	 */
	public static String readProperties(String key)throws Exception{
		Properties propert = new Properties();
		propert.load(Util.class.getClassLoader().getResourceAsStream("application.properties"));
		return propert.getProperty(key);
	}
	
	/**
	 * 创建文件夹
	 * @param path
	 * @return
	 */
	public static String cretaFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return path;
	}
	
	/**
	 * 上传文件
	 */
	public static String uploadFile(File path,String serverPath)throws Exception{
		FileUtils.copyFile(path, new File(serverPath));
		return serverPath;
	}
	
	/**
	 * 得到文件的字节数组
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytes(File filePath) throws IOException{
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(filePath));
		byte[] content=null;
		try {
			content = new byte[bis.available()];
			bis.read(content);
		}finally{
			bis.close();
		}
		return content;
	}
	
	/**
	 * 下载文档
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	public static String download(String fileName,byte[] content) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null;
		try {
			byte[] byname=fileName.getBytes("gbk");
			fileName=new String(byname,"8859_1");
			response.addHeader("Content-Disposition", "attachment;filename="+fileName);
			out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
			bis.close();
		}
		return null;
	}
}

