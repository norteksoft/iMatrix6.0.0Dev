package com.norteksoft.acs.dao.authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.entity.authority.DataRuleResult;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Repository
public class PermissionValidator<T, PK extends Serializable> extends HibernateDao<T, PK>{
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private PermissionDao permissionDao;
	/**
	 * 查询权限
	 * @param <T>
	 * @param className
	 * @param hql
	 * @param values
	 */
	public <T> void addPermissionCondition(String hql,  LogicOperator authlink,LogicOperator dataRulelink, Object... values){
		addConditionResult(hql,authlink,dataRulelink,null,values);
	}
	/**
	 * 查询权限
	 * @param <T>
	 * @param className
	 * @param hql
	 * @param values
	 */
	public <T> void addPermissionCondition(String hql, LogicOperator authlink,LogicOperator dataRulelink, List<String> permissionCodes, Object... values){
		addConditionResult(hql,authlink,dataRulelink,permissionCodes,values);
	}
	
	public <T> void addConditionResult(String hql,LogicOperator authlink,LogicOperator dataRulelink,List<String> permissionCodes, Object... values){
		PermissionInfo  permissionInfo = getSearchAuthorityDataRule(PermissionAuthorize.SEARCH,authlink);
		if(permissionInfo.isNoPermission()){//表示没有授权，可以查看所有数据
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_HQL, null);
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_PARAMETERS, null);
		}else if(!permissionInfo.isHasPermission()){//授权了，没有权限
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_HQL,PermissionUtils.NO_PERMISSION);
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_PARAMETERS, null);
		}else{
			ConditionResult cr = PermissionUtils.getPermissionHqlPamateters(hql, permissionInfo,dataRulelink, values);
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_HQL, cr.getHql());
			Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_PARAMETERS, cr.getPrameters());
		}
	}
	/**
	 * 查询	
	 * @param <T>
	 * @param className
	 * @param authority
	 * @return
	 */
	private  <T> PermissionInfo getSearchAuthorityDataRule(PermissionAuthorize authority,LogicOperator authlink) {
		List<Permission> ps = new ArrayList<Permission>();
		String listCode=Struts2Utils.getParameter("_list_code");
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView = listViewManager.getListViewByCode(listCode);
		if(listView!=null)ps = permissionDao.getPermissionsByListViewId(listView.getId());
		return getDataRuleByPermission(ps,authority,null,authlink);
	}

	/**
	 * 查看权限
	 * @param entity
	 */
	public boolean viewPermission(T entity,LogicOperator authlink,LogicOperator dataRulelink){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.SEARCH,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 查看权限
	 * @param entity
	 */
	public boolean viewPermission(T entity,LogicOperator authlink,LogicOperator dataRulelink,List<String> permissionCodes){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.SEARCH,permissionCodes,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	
	/**
	 * 修改权限
	 * @param entity
	 */
	public boolean updatePermission(T entity,LogicOperator authlink,LogicOperator dataRulelink){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.UPDATE,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity,permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 修改权限
	 * @param entity
	 */
	public boolean updatePermission(T entity,LogicOperator authlink,LogicOperator dataRulelink,List<String> permissionCodes){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.UPDATE,permissionCodes,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	
	/**
	 * 删除权限
	 * @param entity
	 */
	public boolean deletePermission(T entity,LogicOperator authlink,LogicOperator dataRulelink){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.DELETE,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 删除权限
	 * @param entity
	 */
	public boolean deletePermission(T entity,LogicOperator authlink,LogicOperator dataRulelink, List<String> permissionCodes){
		try {
			PermissionInfo permissionInfo = getAuthorityDataRule(PermissionAuthorize.DELETE,permissionCodes,entity,authlink);
			if(permissionInfo.isNoPermission()){//表示没有授权
				return true;
			}
			if(!permissionInfo.isHasPermission()){//表示有授权但没有权限
				return false;
			}
			return PermissionUtils.entityPermission(entity, permissionInfo,dataRulelink);
		} catch (Exception e) {
			logger.error("Get update permission error. ", e);
		}
		return false;
	}
	
	/**
	 * 根据操作查询有权限的规则
	 * @param authority 查询
	 * @return
	 */
	public PermissionInfo getAuthorityDataRule(PermissionAuthorize authority,LogicOperator authlink){
		DataTable table = dataTableDao.getDataTableByEntity(entityClass.getName());
		List<Permission> ps = permissionDao.getPermissionsByDataTableId(table.getId());
		return getDataRuleByPermission(ps,authority,null,authlink);
	}
	
	/**
	 * 根据操作查询有权限的规则
	 * @param authority 保存,修改,删除
	 * @return
	 */
	protected PermissionInfo getAuthorityDataRule(PermissionAuthorize authority,T entity,LogicOperator authlink){
		String className = entity.getClass().getName();
		if(className.contains("_")){
			className =className.substring(0,className.indexOf("_"));
		}
		DataTable table = dataTableDao.getDataTableByEntity(className);
		List<Permission> ps = permissionDao.getPermissionsByDataTableId(table.getId());
		return  getDataRuleByPermission(ps,authority,entity,authlink);
	}
	
	/**
	 * 根据操作查询有权限的规则
	 * @param authority 查询, 修改, 删除
	 * @return
	 */
	public PermissionInfo getAuthorityDataRule(PermissionAuthorize authority,List<String> permissionCodes,T entity,LogicOperator authlink){
		List<Permission> ps = new ArrayList<Permission>();
		for (String permissionCode : permissionCodes) {
			ps.add(permissionDao.getPermissionsByCode(permissionCode));
		}
		return getDataRuleByPermission(ps,authority,entity,authlink);
	}
	
	/**
	 * 根据数据授权，获取数据类别
	 * @param ps
	 * @param authority
	 * @param table
	 * @return
	 */
	private PermissionInfo getDataRuleByPermission(List<Permission> ps,PermissionAuthorize authority,T entity,LogicOperator authlink) {
		List<DataRuleResult> rules = new ArrayList<DataRuleResult>();
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		Long userId = ContextUtils.getUserId();
		List<Long> deptIds = acsService.getDepartmentIds(userId);
		Set<String> userResult = new HashSet<String>();
		
		boolean hasPermission = false;
		boolean noPermission = false;//是否是没有授权,没有授权时,不受权限控制
		if(ps.size()<=0){
			noPermission = true;
		}else{
			StringBuilder userExpress = new StringBuilder();
			for(int i=0;i<ps.size();i++){
				Permission p = ps.get(i);
				if((p.getAuthority() & authority.getCode()) == 0){
					if(authlink.toString().equals("AND")){
						if(userExpress.toString().lastIndexOf("AND ")==userExpress.toString().length()-4 ){
							String express = userExpress.substring(0,userExpress.toString().lastIndexOf("AND "));
							userExpress = new StringBuilder(express+" ");
						}
					}else{
						if(userExpress.toString().lastIndexOf("OR ")==userExpress.toString().length()-3 ){
							String express = userExpress.substring(0,userExpress.toString().lastIndexOf("OR "));
							userExpress = new StringBuilder(express+" ");
						}
					}
					continue;
				}
				List<PermissionItem> items = p.getItems();
				String userCondition = PermissionUtils.getUserConditionExpress(items);
				if(StringUtils.isNotEmpty(userCondition)){
					userExpress.append("(")
					.append(userCondition)
					.append(")");
				}
				if(i<ps.size()-1){
					userExpress.append(" ").append(authlink.toString()).append(" ");
				}
				rules.add(new DataRuleResult(p.getDataRule(),p));
			}
			if(StringUtils.isNotEmpty(userExpress.toString())){
				userResult = PermissionUtils.getUsers(userExpress.toString());
				if(userResult.contains(userId.toString())){//如果包含当前用户
					hasPermission=true;
				}
			}
		}
		return new PermissionInfo(hasPermission,rules,userId,PermissionUtils.getDirectLeader(),deptIds,userResult,noPermission);
	}
	
	static class AuthorityResult{
		PermissionInfo permissionInfo;
		boolean result;
		public AuthorityResult(PermissionInfo permissionInfo, boolean result) {
			this.permissionInfo = permissionInfo;
			this.result = result;
		}
		public AuthorityResult(){}
	}
	
	public static class ConditionResult{
		private String hql;
		private Object[] prameters;
		public String getHql() {
			return hql;
		}
		public void setHql(String hql) {
			this.hql = hql;
		}
		public Object[] getPrameters() {
			return prameters;
		}
		public void setPrameters(Object[] prameters) {
			this.prameters = prameters;
		}
	}
}
