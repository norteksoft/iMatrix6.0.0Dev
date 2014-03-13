<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="myExpenseReport";
	</script>
	<%@ include file="/menus/header.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/loan-bill-menu.jsp"%>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<strong>各种树</strong>
					</div>
					<div id="opt-content" >
						<form  id="contentForm" name="contentForm" method="post" action=""></form>
						<input id="id" name="id" value="" type="hidden"/>
						静态树id<input value="${treeId }"  readonly="readonly" />
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
</html>