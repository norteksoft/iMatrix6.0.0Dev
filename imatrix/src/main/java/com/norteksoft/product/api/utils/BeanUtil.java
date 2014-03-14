package com.norteksoft.product.api.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.Assert;

import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.bs.options.service.OptionGroupManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.DataDictionary;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.FormView;
import com.norteksoft.product.api.entity.ListView;
import com.norteksoft.product.api.entity.Menu;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.OptionGroup;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.task.service.HistoryWorkflowTaskManager;
import com.norteksoft.wf.engine.service.HistoryWorkflowInstanceManager;
import com.norteksoft.wf.engine.service.TaskService;
import com.norteksoft.wf.engine.service.WorkflowInstanceManager;





public class BeanUtil {

	/**
	 * UserList转换成UserModelList
	 */
	public static List<User> turnToModelUserList(Collection<com.norteksoft.acs.entity.organization.User> list) {
		if(list==null) return null;
		List<User> result = new ArrayList<User>();
		User userModel = null;
		for(com.norteksoft.acs.entity.organization.User oldUser : list){
			userModel = new User();
			UserInfo userInfo = oldUser.getUserInfo();
			//user userinfo赋值顺序不能颠倒，因为id赋值的原因
			copy(userModel,oldUser);
			copy(userModel,userInfo);
			if(userInfo!=null)
			userModel.setUserInfoId(userInfo.getId());
			result.add(userModel);
		}
		return result;
	}
	
	/**
	 * UserList转换成UserModelList
	 */
	public static List<User> turnToModelUserList1(List<Object[]> list) {
		if(list==null) return null;
		List<User> result = new ArrayList<User>();
		User userModel = null;
		for(Object[] userObj : list){
			com.norteksoft.acs.entity.organization.User oldUser = (com.norteksoft.acs.entity.organization.User) userObj[0];
			UserInfo oldUserInfo = (UserInfo) userObj[1];
			userModel = new User();
			copy(userModel,oldUser);
			copy(userModel,oldUserInfo);
			userModel.setUserInfoId(oldUserInfo.getId());
			result.add(userModel);
		}
		return result;
	}
	
	/**
	 * UserSet转换成UserModleSet
	 */
	@SuppressWarnings("unchecked")
	public static Set<User> turnToModelUserSet(Set<com.norteksoft.acs.entity.organization.User> oldUserSet ) {
        if(oldUserSet==null) return null;
        Set<User> result = new HashSet<User>();
        User userModel = null;
        Iterator it = oldUserSet.iterator();
        while(it.hasNext()){
        	com.norteksoft.acs.entity.organization.User oldUser = (com.norteksoft.acs.entity.organization.User)it.next();
        	userModel = new User();
		    copy(userModel,oldUser);
		    result.add(userModel);
        }
	    return result;
	}
	
	/**
	 * User转换成UserModel
	 */
	public static User turnToModelUser(com.norteksoft.acs.entity.organization.User oldUser ) {
        if(oldUser==null) return null;
		User userModel = new User();
		UserInfo oldUserInfo = oldUser.getUserInfo();
		copy(userModel,oldUser);
		copy(userModel,oldUserInfo);
		if(oldUserInfo!=null)
		userModel.setUserInfoId(oldUserInfo.getId());
	    return userModel;
	}
	
	/**
	 * UserModel转换成User
	 */
	public static com.norteksoft.acs.entity.organization.User turnToUser(User userModel ) {
		if(userModel==null)return null;
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		com.norteksoft.acs.entity.organization.User user = null;
		if(userModel.getId()==null){
			user = new com.norteksoft.acs.entity.organization.User();
		}else{
			user = userManager.getUserById(userModel.getId());
	    }
		copy(user,userModel);
		UserInfo userInfo = user.getUserInfo();
		copy(userInfo,userModel);
		if(userModel.getUserInfoId()!=null){
		userInfo.setId(userModel.getUserInfoId());
		}
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		userInfoList.add(userInfo);
		user.setUserInfos(userInfoList);
	    return user;
	}
	
