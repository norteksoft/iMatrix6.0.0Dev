package com.example.expense.loan.service;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.loan.dao.TestFormDao;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.View;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.entity.ListView;
import com.norteksoft.product.orm.Page;


@Service
@Transactional
public class TestFormManager {
	@Autowired
	private TestFormDao testFormDao;
	@Autowired
	private ModulePageManager modulePageManager;
	@Autowired
	private FormViewManager formViewManager;
	public Page<Object> list(Page<Object> page, ListView view) {

		if(StringUtils.isNotBlank(page.getOrderBy())){
			if(!page.getOrderBy().startsWith("dt_") && !"id".equals(page.getOrderBy())){
				page.setOrderBy("dt_"+page.getOrderBy());
			}
		}
		testFormDao.list(page, view.getCode());
		if(StringUtils.isNotBlank(page.getOrderBy())){
			if(page.getOrderBy().startsWith("dt_")){
				page.setOrderBy(page.getOrderBy().replaceFirst("dt_", ""));
			}
		}
		return page;
	
	}
	
	/**
	 * 保存表单数据
	 */
	@Transactional
	public Long save(Map<String,String[]> parameter){
		FormView form = formViewManager.getCurrentFormViewByCodeAndVersion("capability_test", 1);
		
		return saveDate(parameter, form);
	}
	
	@Transactional
	public Long saveDate(Map<String,String[]> parameter, FormView form){
		String[] ids = parameter.get("id");
		List<FormControl> controls = formViewManager.getControls(form);
		Long id = null;
		if(ids != null && StringUtils.isNotBlank(ids[0])){
			id = testFormDao.update(parameter, form, controls, Long.parseLong(ids[0]));
		}else{
			id = testFormDao.save(parameter, form, controls);
		}
		return id;
	}
	
	/**
	 * 根据ID，表单视图查询数据
	 * @param formView
	 * @param id
	 * @return
	 */
	@Transactional
	public Object getDateById(ListView formView, Long id){
			return testFormDao.getDateById(formView.getDataTable().getName(), id);
	}
}
