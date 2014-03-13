package com.example.expense.expensereport.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.base.utils.Util;
import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.service.ExpenseReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Document;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.wf.WorkflowActionSupport;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;



@Namespace("/expense-report")
@ParentPackage("default")
@Results({ @Result(name = CrudActionSupport.RELOAD, location = "expense-report", type = "redirectAction") })
public class ExpenseReportAction  extends WorkflowActionSupport<ExpenseReport> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String deleteIds;
	private String leaderId;//第二环节选择领导（第三个环节办理人）
	private Long entityId;
	private ExpenseReport expenseReport;
	
	private Page<ExpenseReport> page;
	
	private List<User> userList = new ArrayList<User>();  //加签userlist
	
	protected boolean opinionflag=false;//false为不同意，true为同意
	
	protected boolean submitflag=false;//提交意见
	
	private String editType;//编辑
	
	private String opinion;// 意见 
	
	private boolean submitFlag=false;//表示是否已提交
	
	private boolean saveTaskFlag=false;//任务保存标志
	
	private String userIds; //加签的人员名
	
	private boolean urgenFlag;//应急处理标识
	
	private String sendPerson;// 消息发送人
	
	private String sendLoginName;// 消息发送人登录名
	
	private String recieveLoginName;// 接收消息登录名
	
	private String sendType;// 发送类型
	
	private String sendContent;// 发送内容
	
	private String addSignPerson;//加签人员
	
	private String cutPersons;//减签人员
	
	private List<String[]> handerList = new ArrayList<String[]>();//减签环节办理人list
	
	private List<WorkflowTask> cutsignList ;//减签环节办理人list
	
	private Long userId;//用户登陆名
	
	private boolean ifFirst = false;//是否是第一环节
	
	private String taches; //选择环节时用
	
	private List<String[]> tacheList = new ArrayList<String[]>();
	
	private String tacheCode;//环节编号
	
	private List<WorkflowDefinition> applicationDefs = new ArrayList<WorkflowDefinition>();
	
	private String assignee; //指派人
	
	private String submitResult;//任务提交结果
	
	private String runAsUser;
	private Document document;//文件
	
	@Autowired
	private ExpenseReportManager expenseReportManager;
	
	/**
	 * 提交流程
	 */
	@Override
	@Action("submit-process")
	public String submitProcess() {
		CompleteTaskTipType completeTaskTipType=null;
		//设置文档创建人的直属上级姓名
		User user = ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getUserId());
		if(user!=null){
			expenseReport.setDirectLeaderName(user.getName());
		}else{
			expenseReport.setDirectLeaderName("空");
		}
		try{
			completeTaskTipType =  expenseReportManager.submitProcess(expenseReport,"发起报销","expense-report");
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		submitResult=expenseReportManager.getCompleteTaskTipType(completeTaskTipType,expenseReport);
		return "input";
	}

	/**
	 * 完成任务
	 */
	@Override
	@Action("complete-task")
	public String completeTask() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  expenseReportManager.completeTask(expenseReport, taskId, taskTransact);
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		return this.renderText(expenseReportManager.getCompleteTaskTipType(completeTaskTipType,expenseReport));
	}
	
	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 * @return
	 */
	@Action("expense-distributeTask")
	@Override
	public String completeInteractiveTask(){
		CompleteTaskTipType completeTaskTipType=null;
		String str=expenseReportManager.getOpinion(opinion,submitflag,opinionflag);//得到意见
		List<String> lists = new ArrayList<String>();
		try{
			if(leaderId!=null&&!leaderId.isEmpty()){//leaderName:记录的是用户id
				lists.add(leaderId);
				completeTaskTipType = expenseReportManager.distributeTask(taskId,lists,str);
			}else if(userIds!=null&&!userIds.isEmpty()){//加签批示传阅人员
				String[] uids = userIds.split("_");
				if(uids.length>0)lists.addAll(Arrays.asList(uids));
				expenseReportManager.addSigner(taskId, lists);
				completeTaskTipType = expenseReportManager.distributeTask(taskId,new ArrayList<String>(),str);
			}else if(tacheCode!=null){
				completeTaskTipType = expenseReportManager.distributeTask(taskId, tacheCode,str);
			}else{
				completeTaskTipType = expenseReportManager.distributeTask(taskId,lists,str);
			}
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		expenseReport =expenseReportManager.getExpenseReportByTaskId(taskId);
		return this.renderText(expenseReportManager.getCompleteTaskTipType(completeTaskTipType,expenseReport));
	}
	
	@Action("select-tache")
	public String selectTache(){
		if(taches!=null){
			String str = taches.substring(1, taches.length()-1);
			String[] tache = str.split(",");
			for (String a : tache) {
				String[] s = new String[]{a.split("=")[0].trim(),a.split("=")[1].trim()}; 
				tacheList.add(s);
 			}
		}
		return"select-tache";
	}

	/**
	 * 跳转流转历史页面
	 */
	@Override
	@Action("history")
	public String showHistory() {
		return "history";
	}
	
	/**
	 * 取回
	 */
	@Override
	@Action("retrieve")
	public String retrieveTask() {
		String msg=expenseReportManager.retrieve(taskId);
		task=expenseReportManager.getWorkflowTask(taskId);
		expenseReport=expenseReportManager.getExpenseReportByTaskId(taskId);
		renderText(msg);
		return null;
	}
	
	public void prepareDrawTask() throws Exception{
		prepareModel();
	}

	/**
	 * 领取任务
	 */
	@Override
	@Action("receive-task")
	public String drawTask() {
		try {
			expenseReportManager.drawTask(taskId);
			task=expenseReportManager.getWorkflowTask(taskId);
			return processTask();
		} catch (Exception e) {
		}
		return "process-task";
	}
	/**
	 * 绑定放弃领取任务
	 * @throws Exception
	 */
	public void prepareAbandonReceive() throws Exception {
		prepareModel();
	}
	
	/**
	 * 放弃领取任务
	 */
	@Override
	@Action("abandonReceive")
	public String abandonReceive() {
		try {
			expenseReportManager.abandonReceive(taskId);
			task=expenseReportManager.getWorkflowTask(taskId);
			return processTask();
		} catch (Exception e) {
		}
		return "process-task";
	}

	/**
	 * 加签
	 */
	@Override
	@Action("add-sign")
	public String addSigner() {
		try{
			expenseReportManager.addSigner(taskId, Arrays.asList(addSignPerson.split(",")));
			renderText("加签成功！");
		}catch (Exception e) {
			renderText(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 减签
	 */
	@Override
	@Action("cut")
	public String removeSigner() {
		List<Long> lists = new ArrayList<Long>();
		String[] cutPersonIdStrs = cutPersons.split(",");
		for(String idStr:cutPersonIdStrs){
			lists.add(Long.parseLong(idStr));
		}
		expenseReportManager.removeSigners(taskId, lists);
		renderText("减签成功！");
		return null;
	}

	/**
	 * 跳转应急处理页面
	 */
	@Action("urgenDonePage")
	@Override
	public String processEmergency() {
		urgenFlag=true;//监控标识
		getRight(taskId,"expense-report");
		return "urgenDonePage";
	}
	
	/**
	 * 弹出意见框
	 * @return
	 */
	@Override
	@Action("expense-openOpinion")
	public String fillOpinion(){
		return "expense-openOpinion";
	}

	
	@Override
	protected void prepareModel() throws Exception {
	    if(taskId!=null){
	    	expenseReport = expenseReportManager.getExpenseReportByTaskId(taskId);
	    	task = expenseReportManager.getWorkflowTask(taskId);
	    	opinions = expenseReportManager.getOpinions(expenseReport);
	    	//当任务状态不是已完成或被取消时(2:任务完成  3:被取消)，办理前自动填写域设值
	    	if(task.getActive()!=2&&task.getActive()!=3&&!urgenFlag){
	    	ApiFactory.getFormService().fillEntityByTask(expenseReport, taskId);
	    	}
	    }else if(id!=null){
	    	expenseReport=expenseReportManager.getExpenseReport(id);
			task = expenseReportManager.getActiveTaskByTransactorId(expenseReport,ContextUtils.getUserId());
			if(task!=null)taskId = task.getId();
			if(task==null) taskId = expenseReport.getWorkflowInfo().getFirstTaskId();
			if(expenseReport.getWorkflowInfo()!=null) 
			opinions = expenseReportManager.getOpinions(expenseReport);	
	    }else if(id==null){
			expenseReport=new ExpenseReport();
			expenseReport.setCompanyId(ContextUtils.getCompanyId());
			expenseReport.setCreatedTime(new Date());
			expenseReport.setCreator(ContextUtils.getLoginName());
			expenseReport.setCreatorId(ContextUtils.getUserId());
			expenseReport.setName(ContextUtils.getUserName());
			ApiFactory.getFormService().fillEntityByDefinition(expenseReport, "expense-report");
		}
	    List<Document> documents = ApiFactory.getDocumentService().getDocuments(expenseReport);
	    if(documents.size()>0) document = documents.get(0);
	}
	@Action(value="input",
			results={@Result(name="input", location="input.jsp"),
					 @Result(name="view", location="view.jsp")
			})
	@Override
	public String input() throws Exception {
		if(expenseReport.getWorkflowInfo()!=null){
			if(!"process.unsubmit".equals(expenseReport.getWorkflowInfo().getProcessState().getCode())){
				return "view";
			}
		}
		getRight(taskId,"expense-report");
		if(urgenFlag){
			return "input";
		}
		//流程在第一个环节时
		if(ifFirst){
			return "input";
		}
		return "input";
	}

	@Action("save")
	@Override
	public String save() throws Exception {
		if(expenseReport.getSignPersons().equals("所有人员")){
			expenseReportManager.getSignName(expenseReport);
		}
		if(expenseReport.getReadPersons().equals("所有人员")){
			expenseReportManager.getReadName(expenseReport);
		}
		//设置文档创建人的直属上级姓名
		User user = ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getUserId());
		if(user!=null){
			expenseReport.setDirectLeaderName(user.getName());
		}else{
			expenseReport.setDirectLeaderName("空");
		}
		expenseReportManager.saveInstance("expense-report",expenseReport);
		task = expenseReportManager.getActiveTaskByTransactorId(expenseReport,ContextUtils.getUserId());
		if(task!=null)taskId = task.getId();
		getRight(taskId,"expense-report");
		if(saveTaskFlag) return renderText(expenseReport.getId().toString());
		return "input";
	}
	
	public void prepareUrgenSave() throws Exception {
		expenseReport=expenseReportManager.getExpenseReport(id);
		task = expenseReportManager.getActiveTaskByTransactorId(expenseReport,ContextUtils.getUserId());
		if(task!=null)taskId = task.getId();
		if(task==null) taskId = expenseReport.getWorkflowInfo().getFirstTaskId();
		if(expenseReport.getWorkflowInfo()!=null) 
		opinions = expenseReportManager.getOpinions(expenseReport);	
		List<Document> documents = ApiFactory.getDocumentService().getDocuments(expenseReport);
	    if(documents.size()>0) document = documents.get(0);
	}
	/**
	 * 应急处理保存
	 * @return
	 * @throws Exception
	 */
	@Action("urgen-save")
	public String urgenSave() throws Exception {
		getRight(taskId,"expense-report");
		expenseReportManager.saveExpenseReport(expenseReport);
		return "input";
	}
	
	public void prepareProcessTask() throws Exception {
		prepareModel();
	}

	// 任务办理界面
	@Action("process-task")
	public String processTask() throws Exception {
		getRight(taskId,"expense-report");
		//任务状态不是已完成或被取消时(2:任务完成  3:被取消)，办理前自动填写域设值
		if(taskId!=null&&task.getActive()!=2&&task.getActive()!=3){
			ApiFactory.getFormService().fillEntityByTask(expenseReport, taskId);
		}
		return "process-task";
	}
	
	/**
	 * 指派人员树
	 * @return
	 */
	@Action("assign-tree")
	public String assignTree(){
		return "assign-tree";
	}
	
	/**
	 * 指派
	 * @return
	 */
	@Action("assign")
	public String assign(){
		expenseReportManager.assign(taskId, Long.parseLong(assignee));
		renderText("指派完成");
		return null;
	}
		
	/**
	 * 抄送
	 * @return
	 */
	@Action("copy-tasks")
	public String copyTasks(){
		List<String> userIds=new ArrayList<String>();
		if("all_user".equals(assignee)){
			List<Long> userids=ApiFactory.getAcsService().getUserIdsByCompanyWithoutAdmin();
			userIds.addAll(Arrays.asList(userids.toString().replace("[", "").replace("]", "").replace(" ", "")));
		}else{
			userIds=Arrays.asList(assignee.split(","));
		}
		expenseReportManager.createCopyTasks(taskId, userIds, null, null);
		renderText("已抄送");
		return null;
	}
	
	
	
	/**
	 * 获取权限
	 * @param taskId
	 * @param defCode
	 */
	public void getRight(Long taskId,String defCode) {
		if(urgenFlag){
			editType="-1,0,1,1,0,0,1,1";//保留痕迹
			taskPermission = new TaskPermission();
			taskPermission.setDocumentCreateable(true);
			taskPermission.setDocumentDownloadable(true);
		}else{
			if(taskId==null){
				editType="-1,0,1,1,0,0,1,1";//保留痕迹
				fieldPermission = expenseReportManager.getFieldPermission(defCode);//禁止或必填字段
				taskPermission = expenseReportManager.getActivityPermission(defCode);
			}else{
				editType=expenseReportManager.getDocumentRight(taskId);//保留痕迹
				ifFirst = expenseReportManager.isFirstTask(taskId);
				fieldPermission = expenseReportManager.getFieldPermissionByTaskId(taskId);//禁止或必填字段
				taskPermission = expenseReportManager.getActivityPermission(taskId);
			}
		}
	}

	public void prepareCompleteTask() throws Exception {
		prepareModel();
	}

	public void prepareSubmitProcess() throws Exception {
		prepareModel();
	}

	/**
	 * 选审批人
	 * @return
	 * @throws Exception 
	 */
	@Action("expense-selectLeader")
	public String selectLeader() throws Exception{
		prepareModel();
		return "expense-selectLeader";
	}
	
	/**
	 * 选批示传阅人（加签）
	 * @return
	 * @throws Exception 
	 */
	@Action("expense-selectReadPerson")
	public String selectReadPerson() throws Exception{
		prepareModel();
		User taskUser = null;
		Long userId = task.getTransactorId();
		if(task.getTrustorId()!=null){
			userId = task.getTrustorId();
		}
		taskUser = ApiFactory.getAcsService().getUserById(userId);
		if(taskUser == null){
			String loginName = task.getTransactor();
			if(StringUtils.isNotEmpty(task.getTrustor())){
				loginName = task.getTrustor();
			}
			taskUser = ApiFactory.getAcsService().getUserByLoginName(loginName);
		}
		
		if(taskUser!=null){
			List<Department> depts =  ApiFactory.getAcsService().getDepartmentsByUserId(taskUser.getId());
			
			for (Department department : depts) {
				List<User> users = ApiFactory.getAcsService().getUsersByDepartmentId(department.getId());
				for (User user : users) {
					if(!userList.contains(user)){
						userList.add(user);
					}
				}
				userId = ContextUtils.getUserId();
			}
		}
		return "expense-selectReadPerson";
	}
	
	/**
	 * 绑定distributeTask
	 * @throws Exception
	 */
	public void prepareDistributeTask() throws Exception {
		prepareModel();
	}
	
	/**
	 * 删除
	 */
	@Action("delete")
	@Override
	public String delete() throws Exception {
		addActionMessage("<font class=\"onSuccess\"><nobr>"+expenseReportManager.deleteExpenseReport(deleteIds)+"</nobr></font>");
		return "list";
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}

	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = expenseReportManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	public void prepareUploadDocument() throws Exception {
		expenseReport= expenseReportManager.getExpenseReport(id);
	}
	
	/**
	 * 上传文件
	 * @return
	 * @throws Exception 
	 */
	@Action("uploadDocument")
	public String uploadDocument() throws Exception{
		HttpServletRequest request=ServletActionContext.getRequest();
		String fileName=request.getParameter("Filename");
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		if(!fileType.equals("pdf")){
			fileType = "."+fileType;
		}
		//把request强转，因为struts从新封装了request(Filedata是它的参数不能改变)
		MultiPartRequestWrapper wrapper=(MultiPartRequestWrapper)request;
		expenseReport = expenseReportManager.getExpenseReport(id);
		//上传不受权限保证，公司id和当先用户登录名需要放入线程
		ThreadParameters param=new ThreadParameters(expenseReport.getCompanyId());
		param.setLoginName(expenseReport.getCreator());
		ParameterUtils.setParameters(param);
		
		List<Document> documents = ApiFactory.getDocumentService().getDocuments(expenseReport);
		if(documents.size()>0){
			ApiFactory.getFileService().deleteFile(documents.get(0).getFilePath());
			ApiFactory.getDocumentService().deleteDocument(documents.get(0).getId());
		}
		File filePath=wrapper.getFiles("Filedata")[0];
		FileInputStream input = new FileInputStream(filePath);
		
		Document document=new Document(fileName,Util.getBytes(filePath),"报销单正文",taskId);
		
		document.setFileSize(input.available());
		document.setFileType(fileType);
		ApiFactory.getDocumentService().saveDocument(document);
		return null;
	}
	
	
	/**
	 * 下载文档
	 * @return
	 * @throws Exception
	 */
	@Action("download-docment")
	public String downloadDocment() throws Exception{
		//expenseReport= expenseReportManager.getExpenseReport(id);
		Document doc = ApiFactory.getDocumentService().getDocument(id);
		byte[] file=ApiFactory.getFileService().getFile(doc.getFilePath());
		this.download(doc.getFileName(),file);
//		this.download(expenseReport.getDocumentName(),FileUtils.readFileToByteArray(new File(expenseReport.getFilePath())));
		return null;
	}
	
	/**
	 * 下载文档
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	private void download(String fileName,byte[] content) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
		HttpServletResponse response = ServletActionContext.getResponse();
		response.reset();
		response.setContentType("application/x-download");
		OutputStream out=null;
		try {
			byte[] byname=fileName.getBytes("gbk");
			fileName=new String(byname,"8859_1");
			response.addHeader("Content-Disposition", "attachment;filename=\""+fileName+"\"");
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
	}
	
	
	/**
	 * 绑定应急处理
	 * @throws Exception
	 */
	public void prepareProcessEmergency() throws Exception {
		prepareModel();
	}
	
	/**
	 * 系统消息提示
	 * @return
	 * @throws Exception
	 */
	@Action("expense-message")
	public String expenseMessage() throws Exception{
		sendPerson = ContextUtils.getUserName();
		sendLoginName = ContextUtils.getLoginName();
		return "expense-message";
	}
	
	/**
	 * 系统消息发送
	 * @return
	 * @throws Exception
	 */
	@Action("send-message")
	public String sendMessage() throws Exception{
		ApiFactory.getPortalService().addMessage(ContextUtils.getSystemCode(), ContextUtils.getUserName(), ContextUtils.getLoginName(), recieveLoginName, sendType, sendContent, "/expense-report/input.htm");
		return null;
		
	}
	
	/**
	 * 选择减签人员
	 * @return
	 * @throws Exception 
	 */
	@Action("cutsign")
	public String cutsign() throws Exception{
		prepareModel();
		//handerList = expenseReportManager.getTaskHander(expenseReport,taskId);
		cutsignList = expenseReportManager.getTaskHander(expenseReport,taskId);
		userId = ContextUtils.getUserId();
		return "cutsign";
	}
	
	/**
	 * 验证是否有流程定义
	 * @return
	 * @throws Exception
	 */
	@Action("validate-process")
	public String validateProcess() throws Exception{
		applicationDefs = ApiFactory.getDefinitionService().getWorkflowDefinitionsByCode("expense-report");
		if(applicationDefs.size()<=0){
			this.renderText("zero");
		}else if(applicationDefs.size()==1){
			this.renderText("one:"+applicationDefs.get(0).getId());
		}else{
			this.renderText("success:"+applicationDefs.get(0).getId());
		}
		return null;
	}
	/**
	 * 流程监控/删除实例的业务补偿(如删除数据关联关系)
	 * @return
	 * @throws Exception
	 */
	@Action("delete-instance-expiation")
	public String deleteInstanceExpiation() throws Exception{
		expenseReportManager.deleteExpenseReportById(entityId);
		renderText("删除成功！");
		return null;
	}
	/**
	 * 流程监控/取消实例的业务补偿
	 * @return
	 * @throws Exception
	 */
	@Action("cancel-instance-expiation")
	public String cancelInstanceExpiation() throws Exception{
		
		renderText("");
		return null;
	}
	/**
	 * 流程监控/环节跳转的业务补偿
	 * @return
	 * @throws Exception
	 */
	@Action("task-jump-expiation")
	public String taskJumpExpiation() throws Exception{
		
		renderText("");
		return null;
	}
	
	//测试http访问方式的定时
	@Action("test-apply")
	public String test(){
		System.out.println(runAsUser);
		return null;
	}
	
	@Action("goback")
	public String goback(){
//		expenseReport=expenseReportManager.getExpenseReportByTaskId(taskId);
//		task=expenseReportManager.getWorkflowTask(taskId);
		String msg=expenseReportManager.goback(taskId);
		renderText(msg);
		return null;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getDeleteIds() {
		return deleteIds;
	}

	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}

	public void setPage(Page<ExpenseReport> page) {
		this.page = page;
	}

	public Page<ExpenseReport> getPage() {
		return page;
	}

	public ExpenseReport getModel() {
		return expenseReport;
	}
	
	public void prepareShowHistory() throws Exception{
		expenseReport=expenseReportManager.getExpenseReport(id);
	}
	
	public String getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(String leaderId) {
		this.leaderId = leaderId;
	}

	public boolean isOpinionflag() {
		return opinionflag;
	}

	public void setOpinionflag(boolean opinionflag) {
		this.opinionflag = opinionflag;
	}

	public boolean isSubmitflag() {
		return submitflag;
	}

	public void setSubmitflag(boolean submitflag) {
		this.submitflag = submitflag;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public boolean isSubmitFlag() {
		return submitFlag;
	}

	public void setSubmitFlag(boolean submitFlag) {
		this.submitFlag = submitFlag;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public boolean isUrgenFlag() {
		return urgenFlag;
	}

	public void setUrgenFlag(boolean urgenFlag) {
		this.urgenFlag = urgenFlag;
	}

	public String getSendLoginName() {
		return sendLoginName;
	}

	public void setSendLoginName(String sendLoginName) {
		this.sendLoginName = sendLoginName;
	}

	public String getRecieveLoginName() {
		return recieveLoginName;
	}

	public void setRecieveLoginName(String recieveLoginName) {
		this.recieveLoginName = recieveLoginName;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public void setSendPerson(String sendPerson) {
		this.sendPerson = sendPerson;
	}

	public String getSendPerson() {
		return sendPerson;
	}

	public String getAddSignPerson() {
		return addSignPerson;
	}

	public void setAddSignPerson(String addSignPerson) {
		this.addSignPerson = addSignPerson;
	}

	public List<String[]> getHanderList() {
		return handerList;
	}

	public void setHanderList(List<String[]> handerList) {
		this.handerList = handerList;
	}

	public String getCutPersons() {
		return cutPersons;
	}

	public void setCutPersons(String cutPersons) {
		this.cutPersons = cutPersons;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean getIfFirst() {
		return ifFirst;
	}

	public void setIfFirst(boolean ifFirst) {
		this.ifFirst = ifFirst;
	}

	public List<WorkflowDefinition> getApplicationDefs() {
		return applicationDefs;
	}

	public void setApplicationDefs(List<WorkflowDefinition> applicationDefs) {
		this.applicationDefs = applicationDefs;
	}

	public String getTaches() {
		return taches;
	}

	public void setTaches(String taches) {
		this.taches = taches;
	}

	public List<String[]> getTacheList() {
		return tacheList;
	}

	public void setTacheList(List<String[]> tacheList) {
		this.tacheList = tacheList;
	}

	public String getTacheCode() {
		return tacheCode;
	}

	public void setTacheCode(String tacheCode) {
		this.tacheCode = tacheCode;
	}

	public boolean isSaveTaskFlag() {
		return saveTaskFlag;
	}

	public void setSaveTaskFlag(boolean saveTaskFlag) {
		this.saveTaskFlag = saveTaskFlag;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getSubmitResult() {
		return submitResult;
	}

	public void setRunAsUser(String runAsUser) {
		this.runAsUser = runAsUser;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<WorkflowTask> getCutsignList() {
		return cutsignList;
	}

	public void setCutsignList(List<WorkflowTask> cutsignList) {
		this.cutsignList = cutsignList;
	}
}
