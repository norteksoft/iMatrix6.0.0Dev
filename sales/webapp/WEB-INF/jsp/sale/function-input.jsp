<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><s:text name="function.functionManagerList"/>(input)</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/jquery-form/jquery.form.js" type="text/javascript"></script>

<script>
	function save(){
		if(validata()){
			$("#ism").attr("value",$("input[name='ismenu']:checked").val());
			$("#inputForm").attr("action","${ctx}/sale/function!save.action");
			$.ajax({
				data:{id:"${id}",code:$("#functionId").val()},
				cache:false,
				type:"post",
				url:"${ctx}/sale/function!validata.action",
				success:function(data){
					if(data=="ok"){
						$("#inputForm").ajaxSubmit(function (){
							createFunCallback();
						});
					}else{
						alert("资源编码已存在!");
					}
				},
				error:function(){
				}
		    });
		}
	}
	function validata(){
		if($("#functionId").val().replace(/(^\s*)|(\s*$)/g, "")==""){
			alert("资源编号不能为空!");
			return false;
		};
		if($("#functionName").val().replace(/(^\s*)|(\s*$)/g, "")==""){
			alert("资源名称不能为空!");
			return false;
		};
		if($("#functionPath").val().replace(/(^\s*)|(\s*$)/g, "")==""){
			alert("资源路径不能为空!");
			return false;
		};
		return true;
	}
	function createFunCallback(){
		window.parent.refreshTree();
		window.parent.tb_remove();
	}
	function back(){
		window.parent.tb_remove();
	}
	function setfocus(){
		setTimeout('$("#functionId").focus();',300);
	}
</script>
</head>
<body onload="setfocus()">
	<div>
	<form id="inputForm" action="" method="post">
	<input type="hidden" name="id" value="${id}" />
	<input type="hidden" name="systemId" value="${businessSystem.id}" />
	<input id="ism" type="hidden" name="ism" value="${ism }"/>
	<input type="hidden" name="paternId" value="${paternId }"/>
	<table>
		<tr>
		<td>&nbsp;&nbsp;&nbsp;<s:text name="父资源名称"/>：</td><td align="left">${paternName }</td>
		</tr>
	    <tr>
			<td><s:text name="function.functionCode"/>:</td>
			<td><input  type="text" id="functionId" name="code" size="40" value="${code}" /><span id="functionIdTip" style="color:red;width:100px">*</span></td>
		</tr>
		<tr>
			<td><s:text name="function.functionName"/>:</td>
			<td><input  type="text" id="functionName" name="name" size="40" value="${name}" /><span id="functionNameTip" style="color:red;width:100px">*</span></td>
		</tr>
		<tr>
			<td><s:text name="function.functionPath"/>:</td>
			<td><input  type="text" id="functionPath" name="path" size="40" value="${path}" /><span id="functionPathTip" style="color:red;width:100px">*</span></td>
		</tr>
		<tr>
			<td>是否菜单:</td>
			<s:if test="canEditRadio==true">
				<td align="left">是&nbsp;<input name="ismenu" value="true" checked="checked" type="radio" />&nbsp;&nbsp;&nbsp;否&nbsp;<input name="ismenu" value="false" type="radio"   <s:if test="function.ismenu==false">checked="checked"</s:if> /><span id="functionIsmenuTip" style="width:100px"></span></td>
			</s:if>
			<s:else>
				<td align="left">是&nbsp;<input name="ismenu" disabled="disabled" value="true" checked="checked" type="radio" />&nbsp;&nbsp;&nbsp;否&nbsp;<input name="ismenu" value="false" type="radio" disabled="disabled"   <s:if test="ism==false">checked="checked"</s:if> /><span id="functionIsmenuTip" style="width:100px"></span></td>
			</s:else>
		</tr>
	</table>
	</form>
	  <p class="buttonP">
		<input class="btnStyle" type="button" value="<s:text name="common.submit"/>" onclick="save()"/>&nbsp; 
		<input class="btnStyle" type="button" value="<s:text name="common.cancel"/>" onclick="back()"/>
	  </p>
	</div>
</body>
</html>