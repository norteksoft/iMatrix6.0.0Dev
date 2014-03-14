<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="role.alterRole"/></title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
	<script src="${ctx}/widgets/validate/jquery.validate.js" type="text/javascript"></script>
	<script src="${ctx}/widgets/validate/jquery.metadata.js" type="text/javascript"></script>
	<script src="${ctx}/widgets/jquery-form/jquery.form.js" type="text/javascript"></script>
<script>
	$(document).ready(function(){
		validate();
	});

	//验证框架
	function validate(){
		$("#inputForm").validate({
			submitHandler: function() {
				saveRole("inputForm");
			},
			rules: {
				code: "required",
				name: "required"
			},
			messages: {
				code: "<span style=\"color: red;\">必填</span>",
				name: "<span style=\"color: red;\">必填</span>"
			}
		});
	}

	function saveRole(id){
		$("#"+id).ajaxSubmit(function (id){
			if(id.split("_")[1]=='true'){
				//history.back();
				 window.location='${ctx}/sale/standard-role.action?businessSystemId='+${businessSystem.id};
			}else{
				alert("此编号已存在，请重新输入！");
			}
		});
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
						<s:if test="id == null"><s:text name="common.create"/></s:if><s:else><s:text name="common.alter"/></s:else><s:text name="role.role"/>
						</h5>
					</div>
						<div class="widget-content">
						<div id="content">
							<form id="inputForm" action="standard-role!save.action" method="post">
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" name="businessSystem.id" value="${businessSystem.id}" />
							业务系统：${businessSystem.name}
							<table class="inputView">
								<tr>
									<td>角色编码:</td>
									<td><input type="text" id="roleCode" name="code" size="40" value="${code}" /><span style="color: red;">*</span></td>
								</tr>
								<tr>
									<td><s:text name="role.roleName"/>:</td>
									<td><input type="text" id="roleName" name="name" size="40"  value="${name}" /><span style="color: red;">*</span></td>
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