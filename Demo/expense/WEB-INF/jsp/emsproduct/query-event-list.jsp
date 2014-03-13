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
		function productNumberQueryClick(id){
			$("#conditionId").attr("value",id);
			$.colorbox({href:"#codePage",inline:true, innerWidth:300, innerHeight:100,overlayClose:false,title:"点击事件"});
		}
		function productNameQueryBlur(id){
			$("#conditionId").attr("value",id);
			$.colorbox({href:"#namePage",inline:true, innerWidth:300, innerHeight:100,overlayClose:false,title:"失去焦点"});
		}
		function colorQueryChange(id){
			$("#conditionId").attr("value",id);
			$.colorbox({href:"#colorPage",inline:true, innerWidth:300, innerHeight:200,overlayClose:false,title:"下拉框切换"});
		}
		function submitCode(){
			var id=$("#conditionId").val();
			parent.$("#"+id).attr("value",$("#code").val());
			$.colorbox.close();
		}
		function submitName(id){
			var id=$("#conditionId").val();
			parent.$("#"+id).attr("value",$("#name").val());
			$.colorbox.close();
		}
		function submitColor(id){
			var id=$("#conditionId").val();
			parent.$("#"+id).attr("value",$("#color").val());
			$.colorbox.close();
		}
		
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="queryEvent";
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
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					</div>
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
					<div id="opt-content" >
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="productList" url="${ctx}/emsproduct/query-event-list-datas.htm" submitForm="sForm" code="PRODUCT_QUERY_EVENT" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
				
			</div>
			
	</div>
	<div style="display: none;">
		<div id="codePage" align="left" style="margin: 10px;">
			<div class="opt-btn">
				<button class="btn" onclick="submitCode();"><span><span>确定</span></span></button>
			</div>
			<input id="conditionId" type="hidden"/>
			<table class="form-table-without-border">
				<tr>
					<td class="content-title">产品编号：</td>
					<td> <s:textfield id="code" theme="simple"></s:textfield></td>
				</tr>	
			</table>
		</div>
		<div id="namePage" align="left" style="margin: 10px;">
			<div class="opt-btn">
				<button class="btn" onclick="submitName();"><span><span>确定</span></span></button>
			</div>
			<input id="nameId" type="hidden"/>
			<table class="form-table-without-border">
				<tr>
					<td class="content-title">产品名称：</td>
					<td> <s:textfield id="name" theme="simple"></s:textfield></td>
				</tr>	
			</table>
		</div>
		<div id="colorPage" align="left" style="margin: 10px;">
			<div class="opt-btn">
				<button class="btn" onclick="submitColor();"><span><span>确定</span></span></button>
			</div>
			<input id="colorId" type="hidden"/>
			<table class="form-table-without-border">
				<tr>
					<td class="content-title">颜色：</td>
					<td> 
						<select id="color">
							<option selected="selected" value="">请选择</option>
							<option value="RED">红色</option>
							<option value="BLUE">蓝色</option>
							<option value="YELLOW">黄色</option>
							<option value="GREEN">绿色</option>
							<option value="ORANGE">橙色</option>
							<option value="PURPLE">紫色</option>
							<option value="CANCEL">白色</option>
						</select>
					</td>
				</tr>	
			</table>
		</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>