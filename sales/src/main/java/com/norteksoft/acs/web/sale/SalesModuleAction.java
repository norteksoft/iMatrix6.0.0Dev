package com.norteksoft.acs.web.sale;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.authorization.FunctionManager;
import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sale.SalesModule;
import com.norteksoft.acs.service.sale.SalesModuleManager;
import com.norteksoft.acs.web.authorization.JsTreeUtil;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.CollectionUtils;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/sales-module.action", type="redirect") })
public class SalesModuleAction extends CRUDActionSupport<SalesModule>{

	private static final long serialVersionUID = 1L;
	private SalesModuleManager salesModuleManager;
	private Page<SalesModule> page = new Page<SalesModule>(Page.EACH_PAGE_TWENTY,true);
	private SalesModule entity;
	private FunctionManager functionManager;
	private List<Function> allFunctions;
	private List<Long> checkedFunctionIds;
	private List<Long> functionIds;
	private List<BusinessSystem> allSystems;
	private BusinessSystemManager businessSystemManager;
	private Long systemId;
	private Long id;
    private String sysName;
    private String currentId;
    private String ids;
    private String functionids;
	@Override
	public String delete() throws Exception {
		salesModuleManager.deleteSalesModule(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		page = salesModuleManager.getAllSalesMdule(page);
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = salesModuleManager.getSalesModule(id);
			checkedFunctionIds = CollectionUtils.fetchPropertyToList(entity.getFunctions(), "id");
			allFunctions = functionManager.getFunctionsBySystem(entity.getSystemId());
			allSystems = new ArrayList<BusinessSystem>();
			BusinessSystem bs = businessSystemManager.getBusiness(entity.getSystemId());
			allSystems.add(bs);
		}else{
			entity = new SalesModule();
			allSystems = businessSystemManager.getAllSystem();
		}
	}

	@Override
	public String input() throws Exception {
		functionids="";
		if(checkedFunctionIds!=null){
		for(int i=0;i<checkedFunctionIds.size();i++){
			functionids+=checkedFunctionIds.get(i)+",";
		}
		}
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		functionIds=getFunctionsIds(ids);
		CollectionUtils.mergeByCheckedIds(entity.getFunctions(), functionIds, Function.class);
		salesModuleManager.saveSalesModule(entity);
		return RELOAD;
	}

	public SalesModule getModel() {
		return entity;
	}

	@Required
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}

	@Required
	public void setSalesModuleManager(SalesModuleManager salesModuleManager) {
		this.salesModuleManager = salesModuleManager;
	}

	public List<Long> getCheckedFunctionIds() {
		return checkedFunctionIds;
	}

	public void setCheckedFunctionIds(List<Long> checkedFunctionIds) {
		this.checkedFunctionIds = checkedFunctionIds;
	}

	public List<Long> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(List<Long> functionIds) {
		this.functionIds = functionIds;
	}

	public List<Function> getAllFunctions() {
		return allFunctions;
	}

	public void setAllFunctions(List<Function> allFunctions) {
		this.allFunctions = allFunctions;
	}

	public Page<SalesModule> getPage() {
		return page;
	}

	public void setPage(Page<SalesModule> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public List<BusinessSystem> getAllSystems() {
		return allSystems;
	}

	public void setAllSystems(List<BusinessSystem> allSystems) {
		this.allSystems = allSystems;
	}
	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	
	public String getFunctionids() {
		return functionids;
	}

	public void setFunctionids(String functionids) {
		this.functionids = functionids;
	}

	@Required
	public void setBusinessSystemManager(BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	
	public String getFunctions() throws Exception{
		allFunctions = functionManager.getFunctionsBySystem(systemId);
		return INPUT;
	}

	public String sysFuntionsTree() throws UnsupportedEncodingException{
		BusinessSystem system=businessSystemManager.getBusiness(systemId);
		sysName=system.getName();
		return renderText(getSysFuntionsTree(systemId,sysName,currentId));
	}
	
	public String getSysFuntionsTree(Long systemId,String systemName,String currentId){
		StringBuffer tree=new StringBuffer();
		
		if (currentId.equals("0")) {
		tree.append(defaultTree(systemName,systemId));
		}
		return tree.toString();
	}
	
	
	
	public String defaultTree(String systemName ,Long systemId){
		StringBuilder tree = new StringBuilder();
		tree.append("[");
		tree.append("{attributes:{id:\"system_"+systemId+"="+systemName+"\"},state:\"closed\",data:\""+systemName+"\",children:[");
		tree.append(delComma(functionGroupTree(systemId)));
		tree.append("]}");
		tree.append("]");
		return tree.toString();
		
	}
	
	public  String functionGroupTree(Long systemId){
		StringBuilder tree = new StringBuilder();
		allFunctions = functionManager.getFunctionsBySystem(systemId);
		for(Function function:allFunctions){
			tree.append(JsTreeUtil.generateJsTreeNode("function_" +function.getId()+ "="+ function.getName(), "", function.getName())).append(",");
		}
		tree.append("");
	
		return tree.toString();
	}
	
	/**
	 * 去逗号
	 * @param str
	 * @return
	 */
	public  String delComma(String str){
		if(StringUtils.endsWith(str, ","))str= str.substring(0,str.length() - 1);
		return str;
	}
	
	public List<Long> getFunctionsIds(String ids){
		List<Long> fIds=new ArrayList<Long>();
		String[] Ids=ids.split(",");
		for(int i=0;i<Ids.length;i++){
			fIds.add(Long.parseLong(Ids[i]));
		}
		return fIds;
		
	}
	
}
