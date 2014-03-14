package com.norteksoft.wf.base.enumeration;

public interface CommonStrings {
	public static final String NEED_GENERATE_TASK = "need_generate_task";//提示办理人需要生成任务
	
	public static final String PARENT_INSTANCE_ID = "parent_instance_id";//提示办理人需要生成任务
	
	public static final String TRANSACTOR_ASSIGNMENT = "_assignment_transactor";
	public static final String TRANSACTOR_SINGLE = "_assignment_transactor_single";
	
	public static final String TRANSACTOR_SINGLE_CANDIDATES = "_transactor_single_candidates";//选择具体办理人
	
	public static final String TRANSITION_NAME = "_transition_name";
	public static final String NEW_TRANSACTOR = "_new_transactor";
	public static final String NEW_TRANSACTOR_ID = "_new_transactor_id";
//	public static final String CURRENTUSERLONGINNAME = "${currentUserLonginName}";//当前用户登录名
//	public static final String CURRENTUSERNAME = "${currentUserName}";//当前用户的用户名
	
	public static final String CURRENTTRANSACTOR = "${currentTransactor}";//本环节办理人登录名
	public static final String CURRENTTRANSACTORID = "${currentTransactorId}";//本环节办理人id
	public static final String CURRENTTIME ="${currentTime}";//本环节办理时间
	public static final String CURRENTOPERATION = "${currentOperation}";//本环节办理人执行的操作

	public static final String CURRENT_OPERATTION_STRING = "_previous_task_operation"; //上一环节执行的操作
	public static final String PREVIOUS_TASK_TRANSACTOR = "_previous_task_transactor";  //上一环节任务的办理人
	public static final String PREVIOUS_TASK_TRANSACTOR_ID = "_previous_task_transactor_id";  //上一环节任务的办理人id
	public static final String PREVIOUS_TASK_PRINCI_TRANSACTOR="_previous_task_princi_transactor";//上一环节任务的委托人
	public static final String PREVIOUS_TASK_PRINCI_TRANSACTOR_ID="_previous_task_princi_transactor_id";//上一环节任务的委托人id
	public static final String PREVIOUS_TASK_NAME = "_previous_task_name";  //上一环节任务名称
	public static final String COLLECTIVE_OPERATION = "task_collective_operation";
	
	public static final String ASSIGN_TO = "assign to:";
	