	/**
	 * UserModelList转换成UserList
	 */
	public static List<com.norteksoft.acs.entity.organization.User> turnToUserList(List<User> list) {
		if(list==null) return null;
		List<com.norteksoft.acs.entity.organization.User> result = new ArrayList<com.norteksoft.acs.entity.organization.User>();
		com.norteksoft.acs.entity.organization.User user = null;
		UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
		for(User userModel : list){
			if(userModel.getId()==null){
			   user = new com.norteksoft.acs.entity.organization.User();
			}else{
			   user = userManager.getUserById(userModel.getId());
			}
			copy(user,userModel);
			UserInfo userInfo = user.getUserInfo();
			copy(userInfo,userModel);
			if(userModel.getUserInfoId()!=null){
			userInfo.setId(userModel.getUserInfoId());
			}
			List<UserInfo> userInfoList = new ArrayList<UserInfo>();
			userInfoList.add(userInfo);
			user.setUserInfos(userInfoList);
			result.add(user);
		}
		return result;
	}
	/**
	 * Task转换成TaskModel
	 */
	public static WorkflowTask turnToModelTask(com.norteksoft.task.entity.WorkflowTask oldTask ) {
        if(oldTask==null) return null;
        WorkflowTask taskModel = new WorkflowTask();
		copy(taskModel,oldTask);
	    return taskModel;
	}
	/**
	 * Task转换成TaskModel
	 */
	public static WorkflowTask turnHistoryToModelTask(com.norteksoft.task.entity.HistoryWorkflowTask oldTask ) {
		if(oldTask==null) return null;
		WorkflowTask taskModel = new WorkflowTask();
		copy(taskModel,oldTask);
		taskModel.setId(oldTask.getSourceTaskId());
		taskModel.setHistoryTaskId(oldTask.getId());
		return taskModel;
	}
	
	/**
	 * TaskModel转换成Task
	 */
	public static com.norteksoft.task.entity.WorkflowTask turnToTask(WorkflowTask taskModel ) {
        if(taskModel==null) return null;
        TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
        com.norteksoft.task.entity.WorkflowTask oldTask = null;
        if(taskModel.getId()==null){
        	oldTask = new com.norteksoft.task.entity.WorkflowTask();
        }else{
            oldTask = taskService.getTask(taskModel.getId());
        }
		copy(oldTask,taskModel);
	    return oldTask;
	}
	/**
	 * TaskModel转换成HistoryTask
	 */
	public static com.norteksoft.task.entity.HistoryWorkflowTask turnToHistoryTask(WorkflowTask taskModel ) {
		if(taskModel==null) return null;
		HistoryWorkflowTaskManager historyWorkflowTaskManager = (HistoryWorkflowTaskManager)ContextUtils.getBean("historyWorkflowTaskManager");
		com.norteksoft.task.entity.HistoryWorkflowTask oldTask = null;
		if(taskModel.getId()==null){
			oldTask = new com.norteksoft.task.entity.HistoryWorkflowTask();
		}else{
			oldTask = historyWorkflowTaskManager.getTask(taskModel.getHistoryTaskId());
		}
		copy(oldTask,taskModel);
		if(oldTask.getId()!=null){
			oldTask.setId(taskModel.getHistoryTaskId());
		}
		return oldTask;
	}
	
	/**
	 * TaskList转换成TaskModelList
	 */
	public static List<WorkflowTask> turnToModelTaskList(List<com.norteksoft.task.entity.WorkflowTask> oldTaskList ) {
        if(oldTaskList==null) return null;
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowTask taskModel = null;
        for(com.norteksoft.task.entity.WorkflowTask oldTask:oldTaskList){
           taskModel = new WorkflowTask();
		   copy(taskModel,oldTask);
		   result.add(taskModel);
        }
	    return result;
	}
	
	/**
	 * HistoryTaskList转换成TaskModelList
	 */
	public static List<WorkflowTask> turnHistoryTaskToModelTaskList(List<com.norteksoft.task.entity.HistoryWorkflowTask> oldTaskList ) {
        if(oldTaskList==null) return null;
        List<WorkflowTask> result = new ArrayList<WorkflowTask>();
        WorkflowTask taskModel = null;
        for(com.norteksoft.task.entity.HistoryWorkflowTask oldTask:oldTaskList){
           taskModel = new WorkflowTask();
		   copy(taskModel,oldTask);
		   result.add(taskModel);
        }
	    return result;
	}
	
