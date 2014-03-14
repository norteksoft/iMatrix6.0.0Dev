<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><s:text name="user.addUser"/></title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
<div class="opt-btn">
	 <security:authorize ifAnyGranted="saveUserState">
		<button  class='btn' id="cpn_from" name="cpn_from" onclick="saveUserState();"><span><span><s:text name="common.submit"/></span></span></button>
		<button  class='btn' id="user.cancel" name="user.cancel" onclick="cancelUserState();"><span><span><s:text name="common.cancel"/></span></span></button>
	 </security:authorize>
</div>
<div id="opt-content">
	<form id="inputForm" name="inputForm" action=""+ method="post">
		<input type="hidden" id="departmId"  name="departmId"  value="" />
		<input type="hidden" id="departmType"  name="departmType"  value="" />
		<input type="hidden" id="branchId"  name="branchId"  value="" />
		<input type="hidden" id="oldType"  name="oldType"  value="" />
		
		 <input type="hidden" name="id" size="40" value="${id}" />
         <input type="hidden" name="user.id" size="40" value="${user.id}" />
         <input type="hidden" name="dr" size="40" value="${dr}" />
         <input type="hidden" id="states" name="states" size="40" value="" />
		 <table class="form_table">
			     <tr>
                     <td class="content-title"> <s:text name="user.loginName"/></td>
                     <td><input  type="text" name="user.userName" id="loginName" size="40" value="${requestScope.user.loginName}" disabled="disabled"/>
                     </td>
                     <td class="content-title"> <s:text name="user.trueName"/></td>
                     <td><input  type="text" name="trueName" size="40" value="${requestScope.user.name}" id="trueName" disabled="disabled"/>
                         
                     </td>
                 </tr>
                 <tr>
	             <td class="content-title"> <s:text name="user.email"/></td>
	             <td><input  type="text" name="email" size="40" id="email1" value="${requestScope.user.email}" id="email" disabled="disabled"/>
	                 </td>
	             <td class="content-title"> <s:text name="userInfo.telephone"/></td>
	             <td><input  type="text" name="telephone" size="40" value="${telephone}" id="telephone" disabled="disabled"/></td>
	         </tr>
	         <tr>
	            <td><input <s:if test="user.enabled==false">checked="checked"</s:if> type="radio" name="_states_enable" value="forbidden" class="checkbox" id="enabled" />
	             </td>
	             <td class="content-title"><s:text name="user.enabled"/></td>
	             <td><input <s:if test="user.enabled">checked="checked"</s:if> type="radio" name="_states_enable" value="invocation" class="checkbox" id="invocation" />
	             </td>
	             <td class="no-edit"><s:text name="user.star"/></td>
	             
	         </tr> 
	         <tr><!--
	             <td><input <s:if test="user.enabled">checked="checked"</s:if> type="checkbox" name="_states" value="invocation" class="checkbox" id="invocation" onclick="shiftCheckbox(this)"/>
	             </td>
	             <td class="no-edit"><s:text name="user.star"/></td>
	             --><!--<td><input <s:if test="user.accountNonExpired">checked="checked"</s:if> type="checkbox" name="_states" value="accountNonLocked" class="checkbox" id="accountUnlock"/></td>
	             <td class="no-edit"><s:text name="user.pw_overTimeUnlock"/></td>
	             --><!--<td><input type="checkbox" name="states" value="accountNonLocked" class="checkbox" id="accountNonLocked"/></td>
	             <td><s:text name="user.accountUnlock"/></td>  -->
	         </tr>
	         
		</table>
	</form>
</div>
</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>