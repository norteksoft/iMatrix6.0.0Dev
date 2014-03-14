<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>授权管理</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="pageTable">
			<div class="opt-btn">
				<button class="btn" onclick="validatePermissionSave();"><span><span>保存</span></span></button>
				<button class="btn" onclick="backPage();"><span><span >返回</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="pageTablelist">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="backForm" id="backForm" method="post"> 
					<input name="permissionId" type="hidden" value="${permissionId }"/>
				</form>
				<form action="" name="viewSaveForm" id="viewSaveForm" method="post">
					<input id="_menuId" type="hidden" name="sysMenuId" value="${sysMenuId }"/>
					<input name="dataRuleId" type="hidden" value="${dataRuleId }" id="_dataRuleId"/>
					<input type="hidden" id="dataTableId" value="${dataTableId }"></input>
					<input name="permissionId" type="hidden" value="${permissionId }" id="permissionId"/>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title" style="width:100px">编&nbsp;&nbsp;&nbsp;&nbsp;号：</td>
				  			<td><input id="code"  name="code" value="${code }" maxlength="255" onblur="isPermissionCodeExist();"></input><span class="required">*</span></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px">描&nbsp;&nbsp;&nbsp;&nbsp;述：</td>
				  			<td><input id="name"  name="name" value="${name }" maxlength="255"></input><span class="required">*</span></td>
						</tr> 
						<!-- 
						<tr>
							<td class="content-title" style="width:100px">优&nbsp;先&nbsp;级：</td>
				  			<td><input id="priority"  name="priority" value="${priority }" onkeyup="value=value.replace(/[^\d]/g,'')"></input><span class="required">*</span></td>
						</tr> 
						-->
						<tr>
							<td class="content-title">人&nbsp;&nbsp;&nbsp;&nbsp;员：</td>
							<td>
								<input type="radio" name="allUser" value="true" <s:if test="allUser">checked=checked</s:if> onclick="settingUserType();" id="allUser"></input>所有人&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="allUser" value="false" <s:if test="!allUser">checked=checked</s:if> onclick="settingUserType();" id="userSet"></input>设置人员
							</td>
						</tr>
						<tr>
							<td class="content-title"></td>
							<td id="userSetTd">
								<div id="userSetGrid">
									<aa:zone name="gridZone">
										<input id="allUserFlag" type="hidden" value="${allUser }"></input>
										<div style="margin-bottom:5px; ">
											<a class="small-btn" onclick="viewCondition();"><span><span>查看</span></span></a>
										</div>
										<view:formGrid gridId="childGridId" code="ACS_PERMISSION_ITEM_EDIT" collection="${permissionItems}"></view:formGrid>
									</aa:zone>
								</div>
							</td>
						</tr>
						<tr>
							<td class="content-title">操作权限：</td>
							<td><fieldset >
									<legend><input type="checkbox" onclick="selectAllPermission(this, 'docAuthes')">权限</legend>
									<ul class="authorize" style="margin: 2px 10px;padding-left:20px;">
										<s:iterator value="@com.norteksoft.acs.base.enumeration.PermissionAuthorize@values()"  id="auth">
											<s:set id="authResult" value="%{authority & #auth.code}"></s:set>
											<li><input name="docAuthes" type="checkbox" value="${auth}" <s:if test="#authResult !=0">checked="checked"</s:if> code="${auth.code }" onclick="showSelectList();"><s:text name="%{#auth.i18nKey}"></s:text></li>
										</s:iterator>
									</ul>
								</fieldset></td>
						</tr>
						<tr class="">
							<td class="content-title"  id="_list_id">列表：</td>
							<td class="listTd"><input readonly="readonly" id="listViewName" value="${listViewName }" /><input type="hidden" name="listViewId" id="listViewId" value="${listViewId }"></input> &nbsp;<a class="small-btn" onclick="selectListView();"><span><span>选择</span></span></a>&nbsp;<a class="small-btn" onclick="clearList();"><span><span>清空</span></span></a></td>
						</tr>
					</table>
					</form>
					<script type="text/javascript">
						$(document).ready(function() {
							showSelectList();
							settingUserType();
						});
					</script>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>