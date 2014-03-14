<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	  <%@ include file="/common/acs-iframe-meta.jsp"%>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>

<body>
<div class="ui-layout-center">
  <div class="opt-body">		
	<aa:zone name="acs_button">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="saveAlterRole"><button  class='btn' onclick="saveRole();"><span><span>保存</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="roleManager"><button  class='btn' onclick="setPageState();returnRoleList();"><span><span>返回</span></span></button></security:authorize>
		</div>
	</aa:zone>
	<aa:zone name="acs_content">
		<div id="message" style="color: red;"><s:actionmessage theme="mytheme"/></div>
		<div class="content">
			<input type="hidden" id="adminSign" value="${adminSign}">
			<s:set id="branchesSize" value="branches.size"></s:set>
			<input type="hidden" id="branchSum" value="${branchesSize }">
			<form id="roleForm" name="roleForm" action="#">
			<input type="hidden" id="businessSystemId" name="businessSystemId" value="${businessSystemId}" />
			<input type="hidden" id="roleId" name="id" value="${id}">
			<table>
				<tr>
					<td>角色编号：</td>
					<td><input type="text" id="roleCode" maxlength="255" name="code" value="${code}" <s:if test="id!=null">readonly="readonly"</s:if>> </td>
				</tr>
				<tr>
					<td>角色名称：</td>
					<td><input id="roleName" type="text" maxlength="255" name="name" value="${name}"> </td>
				</tr>
				<tr>
					<td>权重：</td>
					<td><input type="text" onkeyup="value=value.replace(/[^0-9]/g,'');" name="weight" value="${weight}"> </td>
				</tr>
				<tr>
					<td>所属分支机构：</td>
					<td>
						<select id="subCompanyId" name="subCompanyId">
							<!-- 定义roleSubCompanyId是为了区别“角色(Role)”表中和“部门(Department)”表中的subCompanyId -->
							<s:set id="roleSubCompanyId" value="subCompanyId"></s:set>
							<s:if test='"securityAdmin"==adminSign'>
								<option value="">${companyName }</option>
							</s:if><s:elseif test="branches.size>1">
								<option value="">请选择</option>
							</s:elseif>
							<s:iterator value="branches" var="branchesVar">
								<option <s:if test='#branchesVar.id==#roleSubCompanyId'>selected="selected"</s:if> value="${branchesVar.id}">${branchesVar.name }</option>
							</s:iterator>
						</select>
					</td>
				</tr>
			</table>
			
			</form>
		</div>
	</aa:zone>
  </div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>