<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>订单管理</title>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
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
			
			aa.submit('contentForm', '${ctx}/order/delete.htm?deleteIds='+ids.join(','), 'main'); 
		}
		function openExpenseReport(id,url){
			if(typeof(id)!='undefined'&&id!=""){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:900, innerHeight:530,
				overlayClose:false,
				onClosed:function(){
					jQuery("#orderList").trigger("reloadGrid");
				},
				title:"订单"
				});
		}
		function importData(url,title){
			$.colorbox({href:url,iframe:true, innerWidth:300, innerHeight:100,overlayClose:false,title:title});
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="order1";
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
						<button class='btn' onclick="createExpenseReport('${ctx}/order/input.htm');"><span><span>新建</span></span></button>
						<button class='btn' onclick="editExpenseReport('${ctx}/order/input.htm');"><span><span>编辑</span></span></button>
						<button class='btn' onclick="createExpenseReport('${ctx}/order/formview-input.htm');" ><span><span >新建(表单标签)</span></span></button>
						<button class='btn' onclick="editExpenseReport('${ctx}/order/formview-input.htm');"><span><span title="表单标签">编辑(1)</span></span></button>
						<button class='btn' onclick="editExpenseReport('${ctx}/order/formview-sign-input.htm');"><span><span title="表单标签签章">编辑(2)</span></span></button>
						<button class='btn' onclick="editExpenseReport('${ctx}/order/formview-print.htm');"><span><span>表单打印</span></span></button>
						<button class='btn' onclick="deleteExpenseReport();"><span><span>删除</span></span></button>
						<button class='btn' onclick="importData('${ctx}/order/common-import.htm?type=noInject','导入1(回调)');"><span><span title="回调">导入(1)</span></span></button>
						<button class='btn' onclick="importData('${ctx}/order/common-import.htm?type=inject','导入2(回调,注入)');"><span><span title="回调，注入">导入(2)</span></span></button>
						<button class='btn' onclick="importData('${ctx}/order/common-import.htm?type=noEvent','导入3(无回调)');"><span><span title="无回调">导入(3)</span></span></button>
					</div>
					<div id="opt-content">
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="orderList" url="${ctx}/order/list-datas.htm" code="ES_ORDER" subGrid="orderItemList"></grid:jqGrid>
							<div style="height: 8px;"></div>
							<grid:subGrid gridId="orderItemList" url="${ctx}/order/list-order-item.htm" code="ES_ORDER_ITEM" pageName="pageItem"></grid:subGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>