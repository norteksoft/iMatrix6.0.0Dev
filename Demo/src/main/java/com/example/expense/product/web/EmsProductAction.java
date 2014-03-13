package com.example.expense.product.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.EmsProduct;
import com.example.expense.product.service.EmsProductManager;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.mms.base.TotalColumnValues;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/emsproduct")
@ParentPackage("default")
public class EmsProductAction extends CrudActionSupport<EmsProduct> {
	private static final long serialVersionUID = 1L;
	private Long id;
	private EmsProduct emsProduct;
	private Page<EmsProduct> page;
	private GridColumnInfo gridColumnInfo;
	
	@Autowired
	private EmsProductManager emsProductManager;
	@Autowired
	private MmsUtil mmsUtil;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Page<EmsProduct> getPage() {
		return page;
	}
	public void setPage(Page<EmsProduct> page) {
		this.page = page;
	}
	public GridColumnInfo getGridColumnInfo() {
		return gridColumnInfo;
	}
	
	public EmsProduct getModel() {
		return emsProduct;
	}
	
	@Override
	public void prepareModel() throws Exception {
		if(id==null){
			emsProduct=new EmsProduct();
			emsProduct.setCompanyId(ContextUtils.getCompanyId());
			emsProduct.setCreatedTime(new Date());
			emsProduct.setCreator(ContextUtils.getLoginName());
			Integer index=emsProductManager.getMaxIndex();
			emsProduct.setDisplayIndex(index==null?1:index+1);
		}else{
			emsProduct=emsProductManager.getProduct(id);
		}
		
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		String deleteIds=Struts2Utils.getParameter("deleteIds");
		String[] ids=deleteIds.split(",");
		for(String deleteId:ids){
			emsProductManager.deleteProduct(Long.valueOf(deleteId));
		}
		return null;
	}
	
	@Action("input")
	@Override
	public String input() throws Exception {
		return "input";
	}
	
	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("api-list")
	public String getApiList() throws Exception {
		gridColumnInfo=mmsUtil.getGridColumnInfo("ES_PRODUCT_API");
		return SUCCESS;
	}
	
	@Action("save")
	@Override
	public String save() throws Exception {
		if(emsProduct.getProductName().equals("0")){
			this.renderText("false");
		}else{
			emsProductManager.saveProduct(emsProduct);
			this.renderText(JsonParser.getRowValue(emsProduct));
		}
		
		return null;
	}
	@Action("export")
	public String export() throws Exception {
		Page<EmsProduct> page = new Page<EmsProduct>(100000);
		page = emsProductManager.search(page);
		this.renderText(ExcelExporter.export(mmsUtil.getExportData(page,"ES_PRODUCT"),"emsProduct"));
		return null;
	}
	
	@Action("sort")
	public String sort() throws Exception {
		String originalIndex=Struts2Utils.getParameter("originalIndex");
		String newIndex=Struts2Utils.getParameter("newIndex");
		emsProductManager.saveEmsProduct(Integer.valueOf(originalIndex),Integer.valueOf(newIndex));
		return null;
	}
	
	@Action("total-list")
	public String totalList() throws Exception {
		return SUCCESS;
	}
	
	@Action("total-list-datas")
	public String getTotalListDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.PageToJson(page, new TotalColumnValues(){
			@Override
			public Map<String,Object> getValues(List<String> names) {
				return emsProductManager.getAmountTotal(names);
			}
			
		}));
		return null;
	}
	@Action("custom-search-list")
	public String customSearchList() throws Exception {
		return SUCCESS;
	}
	
	@Action("custom-search-list-datas")
	public String getCustomSearchDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	/**
	 * 单元格合并
	 * @return
	 * @throws Exception
	 */
	@Action("cell-merge-list")
	public String cellMergeList() throws Exception {
		return SUCCESS;
	}
	
	@Action("cell-merge-list-datas")
	public String getCellMergeDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	/**
	 * 表头组合
	 * @return
	 * @throws Exception
	 */
	@Action("group-header-list")
	public String groupHeaderList() throws Exception {
		return SUCCESS;
	}
	
	@Action("group-header-list-datas")
	public String getGroupHeaderDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	/**
	 * 查询事件
	 * @return
	 * @throws Exception
	 */
	@Action("query-event-list")
	public String queryEventList() throws Exception {
		return SUCCESS;
	}
	
	@Action("query-event-list-datas")
	public String getQueryEventDatas() throws Exception {
		page = emsProductManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("dataRule-list")
	public String dataRuleList() throws Exception{
		return SUCCESS;
	}
	
	@Action("dataRule-list-datas")
	public String dataRuleListDatas() throws Exception {
		page = emsProductManager.searchDataRuleList(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
}
