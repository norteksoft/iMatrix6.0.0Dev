package com.norteksoft.acs.service.sale;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.dao.authority.ConditionDao;
import com.norteksoft.acs.dao.authority.DataRuleDao;
import com.norteksoft.acs.dao.authority.PermissionDao;
import com.norteksoft.acs.dao.authority.PermissionItemConditionDao;
import com.norteksoft.acs.dao.authority.PermissionItemDao;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.log.Log;
import com.norteksoft.acs.entity.log.LoginLog;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.DepartmentUser;
import com.norteksoft.acs.entity.organization.MailDeploy;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.UserInfo;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.entity.organization.WorkgroupUser;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.acs.entity.sale.SubsciberPricePolicy;
import com.norteksoft.acs.entity.sale.SubscriberItem;
import com.norteksoft.acs.entity.sale.Tenant;
import com.norteksoft.acs.entity.sysSetting.SecuritySetting;
import com.norteksoft.acs.entity.sysSetting.ServerConfig;
import com.norteksoft.bs.holiday.dao.HolidayDao;
import com.norteksoft.bs.options.dao.CornInfoDao;
import com.norteksoft.bs.options.dao.ImportColumnDao;
import com.norteksoft.bs.options.dao.ImportDefinitionDao;
import com.norteksoft.bs.options.dao.InternationDao;
import com.norteksoft.bs.options.dao.InternationOptionDao;
import com.norteksoft.bs.options.dao.JobInfoDao;
import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.options.dao.OptionGroupDao;
import com.norteksoft.bs.rank.dao.RankDao;
import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.signature.dao.SignatureDao;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.FormAttachmentDao;
import com.norteksoft.mms.form.dao.FormViewDao;
import com.norteksoft.mms.form.dao.GenerateSettingDao;
import com.norteksoft.mms.form.dao.GroupHeaderDao;
import com.norteksoft.mms.form.dao.JqGridPropertyDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.module.dao.ButtonDao;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.dao.OperationDao;
import com.norteksoft.portal.dao.BaseSettingDao;
import com.norteksoft.portal.dao.MessageInfoDao;
import com.norteksoft.portal.dao.ThemeDao;
import com.norteksoft.portal.dao.UserThemeDao;
import com.norteksoft.portal.dao.WebpageDao;
import com.norteksoft.portal.dao.WidgetConfigDao;
import com.norteksoft.portal.dao.WidgetDao;
import com.norteksoft.portal.dao.WidgetParameterDao;
import com.norteksoft.portal.dao.WidgetParameterValueDao;
import com.norteksoft.portal.dao.WidgetRoleDao;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.Md5;
import com.norteksoft.task.dao.HistoryWorkflowTaskDao;
import com.norteksoft.task.dao.TaskDao;
import com.norteksoft.task.dao.WorkflowTaskDao;
import com.norteksoft.wf.engine.dao.DataDictionaryDao;
import com.norteksoft.wf.engine.dao.DataDictionaryProcessDao;
import com.norteksoft.wf.engine.dao.DataDictionaryTypeDao;
import com.norteksoft.wf.engine.dao.DataDictionaryUserDao;
import com.norteksoft.wf.engine.dao.DelegateMainDao;
import com.norteksoft.wf.engine.dao.DocumentFileDao;
import com.norteksoft.wf.engine.dao.DocumentTemplateDao;
import com.norteksoft.wf.engine.dao.DocumentTemplateFileDao;
import com.norteksoft.wf.engine.dao.HistoryInstanceHistoryDao;
import com.norteksoft.wf.engine.dao.HistoryOpinionDao;
import com.norteksoft.wf.engine.dao.HistoryWorkflowInstanceDao;
import com.norteksoft.wf.engine.dao.InstanceHistoryDao;
import com.norteksoft.wf.engine.dao.OfficeDao;
import com.norteksoft.wf.engine.dao.OpinionDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentFileDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionFileDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateDao;
import com.norteksoft.wf.engine.dao.WorkflowDefinitionTemplateFileDao;
import com.norteksoft.wf.engine.dao.WorkflowInstanceDao;
import com.norteksoft.wf.engine.dao.WorkflowTypeDao;

/**
 * 租户管理
 * 
 */
