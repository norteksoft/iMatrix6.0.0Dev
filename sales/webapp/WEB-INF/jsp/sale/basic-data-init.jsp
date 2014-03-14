<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 公司管理</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<script type="text/javascript">
function _showMessage(id, msg){
	if(msg != ""){
		$("#"+id).html(msg);
	}
	$("#"+id).show("show");
	$("."+id).show();
	setTimeout('$("#'+id+'").hide("show");$(".'+id+'").hide();',3000);
}

function initInfo(){
	if($("#filename").val()==""){
		_showMessage('_msg', "请选择导入的文件!");
	}else{
		$.ajax({ 
			url: "business-system!validateCompany.action", 
			success: function(msg){
				if(msg=="true"){
					if($("#_loading_div_id").length==0){
						$('<div id="_loading_div_id" class="loading">正在导入...</div>').insertAfter($(".page_margins div:first-child"));
					}
					$("#_loading_div_id").show();
					$("#inputForm").submit();
				}else{
					alert("请创建租户,再导入初始化数据");
				}
			}
		});
	}
}

//验证框架
function validateInit(){
	$.formValidator.initConfig({formid:"inputForm",onsuccess: function() {submit(); return true;},onerror:function(){}});
}
function submit(){
	if(typeof($("#companyList").val())!="undefined"){
		$("#companyId").attr("value",$("#companyList").val());
	}
	if($("#companyId").val()==""){
		if(confirm("确认给所有公司初始化该文件吗?")){
			$("#submitBtn").attr("disabled","disabled");
			ajaxSubmit("inputForm","${ctx}/sale/basic-data!initData.action",initCallback);
		}
	}else{
		$("#submitBtn").attr("disabled","disabled");
		ajaxSubmit("inputForm","${ctx}/sale/basic-data!initData.action",initCallback);
	}
}
function initCallback(){
	showMsg();
	$("#submitBtn").attr("disabled","disabled");
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
								<h5>初始化数据</h5>
							</div>
						<div class="widget-content">
							<div id="content">
								<aa:zone name="data-zoon">
									<p class="buttonP" >
										<input class="btnStyle" type="button" id="submitBtn" value="初始化" onclick="initInfo();"/>
									</p>
									<div id="message"><s:actionmessage theme="mytheme"/></div>
									<input name="result" id="result" value="${result }" type="hidden"/>
									<s:if test="showFlag">
										<table>
											<tr>
												<td>公司</td><td>
												<select id="companyList">
												<option value="">请选择</option>
												<s:iterator value="companys">
												<option value="${id }">${name }</option>
												</s:iterator></select>
												</td>
											</tr>
										</table>
									</s:if>
									<form id="inputForm" name="inputForm" action="${ctx}/sale/basic-data!initData.action" method="post" enctype="multipart/form-data">
										<table class="inputView">
											<tr><td ><input type="file" id="filename" name="file"/><input name="companyId" id="companyId" value="${companyId }" type="hidden"/></td></tr>
										</table>
									</form>
									<ul class="_msg" style="display: none;list-style-type: none; margin-top: 6px;">
										<li> <span id="_msg" style="color: red;display: none;list-style-type: none;"></span> </li>
									</ul>
									<script>
									$(function(){
										validateInit();
										var result=$("#result").val();
										if(result!=''&&typeof result!='undefined'){
											if(result=='noZip'){
												alert("请选择zip文件格式");
											}else if(result=='success'){
												alert("初始化成功");
											}else if(result=='zipError'){
												alert("导入失败，请检查zip文件格式");
											}
										}
										//setTimeout('$("#message").hide();',1000);
									});
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


