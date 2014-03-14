package com.norteksoft.acs.web.sale;

import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.file.DataMoveManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.product.util.ContextUtils;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/data-move.action", type="redirect") })
public class DataMoveAction extends CRUDActionSupport{
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	private CompanyManager companyManager;
	private List<Company> companys;//公司列表
	
	private boolean showFlag=false;//是否显示公司列表，当只有一个公司时不显示
	private Long companyId;//公司id

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		companys=companyManager.getCompanys();
		if(companys.size()>1){
			showFlag=true;
		}
		if(companys.size()==1){
			companyId=companys.get(0).getId();
		}
		return SUCCESS; 
	}
	
	public String dataMove() throws Exception{
		DataMoveManager dmm=(DataMoveManager)ContextUtils.getBean("dataMoveManager");
		dmm.dataMove(companyId);
		this.addActionSuccessMessage("迁移成功!");
		return list();
	}
	
	public String dataInsert() throws Exception{
		DataMoveManager dmm=(DataMoveManager)ContextUtils.getBean("dataMoveManager");
		dmm.dataInsert();
		this.addActionSuccessMessage("导入成功!");
		return list();
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void addActionSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Autowired
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}
	
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	
	public boolean isShowFlag() {
		return showFlag;
	}
	public List<Company> getCompanys() {
		return companys;
	}
}
