package com.norteksoft.acs.base.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;

import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.acs.base.enumeration.ConditionType;
import com.norteksoft.acs.base.enumeration.DataRange;
import com.norteksoft.acs.base.enumeration.FieldOperator;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.acs.base.enumeration.UserOperator;
import com.norteksoft.acs.base.utils.permission.PermissionAnalysisFactory;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.ConditionVlaueInfo;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.DataRuleConditionValueSetting;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.advanced.DataRuleConditionValueType;
import com.norteksoft.acs.base.utils.permission.impl.dataRule.simpleness.DataRangeSetting;
import com.norteksoft.acs.entity.authority.DataRuleResult;
import com.norteksoft.acs.dao.authority.PermissionItemConditionDao;

import com.norteksoft.acs.dao.authority.PermissionValidator.ConditionResult;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.acs.service.authority.PermissionItemConditionManager;
import com.norteksoft.acs.service.authority.PermissionItemManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.bs.rank.service.RankManager;
import com.norteksoft.mms.base.utils.CompareUtils;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.BeanUtils;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.base.enumeration.TextOperator;
import com.norteksoft.wf.base.utils.BeanShellUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 根据 PermissionItem 组合,判断当前用户是否满足条件
 * @author xiao
 * 2012-11-2
 */
public class PermissionUtils {
	protected static SessionFactory sessionFactory;
	protected static Log logger = LogFactory.getLog(PermissionUtils.class);
	public static final String PERMISSION_HQL = "permission_hql";
	public static final String PERMISSION_PARAMETERS = "permission_parameters";
	public static final String NO_PERMISSION = "no_permission";
	private static final String NO_VALUE_STRING= "NO_VALUE_#~_+~=%";
	
	public static final String STANDARD_FIELD_CREATOR="~~creatorId";//数据分类中标准值字段创建人
	public static final String STANDARD_FIELD_DEPARMENT="~~departmentId";//数据分类中标准值字段创建人部门
	public static final String STANDARD_FIELD_ROLE="~~roleId";//数据分类中标准值字段创建人角色
	public static final String STANDARD_FIELD_WORKGROUP="~~workgroupId";//数据分类中标准值字段创建人工作组
	
	public static final String NO_DEPARTMENT="____NO_DEPARTMENT";//当不存在上级部门或下级部门时，分类返回的值
		
	public static Object getLeftBracket(LeftBracket leftBracket) {
		if(leftBracket==null){
			return "";
		}
		switch (leftBracket) {
		case LEFTDOUBLE:
			return " ((";
		case LEFTSINGLE:
			return " (";
		}
		return "";
	}
	public static Object getRightBracket(RightBracket rightBracket) {
		if(rightBracket==null){
			return "";
		}
		switch (rightBracket) {
		case RIGHTDOUBLE:
			return ")) ";
		case RIGHTSINGLE:
			return ") ";
		}
		return "";
	}
	
	public static boolean permissionDecision(UserOperator operator, List<Long> src, Long value){
		switch (operator) {
		case ET:
			if(src.contains(value)) return true;
			break;
		case NET: 
			if(!src.contains(value)) return true;
			break;
		}
		return false;
	}
	
