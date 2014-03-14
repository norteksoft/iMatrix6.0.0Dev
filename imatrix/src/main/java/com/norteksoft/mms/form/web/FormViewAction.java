package com.norteksoft.mms.form.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.bs.signature.service.SignatureManager;
import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormAttachment;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.ControlType;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ImportFormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.form.service.TableColumnManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Department;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/form")
@ParentPackage("default")
public class FormViewAction extends CrudActionSupport<FormView> {
	private static final long serialVersionUID = 1L;
	
	private static final String INFO_TYPE_SHOW="show";
	private static final String INFO_TYPE_SAVE="save";
	private static final String OCCASION_UPDATE="update";
	private static final String OCCASION_CHANGE_SOURCE="changeSource";
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	private Long formId;
	private String code;//formView编号
	private Integer version;//formView版本
	private FormView formView;
	private Page<FormView> page = new Page<FormView>(0,true);
	
	private Long menuId;
	private Long dataTableId;
	private DataTable table;
	private List<DataTable> dataTables;
	private List<DataTable> defaultsTables;
	private FormViewManager formViewManager;
	private DataTableManager dataTableManager;
	private Long signId;
	@Autowired
	private TableColumnManager tableColumnManager;
	@Autowired
	private SignatureManager signatureManager;
	
	private List<Long> formViewIds;
	
	private String editorId;//编辑器id
	private Long tableColumnId;//数据表字段id
	private TableColumn tableColumn;
	private FormControl formControl;//表单控件
	private String occasion;//区别:改变数据来源或修改属性时
	private String[][] selectList;
	private String formHtml;
	private List<DataTable> dataTableList;
	private String formControlType;
	private String formControlId;
	private String referenceControlValue;
	private String formTypeId;//标准或自定义表单的Id
	private String soleCode;//验证编号的唯一
	private List<Long> formViewDeleteIds;//删除表单视图的Id
	private List<Long> formViewRebackIds;//还原表单试图ID
	private List<Long> formViewDeleteCompleteIds;//彻底删除表单试图ID
 	private List<String[]> urgencyList=new ArrayList<String[]>();
	
	private List<String[]> properties; //数据选择控件/字段集合
	private List<String[]> dataSelectFields=new ArrayList<String[]>();//数据选择控件/表格信息
	
	private Page<Object> datas =new Page<Object>(0,true);
	
	private boolean existTable;
	
	private String states;
	
	private String infoType;//部门人员控件/显示信息和保存信息
	
	private String validateSetting;//校验设置
	
	private String treeType;//部门人员控件中的树的类型
	
	private String multiple;//部门人员控件中的是否是多选树
	
	private String resultId;//部门人员控件中的用于显示信息的组件id
	
	private String hiddenResultId;//部门人员控件中的隐藏域的name属性值
	
	private String inputType;//部门人员控件中的"输入框类型"(input/textArea)
	
	private MenuManager menuManager;
	
	private ListViewManager listViewManager;//
	private DataHandle dataHandle;
	
	private boolean standard;
	
	private Long listViewId;//“标准列表控件”中选中的列表
	private String listViewCode;//“标准列表控件”中选中的列表
	private List<ListView> listViews;//列表视图集合
	private File file;
	private String fileName;
	String dataBase;//数据库类型：oracle、mysql、sqlserver
	
	private String operation;//是更新版本还是增加版本。
	private String tableName;//数据表名称，数据选择、数据获取控件有用到
	
	private List<TableColumn> columns=new ArrayList<TableColumn>();
	private String htmlResult;//html结果
	private String deletedFormViewFolder;
	
	private Long fileCompanyId;//表单编辑控件中上传控件记录当前用户所在公司id
	private Long fileUserId;//表单编辑控件中上传控件记录当前用户id
	private Long fileId;//表单编辑控件中上传控件上传的文件id
	private String controlId;//表单编辑控件中上传控件用于记录该控件上传的附件的用途，保存和查询附件均需要该属性
	private String attachInfo;//表单编辑控件中上传控件查询需要的信息：表名,记录id
	private String myFileName;//用户存放图片上传控件上传的图片
	private String srcFileName;//修改图片上传控件用到
	private String companyCode;//公司编码
	private String pluginType;//公司编码
	private List<FormAttachment> formAttachs;//文件列表
	private List<String[]> dbColumnNames=new ArrayList<String[]>();
	private Integer columnAmount;//修改表单模版的列数
	@Autowired
	private ImportFormViewManager importFormViewManager;
	