	public static final String PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT = "${previousTransactorUpstageDepartment}";//上一环节办理人的顶级部门
	public static final String PREVIOUS_TRANSACTOR_UPSTAGE_DEPARTMENT_ID = "${previousTransactorUpstageDepartmentId}";//上一环节办理人的顶级部门id
	public static final String PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT ="${previousTransactorSuperiorDepartment}";//上一环节办理人的上级部门
	public static final String PREVIOUS_TRANSACTOR_SUPERIOR_DEPARTMENT_ID ="${previousTransactorSuperiorDepartmentId}";//上一环节办理人的上级部门id
	public static final String DOCUMENT_CREATOR_UPSTAGE_DEPARTMENT = "${documentCreatorUpstageDepartment}";//文档创建人的顶级部门
	public static final String DOCUMENT_SUPERIOR_DEPARTMENT = "${superiorDepartment}";//文档创建人的上级部门
	public static final String UPSTAGE_DEPARTMENT = "${upstageDepartment}";//顶级部门
	public static final String CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT = "${currentTransactorUpstageDepartment}";//当前办理人的顶级部门
	public static final String CURRENT_TRANSACTOR_UPSTAGE_DEPARTMENT_ID = "${currentTransactorUpstageDepartmentId}";//当前办理人的顶级部门id
	public static final String CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT = "${currentTransactorSuperiorDepartment}";//当前办理人的上级部门
	public static final String CURRENT_TRANSACTOR_SUPERIOR_DEPARTMENT_ID = "${currentTransactorSuperiorDepartmentId}";//当前办理人的上级部门id
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_NAME="${currentTransactorDirectSuperiorName}";//当前办理人直属上级名称
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_LOGIN_NAME="${currentTransactorDirectSuperior}";//当前办理人直属上级登录名（环节自动填写有用）
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ID="${currentTransactorDirectSuperiorId}";//当前办理人直属上级id（环节自动填写有用）
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT="${currentTransactorDirectSuperiorDepartment}";//当前办理人直属上级部门
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT_ID="${currentTransactorDirectSuperiorDepartmentId}";//当前办理人直属上级部门id
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_MAIN_DEPARTMENT="${currentTransactorDirectSuperiorMainDepartment}";//当前办理人直属上级正职部门(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_MAIN_DEPARTMENT_ID="${currentTransactorDirectSuperiorMainDepartmentId}";//当前办理人直属上级正职部门id(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE="${currentTransactorDirectSuperiorRole}";//当前办理人直属上级角色
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_ROLE_ID="${currentTransactorDirectSuperiorRoleId}";//当前办理人直属上级角色id
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP="${currentTransactorDirectSuperiorWorkGroup}";//当前办理人直属上级工作组
	public static final String CURRENT_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP_ID="${currentTransactorDirectSuperiorWorkGroupId}";//当前办理人直属上级工作组id
	public static final String CURRENT_TRANSACTOR_NAME = "${currentTransactorName}"; //本环节办理人姓名
	public static final String CURRENT_TRANSACTOR_ROLE = "${currentTransactorRole}";//本环节办理人角色
	public static final String CURRENT_TRANSACTOR_ROLE_ID = "${currentTransactorRoleId}";//本环节办理人角色id
	public static final String CURRENT_TRANSACTOR_DEPARTMENT = "${currentTransactorDepartment}";//本环节办理人部门
	public static final String CURRENT_TRANSACTOR_DEPARTMENT_ID = "${currentTransactorDepartmentId}";//本环节办理人部门id
	public static final String CURRENT_TRANSACTOR_MAIN_DEPARTMENT = "${currentTransactorMainDepartment}";//本环节办理人正职部门(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_MAIN_DEPARTMENT_ID = "${currentTransactorMainDepartmentId}";//本环节办理人正职部门id(环节自动填写有用)
	public static final String CURRENT_TRANSACTOR_WORKGROUP = "${currentTransactorWorkGroup}";//本环节办理人工作组
	public static final String CURRENT_TRANSACTOR_WORKGROUP_ID = "${currentTransactorWorkGroupId}";//本环节办理人工作组id
	
	public static final String DOCUMENT_CREATOR_NAME = "${documentCreatorName}"; //文档创建人姓名
	public static final String DOCUMENT_CREATOR_ROLE = "${documentCreatorRole}";//文档创建人角色
	public static final String DOCUMENT_CREATOR_DEPARTMENT = "${documentCreatorDepartment}";//文档创建人部门
	public static final String DOCUMENT_CREATOR_WORKGROUP = "${documentCreatorWorkGroup}";//文档创建人工作组	
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_NAME="${documentCreatorDirectSuperiorName}";//创建人直属上级名称
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_DEPARTMENT="${documentCreatorDirectSuperiorDepartment}";//创建人直属上级部门
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_ROLE="${documentCreatorDirectSuperiorRole}";//创建人直属上级角色
	public static final String DOCUMENT_CREATOR_DIRECT_SUPERIOR_WORKGROUP="${documentCreatorDirectSuperiorWorkGroup}";//创建人直属上级工作组
	