@Service
@Transactional
public class TenantManager{
	private static final String systemAdminCode = "systemAdmin";
	private static final String securityAdminCode = "securityAdmin";
	private static final String auditAdminCode = "auditAdmin";
	private static final String systemAdminRoleCode = "SystemAdmin";
	private static final String securityAdminRoleCode = "SecurityAdmin";
	private static final String auditAdminRoleCode = "AuditAdmin";
	private static final String FORM_MANAGER = "form_manager";
	private static final String TYPE_MANAGER = "type_manager";
	private static final String OPTIONS_MANAGER = "options_manager";
	private static final String MODUEL_MANAGER = "moduel_manager";
	private static final String MMS_COMMON_ROLE = "mms_common_role";
	private static final String PORTAL_COMMON_USER = "portalCommonUser";
	private static final String WF_COMMONS="wfCommons";
	private static final String WORKFLOW_MANAGER="workflowManager";
	private static final String BS_COMMON="common_user";//基础设置权限
	
	private SimpleHibernateTemplate<Tenant, Long> tenantDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private SimpleHibernateTemplate<MailDeploy, Long> mailDeployDao;
	private SimpleHibernateTemplate<Subsciber, Long> subsciberDao;
	private SimpleHibernateTemplate<UserInfo, Long> userInfoDao;
	private SimpleHibernateTemplate<User, Long> userDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<DepartmentUser, Long> departmentUserDao;
	private SimpleHibernateTemplate<WorkgroupUser, Long> workgroupUserDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<RoleFunction, Long> roleFunctionDao;
	private SimpleHibernateTemplate<RoleWorkgroup, Long> roleWorkgroupDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<SubsciberPricePolicy, Long> subsciberPricePolicyDao;
	private SimpleHibernateTemplate<ServerConfig, Long> serverConfigDao;
	private SimpleHibernateTemplate<BranchAuthority, Long> branchAuthorityDao;
	private SimpleHibernateTemplate<Log, Long> logDao;
	private SimpleHibernateTemplate<LoginLog, Long> loginLogDao;
	private SimpleHibernateTemplate<Department, Long> departmentDao;
	private SimpleHibernateTemplate<Workgroup, Long> workgroupDao;
	private SimpleHibernateTemplate<SecuritySetting, Long> securitySettingDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		tenantDao = new SimpleHibernateTemplate<Tenant, Long>(sessionFactory, Tenant.class);
		companyDao = new SimpleHibernateTemplate<Company, Long>(sessionFactory, Company.class);
		mailDeployDao = new SimpleHibernateTemplate<MailDeploy, Long>(sessionFactory, MailDeploy.class);
		subsciberDao = new SimpleHibernateTemplate<Subsciber, Long>(sessionFactory, Subsciber.class);
		userInfoDao = new SimpleHibernateTemplate<UserInfo, Long>(sessionFactory, UserInfo.class);
		userDao = new SimpleHibernateTemplate<User, Long>(sessionFactory, User.class);
		roleUserDao = new SimpleHibernateTemplate<RoleUser, Long>(sessionFactory, RoleUser.class);
		departmentUserDao = new SimpleHibernateTemplate<DepartmentUser, Long>(sessionFactory, DepartmentUser.class);
		workgroupUserDao = new SimpleHibernateTemplate<WorkgroupUser, Long>(sessionFactory, WorkgroupUser.class);
		roleDepartmentDao = new SimpleHibernateTemplate<RoleDepartment, Long>(sessionFactory, RoleDepartment.class);
		roleFunctionDao = new SimpleHibernateTemplate<RoleFunction, Long>(sessionFactory, RoleFunction.class);
		roleWorkgroupDao = new SimpleHibernateTemplate<RoleWorkgroup, Long>(sessionFactory, RoleWorkgroup.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		subsciberPricePolicyDao = new SimpleHibernateTemplate<SubsciberPricePolicy, Long>(sessionFactory, SubsciberPricePolicy.class);
		serverConfigDao = new SimpleHibernateTemplate<ServerConfig, Long>(sessionFactory, ServerConfig.class);
		branchAuthorityDao = new SimpleHibernateTemplate<BranchAuthority, Long>(sessionFactory, BranchAuthority.class);
		logDao = new SimpleHibernateTemplate<Log, Long>(sessionFactory, Log.class);
		loginLogDao = new SimpleHibernateTemplate<LoginLog, Long>(sessionFactory, LoginLog.class);
		departmentDao = new SimpleHibernateTemplate<Department, Long>(sessionFactory, Department.class);
		securitySettingDao = new SimpleHibernateTemplate<SecuritySetting, Long>(sessionFactory, SecuritySetting.class);
		workgroupDao = new SimpleHibernateTemplate<Workgroup, Long>(sessionFactory, Workgroup.class);
	}
	
