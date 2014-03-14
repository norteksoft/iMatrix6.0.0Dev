package com.norteksoft.wf.unit;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.options.entity.ImportDefinition;
import com.norteksoft.bs.options.service.ImportDefinitionManager;
import com.norteksoft.ems.service.ExpenseReportManager;
import com.norteksoft.ems.service.ImportUnitTestManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ParameterUtils;

@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class DataImporterServiceTest extends BaseWorkflowTestCase {
	@SpringBeanByName
	ImportDefinitionManager importDefinitionManager;
	@SpringBeanByName
	ExpenseReportManager expenseReportManager;
	@SpringBeanByName
	ImportUnitTestManager importUnitTestManager;
	
	@Test
	public void importDataOne(){
		try {
			ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
			File file = new File(Thread.currentThread().getContextClassLoader().getResource("import_unitTest_data.txt").getFile());
			String result=ApiFactory.getDataImporterService().importData(file, "import_unitTest_data.txt");
			Assert.assertEquals("导入成功！", result);
			ImportDefinition importDefinition=importDefinitionManager.getImportDefinitionByCode("test2");
			Assert.assertNotNull(importDefinition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void importDataTwo(){
		try {
			ParameterUtils.setParameters(getPrmt(1L, 65L, "test2", "test2"));
			File file = new File(Thread.currentThread().getContextClassLoader().getResource("import_unitTest_data.txt").getFile());
			String result=ApiFactory.getDataImporterService().importData(file, "import_unitTest_data.txt",importUnitTestManager);
			Assert.assertEquals("导入成功！", result);
			ImportDefinition importDefinition=importDefinitionManager.getImportDefinitionByCode("test2");
			Assert.assertNotNull(importDefinition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
