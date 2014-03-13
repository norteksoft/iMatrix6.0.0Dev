<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>订单管理</title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript">
		function createExpenseReport(url){
			openExpenseReport("",url);
		}
		function editExpenseReport(url){
			var ids=jQuery("#orderList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要编辑的记录！");
				return;
			}else if(ids.length>1){
				alert("请不要选择多条记录！");
				return;
			}
			openExpenseReport(ids[0],url);
		}
		function deleteExpenseReport(){
			var ids=jQuery("#orderList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要删除的记录！");
				return;
			}
			
			aa.submit('contentForm', '${ctx}/order/delete.htm?deleteIds='+ids.join(',')+'&position=controlRow', 'main'); 
		}
		function openExpenseReport(id,url){
			if(id!=undefined&&id!=""){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:660, innerHeight:530,
				overlayClose:false,
				onClosed:function(){
					jQuery("#orderList").trigger("reloadGrid");
				},
				title:"订单"
				});
		}
		
		function $gridComplete(){
			var rows= jQuery("#orderList").jqGrid('getCol','type',true);
			for(var i=0;i<rows.length;i++){
				var row=rows[i];
				var type=row.value;
				var id=row.id;
				if("TRAVELLING"==type){
					jQuery("#orderList").setRowData (id, false, 'unsortable');
				}
			}
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="order4";
	</script>
	<%@ include file="/menus/header.jsp" %>
	 
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
						<button class='btn' onclick="createExpenseReport('${ctx}/order/control-input.htm');"><span><span>新建</span></span></button>
						<button class='btn' onclick="editExpenseReport('${ctx}/order/control-input.htm');"><span><span>编辑</span></span></button>
						<button class='btn' onclick="deleteExpenseReport();"><span><span>删除</span></span></button>
					</div>
					<div id="opt-content">
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="orderList" url="${ctx}/order/control-list-datas.htm" code="ES_ORDER_CONTROL"></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>