	/**
	 * TaskModelPage转换成TaskPage
	 */
	public static Page<com.norteksoft.task.entity.WorkflowTask> turnToTaskPage(Page<WorkflowTask> modelTaskPage ) {
        if(modelTaskPage==null) return null;
        Page<com.norteksoft.task.entity.WorkflowTask> result = new Page<com.norteksoft.task.entity.WorkflowTask>();
        com.norteksoft.task.entity.WorkflowTask oldTask = null;
        List<com.norteksoft.task.entity.WorkflowTask> oldTaskList = new ArrayList<com.norteksoft.task.entity.WorkflowTask>();
        TaskService taskService = (TaskService)ContextUtils.getBean("taskService");
        for(WorkflowTask modelTask:modelTaskPage.getResult()){
        	if(modelTask.getId()==null){
        		oldTask = new com.norteksoft.task.entity.WorkflowTask();
        	}else{
                oldTask = taskService.getTask(modelTask.getId());
        	}
		      copy(oldTask,modelTask);
		      oldTaskList.add(oldTask);
        }
        result.setResult(oldTaskList);
        result.setOrder(modelTaskPage.getOrder());
        result.setOrderBy(modelTaskPage.getOrderBy());
        result.setPageNo(modelTaskPage.getPageNo());
        result.setPageSize(modelTaskPage.getPageSize());
        result.setTotalCount(modelTaskPage.getTotalCount());
	    return result;
	}
	
	/**
	 * BusinessSystem转换成BusinessSystemModle
	 */
	public static BusinessSystem turnToModelBusinessSystem(com.norteksoft.acs.entity.authorization.BusinessSystem oldBusinessSystem ) {
        if(oldBusinessSystem==null) return null;
        BusinessSystem businessSystemModle = new BusinessSystem();
		copy(businessSystemModle,oldBusinessSystem);
	    return businessSystemModle;
	}
	
	/**
	 * BusinessSystemList转换成BusinessSystemModleList
	 */
	public static List<BusinessSystem> turnToModelBusinessSystemList(List<com.norteksoft.acs.entity.authorization.BusinessSystem> oldBusinessSystemList ) {
        if(oldBusinessSystemList==null) return null;
        List<BusinessSystem> result = new ArrayList<BusinessSystem>();
        BusinessSystem businessSystemModel = null;
        for(com.norteksoft.acs.entity.authorization.BusinessSystem oldBusinessSystem:oldBusinessSystemList){
        	businessSystemModel = new BusinessSystem();
		   copy(businessSystemModel,oldBusinessSystem);
		   result.add(businessSystemModel);
        }
	    return result;
	}
	
	/**
	 * Role转换成RoleModle
	 */
	public static Role turnToModelRole(com.norteksoft.acs.entity.authorization.Role oldRole ) {
        if(oldRole==null) return null;
        Role RoleModle = new Role();
		copy(RoleModle,oldRole);
	    return RoleModle;
	}
	
	/**
	 * RoleList转换成RoleModleList
	 */
	public static List<Role> turnToModelRoleList(List<com.norteksoft.acs.entity.authorization.Role> oldRoleList ) {
        if(oldRoleList==null) return null;
        List<Role> result = new ArrayList<Role>();
        Role roleModel = null;
        for(com.norteksoft.acs.entity.authorization.Role oldTask:oldRoleList){
        	roleModel = new Role();
		   copy(roleModel,oldTask);
		   result.add(roleModel);
        }
	    return result;
	}
	
	/**
	 * RoleSet转换成RoleModleSet
	 */
	@SuppressWarnings("unchecked")
	public static Set<Role> turnToModelRoleSet(Set<com.norteksoft.acs.entity.authorization.Role> oldRoleSet ) {
        if(oldRoleSet==null) return null;
        Set<Role> result = new HashSet<Role>();
        Role roleModel = null;
        Iterator it = oldRoleSet.iterator();
        while(it.hasNext()){
        	com.norteksoft.acs.entity.authorization.Role oldRole = (com.norteksoft.acs.entity.authorization.Role)it.next();
        	roleModel = new Role();
		    copy(roleModel,oldRole);
		    result.add(roleModel);
        }
	    return result;
	}
	