	public static String joinType(LogicOperator type){
		if(type==null){
			return "";
		}
		switch (type) {
		case AND:
			return " &&";
		case OR:
			return " ||";
		}
		return "";
	}
	
	
	/**
	 * 实体是否满足数据规则
	 * @param entity
	 * @param link 
	 * @param rule
	 */
	public static boolean entityPermission(Object entity, PermissionInfo permissionInfo, LogicOperator link){
		StringBuilder sb = new StringBuilder();
		Object obj = null;
		Condition con =null;
		try {
			List<Long> deptIds = permissionInfo.getDepartmentIds();
			
			List<DataRuleResult> dataRules = permissionInfo.getRules();
			DataTableDao dataTableDao = (DataTableDao)ContextUtils.getBean("dataTableDao");
			for (int n =0;n<dataRules.size();n++) {
				DataRuleResult dataRuleResult = dataRules.get(n);
				DataRule dataRule = dataRuleResult.getDataRule();
				DataTable table = dataTableDao.get(dataRule.getDataTableId());
				
				List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
				//处理用户兼职的情况，数据授权选择人员、部门时部门已经确定，且数据分类规则为本部门、当前用户部门、当前用户上级部门、当前用户顶级部门、当前用户所在部门的子部门、当前用户所在部门的子部门（含继承）需要用授权中设置的部门来确定
				Permission permission = dataRuleResult.getPermission();
				if(permission!=null){
					PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
					permissionItems = permissionItemManager.getPermissionItemsByPermission(permission.getId());
				}
				if(entity instanceof HibernateProxy){
					HibernateProxy proxy = (HibernateProxy)entity;
					entity = proxy.getHibernateLazyInitializer().getImplementation();
				}
				String field = "";
				if(dataRule.getSimplable()){//简易设置
					StringBuilder conHql = new StringBuilder();
					field = "";
					if(dataRule.getDataRange()==DataRange.CURRENT_DEPARTMENT){//本部门
						//获得创建人字段名
						field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".department");//实体名.department
						if(StringUtils.isEmpty(field))field = "departmentId";
					}else if(dataRule.getDataRange()==DataRange.MYSELF){//本人
						//获得创建人字段名
						field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".creator");//实体名.creator
						if(StringUtils.isEmpty(field))field = "creatorId";
					}else if(dataRule.getDataRange()==DataRange.ALL){//全公司
						//获得创建人字段名
						field = "companyId";
					}
					//解析简易设置并获得表达式
					//获得条件值
					if(deptIds.size()<=0&&dataRule.getDataRange()==DataRange.CURRENT_DEPARTMENT){//如果当前用户是无部门人员且数据范围为本部门数据时,无部门人员可以看到当前分支机构内所有部门id为null的数据
						analysisSimpleDepartmentValueExpress(conHql,permissionInfo,entity,field);
					}else{
						analysisSimpleExpress(dataRule,permissionItems,permissionInfo,conHql,entity,field);
					}
					if(StringUtils.isNotEmpty(conHql.toString())){
						sb.append("(");
						sb.append(conHql);
						sb.append(")");
					}
				}else{//高级设置
					StringBuilder conHql = new StringBuilder();
					List<Condition> conditions = dataRule.getConditions();
					for(int i=0;i<conditions.size();i++){
						con = conditions.get(i);
						//获得字段名
						field = con.getField();
						if(con.getField().equals(PermissionUtils.STANDARD_FIELD_CREATOR)||con.getField().equals(PermissionUtils.STANDARD_FIELD_DEPARMENT)
								||con.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||con.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//如果是标准字段或创建人角色或创建人工作组
							if(con.getField().equals(PermissionUtils.STANDARD_FIELD_CREATOR)||con.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||con.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//创建人
								//获得创建人字段名
								field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".creator");//实体名.creator
								if(StringUtils.isEmpty(field))field = "creatorId";
							}else if(con.getField().equals(PermissionUtils.STANDARD_FIELD_DEPARMENT)){//创建人部门
								//获得创建人字段名
								field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".department");//实体名.department
								if(StringUtils.isEmpty(field))field = "departmentId";
							}
						}
						
						StringBuilder conHqlAutomatic = new StringBuilder();
						if(con.getOperator()==FieldOperator.IS_NULL||con.getOperator()==FieldOperator.NOT_NULL){//为空或不为空时数据分类条件没有对应的PermissintItemCondition
							conHqlAutomatic.append(getExpressByCondition(con,entity,null,field));
						}else{
							//解析高级设置并获得表达式
							analysisAdvancedExpress(con,permissionItems,permissionInfo,entity,field,conHqlAutomatic);
						}
						if(StringUtils.isNotEmpty(conHqlAutomatic.toString())){
							//拼接逻辑运算结果表达式sb
							conHql.append(getLeftBracket(con.getLeftBracket()));
							conHql.append("(");
							conHql.append(conHqlAutomatic);
							conHql.append(")");
							conHql.append(getRightBracket(con.getRightBracket()));
							if(i<conditions.size()-1){//表示最后一个不加连接符
								conHql.append(joinType(con.getLgicOperator()));
							}
						}
					}
					if(StringUtils.isNotEmpty(conHql.toString())){
						sb.append("(");
						sb.append(conHql);
						sb.append(")");
					}
				}
				if(n<dataRules.size()-1){
					if(StringUtils.isNotEmpty(sb.toString())){
						sb.append(joinType(link));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Compare value error. Field:[" + con.getField() + 
					"], SRC: [" + obj + "], DEST:["+con.getConditionValue()+"]", e);
		}
		String express = sb.toString();
		return BeanShellUtil.evel(express);
	}
	//无部门人员时,本部门的解析
	private static void analysisSimpleDepartmentValueExpress(StringBuilder sb,PermissionInfo permissionInfo,Object entity,String field) throws Exception{
		User user = ApiFactory.getAcsService().getUserById(permissionInfo.getUserId());
		Long subCompanyId = user.getSubCompanyId();
		if(subCompanyId!=null){//当前用户不是集团公司下的人,
			sb.append(getExpressByCondition(null,entity,subCompanyId.toString(),"subCompanyId"))
			.append(joinType(LogicOperator.AND))
			.append(" ")
			.append(getExpressByCondition(null,entity,null,field));//子公司id
		}else{//当前用户是集团公司下的人
			sb.append(getExpressByCondition(null,entity,null,"subCompanyId"))
			.append(joinType(LogicOperator.AND))
			.append(" ")
			.append(getExpressByCondition(null,entity,null,field));//子公司id
		}
		
	}
	
	private static void analysisSimpleExpress(DataRule dataRule,List<PermissionItem> permissionItems,PermissionInfo permissionInfo,StringBuilder sb,Object entity,String field ) throws Exception{
		String value = null;
		DataRangeSetting rangeSetting = PermissionAnalysisFactory.getDataRangeSetting(dataRule.getDataRange(),dataRule.getDeparmentInheritable());
		if(rangeSetting!=null)value = rangeSetting.getValues(permissionItems,permissionInfo);
		if(value!=null){
			String[] values = value.split(",");
			for(int j =0;j<values.length;j++){
				String val = values[j];
				if(StringUtils.isNotEmpty(val)){
					sb.append(getExpressByCondition(null,entity,val,field));
					if(j<values.length-1){
						sb.append(joinType(LogicOperator.OR));
					}
				}
			}
		}
	}
	
	private static void analysisAdvancedExpress(Condition con ,List<PermissionItem> permissionItems,PermissionInfo permissionInfo,Object entity,String field,StringBuilder sb) throws Exception{
		PermissionItemConditionDao permissionItemConditionDao = (PermissionItemConditionDao)ContextUtils.getBean("permissionItemConditionDao");
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		//获得规则条件项，一个Condition可能会对应多个PermissionItemCondition
		List<PermissionItemCondition> pics = permissionItemConditionDao.getDataRuleConditionItemConditions(con.getId());
		if(pics.size()<=0){//在数据分类中，当【比较符号】不是“空”或“不为空”时，【显示的条件值】没填时，等同于没设该规则
			sb.append("true");
		}else{
			int n=0;
			for(PermissionItemCondition pic:pics){
				String conditionValue = pic.getConditionValue();
				DataRuleConditionValueSetting conditionValueSetting = PermissionAnalysisFactory.getDataRuleConditionValueSetting(conditionValue);
				ConditionVlaueInfo valueInfo = conditionValueSetting.getValues(conditionValue,permissionItems,permissionInfo);
				String value = valueInfo.getValue();
				
				if((StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_SUPERIOR_DEPARTMENT.toString())
						||StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_CHILD_DEPARTMENT.toString())
						||StringUtils.trim(conditionValue).equals(ConditionType.CURRENT_USER_CHILDREN_DEPARTMENT.toString()))
						&&value.equals(NO_DEPARTMENT)){//表示没有上级部门或下级部门
					sb.append(false);
				}else{
					Set<Long> result = new HashSet<Long>();
					if(valueInfo.getValueType()==DataRuleConditionValueType.STANDARD_VALUE||con.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||con.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//表示是标准值 或 标准字段时
						//解析获得的条件值
						String[] values =  value.split(",");
						if(con.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)){//创建人角色
							for (String val : values) {//val为角色id
								if(StringUtils.isEmpty(val))continue;
								Set<Long> userIds = acsService.getUserIdsByRoleIdExceptTrustedRole(Long.parseLong(val));
								if(!userIds.isEmpty())result.addAll(userIds);//去除重复的用户
							}
							values = result.toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
						}else if(con.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//创建人工作组
							for (String val : values) {//val为工作组id
								if(StringUtils.isEmpty(val))continue;
								UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
								List<Long> userIds = userManager.getUserIdsByWorkgroupId(Long.parseLong(val));
								if(!userIds.isEmpty())result.addAll(userIds);//去除重复的用户
							}
							values = result.toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
						}
						//解析值集合
						int valIndex=0;
						for (String val : values) {
							sb.append(getExpressByCondition(con,entity,val,field));
							if(valIndex<values.length-1){
								if(con.getOperator()==FieldOperator.NET){//如果是不等于
									sb.append(joinType(LogicOperator.AND)).append(" ");
								}else{
									sb.append(joinType(LogicOperator.OR)).append(" ");
								}
							}
							valIndex++;
						}
					}else{//非标准值时
						sb.append(getExpressByCondition(con,entity,value,field));
					}
				}
				if(n<pics.size()-1){
					if(con.getOperator()==FieldOperator.NET){//当条件类型为不等于，且值为：“部门1(分支1)，部门2(分支2)”
						sb.append(joinType(LogicOperator.AND));
					}else{
						sb.append(joinType(LogicOperator.OR));
					}
				}
				n++;
				
			}
		}
	}
	
	
	private static String getExpressByCondition(Condition con,Object entity, String value,String fieldName) throws Exception {
		StringBuilder sb = new StringBuilder();
		Object obj = null;
		boolean result;
		if(fieldName.contains("$")){//表示子表字段（子表和主表是一对多的关系）
			String[] field = fieldName.substring(1,con.getField().length()).split("\\.");
			obj = BeanUtils.getFieldValue(entity, field[0]);
			List<Object> o =  (List<Object>)obj;
			if(o.size()>0){
				sb.append("(");
				for(int j=0;j<o.size();j++){
					obj = BeanUtils.getFieldValue(o.get(j), field[1]);
					if(con==null){
						result = calculateCondition(obj, FieldOperator.ET, DataType.LONG, value,null);
					}else{
						result =calculateCondition(obj, con.getOperator(), con.getDataType(),value,con.getEnumPath());
					}
					sb.append(result);
					if(j<o.size()-1){
						sb.append(joinType(LogicOperator.OR));
					}
				}
				sb.append(")");
			}
		}else{
			obj = BeanUtils.getFieldValue(entity, fieldName);
			if(con==null){//简易设置解析
				result = calculateCondition(obj, FieldOperator.ET, DataType.LONG, value,null);
			}else{
				result = calculateCondition(obj, con.getOperator(), con.getDataType(), value,con.getEnumPath());
			}
			sb.append(result);
		}
		return sb.toString();
	}
	/**
	 * 计算表达式的值
	 * @param obj  原始值
	 * @param fo   比较符
	 * @param dt   数据类型
	 * @param value  比较的值
	 * @return
	 */
	public static boolean calculateCondition(Object obj, FieldOperator fo, DataType dt, String value,String enumPath){
		if(fo==FieldOperator.IS_NULL || fo== FieldOperator.NOT_NULL){
			if(obj!=null && StringUtils.isEmpty(obj.toString())) obj = null;
		}
		switch (fo) {
		case IS_NULL: return obj == null;
		case NOT_NULL: return obj != null;
		case CONTAIN: 
			if(dt==DataType.TEXT && obj!=null && value!=null){
				return obj.toString().contains(value);
			}
			return false;
		case NOT_CONTAIN: 
			if(obj==null&&value!=null){//不包含时，为空的数据要显示出来
				return true;
			}
			if(dt==DataType.TEXT && obj!=null && value!=null){
				return !obj.toString().contains(value);
			}
			return false;
		case ET: 
			if(obj==null && value==null)return true;
			if(obj!=null && value!=null){
				if(dt==DataType.ENUM){//处理枚举类型
					return obj.equals(getValueByType(value, enumPath));
				}
				if(dt==DataType.DATE||dt==DataType.TIME){//处理日期类型
					return ((Date)obj).getTime()==((Date)getValueByType(dt, value)).getTime();
				}
				return obj.equals(getValueByType(dt, value));
			}
			return false;
		case NET: 
			if(obj==null&&value!=null){//不等于时，为空的数据要显示出来
				return true;
			}
			if(obj!=null && value!=null){
				if(dt==DataType.ENUM){//处理枚举类型
					return !obj.equals(getValueByType(value, enumPath));
				}
				if(dt==DataType.DATE||dt==DataType.TIME){//处理日期类型
					return !(((Date)obj).getTime()==((Date)getValueByType(dt, value)).getTime());
				}
				return !obj.equals(getValueByType(dt, value));
			}
			return false;
		case GT: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareGT(dt, obj, getValueByType(dt, value));
			}
			return false;
		case GET: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareGET(dt, obj, getValueByType(dt, value));
			}
			return false;
		case LT: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareLT(dt, obj, getValueByType(dt, value));
			}
			return false;
		case LET: 
			if(comparableBigSmall(dt)){
				return CompareUtils.compareLET(dt, obj, getValueByType(dt, value));
			}
			return false;
		}
		return false;
	}
	
	private static boolean comparableBigSmall(DataType dt){
		return DataType.DATE==dt||DataType.TIME==dt||DataType.INTEGER==dt
				||DataType.LONG==dt||DataType.DOUBLE==dt||DataType.FLOAT==dt;
	}
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static Object getValueByType(DataType dt, String value){
		if(StringUtils.isEmpty(value)&& dt!=DataType .TEXT ) return null;
		try {
			switch (dt) {
			case TEXT: return value;
			case DATE: return DATE_FORMAT.parse(value);
			case TIME: return TIME_FORMAT.parse(value);
			case INTEGER: return Integer.valueOf(value);
			case LONG: return Long.valueOf(value);
			case DOUBLE: return Double.valueOf(value);
			case FLOAT: return Float.valueOf(value);
			case BOOLEAN: return Boolean.valueOf(value);
			case ENUM: break;
			}
		} catch (Exception e) {
			logger.error("Parse string to " + dt + " error. string["+value+"]", e);
		}
		return null;
	}
	
	public static Object getValueByType( String value,String enumPath){
		if(StringUtils.isEmpty(value)) return null;
		try {
			Object[] enumValues = Class.forName(enumPath).getEnumConstants();
			for (Object object : enumValues) {
				if(object.toString().equals(value)){
					return object;
				}
			}
		} catch (Exception e) {
			logger.error("Parse  " + value + " to enum:"+enumPath+"error.", e);
		}
		return null;
	}
	/**
	 * 原生SQL查询
	 * @param sql
	 * @param values
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> findBySql(String sql, Object... values){
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if(values != null){
			for(int i = 0; i < values.length; i++){
				sqlQuery.setParameter(i, values[i]);
			}
		}
		return sqlQuery.list();
	}
	
	/**
	 * 取得当前Session.
	 */
	private static Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	
	//获取直属上级的id
	public static Long getDirectLeaderId() {
		RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		com.norteksoft.acs.entity.organization.User directLeader = rankManager.getDirectLeader(ContextUtils.getLoginName());
		if(directLeader!=null){
			return directLeader.getId();
		}
		return null;
	}
	//获取直属上级的登录名
	public static Long getDirectLeader() {
		RankManager rankManager=(RankManager)ContextUtils.getBean("rankManager");
		com.norteksoft.acs.entity.organization.User directLeader = rankManager.getDirectLeader(ContextUtils.getLoginName());
		if(directLeader!=null){
			return directLeader.getId();
		}
		return null;
	}
	
	public static String dealTextNullCondition(DataType dataType,FieldOperator operator,String con){
		StringBuilder sb = new StringBuilder();
		if(dataType==DataType.TEXT &&operator==FieldOperator.IS_NULL){
			//文本类型，如果规则条件为空，则条件为 : a is null or a ='' 
			sb.append(operator.sign).append(" or ").append(con).append("='' ");
		}else if(dataType==DataType.TEXT &&operator==FieldOperator.NOT_NULL){
			//文本类型，如果规则条件不为空，则条件为 : a is not null and a<>'' 
			sb.append(operator.sign).append(" and ").append(con).append("<>' ' ");
		}else{
			sb.append(operator.sign);
		}
		return sb.toString();
	}
	
	public static List<Object> getPermissionResult(String hql,Object[] values) {
		List<Object> obj = new ArrayList<Object>(); 
		String permissionHql = (String)Struts2Utils.getRequest().getAttribute(PermissionUtils.PERMISSION_HQL);
		Object[] parameter = (Object[])Struts2Utils.getRequest().getAttribute(PermissionUtils.PERMISSION_PARAMETERS);
		if(permissionHql==null){
			obj.add(hql);
			obj.add(values);
		}else if(permissionHql.equals(PermissionUtils.NO_PERMISSION)){
			obj.add(PermissionUtils.NO_PERMISSION);
			obj.add(parameter);
		}else{
			obj.add(permissionHql);
			obj.add(parameter);
		}
		return obj;
	}
	
	/**
	 * 根据HQL语句和条件集合拼接HQL，并重新组装条件
	 * @param hql  HQL 如： select x form XX x where x.p=? order by x.op
	 * @param conditions  集合
	 * @param prmts  HQL参数列表
	 * @return 
	 */
	public static ConditionResult getPermissionHqlPamateters(String hql, PermissionInfo  permissionInfo,LogicOperator operator,Object... prmts){
		List<Long> deptIds = permissionInfo.getDepartmentIds();
		
		List<DataRuleResult> dataRules = permissionInfo.getRules();
		DataTableDao dataTableDao = (DataTableDao)ContextUtils.getBean("dataTableDao");
		PermissionItemConditionDao permissionItemConditionDao = (PermissionItemConditionDao)ContextUtils.getBean("permissionItemConditionDao");
		String alias = dataTableDao.getAlias(hql); //HQL实体别名 
		StringBuilder newhql=new StringBuilder();
		List<Object> prameters=new ArrayList<Object>();
		for(Object o:prmts){
			prameters.add(o);
		}
		int t = 0;
		DataTable table = null;
		for (DataRuleResult ruleResult : dataRules) {
			StringBuilder ruleHql = new StringBuilder();
			DataRule rule = ruleResult.getDataRule();
			
			List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
			//处理用户兼职的情况，数据授权选择人员、部门时部门已经确定，且数据分类规则为本部门、当前用户部门、当前用户上级部门、当前用户顶级部门、当前用户所在部门的子部门、当前用户所在部门的子部门（含继承）需要用授权中设置的部门来确定
			Permission permission = ruleResult.getPermission();
			if(permission!=null){
				PermissionItemManager permissionItemManager = (PermissionItemManager)ContextUtils.getBean("permissionItemManager");
				permissionItems = permissionItemManager.getPermissionItemsByPermission(permission.getId());
			}
			table = dataTableDao.get(rule.getDataTableId());
			if(rule==null) {
				t++; continue;
			}
			
			if(rule.getSimplable()){//简易设置
				String field = "";
				if(rule.getDataRange()==DataRange.CURRENT_DEPARTMENT){//本部门
					//获得创建人字段名
					field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".department");//实体名.department
					if(StringUtils.isEmpty(field))field = "departmentId";
				}else if(rule.getDataRange()==DataRange.MYSELF){//本人
					//获得创建人字段名
					field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".creator");//实体名.creator
					if(StringUtils.isEmpty(field))field = "creatorId";
				}else if(rule.getDataRange()==DataRange.ALL){//全公司
					//获得创建人字段名
					field = "companyId";
				}
				//获得条件值
				if(deptIds.size()<=0&&rule.getDataRange()==DataRange.CURRENT_DEPARTMENT){//如果当前用户是无部门人员且数据范围为本部门数据时,无部门人员可以看到当前分支机构内所有部门id为null的数据
					analysisSimpleDepartmentValue(ruleHql,field,alias,prameters);
				}else{
					analysisSimpleValue(rule,ruleHql,field,alias,prameters,permissionItems,permissionInfo);
				}
				
			}else{//高级设置
				int i = 0;
				List<Condition> conditions  = rule.getConditions();
				for(Condition c:conditions){
					//拼接左括号
					ruleHql.append(PermissionUtils.getLeftBracket(c.getLeftBracket()));
					//获得规则条件项，一个Condition可能会对应多个PermissionItemCondition
					String field = c.getField();
					
					if(c.getField().equals(PermissionUtils.STANDARD_FIELD_CREATOR)||c.getField().equals(PermissionUtils.STANDARD_FIELD_DEPARMENT)
							||c.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||c.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//如果是标准字段或创建人角色或创建人工作组
						if(c.getField().equals(PermissionUtils.STANDARD_FIELD_CREATOR)||c.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||c.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//创建人
							//获得创建人字段名
							field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".creator");//实体名.creator
							if(StringUtils.isEmpty(field))field = "creatorId";
						}else if(c.getField().equals(PermissionUtils.STANDARD_FIELD_DEPARMENT)){//创建人部门
							//获得创建人部门字段名
							field = PropUtils.getProp("dataAuthStandardField.properties", table.getName()+".department");//实体名.department
							if(StringUtils.isEmpty(field))field = "departmentId";
						}
					}
					if(c.getOperator()==FieldOperator.IS_NULL||c.getOperator()==FieldOperator.NOT_NULL){//为null或不为null
						analysisHqlParameters(ruleHql,field,alias,c.getDataType(),c.getOperator(),c.getEnumPath(),prameters,null);
					}else{
						List<PermissionItemCondition> pics = permissionItemConditionDao.getDataRuleConditionItemConditions(c.getId());
						//解析条件值
						analysisDataRuleCondition(ruleHql,field,pics,alias,c,prameters,permissionItems,permissionInfo);
					}
					
					//拼接右括号
					ruleHql.append(PermissionUtils.getRightBracket(c.getRightBracket()));
					//拼接连接符
					if(i<conditions.size()-1){
						if(StringUtils.isNotEmpty(ruleHql.toString())){
							ruleHql.append(analysisLogicOperator(c.getLgicOperator()));
						}
					}
					i++;
				}
			}
			
			if(StringUtils.isNotEmpty(ruleHql.toString())){
				newhql.append("(");
				newhql.append(ruleHql);
				newhql.append(")");
			}
			if(t<dataRules.size()-1){
				if(StringUtils.isNotEmpty(ruleHql.toString())){
					newhql.append(analysisLogicOperator(operator));
				}
			}
			t++;
		}
		ConditionResult cr=new ConditionResult();
		if(StringUtils.isNotEmpty(newhql.toString())){
			String condition=" and ("+newhql.toString()+")";
			String where = " where ";
			String order_by = " order by ";
			StringBuilder hqlResult=new StringBuilder();
			if(hql.contains(where) && hql.contains(order_by)){
				String[] arr=hql.split(order_by);
				hqlResult.append(arr[0]);
				hqlResult.append(condition);
				hqlResult.append(order_by);
				hqlResult.append(arr[1]);
			}else if(hql.contains(where)){
				hqlResult.append(hql);
				hqlResult.append(condition);
			}else if(hql.contains(order_by)){
				String[] arr=hql.split(order_by);
				hqlResult.append(arr[0]);
				hqlResult.append(where);
				hqlResult.append(newhql.toString());
				hqlResult.append(order_by);
				hqlResult.append(arr[1]);
			}else{
				hqlResult.append(hql);
				hqlResult.append(where);
				hqlResult.append(newhql.toString());
			}
			cr.setHql(hqlResult.toString());
		}else{
			cr.setHql(hql);
		}
		cr.setPrameters(prameters.toArray());
		return cr;
	}
	
	/**
	 * 解析本部门数据
	 */
	private static void analysisSimpleDepartmentValue(StringBuilder newhql,String field,String alias,List<Object> prameters){
			User user = ApiFactory.getAcsService().getUserById(ContextUtils.getUserId());
			if(user.getSubCompanyId()!=null){//当前用户不是集团公司下的人,
				newhql.append(" (")
				.append(alias)
				.append(".")
				.append("subCompanyId=? and ")
				.append(alias)
				.append(".")
				.append(field)
				.append(" is null) ");
				prameters.add(user.getSubCompanyId());
			}else{//当前用户是集团公司下的人
				newhql.append(" (")
				.append(alias)
				.append(".")
				.append("subCompanyId is null and ")
				.append(alias)
				.append(".")
				.append(field)
				.append(" is null) ");
			}
	}
	/**
	 * 数据分类中简易设置解析
	 * @param rule
	 * @param newhql
	 * @param field
	 * @param alias
	 * @param prameters
	 */
	private static void analysisSimpleValue(DataRule rule,StringBuilder newhql,String field,String alias,List<Object> prameters,List<PermissionItem> permissionItems,PermissionInfo  permissionInfo){
		String value = null;
		DataRangeSetting rangeSetting = PermissionAnalysisFactory.getDataRangeSetting(rule.getDataRange(),rule.getDeparmentInheritable());
		if(rangeSetting!=null)value = rangeSetting.getValues(permissionItems,permissionInfo);
		if(value!=null){
			String[] values = value.split(",");
			int a=0;
			for(String val:values){
				if(StringUtils.isNotEmpty(val)){
					//获得hql和hql的参数集合
					analysisHqlParameters(newhql,field,alias,DataType.LONG,FieldOperator.ET,null,prameters,val);
					if(a<values.length-1){
						newhql.append(analysisLogicOperator(LogicOperator.OR));//value之间是or的关系
					}
				}
				a++;
			}
		}
	}
	/**
	 * 解析条件值
	 * @param newhql
	 * @param field
	 * @param pics
	 * @param alias
	 * @param c
	 * @param prameters
	 */
	private static void analysisDataRuleCondition(StringBuilder newhql,String field,List<PermissionItemCondition> pics,String alias,Condition c,List<Object> prameters,List<PermissionItem> permissionItems,PermissionInfo  permissionInfo){
		
		StringBuilder picsHql = new StringBuilder();
		int n =0;
		for(PermissionItemCondition pic:pics){
//			StringBuilder conHql = new StringBuilder();
			//获得条件值
			String conditionValue = pic.getConditionValue();
			DataRuleConditionValueSetting conditionValueSetting = PermissionAnalysisFactory.getDataRuleConditionValueSetting(conditionValue);
			ConditionVlaueInfo valueInfo = conditionValueSetting.getValues(conditionValue,permissionItems,permissionInfo);
			String value = valueInfo.getValue();
			Set<Long> result = new HashSet<Long>();
			if(valueInfo.getValueType()==DataRuleConditionValueType.STANDARD_VALUE||c.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)||c.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)||c.getField().equals(PermissionUtils.STANDARD_FIELD_DEPARMENT)){//表示是标准值 或 标准字段时
				//解析获得的条件值
				String[] values =  value.split(",");
				if(c.getField().equals(PermissionUtils.STANDARD_FIELD_ROLE)){//创建人角色
					for (String val : values) {//val为角色id
						if(StringUtils.isEmpty(val))continue;
						AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
						Set<Long> userIds = acsService.getUserIdsByRoleIdExceptTrustedRole(Long.parseLong(val));
						if(!userIds.isEmpty())result.addAll(userIds);//去除重复的用户
					}
					values = result.toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
				}else if(c.getField().equals(PermissionUtils.STANDARD_FIELD_WORKGROUP)){//创建人工作组
					for (String val : values) {//val为工作组id
						if(StringUtils.isEmpty(val))continue;
						UserManager userManager = (UserManager)ContextUtils.getBean("userManager");
						List<Long> userIds = userManager.getUserIdsByWorkgroupId(Long.parseLong(val));
						if(!userIds.isEmpty())result.addAll(userIds);//去除重复的用户
					}
					values = result.toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
				}
				//解析值集合
				analysisValue(picsHql,field,alias,c,prameters,values);
			}else{//非标准值时
				//获得hql和hql的参数集合
				analysisHqlParameters(picsHql,field,alias,c.getDataType(),c.getOperator(),c.getEnumPath(),prameters,value);
			}
			
			if(n<pics.size()-1){
				if(StringUtils.isNotEmpty(picsHql.toString())){
					if(c.getOperator()==FieldOperator.NET){
						picsHql.append(analysisLogicOperator(LogicOperator.AND));//value之间是and的关系
					}else{
						picsHql.append(analysisLogicOperator(LogicOperator.OR));//value之间是or的关系
					}
				}
			}
			n++;
		}
		if(StringUtils.isNotEmpty(picsHql.toString())){
			newhql.append("(");
			newhql.append(picsHql);
			newhql.append(")");
		}
	}
	
	private static void  analysisValue(StringBuilder newhql,String field,String alias,Condition c,List<Object> prameters,String[] values){
		String value="";
		int a=0;
		for (String val : values) {
			value = val;
			//获得hql和hql的参数集合
			analysisHqlParameters(newhql,field,alias,c.getDataType(),c.getOperator(),c.getEnumPath(),prameters,value);
			if(a<values.length-1){
				if(c.getOperator()==FieldOperator.NET){//不等于时条件为与
					newhql.append(analysisLogicOperator(LogicOperator.AND));//value之间是AND的关系
				}else{
					newhql.append(analysisLogicOperator(LogicOperator.OR));//value之间是or的关系
				}
			}
			a++;
		}
	}
	
	private static void analysisHqlParameters(StringBuilder newhql,String field,String alias,DataType dataType,FieldOperator operator,String enumPath,List<Object> prameters,String value){
		newhql.append("(");
		String con = "",subCon = "";
		if(field.contains("$")){//解析子表字段
			subCon = field.substring(1, field.length());
			//当数据类型是float类型时，需要使用format函数格式化，因为条件为等于或不等于时单精度时查询不准确
			if(dataType==DataType.FLOAT && (operator==FieldOperator.ET || operator==FieldOperator.NET ))
				subCon = "format("+subCon+",5)";
			newhql.append(subCon);
		}else{
			con = alias+"."+field;
			//当数据类型是float类型时，需要使用format函数格式化，因为条件为等于或不等于时单精度时查询不准确
			if(dataType==DataType.FLOAT && (operator==FieldOperator.ET || operator==FieldOperator.NET))
				con = "format("+con+",5)";
			newhql.append(con);
		}
		//处理字段为空不为空的情况
		if(field.contains("$")){
			newhql.append(PermissionUtils.dealTextNullCondition(dataType, operator, subCon));
		}else{
			newhql.append(PermissionUtils.dealTextNullCondition(dataType, operator, con));
		}
		if(dataType==DataType.ENUM && needPlaceholder(operator)){//数据类型为枚举类型，条件不是包含关系，也不是为空不为空的关系
			newhql.append("? ");
			prameters.add(PermissionUtils.getValueByType(value,enumPath));
		}else if(dataType==DataType.FLOAT && (operator==FieldOperator.ET || operator==FieldOperator.NET)){//处理单精度浮点型，条件为等于或不等于时精度问题
			newhql.append("format(?,5) ");
			prameters.add(PermissionUtils.getValueByType(dataType, value));
		}else if(needPlaceholder(operator)){//条件不是包含关系，也不是为空不为空的关系
			newhql.append("? ");
			prameters.add(PermissionUtils.getValueByType(dataType, value));
		}else if(containtCondition(operator)){//条件为包含关系
			if(StringUtils.isEmpty(value)) value = NO_VALUE_STRING;
			newhql.append("? ");
			prameters.add(PermissionUtils.getValueByType(dataType, "%"+value+"%"));
		}
		if(operator==FieldOperator.NET||operator==FieldOperator.NOT_CONTAIN){
			newhql.append(" or ");
			if(field.contains("$")){//解析子表字段
				newhql.append(subCon).append(" is null");
			}else{
				newhql.append(con).append(" is null");
			}
		}
		newhql.append(")");
	}
	
	private static boolean needPlaceholder(FieldOperator fo){
		return !(FieldOperator.CONTAIN==fo || FieldOperator.NOT_CONTAIN==fo 
				|| FieldOperator.IS_NULL==fo || FieldOperator.NOT_NULL==fo);
	}
	private static boolean containtCondition(FieldOperator fo){
		return (FieldOperator.CONTAIN==fo || FieldOperator.NOT_CONTAIN==fo );
	}
	
	private static String analysisLogicOperator(LogicOperator o){
		if(LogicOperator.AND.equals(o)){
			return " and ";
		}else {
			return " or ";
		}
	}
	
	public static Set<Long> getDepartmentIds(List<PermissionItem> permissionItems,PermissionInfo permissionInfo){
		Set<Long> result = new HashSet<Long>();//存放部门id
		List<Long> currentUserDepartmentIds = permissionInfo.getDepartmentIds();
		
		Set<String> userIds = permissionInfo.getPermissionUsers();
		Long userId = permissionInfo.getUserId();
		if(userIds.contains(userId.toString())){//如果包含当前用户
			result.addAll(currentUserDepartmentIds);
			return result;
		}
		return result;
	}
	
	public static String getUserConditionExpress(List<PermissionItem> permissionItems){
		PermissionItemConditionManager permissionItemConditionManager = (PermissionItemConditionManager)ContextUtils.getBean("permissionItemConditionManager");
		StringBuilder conditionExpress = new StringBuilder();
		for(int n =0;n<permissionItems.size();n++){
			PermissionItem item = permissionItems.get(n);
			List<PermissionItemCondition> pics = permissionItemConditionManager.getPermissionItemConditions(item.getId());
			if(item.getItemType()==ItemType.ALL_USER){//所有人时
				conditionExpress.append(ItemType.USER)
				.append(" ")
				.append(UserOperator.ET)
				.append(" '")
				.append(ItemType.ALL_USER)
				.append("'");
				break;
			}else{//不是所有人时
				if(item.getLeftBracket()!=null){
					conditionExpress.append(item.getLeftBracket())
					.append(" ");
				}
				
				conditionExpress.append("(");
				for(int i =0;i<pics.size();i++){
					//(UESER ET 'value' AND ...)
					conditionExpress.append(item.getItemType().toString())
					.append(" " )
					.append(item.getOperator().toString())
					.append(" ");
					String conditionValue = pics.get(i).getConditionValue();
					conditionExpress.append("'").append(conditionValue).append("'");
					if(i<pics.size()-1){
						if(item.getOperator()==UserOperator.ET){
							conditionExpress.append(" ").append(LogicOperator.OR.toString()).append(" ");
						}else{
							conditionExpress.append(" ").append(LogicOperator.AND.toString()).append(" ");
						}
					}
				}
				conditionExpress.append(") ");
				if(item.getRightBracket()!=null){
					conditionExpress.append(item.getRightBracket());
				}
				if(n<permissionItems.size()-1){
					conditionExpress.append(item.getJoinType().toString())
					.append(" ");
				}
			}
		}
		return conditionExpress.toString();
	}
	
	
	
	/**
	 * 返回Set<String>而不返回Set<Long>的原因是，当是人员、部门时集合的值为userId~departmentId,其他情况均是userId
	 * 
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
	public static Set<String> getUsers(String userCondition){
		return parseBrackets(userCondition);
	}
	private static Map<String,String> userMap = new HashMap<String,String>();
	private static char LEFT_BRACKET = '(';
	private static char RIGHT_BRACKET = ')';
	private static String VARIABLE_PRE = "var";
	private static final String SINGLE_QUOTATION_MARK = "'";
	private static Set<String> parseBrackets(String userCondition){
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
		return parseOr(userCondition);
	}
	
	private static Set<String> parseOr(String condition){
		String[] conds = condition.split(LogicOperator.OR.toString()+" ");
		Set<String> userDepts = new HashSet<String>();
		for(String cond :conds){
			userDepts.addAll(parseAnd(cond));
		}
		return userDepts;
	}
	private static Set<String> parseAnd(String condition){
		String[] conds = condition.split(LogicOperator.AND.toString()+" ");
		Map<Integer,Set<String>> map = new HashMap<Integer,Set<String>>();
		int minSize = 100000;//默认为十万。如果一个公司人数超过十万，这个的初始值可能会出问题
		Set<String> temp = null;
		int minI = 0;
		for(int i=0;i<conds.length;i++){
			if(userMap.get(conds[i].trim())==null){
				temp = parseAtomCondition(conds[i].trim());
				
			}else{
				temp = parseOr(userMap.get(conds[i].trim()));
			}
			if(temp.size()==0) return temp;//如果在and条件中有一个条件没有选出人，则整个and条件也没有人
			if(temp.size()<minSize){
				 minSize = temp.size();
				 minI = i;//人数最少的条件的key
			}
			map.put(i, temp);
		}
		Set<String> result = new HashSet<String>();
		List<String> minSet = new ArrayList<String>(map.get(minI));
		
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
	private static Set<String> parseAtomCondition(String atomCondition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.isEmpty(atomCondition))return userSet;
		if(atomCondition.trim().startsWith(ItemType.USER.toString())){
			userSet.addAll(parseUser(atomCondition));
		}else if(atomCondition.trim().startsWith(ItemType.ROLE.toString())){
			userSet.addAll(parseRole(atomCondition));
		}else if(atomCondition.trim().startsWith(ItemType.DEPARTMENT.toString())){
			userSet.addAll(parseDepartment(atomCondition));
		}else if(atomCondition.trim().startsWith(ItemType.WORKGROUP.toString())){
			userSet.addAll(parseWorkGroup(atomCondition));
		}
		return userSet;
	}
	
	private static Set<String> parseWorkGroup(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, UserOperator.NET.toString())) {
			userSet.addAll(getUsersNotInWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK)));
		}else if(StringUtils.contains(condition, UserOperator.ET.toString())){
			Set<Workgroup> workgroupSet = getWorkGroup(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(getUsersByWorkGroup(workgroupSet));
		}
		return userSet;
	}
	
	private static Set<Workgroup> getWorkGroup(String valueExpress){
		Set<Workgroup> workGroupSet = new HashSet<Workgroup>();
		if(StringUtils.isEmpty(valueExpress)) return workGroupSet;
		if("ALL_WORKGROUP".equals(valueExpress)){
			workGroupSet.addAll(ApiFactory.getAcsService().getAllWorkgroups());
			return workGroupSet;
		}
		workGroupSet.add(getWorkGroupById(Long.parseLong(valueExpress)));
		return workGroupSet;
	}
	
	
	private static Set<String> parseDepartment(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.toString())) {
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			userSet.addAll(getUsersNotInDepartment(departmentSet));
		}else if(StringUtils.contains(condition, TextOperator.ET.toString())){
			Set<Department> departmentSet = getDepartment(StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK));
			if(departmentSet!=null)userSet.addAll(getUsersByDepartment(departmentSet));
		}
		return userSet;
	}
	
	private static Set<Department> getDepartment(String valueExpress){
		Set<Department> departmentSet = new HashSet<Department>();
		if(StringUtils.isEmpty(valueExpress)) return departmentSet;
		if("ALL_DEPARTMENT".equals(valueExpress)){//所有部门
			departmentSet.addAll(ApiFactory.getAcsService().getAllDepartments());
			return departmentSet;
		}
		//值来自组织结构中
		departmentSet.add(getDepartmentById(Long.parseLong(valueExpress)));
		return departmentSet;
	}
	
	private static Set<String> parseRole(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.toString())) {
			String roleId = StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK);
			if(StringUtils.isNotEmpty(roleId)){
				AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
				List<Long> allUserIds = acsServiceImpl.getAllUserIdsByCompany(ContextUtils.getCompanyId());
				
				Set<String> result = new HashSet<String>();
				result.addAll(Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
				
				Set<String> removeUserIds = getUsersByRoleId(Long.parseLong(roleId));
				result.removeAll(removeUserIds);
				
				userSet.addAll(result);
			}
		}else if(StringUtils.contains(condition, TextOperator.ET.toString())){
			String roleId  = StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK);
			if(StringUtils.isNotEmpty(roleId)){
				userSet.addAll(getUsersByRoleId(Long.parseLong(roleId)));
			}
			
		}
		return userSet;
	}
	
	private static Set<String> parseUser(String condition){
		Set<String> userSet =  new HashSet<String>();
		if(StringUtils.contains(condition, TextOperator.NET.toString())) {
			String userId = StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK);
			if(StringUtils.isNotEmpty(userId)){
				Long userid = Long.parseLong(userId);
				List<Long> allUserIds = ApiFactory.getAcsService().getAllUserIdsByCompany(ContextUtils.getCompanyId());
				//移除选择的人员
				allUserIds.remove(userid);
				List<String> list = Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(","));
				userSet.addAll(list);
			}
		}else if(StringUtils.contains(condition, TextOperator.ET.toString())){
			String userId = StringUtils.substringBetween(condition, SINGLE_QUOTATION_MARK, SINGLE_QUOTATION_MARK);
			if(ItemType.ALL_USER.toString().equals(userId)){//当是所有人时
				List<Long> allUserIds = ApiFactory.getAcsService().getAllUserIdsByCompany(ContextUtils.getCompanyId());
				userSet.addAll(Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
			}else{//不是所有人
				if(StringUtils.isNotEmpty(userId)){
					userSet.add(userId);
				}
			}
		}
		return userSet;
	}
	
	private static Workgroup getWorkGroupById(Long workgroupId){
		return ApiFactory.getAcsService().getWorkgroupById(workgroupId);
	}
	
	private static Department getDepartmentById(Long departmentId){
		return ApiFactory.getAcsService().getDepartmentById(departmentId);
	}
	
	private static Set<String> getUsersNotInWorkGroup(String valueExpress){
		if("ALL_WORKGROUP".equals(valueExpress)){//如果是所有工作组
			AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
			List<Long> allUserIds = acsServiceImpl.getAllUserIdsByCompany(ContextUtils.getCompanyId());
			
			Set<String> result = new HashSet<String>();
			List<Long> userids = acsServiceImpl.getUserIdsWithWorkgroup();
			
			allUserIds.removeAll(userids);//在所有用户中移除工作组中的所有人
			
			result.addAll( Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
			return result;
		}else{
			AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
			List<Long> allUserIds = acsServiceImpl.getAllUserIdsByCompany(ContextUtils.getCompanyId());
			
			Set<String> result = new HashSet<String>();
			result.addAll(Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
			
			Set<String> removeUserIds = getUserId(ApiFactory.getAcsService().getUsersByWorkgroupId(Long.parseLong(valueExpress)));
			result.removeAll(removeUserIds);
			return result;
		}
	}
	
	private static Set<String> getUsersByWorkGroup(List<Long> workgroupIds){
		Set<String> userIds = new HashSet<String>();
		for(Long wgId:workgroupIds){
			userIds.addAll(getUserId(ApiFactory.getAcsService().getUsersByWorkgroupId(wgId)));
		}
		return userIds;
	}
	private static Set<String> getUsersByWorkGroup(Set<Workgroup> workgroupSet){
		Set<String> userIds = new HashSet<String>();
		for(Workgroup workGroup:workgroupSet){
			if(workGroup!=null){
				userIds.addAll(getUserId(ApiFactory.getAcsService().getUsersByWorkgroupId(workGroup.getId())));
			}
		}
		return userIds;
	}
	
	private static Set<String> getUserId(Collection<User> users){
		Set<String> userIds = new HashSet<String>();
		for(User user : users){
			if(user!=null){
				userIds.add(user.getId().toString());
			}
		}
		return userIds;
	}
	
	private static List<Long> getAllRoleIds(){
		StandardRoleManager standardRoleManager = (StandardRoleManager)ContextUtils.getBean("standardRoleManager");
		return standardRoleManager.getRoleIdsBySystemId();
	}
	
	private static Set<String> getUsersByRoleId(Long roleId){
		Set<String> userIds = new HashSet<String>();
		for(User user: ApiFactory.getAcsService().getUsersByRoleIdExceptTrustedRole(roleId)){
			if(user!=null){
				userIds.add(user.getId().toString());
			}
		}
		return userIds;
	}
	
	private static Set<String> getUsersNotInDepartment(Set<Department> departmentSet){
		AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		List<Long> allUserIds = new ArrayList<Long>(ApiFactory.getAcsService().getAllUserIdsByCompany(ContextUtils.getCompanyId()));
		for(Department department:departmentSet){
			if(department.getBranch()){//如果是分支机构
				List<Long> branchUserIds = acsServiceImpl.getAllUserIdsByBranch(department.getId());
				allUserIds.removeAll(branchUserIds);
			}else{
				List<Long> deptUserIds = acsServiceImpl.getUserIdsByDepartment(department.getId());
				allUserIds.removeAll(deptUserIds);
			}
		}
		Set<String> result = new HashSet<String>();
		result.addAll(Arrays.asList(allUserIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
		return result;
	}
	
	private static Set<String> getUsersByDepartment(Set<Department> departmentSet){
		AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		Set<Long> userDeptIds = new HashSet<Long>();
		for(Department department:departmentSet){
			if(department!=null){
				if(department.getBranch()){
					userDeptIds.addAll(acsServiceImpl.getAllUserIdsByBranch(department.getId()));
				}else{
					userDeptIds.addAll(acsServiceImpl.getUserIdsByDepartment(department.getId()));
				}
			}
		}
		Set<String> result = new HashSet<String>();
		result.addAll(Arrays.asList(userDeptIds.toString().replace("[", "").replace("]", "").replace(" ","").split(",")));
		return result;
	}
	
}