	public static final String PREVIOUS_TRANSACTOR = "${previousTransactor}";//上一环节办理人登录名
	public static final String PREVIOUS_TRANSACTOR_ID = "${previousTransactorId}";//上一环节办理人id
	public static final String PREVIOUS_TRANSACTOR_NAME = "${previousTransactorName}";//上一环节办理人姓名
	public static final String PREVIOUS_TRANSACTOR_ROLE = "${previousTransactorRole}";//上一环节办理人角色
	public static final String PREVIOUS_TRANSACTOR_DEPARTMENT = "${previousTransactorDepartment}"; // 上一环节办理人部门
	public static final String PREVIOUS_TRANSACTOR_WORKGROUP = "${previousTransactorWorkGroup}";//上一环节办理人工作组	
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_NAME="${previousTransactorDirectSuperiorName}";//上一环节办理人直属上级名称
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_DEPARTMENT="${previousTransactorDirectSuperiorDepartment}";//上一环节办理人直属上级部门
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_ROLE="${previousTransactorDirectSuperiorRole}";//上一环节办理人直属上级角色
	public static final String PREVIOUS_TRANSACTOR_DIRECT_SUPERIOR_WORKGROUP="${previousTransactorDirectSuperiorWorkGroup}";//上一环节办理人直属上级工作组
	
	public static final String FAVOR_COUNT = "${favorCount}";//赞成票总数
	public static final String AGAINST_COUNT = "${againstCount}";//反对票总数
	public static final String ABSTENTION_COUNT = "${abstentionCount}" ;//弃权票总数
	public static final String COUNTERSIGNATURE_AGREE_PERCENTAGE = "${countersignatureAgreePercentage}";//会签同意人员百分比
	public static final String COUNTERSIGNATURE_DISAGREE_PERCENTAGE = "${countersignatureDisagreePercentage}";//会签不同意人员百分比
	public static final String FAVOR_PERCENTAGE = "${favorPercentage}";//赞成票百分比
	public static final String AGAINST_PERCENTAGE = "${againstPercentage}";//反对票百分比
	public static final String ABSTENTION_PERCENTAGE = "${abstentionPercentage}";//弃权票百分比
	public static final String COUNTERSIGNATURE_AGREE_COUNT = "${countersignatureAgreeCount}";//会签同意人员总数
	public static final String COUNTERSIGNATURE_DISAGREE_COUNT = "${countersignatureDisagreeCount}";//会签不同意人员总数
	public static final String APPROVAL_RESULT = "${approvalResult}" ;//审批结果
	
	public static final String DOCUMENT_CREATOR = "${documentCreator}"; //文档创建人
	public static final String PROCESS_ADMIN = "${processAdmin}";        //流程管理员
	public static final String PARTICIPANTS_TRANSACTOR = "${hasHandledTransactor}"; //参与办理人
	public static final String PARTICIPANTS_ALL_TRANSACTOR = "${allHandleTransactors}"; //参与办理人
	
	public static final String TASK_URL_PREFIX = "url_";
	
	public static final String DEFAULT_ENTITY_NAME = "wf_value_prefix";
	
	public static final String ALL_USER = "all_user";
	public static final String ALL_DEPARTMENT = "all_department";
	public static final String ALL_WORKGROUP = "all_workGroup";
	
	public static final String MINISTER="012";//部长角色编号
	
	
	public static final String SYS_VAR_USER = "${user}";
	public static final String SYS_VAR_ROLE = "${role}";
	public static final String SYS_VAR_DEPARTMENT = "${department}";
	public static final String SYS_VAR_WORKGROUP = "${workGroup}";
	
	public static final String RTX_STYLE = "rtx";//RTX方式
	public static final String EMAIL_STYLE = "mail";//email方式
	public static final String SMS_STYLE = "message";//短信方式
	public static final String SWING_STYLE = "swing";//办公助手方式
	
	public static final String DATA_SOURCE="data_sourc";//主表单对应的子表单
	public static final String DATA_SOURCE_FIELD="data_sourc_field";//主表单对应的子表单中的字段名称
	public static final String DATA_SOURCE_FIELD_VALUE="data_sourc_field_value";//主表单对应的子表单中的字段名称/值
	public static final String IS_ORIGINAL_USER="is_original_user";//流向中设置使用目标任务的原办理人登录名
	public static final String ALL_ORIGINAL_USERS="all_original_users";//上次目标任务的所有办理人的登录名或id，历史数据中为登录名
	
