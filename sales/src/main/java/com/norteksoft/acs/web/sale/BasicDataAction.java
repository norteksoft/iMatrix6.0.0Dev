package com.norteksoft.acs.web.sale;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.sale.BasicDataManager;
import com.norteksoft.acs.service.sale.ImportDataManager;
import com.norteksoft.product.util.PropUtils;

@SuppressWarnings("unchecked")
@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/basic-data.action", type = "redirect"),
	@Result(name = "showImport", location = "/sale/basic-data!showImportData.action", type = "redirect"),
	@Result(name = "showInit", location = "/sale/basic-data!showInitData.action?result=${result}", type = "redirect")})
public class BasicDataAction extends CRUDActionSupport {
	private Log log = LogFactory.getLog(BasicDataAction.class);
	private static final long serialVersionUID = 1L;
	private ImportDataManager importDataManager;
	private File file;
	private List<BusinessSystem> systems;
	private BusinessSystemManager businessSystemManager;
	private String systemIds;//系统id的集合，以逗号隔开
	private String dataCodes;//数据类型
	private String fileName;//文件名称
	private String imatrixIp;//应用平台部署ip地址
	private String imatrixPort;//应用平台部署的端口号
	private String imatrixName;//应用名称，默认为imatrix
	private List<Company> companys;//公司列表
	
	private boolean showFlag=false;//是否显示公司列表，当只有一个公司时不显示
	private Long companyId;//公司id
	
	private BasicDataManager basicDataManager;
	private CompanyManager companyManager;
	private String result;
	
	private List<FileConfigModel> basicDataTypes;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	

	@Override
	public String save() throws Exception {
//		importDataManager.saveFileData(file, tableName);
//		addActionMessage(tableName + "导入完成");
		return RELOAD;
	}
	
	public String exportData() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("basic-data.zip","utf-8"));
		basicDataManager.exportBasicData(response.getOutputStream(), systemIds, dataCodes,companyId);
		return null;
	}
	
	public String showImportData() throws Exception{
		return "show-import";
	}
	
	public String importData(){
		if(fileName==null || !fileName.endsWith(".zip")){
			this.renderText("请选择zip文件格式");
			return null;
		}
		boolean success = true;
		try {
			basicDataManager.importBasicData(file,imatrixIp,imatrixPort,imatrixName);
		} catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
			success = false;
		}
		if(success){
			this.renderText("导入成功");
		}else{
			this.renderText("导入失败，请检查zip文件格式");
		}
		return null;
	}
	
	public String showInitData() throws Exception{
		companys=companyManager.getCompanys();
		if(companys.size()>1){
			showFlag=true;
		}
		if(companys.size()==1){
			companyId=companys.get(0).getId();
		}
		return "init";
	}
	
	public String initData() throws Exception{
		if(fileName==null || !fileName.endsWith(".zip")){
			result="noZip";//"请选择zip文件格式"
			return "showInit";
		}
		boolean success = true;
		try {
			basicDataManager.initData(file,companyId);
		} catch (Exception e) {
			success = false;
		}
		if(success){
			result="success";//初始化成功
		}else{
			result="zipError";//导入失败，请检查zip文件格式
		}
		return  "showInit";
	}

	@Required
	public void setImportDataManager(ImportDataManager importDataManager) {
		this.importDataManager = importDataManager;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String delete() throws Exception {
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
		systems=businessSystemManager.getAllSystems();
		basicDataTypes=basicDataManager.getBasicDataTypes();
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		
	}

	public Object getModel() {
		return null;
	}

	public List<BusinessSystem> getSystems() {
		return systems;
	}
	@Autowired
	public void setBusinessSystemManager(
			BusinessSystemManager businessSystemManager) {
		this.businessSystemManager = businessSystemManager;
	}
	@Autowired
	public void setBasicDataManager(BasicDataManager basicDataManager) {
		this.basicDataManager = basicDataManager;
	}
	@Autowired
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	public void setSystemIds(String systemIds) {
		this.systemIds = systemIds;
	}

	public void setDataCodes(String dataCodes) {
		this.dataCodes = dataCodes;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setImatrixIp(String imatrixIp) {
		this.imatrixIp = imatrixIp;
	}

	public void setImatrixPort(String imatrixPort) {
		this.imatrixPort = imatrixPort;
	}
	public List<Company> getCompanys() {
		return companys;
	}
	public boolean isShowFlag() {
		return showFlag;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setImatrixName(String imatrixName) {
		this.imatrixName = imatrixName;
	}
	public List<FileConfigModel> getBasicDataTypes() {
		return basicDataTypes;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
