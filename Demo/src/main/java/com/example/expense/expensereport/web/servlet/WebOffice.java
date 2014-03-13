package com.example.expense.expensereport.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.service.ExpenseReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;

@SuppressWarnings({ "unused" })
public class WebOffice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String METHOD_POST = "post";
	private static final String LOAD_FILE = "LOADFILE";
	private static final String SAVE_FILE = "SAVEFILE";
	private Log log = LogFactory.getLog(getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBstep.iMsgServer2000 msgServer = new DBstep.iMsgServer2000();
		try {
			if(METHOD_POST.equalsIgnoreCase(request.getMethod())) {
				msgServer.Load(request);
				processInfo(msgServer);
			}else{
				addErrorMsg(msgServer, "请使用Post方法");
			}
		} catch (Exception e) {
			log.debug(e);
			addErrorMsg(msgServer, "文件打开错误, 请联系管理员.");
		}
		sendPackage(msgServer,response);
	}
	
	/*
	 * 处理iWebOffice控件信息
	 */
	private void processInfo(DBstep.iMsgServer2000 msgServer) throws IOException{
		if ("DBSTEP".equalsIgnoreCase(msgServer.GetMsgByName("DBSTEP"))) { // 如果是合法的信息包
			Long recordId = Long.parseLong(msgServer.GetMsgByName("RECORDID"));
			String optionMessage = msgServer.GetMsgByName("OPTION");
			if (LOAD_FILE.equalsIgnoreCase(optionMessage)) { // 查看文件
				msgServer.MsgTextClear();
				msgServer.MsgFileBody(loadFile(recordId)); // 将文件信息打包
				msgServer.SetMsgByName("STATUS", "打开成功!"); // 设置状态信息
				msgServer.MsgError("");   // 清除错误信息
			}else if (SAVE_FILE.equalsIgnoreCase(optionMessage)) { // 保存文件
				saveFile(msgServer, recordId);
			}
		}else{
			addErrorMsg(msgServer, "客户端发送数据包错误!");
		}
	}
	
	private byte[] loadFile(Long recordId) throws IOException{
			ExpenseReportManager expenseReportManager = (ExpenseReportManager) ContextUtils.getBean("expenseReportManager");
			ExpenseReport expenseReport = expenseReportManager.getExpenseReport(recordId);
//			return FileUtils.readFileToByteArray(new File(expenseReport.getFilePath()));
			return ApiFactory.getFileService().getFile(expenseReport.getFilePath());
	}
	
	private void saveTempFile(byte[] content,File file){
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(file);
			fos.write(content);
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fos!=null)
				try {
					fos.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}
	
	/**
	 * 保存文件
	 * @param msgServer
	 * @param recordId
	 * @throws IOException 
	 */
	private void saveFile(DBstep.iMsgServer2000 msgServer, Long recordId ){
		msgServer.MsgTextClear();
		int fileSize = msgServer.MsgFileSize();  //字节数
			ExpenseReportManager expenseReportManager = (ExpenseReportManager) ContextUtils.getBean("expenseReportManager");
			ExpenseReport expenseReport = expenseReportManager.getExpenseReport(recordId);
			msgServer.MsgFileSave(expenseReport.getFilePath());
			expenseReportManager.saveExpenseReport(expenseReport);
			msgServer.SetMsgByName("STATUS", "保存成功!");
			msgServer.MsgError(""); 
			msgServer.MsgFileClear();      
		                       
	}
	
	/*
	 * 为iWebOffice控件添加错误信息 
	 */
	private void addErrorMsg(DBstep.iMsgServer2000 msgServer, String msg){
		msgServer.MsgError(msg);
		msgServer.MsgTextClear();
		msgServer.MsgFileClear();
	}
	
	/**
	 * 发送处理后的数据包
	 * @param response
	 */
	private void sendPackage(DBstep.iMsgServer2000 msgServer,HttpServletResponse response) {
		try {
			response.getOutputStream().write(msgServer.MsgVariant());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			log.debug(e);
		}
	}
	
}