	/**
	 * DepartmentSet转换成DepartmentModleSet
	 */
	@SuppressWarnings("unchecked")
	public static Set<Department> turnToModelDepartmentSet(Set<com.norteksoft.acs.entity.organization.Department> oldDepartmentSet ) {
        if(oldDepartmentSet==null) return null;
        Set<Department> result = new HashSet<Department>();
        Department departmentModel = null;
        Iterator it = oldDepartmentSet.iterator();
        while(it.hasNext()){
        	com.norteksoft.acs.entity.organization.Department oldDepartment = (com.norteksoft.acs.entity.organization.Department)it.next();
        	departmentModel = new Department();
		    copy(departmentModel,oldDepartment);
		    result.add(departmentModel);
        }
	    return result;
	}
	
	
	
	/**
	 * DepartmentList转换成DepartmentModleList
	 */
	public static List<Department> turnToModelDepartmentList(List<com.norteksoft.acs.entity.organization.Department> oldDepartmentList ) {
        if(oldDepartmentList==null) return null;
        List<Department> result = new ArrayList<Department>();
        Department departmentModel = null;
        for(com.norteksoft.acs.entity.organization.Department oldDepartment:oldDepartmentList){
        	departmentModel = new Department();
        	copy(departmentModel,oldDepartment);
		   result.add(departmentModel);
        }
	    return result;
	}
	
	/**
	 * DepartmentModleList转换成DepartmentList
	 */
	public static List<com.norteksoft.acs.entity.organization.Department> turnToDepartmentList(List<Department> departmentModleList ) {
        if(departmentModleList==null) return null;
        List<com.norteksoft.acs.entity.organization.Department> result = new ArrayList<com.norteksoft.acs.entity.organization.Department>();
        com.norteksoft.acs.entity.organization.Department oldDepartment = null;
        DepartmentManager departmentManager = (DepartmentManager)ContextUtils.getBean("departmentManager");
        for(Department departmentModle:departmentModleList){
        	if(departmentModle.getId()!=null){
        	   oldDepartment = new com.norteksoft.acs.entity.organization.Department();
        	}else{
        	   oldDepartment = departmentManager.getDepartment(departmentModle.getId());
        	}
		   copy(oldDepartment,departmentModle);
		   result.add(oldDepartment);
        }
	    return result;
	}
	
	/**
	 * Department转换成DepartmentModle
	 */
	public static Department turnToModelDepartment(com.norteksoft.acs.entity.organization.Department oldDepartment ) {
        if(oldDepartment==null) return null;
        Department departmentModle = new Department();
        com.norteksoft.acs.entity.organization.Department parent = oldDepartment.getParent();
        if(parent!=null){
        	departmentModle.setParentDepartmentId(parent.getId());
        }
		copy(departmentModle,oldDepartment);
	    return departmentModle;
	}
	
