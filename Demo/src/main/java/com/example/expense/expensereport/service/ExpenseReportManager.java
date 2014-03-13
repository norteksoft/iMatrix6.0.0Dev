package com.example.expense.expensereport.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.ExpenseReport;
import com.example.expense.expensereport.dao.ExpenseReportDao;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.OnStartingSubProcess;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;

@Service
@Transactional
public class ExpenseReportManager extends WorkflowManagerSupport<ExpenseReport> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface,OnStartingSubProcess {
	@Autowired
	private ExpenseReportDao expenseReportDao;
	
	@Autowired
	public LogUtilDao logUtilDao;

	public ExpenseReport getExpenseReport(Long id){
		return expenseReportDao.get(id);
	}

	public void saveExpenseReport(ExpenseReport expenseReport){
		expenseReportDao.save(expenseReport);
	}

	public void deleteExpenseReport(Long id){
		ApiFactory.getInstanceService()
		.deleteInstance(getExpenseReport(id));
	}
	
	public void deleteExpenseReportById(Long id){
		expenseReportDao.delete(id);
	}
	
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String deleteExpenseReport(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			ExpenseReport  expenseReport = expenseReportDao.get(Long.valueOf(id));
			if(deleteRight(expenseReport)){
				if(expenseReport.getWorkflowInfo()!=null){
					ApiFactory.getInstanceService().deleteInstance(expenseReportDao.get(Long.valueOf(id)));
				}else{
					expenseReportDao.delete(expenseReport);
				}
				deleteNum++;
			}else{
				failNum++;
			}
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}
	
	private boolean deleteRight(ExpenseReport expenseReport){
		return ApiFactory.getInstanceService().isInstanceComplete(expenseReport)||ApiFactory.getInstanceService().canDeleteInstanceInTask(expenseReport, expenseReport.getWorkflowInfo().getCurrentActivityName());
	}

	public void deleteExpenseReport(ExpenseReport expenseReport){
		expenseReportDao.delete(expenseReport);
	}

	public Page<ExpenseReport> search(Page<ExpenseReport>page){
		return expenseReportDao.search(page);
	}

	public List<ExpenseReport> listAll(){
		return expenseReportDao.getAllExpenseReport();
	}
		
