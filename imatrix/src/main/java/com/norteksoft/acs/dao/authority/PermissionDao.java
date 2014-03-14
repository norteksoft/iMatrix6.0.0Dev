package com.norteksoft.acs.dao.authority;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class PermissionDao extends HibernateDao<Permission, Long>{
	public void getPermissions(Page<Permission> page,Long dataRuleId){
		this.searchPageByHql(page, "from Permission p where p.dataRule.id=?", dataRuleId);
	}
	
	public void getPermissionsByMenuId(Page<Permission> page,Long menuId,Boolean fast){
		this.searchPageByHql(page, "from Permission p where p.menuId=? and p.fastable=?", menuId,fast);
	}
	
	public List<Permission> getPermissionsByDataRule(Long dataRuleId){
		return this.find("from Permission p where p.dataRule.id=?", dataRuleId);
	}
	public List<Permission> getAllPermissions(){
		return this.find("from Permission p where p.companyId=?", ContextUtils.getCompanyId());
	}
	
	public List<Permission> getPermissionsByDataTableId(Long dataTableId){
			return this.find("select p from Permission p join p.dataRule dr where dr.dataTableId=? and p.companyId=? order by priority desc", 
					dataTableId, ContextUtils.getCompanyId());
	}
	
	public List<Permission> getPermissionsByListViewId(Long listViewId){
		return this.find("select p from Permission p join p.dataRule dr where p.companyId=? and p.listViewId = ? order by priority desc", 
				ContextUtils.getCompanyId(),listViewId);
	}

	/**
	 * 根据数据规则id删除数据授权
	 * @param valueOf
	 */
	public void deletePermissionByDataRuleId(Long dataRuleId) {
		this.batchExecute("delete Permission c where c.companyId=? and c.dataRule.id=? ", ContextUtils.getCompanyId(),dataRuleId);
	}

	public Permission getPermissions(Integer priority, Integer authority, Long dataRuleId) {
		List<Permission> permissions=this.find("from Permission p where p.companyId=? and p.priority=? and p.authority=? and p.dataRule.id=? ",ContextUtils.getCompanyId(),priority,authority,dataRuleId);
		if(permissions!=null&&permissions.size()>0){
			return permissions.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 根据系统id获得数据授权
	 * @param split
	 * @return
	 */
	public List<Permission> getPermissionsBySystemId(String[] systemIds) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Permission p where p.companyId=? ");
		if(systemIds.length>0){
			hql.append(" and (");
			String condition="";
			for(String systemId:systemIds){
				if(StringUtils.isNotEmpty(condition)){
					condition+=" or ";
				}
				condition+="p.dataRule.systemId="+systemId;
			}
			hql.append(condition);
			hql.append(" )");
		}
		return this.find(hql.toString(), ContextUtils.getCompanyId());
	}

	/**
	 * 根据编码获得授权
	 * @param code
	 * @return
	 */
	public Permission getPermissionsByCode(String code) {
		StringBuilder hql=new StringBuilder();
		hql.append("from Permission p where p.companyId=? and p.code=? ");
		return this.findUnique(hql.toString(),ContextUtils.getCompanyId(),code);
	}
	
	public List<DataRule> getDataRuleByPermission(Long permissionId){
		String hql = "select d from Permission p join p.dataRule d where p.companyId=? and p.id=?";
		return this.find(hql,ContextUtils.getCompanyId(),permissionId);
	}
	
	public List<Permission> getDefaultCodePermissions(){
		String hql = "from Permission p where p.companyId=? and p.code like 'dataAuth-%' " ;
		return this.find(hql, ContextUtils.getCompanyId());
	}
	/**
	 * 根据数据分类获得数据授权
	 * @param page
	 * @param dataRuleId
	 * @param fast
	 */
	public void getPermissionPageByDataRule(Page<Permission> page,Long dataRuleId){
		this.searchPageByHql(page, "from Permission p where p.dataRule.id=? and p.fastable=?", dataRuleId,false);
	}
	
}