	/**
	 * DepartmentModel转换成Department
	 */
	public static com.norteksoft.acs.entity.organization.Department turnToDepartment(Department departmentModel ) {
        if(departmentModel==null) return null;
        DepartmentManager departmentManager = (DepartmentManager)ContextUtils.getBean("departmentManager");
        com.norteksoft.acs.entity.organization.Department oldDepartment = null;
        if(departmentModel.getId()==null){
        	oldDepartment = new com.norteksoft.acs.entity.organization.Department();
        }else{
            oldDepartment = departmentManager.getDepartment(departmentModel.getId());
        }
        if(departmentModel.getParentDepartmentId()!=null){//设置父部门
        	com.norteksoft.acs.entity.organization.Department parent = departmentManager.getDepartment(departmentModel.getParentDepartmentId());
        	if(parent!=null)oldDepartment.setParent(parent);
        }
		copy(oldDepartment,departmentModel);
	    return oldDepartment;
	}
	/**
	 * Workgroup转换成WorkgroupModle
	 */
	public static Workgroup turnToModelWorkgroup(com.norteksoft.acs.entity.organization.Workgroup oldWorkgroup ) {
        if(oldWorkgroup==null) return null;
        Workgroup workgroupModle = new Workgroup();
		copy(workgroupModle,oldWorkgroup);
	    return workgroupModle;
	}
	/**
	 * WorkgroupModle转换成Workgroup
	 */
	public static com.norteksoft.acs.entity.organization.Workgroup turnToWorkgroup(Workgroup workgroupModel ) {
        if(workgroupModel==null) return null;
        WorkGroupManager workGroupManager = (WorkGroupManager)ContextUtils.getBean("workGroupManager");
        com.norteksoft.acs.entity.organization.Workgroup oldWorkgroup = null;
        if(workgroupModel.getId()==null){
        	oldWorkgroup = new com.norteksoft.acs.entity.organization.Workgroup();
        }else{
            oldWorkgroup = workGroupManager.getWorkGroup(workgroupModel.getId());
        }
        copy(oldWorkgroup,workgroupModel);
	    return oldWorkgroup;
	}
	/**
	 * WorkgroupList转换成WorkgroupModleList
	 */
	public static List<Workgroup> turnToModelWorkgroupList(List<com.norteksoft.acs.entity.organization.Workgroup> oldWorkgroupList ) {
		  if(oldWorkgroupList==null) return null;
	        List<Workgroup> result = new ArrayList<Workgroup>();
	        Workgroup workgroupModel = null;
	        for(com.norteksoft.acs.entity.organization.Workgroup oldWorkgroup:oldWorkgroupList){
	        	workgroupModel = new Workgroup();
			   copy(workgroupModel,oldWorkgroup);
			   result.add(workgroupModel);
	        }
		    return result;
	}
	/**
	 * WorkgroupModleList转换成WorkgroupList
	 * 
	 */
	public static List<com.norteksoft.acs.entity.organization.Workgroup> turnToWorkgroupList(List<Workgroup> workgroupModleList ) {
        if(workgroupModleList==null) return null;
        WorkGroupManager workGroupManager = (WorkGroupManager)ContextUtils.getBean("workGroupManager");
        List<com.norteksoft.acs.entity.organization.Workgroup> result = new ArrayList<com.norteksoft.acs.entity.organization.Workgroup>();
        com.norteksoft.acs.entity.organization.Workgroup oldWorkgroup = null;
        for(Workgroup workgroupModle:workgroupModleList){
        	if(workgroupModle.getId()==null){
        		oldWorkgroup = new com.norteksoft.acs.entity.organization.Workgroup();
        	}else{
        	    oldWorkgroup = workGroupManager.getWorkGroup(workgroupModle.getId());
        	}
        	copy(oldWorkgroup,workgroupModle);
		   result.add(oldWorkgroup);
        }
	    return result;
	}
	
	/**
	 * ListView转换成ListViewModle
	 */
	public static ListView turnToModelListView(com.norteksoft.mms.form.entity.ListView oldListView ) {
        if(oldListView==null) return null;
        ListView listViewModle = new ListView();
		copy(listViewModle,oldListView);
	    return listViewModle;
	}
	
	/**
	 * ListViewModle转换成ListView
	 */
	public static com.norteksoft.mms.form.entity.ListView turnToListView(ListView listViewModel ) {
        if(listViewModel==null) return null;
        ListViewManager listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
        com.norteksoft.mms.form.entity.ListView oldListView = null;
        if(listViewModel.getId()==null){
        	oldListView = new com.norteksoft.mms.form.entity.ListView();
        }else{
            oldListView = listViewManager.getView(listViewModel.getId());
        }
        copy(oldListView,listViewModel);
	    return oldListView;
	}
	
	/**
	 * ListViewList转换成ListViewModleList
	 */
	public static List<ListView> turnToModelListViewList(List<com.norteksoft.mms.form.entity.ListView> oldListViewList ) {
		  if(oldListViewList==null) return null;
	        List<ListView> result = new ArrayList<ListView>();
	        ListView listViewModel = null;
	        for(com.norteksoft.mms.form.entity.ListView oldListView:oldListViewList){
	        	listViewModel = new ListView();
			   copy(listViewModel,oldListView);
			   result.add(listViewModel);
	        }
		    return result;
	}
	
	/**
	 * FormView转换成FormViewModle
	 */
	public static FormView turnToModelFormView(com.norteksoft.mms.form.entity.FormView oldFormView ) {
        if(oldFormView==null) return null;
        FormView formViewModle = new FormView();
		copy(formViewModle,oldFormView);
	    return formViewModle;
	}
	
