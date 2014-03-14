package com.norteksoft.acs.service.authority;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.utils.PermissionUtils;
import com.norteksoft.acs.dao.authority.ConditionDao;
import com.norteksoft.acs.dao.authority.DataRuleDao;
import com.norteksoft.acs.entity.authority.DataRuleResult;
import com.norteksoft.acs.dao.authority.PermissionDao;
import com.norteksoft.acs.dao.authority.PermissionValidator.ConditionResult;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionInfo;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.bs.options.dao.OptionGroupDao;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Service
@Transactional
public class DataRuleManager {
	@Autowired
	private DataRuleDao dataRuleDao;
	@Autowired
	private ConditionDao conditionDao;
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private TableColumnDao tableColumnDao;
	@Autowired
	private PermissionDao permissionDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private OptionGroupDao optionGroupDao;
	@Autowired
	private PermissionItemConditionManager permissionItemConditionManager;
	
	private final String NO_VALUE_STRING= "NO_VALUE_#~_+~=%";
	private static final String BACK_VALUE_FLAG="~~~~";//表示是在弹框中回填的值

	/**
	 * 根据id获得数据规则
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRule(Long id) {
		return dataRuleDao.get(id);
	}

	/**
	 * 获得所有数据规则
	 * @param page
	 */
	@Transactional(readOnly=true)
	public void getDataRulePage(Page<DataRule> page) {
		dataRuleDao.getDataRulePage(page);
	}

	/**
	 * 保存数据规则
	 * @param dataRule
	 */
	public void saveDataRule(DataRule dataRule) {
		Long dataTableId = dataRule.getDataTableId();
		if(dataTableId!=null){
			DataTable table = dataTableDao.get(dataTableId);
			if(table!=null){
				Long menuId = table.getMenuId();
				Menu menu=menuDao.getMenu(menuId);
				if(menu != null)
					dataRule.setSystemId(menu.getSystemId());
			}
		}
		dataRuleDao.save(dataRule);
		//删除规则条件项
		permissionItemConditionManager.deleteAllDataRuleItemConditions(dataRule.getId());
		//更新数据分类对应的规则条件
		if(!dataRule.getSimplable()){
			List<Condition> conditions=new ArrayList<Condition>();
			List<Object> objects=JsonParser.getFormTableDatas(Condition.class);
			for(Object obj:objects){
				Condition condition=(Condition)obj;
				condition.setDataRule(dataRule);
				conditionDao.save(condition);
				conditions.add(condition);
				//保存数据分类条件集合
				saveConditionValue(condition.getConditionName(), condition.getConditionValue(), condition.getId(),condition.getDataType());
			}
			if(conditions.size()>0){
				dataRule.setConditions(conditions);
			}
			deleteConditionByRuleId(dataRule,conditions);
		}else{
			//删除规则
			conditionDao.deleteConditionByRuleId(dataRule.getId());
		}
	}
	
	private void deleteConditionByRuleId(DataRule dataRule,List<Condition> currentCons){
		//删除规则
		List<Condition> cons = conditionDao.getConditionsByDataRuleId(dataRule.getId());
		List<Long> deleteIds = new ArrayList<Long>();
		for(Condition con:cons){
			if(!currentCons.contains(con)){
				deleteIds.add(con.getId());
			}
		}
		conditionDao.deleteConditionByConIds(deleteIds);
	}
	
	/**
	 * 保存数据分类规则条件
	 * @param conditionName:以逗号隔开的字符串
	 * @param conditionValue:以逗号隔开的字符串
	 */
	public void saveConditionValue(String conditionName,String conditionValue,Long dataId,DataType dataType){
		if(dataType==DataType.TEXT||dataType==DataType.ENUM||dataType==DataType.BOOLEAN||dataType==DataType.LONG){//因为这几种情况有弹框设置的情况
			if(StringUtils.isNotEmpty(conditionValue)&&conditionValue.contains(BACK_VALUE_FLAG)){//表示是在弹框中回填的值,conditionValue以回填的值为准
				conditionValue=conditionValue.substring(0, conditionValue.indexOf(BACK_VALUE_FLAG));
			}
		}else{//conditionValue以conditionName的值为准
			conditionValue=conditionName;
		}
		if(StringUtils.isNotEmpty(conditionValue)){
			if(dataType==DataType.TEXT){//文本类型时手动输入了逗号，不应再截取
				saveDataRuleItems(new String[]{conditionValue},new String[]{conditionName},dataId);
			}else{
				String[] values = conditionValue.split(",");
				String[] names = conditionName.split(",");
				saveDataRuleItems(values,names,dataId);
			}
			
		}
	}
	
	private void saveDataRuleItems(String[] values,String[] names,Long dataId){
		for(int i=0;i<values.length;i++){
			String val = values[i];
			if(StringUtils.isNotEmpty(val)){
				PermissionItemCondition itemCon = new PermissionItemCondition();
				itemCon.setConditionName(names[i]);
				itemCon.setConditionValue(val);
				itemCon.setDataId(dataId);
				itemCon.setValueType(ConditionValueType.DATA_RULE);
				permissionItemConditionManager.save(itemCon);
			}
		}
	}
	
	
	public List<DataRule> getDataRuleByDataTable(Long tableId){
		return dataRuleDao.getDataRuleByDataTable(tableId);
	}

	/**
	 * 删除数据规则且该规则下的所有条件
	 * @param ids
	 */
	public void deleteDataRule(String ids) {
		for(String id:ids.split(",")){
			List<Permission> list = permissionDao.getPermissionsByDataRule(Long.valueOf(id));
			for(Permission p:list){
				p.setDataRule(null);
				permissionDao.delete(p);
			}
			//删除规则条件项
			permissionItemConditionManager.deleteAllDataRuleItemConditions(Long.valueOf(id));
			//删除规则条件
			conditionDao.deleteConditionByRuleId(Long.valueOf(id));
			//删除数据分类
			dataRuleDao.delete(Long.valueOf(id));
		}
	}

