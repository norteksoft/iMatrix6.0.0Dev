package com.norteksoft.wf.unit;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.mms.form.dao.DataTableDao;
import com.norteksoft.mms.form.dao.FormViewDao;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.wf.entity.TestEntity;



@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class MmsServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	ListViewDao listViewDao;
	
	@SpringBeanByName
	DataTableDao dataTableDao;
	
	@SpringBeanByName
	ListColumnDao listColumnDao;
	
	@SpringBeanByName
	MenuDao menuDao;
	
	@SpringBeanByName
	FormViewDao formViewDao;
	
	@SpringBeanByName
	TableColumnDao tableColumnDao;
	
	@SpringBeanByName
	FormViewManager formViewManager;

	@Test
	public void deleteCustomListView(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		ListView listView2 = new ListView();
		listView2.setCode("wangjing_bbbbb");
		listView2.setCompanyId(1L);
		
		ListColumn listColumn2 = new ListColumn();
		listColumn2.setColumnName("fdsf");
		listColumn2.setCompanyId(1L);
		listColumn2.setListView(listView2);
		List<ListColumn> listColumnList2 = new ArrayList<ListColumn>();
		listColumnList2.add(listColumn2);
		listView2.setColumns(listColumnList2);
		
		DataTable dataTable = new DataTable();
		dataTable.setCompanyId(1L);
		listView.setDataTable(dataTable);
		listView2.setDataTable(dataTable);
		
		dataTableDao.save(dataTable);
		listColumnDao.save(listColumn);
		listColumnDao.save(listColumn2);
		listViewDao.save(listView);
		listViewDao.save(listView2);
		
		ApiFactory.getMmsService().deleteCustomListView("wangjing_aaaa");
		com.norteksoft.product.api.entity.ListView result = ApiFactory.getMmsService().getListViewByCode("wangjing_aaaa");
		Assert.assertTrue(result==null);
	}
	
	@Test
	public void getTopMenus(){
		Menu menu = new Menu();
		menu.setCode("wangjing_code");
		menu.setCompanyId(1L);
		menu.setLayer(1);
		menuDao.save(menu);
		
		List<com.norteksoft.product.api.entity.Menu> result = ApiFactory.getMmsService().getTopMenus();
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getTopMenu(){
		Menu menu = new Menu();
		menu.setCode("wangjing_code");
		menu.setCompanyId(1L);
		menu.setLayer(1);
		menuDao.save(menu);
		
		com.norteksoft.product.api.entity.Menu result = ApiFactory.getMmsService().getTopMenu("wangjing_code");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getListViews(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		Menu menu = new Menu();
		menu.setCode("wangjing_code");
		menu.setCompanyId(1L);
		menuDao.save(menu);
		com.norteksoft.product.api.entity.Menu m = ApiFactory.getMmsService().getTopMenu("wangjing_code");
		listView.setMenuId(m.getId());
		
		listViewDao.save(listView);
		
		List<com.norteksoft.product.api.entity.ListView> result = ApiFactory.getMmsService().getListViews("wangjing_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_aaaa", result.get(0).getCode());
	}
	
	@Test
	public void getFormViewByCode(){
		FormView formView = new FormView();
		formView.setCode("wangjing_formview_code");
		formView.setCompanyId(1L);
		formView.setVersion(1);
		formViewDao.save(formView);
		
		com.norteksoft.product.api.entity.FormView result = ApiFactory.getMmsService().getFormViewByCode("wangjing_formview_code",1);
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_formview_code", result.getCode());
	}
	
	
	@Test
	public void getGridColumnInfo(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setVisible(true);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		listColumnDao.save(listColumn);
		listViewDao.save(listView);
		
		GridColumnInfo result = ApiFactory.getMmsService().getGridColumnInfo("wangjing_aaaa");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getExportData(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setVisible(true);
		listColumn.setExportable(true);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		listColumnDao.save(listColumn);
		listViewDao.save(listView);
		
		ExportData result = ApiFactory.getMmsService().getExportData(new Page<TestEntity>(),"wangjing_aaaa");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getColumnsByCode(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		TableColumn tableColumn = new TableColumn();
		tableColumn.setName("fdaf");
		tableColumn.setDbColumnName("fsafdaf");
		listColumn.setTableColumn(tableColumn);
		
		DataTable dataTable = new DataTable();
		dataTable.setCompanyId(1L);
		listView.setDataTable(dataTable);
		
		tableColumnDao.save(tableColumn);
		dataTableDao.save(dataTable);
		listColumnDao.save(listColumn);
		listViewDao.save(listView);
		
		String result = ApiFactory.getMmsService().getColumnsByCode("wangjing_aaaa");
		Assert.assertNotNull(result);
		Assert.assertEquals("fsafdaf", result);
	}
	
	@Test
	public void getExportColumnsByCode(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setExportable(true);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		TableColumn tableColumn = new TableColumn();
		tableColumn.setName("fdaf");
		tableColumn.setDbColumnName("fsafdaf");
		listColumn.setTableColumn(tableColumn);
		
		DataTable dataTable = new DataTable();
		dataTable.setCompanyId(1L);
		listView.setDataTable(dataTable);
		
		tableColumnDao.save(tableColumn);
		dataTableDao.save(dataTable);
		listColumnDao.save(listColumn);
		listViewDao.save(listView);
		
		String result = ApiFactory.getMmsService().getExportColumnsByCode("wangjing_aaaa");
		Assert.assertNotNull(result);
		Assert.assertEquals("fdaf", result);
	}
	
	@Test
	public void saveView(){
		com.norteksoft.product.api.entity.ListView listView = new com.norteksoft.product.api.entity.ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ApiFactory.getMmsService().saveView(listView);
		com.norteksoft.product.api.entity.ListView result = ApiFactory.getMmsService().getListViewByCode("wangjing_aaaa");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getDefaultListViewByDataTable(){
		ListView listView = new ListView();
		listView.setCode("wangjing_aaaa");
		listView.setCompanyId(1L);
		
		ListColumn listColumn = new ListColumn();
		listColumn.setColumnName("fdsf");
		listColumn.setCompanyId(1L);
		listColumn.setExportable(true);
		listColumn.setListView(listView);
		List<ListColumn> listColumnList = new ArrayList<ListColumn>();
		listColumnList.add(listColumn);
		listView.setColumns(listColumnList);
		
		TableColumn tableColumn = new TableColumn();
		tableColumn.setName("fdaf");
		tableColumn.setDbColumnName("fsafdaf");
		listColumn.setTableColumn(tableColumn);
		
		DataTable dataTable = new DataTable();
		dataTable.setName("wangjing_datatable_name");
		dataTable.setCompanyId(1L);
		listView.setDataTable(dataTable);
		
		tableColumnDao.save(tableColumn);
		dataTableDao.save(dataTable);
		listColumnDao.save(listColumn);
		listViewDao.save(listView);
		
		com.norteksoft.product.api.entity.ListView result = ApiFactory.getMmsService().getDefaultListViewByDataTable("wangjing_datatable_name");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_aaaa", result.getCode());
	}
	
	@Test
	public void getSignatureFieldByFormViewCode(){
		FormView formView = new FormView();
		formView.setCode("wangjing_formview_code");
		formView.setCompanyId(1L);
		formView.setVersion(1);
		formView.setHtml("&lt;p&gt;	编号：&lt;input plugintype=\"TEXT\" id=\"code\" signaturevisible=\"true\" name=\"code\" title=\"编号\" datatype=\"TEXT\" request=\"false\" readolny=\"false\" format=\"number\" formattype=\"null\" formattip=\"数字\" value=\"\" maxlength=\"\" type=\"TEXT\" /&gt;&lt;/p&gt;&lt;p&gt;	名称：&lt;input plugintype=\"TEXT\" id=\"name\" signaturevisible=\"false\" name=\"name\" title=\"名称\" datatype=\"TEXT\" request=\"false\" readolny=\"false\" format=\"number\" formattype=\"null\" formattip=\"数字\" value=\"\" maxlength=\"\" type=\"TEXT\" /&gt;&lt;/p&gt;");
		formView.setFormState(DataState.ENABLE);
		formViewDao.save(formView);
		
		formViewManager.getAllSignatureFields();
		
		List<String> result = ApiFactory.getMmsService().getSignatureFieldByFormViewCode("wangjing_formview_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("code", result.get(0));
	}
	
	@Test
	public void getSignatureFieldByFormViewCodeTwo(){
		FormView formView = new FormView();
		formView.setCode("wangjing_formview_code");
		formView.setCompanyId(1L);
		formView.setVersion(1);
		formView.setHtml("&lt;p&gt;	编号：&lt;input plugintype=\"TEXT\" id=\"code\" signaturevisible=\"true\" name=\"code\" title=\"编号\" datatype=\"TEXT\" request=\"false\" readolny=\"false\" format=\"number\" formattype=\"null\" formattip=\"数字\" value=\"\" maxlength=\"\" type=\"TEXT\" /&gt;&lt;/p&gt;&lt;p&gt;	名称：&lt;input plugintype=\"TEXT\" id=\"name\" signaturevisible=\"false\" name=\"name\" title=\"名称\" datatype=\"TEXT\" request=\"false\" readolny=\"false\" format=\"number\" formattype=\"null\" formattip=\"数字\" value=\"\" maxlength=\"\" type=\"TEXT\" /&gt;&lt;/p&gt;");
		formView.setFormState(DataState.ENABLE);
		formViewDao.save(formView);
		
		formViewManager.getAllSignatureFields();
		
		List<String> result = ApiFactory.getMmsService().getSignatureFieldByFormViewCode("wangjing_formview_code",1);
		Assert.assertNotNull(result);
		Assert.assertEquals("code", result.get(0));
	}
	
}
