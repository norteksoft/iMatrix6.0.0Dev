<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<%@include file="/common/iframe-meta.jsp" %>
	
	<title>定时设置</title>
	<script type="text/javascript">
	function submitImport(){
		var url="";
			url='${ctx}/order/do-import.htm';
		if($("#file").val()==''){
			alert('请选择导入的文件。');
			return;
		}
		
		$("#submitImportForm").attr("action",url);
		$("#submitImportForm").ajaxSubmit(function (id){
			id=id.replace("<pre>","").replace("</pre>","");
			id=id.replace("<PRE>","").replace("</PRE>","");
			alert(id);
			window.parent.location="${ctx}/order/list.htm";
		});
	}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<button class="btn" type="button" onclick="submitImport();"><span><span>导入</span></span></button>
	</div>
	<div id="opt-content" >
		<form id="submitImportForm" name="submitImportForm" action="" method="post" enctype="multipart/form-data">
			<input type="hidden" name="type" id="type" value="${type}"/>
			<input type="file" name="file" id="file"/>
		</form>
	</div>
</div>
</div>
</body>
</html>