<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:if test="id == null">新建</s:if><s:else>修改</s:else>租户</title>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/calendar.jsp"%>
<!--<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>

--><script src="${ctx}/widgets/validate/jquery.validate.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="${ctx}/widgets/validate/cmxform.css"/>
<script>
$(document).ready(function(){
	validate();
});

//验证框架
function validate(){
	$("#inputForm").validate({
		submitHandler: function() {
		ajaxSubmit('inputForm', '${ctx}/sale/tenant!save.action', 'form_main',null); 
		},
		rules: {
			tenantName: "required",
			linkman:"required",
			telephone:"required",
			"company.code":"required",
			"company.name":"required",
			"subsciber.beginDate":"required",
			"subsciber.validDate":"required",
			"subsciber.useNumber": {
	 			number:true,
				required:true
 			}
		},
		messages: {
			tenantName: "必填",
			linkman:"必填",
			telephone:"必填",
			companyCode:"必填",
			companyName:"必填",
			"subsciber.beginDate":"必填",
			"subsciber.validDate":"必填",
			"subsciber.useNumber": {
				number:"请输入数字",
				required:"必填"
			}
		
		}
	});
}

function save(){
	if(!submitForm())return;
	$("#inputForm").submit();
}

function submitForm(){
	var tid = $("#tenantId").attr("value");
	if(tid != "") return true;
	//var rslt = document.getElementsByName("priceIds");//$("input[name='priceIds']"); //$(":checkbox");//$("input:checked");
	var rslt = $("input[name^='priceIds_']");
	var isSelected = false;
	for(var i = 0; i < rslt.length; i++){
		if($(rslt[i]).attr("checked")){
			if(!isSelected) isSelected = true;
			//var mk = $(rslt[i]).attr("id").split("_")[1];
			var mk = $(rslt[i]).val();
			var val = $("#amount_" + mk).val();
			if(val == ""){
				$("#message").html("<span style='color: red'>请为产品选择价格!</span>");
				return false;
			}else{
				$("#P_" + mk).attr("value", val);
			}
		}
	}
	if(!isSelected){ $("#message").html("<span style='color: red'>请选择产品</span>"); }
	return isSelected;
}
function setUserNum(obj){
	var objs = $("input[name^='concurrency_']");
	var v = $(obj).attr('value');
	for(var i=0;i<objs.length;i++){
		$(objs[i]).attr('value',v);
	}
}
function sDataChante(obj){
	var objs = $("input[name^='effectDate_']");
	var v = $(obj).attr('value');
	for(var i=0;i<objs.length;i++){
		$(objs[i]).attr('value',v);
	}
}
function vDataChante(obj){
	var objs = $("input[name^='invalidDate_']");
	var v = $(obj).attr('value');
	for(var i=0;i<objs.length;i++){
		$(objs[i]).attr('value',v);
	}
}
function amountChange(obj){
	var objs = $("input[name^='amount_']");
	var amnt = 0;
	var v ;
	for(var i=0;i<objs.length;i++){
		v = $(objs[i]).attr('value');
		amnt = Number(amnt)+Number(v);
	}
	$('#amountCount').html(amnt);
}
</script>
<style type="text/css">
	FIELDSET{ border: 1px solid #2C97E6; margin: 5px;}
	FIELDSET legend{ margin-left: 10px; font-size: 14px; font-weight: bolder; color: blue; padding: 2px 4px;}
	td.title{ width: 80px;}
	td.txt{ width: 400px;}
	table thead tr th{ text-align: center; }
</style>
</head>

<body>
<aa:zone name="form_main">
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
						<h5><s:if test="id == null">创建</s:if><s:else>修改</s:else>租户</h5>
					</div>
					<div class="widget-content">
					
					<input class="btnStyle" type="button" value="提交" onclick="save();"/>&nbsp; 
					<input class="btnStyle" type="button" value="取消" onclick="history.back()"/>
					<div id="message"><s:actionmessage theme="mytheme"/></div>
					<form id="inputForm" action="tenant!save.action" method="post" >
						<input id="tenantId" type="hidden" name="id" value="${id}" />
						<input type="hidden" name="company.id" value="${company.id}" />
						<input type="hidden" value="${productCount}" name="productCount">
						
					<FIELDSET>
						<legend>基本信息</legend>
						<table><tbody>
							<tr>
								<td class="title">租户名：</td><td class="txt"><input type="text" id="tenantName" name="tenantName" size="30" value="${tenantName}" /><span style="color: red;font-weight: bold;">*</span></td>
								<td class="title">联系人:</td><td class="txt"><input type="text" id="linkman" name="linkman" size="30"  value="${linkman}" /><span style="color: red;font-weight: bold;">*</span></td>
							</tr>
							<tr>
								<td class="title">联系电话:</td><td class="txt"><input type="text" id="telephone" name="telephone" size="30" value="${telephone}" /><span style="color: red;font-weight: bold;">*</span></td>
								<td class="title">Email:</td><td class="txt"><input type="text" name="email" size="30" value="${email}" /></td>
							</tr>
						</tbody></table>
					</FIELDSET>
					<FIELDSET>
						<legend>公司信息</legend>
						<table><tbody>
							<tr>
								<td class="title">公司编码:</td>
								<td class="txt"><input <s:if test="company.code!=null">disabled="disabled"</s:if>  type="text" name="company.code" id="companyCode" size="30" value="${company.code}" /><span style="color: red;font-weight: bold;">*</span></td>
								<td class="title">公司名称:</td><td class="txt"><input type="text" name="company.name" id="companyName" size="30" value="${company.name}" /><span style="color: red;font-weight: bold;">*</span></td>
							</tr>
							<tr>
								<td class="title">国家:</td><td class="txt"><input type="text" name="company.country" size="30" value="${company.country}" /></td>
								<td class="title">城市:</td><td class="txt"><input type="text" name="company.city" size="30" value="${company.city}" /></td>
							</tr>
							<tr>
								<td class="title">公司地址:</td><td class="txt"><input type="text" name="company.address" size="30" value="${company.address}" /></td>
								<td class="title">所属行业:</td><td class="txt"><input type="text" name="company.industry" size="30" value="${company.industry}" /></td>
							</tr>
							<tr>
								<td class="title">公司人数:</td><td class="txt"><input type="text" name="company.peopleNumber" size="30" value="${company.peopleNumber}" /></td>
								<td class="title">备注:</td><td class="txt"><input type="text" name="company.remark" size="30" value="${company.remark}" /></td>
							</tr>
							<tr>
								<td class="title"></td><td class="txt"></td>
								<td class="title"></td><td class="txt"></td>
							</tr>
						</tbody></table>
					</FIELDSET>
					<FIELDSET>
						<legend>邮件配置-内网配置</legend>
						<table><tbody>
							<tr>
								<td class="title">是否需要验证用户存在：</td>
								<td class="txt">
									<select id="smtpAuthInside" name="mailDeploy.smtpAuthInside" >
									<option value="true" <s:if test="mailDeploy.smtpAuthInside=='true'">selected="selected"</s:if>>是</option>
									<option value="false" <s:if test="mailDeploy.smtpAuthInside=='false'">selected="selected"</s:if>>否</option>
									</select>
								</td>
								<td class="title">邮件服务器使用的协议：</td>
								<td class="txt">
								<input id="transportProtocolInside" name="mailDeploy.transportProtocolInside" value="${mailDeploy.transportProtocolInside }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">邮件服务器地址：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.smtpHostInside" id="smtpHostInside" value="${mailDeploy.smtpHostInside }" size="30"/>
								</td>
								<td class="title">邮件服务器使用的端口</td>
								<td class="txt">
								<input type="text" name="mailDeploy.smtpPortInside" id="smtpPortInside" value="${mailDeploy.smtpPortInside }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">默认服务器端用户名：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserInside" id="hostUserInside" value="${mailDeploy.hostUserInside }" size="30"/>
								</td>
								<td class="title">默认服务器用户密码</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserPasswordInside" id="hostUserPasswordInside" value="${mailDeploy.hostUserPasswordInside }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">默认主机地址：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserFromInside" id="hostUserFromInside" value="${mailDeploy.hostUserFromInside }" size="30"/>
								</td>
								<td class="title"></td>
								<td class="txt"></td>
							</tr>
						</tbody></table>
					</FIELDSET>	
					<FIELDSET>
						<legend>邮件配置-外网配置</legend>
						<table><tbody>
							<tr>
								<td class="title">是否需要验证用户存在：</td>
								<td class="txt">
									<select id="smtpAuthExterior" name="mailDeploy.smtpAuthExterior" >
									<option value="true" <s:if test="mailDeploy.smtpAuthExterior=='true'">selected="selected"</s:if>>是</option>
									<option value="false" <s:if test="mailDeploy.smtpAuthExterior=='false'">selected="selected"</s:if>>否</option>
									</select>
								</td>
								<td class="title">邮件服务器使用的协议：</td>
								<td class="txt">
								<input id="transportProtocolExterior" name="mailDeploy.transportProtocolExterior" value="${mailDeploy.transportProtocolExterior }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">邮件服务器地址：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.smtpHostExterior" id="smtpHostExterior" value="${mailDeploy.smtpHostExterior }" size="30"/>
								</td>
								<td class="title">邮件服务器使用的端口</td>
								<td class="txt">
								<input type="text" name="mailDeploy.smtpPortExterior" id="smtpPortExterior" value="${mailDeploy.smtpPortExterior }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">默认服务器端用户名：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserExterior" id="hostUserExterior" value="${mailDeploy.hostUserExterior }" size="30"/>
								</td>
								<td class="title">默认服务器用户密码</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserPasswordExterior" id="hostUserPasswordExterior" value="${mailDeploy.hostUserPasswordExterior }" size="30"/>
								</td>
							</tr>
							<tr>
								<td class="title">默认主机地址：</td>
								<td class="txt">
								<input type="text" name="mailDeploy.hostUserFromExterior" id="hostUserFromExterior" value="${mailDeploy.hostUserFromExterior }" size="30"/>
								</td>
								<td class="title"></td>
								<td class="txt"></td>
							</tr>
						</tbody></table>
					</FIELDSET>	
					<s:if test="id == null">
					<FIELDSET>
						<legend>订购信息</legend>
						<table><tbody>
							<tr>
								<td class="title">开始日期：</td>
								<td class="txt">
										<input id="subsciberBeginDate" name="subsciber.beginDate" size="30" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="<s:date name="leaveFactoryDate"  format="yyyy-MM-dd" />"  readonly="readonly" onchange="sDataChante(this);"/>
										<span style="color: red;font-weight: bold;">*</span>
								</td>
								<td class="title">有效日期：</td>
								<td class="txt">
								<input id="subsciberValidDate" name="subsciber.validDate" size="30" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="<s:date name="leaveFactoryDate"  format="yyyy-MM-dd" />"  readonly="readonly" onchange="vDataChante(this);"/>
								<span style="color: red;font-weight: bold;">*</span>
								</td>
							</tr>
							<tr>
								<td class="title">注册用户数：</td><td class="txt"><input type="text" name="subsciber.useNumber" 
									id="subsciberUseNumber" size="30" value="${subsciber.useNumber}" onblur="setUserNum(this);"/>
									<span style="color: red;font-weight: bold;">*</span></td>
								<td class="title"></td><td class="txt"></td>
							</tr>
						</tbody></table>
					</FIELDSET>	
					<FIELDSET>
						<legend>产品信息</legend>
						<table><thead>
							<tr><th></th><th>产品</th><th>版本</th><th>生效日期</th><th>失效日期</th><th>人数</th><th>金额</th></tr>
						</thead><tbody>
							<s:iterator	value="products.keySet()" id="pd" status="idx">
								<tr>
									<td><input type="checkbox" value="${id}" name="priceIds_${idx.index}"/></td>
									<td>&emsp; ${productName} &emsp;</td>
									<td>&emsp; ${version} &emsp;</td>
									<td> <input name="effectDate_${idx.index}"  size="25" onfocus="WdatePicker({})" readonly="readonly"/> </td>
									<td> <input name="invalidDate_${idx.index}"  size="25" onfocus="WdatePicker({})" readonly="readonly"/> </td>
									<td> <input name="concurrency_${idx.index}"  size="25"/> </td>
									<td> <input name="amount_${idx.index}" id="amount_${id}" size="25" onblur="amountChange(this);"/> </td>
								</tr>
							</s:iterator>
							<tr><td colspan="2" style="height: 30px;text-align: center;">合计：</td><td colspan="4"></td><td id="amountCount"></td></tr>
						</tbody></table>
					</FIELDSET>
					</s:if>
				    </form>
				    
				    
				    <div></div>
					</div>
					<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
				</div>
			</div>
    	</div>
    </div>
  </div>
</div>
</aa:zone>
</body>
</html>