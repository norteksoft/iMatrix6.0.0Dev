<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Mini-Web 帐号管理</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/js/aa.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function(){
	validate();
});
//验证框架
function validate(){
	$.formValidator.initConfig({formid:"inputForm",onsuccess: function() {},onerror:function(){}});
	$("#priceName").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});
	$("#amount").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});

}

//提交方法
function save(){
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
						<h5>
						<s:if test="id == null">创建</s:if><s:else>修改</s:else>价格策略
						</h5>
					</div>
						<div class="widget-content">
						  <div id="content">
								<form id="inputForm" action="price-policy!save.action" method="post">
								<input type="hidden" name="pricePolicy.id" value="${pricePolicy.id}" />
								<input type="hidden" id="productId" name="productId" value="${pricePolicy.product.id}" />
									<table class="inputView">
										<tr>
											<td>策略名称:</td>
											<td><input type="text" id="priceName" name="pricePolicy.priceName" size="40" value="${pricePolicy.priceName}" /><span id="priceNameTip" style="width:250px"></span></td>
										</tr>
										<tr>
											<td>金额:</td>
											<td><input type="text" id="amount" name="pricePolicy.amount" size="40" value="${pricePolicy.amount}" /><span id="amountTip" style="width:250px"></span></td>
										</tr>
										<tr>
											<td>备注:</td>
											<td><input type="text" id="remark" name="pricePolicy.remark" size="40" value="${pricePolicy.remark}" /></td>
										</tr>
										
										<tr>
											<td colspan="2">
											  <p class="buttonP">
												<input class="btnStyle" type="button" value="提交" onclick="save()"/>&nbsp; 
												<input class="btnStyle" type="button" value="取消" onclick="history.back()"/>
											  </p>
											</td>
										</tr>
									</table>
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