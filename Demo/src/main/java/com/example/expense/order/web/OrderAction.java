package com.example.expense.order.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.Order;
import com.example.expense.entity.OrderItem;
import com.example.expense.order.service.CommonImportInjectManager;
import com.example.expense.order.service.CommonImportManager;
import com.example.expense.order.service.OrderItemManager;
import com.example.expense.order.service.OrderManager;
import com.norteksoft.acs.base.web.struts2.Struts2Utils;
import com.norteksoft.product.api.entity.Role;
import com.norteksoft.mms.base.DynamicColumnValues;
import com.norteksoft.mms.base.ExportDynamicColumnValues;
import com.norteksoft.mms.base.MmsUtil;
import com.norteksoft.mms.base.utils.view.DynamicColumnDefinition;
import com.norteksoft.mms.base.utils.view.ExportData;
import com.norteksoft.mms.base.utils.view.GridColumnInfo;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ExcelExportEnum;
import com.norteksoft.product.util.ExcelExporter;
import com.norteksoft.product.util.JsTreeUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.example.expense.base.utils.Util;


@Namespace("/order")
@ParentPackage("default")
public class OrderAction extends CrudActionSupport<Order> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Order order;
	private String deleteIds;
	private String position;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private OrderItemManager orderItemManager;
	@Autowired
	private CommonImportInjectManager commonImportInjectManager;
	@Autowired
	private MmsUtil mmsUtil;
	private Page<Order> page;
	private Page<Order> portalOrderpage = new Page<Order>(Page.EACH_PAGE_FIVE, true);;
	private Page<OrderItem> pageItem;
	private Page<Order> dynamicPage;
	private List<DynamicColumnDefinition> dynamicColumn=new ArrayList<DynamicColumnDefinition>();
	private GridColumnInfo gridColumnInfo;
	private String currentInputId;
	private File file;
	private String fileName;
	private String type;
	
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setPage(Page<Order> page) {
		this.page = page;
	}
	
	public Page<Order> getPage() {
		return page;
	}
	
	public Page<OrderItem> getPageItem() {
		return pageItem;
	}

	public void setPageItem(Page<OrderItem> pageItem) {
		this.pageItem = pageItem;
	}

	public Page<Order> getDynamicPage() {
		return dynamicPage;
	}

	public void setDynamicPage(Page<Order> dynamicPage) {
		this.dynamicPage = dynamicPage;
	}

	public List<DynamicColumnDefinition> getDynamicColumn() {
		return dynamicColumn;
	}

	public GridColumnInfo getGridColumnInfo() {
		return gridColumnInfo;
	}

	public Order getModel() {
		return order;
	}
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			order=new Order();
			order.setCompanyId(ContextUtils.getCompanyId());
			order.setCreatedTime(new Date());
			order.setCreator(ContextUtils.getUserName());
			Integer index=orderManager.getMaxIndex();
			order.setDisplayIndex(index==null?1:index+1);
		}else {
			order=orderManager.getOrder(id);
		}
	}
	
	@Action("input")
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	public void preparePortalInput() throws Exception{
		prepareModel();
	}
	
	@Action("portal-order-input")
	public String portalInput() throws Exception {
		return SUCCESS;
	}
	
	
	
	public void prepareFormviewInput() throws Exception{
		prepareModel();
	}
	@Action("formview-input")
	public String formviewInput() throws Exception {
		return SUCCESS;
	}
	public void prepareFormviewSignInput() throws Exception{
		prepareModel();
	}
	@Action("formview-sign-input")
	public String formviewSignInput() throws Exception {
		return SUCCESS;
	}
	
	public void prepareFormPrint() throws Exception{
		prepareModel();
	}
	@Action("formview-print")
	public String formPrint() throws Exception {
		return SUCCESS;
	}
	
	
	@Action("save")
	@Override
	public String save() throws Exception {
		orderManager.saveOrder(order);
		order=orderManager.getOrder(order.getId());
		id=order.getId();
		//第一次保存订单时发送一条消息
		if(order.getIfCreateMessage()==null||!order.getIfCreateMessage()){
			order.setIfCreateMessage(true);
			ApiFactory.getPortalService().addMessage(ContextUtils.getSystemCode(), ContextUtils.getUserName(), ContextUtils.getLoginName(), ContextUtils.getLoginName(), "订单", "新建一条订单", "/order/input-message.htm?id="+id);
		}
		addActionMessage("保存成功");
		if("formview".equals(position)){
			return "formview-input";
		}else if("controlRow".equals(position)){
			return "control-input";
		}else if("formviewSign".equals(position)){
			return "formview-sign-input";
		}else{
			return "input";
		}
	}
	
	@Action("delete")
	@Override
	public String delete() throws Exception {
		orderManager.deleteOrder(deleteIds);
		if("controlRow".equals(position)){
			return "control-list";
		}
		return "list";
	}
	
	@Action("delete-item")
	public String deleteOrderItem() throws Exception {
		orderItemManager.deleteOrderItem(id);
		String callback=Struts2Utils.getParameter("callback");
		this.renderText(callback+"({msg:'删除成功！'})");
		return null;
	}
	
	
	
	/**
	 * portal打开消息页面
	 * @return
	 * @throws Exception
	 */
	@Action("input-message")
	public String inputMessage() throws Exception {
		HttpServletResponse response = Struts2Utils.getResponse();
		order=orderManager.getOrderById(id);
		if(order==null){
			response.sendRedirect(SystemUrls.getBusinessPath("ems")+"/portal/my-message-error.action?errorInfo="+URLEncoder.encode("此订单已经删除！","UTF-8") );
		}else{
			response.sendRedirect(SystemUrls.getBusinessPath("ems")+"/order/input.htm?id="+id );
		}
		return null;
	}

	@Action("list")
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	@Action("list-datas")
	public String getListDatas() throws Exception {
		page = orderManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}

	public Order getOrder() {
		return order;
	}

	public void setDeleteIds(String deleteIds) {
		this.deleteIds = deleteIds;
	}
	
	@Action("list-order-item")
	public String getOrderItem() throws Exception {
		pageItem=orderManager.getOrderItemById(pageItem,id);
		renderText(PageUtils.pageToJson(pageItem));
		return null;
	}
	
	@Action("dynamic-column-click")
	public String dynamicColumnClick() throws Exception {
		packagingDynamicColumn();
		return "dynamic-column-click";
	}
	@Action("dynamic-list")
	public String getDynamicList() throws Exception {
		packagingDynamicColumn();
		return SUCCESS;
	}
	
	private void packagingDynamicColumn() throws Exception {
		List<Object> productNames=orderManager.getOrderItem();
		for(int i=0;i<productNames.size();i++){
			DynamicColumnDefinition dynamicColumnDefinition=new DynamicColumnDefinition(productNames.get(i).toString(),"productName"+i);
//			dynamicColumnDefinition.setEditable(true);
			dynamicColumnDefinition.setType(DataType.INTEGER);
			dynamicColumnDefinition.setEditRules("required:true");
			dynamicColumnDefinition.setIsTotal(true);
			
			//是否导出true表示导出，false表示不导出，默认为true
//			dynamicColumnDefinition.setExportable(false);
			
			//是否显示true表示显示，false表示不显示，默认为true
//			dynamicColumnDefinition.setVisible(false);
			//列宽
//			dynamicColumnDefinition.setColWidth("100");
			
			//编辑时为下拉选
//			dynamicColumnDefinition.setEdittype(EditControlType.SELECT);
//			dynamicColumnDefinition.setEditoptions("1:'启用',2:'禁用'");
			///编辑时为下拉选时的chang事件
//			dynamicColumnDefinition.setEventType(EventType.ONCHANGE);
			
			//编辑时给input框增加点击事件
//			dynamicColumnDefinition.setEventType(EventType.ONCLICK);
			
			//编辑时增加默认值CURRENT_USER_NAME表示当前用户名、CURRENT_LOGIN_NAME表示当前登录名、CURRENT_TIME表示当前时间（yyyy-MM-dd hh:mm:ss）、CURRENT_DATE表示日期（yyyy-MM-dd）
//			dynamicColumnDefinition.setDefaultValue(DefaultValue.CURRENT_USER_NAME);
//			dynamicColumnDefinition.setDefaultValue(DefaultValue.CURRENT_LOGIN_NAME);
//			dynamicColumnDefinition.setDefaultValue(DefaultValue.CURRENT_TIME);
//			dynamicColumnDefinition.setDefaultValue(DefaultValue.CURRENT_DATE);
			
			
			dynamicColumn.add(dynamicColumnDefinition);
		}
	}
	
	@Action("dynamic-order")
	public String getDynamicOrder() throws Exception {
		dynamicPage=orderManager.search(dynamicPage);
		this.renderText(PageUtils.dynamicPageToJson(dynamicPage,new DynamicColumnValues(){
			public void addValuesTo(List<Map<String, Object>> result) {
				Map<String,DynamicColumnDefinition> dynamicColumnName=mmsUtil.getDynamicColumnName();
				Set<String> productNames=dynamicColumnName.keySet();
				for(Map<String, Object> map:result){
					Long orderId=Long.valueOf(map.get("id").toString());
					for(String productName:productNames){
						DynamicColumnDefinition productValue=dynamicColumnName.get(productName);
						map.put(productName.toString(), orderManager.getProductAmount(productValue.getColName(),orderId));
					}
				}
				
			}
		}));
		return null;
	}
	
	/**
	 * 调用动态导出API例子
	 * @return
	 * @throws Exception
	 */
	@Action("export")
	public String export() throws Exception {
		Page<Order> dynamicPage = new Page<Order>(100000);
		dynamicPage=orderManager.search(dynamicPage);
		ExportData exportData=ApiFactory.getMmsService().getDynamicColumnExportData(dynamicPage,new ExportDynamicColumnValues(){
			public void addValuesTo(List<List<Object>> result){
				String[] productNames=ApiFactory.getMmsService().getDynamicColumnNames();
				if(productNames != null)
					for(List<Object> rowData:result){
						Long orderId=Long.valueOf(rowData.get(0).toString());
						for(Object productName:productNames){
							rowData.add(orderManager.getProductAmount(productName.toString(),orderId));
						}
					}
			}
		});
		//导出的方法调用exportData
		this.renderText(ExcelExporter.export(exportData,"我的导出",ExcelExportEnum.EXCEL2003));
		return null;
	}
	
	@Action("order-portlet")
	public String showOrders()throws Exception{
		String companyId=Struts2Utils.getRequest().getParameter("companyId");
		String userId=Struts2Utils.getRequest().getParameter("userId");
		String type=Struts2Utils.getRequest().getParameter("type");
		String rows= Struts2Utils.getRequest().getParameter("rows");
		String pageNo= Struts2Utils.getRequest().getParameter("pageNo");
		if(!companyId.equals("") && !userId.equals("")){
			ThreadParameters parameters= new ThreadParameters();
			parameters.setCompanyId(Long.parseLong(companyId));
			parameters.setUserId(Long.parseLong(userId));
			ParameterUtils.setParameters(parameters);
			HashMap<String, Object> dataModel=new HashMap<String, Object>();
			dataModel.put("page", orderManager.listAll(portalOrderpage,type,rows,pageNo));
			renderText(TagUtil.getContent(dataModel, "order.ftl")+"totalNo"+portalOrderpage.getTotalPages());
		}
		
		return null;
	}
	
	@Action("api-list")
	public String getApiList() throws Exception {
		gridColumnInfo=mmsUtil.getGridColumnInfo("ES_ORDER");
		return SUCCESS;
	}
	
	@Action("grid-column")
	public String getGridColumn() throws Exception {
		page = orderManager.search(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("control-list")
	public String controlList() throws Exception {
		return SUCCESS;
	}
	
	@Action("control-list-datas")
	public String controlListDatas() throws Exception {
		page = orderManager.searchSort(page);
		renderText(PageUtils.pageToJson(page));
		return null;
	}
	
	@Action("control-sort")
	public String controlSort() throws Exception {
		String originalIndex=Struts2Utils.getParameter("originalIndex");
		String newIndex=Struts2Utils.getParameter("newIndex");
		orderManager.saveOrder(Integer.valueOf(originalIndex),Integer.valueOf(newIndex));
		return null;
	}
	
	public void prepareControlInput() throws Exception {
		prepareModel();
	}
	
	@Action("control-input")
	public String controlInput() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 导入页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("common-import")
	public String commonImport() throws Exception {
		return "common-import";
	}
	
	/**
	 * 导入
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("common-import-shift")
	public String commonImportShift() throws Exception {
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,new CommonImportManager());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	
	/**
	 * 导入noEvent
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("common-import-noEvent")
	public String commonImportNoEvent() throws Exception {
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
	
	/**
	 * 导入
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action("common-import-shift-inject")
	public String commonImportShiftInject() throws Exception {
		String result = "";
		try {
			result = ApiFactory.getDataImporterService().importData(file, fileName,commonImportInjectManager);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderText(result);
		return null;
	}
	/**
	 * 角色树
	 * @return
	 * @throws Exception
	 */
	@Action("role-tree")
	public String roleTree() throws Exception{
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		List<Role> roles=ApiFactory.getAcsService().getRolesExcludeTrustedRole(ContextUtils.getUserId());
		if(roles.size()<=0){
			ZTreeNode root = new ZTreeNode("_role","0","角色", "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}else{
			ZTreeNode root = new ZTreeNode("_role","0","角色", "true", "false", "", "", "folder", "");
			treeNodes.add(root);
			roles(roles,treeNodes,"_role");
		}
		this.renderText(JsonParser.object2Json(treeNodes));
		return null;
	}
	
	private void roles(List<Role> roles,List<ZTreeNode> treeNodes,String parentId){
		for(Role role:roles){
			ZTreeNode root = new ZTreeNode("role~~"+role.getCode()+"~~"+role.getName(),parentId,role.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
	}
	
	/**
	 * 下载导入模板
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Action("download-model")
	public String downLoadModel() throws IOException{
		if("importThree".equals(fileName)){//导入3
			HttpServletResponse response = Struts2Utils.getResponse();
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("order-template.zip","utf-8"));
			
			 OutputStream myout = null;
			String excelPath = Struts2Utils.getRequest().getRealPath("/")+"excel\\";
			//获得导出的根节点
	    	String exportRootPath=excelPath+"importThree";
	    	
			 try {
				//创建导出文件夹，导出的文件暂存的位置
					File folder = new File(exportRootPath);
				 if(!folder.exists()){
					 folder.mkdirs();
				 }
				 //将生成的文件夹打成zip包且删除暂时文件夹
				 File file1 = new File(excelPath+"orderTemplate.xls");
				 FileUtils.copyFileToDirectory(file1, folder);
				 File file2 = new File( excelPath+"order-details.xls");
				 FileUtils.copyFileToDirectory(file2, folder);
				 myout = response.getOutputStream();
					
				 ZipUtils.zipFolder(exportRootPath, myout);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(myout!=null)myout.close();
					FileUtils.deleteDirectory(new File(exportRootPath));
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{//导入1和导入2
			byte[] file=FileUtils.readFileToByteArray(new File( Struts2Utils.getRequest().getRealPath("/")+"/excel/"+fileName));
			Util.download(fileName, file);
		}
		return null;
	}
	public void setPosition(String position) {
		this.position = position;
	}

	public String getCurrentInputId() {
		return currentInputId;
	}

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
