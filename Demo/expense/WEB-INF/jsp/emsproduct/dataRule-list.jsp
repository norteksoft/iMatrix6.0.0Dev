<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>产品管理</title>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<script type="text/javascript">
		function exportProduct(){
			$("#contentForm").attr("action","${ctx}/emsproduct/export.htm");
			$("#contentForm").submit();
		}
		function buyerClick(obj){
			var acsSystemUrl = "${ctx}";
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {
					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
				                showInput:obj.currentInputId,
				                hiddenInput:obj.rowid+"_buyerLoginName",
				                append:false
					},
					callback: {
						onClose:function(){
							buyerCallBack(obj);
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		function buyerCallBack(obj){
			$("#"+obj.rowid+"_buyerLoginName").attr("value",jstree.getLoginName());
		}
		
		function $successfunc(response){
			if(response.responseText=="false"){
				alert("产品名称不能填写0！");
				return false;
			}else{
				return true;
			}
		}

		function anniu(cellvalue, options, rowObject ){
			var addButton="<a href='#' class='small-button-bg' onclick=\"alert('增加按钮');\"><span class='ui-icon ui-icon-plusthick'></span></a>";
			var deleteButton="<a href='#' class='small-button-bg' onclick=\"alert('删除按钮');\"><span class='ui-icon  ui-icon-minusthick'></span></a>";
			return addButton+deleteButton;
		}

		function tupian(cellvalue, options, rowObject ){
			return '<img src="../images/12.PNG" />';
		}
		
		function viewProduct(cellvalue, options, rowObject ){
			var v = "<a href='#' onclick='viewProductInfo("+rowObject.id+");'>"+cellvalue+"</a>";
			return v;
		}

		function viewProductInfo(id){
			aa.submit('contentForm', webRoot+'/emsproduct/input.htm?id='+id, 'main');
		}
		function back(){
			aa.submit('sForm', webRoot+'/emsproduct/list.htm', 'main');
		}

		//自定义元素
		function customElementElement (value, options) {
			var el="<input id=\"inputId\" type=\"text\" style=\"width:90px;\" value = "+value+"></input><button onclick=\"clearValue('"+options.id+"');\" type=\"button\">清除</button>";
			return el;
		  }
		//自定义元素的值
		function customElementValue(elem) {
		    return $(elem).val();
		  }


	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="product-dataRule";
	</script>
	
	<%@ include file="/menus/header.jsp" %>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
			<form id="sForm" name="sForm" method="post"  action=""></form>
				<aa:zone name="main">
					<div class="opt-btn">
					</div>
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
					<div id="opt-content" >
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="productList" url="${ctx}/emsproduct/dataRule-list-datas.htm" submitForm="sForm" code="DATA_RULE_PRODUCT" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
				
			</div>
			
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
</html>