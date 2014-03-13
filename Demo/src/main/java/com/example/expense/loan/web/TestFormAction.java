package com.example.expense.loan.web;



import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.loan.service.TestFormManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.ListView;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;


@Namespace("/loan-bill")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "test-form", type = "redirectAction") })
public class TestFormAction extends CrudActionSupport<Object> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Object data;
	private Page<Object> page = new Page<Object>(0, true);
	
	@Autowired
	private TestFormManager testFormManager;
	
	@Override
	protected void prepareModel() throws Exception {
	}
	
	@Action("test-form-input")
	@Override
	public String input() throws Exception {
		if(id!=null){
			ListView view = ApiFactory.getMmsService().getListViewByCode("capability_test_1");
			data = testFormManager.getDateById(view, id);
		}
		return SUCCESS;
	}
	
	@Action("test-form-save")
	@Override
	public String save() throws Exception {
		ListView view = ApiFactory.getMmsService().getListViewByCode("capability_test_1");
		Map<String,String[]> parameterMap = Struts2Utils.getRequest().getParameterMap();
		Long id = testFormManager.save(parameterMap);
		data = testFormManager.getDateById(view, id);
		renderText("保存成功！");
		return "test-form-input";
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		return null;
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("test-form-list")
	public String listData(){
		ListView view = ApiFactory.getMmsService().getListViewByCode("capability_test_1");
		if(page.getPageSize()>1){
			testFormManager.list(page, view);
			ApiFactory.getBussinessLogService().log("自定义系统", 
					"自定义列表", 
					ContextUtils.getSystemId("mms"));
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return SUCCESS;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	@Override
	public Object getModel() {
		return null;
	}

	public Page<Object> getPage() {
		return page;
	}

	public void setPage(Page<Object> page) {
		this.page = page;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
}