	/*
	 * 新建订单
	 */
	@SuppressWarnings("unchecked")
	public void saveNewSubsciber(Tenant tenant, Subsciber subsciber, List<Long> priceIds){
	
		//保存订单
		subsciber.setTenantId(tenant.getId());
		subsciber.setOrderNo(String.valueOf(new Date().getTime()));
		subsciberDao.save(subsciber);
	
	
		String hql="from SubsciberPricePolicy spp where spp.subsciberId=?";
		List<SubsciberPricePolicy> s=subsciberPricePolicyDao.find(hql, subsciber.getId());
		if(s.size()>0){
		for(int i=0;i<s.size();i++){
			subsciberPricePolicyDao.delete(s.get(i));
		}
		}
		
		SubsciberPricePolicy spp = null;
			
		for(Long priceId : priceIds){
				spp = new SubsciberPricePolicy();
				spp.setPricePolicyId(priceId);
				spp.setSubsciberId(subsciber.getId());
				subsciberPricePolicyDao.save(spp);
				
			}
		
	
	}
	
	/*
	 * 创建用户
	 */
	private UserInfo createUser(Long compId,String scname, String loginName, String password, String trueName, Date pswdCreateDate){
		User user = new User();
		user.setSubCompanyName(scname);
		user.setCompanyId(compId);
		user.setLoginName(loginName);
//		user.setPassword(password);
		user.setPassword(Md5.toMessageDigest(password));
		user.setName(trueName);
		userDao.save(user);
		UserInfo ui = new UserInfo();
		ui.setUser(user);
		ui.setCompanyId(compId);
		ui.setPasswordUpdatedTime(pswdCreateDate);
		userInfoDao.save(ui);
		return ui;
	}
	
	public void saveTenant(Tenant tenant){
		tenantDao.save(tenant);
	}
	
	public void deleteTenant(Long id){
		Long cid=null;
		Tenant tenant = tenantDao.get(id);
		
		//获取公司id
		cid=tenant.getCompany().getId();
		
		//删除acs系统相关数据
		deleteAcsInfo(cid);
		//删除系统元数据相关信息
		deleteMmsInfo(cid);
		//删除工作流及任务相关信息
		deleteWfTaskInfo(cid);
		//删除基础设置相关信息
		deleteBsInfo(cid);
		//删除门户相关信息
		deletePortalInfo(cid);
		
		//删除租户Tenant
		tenantDao.delete(id);
		//删除Company
		companyDao.createQuery("delete from Company c where c.companyId=?", cid).executeUpdate();
	}
	
	private void deleteAcsInfo(Long companyId){
		//删除Condition
		ConditionDao conditionDao = (ConditionDao)ContextUtils.getBean("conditionDao");
		conditionDao.createQuery("delete from Condition c where c.companyId=?", companyId).executeUpdate();
		//删除DataRule
		DataRuleDao dataRuleDao = (DataRuleDao)ContextUtils.getBean("dataRuleDao");
		dataRuleDao.createQuery("delete from DataRule c where c.companyId=?", companyId).executeUpdate();
		//删除Permission
		PermissionDao permissionDao = (PermissionDao)ContextUtils.getBean("permissionDao");
		permissionDao.createQuery("delete from Permission c where c.companyId=?", companyId).executeUpdate();
		//删除PermissionItem
		PermissionItemDao permissionItemDao = (PermissionItemDao)ContextUtils.getBean("permissionItemDao");
		permissionItemDao.createQuery("delete from PermissionItem c where c.companyId=?", companyId).executeUpdate();
		//删除PermissionItemCondition
		PermissionItemConditionDao permissionItemConditionDao = (PermissionItemConditionDao)ContextUtils.getBean("permissionItemConditionDao");
		permissionItemConditionDao.createQuery("delete from PermissionItemCondition c where c.companyId=?", companyId).executeUpdate();
		
		
		//删除BranchAuthority
		branchAuthorityDao.createQuery("delete from BranchAuthority c where c.companyId=?", companyId).executeUpdate();
		//删除Log
		logDao.createQuery("delete from Log c where c.companyId=?", companyId).executeUpdate();
		//删除LoginLog
		loginLogDao.createQuery("delete from LoginLog c where c.companyId=?", companyId).executeUpdate();
		//删除部门和角色的中间表
		roleDepartmentDao.createQuery("delete from RoleDepartment rd where rd.role.id in (select r.id from Role r where r.companyId=?)",companyId).executeUpdate();
		//删除部门和用户的中间表
		departmentUserDao.createQuery("delete from DepartmentUser du where du.user.id in (select u.id from User u where u.companyId=?)",companyId).executeUpdate();
		//删除Department
		departmentDao.createQuery("delete from Department c where c.company.id=?", companyId).executeUpdate();
		//删除MailDeploy
		mailDeployDao.createQuery("delete from MailDeploy c where c.companyId=?", companyId).executeUpdate();
		//删除工作组和角色的中间表
		roleWorkgroupDao.createQuery("delete from RoleWorkgroup rw where rw.role.id in (select r.id from Role r where r.companyId=?)",companyId).executeUpdate();
		//删除工作组和用户的中间表
		workgroupUserDao.createQuery("delete from WorkgroupUser wu where wu.user.id in (select u.id from User u where u.companyId=?)",companyId).executeUpdate();
		//删除Workgroup
		workgroupDao.createQuery("delete from Workgroup c where c.company.id=?", companyId).executeUpdate();
		//删除角色和用户的中间表
		roleUserDao.createQuery("delete from RoleUser ru where ru.user.id in (select u.id from User u where u.companyId=?)",companyId).executeUpdate();
		
		//删除管理员用户User和UserInfo
		roleUserDao.createQuery("delete from UserInfo ui where ui.user.id in (select u.id from User u where u.companyId=?) and ui.companyId=?",companyId,companyId).executeUpdate();
		roleUserDao.createQuery("delete from User u where u.companyId=?",companyId).executeUpdate();
		//删除资源和角色的中间表
		roleFunctionDao.createQuery("delete from RoleFunction rf where rf.role.id in (select r.id from Role r where r.companyId=?)",companyId).executeUpdate();
		//删除角色
		roleWorkgroupDao.createQuery("delete from Role r where r.companyId=?",companyId).executeUpdate();
		//删除SecuritySetting
		securitySettingDao.createQuery("delete from SecuritySetting r where r.companyId=?",companyId).executeUpdate();
		//删除ServerConfig
		serverConfigDao.createQuery("delete from ServerConfig r where r.companyId=?",companyId).executeUpdate();
	}
	
