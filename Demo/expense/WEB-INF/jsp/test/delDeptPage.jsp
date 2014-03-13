<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<%@include file="/common/iframe-meta.jsp" %>
	<script>
	function tijiao(){
		$("#inputForm").attr("action",$("#inputForm").attr("action")+"?_data"+new Date());
		$("#inputForm").ajaxSubmit(function (data){
			alert(data);
			if(data=="ok"){
				window.parent.$.colorbox.close();
				window.parent.refreshTree();
			}
		});
	}
	</script>
</head>
<body>
<div class="ui-layout-center">
		<div class="opt-body">
	<form id="inputForm" action="/ems/test/delDept.htm" method="post">
		<table>
			<tr><td>path</td><td><input name="path" onkeydown="if(event.keyCode==13)return false;"/><font color="red">*</font></td><td></td><td></td></tr>
			<tr><td></td><td><input type="button" value="提交" onclick="tijiao()" /></td><td><input type="reset" value="重置" /></td><td></td></tr>
		</table>
	</form>
	</div></div>
</body>
</html>