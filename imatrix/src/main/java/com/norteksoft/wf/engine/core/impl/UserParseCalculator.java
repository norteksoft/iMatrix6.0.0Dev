package com.norteksoft.wf.engine.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.mms.form.dao.GeneralDao;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.wf.base.enumeration.CommonStrings;
import com.norteksoft.wf.base.enumeration.LogicOperator;
import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.base.utils.UserUtil;
import com.norteksoft.wf.engine.core.Computable;

/**
 * 对用户解析的运算器
 */
public class UserParseCalculator implements Computable {
	
	private String documentCreator;//文档创建人
	private String currentTransactor;//当前办理人（如果有委托为受托人）
	private String previousTransactor;//上一环节办理人（如果有委托为受托人）
	private String processAdmin;//流程管理员
	private Collection<String> handledTransactors;//已办理人员
	private Collection<String> allHandleTransactors;//已办理人员
	private FormView formView;
	private Long dataId;
	private String approvalResult;//审批结果
	private Long currentTransactorId;//当前办理人id（如果有委托为受托人id）
	private Long documentCreatorId;//文档创建人id
	private Long previousTransactorId;//上一环节办理人id（如果有委托为受托人id）
	private Long processAdminId;//流程管理员id
	private Collection<Long> handledTransactorIds;//已办理人员id
	private Collection<Long> allHandleTransactorIds;//已办理人员id
	
	public static final String SQUARE_BRACKETS_LEFT = "[";
	public static final String SQUARE_BRACKETS_RIGHT = "]";
	public static final String DELTA_START_REGEXP = "\\[";

