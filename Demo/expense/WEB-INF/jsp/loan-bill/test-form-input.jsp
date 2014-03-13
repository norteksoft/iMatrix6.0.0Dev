<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
</head>

<body>
		<aa:zone name="main">
			<div class="opt-btn">
				<button class='btn' onclick="submitTestForm('${ctx}/loan-bill/test-form-save.htm');"><span><span>保存</span></span></button>
				<button class='btn' onclick="ajaxSubmit('contentForm','${ctx}/loan-bill/test-form-list.htm','main');"><span><span>返回</span></span></button>
			</div>
			<div id="opt-content" >
				<div style="display: none;" id="message"><font class="onSuccess" ><nobr>保存成功！</nobr></font></div>
				<form  id="contentForm" name="contentForm" method="post" action="">
						<grid:formView code="capability_test" entity="${data}" version="1"></grid:formView>
				</form>
			</div>
		</aa:zone>
</body>
</html>