	/**
	 * FormViewModle转换成FormView
	 */
	public static com.norteksoft.mms.form.entity.FormView turnToFormView(FormView formViewModel ) {
        if(formViewModel==null) return null;
        FormViewManager formViewManager = (FormViewManager)ContextUtils.getBean("formViewManager");
        com.norteksoft.mms.form.entity.FormView oldFormView = null;
        if(formViewModel.getId()==null){
            oldFormView = new com.norteksoft.mms.form.entity.FormView();
        }else{
        	oldFormView = formViewManager.getFormView(formViewModel.getId());
        }
		copy(oldFormView,formViewModel);
	    return oldFormView;
	}
	
	/**
	 * Menu转换成MenuModle
	 */
	public static Menu turnToModelMenu(com.norteksoft.mms.module.entity.Menu oldMenu ) {
        if(oldMenu==null) return null;
        Menu menuModle = new Menu();
		copy(menuModle,oldMenu);
	    return menuModle;
	}
	
	/**
	 * MenuList转换成MenuModleList
	 */
	public static List<Menu> turnToModelMenuList(List<com.norteksoft.mms.module.entity.Menu> oldMenuList ) {
		  if(oldMenuList==null) return null;
	        List<Menu> result = new ArrayList<Menu>();
	        Menu menuModel = null;
	        for(com.norteksoft.mms.module.entity.Menu oldMenu:oldMenuList){
	        	menuModel = new Menu();
			   copy(menuModel,oldMenu);
			   result.add(menuModel);
	        }
		    return result;
	}
	
	/**
	 * OptionGroup转换成OptionGroupModle
	 */
	public static OptionGroup turnToModelOptionGroup(com.norteksoft.bs.options.entity.OptionGroup
 oldOptionGroup ) {
        if(oldOptionGroup==null) return null;
        OptionGroup optionGroupModle = new OptionGroup();
		copy(optionGroupModle,oldOptionGroup);
	    return optionGroupModle;
	}
	
	/**
	 * OptionGroupList转换成OptionGroupModleList
	 */
	public static List<OptionGroup> turnToModelOptionGroupList(List<com.norteksoft.bs.options.entity.OptionGroup> oldOptionGroupList ) {
		  if(oldOptionGroupList==null) return null;
	        List<OptionGroup> result = new ArrayList<OptionGroup>();
	        OptionGroup optionGroupModel = null;
	        for(com.norteksoft.bs.options.entity.OptionGroup oldOptionGroup:oldOptionGroupList){
	        	optionGroupModel = new OptionGroup();
			   copy(optionGroupModel,oldOptionGroup);
			   result.add(optionGroupModel);
	        }
		    return result;
	}
	
	/**
	 * Option转换成OptionModle
	 */
	public static Option turnToModelOption(com.norteksoft.bs.options.entity.Option
 oldOption ) {
        if(oldOption==null) return null;
        Option optionModle = new Option();
		copy(optionModle,oldOption);
	    return optionModle;
	}
	
	
	/**
	 * OptionModle转换成Option
	 */
	public static com.norteksoft.bs.options.entity.Option turnToListView(Option optionModel ) {
        if(optionModel==null) return null;
        OptionGroupManager optionGroupManager = (OptionGroupManager)ContextUtils.getBean("optionGroupManager");
        com.norteksoft.bs.options.entity.Option oldOption = null;
        if(optionModel.getId()==null){
        	oldOption = new com.norteksoft.bs.options.entity.Option();
        }else{
            oldOption = optionGroupManager.getOptionById(optionModel.getId());
        }
        copy(oldOption,optionModel);
	    return oldOption;
	}
	
	/**
	 * OptionList转换成OptionModleList
	 */
	public static List<Option> turnToModelOptionList(List<com.norteksoft.bs.options.entity.Option> oldOptionList ) {
		  if(oldOptionList==null) return null;
	        List<Option> result = new ArrayList<Option>();
	        Option optionModel = null;
	        for(com.norteksoft.bs.options.entity.Option oldOption:oldOptionList){
	        	optionModel = new Option();
			    copy(optionModel,oldOption);
			    result.add(optionModel);
	        }
		    return result;
	}
	/**
	 * OptionModleList转换成OptionList
	 */
	public static List<com.norteksoft.bs.options.entity.Option> turnToOptionList(List<Option> optionModleList ) {
        if(optionModleList==null) return null;
        List<com.norteksoft.bs.options.entity.Option> result = new ArrayList<com.norteksoft.bs.options.entity.Option>();
        com.norteksoft.bs.options.entity.Option oldOption = null;
        OptionGroupManager optionGroupManager = (OptionGroupManager)ContextUtils.getBean("optionGroupManager");
        for(Option optionModle:optionModleList){
        	if(optionModle.getId()==null){
        		oldOption = new com.norteksoft.bs.options.entity.Option();	
        	}else{
        	    oldOption = optionGroupManager.getOptionById(optionModle.getId());;
        	}
        	copy(oldOption,optionModle);
		    result.add(oldOption);
        }
	    return result;
	}
	
