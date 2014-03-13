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

import com.example.expense.entity.PlanTask;
import com.example.expense.loan.service.PlanTaskManager;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;


@Namespace("/loan-bill")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "plan-task", type = "redirectAction") })
public class PlanTaskAction extends CrudActionSupport<PlanTask> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private PlanTask planTask;
	private Page<PlanTask> page = new Page<PlanTask>(Page.EACH_PAGE_TEN, true);
	private File file;
	private String fileName;
	
	@Autowired
	private PlanTaskManager planTaskManager;
	@Autowired
	private MmsUtil mmsUtil;
	
	@Override
	protected void prepareModel() throws Exception {
	}
	
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	@Action("save")
	@Override
	public String save() throws Exception {
		return null;
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		planTaskManager.deletePlanTask(id);
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功删除!</nobr></font>");
		return null;
	}

	@Action("task-list")
	@Override
	public String list() throws Exception {
		return "plan-task";
	}
	
	@Action("task-list-data")
	public String listData(){
		page = planTaskManager.list(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("task-export")
	public String export() throws Exception {
		Page<PlanTask> page = new Page<PlanTask>(50000);
		page = planTaskManager.list(page);
		this.renderText(ExcelExporter.export(mmsUtil.getExportData(page,"ES_PLAN_TASK"),"planTask"));
		return null;
	}
	
	@Action("task-import")
	public String taskImport(){
		return "task-import";
	}
	
	@Action("do-import")
	public String doImport() throws Exception{
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	@Action("insert-data")
	public String insertData(){
		planTaskManager.batchInsertData();
		renderText("插入成功！");
		return null;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public PlanTask getModel() {
		return planTask;
	}

	public Page<PlanTask> getPage() {
		return page;
	}

	public void setPage(Page<PlanTask> page) {
		this.page = page;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