	public Boolean execute(String atomicExpress) {
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		if(StringUtils.isEmpty(atomicExpress)) return false;
		boolean result = false;
		atomicExpress = atomicExpress.trim();
		if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_NAME)==0){
			//表达式左边为文档创建人姓名
			result = parseUser(atomicExpress,documentCreator,documentCreatorId);
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_ROLE)==0){
			//表达式左边为文档创建人角色
			result = parseRole(atomicExpress,documentCreator,documentCreatorId);
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)==0){
			//表达式左边为文档创建人部门
			result = parseDepartment(atomicExpress,documentCreator,documentCreatorId);
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)==0){
			//表达式左边为文档创建人上级部门 
			List<Department> departments2 = new ArrayList<Department>();
			if(documentCreatorId!=null){
				departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(documentCreatorId);
			}else{
				departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(documentCreator);
			}
			result = parseDepartment(atomicExpress,departments2);
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)==0){
			//表达式左边为文档创建人顶级部门
			List<Department> departments2 = new ArrayList<Department>();
			if(documentCreatorId!=null){
				departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreatorId);
			}else{
				departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
			}
			result = parseDepartment(atomicExpress,departments2);	
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_WORKGROUP)==0){
			//表达式左边为文档创建人工作组
			result = parseWorkGroup(atomicExpress,documentCreator,documentCreatorId);
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME)==0){
			//表达式左边为文档创建人直属上级名称
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)==0){
			//表达式左边为文档创建人直属上级部门
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_ROLE)==0){
			//表达式左边为文档创建人直属上级角色
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)==0){
			//表达式左边为文档创建人直属上级工作组
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_NAME)==0){
			//当前办理人姓名
			result = parseUser(atomicExpress,currentTransactor,currentTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_ROLE)==0){
			result = parseRole(atomicExpress,currentTransactor,currentTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_DEPARTMENT)==0){
			result = parseDepartment(atomicExpress,currentTransactor,currentTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)==0){
			List<Department> departments2 = new ArrayList<Department>();
			if(currentTransactorId!=null){
				departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactorId);
			}else{
				departments2 = ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactor);
			}
			result = parseDepartment(atomicExpress,departments2);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)==0){
			List<Department> departments2 = new ArrayList<Department>();
			if(currentTransactorId!=null){
				departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactorId);
			}else{
				departments2 = ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactor);
			}
			result = parseDepartment(atomicExpress,departments2);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_WORKGROUP)==0){
			result = parseWorkGroup(atomicExpress,currentTransactor,currentTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME)==0){
			User user=rankManager.getDirectLeader(currentTransactorId,currentTransactor);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)==0){
			User user=rankManager.getDirectLeader(currentTransactorId,currentTransactor);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE)==0){
			User user=rankManager.getDirectLeader(currentTransactorId,currentTransactor);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)==0){
			User user=rankManager.getDirectLeader(currentTransactorId,currentTransactor);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_NAME)==0){
			result = parseUser(atomicExpress,previousTransactor,previousTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_ROLE)==0){
			result = parseRole(atomicExpress,previousTransactor,previousTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)==0){
			result = parseDepartment(atomicExpress,previousTransactor,currentTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)==0){
			result = parseWorkGroup(atomicExpress,previousTransactor,previousTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_NAME)==0){
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			result = user==null?false:parseUser(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)==0){
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			result = user==null?false:parseDepartment(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_ROLE)==0){
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			result = user==null?false:parseRole(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)==0){
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			result = user==null?false:parseWorkGroup(atomicExpress,user.getLoginName(),user.getId());
		}else if(atomicExpress.indexOf(CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)==0){
			result = parseWorkGroup(atomicExpress,previousTransactor,previousTransactorId);
		}else if(atomicExpress.indexOf(CommonStrings.APPROVAL_RESULT)==0){
			parseApprovalResult(atomicExpress);
		}
		return result;
	}
	/*
	 * 解析表达式 返回部门列表
	 */
	private List<Department> getDepartmentByExpress(String valueExpress){
		List<Department> result = new ArrayList<Department>();
		if(StringUtils.isEmpty(valueExpress)) return result;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			Department tempDepartment = ApiFactory.getAcsService().getDepartmentByCode(departmentName);
			if(tempDepartment==null){
				tempDepartment = ApiFactory.getAcsService().getDepartmentByName(departmentName);
			}
			if(tempDepartment!=null)result.add(tempDepartment);
			return result;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)){
			//创建人部门
			if(documentCreatorId!=null){
				return ApiFactory.getAcsService().getDepartments(documentCreatorId);
			}else{
				return ApiFactory.getAcsService().getDepartments(documentCreator);
			}
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//创建人上级部门
			if(documentCreatorId!=null){
				return ApiFactory.getAcsService().getParentDepartmentsByUser( documentCreatorId);
			}else{
				return ApiFactory.getAcsService().getParentDepartmentsByUser( documentCreator);
			}
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//创建人顶级部门
			if(documentCreatorId!=null){
				return ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreatorId);
			}else{
				return ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
			}
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门(当前办理人所在部门为顶级部门)
			return ApiFactory.getAcsService().getDepartmentsByCompany();
		}else if(valueExpress.trim().equals(CommonStrings.CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT)){
			//当前办理人的上级部门
			if(currentTransactorId!=null){
				return ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactorId);
			}else{
				return ApiFactory.getAcsService().getParentDepartmentsByUser(currentTransactor);
			}
		}else if(valueExpress.trim().equals(CommonStrings.CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT)){
			//当前办理人的顶级部门
			if(currentTransactorId!=null){
				return ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactorId);
			}else{
				return ApiFactory.getAcsService().getTopDepartmentsByUser(currentTransactor);
			}
		}else{
			Department tempDepartment = ApiFactory.getAcsService().getDepartmentByCode(valueExpress);
			if(tempDepartment==null){
				tempDepartment = ApiFactory.getAcsService().getDepartmentByName(valueExpress);
			}
			if(tempDepartment!=null)result.add(tempDepartment);
			return result;
		}
	}
	/*
	 * 判断两个部门集合是不是有交集
	 */
	private boolean haveIntersectionDepartment(List<Department> departments1,List<Department> departments2) {
		if(departments1.size()==0||departments2.size()==0) return false;
		if(departments1.size()<departments2.size()){
			for(Department department :departments1){
				if(deptContains(departments2,department)) return true;
			}
		}else{
			for(Department department :departments2){
				if(deptContains(departments1,department)) return true;
			}
		}
		return false;
	}
	
	private boolean deptContains(List<Department> departments,Department department){
		for(Department dept :departments){
			if(dept.getId().equals(department.getId())){
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析表单式和部门的关系
	 */
	private boolean parseDepartment(String atomicExpress,List<Department> departments2) {
		List<Department> department1 = getDepartmentByExpress(getExpressCode(atomicExpress));
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			return !haveIntersectionDepartment(department1,departments2);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return haveIntersectionDepartment(department1,departments2);
		}
		return false;
	}

	private boolean parseApprovalResult(String atomicExpress){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
				return !approvalResult.equals(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
				return approvalResult.equals(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			}
			return false;
	}
	private static final String SINGLE_QUOTATION_MARK = "'";
	/**
	 * 解析用户和工作组的关系
	 * ${documentCreatorWorkGroup} operator.text.et '${field[姓名[name]]}'
	 * ${documentCreatorWorkGroup} operator.text.et 'SBU工作组'
	 * ${currentTransactorWorkGroup} operator.text.et '${documentCreatorWorkGroup}'
	 */
	private   boolean parseWorkGroup(String atomicExpress,String loginName,Long userId){
		 if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			return !userInWorkGroup(getExpressCode(atomicExpress), userId );
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return userInWorkGroup(getExpressCode(atomicExpress), userId );
		}
		return false;
	}
	private  boolean userInWorkGroup(String valueExpress,Long userId){
		RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		if(StringUtils.isEmpty(valueExpress)) return false;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String workGroupName = getValue(fieldName);
			if(StringUtils.isNotEmpty(workGroupName)){
				for(String fieldValue:workGroupName.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							AcsServiceImpl acsServiceImpl=(AcsServiceImpl)ContextUtils.getBean("AcsServiceImpl");
							List<Workgroup> workgroups = acsServiceImpl.getWorkgroupsByCompany();
							for(Workgroup wg:workgroups){
								boolean result = UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), userId, wg.getName());
								if(result)return result;
							}
						}else{
							boolean result = UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), userId, fieldValue);
							if(result)return result;
						}
					}
				}
			}
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_WORKGROUP)){
			List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByUser(documentCreator);
			for (com.norteksoft.product.api.entity.Workgroup workGroup : workGroups) {
				if(UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), userId, workGroup.getName())){
					return true;
				}
			}
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user =rankManager.getDirectLeader(currentTransactorId,currentTransactor);
			if(user==null){
				return false;
			}else{
				List<com.norteksoft.product.api.entity.Workgroup> workGroups = ApiFactory.getAcsService().getWorkgroupsByUser(user.getId());
				for (com.norteksoft.product.api.entity.Workgroup workGroup : workGroups) {
					if(UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), userId, workGroup.getName())){
						return true;
					}
				}
			}
			return false;
		}else{
			return UserUtil.userInWorkGroup(ContextUtils.getCompanyId(), userId, valueExpress);
		}
	}
	
	
	/**
	 * 解析用户和部门的关系
	 * ${documentCreatorDepartment} operator.text.et '${upstageDepartment}'
	 * ${documentCreatorName} operator.text.et '${field[姓名[name]]}' 
	 * ${documentCreatorDepartment} operator.text.et '财务部' 
	 * ${currentTransactorDepartment} operator.text.et '${documentCreatorDepartment}' 
	 * ${currentTransactorDepartment} operator.text.et '${superiorDepartment}'
	 */
	private  boolean parseDepartment(String atomicExpress,String loginName,Long userId){
		if(userId==null){
			com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginName(loginName);
			if(u!=null){
				userId = u.getId();
			}
		}
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			return !userInDepartment(getExpressCode(atomicExpress),userId);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			return userInDepartment(getExpressCode(atomicExpress),userId);
		}
		return false;
	}
	
	private  boolean userInDepartment(String valueExpress,Long userId){
		if(StringUtils.isEmpty(valueExpress)) return false;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			if(StringUtils.isNotEmpty(departmentName)){
				for(String fieldValue:departmentName.split(",")){//字段中选择了多个部门时
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("所有人员".equals(fieldValue)||"ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							return true;
						}else{
							boolean result = UserUtil.userInDepartment(ContextUtils.getCompanyId(), userId, fieldValue);
							if(result)return result;
						}
					}
				}
			}
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT) 
				|| valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//创建人部门
			List<Department> departments = ApiFactory.getAcsService().getDepartments(userId);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), userId, department.getName())){
					return true;
				}
			}
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//创建人上级部门
			List<Department> departments = ApiFactory.getAcsService().getParentDepartmentsByUser( documentCreator);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), userId, department.getName())){
					return true;
				}
			}
			
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//创建人顶级部门
			List<Department> departments = ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreator);
			for (Department department : departments) {
				if(UserUtil.userInDepartment(ContextUtils.getCompanyId(), userId, department.getName())){
					return true;
				}
			}
			
			return false;
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门(当前办理人所在部门为顶级部门)
			List<Department> departments = ApiFactory.getAcsService().getDepartments(userId);
			
			List<Department> topDepts = ApiFactory.getAcsService().getDepartmentsByCompany();
			for (Department department : departments) {
				if(deptContains(topDepts, department)){
					return true;
				}
			}
			return false;
		}else{
			return UserUtil.userInDepartment(ContextUtils.getCompanyId(), userId, valueExpress);
		}
	}
	
	
	/**
	 * 解析用户是否拥有某角色 角色来源：字段中或组织结果中
	 * ${currentTransactorRole} operator.text.et '${field[姓名[name]]}' 
	 * ${currentTransactorRole} operator.text.et '安全管理员'
	 */
	private  boolean parseRole(String atomicExpress,String loginName,Long userid){
		if(userid==null){
			com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginName(loginName);
			if(u!=null){
				userid = u.getId();
			}
		}
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String roleName = getExpressCode(atomicExpress);
			return !UserUtil.userHaveRole(ContextUtils.getCompanyId(), userid, roleName);
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String roleName = getExpressCode(atomicExpress);
			return UserUtil.userHaveRole(ContextUtils.getCompanyId(), userid, roleName);
		}
		return false;
	}
	
	private  String getExpressCode(String atomicExpress){
		String valueExpress = StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK);
		if(StringUtils.isEmpty(valueExpress)) return "";
		if(valueExpress.trim().startsWith("${field[")){//老版本中是角色名称，新版本是角色编码
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String roleName = getValue(fieldName);
			return roleName;
		}else if(valueExpress.trim().indexOf(SQUARE_BRACKETS_LEFT)>=0){//roleName[roleCode]
			return valueExpress.trim().substring(valueExpress.trim().indexOf(SQUARE_BRACKETS_LEFT)+1,valueExpress.trim().lastIndexOf(SQUARE_BRACKETS_RIGHT));
		}else{//兼容老版本的流程定义，此处获得的是角色名称
			return valueExpress;
		}
	}
	
	/**
	 * 解析用户是否满足条件 ，判断条件的值有3中来源，分别为组织结构中、表单字段中和标准值
	 * ${currentTransactorName} operator.text.et '吴荣[wurong]'，新版本中的多分支机构时改为了'吴荣[wurong~~nortek]',同时还要兼容老版本的 
	 *  ${currentTransactorName} operator.text.et '${documentCreatorName}'
	 * ${currentTransactorName} operator.text.et '${field[姓名[name]]}
	 */
	private  boolean parseUser(String atomicExpress,String loginName,Long userid){
		if(StringUtils.contains(atomicExpress, TextOperator.NET.getCode())) {
			String userId = getUserId(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
//			userId:老版本记录的是登录名，新版本有多分支时获得的是用户id
			if(StringUtils.isNotEmpty(userId)){
				for(String fieldValue:userId.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							return false;
						}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
							for(Long uid:userids){
								//zhangsan的id,lisi的id 当前为lisi的id，应返回false，说明当前用户loginName包含在结果集中
								boolean result = userid.equals(uid);
								if(result) return false;
							}
						}else{
							//zhangsan的id,lisi的id 当前为lisi的id，应返回false，说明当前用户loginName包含在结果集中
							boolean result = false;
							if(fieldValue.matches(CommonStrings.NUMBER_REG)){//是数字表示是用户id
								result = loginName.equals(fieldValue);
								if(!result)result = userid.equals(Long.parseLong(fieldValue));
							}else{
								if(fieldValue.indexOf(CommonStrings.LOGINNAME_COMPANY_SPLIT)>=0){//loginName~~companyCode
									String[] userinfos = fieldValue.split(CommonStrings.LOGINNAME_COMPANY_SPLIT);
									com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginNameAndBranchCode(userinfos[0], userinfos[1]);
									if(u!=null){
										result = userid.equals(u.getId());
									}
								}else{//loginName  为了兼容老版本的流程定义文件
									result = loginName.equals(fieldValue);
								}
							}
							if(result) return false;
						}
					}
				}
				return true;
			}
			return false;
		}else if(StringUtils.contains(atomicExpress, TextOperator.ET.getCode())){
			String userId = getUserId(StringUtils.substringBetween(atomicExpress, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			for(String fieldValue:userId.split(",")){
				fieldValue = fieldValue.trim();
				if(StringUtils.isNotEmpty(fieldValue)){
					if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
						return true;
					}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
						List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
						for(Long uid:userids){
							return userid.equals(uid);
						}
					}else{
						boolean result = false;
						if(fieldValue.matches(CommonStrings.NUMBER_REG)){//是数字表示是用户id
							result = loginName.equals(fieldValue);//表示该数字是登录名
							if(!result){
								result = userid.equals(Long.parseLong(fieldValue));
							}
						}else{
							if(fieldValue.indexOf(CommonStrings.LOGINNAME_COMPANY_SPLIT)>=0){//loginName~~companyCode
								String[] userinfos = fieldValue.split(CommonStrings.LOGINNAME_COMPANY_SPLIT);
								com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginNameAndBranchCode(userinfos[0], userinfos[1]);
								if(u!=null){
									result = userid.equals(u.getId());
								}
							}else{//loginName  为了兼容老版本的流程定义文件
								result = loginName.equals(fieldValue);
							}
						}
						return result;
					}
				}
			}
		}
		return false;
	}
	
	private  String getUserId(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		if(StringUtils.isEmpty(valueExpress)) return "";
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String loginName = getValue(fieldName);
			return loginName;
		}else if(valueExpress.trim().endsWith("${documentCreatorName}")){
			if(documentCreatorId==null)return null;
			return documentCreatorId+"";
		}else if(valueExpress.trim().endsWith(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME)){
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			return user==null?null:user.getId()+"";
		}else{
			return StringUtils.substringBetween(valueExpress, "[", "]");
		}
	}

	@SuppressWarnings("unchecked")
	private String getValue(String fieldName){
		 GeneralDao generalDao  =  (GeneralDao)ContextUtils.getBean("generalDao");
		 FormViewManager formManager = (FormViewManager)ContextUtils.getBean("formViewManager");
		 TableColumnManager tableColumnManager = (TableColumnManager)ContextUtils.getBean("tableColumnManager");
		String value = "";
		boolean isSql = true;
		Object entity = null;
		Map dataMap = null;
		//标准表单
		if(!formView.isStandardForm()){
			//自定义表单
			dataMap = formManager.getDataMap(formView.getDataTable().getName(), dataId);
		}else if(formView.isStandardForm()){
			try{
				Class.forName(formView.getDataTable().getEntityName());//判断是否存在该类型
				entity = generalDao.getObject(formView.getDataTable().getEntityName(),dataId);
				isSql=false;
			}catch(ClassNotFoundException e){
				dataMap = formManager.getDataMap(formView.getDataTable().getName(), dataId);
			}
		}
		if(isSql){
			Object obj = null;
			if(!formView.isStandardForm()){
				//自定义表单
				obj = dataMap.get(JdbcSupport.FORM_FIELD_PREFIX_STRING+fieldName);
			}else{
				//标准表单
				String dbName = fieldName;
				TableColumn column=tableColumnManager.getTableColumnByColName(formView.getDataTable().getId(), fieldName);
				if(column!=null){
					dbName=column.getDbColumnName();
				}
				if(StringUtils.isNotEmpty(fieldName))obj = dataMap.get(dbName);
			}
			if(obj==null){
				value = "";
			}else{
				value = obj.toString();
			}
		}else{
			//标准表单
			try {
				Object object = BeanUtils.getProperty(entity, fieldName);
				if(object==null){
					value = "";
				}else{
					value = object.toString();
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		
		return value;
	}
	
	
	//--------------获得用户
	
	
	/**
	 * 从条件中取得用户
	 * 算法：
	 * A OR B AND ((C OR D) OR E AND F) AND G OR (H OR I) AND J
	 * 截取	从右向左找第一 '('  从'('的位置向右找第一个 ） 将它中间内容存为 x1，并将它们替换为 x1 x1= H OR I
	 *  得 A OR B AND ((C OR D) OR E AND F) AND G OR x1 AND J
	 *  截取	从右向左找第一 （  从左向右找第一个 ） 将它中间内容存为 x2，并将它们替换为 x2 = C OR D
	 *  得 A OR B AND (x2 OR E AND F) AND G OR x1 AND J
	 
	 *  截取	从右向左找第一 （  从左向右找第一个 ） 将它中间内容存为 x3，并将它们替换为 x3 = x2 OR E AND F
	 *  得 A OR B AND x3 AND G OR x1 AND J
	 * 
	 * 再以 or 分割表达式 得 y1 = A ；y2 = B AND x3 AND G ; y3 = x1 AND J
	 
	 *  再以 and 分割表达式 的 y2{z1 = B ; z2 = x3; z3 = G;} y3{x1 , J}
	 * 
	 * @param userCondition
	 * @return 满足条件的用户
	 */
	public Set<Long> getUsers(String userCondition,Long systemId,Long companyId){
		return parseBrackets(userCondition,systemId,companyId);
	}
	private Map<String,String> userMap = new HashMap<String,String>();
	private static char LEFT_BRACKET = '(';
	private static char RIGHT_BRACKET = ')';
	private static String VARIABLE_PRE = "var";
	private Set<Long> parseBrackets(String userCondition,Long systemId,Long companyId){
		int left_Bracket_index = -1;
		int right__Bracket_index = -1;
		String subString = null;
		while(true){
			left_Bracket_index = userCondition.lastIndexOf(LEFT_BRACKET);
			if(left_Bracket_index==-1) break;
			right__Bracket_index = userCondition.indexOf(RIGHT_BRACKET,left_Bracket_index);
			subString = userCondition.substring(left_Bracket_index+1,right__Bracket_index);
			userCondition = StringUtils.replace(userCondition, userCondition.substring(left_Bracket_index,right__Bracket_index+1), VARIABLE_PRE+subString.hashCode());
			userMap.put(VARIABLE_PRE+subString.hashCode(), subString);
		}
		return parseOr(userCondition,systemId,companyId);
	}
	private Set<Long> parseOr(String condition,Long systemId,Long companyId){
		String[] conds = condition.split(LogicOperator.OR.getCode());
		Set<Long> userIds = new HashSet<Long>();
		for(String cond :conds){
			userIds.addAll(parseAnd(cond,systemId,companyId));
		}
		return userIds;
	}
	private Set<Long> parseAnd(String condition,Long systemId,Long companyId){
		String[] conds = condition.split(LogicOperator.AND.getCode());
		Map<Integer,Set<Long>> map = new HashMap<Integer,Set<Long>>();
		int minSize = 100000;//默认为十万。如果一个公司人数超过十万，这个的初始值可能会出问题
		Set<Long> temp = null;
		int minI = 0;
		for(int i=0;i<conds.length;i++){
			if(userMap.get(conds[i].trim())==null){
				temp = parseAtomCondition(conds[i].trim(),systemId,companyId);
				
			}else{
				temp = parseOr(userMap.get(conds[i].trim()),systemId,companyId);
			}
			if(temp.size()==0) return temp;//如果在and条件中有一个条件没有选出人，则整个and条件也没有人
			if(temp.size()<minSize){
				 minSize = temp.size();
				 minI = i;//人数最少的条件的key
			}
			map.put(i, temp);
		}
		Set<Long> result = new HashSet<Long>();
		List<Long> minSet = new ArrayList<Long>(map.get(minI));
		
		for(int j=0;j<minSet.size();j++ ){
			boolean isSelect = true;
			for(int i=0;i<conds.length;i++){
				if(!map.get(i).contains(minSet.get(j))){
					isSelect = false;
					break;
				} 
			}
			if(isSelect)result.add(minSet.get(j));
		}
		return result;
	}
	private Set<Long> parseAtomCondition(String atomCondition,Long systemId,Long companyId){
		Set<Long> userSet =  new HashSet<Long>();
		if(StringUtils.isEmpty(atomCondition))return userSet;
		if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_USER)){
			userSet.addAll(parseUser(atomCondition));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_ROLE)){
			userSet.addAll(parseRole(atomCondition,systemId,companyId));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_DEPARTMENT)){
			userSet.addAll(parseDepartment(atomCondition));
		}else if(atomCondition.trim().startsWith(CommonStrings.SYS_VAR_WORKGROUP)){
			userSet.addAll(parseWorkGroup(atomCondition));
		}else if(atomCondition.trim().equals(CommonStrings.PROCESS_ADMIN)){
			userSet.add(processAdminId);
		}else if(atomCondition.trim().equals(CommonStrings.CURRENTTRANSACTOR)){
			userSet.add(currentTransactorId);
		}else if(atomCondition.trim().equals(CommonStrings.DOCUMENT_CREATOR)){
			userSet.add(documentCreatorId);
		}else if(atomCondition.trim().equals(CommonStrings.PARTICIPANTS_TRANSACTOR)){
			userSet.addAll(handledTransactorIds);
		}else if(atomCondition.trim().equals(CommonStrings.PARTICIPANTS_ALL_TRANSACTOR)){
			userSet.addAll(allHandleTransactorIds);
		}
		return userSet;
	}
	
	private Set<Long> parseWorkGroup(String condition){
		Set<Long> userSet =  new HashSet<Long>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			Set<Workgroup> workgroupSet = getWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersNotInWorkGroup(workgroupSet));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			Set<Workgroup> workgroupSet = getWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersByWorkGroup(workgroupSet));
		}
		return userSet;
	}
	
	private Set<com.norteksoft.acs.entity.organization.Workgroup> getWorkGroup(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		Set<Workgroup> workGroupSet = new HashSet<Workgroup>();
		if(StringUtils.isEmpty(valueExpress)) return workGroupSet;
		if(valueExpress.trim().startsWith("${field[")){
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String workGroupName = getValue(fieldName);
			if(StringUtils.isNotEmpty(workGroupName)){
				for(String fieldValue:workGroupName.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							AcsServiceImpl acsServiceImpl=(AcsServiceImpl)ContextUtils.getBean("AcsServiceImpl");
							List<Workgroup> workgroups = acsServiceImpl.getWorkgroupsByCompany();
							workGroupSet.addAll(workgroups);
						}else{
							Workgroup wg = UserUtil.getWorkGroupByCode(fieldValue);
							if(wg!=null){
								workGroupSet.add(wg);
							}else{//兼容老版本中字段中记录的部门名称
								wg = UserUtil.getWorkGroupByName(fieldValue);
								if(wg!=null)workGroupSet.add(wg);
							}
						}
					}
				}
			}
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_WORKGROUP)){
			List<Workgroup> workGroups = UserUtil.getWorkGroupsByUserId( documentCreatorId);
			workGroupSet.addAll(workGroups);
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			if(user!=null){
				List<Workgroup> workGroups = UserUtil.getWorkGroupsByUserId( user.getId());
				workGroupSet.addAll(workGroups);
			}
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_WORKGROUP)){
			List<Workgroup> workGroups = UserUtil.getWorkGroupsByUserId(previousTransactorId);
			workGroupSet.addAll(workGroups);
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP)){
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			if(user!=null){
				List<Workgroup> workGroups = UserUtil.getWorkGroupsByUserId(user.getId());
				workGroupSet.addAll(workGroups);
			}
		}else{
			if(valueExpress.indexOf(SQUARE_BRACKETS_LEFT)>=0){//workgroupName/companyName[workgroupcode]
				String workGroupCode = valueExpress.trim().substring(valueExpress.trim().indexOf(SQUARE_BRACKETS_LEFT)+1,valueExpress.trim().lastIndexOf(SQUARE_BRACKETS_RIGHT));
				workGroupSet.add(UserUtil.getWorkGroupByCode(workGroupCode));
			}else{
				workGroupSet.add(UserUtil.getWorkGroupByName(valueExpress));
			}
		}
		return workGroupSet;
	}
	
	
	private Set<Long> parseDepartment(String condition){
		Set<Long> userSet =  new HashSet<Long>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(UserUtil.getUsersNotInDepartment(departmentSet));
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			if(departmentSet!=null)userSet.addAll(UserUtil.getUsersByDepartment(departmentSet));
		}
		return userSet;
	}
	
	private Set<Department> getDepartment(String valueExpress){
		 RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		Set<Department> departmentSet = new HashSet<Department>();
		if(StringUtils.isEmpty(valueExpress)) return departmentSet;
		if(valueExpress.trim().startsWith("${field[")){
			//值来自表单字段中
			int start = valueExpress.lastIndexOf(SQUARE_BRACKETS_LEFT);
			int end = valueExpress.indexOf(SQUARE_BRACKETS_RIGHT);
			String fieldName = valueExpress.substring(start + 1, end);
			String departmentName = getValue(fieldName);
			if(StringUtils.isNotEmpty(departmentName)){
				for(String fieldValue:departmentName.split(",")){//字段中选择了多个部门时
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("所有人员".equals(fieldValue)||"ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							departmentSet.addAll(ApiFactory.getAcsService().getAllDepartments());
						}else{
							Department department = UserUtil.getDepartmentByCode(fieldValue);
							if(department!=null){
								departmentSet.add(department);
							}else{//兼容老版本中字段中记录的部门名称
								department = UserUtil.getDepartmentByName(fieldValue);
								if(department!=null)departmentSet.add(department);
							}
						}
					}
				}
			}
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DEPARTMENT)){
			//文档创建人部门
			departmentSet.addAll(UserUtil.getDepartmentsByUserId( documentCreatorId));
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_SUPERIOR_DEPARTMENT)){
			//文档创建人上级部门
			departmentSet.addAll(ApiFactory.getAcsService().getParentDepartmentsByUserId(documentCreatorId));
			
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT)){
			//文档创建人顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getTopDepartmentsByUser(documentCreatorId));
		}else if(valueExpress.trim().equals(CommonStrings.DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//文档创建人直属上级部门
			User user=rankManager.getDirectLeader(documentCreatorId,documentCreator);
			if(user!=null){
				departmentSet.addAll(UserUtil.getDepartmentsByUserId( user.getId()));
			}
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DEPARTMENT)){
			//上环节办理人部门
			departmentSet.addAll(UserUtil.getDepartmentsByUserId( previousTransactorId));
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT)){
			//上环节办理人上级部门
			departmentSet.addAll(ApiFactory.getAcsService().getParentDepartmentsByUserId(previousTransactorId));
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT)){
			//上环节办理人顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getTopDepartmentsByUser(previousTransactorId));
		}else if(valueExpress.trim().equals(CommonStrings.UPSTAGE_DEPARTMENT)){
			//顶级部门
			departmentSet.addAll(ApiFactory.getAcsService().getDepartmentsByCompany());
		}else if(valueExpress.trim().equals(CommonStrings.PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT)){
			//上一环节办理人直属上级的部门
			User user=rankManager.getDirectLeader(previousTransactorId,previousTransactor);
			if(user!=null){
				departmentSet.addAll(UserUtil.getDepartmentsByUserId(user.getId()));
			}
		}else{
			//值来自组织结构中
			if(valueExpress.indexOf(SQUARE_BRACKETS_LEFT)>=0){//departmentName/companyName[departmentcode]
				String departmentCode = valueExpress.trim().substring(valueExpress.trim().indexOf(SQUARE_BRACKETS_LEFT)+1,valueExpress.trim().lastIndexOf(SQUARE_BRACKETS_RIGHT));
				departmentSet.add(UserUtil.getDepartmentByCode(departmentCode));
			}else{
				departmentSet.add(UserUtil.getDepartmentByName(valueExpress));
			}
		}
		return departmentSet;
	}
	
	private Set<Long> parseRole(String condition,Long systemId,Long companyId){
		Set<Long> userSet =  new HashSet<Long>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			String roleName = getExpressCode(condition);
			if(StringUtils.isNotEmpty(roleName)){
				Set<Long> userids = new HashSet<Long>();
				List<Long> alluserids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
				for(String fieldValue:roleName.split(",")){//当是字段中的角色时，会有逗号
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						Role role = ApiFactory.getAcsService().getRoleByCode(fieldValue);
						if(role==null){
							List<Role> roles = ApiFactory.getAcsService().getRolesByName(fieldValue);
							for(Role r:roles){
								userids.addAll(ApiFactory.getAcsService().getUserIdsByRoleCodeExceptTrustedRole(systemId, r.getCode()));
							}
						}else{
							userids.addAll(ApiFactory.getAcsService().getUserIdsByRoleCodeExceptTrustedRole(systemId, fieldValue));
						}
					}
				}
				
				alluserids.removeAll(userids);
				userSet.addAll(alluserids);
			}
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			String roleName = getExpressCode(condition);
			if(StringUtils.isNotEmpty(roleName)){
				for(String fieldValue:roleName.split(",")){//当是字段中的角色时，会有逗号
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						userSet.addAll(UserUtil.getUsersByRoleName(fieldValue,systemId,companyId));//兼容老版本的流程定义
						userSet.addAll(UserUtil.getUsersByRoleCode(fieldValue,systemId,companyId));//新版本的流程定义记录的是角色编码
					}
				}
			}
		}
		return userSet;
	}
	
	private Set<Long> parseUser(String condition){
		Set<Long> userSet =  new HashSet<Long>();
		if(StringUtils.contains(condition, TextOperator.NET.getCode())) {
			String userId = getUserId(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			List<Long> eqIds = new ArrayList<Long>();
			if(StringUtils.isNotEmpty(userId)){
				for(String fieldValue:userId.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							return userSet;
						}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
							List<Long> allUserids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
							//移除工作组中的所有人
							allUserids.removeAll(userids);
							userSet.addAll(allUserids);
							return userSet;
						}else{
							if(fieldValue.matches("^-?\\d+$")){//是数字表示是用户id,表单中记录的用户id
								eqIds.add(Long.parseLong(fieldValue));
							}else{
								if(fieldValue.indexOf(CommonStrings.LOGINNAME_COMPANY_SPLIT)>=0){//loginName~~companyCode
									String[] userinfos = fieldValue.split(CommonStrings.LOGINNAME_COMPANY_SPLIT);
									com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginNameAndBranchCode(userinfos[0], userinfos[1]);
									if(u!=null)eqIds.add(u.getId());
								}else{//loginName  为了兼容老版本的流程定义文件
									com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginName(fieldValue);
									if(u!=null)eqIds.add(u.getId());
								}
							}
							
						}
					}
				}
				List<Long> allUserids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
				//移除选择的人员
				allUserids.removeAll(eqIds);
				userSet.addAll(allUserids);
			}
		}else if(StringUtils.contains(condition, TextOperator.ET.getCode())){
			String userId = getUserId(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			if(StringUtils.isNotEmpty(userId)){
				for(String fieldValue:userId.split(",")){
					fieldValue = fieldValue.trim();
					if(StringUtils.isNotEmpty(fieldValue)){
						if("ALLCOMPANYID".equals(fieldValue)){//所有人员(不包含无部门人员),只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							List<Long> userids = ApiFactory.getAcsService().getAllUserIdsWithoutAdminByCompany();
							userSet.addAll(userids);
							return userSet;
						}else if("ALLWORKGROUP".equals(fieldValue)){//所有工作组中的人员,只有在条件为【人员  等于 “ 标准字段的值”】时才有用
							List<Long> userids = ApiFactory.getAcsService().getUserIdsByWorkgroup(ContextUtils.getCompanyId());
							userSet.addAll(userids);
							return userSet;
						}else{
							if(fieldValue.matches("^-?\\d+$")){//是数字表示是用户id
								userSet.add(Long.parseLong(fieldValue));
							}else{
								if(fieldValue.indexOf(CommonStrings.LOGINNAME_COMPANY_SPLIT)>=0){//loginName~~companyCode
									String[] userinfos = fieldValue.split(CommonStrings.LOGINNAME_COMPANY_SPLIT);
									com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginNameAndBranchCode(userinfos[0], userinfos[1]);
									if(u!=null)userSet.add(u.getId());
								}else{//loginName 为了兼容老版本的流程定义文件
									com.norteksoft.product.api.entity.User u = ApiFactory.getAcsService().getUserByLoginName(fieldValue);
									if(u!=null)userSet.add(u.getId());
								}
								
							}
						}
					}
				}
			}
		}
		return userSet;
	}
	
	public void setDocumentCreator(String documentCreator) {
		this.documentCreator = documentCreator;
	}

	public void setCurrentTransactor(String currentTransactor) {
		this.currentTransactor = currentTransactor;
	}

	public void setPreviousTransactor(String previousTransactor) {
		this.previousTransactor = previousTransactor;
	}

	public void setFormView(FormView form) {
		this.formView = form;
	}
	
	public FormView getFormView() {
		return formView;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public Long getDataId() {
		return dataId;
	}
	public String getApprovalResult() {
		return approvalResult;
	}
	public void setApprovalResult(String approvalResult) {
		this.approvalResult = approvalResult;
	}
	public void setProcessAdmin(String processAdmin) {
		this.processAdmin = processAdmin;
	}
	public void setHandledTransactors(Collection<String> handledTransactors) {
		this.handledTransactors = handledTransactors;
	}
	public void setAllHandleTransactors(Collection<String> allHandleTransactors) {
		this.allHandleTransactors = allHandleTransactors;
	}
	public String getDocumentCreator() {
		return documentCreator;
	}
	public String getCurrentTransactor() {
		return currentTransactor;
	}
	public String getPreviousTransactor() {
		return previousTransactor;
	}
	public String getProcessAdmin() {
		return processAdmin;
	}
	public Collection<String> getHandledTransactors() {
		return handledTransactors;
	}
	public Collection<String> getAllHandleTransactors() {
		return allHandleTransactors;
	}
	public Long getCurrentTransactorId() {
		return currentTransactorId;
	}
	public void setCurrentTransactorId(Long currentTransactorId) {
		this.currentTransactorId = currentTransactorId;
	}
	public Long getDocumentCreatorId() {
		return documentCreatorId;
	}
	public void setDocumentCreatorId(Long documentCreatorId) {
		this.documentCreatorId = documentCreatorId;
	}
	public Long getProcessAdminId() {
		return processAdminId;
	}
	public void setProcessAdminId(Long processAdminId) {
		this.processAdminId = processAdminId;
	}
	public Long getPreviousTransactorId() {
		return previousTransactorId;
	}
	public void setPreviousTransactorId(Long previousTransactorId) {
		this.previousTransactorId = previousTransactorId;
	}
	public Collection<Long> getHandledTransactorIds() {
		return handledTransactorIds;
	}
	public void setHandledTransactorIds(Collection<Long> handledTransactorIds) {
		this.handledTransactorIds = handledTransactorIds;
	}
	public Collection<Long> getAllHandleTransactorIds() {
		return allHandleTransactorIds;
	}
	public void setAllHandleTransactorIds(Collection<Long> allHandleTransactorIds) {
		this.allHandleTransactorIds = allHandleTransactorIds;
	}
}
