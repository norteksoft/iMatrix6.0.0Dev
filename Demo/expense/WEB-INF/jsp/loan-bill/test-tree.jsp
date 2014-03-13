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
						<button class='btn' onclick="companyTree();"><span><span>公司树</span></span></button>
						<button class='btn' onclick="userAndDeptTree();"><span><span>人员部门树</span></span></button>
						<button class='btn' onclick="groupTree();"><span><span>工作组树</span></span></button>
						<button class='btn' onclick="deptAndGroupTree();"><span><span>部门工作组树</span></span></button>
						<button class='btn' onclick="deptTree();"><span><span>部门树</span></span></button>
					</div>
					<div id="opt-content" >
						<form  id="contentForm" name="contentForm" method="post" action="">
							<table class="form-table-border-left" style="width: 300px;">
								<tr>
									<td>人员名称</td>
									<td><input id="userName"></input></td>
								</tr>
								<tr>
									<td>部门名称</td>
									<td><input id="deptName"></input></td>
								</tr>
								<tr>
									<td>工作组名称</td>
									<td><input id="groupName"></input></td>
								</tr>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
</html>