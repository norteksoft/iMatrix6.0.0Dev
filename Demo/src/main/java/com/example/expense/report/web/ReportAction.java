package com.example.expense.report.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.Report;
import com.example.expense.report.service.ReportManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.wf.WorkflowActionSupport;
import com.norteksoft.wf.base.enumeration.CompleteTaskTipType;


@Namespace("/report")
@ParentPackage("default")
@Results({ @Result(name = CrudActionSupport.RELOAD, location = "report", type = "redirectAction") })
public class ReportAction  extends WorkflowActionSupport<Report> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String deleteIds;
	private String leaderName;//第二环节选择领导（第三个环节办理人）
	private Long entityId;
	private Report report;
	private Long companyId;
	private Page<Report> page;
	
	
	protected boolean opinionflag=false;//false为不同意，true为同意
	
	protected boolean submitflag=false;//提交意见
	
	private String editType;//编辑
	
	private String opinion;// 意见 
	
	private boolean submitFlag=false;//表示是否已提交
	
	private String submitResult;//任务提交结果
	
	private boolean saveTaskFlag=false;//任务保存标志
	
	private boolean ifHasStartedProcess=true;//是否有启用的流程
	
	private String assignee; //指派人
	
	@Autowired
	private ReportManager reportManager;
	
	/**
	 * 提交流程
	 */
	@Override
	@Action("submit-process")
	public String submitProcess() {
		CompleteTaskTipType completeTaskTipType=null;
		try{
			completeTaskTipType =  reportManager.submitProcess(report,"发起报销子流程","sub-report-process");
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		submitResult=reportManager.getCompleteTaskTipType(completeTaskTipType,report);
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
			completeTaskTipType =  reportManager.completeTask(report, taskId, taskTransact);
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		return this.renderText(reportManager.getCompleteTaskTipType(completeTaskTipType,report));
	}
	
	/**
	 * 完成交互任务：用于选人、选环节、填意见
	 * @return
	 */
	@Action("report-distributeTask")
	@Override
	public String completeInteractiveTask(){
		CompleteTaskTipType completeTaskTipType=null;
		String str=reportManager.getOpinion(opinion,submitflag,opinionflag);//得到意见
		List<String> lists = new ArrayList<String>();
		try{
			if(leaderName!=null&&!leaderName.isEmpty()){
				lists.add(leaderName);
				completeTaskTipType = reportManager.distributeTask(taskId,lists,str);
			}else{
				completeTaskTipType = reportManager.distributeTask(taskId,lists,str);
			}
		}catch(RuntimeException de){
			de.printStackTrace();
		}
		report =reportManager.getReportByTaskId(taskId);
		return this.renderText(reportManager.getCompleteTaskTipType(completeTaskTipType,report));
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
		String msg=reportManager.retrieve(taskId);
		task=reportManager.getWorkflowTask(taskId);
		report=reportManager.getReportByTaskId(taskId);
		renderText(msg);
		return null;
	}

	/**
	 * 领取任务
	 */
	@Override
	@Action("receive-task")
	public String drawTask() {
		reportManager.drawTask(taskId);
		task=reportManager.getWorkflowTask(taskId);
		return "process-task";
	}
	/**
	 * 放弃领取任务
	 */
	@Override
	@Action("abandonReceive")
	public String abandonReceive() {
		reportManager.abandonReceive(taskId);
		task=reportManager.getWorkflowTask(taskId);
		return "process-task";
	}

	/**
	 * 弹出意见框
	 * @return
	 */
	@Override
	@Action("report-openOpinion")
	public String fillOpinion(){
		return "report-openOpinion";
	}

	
	@Override
	protected void prepareModel() throws Exception {
	    if(taskId!=null){
	    	report = reportManager.getReportByTaskId(taskId);
	    	task = reportManager.getWorkflowTask(taskId);
	    	opinions = reportManager.getOpinions(report);
	    	ApiFactory.getFormService().fillEntityByTask(report, taskId);
	    }else if(id!=null){
	    	report=reportManager.getReport(id);
			task = reportManager.getMyTask(report,ContextUtils.getLoginName());
			if(task!=null)taskId = task.getId();
			if(task==null) taskId = report.getWorkflowInfo().getFirstTaskId();
			if(report.getWorkflowInfo()!=null) 
			opinions = reportManager.getOpinions(report);	
	    }else if(id==null){
	    	report=new Report();
	    	report.setCompanyId(ContextUtils.getCompanyId());
	    	report.setCreatedTime(new Date());
	    	report.setCreator(ContextUtils.getUserName());
	    	report.setCreatorId(ContextUtils.getUserId());
	    	report.setName(ContextUtils.getUserName());
			ApiFactory.getFormService().fillEntityByDefinition(report, "sub-report-process");
		}
	}
	
	@Action(value="input",
			results={@Result(name="input", location="input.jsp"),
					 @Result(name="view", location="view.jsp")
			})
	@Override
	public String input() throws Exception {
		if(report.getWorkflowInfo()!=null){
			if(!"process.unsubmit".equals(report.getWorkflowInfo().getProcessState().getCode())){
				return "view";
			}
		}
		List<com.norteksoft.product.api.entity.WorkflowDefinition> definitions = ApiFactory.getDefinitionService().getWorkflowDefinitionsByCode("sub-report-process");
		if(definitions.size()>0) {
			getRight(taskId,"sub-report-process");
		}else{
			ifHasStartedProcess=false;
			addActionMessage("<font class=\"onError\"><nobr>流程定义中没有启用报销子流程!</nobr></font>");
		}
		return "input";
	}

	@Action("save")
	@Override
	public String save() throws Exception {
		getRight(taskId,"sub-report-process");
		reportManager.saveInstance("sub-report-process",report);
		if(saveTaskFlag) return renderText(report.getId().toString());
		return "input";
	}
	
	public void prepareProcessTask() throws Exception {
		prepareModel();
	}

	// 任务办理界面
	@Action("process-task")
	public String processTask() throws Exception {
		getRight(taskId,"sub-report-process");
		//办理前自动填写域设值
		if(taskId!=null){
			ApiFactory.getFormService().fillEntityByTask(report, taskId);
		}
		return SUCCESS;
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
	 * 获取权限
	 * @param taskId
	 * @param defCode
	 */
	public void getRight(Long taskId,String defCode) {
		if(taskId==null){
			editType="-1,0,1,1,0,0,1,1";//保留痕迹
			fieldPermission =reportManager.getFieldPermission(defCode);//禁止或必填字段
			taskPermission =reportManager.getActivityPermission(defCode);
		}else{
			editType=reportManager.getDocumentRight(taskId);//保留痕迹
			fieldPermission = reportManager.getFieldPermissionByTaskId(taskId);//禁止或必填字段
			taskPermission = reportManager.getActivityPermission(taskId);
		}
	}

	public void prepareCompleteTask() throws Exception {
		prepareModel();
	}

	public void prepareSubmitProcess() throws Exception {
		prepareModel();
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
		addActionMessage("<font class=\"onSuccess\"><nobr>"+reportManager.deleteReport(deleteIds)+"</nobr></font>");
		return "list";
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}

	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = reportManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	public void prepareUploadDocument() throws Exception {
		report= reportManager.getReport(id);
	}
	
	/**
	 * 流程监控/删除实例的业务补偿(如删除数据关联关系)
	 * @return
	 * @throws Exception
	 */
	@Action("delete-instance-expiation")
	public String deleteInstanceExpiation() throws Exception{
		reportManager.deleteReportById(entityId);
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
	
	/**
	 * 抄送
	 * @return
	 */
	@Action("copy-tasks")
	public String copyTasks(){
		List<String> loginNames=new ArrayList<String>();
		if("all_user".equals(assignee)){
			List<User> users=ApiFactory.getAcsService().getUsersByCompany(ContextUtils.getCompanyId());
			for(User u:users){
				loginNames.add(u.getLoginName());
			}
		}else{
			loginNames=Arrays.asList(assignee.split(","));
		}
		reportManager.createCopyTasks(taskId, loginNames, null, null);
		renderText("已抄送");
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

	public void setPage(Page<Report> page) {
		this.page = page;
	}

	public Page<Report> getPage() {
		return page;
	}

	public Report getModel() {
		return report;
	}
	
	public void prepareShowHistory() throws Exception{
		if(id!=null){
			report=reportManager.getReport(id);
		}
	}
	
	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
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

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getSubmitResult() {
		return submitResult;
	}

	@Override
	public String addSigner() {
		return null;
	}

	@Override
	public String processEmergency() {
		return null;
	}

	@Override
	public String removeSigner() {
		return null;
	}

	public boolean SetSaveTaskFlag() {
		return saveTaskFlag;
	}

	public void setSaveTaskFlag(boolean saveTaskFlag) {
		this.saveTaskFlag = saveTaskFlag;
	}

	public boolean isIfHasStartedProcess() {
		return ifHasStartedProcess;
	}

	public void setIfHasStartedProcess(boolean ifHasStartedProcess) {
		this.ifHasStartedProcess = ifHasStartedProcess;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getAssignee() {
		return assignee;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