	/**
	 * 工作流引擎使用的变量key
	 * 文档创建人的后续者
	 */
	public static final String CREATOR_CANDIDATES = "creatorCandidates"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 文档创建人
	 */
	public static final String CREATOR = "creator"; 
	/**
	 * 工作流引擎使用的变量key
	 * 文档创建人id
	 */
	public static final String CREATOR_ID = "creatorID"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 父流程的workflowId
	 */
	public static final String PARENT_WORKFLOW_ID = "parentWorkflowId"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 父流程发起子流程的环节名
	 */
	public static final String PARENT_TACHE_NAME = "parentTacheName"; 
	
	/**
	 * 工作流引擎使用的变量key
	 * 流程紧急程度标志
	 */
	public static final String PRIORITY = "priority";
	
	/**
	 * 流程实例id
	 */
	public static final String PROCESS_ID = "processId";

	/**
	 * 父流程的executionId
	 */
	public static final String PARENT_EXECUTION_ID = "parentExecutionId";
	/**
	 * 表单数据id
	 */
	public static final String FORM_DATA_ID="formDataId";
	
	public static final String SUBPROCESS_TASK_ID="subprocess_task_id";
	
	public static final String SUBPROCESS_PARSE="subprocess_parse";
	public static final String COMPANY_ID="companyId";
	public static final String SYSTEM_ID="systemId";
	/**
	 * 流程取消标识:true:取消,false：非取消
	 */
	public static final String CANCEL_FLAG = "cancel";
	/**
	 * 流程强制结束标识:true:强制结束,false：非强制结束
	 */
	public static final String COMPEL_END_FLAG = "compelEnd";
	
	public static final String WORKFLOW_PARAMETER_URL="workflowParameterUrl.properties";
	public static final String PROCESS_TASK_URL="process.task.url";
	public static final String PROCESS_TASK_PARAMTER_NAME="process.task.paramter.name";
	public static final String FORM_VIEW_URL="form.view.url";
	public static final String FORM_VIEW_PARAMTER_NAME="form.view.paramter.name";
	public static final String FORM_URGEN_URL="form.urgen.url";
	public static final String FORM_URGEN_PARAMTER_NAME="form.urgen.paramter.name";
	public static final String TASK_JUMP_FIELD_NO_VALUE_FLAG="task_jump_field_no_value_flag";//环节跳转时，办理人设置为“字段中人员指定”，该字段值为空时的标识或 条件筛选没有获得相应的办理人时的标识
	public static final String TASK_JUMP_FIELD_VALUE_FLAG="task_jump_field_value_flag";//环节跳转时，办理人设置为“字段中人员指定”，该字段值不为空时的标识或 条件筛选没有获得相应的办理人时的标识
	public static final String TASK_JUMP_SELECT_USER="task_jump_select_user";//环节跳转时，环节中无办理人时，需要用户选择（如：字段中指定人员时，该字段的值为空； 条件筛选没有获得相应的办理人），该jbpm变量存储选择的用户的登录名集合，以逗号隔开
	public static final String RETRIEVE_TASK_USER="retrieve_task_user";//取回任务时，环节中无办理人时（如：字段中指定人员时，该字段的值为空； 条件筛选没有获得相应的办理人），该jbpm变量存储当前任务的办理人的登录名
	public static final String RETRIEVE_TASK_USER_ID="retrieve_task_user_id";//取回任务时，环节中无办理人时（如：字段中指定人员时，该字段的值为空； 条件筛选没有获得相应的办理人），该jbpm变量存储当前任务的办理人的登录名
	
	public static final String LOGINNAME_COMPANY_SPLIT = "~~";//登录名和公司编码之间的分隔符
	public static final String NUMBER_REG="^-?\\d+$";//表示数字正则表达式
	public static final String SUB_PROCESS_CREATOR_TRUSTOR="-:-";//表示子流程第一环节被委托出去后，创建人和受托人之间的分隔符
}
