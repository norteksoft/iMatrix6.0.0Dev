package com.norteksoft.ems.service;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.ImportDefinitionDao;
import com.norteksoft.bs.options.entity.ImportColumn;
import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.service.ImportDefinitionManager;
import com.norteksoft.product.api.impl.DefaultDataImporterCallBack;

@Service
@Transactional
public class ImportUnitTestManager extends DefaultDataImporterCallBack{
	@Autowired
	private ImportDefinitionDao importDefDao;
	@Autowired
	ImportDefinitionManager importDefinitionManager;
	
	public String saveSingleRow(String[] rowValue,ImportDefinition importDefinition) {
		ImportDefinition importDef=importDefinitionManager.getImportDefinitionByCode(rowValue[0]);
		if(importDef==null){
			importDef=new ImportDefinition();
		}
		int i=0;
		for(ImportColumn importColumn:importDefinition.getImportColumns()){
			try {
					BeanUtils.copyProperty(importDef,importColumn.getName() , rowValue[i]);
			}  catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
		importDefDao.save(importDef);
		return "";
	}
}
