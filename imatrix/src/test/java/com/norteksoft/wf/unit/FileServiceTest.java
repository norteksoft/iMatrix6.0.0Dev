package com.norteksoft.wf.unit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import com.norteksoft.product.api.ApiFactory;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class FileServiceTest extends BaseWorkflowTestCase {

	@Test
	public void saveFile() throws IOException{
		File file = new File("D:\\FileService单元测试\\test.txt");
		if(!file.exists()){
			File dir = new File("D:\\FileService单元测试");
			if(!dir.exists())dir.mkdir();
			file.createNewFile();
		}
		FileOutputStream fos  = new FileOutputStream(file);
		fos.write("FileServiceTest：saveFile()".getBytes());
		String result = ApiFactory.getFileService().saveFile(file,1L);
		Assert.assertNotNull(result);
	}

	@Test
	public void saveFileTwo() throws IOException{
		byte[] fileBody = {1,1,1,1,1,1,1,1,1};
		String result = ApiFactory.getFileService().saveFile(fileBody,1L);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getFile() throws IOException{
		File file = new File("D:\\FileService单元测试\\test.txt");
		if(!file.exists()){
			File dir = new File("D:\\FileService单元测试");
			if(!dir.exists())dir.mkdir();
			file.createNewFile();
		}
		FileOutputStream fos  = new FileOutputStream(file);
		fos.write("FileServiceTest：saveFile()".getBytes());
		String filePath = ApiFactory.getFileService().saveFile(file,1L);
		byte[] result = ApiFactory.getFileService().getFile(filePath);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void writeTo() throws IOException{
		File file = new File("D:\\FileService单元测试\\test1.txt");
		if(!file.exists()){
			File dir = new File("D:\\FileService单元测试");
			if(!dir.exists())dir.mkdir();
			file.createNewFile();
		}
		FileOutputStream fos  = new FileOutputStream(file);
		fos.write("FileServiceTest：saveFile()".getBytes());
		
		File target = new File("D:\\FileService单元测试\\test2.txt");
		if(!target.exists())target.createNewFile();
		FileOutputStream fos2  = new FileOutputStream(target);
		ApiFactory.getFileService().writeTo("D:\\FileService单元测试\\test1.txt",fos2);
		Assert.assertTrue(target.length()!=0);
	}
	
	@Test
	public void deleteFile() throws IOException{
		File file = new File("D:\\FileService单元测试delete\\test77777.txt");
		if(!file.exists()){
			File dir = new File("D:\\FileService单元测试delete");
			if(!dir.exists())dir.mkdir();
			file.createNewFile();
		}
		Assert.assertTrue(file.exists());
		ApiFactory.getFileService().deleteFile("D:/FileService单元测试delete/test77777.txt");
		Assert.assertTrue(!file.exists());
	}
}