	private void deleteMmsInfo(Long companyId){
		//删除菜单
		MenuDao menuDao = (MenuDao)ContextUtils.getBean("menuDao");
		menuDao.createQuery("delete from Menu c where c.companyId=?", companyId).executeUpdate();
		//删除通用类型
		OperationDao operationDao = (OperationDao)ContextUtils.getBean("operationDao");
		operationDao.createQuery("delete from Operation c where c.companyId=?", companyId).executeUpdate();
		//删除按钮
		ButtonDao buttonDao = (ButtonDao)ContextUtils.getBean("buttonDao");
		buttonDao.createQuery("delete from Button c where c.companyId=?", companyId).executeUpdate();
		//删除页面
		ModulePageDao modulePageDao = (ModulePageDao)ContextUtils.getBean("modulePageDao");
		modulePageDao.createQuery("delete from ModulePage c where c.companyId=?", companyId).executeUpdate();
		//删除FormAttachment
		FormAttachmentDao formAttachmentDao = (FormAttachmentDao)ContextUtils.getBean("formAttachmentDao");
		formAttachmentDao.createQuery("delete from FormAttachment c where c.companyId=?", companyId).executeUpdate();
		//删除GroupHeader
		GroupHeaderDao groupHeaderDao = (GroupHeaderDao)ContextUtils.getBean("groupHeaderDao");
		groupHeaderDao.createQuery("delete from GroupHeader c where c.companyId=?", companyId).executeUpdate();
		//删除JqGridProperty
		JqGridPropertyDao jqGridPropertyDao = (JqGridPropertyDao)ContextUtils.getBean("jqGridPropertyDao");
		jqGridPropertyDao.createQuery("delete from JqGridProperty c where c.companyId=?", companyId).executeUpdate();
		//删除ListColumn
		ListColumnDao listColumnDao = (ListColumnDao)ContextUtils.getBean("listColumnDao");
		listColumnDao.createQuery("delete from ListColumn c where c.companyId=?", companyId).executeUpdate();
		//删除TableColumn
		TableColumnDao tableColumnDao = (TableColumnDao)ContextUtils.getBean("tableColumnDao");
		tableColumnDao.createQuery("delete from TableColumn c where c.companyId=?", companyId).executeUpdate();
		//删除列表和表单
		FormViewDao formViewDao = (FormViewDao)ContextUtils.getBean("formViewDao");
		formViewDao.createQuery("delete from View c where c.companyId=?", companyId).executeUpdate();
		//删除GenerateSetting
		GenerateSettingDao generateSettingDao = (GenerateSettingDao)ContextUtils.getBean("generateSettingDao");
		generateSettingDao.createQuery("delete from GenerateSetting c where c.companyId=?", companyId).executeUpdate();
		//删除数据表
		DataTableDao dataTableDao = (DataTableDao)ContextUtils.getBean("dataTableDao");
		dataTableDao.createQuery("delete from Operation c where c.companyId=?", companyId).executeUpdate();
	}
	