	@Autowired
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	@Autowired
	public void setFormViewManager(FormViewManager formViewManager) {
		this.formViewManager = formViewManager;
	}
	@Autowired
	public void setDataTableManager(DataTableManager dataTableManager) {
		this.dataTableManager = dataTableManager;
	}
	@Autowired
	public void setListViewManager(ListViewManager listViewManager) {
		this.listViewManager = listViewManager;
	}
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}
	
	//软删除表单
	@Override
	@Action("form-view-delete")
	public String delete() throws Exception {
		for(Long fvId:formViewDeleteIds){
			formViewManager.deleteFormView(fvId);
		}
		ApiFactory.getBussinessLogService().log("表单管理", 
				"删除表单", 
				ContextUtils.getSystemId("mms"));
		this.addSuccessMessage("删除成功");
		return list();
	}
	
	//还原表单
	@Action("form-view-reback")
	public String rebackForm() throws Exception {
		for(Long id:formViewRebackIds){
			formViewManager.rebackFormView(id);
		}
		ApiFactory.getBussinessLogService().log("表单管理", 
				"删除表单", 
				ContextUtils.getSystemId("mms"));
		this.addSuccessMessage("还原成功");
		deletedFormViewFolder="deleted_form_view";
		return list();
	}
	
	//彻底删除表单
	@Action("form-view-delete-complete")
	public String deleteComplete() throws Exception {
		for(Long id:formViewDeleteCompleteIds){
			formViewManager.deleteFormViewComplete(id);
		}
		ApiFactory.getBussinessLogService().log("表单管理", 
				"彻底删除表单", 
				ContextUtils.getSystemId("mms"));
		this.addSuccessMessage("删除成功");
		deletedFormViewFolder="deleted_form_view";
		return list();
	}
	
	@Override
	public String input() throws Exception {
		//dataTables = dataTableManager.getEnabledDataTables();
		return INPUT;
	}
	@Override
	@Action("list-data")
	public String list() throws Exception {
		List<Menu> menus = menuManager.getEnabledRootMenuByCompany();
		if(menuId==null&&menus.size()>0){
			menuId = menus.get(0).getId();
		}
		if("deleted_form_view".equals(deletedFormViewFolder)){
			if(page.getPageSize()>1){
				formViewManager.getDeletedFormViewPage(page);
				ApiFactory.getBussinessLogService().log("表单管理", 
						"表单已删除列表", 
						ContextUtils.getSystemId("mms"));
				this.renderText(PageUtils.pageToJson(page));
				return null;
			}
		}else{
			if(menuId!=null){
				if(page.getPageSize()>1){
					formViewManager.getFormViewPageByMenu(page, menuId);
					ApiFactory.getBussinessLogService().log("表单管理", 
							"表单列表", 
							ContextUtils.getSystemId("mms"));
					this.renderText(PageUtils.pageToJson(page));
					return null;
				}
			}
		}
		return "list-data";
	}
	@Override
	public String save() throws Exception {
		try {
			ApiFactory.getBussinessLogService().log("表单管理", 
					"保存表单", 
					ContextUtils.getSystemId("mms"));
			String result = formViewManager.validatHtml(htmlResult);
			if(result.equals("ok")){
				formViewManager.saveFormView(formView,menuId,operation,htmlResult);
				
				//修改自定义表单字段别名  相应的列表的列头名应该变过来
				if(!formView.getStandard()&&formView.getFormState()!=DataState.DRAFT){
					formViewManager.setListViewColumName(formView);
				}
				this.renderText("id:"+formView.getId().toString()+";version:"+formView.getVersion());
			}else{
				this.renderText("ms:"+result);
			}
		} catch (Exception e) {
			this.renderText("ms:"+e.getMessage());
		}
		return null;
	}
	public void prepareText() throws Exception {
//		formControl = new FormControl();
		if(StringUtils.isNotEmpty(code)&&version!=null){
			formView = formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
			standard=formView.getStandard();
			if(standard){
				table = formView.getDataTable();
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
		}
		if(formControl==null){
			formControl = new FormControl();
		}else{
			if(StringUtils.isNotEmpty(formControl.getName())&&standard){
				tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getName());
				if(tableColumn!=null)
				tableColumnId=tableColumn.getId();
			}
		}
	}
	
	private void setCommonValue(){
		tableColumn = dataTableManager.getTableColumn(tableColumnId);
		formControl.setControlId(tableColumn.getName());
		if(table!=null)formControl.setTableName(table.getName());
		formControl.setName(tableColumn.getName());
		formControl.setDbName(tableColumn.getDbColumnName());
		formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
		if(tableColumn.getMaxLength()==null||tableColumn.getMaxLength().equals(0)){
			if(tableColumn.getDataType()==DataType.TEXT){
				formControl.setMaxLength(255);
			}else if(tableColumn.getDataType()==DataType.DOUBLE||tableColumn.getDataType()==DataType.FLOAT||tableColumn.getDataType()==DataType.AMOUNT){
				formControl.setMaxLength(25);
			}else if(tableColumn.getDataType()==DataType.INTEGER||tableColumn.getDataType()==DataType.NUMBER){
				formControl.setMaxLength(10);
			}else if(tableColumn.getDataType()==DataType.LONG){
				formControl.setMaxLength(19);
			}else if(tableColumn.getDataType()==DataType.BOOLEAN){
				formControl.setMaxLength(1);
			}else if(tableColumn.getDataType()==DataType.BLOB||tableColumn.getDataType()==DataType.CLOB||tableColumn.getDataType()==DataType.COLLECTION||tableColumn.getDataType()==DataType.ENUM||tableColumn.getDataType()==DataType.REFERENCE||tableColumn.getDataType()==DataType.TIME){
				formControl.setMaxLength(null);
			}
		}else{
			formControl.setMaxLength(tableColumn.getMaxLength());
		}
		formControl.setDataType(tableColumn.getDataType());
		if(StringUtils.isNotEmpty(tableColumn.getDefaultValue())){
			formControl.setControlValue(tableColumn.getDefaultValue());
		}
	}
	/**
	 * 转向单行文本的设置页面
	 * @return
	 * @throws Exception
	 */
	public String text() throws Exception {
		String result = "text";
		dataBase=PropUtils.getDataBase();
		//计算控件中的计算公式字段里含有符号+时从js中提交到后台为空，所以在js中把符号+替换成了符号@，在此在把@替换回+
		if(StringUtils.isNotEmpty(formControl.getComputational())&&formControl.getComputational().contains("@")){
			formControl.setComputational(formControl.getComputational().replaceAll("@", "+"));
		}
		//自定义列表控件中的计算公式字段里含有符号+时从js中提交到后台为空，所以在js中把符号+替换成了符号@，在此在把@替换回+
		//自定义列表控件中的计算公式字段里含有符号%时从js中提交到后台为空，所以在js中把符号%替换成了符号~，在此在把~替换回%
		if(StringUtils.isNotEmpty(formControl.getLcCals())&&(formControl.getLcCals().contains("@")||formControl.getLcCals().contains("~"))){
			formControl.setLcCals(formControl.getLcCals().replaceAll("@", "+").replaceAll("~", "%"));
		}
		companyCode = ContextUtils.getCompanyCode();
		
		switch (formControl.getControlType()) {
		case SELECT_MAN_DEPT:
//			if(StringUtils.isNotEmpty(formControl.getSaveDeptControlValue())){
//				tableColumn = dataTableManager.getTableColumn(formControl.getSaveDeptControlValue());
//				Long tableId=tableColumn.getDataTableId();
//				table = dataTableManager.getDataTable(tableId);
//				columns=tableColumnManager.getTableColumnByDataTableId(tableId);
//			}
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(INFO_TYPE_SHOW.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getShowDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getShowDeptControlValue());
						if(tableColumn!=null)
						formControl.setShowDeptControlId(tableColumn.getName());
					}
				}else if(INFO_TYPE_SAVE.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getSaveDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getSaveDeptControlValue());
						if(tableColumn!=null)
						formControl.setSaveDeptControlId(tableColumn.getName());
					}
				}
			}
			result= "selectManOrDept";
			break;
		case CALCULATE_COMPONENT:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					setCommonValue();
				}
			}
			result= "calculateComponent";
			break;
		case PULLDOWNMENU:
			packagingSelectValues(occasion,tableColumnId,formControl);
			result= "pullDownMenu";
			break;
		case DATA_SELECTION:
			dataTableList=dataTableManager.getAllEnabledDataTables();
			if(formControl.getDataSrc()!=null){
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null)
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
			if(formControl.getDataFields()!=null)dataSelectFields.add(formControl.getDataFields().split(","));
			if(formControl.getDataFieldNames()!=null)	dataSelectFields.add(formControl.getDataFieldNames().split(","));
			if(formControl.getDataControlIds()!=null)dataSelectFields.add(formControl.getDataControlIds().split(","));
			if(formControl.getDataQuerys()!=null){
				String[] querys=formControl.getDataQuerys().split(",");
				String[] myQuerys=new String[querys.length];
				for(int i=0;i<querys.length;i++){
					if(querys[i].equals("0")){
						myQuerys[i]="否";
					}else{
						myQuerys[i]="是";
					}
				}
				dataSelectFields.add(myQuerys);
			}
			result= "dataSelection";
			break;
		case DATA_ACQUISITION:
			dataTableList=dataTableManager.getAllEnabledDataTables();
			if(formControl.getDataSrc()!=null){
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null)
				columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			}
			if(formControl.getDataFields()!=null)dataSelectFields.add(formControl.getDataFields().split(","));
			if(formControl.getDataFieldNames()!=null)	dataSelectFields.add(formControl.getDataFieldNames().split(","));
			if(formControl.getDataControlIds()!=null)dataSelectFields.add(formControl.getDataControlIds().split(","));
			result= "dataAcquisition";
			break;
		case URGENCY:
			if(formControl.getUrgencyValues()!=null)urgencyList.add(formControl.getUrgencyValues().split(","));
			if(formControl.getUrgencyDescribes()!=null)	urgencyList.add(formControl.getUrgencyDescribes().split(","));
			result= "urgency";
			break;
		case CREATE_SPECIAL_TASK:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					tableColumn = dataTableManager.getTableColumn(tableColumnId);
					formControl.setControlId(tableColumn.getName());
					formControl.setName(tableColumn.getName());
					formControl.setDbName(tableColumn.getDbColumnName());
					formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
				}
			}
			result= "specialTask";
			break;
		case SPECIAL_TASK_TRANSACTOR:
			result= "specialTaskTransactor";
			break;
		case TEXTAREA:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					setCommonValue();
				}
			}
			result = "textArea";
			break;
		case TIME:
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				if(OCCASION_CHANGE_SOURCE.equals(occasion)){
					setCommonValue();
				}
			}
			result = "time";
			break;
		case LIST_CONTROL:
