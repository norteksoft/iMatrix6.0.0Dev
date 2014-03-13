package com.example.expense.report.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.Order;
import com.example.expense.entity.Report;
import com.example.expense.report.dao.ReportDao;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.wf.base.enumeration.ProcessState;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.OnStartingSubProcess;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;

@Service
@Transactional
public class ReportManager extends WorkflowManagerSupport<Report> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface,OnStartingSubProcess {
	@Autowired
	private ReportDao reportDao;
	
	@Autowired
	public LogUtilDao logUtilDao;

	public Report getReport(Long id){
		return reportDao.get(id);
	}

	public void saveExpenseReport(Report expenseReport){
		reportDao.save(expenseReport);
	}

	public void deleteExpenseReport(Long id){
		ApiFactory.getInstanceService()
		.deleteInstance(getReport(id));
	}
	
	public void deleteReportById(Long id){
		reportDao.delete(id);
	}
	
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String deleteReport(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			Report  report = reportDao.get(Long.valueOf(id));
			if(deleteRight(report)){
				if(report.getWorkflowInfo()!=null){
					ApiFactory.getInstanceService().deleteInstance(reportDao.get(Long.valueOf(id)));
				}else{
					reportDao.delete(report);
				}
				deleteNum++;
			}else{
				failNum++;
			}
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}
	
	private boolean deleteRight(Report report){
		return ApiFactory.getInstanceService().isInstanceComplete(report)||ApiFactory.getInstanceService().canDeleteInstanceInTask(report, report.getWorkflowInfo().getCurrentActivityName());
	}

	public void deleteExpenseReport(Report report){
		reportDao.delete(report);
	}

	public Page<Report> search(Page<Report>page){
		return reportDao.search(page);
	}

	public List<Report> listAll(){
		return reportDao.getAllExpenseReport();
	}
		
	/**
	 * 得到所有意见集合
	 */
	public List<Opinion> getOpinions(Report report) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		return ApiFactory.getOpinionService().getOpinions(report);
	}

	public Report getReportByTaskId(Long taskId) {
		if(taskId==null)return null;
		return getReport(ApiFactory.getFormService().getFormFlowableIdByTask(taskId));
	}

	/*
	 * 删除流程实例时的回调方法（在流程参数中配置了beanName）
	 * 
	 * @see com.norteksoft.wf.engine.client.FormFlowableDeleteInterface#
	 * deleteFormFlowable(java.lang.Long)
	 */
	@Override
	public void deleteFormFlowable(Long id) {
		reportDao.delete(id);
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

	public void saveLog(String opType,Report report){
		ThreadParameters parameters=new ThreadParameters();
		parameters.setCompanyId(report.getCompanyId());
		parameters.setUserName(report.getCreator());
		parameters.setLoginName(report.getCreator());
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
	 * 得到当前环节办理人
	 * @param expenseReport
	 * @return
	 */
	public List<String[]> getTaskHander(Report report) {
	  return ApiFactory.getTaskService().getActivityTaskTransactors(report);
	}

	/**
	 * 取回任务业务补偿
	 */
	@Override
	public void retrieveTaskExecute(Long entityId,Long taskId) {
		 getReport(entityId);
	}

	/**
	 * 流程正常结束时的业务补偿
	 */
	@Override
	public void endInstanceExecute(Long entityId) {
		Report report = getReport(entityId);
		report.getWorkflowInfo().setProcessState(ProcessState.END);
		this.saveEntity(report);
	}

	@Override
	protected Report getEntity(Long id) {
		return reportDao.get(id);
	}

	@Override
	protected void saveEntity(Report report) {
		reportDao.save(report);
	}
	public String goback(Long taskId){
		return ApiFactory.getTaskService().returnTask(taskId);
	}

	@Override
	public FormFlowable getRequiredSubEntity(Map<String, Object> arg0) {
		Report report = new Report();
		report.setName("qiao");
		report.setDepartment("开发");
		report.setMoney(5.0);
		report.setInvoiceAmount(10.0);
		reportDao.save(report);
		return report;
	}

}
