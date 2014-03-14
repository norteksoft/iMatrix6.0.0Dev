<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 公司管理</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
	<script src="${ctx}/widgets/validate/jquery.validate.js" type="text/javascript"></script>
	<script src="${ctx}/widgets/validate/jquery.metadata.js" type="text/javascript"></script>
	<script src="${ctx}/widgets/jquery-form/jquery.form.js" type="text/javascript"></script>
<script type="text/javascript">
//验证框架
function validate(){
	$("#inputForm").validate({
		submitHandler: function() {
			if($("#filename").val()==''){
				alert('请选择导入的文件。');
				return;
			}else{
				$("#submitBtn").attr("disabled","disabled");
				$("#inputForm").ajaxSubmit(function (id){
					$("#_loading_div_id").hide();
					id=id.replace("<pre>","").replace("</pre>","");
					id=id.replace("<PRE>","").replace("</PRE>","");
					alert(id);
					$("#submitBtn").removeAttr("disabled");
				});
			}
		},
		rules: {
			imatrixIp: "required",
			imatrixPort: "required"
		},
		messages: {
			imatrixIp: "<span style=\"color: red;\">必填</span>",
			imatrixPort: "<span style=\"color: red;\">必填</span>"
		}
	});
}
function submitbutt(){
	if($("#_loading_div_id").length==0){
		$('<div id="_loading_div_id" class="loading">正在导入...</div>').insertAfter($(".page_margins div:first-child"));
	}
	$("#_loading_div_id").show();
	$("#inputForm").submit();
}
</script>
</head>
<body>

<div class="page_margins">
  <div class="page">
        <%@ include file="/menus/header.jsp"%>
		<%@ include file="/menus/second_menu.jsp"%>
           <div id="main">
		     <%@ include file="/menus/left_menu.jsp"%>
		        <div id="col3" style="margin: 0 0 0 150px;">
		           <div class="widget-place" id="widget-place-1">
		    			<div class="widget" id="identifierwidget-1">
							<div class="widget-header">
								<h5>导入基础数据</h5>
							</div>
						<div class="widget-content">
							<div id="content">
								<aa:zone name="data-zoon">
									<p class="buttonP">
							           	<input class="btnStyle" id="submitBtn" type="button" value='<s:text name="common.import"/>' onclick="submitbutt();"/>
							       	</p>
							       	<div id="message" ><s:actionmessage theme="mytheme"/></div>
									<form id="inputForm" name="inputForm" action="basic-data!importData.action" method="post" enctype="multipart/form-data">
										<table class="inputView">
											<tr><td>应用平台IP地址:</td><td><input id="imatrixIp" name="imatrixIp"/><span style="color: red;">*</span>(如:192.168.1.98)</td><td></td></tr>
											<tr><td>应用平台端口号:</td><td><input id="imatrixPort" name="imatrixPort"/><span style="color: red;">*</span>(如:8080)</td><td></td></tr>
											<tr><td>应用名称:</td><td><input id="imatrixName" name="imatrixName"/>(如:imatrix,默认为imatrix)</td><td></td></tr>
											<tr><td colspan="2"><input type="file" id="filename" name="file"/></td></tr>
										</table>
									</form>
									<ul class="_msg" style="display: none;list-style-type: none; margin-top: 6px;">
										<li> <span id="_msg" style="color: red;display: none;list-style-type: none;"></span> </li>
									</ul>
									<script>
									$(function(){validate();setTimeout('$("#message").hide();',1000);});
									</script>
								</aa:zone>
							</div>
						</div>
						<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>