//			lcTitles,lcSums,lcSizes,lcCals,dataFields
			dataTableList=dataTableManager.getDefaultDataTables();
			if(StringUtils.isNotEmpty(formControl.getDataSrc())){
//				table = dataTableManager.getDataTable(Long.valueOf(formControl.getDataSrc()));
				table = dataTableManager.getDataTableByTableName(formControl.getDataSrc());
				if(table!=null){
					columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
					for(TableColumn field:columns){
						if(field.getDbColumnName()!=null&&field.getDbColumnName().contains("dt_")){
							String[] column=new String[2];
							column[0]=field.getDbColumnName()+":"+field.getDataType();
							column[1]=field.getAlias();
							dbColumnNames.add(column);
						}
					}
					if(formControl.getDataFields()!=null){//对应的字段
						dataSelectFields.add(formControl.getDataFields().split(","));
					}
					if(formControl.getLcTitles()!=null){//列表控件表头项目
						dataSelectFields.add(formControl.getLcTitles().split(","));
					}
					if(formControl.getLcSums()!=null){//合计
						dataSelectFields.add(formControl.getLcSums().split(","));
					}
					if(formControl.getLcSizes()!=null){//宽度
						dataSelectFields.add(formControl.getLcSizes().split(","));
					}
					if(formControl.getLcCals()!=null){//计算公式
						dataSelectFields.add(formControl.getLcCals().split(","));
					}
				}
			}
			result = "listControl";
			break;
		case STANDARD_LIST_CONTROL:
			if(formView!=null){
				listViews=listViewManager.getListViewsBySystem(formView.getMenuId());
			}
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					tableColumn = dataTableManager.getTableColumn(tableColumnId);
					formControl.setControlId(tableColumn.getName());
					formControl.setName(tableColumn.getName());
					formControl.setDbName(tableColumn.getDbColumnName());
					formControl.setTitle(dataTableManager.getInternation(tableColumn.getAlias()));
					formControl.setDataType(tableColumn.getDataType());
				}
			}
			result = "standardListControl";
			break;
		case BUTTON:
			result = "button";
			break;
		case LABEL:
			result = "label";
			break;
		case SIGNATURE_CONTROL:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(INFO_TYPE_SHOW.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getShowDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getShowDeptControlValue());
						if(tableColumn!=null)
						formControl.setShowDeptControlId(tableColumn.getName());
					}
				}else if(INFO_TYPE_SAVE.equals(infoType)){
					if(StringUtils.isNotEmpty(formControl.getSaveDeptControlValue())){
						tableColumn =tableColumnManager.getTableColumnByColName(table.getId(), formControl.getSaveDeptControlValue());
						if(tableColumn!=null)
						formControl.setSaveDeptControlId(tableColumn.getName());
					}
				}
			}
			result= "signature";
			break;
		case ATTACH_UPLOAD:
			if(formControl.getMaxLength()==null){
				formControl.setMaxLength(50);
			}
			if(StringUtils.isEmpty(formControl.getFormat())){
				formControl.setFormat("*.*");
				formControl.setTitle("所有文件");
			}
			result= "attachUpload";
			break;
		case RADIO:
			packagingSelectValues(occasion, tableColumnId, formControl);
			result= "radioCheckbox";
			break;
		case CHECKBOX:
			packagingSelectValues(occasion, tableColumnId, formControl);
			result= "radioCheckbox";
			break;
		case IMAGE:
			if(formControl.getComponentWidth()==null){
				formControl.setComponentWidth(200);
				formControl.setComponentHeight(100);
			}
			formAttachs = formViewManager.getFiles("formResources/formImages/"+ContextUtils.getCompanyCode());
			result= "image";
			break;
		case IMAGE_UPLOAD:
			if(formControl.getComponentWidth()==null){
				formControl.setComponentWidth(50);
				formControl.setComponentHeight(50);
			}
			result= "imageUpload";
			break;
		case JAVASCRIPT_CSS:
			formAttachs = formViewManager.getFiles("formResources/formJCs/"+ContextUtils.getCompanyCode());
			result= "javaScriptCss";
			break;
		case MACRO:
			result= "macro";
			break;
		case PLACEHOLDER:
			if(formControl.getComponentWidth()==null){
				formControl.setComponentWidth(200);
				formControl.setComponentHeight(100);
			}
			result= "placeholder";
			break;
		default:
			if(OCCASION_CHANGE_SOURCE.equals(occasion)){
				if(tableColumnId!=null&&tableColumnId.intValue()!=0){
					setCommonValue();
				}
			}
			break;
		}
		return result;
	}
	
	private void packagingSelectValues(String occasion, Long tableColumnId, FormControl formControl) {
		if(OCCASION_CHANGE_SOURCE.equals(occasion)){
			if(tableColumnId!=null&&tableColumnId.intValue()!=0){
				setCommonValue();
			}
		}
		String selectValues=formControl.getSelectValues();
		if(selectValues!=null){
			String[] vals=selectValues.split(",");
			selectList=new String[vals.length][2];
			for(int i=0;i<vals.length;i++){
				if(vals[i].contains(";")){
					selectList[i]=vals[i].split(";");
				}
			}
		}
	}
	public String validateFormControl() throws Exception{
		return null;
	}
	
	/**
	 * 显示表单属性窗口
	 * @return
	 * @throws Exception
	 */
	public String getTabelColumns() throws Exception {
		StringBuffer str = new StringBuffer();
		if(ControlType.LIST_CONTROL.toString().equals(formControlType)||ControlType.DATA_SELECTION.toString().equals(formControlType) ||ControlType.DATA_ACQUISITION.toString().equals(formControlType)){
			str.append("<option value=\"\">请选择字段</option>");
			if(StringUtils.isNotEmpty(tableName)){
				table=dataTableManager.getDataTableByTableName(tableName);
			}
		}
		if(table!=null){
			List<TableColumn> columns=tableColumnManager.getTableColumnByDataTableId(table.getId());
			if(columns!=null && !columns.isEmpty()){
				for (TableColumn field : columns) {
					if("LIST_CONTROL".equals(formControlType)){
						if(field.getDbColumnName()!=null&&field.getDbColumnName().contains("dt_")){
							str.append("<option value=\""+field.getDbColumnName()+":"+field.getDataType()+"\">"+dataTableManager.getInternation(field.getAlias())+"</option>");
						}
					}else{
						if(field.getDbColumnName()!=null&&field.getDbColumnName().contains("dt_")){
							str.append("<option value=\""+field.getDbColumnName()+"\">"+dataTableManager.getInternation(field.getAlias())+"</option>");
						}
					}
				}
			}
		}
		this.renderText(str.toString());
		return null;
	}
	
	/**
	 * 数据选择控件/显示
	 * @return
	 * @throws Exception
	 */
	public String showDataSelection() throws Exception {
		if(version==0){
			version=1;
		}
		formView=formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
		properties=formViewManager.getDataProperties(formView.getHtml(), formControlId);
		Map<String, String[]> parameterMap=Struts2Utils.getRequest().getParameterMap();
		if(datas.getPageSize()>1){
			datas = formViewManager.getDataExcutionSql(datas,formView.getHtml(), formControlId,parameterMap,properties);
			StringBuilder json = new StringBuilder();
			json.append("{\"page\":\"");
			json.append(datas.getPageNo());
			json.append("\",\"total\":");
			json.append(datas.getTotalPages());
			json.append(",\"records\":\"");
			json.append(datas.getTotalCount());
			json.append("\",\"rows\":");
			json.append("[");
			for(Object obj:datas.getResult()){
				StringBuilder sb=new StringBuilder();
				sb.append("{");
				int i=0;
				Object val=null;
				for(String[] strs:properties){
					if(properties.size()==1){
						val=obj;
					}else{
						val=((Object[])obj)[i];
					}
					
					if(val==null){
						val="&nbsp;";
					}
					sb.append("\"")
					 .append(strs[0])
					 .append("\"")
					 .append(":")
					 .append("\"");
					 if(val!=null){
						 sb.append(val.toString().replaceAll("\"", "_@_#"));
					 }else{
						 sb.append(val);
					 }
					 
					sb.append("\"")
					 .append(",");
					i++;
				}
				//去掉最后一个逗号
				if(sb.charAt(sb.length()-1)==','){
					sb.delete(sb.length()-1, sb.length());
				}
				sb.append("}");
				json.append(sb);
				json.append(",");
			}
			//去掉最后一个逗号
			if(json.charAt(json.length()-1)==','){
				json.delete(json.length()-1, json.length());
			}
			json.append("]");
			json.append("}");
			this.renderText(PageUtils.disposeSpecialCharacter(json.toString()));
			return null;
		}
		StringBuilder colNames=new StringBuilder();//数据选择控件中列表jqgrid中的colNames
		StringBuilder colModel=new StringBuilder();//数据选择控件中列表jqgrid中的colModel
		colNames.append("[");
		colModel.append("[");
		for(String[] strs:properties){
			colModel.append("{name:'").append(strs[0]).append("',")
			.append("index:'").append(strs[0]).append("'}").append(",");
			colNames.append("'").append(strs[1]).append("'").append(",");
		}
		//去掉最后一个逗号
		if(colModel.charAt(colModel.length()-1)==','){
			colModel.append("{name:'act',index:'act',width:30,align:'center',formatter:function addAct(){return \"<a href='#' class='small-button-bg' onclick='addValue(this);'><span class='ui-icon ui-icon-plusthick'></span></a>\"}}");
		}
		//去掉最后一个逗号
		if(colNames.charAt(colNames.length()-1)==','){
			colNames.append("'操作'");
		}
		colModel.append("]");
		colNames.append("]");
		DataTable dataTable=formViewManager.getDataSource(formView.getHtml(), formControlId);
		if(StringUtils.isNotEmpty(dataTable.getEntityName())){
			existTable=true;
		}else{
			existTable=false;
		}
		String resourceCtx=PropUtils.getProp("host.resources");
		String ctx=PropUtils.getProp("host.app");
		Map<String, Object> root=new HashMap<String, Object>();
		root.put("colNames", colNames);
		root.put("colModel", colModel);
		root.put("resourcesCtx", resourceCtx);
		root.put("ctx", ctx);
		root.put("pageName", "datas");
		root.put("code", code);
		root.put("version", version);
		root.put("formControlId", formControlId);
		root.put("properties", properties);
		root.put("theme", ContextUtils.getTheme());
		if(parameterMap!=null){
			StringBuilder urlParam=new StringBuilder();
			String[] value = null;
			for(String[] strs:properties){
				value=parameterMap.get(strs[0]);
 				if(value!=null&&value.length>0&&StringUtils.isNotEmpty(value[0])){
					urlParam.append("&").append(strs[0]).append("=").append(value[0]);
				}
			}
 			root.put("urlParam", urlParam);
		}
		String html = TagUtil.getContent(root, "show-data-selection-tag.ftl");
		//将信息内容输出到JSP页面
		HttpServletResponse response = Struts2Utils.getResponse();
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(html);
		return null;
	}
	
	
	/**
	 * 数据选择控件/显示
	 * @return
	 * @throws Exception
	 */
	public String getData() throws Exception {
		formView=formViewManager.getFormViewByCodeAndVersion(ContextUtils.getCompanyId(),code,version);
		this.renderText(formViewManager.getDataAcquisitionResult(formView.getHtml(), formControlId, referenceControlValue));
		return null;
	}
	
	/**
	 * （人员部门控件）显示树
	 * @return
	 * @throws Exception
	 */
	public String createTree() throws Exception {
		return "create-tree";
	}
	
	public void prepareChoiceColumn() throws Exception {
		table = dataTableManager.getDataTable(dataTableId);
	}
	/**
	 * 转向选择数据列的页面
	 * @return
	 * @throws Exception
	 */
	public String choiceColumn() throws Exception {
		return "choiceColumn";
	}
	/**
	 * 显示表单管理树
	 * @return
	 * @throws Exception
	 */
	public String formTree() throws Exception{
		List<Menu> menus = menuManager.getRootMenuByCompany();
		java.util.Collections.sort(menus);
		StringBuilder tree = new StringBuilder("[ ");
		for(Menu menu :menus){
				tree.append(JsTreeUtils.generateJsTreeNode(menu.getId().toString(), "root", menu.getName())).append(",");
		}
		JsTreeUtils.removeLastComma(tree);
		tree.append(" ]");
		renderText(tree.toString());
		return null;
	}
	public String childDefaultForm(){
		StringBuilder tree = new StringBuilder();
		List<DataTable> tables=dataTableManager.getDefaultDataTables();
		for(DataTable dt:tables){
			tree.append(JsTreeUtils.generateJsTreeNode(dt.getId().toString(), "leaf", dataTableManager.getInternation(dt.getAlias()))).append(",");	
		}
		return tree.toString();
	}
	@Override
	protected void prepareModel() throws Exception {
		if(formId==null){
			formView = new FormView();
		}else{
			formView = formViewManager.getFormView(formId);
		}
		if(dataTableId!=null){
			table = dataTableManager.getDataTable(dataTableId);
			formView.setDataTable(table);
		}
	}
	
	public String copy() throws Exception{
		formView = formViewManager.getFormView(formId);
		return "copy";
	}
	public void prepareSavecopy() throws Exception{
		formView = new FormView();
	}
	@Action("form-view-savecopy")
	public String savecopy() throws Exception{
		formViewManager.savecopy(formId, menuId, formView);
		return "list-data";
	}
	/**
	 * 改变表单的状态(草稿->启用;启用->禁用;禁用->启用)
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("form-view-state")
	public String changeFormState()throws Exception{
		String mssge = formViewManager.changeFormState(formViewIds, menuId);
		addSuccessMessage(mssge);
		return list();
	}
	
	public FormView getModel() {
		return formView;
	}
	
	public void prepareNext() throws Exception {
		this.prepareModel();
	}
	public String next() throws Exception{
		//dataTables = dataTableManager.getEnabledDataTables();
		if(formId!=null&&formId!=0){
			formView = formViewManager.getFormView(formId);
		}
		return "editor";
	}
	
	public void prepareUpdateColumnAmount() throws Exception {
		this.prepareModel();
	}
	
	@Action("form-view-updateColumnAmount")
	public String updateColumnAmount() throws Exception{
//		formView = formViewManager.getFormView(formId);
		formView = formViewManager.updateColumnAmount(formId,columnAmount);
//		Integer a=columnAmount;
		return "form-view-editor";
	}
	
	public void preparePreview() throws Exception {
		this.prepareModel();
	}
	public String preview() throws Exception {
		code = formView.getCode();
		version = formView.getVersion();
		validateSetting = formViewManager.getValidateSetting(formView);
		if(formView!=null)formHtml=formViewManager.getFormHtml(formView,formView.getHtml());
		return "preview";
	}
	/**
	 * 验证编号的唯一
	 * @return
	 * @throws Exception
	 */
	public String validateFormCode() throws Exception {
		this.renderText(formViewManager.isFormCodeExist(soleCode,null).toString());
		return null;
	}
	
	/**
	 * 导出表单
	 * @return
	 * @throws Exception
	 */
	@Action("export-form-view")
	public String exportFormView() throws Exception{
		HttpServletResponse response = Struts2Utils.getResponse();
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("export-form.zip","utf-8"));
		String name="form-view";
		String path="basic-data";
		File folder = new File(path);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file = new File(path+"/"+name+".xls");
		OutputStream out=new FileOutputStream(file);
		dataHandle.exportFormView(out,formViewIds,menuId);
		OutputStream fileOut=response.getOutputStream();
		ZipUtils.zipFolder(path, fileOut);
		if(fileOut!=null)fileOut.close();
		FileUtils.deleteDirectory(new File(path));//删除文件夹
		return null;
	}
	@Action("show-import-form-view")
	public String showImportDataTable() throws Exception{
		return "show-import-form-view";
	}
	/**
	 * 导入数据表及字段信息
	 * @return
	 * @throws Exception
	 */
	@Action("import-form-view")
	public String importDataTable() throws Exception{
		String result = "";
		if(fileName==null || !fileName.endsWith(".zip")){
			result="请选择zip文件格式";
		}
		boolean success = true;
		try {
			String importRootPath="basic-data-temp";
			ZipFile zipFile = new ZipFile(file);
			ZipUtils.unZipFileByOpache(zipFile, importRootPath); 
			importFormView(importRootPath);
			FileUtils.deleteDirectory(new File(importRootPath));
		} catch (Exception e) {
			success = false;
		}
		if(success){
			result="导入成功";
		}else{
			result="导入失败，请检查zip文件格式";
		}
		
		renderText(result);
		return null;
	}
	
	private void importFormView(String importRootPath) {
		File f=new File(importRootPath+"/form-view.xls");
		if(f.exists()){
			dataHandle.importFormView(f, ContextUtils.getCompanyId());
		}
		//读取表单内容
		File dir=new File(importRootPath+"/formview");
		if(dir.exists()){
			File[]files=dir.listFiles();
			for(int i=0;i<files.length;i++){
				File filei=files[i];
				String fileName=filei.getName().split("\\.")[0];
				String formCode=fileName.substring(0,fileName.lastIndexOf("#"));
				String formVersion=fileName.substring(fileName.lastIndexOf("#")+1);
				FormView formview=formViewManager.getCurrentFormViewByCodeAndVersion(formCode, Integer.parseInt(formVersion));
				try {
					String html=FileUtils.readFileToString(filei, "UTF-8");
					if(StringUtils.isNotEmpty(html.toString())){
						formview.setHtml(html.toString());
					}
					formViewManager.save(formview);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * 显示签章图片
	 * @return
	 * @throws Exception 
	 */
	@Action("form-view-showPic")
	public String showPic() throws Exception{
		if(signId!=null){
			Signature signature=signatureManager.getSignatureById(signId);
			String uploadPath = PropUtils.getProp("application.properties","upload.file.path");
			if(StringUtils.isEmpty(uploadPath)){
				uploadPath = PropUtils.getProp("applicationContent.properties","upload.file.path");
			}
			
			String companyCode=null;
			User user = ApiFactory.getAcsService().getUserById(signature.getUserId());
			if(user!=null){
				Long subCompanyId = user.getSubCompanyId();
				if(subCompanyId==null){
					companyCode = ContextUtils.getCompanyCode();
				}else{
					Department branch = ApiFactory.getAcsService().getDepartmentById(subCompanyId);
					if(branch!=null)companyCode = branch.getCode();
				}
			}
			String path=uploadPath+"/"+"Signature";
			if(StringUtils.isNotEmpty(companyCode)){//存在分支机构时
				path = path+"/"+companyCode;
			}
			path= path+signature.getPictureSrc();
			File file=new File(path);
			if(!file.exists()){
				path = uploadPath+"/"+"Signature"+signature.getPictureSrc();
				file=new File(path);
			}
			if(file.exists())PropUtils.showPic(file);
		}
		return null;
	}
	
	/**
	 * 表单编辑器中上传控件的上传文件处理
	 * @return
	 * @throws Exception 
	 */
	@Action("uploadAttachment")
	public String uploadAttachment() throws Exception{
		HttpServletRequest request=ServletActionContext.getRequest();
		String fileName=request.getParameter("Filename");
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		//把request强转，因为struts从新封装了request(Filedata是它的参数不能改变)
		MultiPartRequestWrapper wrapper=(MultiPartRequestWrapper)request;
		File filePath=wrapper.getFiles("Filedata")[0];
		FileInputStream input = new FileInputStream(filePath);
		
		String companyIdStr = request.getParameter("fileCompanyId");
		String userIdStr = request.getParameter("fileUserId");
		String attachInfo = request.getParameter("attachInfo");
		pluginType = request.getParameter("pluginType");
		controlId = request.getParameter("controlId");
		Long companyId = null;
		Long userId = null;
		if(StringUtils.isNotEmpty(companyIdStr)){
			companyId = Long.parseLong(companyIdStr);
		}
		if(StringUtils.isNotEmpty(userIdStr)){
			userId = Long.parseLong(userIdStr);
		}
		
		ThreadParameters parameters = new ThreadParameters();
		parameters.setCompanyId(companyId);
		parameters.setUserId(userId);
		ParameterUtils.setParameters(parameters);
		
		formViewManager.saveFormAttachment(fileName,input.available(),fileType,PropUtils.getBytes(filePath),attachInfo,controlId,pluginType);
		return null;
	}
	/**
	 * 附件控件的列表
	 * @return
	 * @throws Exception
	 */
	@Action("getAttachments")
	public String getAttachments() throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		ThreadParameters parameters = new ThreadParameters();
		parameters.setCompanyId(fileCompanyId);
		parameters.setUserId(fileUserId);
		ParameterUtils.setParameters(parameters);
		this.renderText(callback+"({data:'"+formViewManager.getAttachments(attachInfo,controlId,pluginType)+"'})");
		return null;
	}
	/**
	 * 附件控件列表中删除附件
	 * @return
	 * @throws Exception
	 */
	@Action("deleteAttachment")
	public String deleteAttachment() throws Exception{
		formViewManager.deleteAttachment(fileId,pluginType);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功'})");
		return null;
	}
	/**
	 * 附件控件列表中下载附件
	 * @return
	 * @throws Exception
	 */
	@Action("downloadAttachment")
	public String downloadAttachment() throws Exception{
		FormAttachment attachment = formViewManager.getAttachment(fileId);
		if(attachment!=null){
			fileName = attachment.getFileName();
		
			String filePath = "";
			if(ControlType.ATTACH_UPLOAD.toString().equals(pluginType)){
				filePath = PropUtils.getProp("form.upload.file.path");
				if(filePath.lastIndexOf("/")==filePath.length()-1){
					filePath = filePath.substring(0,filePath.lastIndexOf("/"));
				}
				filePath = filePath+"/"+attachment.getFilePath();
			}else if(ControlType.IMAGE_UPLOAD.toString().equals(pluginType)){
				String path=FormViewManager.class.getClassLoader().getResource("application.properties").getPath();
				 path=path.substring(1, path.indexOf("WEB-INF/classes"));//获得当前应用所在路径:D:\MyToolbox\eclipse3.5\ws\imatrix-6.0.0.RC\webapp
				 filePath = path+attachment.getFilePath();
			}
			byte[] file=ApiFactory.getFileService().getFile(filePath);
			
			PropUtils.download(fileName, file);
		}
		return null;
	}
	/**************************图片控件******************************/
	@Action("upload-image")
	public String uploadImage() throws Exception{
		try {
			HttpServletRequest request=ServletActionContext.getRequest();
			//把request强转，因为struts从新封装了request(Filedata是它的参数不能改变)
			MultiPartRequestWrapper wrapper=(MultiPartRequestWrapper)request;
			File filePath=wrapper.getFiles("Filedata")[0];
			String fileName=request.getParameter("Filename");
			
			//myFileName:formResources/formImages/2013-12-10/111111.js:trueFileName.js,formResources/formImages/2013-12-10/111111.js:trueFileName.css,...
			if(myFileName.contains(":")){
				String currentFileName = myFileName.substring(myFileName.lastIndexOf(":")+1);
				String currentFilePath = myFileName.substring(0,myFileName.lastIndexOf(":"));
				if(fileName.equals(currentFileName)){
					formViewManager.uploadFile(PropUtils.getBytes(filePath), currentFilePath);
				}
			}else{
				formViewManager.uploadFile(PropUtils.getBytes(filePath), myFileName);
			}
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 图片上传控件的删除属性处理
	 * @return
	 * @throws Exception
	 */
	@Action("delete-upload-file")
	public String deleteUploadImage() throws Exception{
		try {
			formViewManager.deleteImage(srcFileName);
		} catch (Exception e) {
		}
		return null;
	}
	
	@Action("getFiles")
	public String getFiles() throws Exception{
		String callback=Struts2Utils.getParameter("callback");
		formAttachs = formViewManager.getFiles(myFileName);
		this.renderText(callback+"({data:'"+JsonParser.object2Json(formAttachs)+"'})");
		return null;
	}
	
	
	/**
	 * 图片控件、js/css控件下载文件
	 * @return
	 * @throws Exception
	 */
	@Action("download-upload-file")
	public String downloadFile() throws Exception{
		if(StringUtils.isNotEmpty(srcFileName)){
			String path=FormViewManager.class.getClassLoader().getResource("application.properties").getPath();
			 path=path.substring(1, path.indexOf("WEB-INF/classes"));
			String folderPath = path+srcFileName;
			byte[] file=ApiFactory.getFileService().getFile(folderPath);
			
			if(srcFileName.indexOf("~~")>=0){
				String fileName = srcFileName.substring(srcFileName.lastIndexOf("/")+1,srcFileName.lastIndexOf("~~"));
				PropUtils.download(fileName, file);
			}
		}
		return null;
	}
	
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public Page<FormView> getPage() {
		return page;
	}
	public void setPage(Page<FormView> page) {
		this.page = page;
	}
	public List<DataTable> getDataTables() {
		return dataTables;
	}
	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}
	public void setFormViewIds(List<Long> formViewIds) {
		this.formViewIds = formViewIds;
	}
	public Long getDataTableId() {
		return dataTableId;
	}
	public String getEditorId() {
		return editorId;
	}
	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}
	public TableColumn getTableColumn() {
		return tableColumn;
	}
	public Long getTableColumnId() {
		return tableColumnId;
	}
	public void setTableColumnId(Long tableColumnId) {
		this.tableColumnId = tableColumnId;
	}
	public FormControl getFormControl() {
		return formControl;
	}
	public void setFormControl(FormControl formControl) {
		this.formControl = formControl;
	}
	public DataTable getTable() {
		return table;
	}
	public String getValidateSetting() {
		return validateSetting;
	}
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public String[][] getSelectList() {
		return selectList;
	}
	public String getFormHtml() {
		return formHtml;
	}
	public List<DataTable> getDataTableList() {
		return dataTableList;
	}
	public void setFormControlType(String formControlType) {
		this.formControlType = formControlType;
	}
	public void setFormControlId(String formControlId) {
		this.formControlId = formControlId;
	}
	public String getFormControlId() {
		return formControlId;
	}
	public List<String[]> getProperties() {
		return properties;
	}
	
	public Page<Object> getDatas() {
		return datas;
	}
	public boolean isExistTable() {
		return existTable;
	}
	public List<String[]> getDataSelectFields() {
		return dataSelectFields;
	}
	public void setReferenceControlValue(String referenceControlValue) {
		this.referenceControlValue = referenceControlValue;
	}
	public List<String[]> getUrgencyList() {
		return urgencyList;
	}
	public String getTreeType() {
		return treeType;
	}
	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getResultId() {
		return resultId;
	}
	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	public String getHiddenResultId() {
		return hiddenResultId;
	}
	public void setHiddenResultId(String hiddenResultId) {
		this.hiddenResultId = hiddenResultId;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getFormTypeId() {
		return formTypeId;
	}
	public void setFormTypeId(String formTypeId) {
		this.formTypeId = formTypeId;
	}
	public List<DataTable> getDefaultsTables() {
		return defaultsTables;
	}
	public void setDefaultsTables(List<DataTable> defaultsTables) {
		this.defaultsTables = defaultsTables;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public FormView getFormView() {
		return formView;
	}
	public void setFormView(FormView formView) {
		this.formView = formView;
	}
	public String getStates() {
		return states;
	}
	public void setStates(String states) {
		this.states = states;
	}
	public boolean isStandard() {
		return standard;
	}
	public void setStandard(boolean standard) {
		this.standard = standard;
	}
	public String getSoleCode() {
		return soleCode;
	}
	public void setSoleCode(String soleCode) {
		this.soleCode = soleCode;
	}
	public List<Long> getFormViewDeleteIds() {
		return formViewDeleteIds;
	}
	public void setFormViewDeleteIds(List<Long> formViewDeleteIds) {
		this.formViewDeleteIds = formViewDeleteIds;
	}
	public Long getListViewId() {
		return listViewId;
	}
	public void setListViewId(Long listViewId) {
		this.listViewId = listViewId;
	}
	public List<ListView> getListViews() {
		return listViews;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public List<TableColumn> getColumns() {
		return columns;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setHtmlResult(String htmlResult) {
		this.htmlResult = htmlResult;
	}
	
	public String getListViewCode() {
		return listViewCode;
	}
	public void setListViewCode(String listViewCode) {
		this.listViewCode = listViewCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Long getSignId() {
		return signId;
	}
	public void setSignId(Long signId) {
		this.signId = signId;
	}
	public String getDataBase() {
		return dataBase;
	}
	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}
	public String getDeletedFormViewFolder() {
		return deletedFormViewFolder;
	}
	public void setDeletedFormViewFolder(String deletedFormViewFolder) {
		this.deletedFormViewFolder = deletedFormViewFolder;
	}
	public List<Long> getFormViewRebackIds() {
		return formViewRebackIds;
	}
	public void setFormViewRebackIds(List<Long> formViewRebackIds) {
		this.formViewRebackIds = formViewRebackIds;
	}
	public List<Long> getFormViewDeleteCompleteIds() {
		return formViewDeleteCompleteIds;
	}
	public void setFormViewDeleteCompleteIds(List<Long> formViewDeleteCompleteIds) {
		this.formViewDeleteCompleteIds = formViewDeleteCompleteIds;
	}
	public Long getFileCompanyId() {
		return fileCompanyId;
	}
	public void setFileCompanyId(Long fileCompanyId) {
		this.fileCompanyId = fileCompanyId;
	}
	public Long getFileUserId() {
		return fileUserId;
	}
	public void setFileUserId(Long fileUserId) {
		this.fileUserId = fileUserId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public void setMyFileName(String myFileName) {
		this.myFileName = myFileName;
	}
	public String getSrcFileName() {
		return srcFileName;
	}
	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}
	public void setAttachInfo(String attachInfo) {
		this.attachInfo = attachInfo;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
	public List<FormAttachment> getFormAttachs() {
		return formAttachs;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
	public List<String[]> getDbColumnNames() {
		return dbColumnNames;
	}
	public void setDbColumnNames(List<String[]> dbColumnNames) {
		this.dbColumnNames = dbColumnNames;
	}
	public Integer getColumnAmount() {
		return columnAmount;
	}
	public void setColumnAmount(Integer columnAmount) {
		this.columnAmount = columnAmount;
	}
}
