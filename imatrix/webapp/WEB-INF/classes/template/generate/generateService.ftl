package ${packageName}.service;

import ${entityPath};
import ${packageName}.dao.${entityName}Dao;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;

<#if processFlag?if_exists=="true">
import java.util.Map;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.product.web.wf.WorkflowManagerSupport;
import com.norteksoft.wf.engine.client.EndInstanceInterface;
import com.norteksoft.wf.engine.client.FormFlowableDeleteInterface;
import com.norteksoft.wf.engine.client.OnStartingSubProcess;
import com.norteksoft.wf.engine.client.RetrieveTaskInterface;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.ZTreeNode;

</#if>
@Service
@Transactional
public class ${entityName}Manager <#if processFlag?if_exists=="true">extends WorkflowManagerSupport<${entityName}> implements FormFlowableDeleteInterface,RetrieveTaskInterface,EndInstanceInterface,OnStartingSubProcess </#if>{
	@Autowired
	private ${entityName}Dao ${lowCaseEntityName}Dao;
	

	public ${entityName} get${entityName}(Long id){
		return ${lowCaseEntityName}Dao.get(id);
	}

	public void save${entityName}(${entityName} ${lowCaseEntityName}){
		${lowCaseEntityName}Dao.save(${lowCaseEntityName});
	}

	
	
	public void delete${entityName}(${entityName} ${lowCaseEntityName}){
		${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
	}

	public Page<${entityName}> search(Page<${entityName}>page){
		return ${lowCaseEntityName}Dao.search(page);
	}

	public List<${entityName}> listAll(){
		return ${lowCaseEntityName}Dao.getAll${entityName}();
	}
		
<#if processFlag?if_exists=="true">
 	/*
	 * 删除流程实例时的回调方法（在流程参数中配置了beanName）
	 * 
	 * @see com.norteksoft.wf.engine.client.FormFlowableDeleteInterface#
	 * deleteFormFlowable(java.lang.Long)
	 */
	@Override
	public void deleteFormFlowable(Long id) {}
	
	/**
	 * 取回任务业务补偿
	 */
	@Override
	public void retrieveTaskExecute(Long entityId,Long taskId) {}

	/**
	 * 流程正常结束时的业务补偿
	 */
	@Override
	public void endInstanceExecute(Long entityId) {}

	@Override
	protected ${entityName} getEntity(Long id) {
		return ${lowCaseEntityName}Dao.get(id);
	}

	@Override
	protected void saveEntity(${entityName} ${lowCaseEntityName}) {
		${lowCaseEntityName}Dao.save(${lowCaseEntityName});
	}
	
	@Override
	public FormFlowable getRequiredSubEntity(Map<String, Object> param) {
		return null;
	}
	
		public String goback(Long taskId){
		return ApiFactory.getTaskService().returnTask(taskId);
	}
	
	/**
	 * 得到当前环节办理人
	 * @param taskId
	 * @return
	 */
	public String getTaskHander(Long taskId) {
		com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		
		WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
		String name = "";
		if(instance.getSubCompanyId()==null){
			name = ContextUtils.getCompanyName();
		}else{
			name = instance.getSubCompanyName();
		}
		String parentId = "company_company-company-company";
		ZTreeNode root = new ZTreeNode("company_company-company-company","0", name, "true", "false", "", "", "root", "");
		treeNodes.add(root);
		
		List<com.norteksoft.product.api.entity.WorkflowTask> currentTasks=ApiFactory.getTaskService().getActivityTaskByInstance(task.getProcessInstanceId());
		List<com.norteksoft.product.api.entity.WorkflowTask> currentPrincipalTasks=ApiFactory.getTaskService().getActivityPrincipalTasks(task.getProcessInstanceId());
		int transactNum=currentTasks.size()+currentPrincipalTasks.size();
		if(transactNum>1){
			for(int i=0;i<currentTasks.size();i++){
				com.norteksoft.product.api.entity.WorkflowTask currentTask=currentTasks.get(i);
				if((currentTask.getTransactorId()!=null&&!currentTask.getTransactorId().equals(ContextUtils.getUserId()))){//如果办理人不是当前用户
					String showName = currentTask.getTransactorName();
					Long transactorId = currentTask.getTransactorId();
					if(transactorId==null){
						User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTransactor());
						if(u!=null)transactorId = u.getId();
					}
					String nodeId = "user-"+ currentTask.getTransactorName()+"-"+currentTask.getTransactor()+"-"+(transactorId==null?"":transactorId);
					root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
					treeNodes.add(root);
				}
			}
			for(com.norteksoft.product.api.entity.WorkflowTask currentTask:currentPrincipalTasks){
				if((currentTask.getTransactorId()!=null&&!currentTask.getTransactor().equals(ContextUtils.getUserId()))){//如果办理人不是当前用户
					String showName = currentTask.getTrustorName();
					Long trustorId = currentTask.getTrustorId();
					if(trustorId==null){
						User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTrustor());
						if(u!=null)trustorId = u.getId();
					}
					String nodeId = "user-"+ currentTask.getTrustorName()+"-"+currentTask.getTrustor()+"-"+(trustorId==null?"":trustorId);
					root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
					treeNodes.add(root);
				}
			}
		}
		
		return JsonParser.object2Json(treeNodes);
	}
	
	public ${entityName} get${entityName}ByTaskId(Long taskId) {
		if(taskId==null)return null;
		return get${entityName}(ApiFactory.getFormService().getFormFlowableIdByTask(taskId));
	}

	public void delete${entityName}(Long id){
		ApiFactory.getInstanceService()
		.deleteInstance(get${entityName}(id));
	}
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String delete${entityName}(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			${entityName}  ${lowCaseEntityName} = ${lowCaseEntityName}Dao.get(Long.valueOf(id));
			if(deleteRight(${lowCaseEntityName})){
				if(${lowCaseEntityName}.getWorkflowInfo()!=null){
					ApiFactory.getInstanceService().deleteInstance(${lowCaseEntityName}Dao.get(Long.valueOf(id)));
				}else{
					${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
				}
				deleteNum++;
			}else{
				failNum++;
			}
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}
	
	private boolean deleteRight(${entityName} ${lowCaseEntityName}){
		return ApiFactory.getInstanceService().isInstanceComplete(${lowCaseEntityName})||ApiFactory.getInstanceService().canDeleteInstanceInTask(${lowCaseEntityName}, ${lowCaseEntityName}.getWorkflowInfo().getCurrentActivityName());
	}
<#else>	
	public void delete${entityName}(Long id){
		${lowCaseEntityName}Dao.delete(id);
	}
	/**
	 * 删除实体，流程相关文件都删除
	 * @param ids
	 */
	public String delete${entityName}(String ids) {
		String[] deleteIds = ids.split(",");
		int deleteNum=0;
		int failNum=0;
		for (String id : deleteIds) {
			${entityName}  ${lowCaseEntityName} = ${lowCaseEntityName}Dao.get(Long.valueOf(id));
				${lowCaseEntityName}Dao.delete(${lowCaseEntityName});
				deleteNum++;
		}
		return deleteNum+" 条数据成功删除，"+failNum+" 条数据没有权限删除！";
	}

</#if>
	
}