	/**
	 * 得到所有意见集合
	 */
	public List<Opinion> getOpinions(ExpenseReport expenseReport) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		return ApiFactory.getOpinionService().getOpinions(expenseReport);
	}

	public ExpenseReport getExpenseReportByTaskId(Long taskId) {
		if(taskId==null)return null;
		return getExpenseReport(ApiFactory.getFormService().getFormFlowableIdByTask(taskId));
	}

	/*
	 * 删除流程实例时的回调方法（在流程参数中配置了beanName）
	 * 
	 * @see com.norteksoft.wf.engine.client.FormFlowableDeleteInterface#
	 * deleteFormFlowable(java.lang.Long)
	 */
	@Override
	public void deleteFormFlowable(Long id) {
		expenseReportDao.delete(id);
	}
	
	/**
	 * 得到意见
	 * @param opinion
	 * @param submitflag
	 * @param opinionflag
	 * @return
	 */
	public String getOpinion(String opinion,boolean submitflag,boolean opinionflag){
		String str;
		if(submitflag){
			str="已阅。 ";
		}else{
			if(opinionflag)
				str="同意。 ";
			else
				str="不同意。 ";
		}
		if(StringUtils.isNotEmpty(opinion))str=str+"其它意见："+opinion;
		return str;
	}

	public void saveLog(String opType,ExpenseReport expenseReport){
		ThreadParameters parameters=new ThreadParameters();
		parameters.setCompanyId(expenseReport.getCompanyId());
		parameters.setUserName(expenseReport.getCreator());
		parameters.setLoginName(expenseReport.getCreator());
		ParameterUtils.setParameters(parameters);
		ApiFactory.getBussinessLogService().log(opType, "上传文件");
	}
	
	/**
	 * 返回环节办理人是否具有创建正文的权限
	 * @param incomeArchives
	 * @param taskId
	 * @return
	 */
	public boolean getOfficialTextCreateRight(String defCode){
		return ApiFactory.getPermissionService().getActivityPermission(defCode).getDocumentCreateable();
	}

	/**
	 * 获得保留编辑痕迹
	 * @param documentTaskId
	 * @return
	 */
	public String getEditRight(Long documentTaskId) {
		return ApiFactory.getPermissionService().getDocumentPermission(documentTaskId);
	}
	
	/**
	 * 返回环节办理人是否具有创建正文的权限
	 * @param incomeArchives
	 * @param taskId
	 * @return
	 */
	public boolean getOfficialTextCreateRight(Long taskId){
		return ApiFactory.getPermissionService().getActivityPermission(taskId).getDocumentCreateable();
	}
	
	/**
	 * 下载正文权限
	 * @return
	 */
	public boolean isDownLoadDocument(Long taskId){
		if(taskId!=null){
			return ApiFactory.getPermissionService().getActivityPermission(taskId).getDocumentDownloadable();
		}
		return false;
	}
	
	/**
	 * 得加签人员（选所有人的时候）
	 * @param expenseReport
	 */
	public void getSignName(ExpenseReport expenseReport){
		List<User> allUser = ApiFactory.getAcsService().getUsersByCompany(ContextUtils.getCompanyId());
		String signLoginNames = "";
		for (User user : allUser) {
			signLoginNames+=user.getLoginName()+",";
		}
		expenseReport.setSignLoginNames(signLoginNames.substring(0, signLoginNames.length()-1));
	}

	/**
	 * 得批示传阅人员（选所有人的时候）
	 * @param expenseReport
	 */
	public void getReadName(ExpenseReport expenseReport) {
		List<User> allUser = ApiFactory.getAcsService().getUsersByCompany(ContextUtils.getCompanyId());
		String signLoginNames = "";
		for (User user : allUser) {
			signLoginNames+=user.getLoginName()+",";
		}
		expenseReport.setReadLoginNames(signLoginNames.substring(0, signLoginNames.length()-1));
	}
	
	/**
	 * 得到当前环节办理人（包括受托人）
	 * @param expenseReport
	 * @return
	 */
	public List<WorkflowTask> getTaskHander(ExpenseReport expenseReport,Long taskId) {
	  WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
	 // List<String[]> transactors = ApiFactory.getTaskService().getActivityTaskTransactors(expenseReport);
	 // List<String[]> principals = ApiFactory.getTaskService().getActivityTaskPrincipalsDetail(expenseReport);
	  List<WorkflowTask> taskList = ApiFactory.getTaskService().getActivityTasks(expenseReport);
	 /* List<String[]> result = new ArrayList<String[]>();
	  if(transactors.size()>0)result.addAll(transactors);
	  //当受托人减签委托人时 会报错所以是委托任务是不让选择委托人
	  if(task.getTrustor()==null){
	  if(principals.size()>0)result.addAll(principals);
	  }*/
	  return taskList;
	}

	/**
	 * 取回任务业务补偿
	 */
	@Override
	public void retrieveTaskExecute(Long entityId,Long taskId) {
		 getExpenseReport(entityId);
	}

	/**
	 * 流程正常结束时的业务补偿
	 */
	@Override
	public void endInstanceExecute(Long entityId) {
		ExpenseReport expenseReport = getExpenseReport(entityId);
		expenseReport.getWorkflowInfo().setProcessState(ProcessState.END);
		this.saveEntity(expenseReport);
	}

	@Override
	protected ExpenseReport getEntity(Long id) {
		return expenseReportDao.get(id);
	}

	@Override
	protected void saveEntity(ExpenseReport expenseReport) {
		expenseReportDao.save(expenseReport);
	}
	public String goback(Long taskId){
		return ApiFactory.getTaskService().returnTask(taskId);
	}

	@Override
	public FormFlowable getRequiredSubEntity(Map<String, Object> param) {
		ExpenseReport expenseReport = new ExpenseReport();
		expenseReport.setSignPersons(ContextUtils.getUserName());
		expenseReport.setSignLoginNames(ContextUtils.getLoginName());
		expenseReport.setSignPersonIds(ContextUtils.getUserId()+"");
		expenseReport.setReadLoginNames(ContextUtils.getLoginName());
		expenseReport.setReadPersonIds(ContextUtils.getUserId()+"");
		expenseReport.setReadPersons(ContextUtils.getUserName());
		expenseReport.setCreator(ContextUtils.getLoginName());
		expenseReport.setCompanyId(ContextUtils.getCompanyId());
		expenseReport.setCreatedTime(new Date());
		expenseReport.setCreatorId(ContextUtils.getUserId());
		expenseReport.setName(ContextUtils.getUserName());
		User user = ApiFactory.getDataDictService().getDirectLeader(ContextUtils.getUserId());
		if(user!=null){
			expenseReport.setDirectLeaderName(user.getName());
		}else{
			expenseReport.setDirectLeaderName("空");
		}
		expenseReportDao.save(expenseReport);
		return expenseReport;
	}
}
