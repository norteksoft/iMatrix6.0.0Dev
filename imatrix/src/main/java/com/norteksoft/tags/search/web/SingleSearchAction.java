package com.norteksoft.tags.search.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.StartQuery;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.tags.search.ObjectField;
import com.norteksoft.tags.search.SearchData;

@Namespace("/search")
@ParentPackage("default")
public class SingleSearchAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;

	private String listCode;
	private String submitForm;
	private String placeId;
	
	@Autowired
	private ListViewManager listViewManager;
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String singleSearch() throws Exception {
		ListView listView = listViewManager.getListViewByCode(listCode);
		if(StartQuery.NO_QUERY.equals(listView.getStartQuery())){
			this.renderText("noQuery");
		}else{
			SearchData searchData = (SearchData) ContextUtils.getBean("searchData");
			List<ObjectField> fieldList = new ArrayList<ObjectField>();
			fieldList = searchData.getFieldListByCode(listView);
			//判断是否高级查询从mms中取值
			String advancedSearch = searchData.getSearchPropertyByCode(listView,"isAdvancedQuery");
			//判断是否弹框从mms中取值
			String containerId = searchData.getSearchPropertyByCode(listView,"isContainerIdQuery");
			String jsonStr = JsonParser.object2Json(fieldList);
			List<ObjectField> fixedFields = new ArrayList<ObjectField>();
			StringBuilder searchInformation=new StringBuilder();
			searchInformation.append("fixedField:").append(searchData.getFixedFields(fixedFields,fieldList)).append(",");
			searchInformation.append("fixedSearchSign:").append(fixedFields.size()).append(",");
			searchInformation.append("submitForm:").append(submitForm==null?"''":"'"+submitForm+"'").append(",");
			searchInformation.append("containerId:'").append(containerId).append("',");
			searchInformation.append("advancedSearch:'").append(advancedSearch).append("',");
			searchInformation.append("placeId:'").append(placeId).append("',");
			searchInformation.append("fieldList:").append(jsonStr);
			this.renderText("{"+searchInformation.toString()+"}");
		}
		return null;
	}
	
	public String getListCode() {
		return listCode;
	}

	public void setListCode(String listCode) {
		this.listCode = listCode;
	}

	public String getSubmitForm() {
		return submitForm;
	}

	public void setSubmitForm(String submitForm) {
		this.submitForm = submitForm;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
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

}