	/**
	 * DataDictionary转换成DataDictionaryModle
	 */
	public static DataDictionary turnToModelDataDictionary(com.norteksoft.wf.engine.entity.DataDictionary
 oldDataDictionary ) {
        if(oldDataDictionary==null) return null;
        DataDictionary dataDictionaryModle = new DataDictionary();
		copy(dataDictionaryModle,oldDataDictionary);
	    return dataDictionaryModle;
	}
	
	/**
	 * DataDictionaryList转换成DataDictionaryModleList
	 */
	public static List<DataDictionary> turnToModelDataDictionaryList(List<com.norteksoft.wf.engine.entity.DataDictionary
> oldDataDictionaryList ) {
		  if(oldDataDictionaryList==null) return null;
	        List<DataDictionary> result = new ArrayList<DataDictionary>();
	        DataDictionary dataDictionaryModel = null;
	        for(com.norteksoft.wf.engine.entity.DataDictionary oldOption:oldDataDictionaryList){
	        	dataDictionaryModel = new DataDictionary();
			   copy(dataDictionaryModel,oldOption);
			   result.add(dataDictionaryModel);
	        }
		    return result;
	}
	/**
	 * WorkflowInstance转换成WorkflowInstanceModle
	 */
	public static WorkflowInstance turnToModelWorkflowInstance(com.norteksoft.wf.engine.entity.WorkflowInstance
 oldWorkflowInstance ) {
        if(oldWorkflowInstance==null) return null;
        WorkflowInstance workflowInstanceModle = new WorkflowInstance();
		copy(workflowInstanceModle,oldWorkflowInstance);
		if(oldWorkflowInstance.getSubCompanyId()==null){
			workflowInstanceModle.setSubCompanyName(ContextUtils.getCompanyName());
		}
	    return workflowInstanceModle;
	}
	/**
	 * WorkflowInstance转换成WorkflowInstanceModle
	 */
	public static WorkflowInstance turnHistoryToModelWorkflowInstance(com.norteksoft.wf.engine.entity.HistoryWorkflowInstance
			oldWorkflowInstance ) {
		if(oldWorkflowInstance==null) return null;
		WorkflowInstance workflowInstanceModle = new WorkflowInstance();
		copy(workflowInstanceModle,oldWorkflowInstance);
		return workflowInstanceModle;
	}
	
	/**
	 * WorkflowInstanceModle转换成WorkflowInstance
	 */
	public static com.norteksoft.wf.engine.entity.WorkflowInstance turnToWorkflowInstance(WorkflowInstance workflowInstanceModel ) {
        if(workflowInstanceModel==null) return null;
        WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
        com.norteksoft.wf.engine.entity.WorkflowInstance oldWorkflowInstance = null;
        if(workflowInstanceModel.getId()==null){
        	oldWorkflowInstance = new com.norteksoft.wf.engine.entity.WorkflowInstance();
        }else{
            oldWorkflowInstance = workflowInstanceManager.getWorkflowInstance(workflowInstanceModel.getId());
        }
        copy(oldWorkflowInstance,workflowInstanceModel);
	    return oldWorkflowInstance;
	}
	
