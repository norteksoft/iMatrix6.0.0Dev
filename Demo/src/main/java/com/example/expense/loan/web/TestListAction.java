package com.example.expense.loan.web;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.TestList;
import com.example.expense.loan.service.TestListManager;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;


@Namespace("/loan-bill")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "test-list", type = "redirectAction") })
public class TestListAction extends CrudActionSupport<TestList> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private TestList testList;
	private Page<TestList> page = new Page<TestList>(Page.EACH_PAGE_TEN, true);
	private File file;
	private String fileName;
	
	@Autowired
	private TestListManager testListManager;
	@Autowired
	private MmsUtil mmsUtil;
	
	@Override
	protected void prepareModel() throws Exception {
	}
	
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	@Action("test-list-save")
	@Override
	public String save() throws Exception {
		return null;
	}
	
	@Action("test-list-delete")
	@Override
	public String delete() throws Exception {
		testListManager.deleteTestList(id);
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功删除!</nobr></font>");
		return null;
	}

	@Action("test-list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("test-list-data")
	public String listData(){
		page = testListManager.list(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("test-list-export")
	public String export() throws Exception {
		Page<TestList> page = new Page<TestList>(100000);
		page = testListManager.list(page);
		this.renderText(ExcelExporter.export(mmsUtil.getExportData(page,"ES_TEST_LIST"),"testList"));
		return null;
	}
	
	@Action("test-list-import")
	public String taskImport(){
		return "task-import";
	}
	
	@Action("test-list-do-import")
	public String doImport() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file,fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	@Action("test-list-insert")
	public String insertData(){
		testListManager.batchInsertData();
		renderText("插入成功！");
		return null;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public TestList getModel() {
		return testList;
	}

	public Page<TestList> getPage() {
		return page;
	}

	public void setPage(Page<TestList> page) {
		this.page = page;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}

}
