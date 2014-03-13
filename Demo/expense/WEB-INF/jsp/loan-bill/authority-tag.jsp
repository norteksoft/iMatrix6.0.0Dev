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
						<strong>自定义权限标签</strong>
					</div>
					<div id="opt-content" >
					<form  id="contentForm" name="contentForm" method="post" action=""></form>
					权限标签：<a  onclick="alert('您有资源编号为：loan-bill-have-authority的权限，可以访问连接：<grid:authorize code="loan_bill_have_authority,loan_bill_no_authority"></grid:authorize>')">点击我</a><br />
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
</html>