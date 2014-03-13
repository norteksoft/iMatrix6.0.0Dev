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
		function productNameClick(obj){
			$.colorbox({href:webRoot+"/order/dynamic-column-click.htm"+"?currentInputId="+obj.currentInputId,iframe:true, innerWidth:300, innerHeight:200,overlayClose:false,title:"修改"});
		}
		function productNameChange(obj){
			alert("change事件");
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >

	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="order2";
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
						<button class='btn' onclick="iMatrix.export_Data('${ctx}/order/export.htm');"><span><span>导出</span></span></button>
					</div>
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
					<div id="opt-content">
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="dynamicOrder" url="${ctx}/order/dynamic-order.htm" code="ES_ORDER" pageName="dynamicPage" dynamicColumn="${dynamicColumn}"></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>