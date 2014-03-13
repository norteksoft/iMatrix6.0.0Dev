<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>产品管理</title>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	
	<script type="text/javascript">
		function exportProduct(){
			$("#contentForm").attr("action","${ctx}/emsproduct/export.htm");
			$("#contentForm").submit();
		}
		//obj:{rowid:id,currentInputId:id_buyer}
		function buyerClick(obj){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:obj.rowid+"_buyerLoginName",
//				showInputId:obj.currentInputId,
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
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
							getPermissionUserInfo();
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function $successfunc(response){
			if(response.responseText=="false"){
				alert("产品名称不能填写0！");
				return false;
			}else{
				return true;
			}
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
			aa.submit('sForm', webRoot+'/emsproduct/api-list.htm', 'main');
		}

	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="productApi";
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
					<div id="searchDiv" style="display:block">
						
					</div>
					<div id="opt-content">
						<form id="contentForm" name="contentForm" method="post"  action="">
							<table id="productList"></table>
							<div id="productList_pager"></div> 
							<script type="text/javascript">
							var lastselect;
								$(document).ready(function(){
									jQuery("#productList").jqGrid({
										url: '${ctx}/emsproduct/list-datas.htm',
										pageName: 'page',
										datatype: "json", 
										colNames:${gridColumnInfo.colNames}, 
										colModel:${gridColumnInfo.colModel}, 
										editurl: "${ctx}/emsproduct/list-datas.htm",
										ondblClickRow: function(ids){
											if(ids && ids!==lastselect){
												jQuery('#productList').jqGrid('restoreRow',lastselect);
												jQuery('#productList').jqGrid('editRow',ids,true,editFun,function(){},"${ctx}/emsproduct/save.htm",'',function(){});
												lastselect=ids;
												editing=true;
											}
										},
										rowNum: 20, 
									   	autowidth: true,
									   	pager: '#productList_pager', 
										viewrecords: true, 
										sortorder: "desc",
										multiselect: true,
										postData: { _list_code: "ES_PRODUCT_API" }
									}).navGrid('#productList_pager',{edit:false,add:false,del:false,search:false});

									
									var h= $("#opt-content").height();
									jQuery("#productList").jqGrid("setGridHeight",h-65);
					     	  	});

								function editFun(id){
									var colModel="${gridColumnInfo.colModel}";
									var columns=eval(colModel);
									for(var i=0;i<columns.length;i++){
										if(columns[i].name=="buyDate"){
											jQuery('#'+id+'_buyDate','#productList').attr("readonly","readonly");
											jQuery('#'+id+'_buyDate','#productList').click(function(){WdatePicker({dateFmt:'yyyy-MM-dd'});});
										}else if(columns[i].name=="buyer"){
											jQuery('#'+id+'_buyer','#productList').attr("readonly","readonly");
											jQuery('#'+id+'_buyer','#productList').click(function(){buyerClick({rowid:id,currentInputId:id+'_buyer'});});
										}
									}
								}
							</script>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
</html>