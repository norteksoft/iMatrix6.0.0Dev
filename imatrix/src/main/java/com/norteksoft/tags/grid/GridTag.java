package com.norteksoft.tags.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.form.entity.GroupHeader;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.OrderType;
import com.norteksoft.mms.form.enumeration.StartQuery;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.GroupHeaderManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.tags.search.SearchData;
public class GridTag extends TagSupport {
private static final long serialVersionUID = 1L;
	
	private String gridId;//表单的ID
	private String code;//表单编号
	private String url;//表单URL
	private String pageName;//数据名称
	private String subGrid;//是否有子表
	private List<DynamicColumnDefinition> dynamicColumn;//动态字段
	private String submitForm;//查询需要的FormID
	
	//标签开始时调用的出来方法
	@Override
	public int doStartTag() throws JspException {
		try {
			String html =readTemplate();
			//将信息内容输出到JSP页面
			pageContext.getOut().print(html);
		} catch (Exception e) {
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
		//跳过标签体的执行
//		return SKIP_BODY;
	}
	
	private String readTemplate() throws Exception {
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		String loginName=ContextUtils.getLoginName();
		String userName=ContextUtils.getTrueName();
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(code);
		Assert.notNull(listView,"listView不能为空");
		List<ListColumn> columns=listView.getColumns();
		parserListColumns(columns);
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("gridId", gridId);
		root.put("url", url);
		root.put("ctx", webapp);
		root.put("resourceCtx", PropUtils.getProp("host.resources"));
		root.put("_list_code", code);
		String total=hasTotal(columns);//是否合计
		String mergerCell=hasMergerCell(columns);//是否合并单元格
		String export=hasExport(columns);//是否导出
		root.put("loginName", loginName);
		root.put("userName", userName);
		root.put("pageName", StringUtils.isEmpty(pageName)?"":pageName);
		root.put("editurl", StringUtils.isNotEmpty(listView.getEditUrl())?listView.getEditUrl():"");
		root.put("dragRowUrl", StringUtils.isNotEmpty(listView.getDragRowUrl())?listView.getDragRowUrl():"");
		root.put("deleteUrl", StringUtils.isNotEmpty(listView.getDeleteUrl())?listView.getDeleteUrl():"");
		root.put("rowNumbers", listView.getRowNumbers()?"true":"false");
		root.put("customProperty", StringUtils.isNotEmpty(listView.getCustomProperty())?listView.getCustomProperty():"");
		root.put("subGrid", StringUtils.isNotEmpty(subGrid)?subGrid:"");
		root.put("rowNum", listView.getRowNum()!=null?listView.getRowNum().toString():"");
		root.put("rowList", StringUtils.isNotEmpty(listView.getRowList())?listView.getRowList():"");
		root.put("multiselect", getMultiselect(listView));
		root.put("multiboxSelectOnly", getMultiboxSelectOnly(listView));
		root.put("sortname", StringUtils.isNotEmpty(listView.getDefaultSortField())?listView.getDefaultSortField():"");
		root.put("sortorder", OrderType.DESC.equals(listView.getOrderType())?"desc":"asc");
		root.put("pagination", listView.getPagination()?"true":"");
		root.put("frozenColumn", listView.getFrozenColumn()==null?0:listView.getFrozenColumn());
		parserGroupHeader(root,listView);
		if(dynamicColumn!=null&&dynamicColumn.size()>0){
			total=parserDynamicColumn(dynamicColumn,total);
			root.put("dynamicColumn", getDynamicColumn(dynamicColumn));
			root.put("dynamicColumns", getDynamicColumns(dynamicColumn));
			String colName=dynamicColumn.get(0).getName();
			root.put("dynamicColumnName", colName.subSequence(0, colName.length()-1));
		}else{
			root.put("dynamicColumn", "");
			root.put("dynamicColumns", "");
			root.put("dynamicColumnName", "");
		}
		root.put("total", total);
		root.put("mergerCell", mergerCell);
		root.put("export", export);
		root.put("columns", getColumns(columns));
		root.put("startQuerySign", listView.getStartQuery()==null?StartQuery.NO_QUERY:listView.getStartQuery());
		String result = TagUtil.getContent(root, "grid/gridTag.ftl");
		
		if(!StartQuery.NO_QUERY.equals(listView.getStartQuery())){
			SearchData searchData = (SearchData) ContextUtils.getBean("searchData");
			String searchResult = searchData.getContent(code,submitForm);
			result+=searchResult;
		}
		result+="<input id='totalable_page_id' type='hidden' value='"+listView.getTotalable()+"'/><input id='searchTotalable_page_id' type='hidden' value='"+listView.getSearchTotalable()+"'/>";
		return result;
	}
	
	private void parserGroupHeader(Map<String, Object> root,ListView listView) {
		GroupHeaderManager groupHeaderManager = (GroupHeaderManager) ContextUtils.getBean("groupHeaderManager");
		List<GroupHeader> groupHeaders=groupHeaderManager.getGroupHeadersByViewId(listView.getId());
		String groupHeadersJsonString=getGroupHeadersJsonString(groupHeaders);
		if(groupHeaders !=null && groupHeaders.size()>0){
			root.put("groupHeaderSign", "true");
			root.put("groupHeader", groupHeadersJsonString);
		}else{
			root.put("groupHeaderSign", "");
			root.put("groupHeader", "");
		}
	}

	private String getMultiboxSelectOnly(ListView listView) {
		if(listView.getMultiboxSelectOnly()!=null&&listView.getMultiboxSelectOnly()){
			return "true";
		}else{
			return "false";
		}
	}

	private String getMultiselect(ListView listView) {
		if(listView.getMultiSelect()!=null&&listView.getMultiSelect()){
			return "true";
		}else {
			return "false";
		}
	}

	private String getGroupHeadersJsonString(List<GroupHeader> groupHeaders) {
		StringBuilder jsonString=new StringBuilder();
		for(GroupHeader gh:groupHeaders){
			if(StringUtils.isNotEmpty(jsonString.toString())){
				jsonString.append(",");
			}
			jsonString.append(JsonParser.object2Json(gh));
		}
		return "["+jsonString.toString()+"]";
	}

	private String getDynamicColumn(List<DynamicColumnDefinition> dynamicColumn){
		StringBuilder col=new StringBuilder();
		for(DynamicColumnDefinition dynamicCol:dynamicColumn){
			if(StringUtils.isNotEmpty(col.toString())){
				col.append(",");
			}
			col.append("{");
			col.append("colName:'").append(dynamicCol.getColName()==null?"":dynamicCol.getColName()).append("',");
			col.append("exportable:").append(dynamicCol.getExportable()).append(",");
			col.append("name:'").append(dynamicCol.getName()==null?"":dynamicCol.getName()).append("',");
			col.append("editable:").append(dynamicCol.getEditable()).append(",");
			col.append("editRules:'").append(dynamicCol.getEditRules()==null?"":dynamicCol.getEditRules()).append("',");
			col.append("type:'").append(dynamicCol.getType()).append("',");//枚举
			col.append("editoptions:'").append(dynamicCol.getEditoptions()==null?"":dynamicCol.getEditoptions()).append("',");
			col.append("edittype:'").append(dynamicCol.getEdittype()).append("',");//枚举
			col.append("defaultValue:'").append(dynamicCol.getDefaultValue()).append("',");//枚举
			col.append("colWidth:'").append(dynamicCol.getColWidth()==null?"":dynamicCol.getColWidth()).append("',");
			col.append("visible:").append(dynamicCol.getVisible()).append(",");
			col.append("eventType:'").append(dynamicCol.getEventType()==null?"":dynamicCol.getEventType()).append("'");//枚举
			col.append("}");
		}
		return "["+col.toString()+"]";
	}
	
	private String getColumns(List<ListColumn> columns) {
		StringBuilder col=new StringBuilder();
		for(ListColumn listColumn:columns){
			if(StringUtils.isNotEmpty(col.toString())){
				col.append(",");
			}
			col.append("{");
			col.append(getTableColumnJsonString(listColumn.getTableColumn())).append(",");
			col.append("headerName:'").append(listColumn.getHeaderName()==null?"":listColumn.getHeaderName()).append("',");
			col.append("internationName:'").append(listColumn.getInternationName()==null?"":listColumn.getInternationName()).append("',");
			col.append("mergerCell:").append(listColumn.getMergerCell()).append(",");
			col.append("editable:").append(listColumn.getEditable()).append(",");
			col.append("editRules:'").append(listColumn.getEditRules()==null?"":listColumn.getEditRules()).append("',");
			if(listColumn.getOptionSet()!=null){
				col.append("optionSet:{").append(listColumn.getOptionSet()).append("},");
			}else{
				col.append("optionSet:'',");
			}
			col.append("controlValue:'").append(listColumn.getControlValue()==null?"":listColumn.getControlValue()).append("',");
			col.append("format:'").append(listColumn.getFormat()==null?"":listColumn.getFormat()).append("',");
			col.append("columnName:'").append(listColumn.getColumnName()==null?"":listColumn.getColumnName()).append("',");
			col.append("defaultValue:'").append(listColumn.getDefaultValue()==null?"":listColumn.getDefaultValue()).append("',");//枚举
			col.append("headStyle:'").append(listColumn.getHeadStyle()==null?"":listColumn.getHeadStyle()).append("',");
			col.append("sortable:").append(listColumn.getSortable()).append(",");
			col.append("visible:").append(listColumn.getVisible()).append(",");
			col.append(getMainKeyJsonString(listColumn.getMainKey())).append(",");
			col.append("eventType:'").append(listColumn.getEventType()==null?"":listColumn.getEventType()).append("'");
			col.append("}");
		}
		return "["+col.toString()+"]";
	}

	private String getMainKeyJsonString(TableColumn tableColumn) {
		StringBuilder jsonString=new StringBuilder();
		if(tableColumn!=null){
			jsonString.append("mainKeyName:'").append(tableColumn.getName()).append("'");
		}else{
			jsonString.append("mainKeyName:''");
		}
		return jsonString.toString();
	}
	
	private String getTableColumnJsonString(TableColumn tableColumn) {
		StringBuilder jsonString=new StringBuilder();
		if(tableColumn!=null){
			jsonString.append("tableColumn:{");
			jsonString.append("name:'").append(tableColumn.getName());
			jsonString.append("',dataType:'").append(tableColumn.getDataType()).append("'");
			jsonString.append("}");
		}else{
			jsonString.append("tableColumn:''");
		}
		return jsonString.toString();
	}

	private String parserDynamicColumn(List<DynamicColumnDefinition> dynamicColumn,String total) {
		for(DynamicColumnDefinition dfo:dynamicColumn){
			formatterEnum(dfo);
			if("false".equals(total)){
				if(dfo.getIsTotal()!=null&&dfo.getIsTotal()){
					total="true";
				}
			}
		}
		return total;
	}

	private void parserListColumns(List<ListColumn> columns) {
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		if(columns!=null){
			for(ListColumn lc:columns){
				lc.setInternationName(formHtmlParser.getInternation(lc.getHeaderName()));
				String vs=formHtmlParser.getValueSet(lc,null,null);
				lc.setOptionSet(vs);
				if(lc.getTableColumn()!=null){
					lc.setColumnName(formHtmlParser.getColModelName(lc.getTableColumn().getName()));
				}
			}
		}else{
			columns=new ArrayList<ListColumn>();
		}
	}

	private String hasExport(List<ListColumn> columns) {
		for(ListColumn lc:columns){
			if(lc.getExportable()!=null&&lc.getExportable()){
				return "true";
			}
		}
		return "false";
	}

	private String hasMergerCell(List<ListColumn> columns) {
		for(ListColumn lc:columns){
			if(lc.getTableColumn()!=null&&lc.getMergerCell()){
				return "true";
			}
		}
		return "false";
	}

	private String hasTotal(List<ListColumn> columns) {
		for(ListColumn lc:columns){
			if(lc.getTotal()){
				return "true";
			}
		}
		return "false";
	}

	
	/**
	 * 获得动态列,形式为{key:value,key:value,...},key表示列体name,value表示列头名称
	 * @param dynamicColumnDefinition2
	 * @return
	 */
	private String getDynamicColumns(List<DynamicColumnDefinition> dynamicColumnDefinitions) {
		StringBuilder dynamicColumns=new StringBuilder();
		dynamicColumns.append("{");
		for(DynamicColumnDefinition dynamicColumnDefinition:dynamicColumnDefinitions){
			dynamicColumns.append("\"");
			dynamicColumns.append(dynamicColumnDefinition.getName());
			dynamicColumns.append("\":");
			dynamicColumns.append(JsonParser.object2Json(dynamicColumnDefinition));
			dynamicColumns.append(",");
		}
		if(dynamicColumns.charAt(dynamicColumns.length()-1)==','){
			dynamicColumns.deleteCharAt(dynamicColumns.length()-1);
		}
		dynamicColumns.append("}");
		return dynamicColumns.toString();
	}

	/**
	 * 若该字段editoptions不为null或空字符串，
	 * 并且该字段的值是枚举类的全名，
	 * 则把该字段封装为'key':'value','key':'value','key':'value' ...形式的字符串。
	 * @param dfo
	 */
	private void formatterEnum(DynamicColumnDefinition dfo){
		String editoptions=dfo.getEditoptions();
		if(StringUtils.isNotEmpty(editoptions)&&DataType.ENUM.equals(dfo.getType())){
			StringBuilder opitions=new StringBuilder(); 
			try {
				Object[] objs = Class.forName(editoptions).getEnumConstants();
				int i=0;
				for(Object obj : objs){
					i++;
					opitions.append("'").append(obj.toString())
					.append("':'")
//					.append(textProvider.getText(BeanUtils.getProperty(obj, "code")))
					.append("'");
					if(i<objs.length){
						opitions.append(",");
					}
				}
			} catch (Exception e) {
			}
			dfo.setEditoptions(opitions.toString());
		}
	}
	
	//标签结束时调用的处理方法
	public int doEndTag(){
		//继续执行后续的JSP页面的内容
		return Tag.EVAL_PAGE;
	}
	
	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getSubGrid() {
		return subGrid;
	}

	public void setSubGrid(String subGrid) {
		this.subGrid = subGrid;
	}

	public List<DynamicColumnDefinition> getDynamicColumn() {
		return dynamicColumn;
	}

	public void setDynamicColumn(List<DynamicColumnDefinition> dynamicColumn) {
		this.dynamicColumn = dynamicColumn;
	}

	public String getSubmitForm() {
		return submitForm;
	}

	public void setSubmitForm(String submitForm) {
		this.submitForm = submitForm;
	}

}