	private void deleteWfTaskInfo(Long companyId){
		//删除DataDictionaryType
		DataDictionaryTypeDao dataDictionaryTypeDao = (DataDictionaryTypeDao)ContextUtils.getBean("dataDictionaryTypeDao");
		dataDictionaryTypeDao.createQuery("delete from DataDictionaryType c where c.companyId=?", companyId).executeUpdate();
		//删除DataDictionaryUser
		DataDictionaryUserDao dataDictionaryUserDao = (DataDictionaryUserDao)ContextUtils.getBean("dataDictionaryUserDao");
		dataDictionaryUserDao.createQuery("delete from DataDictionaryUser c where c.companyId=?", companyId).executeUpdate();
		//删除DataDictionaryProcess
		DataDictionaryProcessDao dataDictionaryProcessDao = (DataDictionaryProcessDao)ContextUtils.getBean("dataDictionaryProcessDao");
		dataDictionaryProcessDao.createQuery("delete from DataDictionaryProcess c where c.companyId=?", companyId).executeUpdate();
		//删除DataDictionary
		DataDictionaryDao dataDictionaryDao = (DataDictionaryDao)ContextUtils.getBean("dataDictionaryDao");
		dataDictionaryDao.createQuery("delete from DataDictionary c where c.companyId=?", companyId).executeUpdate();
		//删除Document
		OfficeDao officeDao = (OfficeDao)ContextUtils.getBean("officeDao");
		officeDao.createQuery("delete from Document c where c.companyId=?", companyId).executeUpdate();
		//删除DocumentFile
		DocumentFileDao documentFileDao = (DocumentFileDao)ContextUtils.getBean("documentFileDao");
		documentFileDao.createQuery("delete from DocumentFile c where c.companyId=?", companyId).executeUpdate();
		//删除DocumentTemplate
		DocumentTemplateDao documentTemplateDao = (DocumentTemplateDao)ContextUtils.getBean("documentTemplateDao");
		documentTemplateDao.createQuery("delete from DocumentTemplate c where c.companyId=?", companyId).executeUpdate();
		//删除DocumentTemplateFile
		DocumentTemplateFileDao documentTemplateFileDao = (DocumentTemplateFileDao)ContextUtils.getBean("documentTemplateFileDao");
		documentTemplateFileDao.createQuery("delete from DocumentTemplateFile c where c.companyId=?", companyId).executeUpdate();
		//删除HistoryInstanceHistory
		HistoryInstanceHistoryDao historyInstanceHistoryDao = (HistoryInstanceHistoryDao)ContextUtils.getBean("historyInstanceHistoryDao");
		historyInstanceHistoryDao.createQuery("delete from HistoryInstanceHistory c where c.companyId=?", companyId).executeUpdate();
		//删除HistoryOpinion
		HistoryOpinionDao historyOpinionDao = (HistoryOpinionDao)ContextUtils.getBean("historyOpinionDao");
		historyOpinionDao.createQuery("delete from HistoryOpinion c where c.companyId=?", companyId).executeUpdate();
		//删除HistoryWorkflowInstance
		HistoryWorkflowInstanceDao historyWorkflowInstanceDao = (HistoryWorkflowInstanceDao)ContextUtils.getBean("historyWorkflowInstanceDao");
		historyWorkflowInstanceDao.createQuery("delete from HistoryWorkflowInstance c where c.companyId=?", companyId).executeUpdate();
		//删除InstanceHistory
		InstanceHistoryDao instanceHistoryDao = (InstanceHistoryDao)ContextUtils.getBean("instanceHistoryDao");
		instanceHistoryDao.createQuery("delete from InstanceHistory c where c.companyId=?", companyId).executeUpdate();
		//删除Opinion
		OpinionDao opinionDao = (OpinionDao)ContextUtils.getBean("opinionDao");
		opinionDao.createQuery("delete from Opinion c where c.companyId=?", companyId).executeUpdate();
		//删除TrustRecord
		DelegateMainDao trustRecordDao = (DelegateMainDao)ContextUtils.getBean("delegateMainDao");
		trustRecordDao.createQuery("delete from TrustRecord c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowAttachment
		WorkflowAttachmentDao workflowAttachmentDao = (WorkflowAttachmentDao)ContextUtils.getBean("workflowAttachmentDao");
		workflowAttachmentDao.createQuery("delete from WorkflowAttachment c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowAttachmentFile
		WorkflowAttachmentFileDao workflowAttachmentFileDao = (WorkflowAttachmentFileDao)ContextUtils.getBean("workflowAttachmentFileDao");
		workflowAttachmentFileDao.createQuery("delete from WorkflowAttachmentFile c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowDefinition
		WorkflowDefinitionDao workflowDefinitionDao = (WorkflowDefinitionDao)ContextUtils.getBean("workflowDefinitionDao");
		workflowDefinitionDao.createQuery("delete from WorkflowDefinition c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowDefinitionFile
		WorkflowDefinitionFileDao workflowDefinitionFileDao = (WorkflowDefinitionFileDao)ContextUtils.getBean("workflowDefinitionFileDao");
		workflowDefinitionFileDao.createQuery("delete from WorkflowDefinitionFile c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowDefinitionTemplate
		WorkflowDefinitionTemplateDao workflowDefinitionTemplateDao = (WorkflowDefinitionTemplateDao)ContextUtils.getBean("workflowDefinitionTemplateDao");
		workflowDefinitionTemplateDao.createQuery("delete from WorkflowDefinitionTemplate c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowDefinitionTemplateFile
		WorkflowDefinitionTemplateFileDao workflowDefinitionTemplateFileDao = (WorkflowDefinitionTemplateFileDao)ContextUtils.getBean("workflowDefinitionTemplateFileDao");
		workflowDefinitionTemplateFileDao.createQuery("delete from WorkflowDefinitionTemplateFile c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowInstance
		WorkflowInstanceDao workflowInstanceDao = (WorkflowInstanceDao)ContextUtils.getBean("workflowInstanceDao");
		workflowInstanceDao.createQuery("delete from WorkflowInstance c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowType
		WorkflowTypeDao workflowTypeDao = (WorkflowTypeDao)ContextUtils.getBean("workflowTypeDao");
		workflowTypeDao.createQuery("delete from WorkflowType c where c.companyId=?", companyId).executeUpdate();
		
		//删除HistoryWorkflowTask
		HistoryWorkflowTaskDao historyWorkflowTaskDao = (HistoryWorkflowTaskDao)ContextUtils.getBean("historyWorkflowTaskDao");
		historyWorkflowTaskDao.createQuery("delete from HistoryWorkflowTask c where c.companyId=?", companyId).executeUpdate();
		//删除WorkflowTask
		WorkflowTaskDao workflowTaskDao = (WorkflowTaskDao)ContextUtils.getBean("workflowTaskDao");
		workflowTaskDao.createQuery("delete from WorkflowTask c where c.companyId=?", companyId).executeUpdate();
		//删除Task
		TaskDao taskDao = (TaskDao)ContextUtils.getBean("taskDao");
		taskDao.createQuery("delete from Task c where c.companyId=?", companyId).executeUpdate();
	}
	
	private void deleteBsInfo(Long companyId){
		//删除Holiday
		HolidayDao holidayDao = (HolidayDao)ContextUtils.getBean("holidayDao");
		holidayDao.createQuery("delete from Holiday c where c.companyId=?", companyId).executeUpdate();
		//删除ImportColumn
		ImportColumnDao importColumnDao = (ImportColumnDao)ContextUtils.getBean("importColumnDao");
		importColumnDao.createQuery("delete from ImportColumn c where c.companyId=?", companyId).executeUpdate();
		//删除ImportDefinition
		ImportDefinitionDao importDefinitionDao = (ImportDefinitionDao)ContextUtils.getBean("importDefinitionDao");
		importDefinitionDao.createQuery("delete from ImportDefinition c where c.companyId=?", companyId).executeUpdate();
		//删除InternationOption
		InternationOptionDao internationOptionDao = (InternationOptionDao)ContextUtils.getBean("internationOptionDao");
		internationOptionDao.createQuery("delete from InternationOption c where c.companyId=?", companyId).executeUpdate();
		//删除Internation
		InternationDao internationDao = (InternationDao)ContextUtils.getBean("internationDao");
		internationDao.createQuery("delete from Internation c where c.companyId=?", companyId).executeUpdate();
		//删除Option
		OptionDao optionDao = (OptionDao)ContextUtils.getBean("optionDao");
		optionDao.createQuery("delete from Option c where c.companyId=?", companyId).executeUpdate();
		//删除OptionGroup
		OptionGroupDao optionGroupDao = (OptionGroupDao)ContextUtils.getBean("optionGroupDao");
		optionGroupDao.createQuery("delete from OptionGroup c where c.companyId=?", companyId).executeUpdate();
		//删除TimedTask
		JobInfoDao jobInfoDao = (JobInfoDao)ContextUtils.getBean("jobInfoDao");
		jobInfoDao.createQuery("delete from TimedTask c where c.companyId=?", companyId).executeUpdate();
		//删除Timer
		CornInfoDao cornInfoDao = (CornInfoDao)ContextUtils.getBean("cornInfoDao");
		cornInfoDao.createQuery("delete from Timer c where c.companyId=?", companyId).executeUpdate();
		//删除Subordinate
		RankUserDao rankUserDao = (RankUserDao)ContextUtils.getBean("rankUserDao");
		rankUserDao.createQuery("delete from Subordinate c where c.companyId=?", companyId).executeUpdate();
		//删除Superior
		RankDao rankDao = (RankDao)ContextUtils.getBean("rankDao");
		rankDao.createQuery("delete from Superior c where c.companyId=?", companyId).executeUpdate();
		//删除Signature
		SignatureDao signatureDao = (SignatureDao)ContextUtils.getBean("signatureDao");
		signatureDao.createQuery("delete from Signature c where c.companyId=?", companyId).executeUpdate();
	}
	
	private void deletePortalInfo(Long companyId){
		//删除BaseSetting
		BaseSettingDao baseSettingDao = (BaseSettingDao)ContextUtils.getBean("baseSettingDao");
		baseSettingDao.createQuery("delete from BaseSetting c where c.companyId=?", companyId).executeUpdate();
		//删除Message
		MessageInfoDao messageDao = (MessageInfoDao)ContextUtils.getBean("messageInfoDao");
		messageDao.createQuery("delete from Message c where c.companyId=?", companyId).executeUpdate();
		//删除Theme
		ThemeDao themeDao = (ThemeDao)ContextUtils.getBean("themeDao");
		themeDao.createQuery("delete from Theme c where c.companyId=?", companyId).executeUpdate();
		//删除UserTheme
		UserThemeDao userThemeDao = (UserThemeDao)ContextUtils.getBean("userThemeDao");
		userThemeDao.createQuery("delete from UserTheme c where c.companyId=?", companyId).executeUpdate();
		//删除Webpage
		WebpageDao webpageDao = (WebpageDao)ContextUtils.getBean("webpageDao");
		webpageDao.createQuery("delete from Webpage c where c.companyId=?", companyId).executeUpdate();
		//删除Widget
		WidgetDao widgetDao = (WidgetDao)ContextUtils.getBean("widgetDao");
		widgetDao.createQuery("delete from Widget c where c.companyId=?", companyId).executeUpdate();
		//删除WidgetConfig
		WidgetConfigDao widgetConfigDao = (WidgetConfigDao)ContextUtils.getBean("widgetConfigDao");
		widgetConfigDao.createQuery("delete from WidgetConfig c where c.companyId=?", companyId).executeUpdate();
		//删除WidgetParameter
		WidgetParameterDao widgetParameterDao = (WidgetParameterDao)ContextUtils.getBean("widgetParameterDao");
		widgetParameterDao.createQuery("delete from WidgetParameter c where c.companyId=?", companyId).executeUpdate();
		//删除WidgetParameterValue
		WidgetParameterValueDao widgetParameterValueDao = (WidgetParameterValueDao)ContextUtils.getBean("widgetParameterValueDao");
		widgetParameterValueDao.createQuery("delete from WidgetParameterValue c where c.companyId=?", companyId).executeUpdate();
		//删除WidgetRole
		WidgetRoleDao widgetRoleDao = (WidgetRoleDao)ContextUtils.getBean("widgetRoleDao");
		widgetRoleDao.createQuery("delete from WidgetRole c where c.companyId=?", companyId).executeUpdate();
		
	}
	
	public Tenant getTenant(Long id){
		return tenantDao.get(id);
	}
	
	public Page<Tenant> getAllTenants(Page<Tenant> page){
		return tenantDao.findByCriteria(page, Restrictions.eq("deleted", false));
	}
	
	public List<Tenant> getAllTenants(){
		return tenantDao.findByCriteria(Restrictions.eq("deleted", false));
	}
	
	@Autowired
	private SubscriberItemManager subscriberItemManager;
	
	@SuppressWarnings("unchecked")
	public void saveSubsciberItem(Tenant tenant, Subsciber subsciber, List<SubscriberItem> items){
		//租户
		tenantDao.save(tenant);
		tenant.getCompany().setCompanyId(tenant.getId());
		//订单
		subsciber.setOrderDate(new Date());
		subsciber.setTenantId(tenant.getId());
		subsciber.setOrderNo(String.valueOf(new Date().getTime()));
		subsciberDao.save(subsciber);
		// ServerConfig
		ServerConfig config = new ServerConfig();
		config.setCompanyId(tenant.getId());
		serverConfigDao.save(config);
		//创建三个个公司的管理员，拥有该公司所购买的所有的系统的管理员角色
		//系统管理员
		UserInfo systemAdmin = createUser(tenant.getCompany().getId(),tenant.getCompany().getName(),
				(new StringBuffer(tenant.getCompany().getCode().trim()).append(".")).append(systemAdminCode).toString(),
				systemAdminCode, systemAdminCode, subsciber.getBeginDate());
		
		//安全管理员
		UserInfo securityAdmin = createUser(tenant.getCompany().getId(),tenant.getCompany().getName(),
				(new StringBuffer(tenant.getCompany().getCode().trim()).append(".")).append(securityAdminCode).toString(),
				securityAdminCode, securityAdminCode, subsciber.getBeginDate());
		
		//审计管理员
		UserInfo auditAdmin = createUser(tenant.getCompany().getId(),tenant.getCompany().getName(),
				(new StringBuffer(tenant.getCompany().getCode().trim()).append(".")).append(auditAdminCode).toString(),
				auditAdminCode, auditAdminCode, subsciber.getBeginDate());
		
		StringBuilder hql = new StringBuilder();
		hql.append("select sr from Role sr,Product p ");
		hql.append("where sr.businessSystem.id=p.systemId and sr.deleted=false and sr.businessSystem.deleted=false and p.id=?");

		for(SubscriberItem item : items){
			item.setSubsciber(subsciber);
			subscriberItemManager.saveItem(item);
			
			//给三个管理员分配各自的角色
			RoleUser roleUser = null;
			List<Role> roles = roleDao.find(hql.toString(), item.getProduct().getId());
			for(Role role : roles){
				if(role.getCode().endsWith(PORTAL_COMMON_USER)){
					roleUser = new RoleUser();
					roleUser.setCompanyId(tenant.getId());
					roleUser.setRole(role);
					roleUser.setUser(systemAdmin.getUser());
					roleUserDao.save(roleUser);
					roleUser = new RoleUser();
					roleUser.setCompanyId(tenant.getId());
					roleUser.setRole(role);
					roleUser.setUser(securityAdmin.getUser());
					roleUserDao.save(roleUser);
					roleUser = new RoleUser();
					roleUser.setCompanyId(tenant.getId());
					roleUser.setRole(role);
					roleUser.setUser(auditAdmin.getUser());
					roleUserDao.save(roleUser);
				}
				roleUser = new RoleUser();
				roleUser.setCompanyId(tenant.getId());
				roleUser.setRole(role);
				if(role.getCode().endsWith(systemAdminRoleCode)||
						role.getCode().endsWith(FORM_MANAGER)||
						role.getCode().endsWith(TYPE_MANAGER)||
						role.getCode().endsWith(OPTIONS_MANAGER)||
						role.getCode().endsWith(MODUEL_MANAGER)||
						role.getCode().endsWith(MMS_COMMON_ROLE)||
						role.getCode().endsWith(WF_COMMONS)||
						role.getCode().endsWith(BS_COMMON)||
						role.getCode().endsWith(WORKFLOW_MANAGER)){
					roleUser.setUser(systemAdmin.getUser());
					roleUserDao.save(roleUser);
				}else if(role.getCode().endsWith(securityAdminRoleCode)){
					roleUser.setUser(securityAdmin.getUser());
					roleUserDao.save(roleUser);
				}else if(role.getCode().endsWith(auditAdminRoleCode)){
					roleUser.setUser(auditAdmin.getUser());
					roleUserDao.save(roleUser);
				}
			}
		}
	}
	//给所有用户密码MD5加密
	
	@SuppressWarnings("unchecked")
	public void encryotionByMD5(Long companyId) {
		List<User> userList = userDao.find("select u FROM User u  WHERE u.companyId=? ", companyId);
		for(User user : userList){
			if(user.getPassword().length()<32){
				user.setPassword(Md5.toMessageDigest(user.getPassword()));
				userDao.save(user);
			}
		}
	}

	/**
	 * 保存邮件配置
	 * @param mailDeploy
	 */
	public void saveMailDeploy(MailDeploy mailDeploy) {
		mailDeployDao.save(mailDeploy);
	}

	/**
	 * 根据公司id获得邮件配置
	 * @param companyId
	 * @return
	 */
	public MailDeploy getMailDeployByCompanyId(Long companyId) {
		return mailDeployDao.findUniqueByProperty("companyId", companyId);
	}
}
