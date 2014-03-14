package com.norteksoft.acs.service.authority;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.FieldOperator;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.acs.base.enumeration.UserOperator;
import com.norteksoft.acs.dao.authority.ConditionDao;
import com.norteksoft.acs.dao.authority.DataRuleDao;
import com.norteksoft.acs.dao.authority.PermissionDao;
import com.norteksoft.acs.dao.authority.PermissionItemConditionDao;
import com.norteksoft.acs.dao.authority.PermissionItemDao;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.DataSheetConfig;
import com.norteksoft.mms.base.data.DataTransfer;
import com.norteksoft.mms.base.data.FileConfigModel;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.BusinessSystem;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class importAuthorityManager implements DataTransfer {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private DataHandle dataHandle;
	@Autowired
	private DataRuleDao dataRuleDao;
	@Autowired
	private DataTableDao dataTableDao;
	@Autowired
	private ConditionDao conditionDao;
	@Autowired
	private PermissionDao permissionDao;
	@Autowired
	private PermissionItemDao permissionItemDao;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private PermissionItemConditionDao permissionItemConditionDao;
	
	public void backup(String systemIds, Long companyId,FileConfigModel fileConfig) {
		try {
			ThreadParameters parameters=new ThreadParameters(companyId, null);
			ParameterUtils.setParameters(parameters);
			String path=fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/";
			File file = new File(path+fileConfig.getFilename()+".xls");
			OutputStream out=null;
			out=new FileOutputStream(file);
			if("ACS_DATA_RULE".equals(fileConfig.getData())){
				exportDataRule(out,systemIds);
			}else if("ACS_PERMISSION".equals(fileConfig.getData())){
				exportPermission(out,systemIds);
			}
			
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}

	private void exportPermission(OutputStream fileOut,String systemIds) {
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PERMISSION']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PERMISSION_ITEM']");
		wb = new HSSFWorkbook();
		
		HSSFSheet sheet=wb.createSheet("ACS_PERMISSION");
        HSSFRow row = sheet.createRow(0);
        dataHandle.getFileHead(wb,row,confs);
        
        HSSFSheet conditionSheet=wb.createSheet("ACS_PERMISSION_ITEM");
        HSSFRow conditionRow = conditionSheet.createRow(0);
        dataHandle.getFileHead(wb,conditionRow,conditionConfs);
        List<Permission> permissions=new ArrayList<Permission>();
        if(StringUtils.isEmpty(systemIds)){
        	permissions=permissionDao.getAllPermissions();
        }else{
        	permissions=permissionDao.getPermissionsBySystemId(systemIds.split(","));
        }
        for(Permission permission:permissions){
        	permissionInfo(permission,sheet,conditionSheet,confs,conditionConfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void permissionInfo(Permission permission, HSSFSheet sheet,HSSFSheet conditionSheet, List<DataSheetConfig> confs,List<DataSheetConfig> conditionConfs) {
		if(permission != null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					if("dataRuleCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permission.getDataRule().getCode());
					}else if("menuCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						Menu menu=menuDao.get(permission.getMenuId());
						if(menu!=null){
							cell.setCellValue(menu.getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
						dataHandle.setFieldValue(conf,i,rowi,permission);
					}
				}
			}
			permissionItemInfo(permission,conditionSheet,conditionConfs);
		}
	}

	private void permissionItemInfo(Permission permission,HSSFSheet conditionSheet, List<DataSheetConfig> conditionConfs) {
		List<PermissionItem> permissionItems=permissionItemDao.getAllPermissionItems(permission.getId());
		for(PermissionItem permissionItem:permissionItems){
			HSSFRow rowi = conditionSheet.createRow(conditionSheet.getLastRowNum()+1);
			for(int i=0;i<conditionConfs.size();i++){
				DataSheetConfig conf=conditionConfs.get(i);
				if(!conf.isIgnore()){
					if("conditionValue".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						if(ItemType.USER.equals(permissionItem.getItemType())){
							cell.setCellValue(permissionItem.getConditionValue());
						}else if(ItemType.DEPARTMENT.equals(permissionItem.getItemType())){
							Department department=ApiFactory.getAcsService().getDepartmentById(Long.valueOf(permissionItem.getConditionValue()));
							cell.setCellValue(department.getCode());
						}else if(ItemType.ROLE.equals(permissionItem.getItemType())){
							cell.setCellValue(permissionItem.getConditionValue());
						}else if(ItemType.WORKGROUP.equals(permissionItem.getItemType())){
							Workgroup workgroup=ApiFactory.getAcsService().getWorkgroupById(Long.valueOf(permissionItem.getConditionValue()));
							cell.setCellValue(workgroup.getCode());
						}
					}else if("permissionCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(permissionItem.getPermission().getCode());
					}else{
						dataHandle.setFieldValue(conf,i,rowi,permissionItem);
					}
				}
			}
		}
		
	}

	private void exportDataRule(OutputStream fileOut,String systemIds) {
		HSSFWorkbook wb;
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_DATA_RULE']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_CONDITION']");
		wb = new HSSFWorkbook();
		
		HSSFSheet sheet=wb.createSheet("ACS_DATA_RULE");
        HSSFRow row = sheet.createRow(0);
        dataHandle.getFileHead(wb,row,confs);
        
        HSSFSheet conditionSheet=wb.createSheet("ACS_CONDITION");
        HSSFRow conditionRow = conditionSheet.createRow(0);
        dataHandle.getFileHead(wb,conditionRow,conditionConfs);
        List<DataRule> dataRules=new ArrayList<DataRule>();
        if(StringUtils.isEmpty(systemIds)){
	        dataRules=dataRuleDao.getAllDataRule();
        }else{
        	dataRules=dataRuleDao.getDataRuleBySystemId(systemIds.split(","));
        }
        for(DataRule dataRule:dataRules){
        	dataRuleInfo(dataRule,sheet,conditionSheet,confs,conditionConfs);
        }
        try {
			wb.write(fileOut);
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
			if(fileOut!=null)
				try {
					fileOut.close();
				} catch (IOException e) {
					log.debug(PropUtils.getExceptionInfo(e));
				}
		}
	}

	private void dataRuleInfo(DataRule dataRule, HSSFSheet sheet,HSSFSheet conditionSheet, List<DataSheetConfig> confs,List<DataSheetConfig> conditionConfs) {
		if(dataRule != null){
			HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
			for(int i=0;i<confs.size();i++){
				DataSheetConfig conf=confs.get(i);
				if(!conf.isIgnore()){
					if("dataTableName".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						DataTable dataTable=dataTableDao.get(dataRule.getDataTableId());
						if(dataTable!=null){
							cell.setCellValue(dataTable.getName());
						}else{
							cell.setCellValue("");
						}
					}else if("menuCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						Menu menu=menuDao.get(dataRule.getMenuId());
						if(menu!=null){
							cell.setCellValue(menu.getCode());
						}else{
							cell.setCellValue("");
						}
					}else if("systemCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						BusinessSystem system=ApiFactory.getAcsService().getSystemById(dataRule.getSystemId());
						if(system!=null){
							cell.setCellValue(system.getCode());
						}else{
							cell.setCellValue("");
						}
					}else{
						dataHandle.setFieldValue(conf,i,rowi,dataRule);
					}
				}
			}
			conditionInfo(dataRule,conditionSheet,conditionConfs);
		}
	}

	private void conditionInfo(DataRule dataRule, HSSFSheet conditionSheet,List<DataSheetConfig> conditionConfs) {
		List<Condition> conditions=conditionDao.getConditionsByDataRuleId(dataRule.getId());
		for(Condition condition:conditions){
			HSSFRow rowi = conditionSheet.createRow(conditionSheet.getLastRowNum()+1);
			for(int i=0;i<conditionConfs.size();i++){
				DataSheetConfig conf=conditionConfs.get(i);
				if(!conf.isIgnore()){
					if("dataRuleCode".equals(conf.getFieldName())){
						HSSFCell cell = rowi.createCell(i);
						cell.setCellValue(dataRule.getCode());
					}else{
						dataHandle.setFieldValue(conf,i,rowi,condition);
					}
				}
			}
		}
	}

	public void restore(Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		File file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilename()+".xls");
		if(file.exists()){
			if("ACS_DATA_RULE".equals(fileConfig.getData())){
				importDataRule(file, companyId);
			}else if("ACS_PERMISSION".equals(fileConfig.getData())){
				importPermission(file, companyId);
			}
		}
	}

	private void importPermission(File file, Long companyId) {
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PERMISSION']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_PERMISSION_ITEM']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		Map<String,Integer> conditionMap=dataHandle.getIdentifier(conditionConfs);
		FileInputStream fis=null;
		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_PERMISSION");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importPermission(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importPermission(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importPermission(sheet,confs,map);
 			}
 			HSSFSheet conditionSheet=wb.getSheet("ACS_PERMISSION_ITEM");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importPermissionItem(conditionSheet,conditionConfs,conditionMap);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importPermissionItem(conditionSheet,conditionConfs,conditionMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}

	private void importPermissionItem(HSSFSheet conditionSheet,
			List<DataSheetConfig> conditionConfs,
			Map<String, Integer> conditionMap) {
		int firstRowNum = conditionSheet.getFirstRowNum();
		int rowNum=conditionSheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =conditionSheet.getRow(i);
			if(conditionSheet.getRow(i)!=null){
				addPermissionItem(conditionConfs,row,conditionMap);
			}
		}
	}

	private void addPermissionItem(List<DataSheetConfig> conditionConfs,
			HSSFRow row, Map<String, Integer> conditionMap) {
		Integer index=conditionMap.get("itemType");
		String itemType=row.getCell(index).getStringCellValue();
		index=conditionMap.get("operator");
		String operator=row.getCell(index).getStringCellValue();
		index=conditionMap.get("joinType");
		String joinType=row.getCell(index).getStringCellValue();
		index=conditionMap.get("conditionValue");
		String conditionValue=row.getCell(index).getStringCellValue();
		index=conditionMap.get("conditionName");
		String conditionName=row.getCell(index).getStringCellValue();
		index=conditionMap.get("displayOrder");
		String displayOrder=row.getCell(index).getStringCellValue();
		index=conditionMap.get("leftBracket");
		String leftBracket=row.getCell(index).getStringCellValue();
		index=conditionMap.get("rightBracket");
		String rightBracket=row.getCell(index).getStringCellValue();
		index=conditionMap.get("permissionCode");
		String permissionCode=row.getCell(index).getStringCellValue();
		Permission permission=permissionDao.getPermissionsByCode(permissionCode);
		conditionValue=getConditionValue(itemType,conditionValue,permission.getDataRule().getSystemId());
		if(StringUtils.isNotEmpty(conditionValue)){
			if(permission!=null){
				ItemType itemType1=null;
				UserOperator operator1=null;
				LogicOperator joinType1=null;
				LeftBracket leftBracket1=null;
				RightBracket rightBracket1=null;
				if(StringUtils.isNotEmpty(itemType)){
					itemType1=ItemType.valueOf(itemType);
				}
				if(StringUtils.isNotEmpty(operator)){
					operator1=UserOperator.valueOf(operator);
				}
				if(StringUtils.isNotEmpty(joinType)){
					joinType1=LogicOperator.valueOf(joinType);
				}
				if(StringUtils.isNotEmpty(leftBracket)){
					leftBracket1=LeftBracket.valueOf(leftBracket);
					
				}
				if(StringUtils.isNotEmpty(rightBracket)){
					rightBracket1=RightBracket.valueOf(rightBracket);
				}
				PermissionItem permissionItem=permissionItemDao.getPermissionItem(itemType1,operator1,joinType1,conditionValue,permission.getId(),leftBracket1,rightBracket1);
				if(permissionItem==null){
					permissionItem=new PermissionItem();
				}
				permissionItem.setItemType(itemType1);
				permissionItem.setOperator(operator1);
				permissionItem.setJoinType(joinType1);
//				permissionItem.setConditionValue(conditionValue);
//				permissionItem.setConditionName(conditionName);
				if(StringUtils.isNotEmpty(displayOrder)){
					permissionItem.setDisplayOrder(Integer.valueOf(displayOrder));
				}
				permissionItem.setLeftBracket(leftBracket1);
				permissionItem.setRightBracket(rightBracket1);
				permissionItem.setPermission(permission);
				permissionItem.setCreatedTime(new Date());
				permissionItem.setCreator(ContextUtils.getLoginName());
				permissionItem.setCreatorName(ContextUtils.getUserName());
				permissionItem.setCompanyId(ContextUtils.getCompanyId());
				permissionItemDao.save(permissionItem);
			}
//				saveItemCondition(conditionValue,conditionName,);
		}
	}
	
	public void saveItemCondition(String conditionValue,String conditionName,Long permissionItemId){
		PermissionItemCondition itemCondition = new PermissionItemCondition();
		itemCondition.setConditionValue(conditionValue);
		itemCondition.setConditionName(conditionName);
		itemCondition.setDataId(permissionItemId);
		itemCondition.setValueType(ConditionValueType.PERMISSION);
		permissionItemConditionDao.save(itemCondition);
	}
	
	private String getConditionValue(String itemType,String conditionValue,Long systemId){
		if(ItemType.USER.equals(ItemType.valueOf(itemType))){
			User user=ApiFactory.getAcsService().getUserByLoginName(conditionValue);
			if(user!=null){
				return conditionValue;
			}
		}else if(ItemType.DEPARTMENT.equals(ItemType.valueOf(itemType))){
			Department department=ApiFactory.getAcsService().getDepartmentByCode(conditionValue);
			if(department!=null){
				return department.getId().toString();
			}
		}else if(ItemType.WORKGROUP.equals(ItemType.valueOf(itemType))){
			Workgroup workgroup=ApiFactory.getAcsService().getWorkgroupByCode(conditionValue);
			if(workgroup!=null){
				return workgroup.getId().toString();
			}
		}else if(ItemType.ROLE.equals(ItemType.valueOf(itemType))){
			Role role=roleManager.getRole(systemId, conditionValue);
			if(role != null){
				return conditionValue;
			}
		}
		return null;
	}

	private void importPermission(HSSFSheet sheet, List<DataSheetConfig> confs,
			Map<String, Integer> map) {
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addPermission(confs,row,map);
			}
		}
	}

	private void addPermission(List<DataSheetConfig> confs, HSSFRow row,
			Map<String, Integer> map) {
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();
		Permission permission=permissionDao.getPermissionsByCode(code);
		if(permission==null){
			permission=new Permission();
		}
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("dataRuleCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						DataRule dataRule=dataRuleDao.getDataRuleByCode(value);
						permission.setDataRule(dataRule);
					}
				}else if("menuCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						Menu menu=menuDao.getRootMenuByCode(value, ContextUtils.getCompanyId());
						if(menu!=null){
							permission.setMenuId(menu.getId());
						}
					}
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(permission,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(permission,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		permission.setCreatedTime(new Date());
		permission.setCreator(ContextUtils.getLoginName());
		permission.setCreatorName(ContextUtils.getUserName());
		permission.setCompanyId(ContextUtils.getCompanyId());
		permissionDao.save(permission);
		
	}

	private void importDataRule(File file, Long companyId) {
		List<DataSheetConfig> confs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_DATA_RULE']");
		List<DataSheetConfig> conditionConfs=dataHandle.getConfigInfo("data-sheets/sheets/data-sheet[@name='ACS_CONDITION']");
		Map<String,Integer> map=dataHandle.getIdentifier(confs);
		Map<String,Integer> conditionMap=dataHandle.getIdentifier(conditionConfs);
		FileInputStream fis=null;
		try{
 			fis=new FileInputStream(file);
 			HSSFWorkbook wb=new HSSFWorkbook(fis);
 			HSSFSheet sheet=wb.getSheet("ACS_DATA_RULE");
 			if(ContextUtils.getCompanyId()==null){
 				if(companyId==null){
 					List<Company> companys=companyManager.getCompanys();
 					for(Company company:companys){
 						ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 						ParameterUtils.setParameters(parameters);
 						importDataRule(sheet,confs,map);
 					}
 				}else{
 					ThreadParameters parameters=new ThreadParameters(companyId,null);
						ParameterUtils.setParameters(parameters);
						importDataRule(sheet,confs,map);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importDataRule(sheet,confs,map);
 			}
 			HSSFSheet conditionSheet=wb.getSheet("ACS_CONDITION");
 			if(ContextUtils.getCompanyId()==null){
 				List<Company> companys=companyManager.getCompanys();
 				for(Company company:companys){
 					ThreadParameters parameters=new ThreadParameters(company.getCompanyId(),null);
 					ParameterUtils.setParameters(parameters);
 					importCondition(conditionSheet,conditionConfs,conditionMap);
 				}
 				dataHandle.clearCompanyId();
 			}else{
 				importCondition(conditionSheet,conditionConfs,conditionMap);
 			}
 		} catch (FileNotFoundException e) {
 			log.debug(PropUtils.getExceptionInfo(e));
		}catch (IOException e){
			log.debug(PropUtils.getExceptionInfo(e));
		}finally{
 			try{
	 			if(fis!=null)fis.close();
 			}catch(IOException ep){
 				log.debug(PropUtils.getExceptionInfo(ep));
 			}
 		}
	}

	private void importCondition(HSSFSheet conditionSheet,
			List<DataSheetConfig> conditionConfs,
			Map<String, Integer> conditionMap) {
		int firstRowNum = conditionSheet.getFirstRowNum();
		int rowNum=conditionSheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =conditionSheet.getRow(i);
			if(conditionSheet.getRow(i)!=null){
				addCondition(conditionConfs,row,conditionMap);
			}
		}
	}

	private void addCondition(List<DataSheetConfig> conditionConfs,
			HSSFRow row, Map<String, Integer> conditionMap) {
		//数据表字段
		Integer index=conditionMap.get("field");
		String field=row.getCell(index).getStringCellValue();
		//比较符号
		index=conditionMap.get("operator");
		String operator=row.getCell(index).getStringCellValue();
		//条件连接类型
		index=conditionMap.get("lgicOperator");
		String lgicOperator=row.getCell(index).getStringCellValue();
		//字段数据类型
		index=conditionMap.get("dataType");
		String dataType=row.getCell(index).getStringCellValue();
		//条件值
		index=conditionMap.get("conditionValue");
		String conditionValue=row.getCell(index).getStringCellValue();
		//数据规则编号
		index=conditionMap.get("dataRuleCode");
		String dataRuleCode=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(dataRuleCode);
		FieldOperator operator1=null;
		LogicOperator lgicOperator1=null;
		DataType dataType1=null;
		if(StringUtils.isNotEmpty(operator)){
			operator1=FieldOperator.valueOf(operator);
		}
		if(StringUtils.isNotEmpty(lgicOperator)){
			lgicOperator1=LogicOperator.valueOf(lgicOperator);
		}
		if(StringUtils.isNotEmpty(dataType)){
			dataType1=DataType.valueOf(dataType);
		}
		
		Condition condition=conditionDao.getCondition(field,operator1,
				lgicOperator1,dataType1,conditionValue,dataRule.getId());
		if(condition==null){
			condition=new Condition();
		}
		for(int j=0;j<conditionConfs.size();j++){
			DataSheetConfig conf=conditionConfs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("dataRuleCode".equals(fieldName)){
					condition.setDataRule(dataRule);
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(condition,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(condition,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		condition.setCreatedTime(new Date());
		condition.setCreator(ContextUtils.getLoginName());
		condition.setCreatorName(ContextUtils.getUserName());
		condition.setCompanyId(ContextUtils.getCompanyId());
		conditionDao.save(condition);
		
	}

	private void importDataRule(HSSFSheet sheet, List<DataSheetConfig> confs,
			Map<String, Integer> map) {
		int firstRowNum = sheet.getFirstRowNum();
		int rowNum=sheet.getLastRowNum();
		for(int i=firstRowNum+1;i<=rowNum;i++){
			HSSFRow row =sheet.getRow(i);
			if(sheet.getRow(i)!=null){
				addDataRule(confs,row,map);
			}
		}
	}

	private void addDataRule(List<DataSheetConfig> confs, HSSFRow row,
			Map<String, Integer> map) {
		Integer index=map.get("code");
		String code=row.getCell(index).getStringCellValue();
		DataRule dataRule=dataRuleDao.getDataRuleByCode(code);
		if(dataRule==null){
			dataRule=new DataRule();
		}
		for(int j=0;j<confs.size();j++){
			DataSheetConfig conf=confs.get(j);
			if(!conf.isIgnore()){
				String fieldName=conf.getFieldName();
				String value=null;
				if(row.getCell(j)!=null){
					value=row.getCell(j).getStringCellValue();
				}
				if("dataTableName".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						DataTable dataTable=dataTableDao.findDataTableByName(value);
						dataRule.setDataTableId(dataTable.getId());
						dataRule.setDataTableName(dataTable.getAlias());
					}
				}else if("systemCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						BusinessSystem system=ApiFactory.getAcsService().getSystemByCode(value);
						dataRule.setSystemId(system.getId());
					}
				}else if("menuCode".equals(fieldName)){
					if(StringUtils.isNotEmpty(value)){
						Menu menu=menuDao.getRootMenuByCode(value, ContextUtils.getCompanyId());
						if(menu!=null){
							dataRule.setMenuId(menu.getId());
						}
					}
				}else{
					if(StringUtils.isNotEmpty(value)){//导入数据
						dataHandle.setValue(dataRule,fieldName,conf.getDataType(),value,conf.getEnumName());
					}else if(StringUtils.isNotEmpty(conf.getDefaultValue())){//导入默认值
						dataHandle.setValue(dataRule,fieldName,conf.getDataType(),conf.getDefaultValue(),conf.getEnumName());
					}
				}
			}
		}
		dataRule.setCreatedTime(new Date());
		dataRule.setCreator(ContextUtils.getLoginName());
		dataRule.setCreatorName(ContextUtils.getUserName());
		dataRule.setCompanyId(ContextUtils.getCompanyId());
		dataRuleDao.save(dataRule);
		
	}
}
