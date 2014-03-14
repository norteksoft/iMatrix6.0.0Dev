<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><s:text name="functionGroup.functionGroupManager"/>(input)</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<script>
	$(document).ready( function() {
		validate();
	});
	//验证框架
	function validate(){
		$.formValidator.initConfig({formid:"inputForm",onsuccess: function() {},onerror:function(){}});
		$("#functionGroupId").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});
		$("#functionGroupName").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});

	}
	
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
						<s:if test="id == null"><s:text name="common.create"/></s:if><s:else><s:text name="common.alter"/></s:else><s:text name="functionGroup.functionGroup"/>
						</h5>
					</div>
						<div class="widget-content">
				           <div id="content">
							<form id="inputForm" action="function-group!save.action" method="post">
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" name="systemId" value="${systemId}" />
							<table >
							    
							    <tr>
									<td><s:text name="functionGroup.functionGroupCode"/>:</td>
									<td><input type="text" id="functionGroupId" name="code" size="40" value="${code}" /><span id="functionGroupIdTip" style="width:250px"></span></td>
								</tr>
								<tr>
									<td><s:text name="functionGroup.functionGroupName"/>:</td>
									<td><input type="text" id="functionGroupName" name="name" size="40" value="${name}" /><span id="functionGroupNameTip" style="width:250px"></span></td>
								</tr>
								<tr>
									<td colspan="2">
									    <p class="buttonP">
										<input class="btnStyle" type="button" value="<s:text name="common.submit"/>" onclick="save()"/>&nbsp; 
										<input class="btnStyle" type="button" value="<s:text name="common.cancel"/>" onclick="history.back()"/>
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