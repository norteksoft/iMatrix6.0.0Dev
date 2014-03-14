<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Mini-Web 帐号管理</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>

<script src="${ctx}/js/aa.js" type="text/javascript"></script>
<script>
$(document).ready(function(){
	validate();
});
//验证框架
function validate(){
	$.formValidator.initConfig({formid:"inputForm",onsuccess: function() {},onerror:function(){}});
	$("#productName").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});
	$("#version").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});

}

//提交方法
function save(){
	$("#inputForm").submit();
}

function listSalesModules(){
	var selector = document.getElementById("systemslist");
	var index = selector.selectedIndex;
	var value=selector.options[index].value;
	document.getElementById("systemId").setAttribute("value", value);
	document.getElementById("system_id").setAttribute("value", value);
	
	ajaxAnywhere.formName = "functionsbysystem";
	ajaxAnywhere.getZonesToReload = function() {
		return "saleModules";
	};
	ajaxAnywhere.submitAJAX();
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
						<h5><s:if test="id == null">创建</s:if><s:else>修改</s:else>销售包</h5>
					</div>
						<div class="widget-content">
							<form id="functionsbysystem" name="functionsbysystem" action="${ctx}/sale/product!getSalesModules.action">
								<input id="system_id" type="hidden" name="systemId" value=""/>
							</form>
					        <div id="content">
							<form id="inputForm" action="product!save.action" method="post">
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" id="systemId" name="systemId" value="${systemId}" />
							<table class="inputView">
								<tr>
									<td>产品名称:</td>
									<td><input type="text" id="productName" name="productName" size="40" value="${productName}" /><span id="productNameTip" style="width:250px"></span></td>
								</tr>
								<tr>
									<td>版本:</td>
									<td><input type="text" id="version" name="version" size="40" value="${version}" /><span id="versionTip" style="width:250px"></span></td>
								</tr>
								
								<tr>
									<td style="color:blue;">系统：</td>
									<td>
										<select id="systemslist" size="1" name="select_system" onchange="listSalesModules()">
										<s:if test="id == null"><option value="0"></option></s:if>
										<s:iterator	value="allSystems">
											<option value="${id}">${name}</option>
										</s:iterator>
										</select>
									</td>
								</tr>
								
								<tr><td style="color:blue;">销售包列表：</td></tr>
								<tr><td>
								
								</td></tr>
								<tr>
									<td colspan="2">
									  <p class="buttonP">
										<input class="btnStyle" type="button" value="提交" onclick="save()"/>&nbsp; 
										<input class="btnStyle" type="button" value="取消" onclick="history.back()"/>
									  </p>	
									</td>
								</tr>
							</table>
							<aa:zone name="saleModules">
								<s:iterator	value="allSalesModules">
										<s:if test="checkedSalesModuleIds.contains(id)">
										<input type="checkbox" name="salesModuleIds" checked="checked" value="${id}" />
										</s:if><s:else>
										<input type="checkbox" name="salesModuleIds" value="${id}" />
										</s:else>
										${moduleName}&nbsp;<br/>
								</s:iterator>
								</aa:zone>
							</form>
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