	/**
	 * 根据编号获得规则
	 * @param code
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRuleByCode(String code) {
		return dataRuleDao.getDataRuleByCode(code);
	}

	/**
	 * 根据编号和ID获得编号相同且ID不同的规则
	 * @param code
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public DataRule getDataRuleByCode(String code, Long id) {
		return dataRuleDao.getDataRuleByCode(code,id);
	}

	/**
	 * 获得所有启用的数据表
	 * @return
	 */
	public void findAllEnabledDataTable(Page<DataTable> page) {
		dataTableDao.findAllEnabledDataTable(page);
	}

	/**
	 * 根据数据表id获得字段
	 * @param tableColumnPage
	 * @param tableId
	 */
	public void getTableColumnByDataTableId(Page<TableColumn> tableColumnPage,Long dataTableId) {
		tableColumnDao.getTableColumnByDataTableId(tableColumnPage, dataTableId);
	}
	
	/**
	 * 根据规则类型查询数据规则
	 * @param ruleTypeId
	 * @return
	 */
	public List<DataRule> getDataRulesByRuleType(Long ruleTypeId){
		return dataRuleDao.getDataRulesByRuleType(ruleTypeId);
	}

	/**
	 * 根据规则类型查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByRuleType(Page<DataRule> page, Long ruleTypeId) {
		dataRuleDao.getDataRulesByRuleType(page,ruleTypeId);
	}
	
	/**
	 * 根据菜单查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public void getDataRulesByMenuId(Page<DataRule> page, Long menuId) {
		dataRuleDao.getDataRulesByMenuId(page,menuId);
	}
	
	/**
	 * 根据菜单查询数据规则
	 * @param page
	 * @param ruleTypeId
	 */
	public List<DataRule> getDataRulesByMenuId(Long menuId) {
		return  dataRuleDao.getDataRulesByMenuId(menuId);
	}
	/**
	 * 验证删除
	 * @param ids
	 * @return
	 */
	public String validateDelete(String ids) {
		String result="";
		for(String id:ids.split(",")){
			List<Permission> permissions=permissionDao.getPermissionsByDataRule(Long.valueOf(id));
			if(permissions != null && permissions.size()>0){
				DataRule dataRule=dataRuleDao.get(Long.valueOf(id));
				if(StringUtils.isNotEmpty(result))
					result+="、";
				result+=dataRule.getName();
			}
		}
		if(StringUtils.isNotEmpty(result))
			result="名称为："+result+" 的数据规则中有数据授权，确定删除吗？";
		return result;
	}
	/**
	 * 根据menuId获得启用的数据表
	 * @return
	 */
	public List<DataTable> getEnabledStandardDataTableByMenuId(Long menuId) {
		return  dataTableDao.getEnabledStandardDataTableByMenuId(menuId);
	}
	
	public ConditionResult getConditionResult(String hql,List<String> ruleCodes, LogicOperator link,Object... prmts) {
		List<DataRuleResult> rules = new ArrayList<DataRuleResult>();
		for (String code : ruleCodes) {
			DataRule r = dataRuleDao.getDataRuleByCode(code);
			DataRuleResult ruleResult = new DataRuleResult(r, null);
			if(r!=null&&r.getSimplable()){
				rules.add(ruleResult);
			}else{
				if(r!=null&& r.getConditions().size()>0) rules.add(ruleResult);
			}
		}
		AcsServiceImpl acsService = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
		Long userId = ContextUtils.getUserId();
		List<Long> deptIds = acsService.getDepartmentIds(userId);
		PermissionInfo permissionInfo = new PermissionInfo(userId, PermissionUtils.getDirectLeader(), deptIds, rules);
		return PermissionUtils.getPermissionHqlPamateters(hql, permissionInfo,link, prmts);
	}
	public String getSystemUrlByTalbeId(Long dataTableId) {
		DataTable dt = dataTableDao.get(dataTableId);
		Menu menu = menuDao.getMenu(dt.getMenuId());
		return SystemUrls.getSystemUrl(menu.getCode());
	}

	public String getOptionValue(String dataValue) {
		StringBuilder result = new StringBuilder();
		OptionGroup optionGroup = optionGroupDao.getOptionGroupByCode(dataValue);
		for (Option option : optionGroup.getOptions()) {
			result.append(option.getValue()).append(":").append(option.getName()).append(",");
		}
		return result.toString();
	}

	public void addConditionResult(String hql, List<String> dataRuleCodes,
			LogicOperator link, Object[] values) {
		ConditionResult cr = getConditionResult(hql, dataRuleCodes, link, values);
		Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_HQL, cr.getHql());
		Struts2Utils.getRequest().setAttribute(PermissionUtils.PERMISSION_PARAMETERS, cr.getPrameters());
	}
	
	/**
	 * 保存数据规则
	 * @param dataRule
	 */
	public void saveRule(DataRule dataRule) {
		dataRuleDao.save(dataRule);
	}
	/**
	 * 删除数据规则
	 * @param dataRule
	 */
	public void deleteRule(DataRule dataRule){
		dataRuleDao.delete(dataRule);
	}

	/**
	 * 获得数据分类编码为默认编码的所有数据分类
	 * @return
	 */
	public List<DataRule> getDefaultCodeDataRules() {
		return dataRuleDao.getDefaultCodeDataRules();
	}
}
