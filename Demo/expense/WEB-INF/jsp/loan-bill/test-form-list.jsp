<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
</head>

<body >
	<aa:zone name="main">
		<div class="opt-btn">
			<button class='btn' onclick="createTestForm();"><span><span>新建</span></span></button>
			<button class='btn' onclick="editTestForm();"><span><span>修改</span></span></button>
		</div>
		<div id="opt-content" >
			<form  id="contentForm" name="contentForm" method="post" action="">
				<grid:jqGrid url="${ctx}/loan-bill/test-form-list.htm" pageName="page" code="capability_test_1" gridId="main_table"></grid:jqGrid>
			</form>
		</div>
	</aa:zone>
	
</body>
</html>