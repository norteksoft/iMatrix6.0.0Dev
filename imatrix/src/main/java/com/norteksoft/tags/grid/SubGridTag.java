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

import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.OrderType;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
public class SubGridTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String gridId;//子表的ID
	private String code;//对应的子表编号
	private String url;//子表URL
	private String pageName;//数据名称
	
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
		ListViewManager listViewManager = (ListViewManager) ContextUtils.getBean("listViewManager");
		ListView listView=listViewManager.getListViewByCode(code);
		List<ListColumn> columns=listView.getColumns();
		parserListColumns(columns);
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("gridId", gridId);
		parserUrl(root,url);
		root.put("ctx", webapp);
		root.put("resourceCtx", PropUtils.getProp("host.resources"));
		root.put("_list_code", code);
		String total=hasTotal(columns);//是否合计
		root.put("pageName", StringUtils.isEmpty(pageName)?"":pageName);
		root.put("rowNumbers", listView.getRowNumbers()?"true":"false");
		root.put("editurl", StringUtils.isNotEmpty(listView.getEditUrl())?listView.getEditUrl():"");
		root.put("rowNum", listView.getRowNum()!=null?listView.getRowNum().toString():"");
		root.put("rowList", StringUtils.isNotEmpty(listView.getRowList())?listView.getRowList():"");
		root.put("multiselect", getMultiselect(listView));
		root.put("multiboxSelectOnly", getMultiboxSelectOnly(listView));
		root.put("sortname", StringUtils.isNotEmpty(listView.getDefaultSortField())?listView.getDefaultSortField():"");
		root.put("sortorder", OrderType.DESC.equals(listView.getOrderType())?"desc":"asc");
		root.put("pagination", listView.getPagination()?"true":"");
		root.put("total", total);
		root.put("columns", getColumns(columns));
		String result = TagUtil.getContent(root, "grid/subGridTag.ftl");
		return result;
	}
	
	private void parserUrl(Map<String, Object> root, String subGridUrl) {
		if(subGridUrl.contains("?")){
			root.put("url", subGridUrl.substring(0,subGridUrl.indexOf("?")));
			root.put("urlParameter", subGridUrl.substring(subGridUrl.indexOf("?")+1,subGridUrl.length()));
		}else{
			root.put("url", url);
			root.put("urlParameter", "");
		}
	}

	private String getMultiselect(ListView listView){
		if(listView.getMultiSelect()!=null&&listView.getMultiSelect()){
			return "true";
		}else {
			return "false";
		}
	}
	
	private String getMultiboxSelectOnly(ListView listView){
		if(listView.getMultiboxSelectOnly()!=null&&listView.getMultiboxSelectOnly()){
			return "true";
		}else{
			return "false";
		}
	}
	
	private String hasTotal(List<ListColumn> columns) {
		for(ListColumn lc:columns){
			if(lc.getTotal()){
				return "true";
			}
		}
		return "false";
	}
	
	private void parserListColumns(List<ListColumn> columns) {
		FormHtmlParser formHtmlParser = (FormHtmlParser) ContextUtils.getBean("formHtmlParser");
		if(columns!=null){
			for(ListColumn lc:columns){
				lc.setInternationName(formHtmlParser.getInternation(lc.getHeaderName()));
				String vs=formHtmlParser.getValueSet(lc,null,null);
				lc.setOptionSet(vs);
			}
		}else{
			columns=new ArrayList<ListColumn>();
		}
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
			col.append("defaultValue:'").append(listColumn.getDefaultValue()).append("',");//枚举
			col.append("headStyle:'").append(listColumn.getHeadStyle()==null?"":listColumn.getHeadStyle()).append("',");
			col.append("sortable:").append(listColumn.getSortable()).append(",");
			col.append("visible:").append(listColumn.getVisible()).append(",");
			col.append("}");
		}
		return "["+col.toString()+"]";
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
}
