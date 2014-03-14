package com.norteksoft.acs.dao.authority;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

/**
 * 数据规则
 * @author Administrator
 *
 */
@Repository
public class DataRuleDao extends HibernateDao<DataRule, Long>{
	/**
	 * 获得所有数据规则
	 * @param page
	 */
	public void getDataRulePage(Page<DataRule> page) {
		this.searchPageByHql(page, "from DataRule d where d.companyId=? ", ContextUtils.getCompanyId());
	}
	
	public List<DataRule> getDataRuleByDataTable(Long tableId){
		return this.find("from DataRule d where d.dataTableId=?  ", tableId);
	}
	
	public List<DataRule> getAllDataRule(){
		return this.find("from DataRule d where d.companyId=?  ", ContextUtils.getCompanyId());
	}

	/**
	 * 根据编号获得规则
	 * @param code
	 * @return
	 */
	public DataRule getDataRuleByCode(String code) {
		return this.findUnique("from DataRule d where d.companyId=? and d.code=? ",ContextUtils.getCompanyId(),code);
	}

	/**
	 * 根据编号和ID获得编号相同且ID不同的规则
	 * @param code
	 * @param id
	 * @return
	 */
	public DataRule getDataRuleByCode(String code, Long id) {
		return this.findUnique("from DataRule d where d.companyId=? and d.code=? and d.id <> ? ",ContextUtils.getCompanyId(),code,id);
	}
	/**
	 * 根据规则类型查询数据规则
	 * @param ruleTypeId
	 * @return
	 */
	public List<DataRule> getDataRulesByRuleType(Long ruleTypeId){
		return this.find("from DataRule d where d.ruleTypeId=?  ", ruleTypeId);
	}

	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByRuleType(Page<DataRule> page, Long ruleTypeId) {
		this.searchPageByHql(page, "from DataRule d where d.companyId=? and d.ruleTypeId=? ", ContextUtils.getCompanyId(),ruleTypeId);
	}
	
	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByMenuId(Page<DataRule> page, Long menuId) {
		this.searchPageByHql(page, "from DataRule d where d.companyId=? and d.menuId=? and d.fastable=?", ContextUtils.getCompanyId(),menuId,false);
	}
	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public List<DataRule> getDataRulesByMenuId(Long menuId) {
		return this.find("from DataRule d where d.companyId=? and d.menuId=? and d.fastable=?", ContextUtils.getCompanyId(),menuId,false);
	}
	/**
	 * 根据系统id获得数据规则
	 * @param systemIds
	 * @return
	 */
	public List<DataRule> getDataRuleBySystemId(String[] systemIds) {
		StringBuilder hql=new StringBuilder();
		hql.append("from DataRule d where d.companyId=? ");
		if(systemIds.length>0){
			hql.append(" and (");
			String condition="";
			for(String systemId:systemIds){
				if(StringUtils.isNotEmpty(condition)){
					condition+=" or ";
				}
				condition+="d.systemId="+systemId;
			}
			hql.append(condition);
			hql.append(" )");
		}
		return this.find(hql.toString(), ContextUtils.getCompanyId());
	}

	/**
	 * 获得数据分类编码为默认编码的所有数据分类
	 * @return
	 */
	public List<DataRule> getDefaultCodeDataRules() {
		return this.find("from DataRule d where d.companyId=? and d.code like 'dataRule-%' ", ContextUtils.getCompanyId());
	}
	
	

}
