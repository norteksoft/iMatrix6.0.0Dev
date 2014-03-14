package com.norteksoft.tags.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.task.entity.HistoryWorkflowTask;
import com.norteksoft.task.entity.WorkflowTask;
import com.norteksoft.wf.engine.entity.HistoryInstanceHistory;
import com.norteksoft.wf.engine.entity.HistoryWorkflowInstance;
import com.norteksoft.wf.engine.entity.InstanceHistory;
import com.norteksoft.wf.engine.entity.WorkflowInstance;
import com.norteksoft.wf.engine.service.HistoryWorkflowInstanceManager;
import com.norteksoft.wf.engine.service.InstanceHistoryManager;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;




public class WorkflowHistoryTag extends TagSupport{

	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(WorkflowHistoryTag.class);
	
	private String url;
	private String webRoot;
	private Long companyId;
	private String workflowId;
	private String locale;
	private Long taskId;
	private Boolean view =false;
	private WorkflowInstanceManager workflowInstanceManager;
	 public int doStartTag() throws JspException{  
		 try {
			 workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			 webRoot = SystemUrls.getSystemUrl(ContextUtils.getSystemCode());
			 if(webRoot.lastIndexOf("/")==webRoot.length()-1){//是80端口时
				 webRoot = webRoot.substring(0,webRoot.length()-1);
			 }
			 ((HttpServletRequest)this.pageContext.getRequest()).setCharacterEncoding("utf-8");
			 locale = this.pageContext.getRequest().getLocale().toString();
			 JspWriter out=pageContext.getOut(); 
			 if(taskId!=0){
				 com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
				 workflowId = task.getProcessInstanceId();
				 TaskPermission  permission = ApiFactory.getPermissionService().getActivityPermission(taskId);
				 view=permission.getHistoryVisible();
				 out.print(readScriptTemplet());
			 }else{
				 out.print("taskId没有值");
			 }
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
	     

		 return Tag.EVAL_PAGE;
	 }

	//读取脚本模板
		private String readScriptTemplet() throws Exception{
			List<InstanceHistory> ihs= new ArrayList<InstanceHistory>();
			List<HistoryInstanceHistory> hihs= new ArrayList<HistoryInstanceHistory>();
			boolean isInstanceComplete = workflowInstanceManager.isInstanceInHistory(workflowId,companyId);
			if(isInstanceComplete){//实例在历史实例表中
				hihs = getHistoryInstanceHistorys();
			}else{//实例在实例表中
				ihs = getInstanceHistorys();
			}
			Map<String, Object> root=new HashMap<String, Object>();
			root.put("ctx", webRoot);
			root.put("url", url);
			root.put("companyId", companyId.toString());
			root.put("workflowId", workflowId);
			root.put("view", view);
			root.put("locale", locale);
			root.put("textContent", "列表视图");
			root.put("flashContent", "图形视图");
			root.put("instanceHistory",ihs);
			root.put("historyInstanceHistory",hihs);
			root.put("sequence","序号");
			root.put("name","环节名称");
			root.put("history","流转操作");
			root.put("start","流程开始");
			root.put("end","流程结束");
			root.put("opinion","办理意见");
			root.put("taskId", taskId.toString());
			String result =TagUtil.getContent(root, "workflow/workflowHistory.ftl");
			return result;
		}
		
		private List<InstanceHistory> getInstanceHistorys(){
			InstanceHistoryManager instanceHistoryManager=(InstanceHistoryManager)ContextUtils.getBean("instanceHistoryManager");
			WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
			WorkflowTaskDao workflowTaskDao = (WorkflowTaskDao)ContextUtils.getBean("workflowTaskDao");
			List<InstanceHistory> ihs=instanceHistoryManager.getHistorysByWorkflowId(companyId, workflowId);
			for(int i=0;i<ihs.size();i++){
				InstanceHistory ih=ihs.get(i);
				List<com.norteksoft.product.api.entity.WorkflowInstance> subTacheInstance = workflowInstanceManager.getSubProcessInstanceByTaskName(ih.getInstanceId(), ih.getTaskName());
				if(subTacheInstance.size()>0){//表示此环节是子流程
					List<WorkflowTask> tasks = workflowTaskDao.getTasksByInstanceId(subTacheInstance.get(0).getProcessInstanceId());
					ih.setSubTaskId(tasks.get(0).getId());//设置子流程任务id
				}else{
					ih.setSubTaskId(0l);
				}
				
				String result=ih.getTransactionResult();
				if(result.contains("[")){
					String temp=result.substring(result.indexOf("[")+1,result.indexOf("]"));
					if(temp.equals("transition.approval.result.agree")){
						result=result.substring(0, result.indexOf("[")) + "[同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if (temp .equals( "transition.approval.result.disagree")){
						result=result.substring(0, result.indexOf("[")) + "[不同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if(temp.contains("_")){
						WorkflowInstance workflowInstance = workflowInstanceManager.getWorkflowInstance(workflowId);
						result=result.substring(0, result.indexOf("[")) + "["+workflowInstance.getProcessName()+"]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}
				}
			}
			return ihs;
		}
		private List<HistoryInstanceHistory> getHistoryInstanceHistorys(){
			InstanceHistoryManager instanceHistoryManager=(InstanceHistoryManager)ContextUtils.getBean("instanceHistoryManager");
			HistoryWorkflowInstanceManager historyWorkflowInstanceManager=(HistoryWorkflowInstanceManager)ContextUtils.getBean("historyWorkflowInstanceManager");
			HistoryWorkflowTaskDao historyWorkflowTaskDao = (HistoryWorkflowTaskDao)ContextUtils.getBean("historyWorkflowTaskDao");
			List<HistoryInstanceHistory> ihs=instanceHistoryManager.getHistoryHistorysByWorkflowId(companyId, workflowId);
			for(int i=0;i<ihs.size();i++){
				HistoryInstanceHistory ih=ihs.get(i);
				List<HistoryWorkflowInstance> subTacheHistoryInstance = historyWorkflowInstanceManager.getSubProcessHistoryInstanceByTaskName(ih.getInstanceId(), ih.getTaskName());
				if(subTacheHistoryInstance.size()>0){//表示此环节是子流程
					List<HistoryWorkflowTask> tasks = historyWorkflowTaskDao.getHistoryTasksByInstanceId(subTacheHistoryInstance.get(0).getProcessInstanceId());
					ih.setSubTaskId(tasks.get(0).getSourceTaskId());
				}else{
					ih.setSubTaskId(0l);
				}
				String result=ih.getTransactionResult();
				if(result.contains("[")){
					String temp=result.substring(result.indexOf("[")+1,result.indexOf("]"));
					if(temp.equals("transition.approval.result.agree")){
						result=result.substring(0, result.indexOf("[")) + "[同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if (temp .equals( "transition.approval.result.disagree")){
						result=result.substring(0, result.indexOf("[")) + "[不同意]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}else if(temp.contains("_")){
						HistoryWorkflowInstance workflowInstance = historyWorkflowInstanceManager.getHistoryWorkflowInstance(workflowId);
						result=result.substring(0, result.indexOf("[")) + "["+workflowInstance.getProcessName()+"]" + result.substring(result.lastIndexOf("]") + 1, result.length());
						ih.setTransactionResult(result);
						ihs.set(i, ih);
					}
				}
			}
			return ihs;
		}
	 public int doEndTag() throws JspException{
		 return Tag.EVAL_PAGE;
	 }

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
}