	/**
	 * 把实体tar的属性值复制到destini相对应的属性
	 */
	@SuppressWarnings("unchecked")
    public static void copy(Object destini, Object tar ){
			try {
				try {
					if(tar!=null){
						Map modelDescribe = BeanUtils.describe(destini);
						Object target = null;
						if(tar instanceof HibernateProxy){
							HibernateProxy proxy = (HibernateProxy)tar;
							target =proxy.getHibernateLazyInitializer().getImplementation();
						}else{
							target = tar;
						}
						List<Field> userFields = null;
						if(target.getClass().getSuperclass()==Object.class){
							userFields = Arrays.asList(target.getClass().getDeclaredFields());
						}else{
							userFields = getAllFieldsIncludeSuperClass(target.getClass());
						}
						for(Field field : userFields){
							if(modelDescribe.containsKey(field.getName())){
								if("id".equals(field.getName())){//判断当前的属性名是否是id
									Object objValue = BeanUtils.getProperty(destini, "id");
									if(objValue!=null){//如果id已有值则不用再赋值，user userinfo中用到
										continue;
									}
								}
								try {
									Object obj = com.norteksoft.product.util.BeanUtils.getFieldValue(target,field.getName());
									if(obj!=null)
									BeanUtils.setProperty(destini, field.getName(),obj);
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
	
    }

	private static List<Field> getAllFieldsIncludeSuperClass(Class clazz) {
		Assert.notNull(clazz);
		List<Field> result = new ArrayList<Field>();
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
				Field[] sub = superClass.getDeclaredFields();
				result.addAll(Arrays.asList(sub));
		}
		return result;
	}
	
	/**
	 * Opinion转换成OpinionModel
	 */
	public static Opinion turnToModelOpinion(com.norteksoft.wf.engine.entity.Opinion opinion ) {
        if(opinion==null) return null;
        Opinion opinionModel = new Opinion();
		copy(opinionModel,opinion);
	    return opinionModel;
	}
	
	/**
	 * 历史Opinion转换成TaskModel
	 */
	public static Opinion turnHistoryToModelOpinion(com.norteksoft.wf.engine.entity.HistoryOpinion historyOpinion ) {
		if(historyOpinion==null) return null;
		Opinion opinionModel = new Opinion();
		copy(opinionModel,historyOpinion);
		return opinionModel;
	}
	
	/**
	 * opinionList转换成opinionModelList
	 */
	public static List<Opinion> turnToModelOpinionList(List<com.norteksoft.wf.engine.entity.Opinion> opinions ) {
        if(opinions==null) return null;
        List<Opinion> result = new ArrayList<Opinion>();
        Opinion opinionModel = null;
        for(com.norteksoft.wf.engine.entity.Opinion opinion:opinions){
        	opinionModel = new Opinion();
		   copy(opinionModel,opinion);
		   result.add(opinionModel);
        }
	    return result;
	}
	
	/**
	 * 历史opinionList转换成opinionModelList
	 */
	public static List<Opinion> turnHistoryToModelOpinionList(List<com.norteksoft.wf.engine.entity.HistoryOpinion> histotyOpinions ) {
        if(histotyOpinions==null) return null;
        List<Opinion> result = new ArrayList<Opinion>();
        Opinion opinionModel = null;
        for(com.norteksoft.wf.engine.entity.HistoryOpinion opinion:histotyOpinions){
        	opinionModel = new Opinion();
		   copy(opinionModel,opinion);
		   result.add(opinionModel);
        }
	    return result;
	}

	public static com.norteksoft.wf.engine.entity.WorkflowInstance turnToInstance(
			WorkflowInstance instanceModel) {
		if(instanceModel==null) return null;
		WorkflowInstanceManager workflowInstanceManager = (WorkflowInstanceManager)ContextUtils.getBean("workflowInstanceManager");
		com.norteksoft.wf.engine.entity.WorkflowInstance instance = null;
        if(instanceModel.getId()==null){
        	instance = new com.norteksoft.wf.engine.entity.WorkflowInstance();
        }else{
        	instance = workflowInstanceManager.getWorkflowInstance(instanceModel.getId());
        }
		copy(instance,instanceModel);
	    return instance;
	}

	public static com.norteksoft.wf.engine.entity.HistoryWorkflowInstance turnToHistoryInctance(WorkflowInstance instanceModel) {
		if(instanceModel==null) return null;
		HistoryWorkflowInstanceManager historyWorkflowInstanceManager = (HistoryWorkflowInstanceManager)ContextUtils.getBean("historyWorkflowInstanceManager");
		com.norteksoft.wf.engine.entity.HistoryWorkflowInstance historyInstance = null;
        if(instanceModel.getId()==null){
        	historyInstance = new com.norteksoft.wf.engine.entity.HistoryWorkflowInstance();
        }else{
        	historyInstance = historyWorkflowInstanceManager.getHistoryWorkflowInstance(instanceModel.getId());
        }
		copy(historyInstance,instanceModel);
	    return historyInstance;
	}
}
