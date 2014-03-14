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
					<input name="sysMenuId" type="hidden" value="${sysMenuId }"/>
					<input name="permissionId" type="hidden" value="${permissionId }" id="permissionId"/>
					<input name="deparmentInheritable" type="hidden" id="deparmentInheritable"/>
					<input name="permissionUsers" type="hidden" id="permissionUsers"/>
					<table class="form-table-without-border">
						<tr>
							<td class="content-title" style="width:100px">编&nbsp;&nbsp;&nbsp;&nbsp;号：</td>
				  			<td><input id="code"  name="code" value="${code }" maxlength="255" onblur="isPermissionCodeExist();"></input><span class="required">*</span></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px">描&nbsp;&nbsp;&nbsp;&nbsp;述：</td>
				  			<td><input id="name"  name="name" value="${name }" maxlength="255"></input><span class="required">*</span></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px">人&nbsp;&nbsp;&nbsp;&nbsp;员：</td>
				  			<td><input type="checkbox" id="all_user_checkbox" onclick="allUserChange();" name="permissionUserCheck" <s:if test="permissionUsers.contains('\"ALL_USER\"')&&!permissionUsers.contains('\"USER\"')">checked=checked</s:if>  ></input>所有人</td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px"></td>
				  			<td><input type="checkbox" id="user_checkbox" name="permissionUserCheck" <s:if test="permissionUsers.contains('\"USER\"')">checked=checked</s:if> ></input>具体人&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="point_user" value="${pointUserNames }"  style="width:400px;" readonly="readonly"/> <input id="point_user_value" type="hidden" value='${pointUserValues }'/>&nbsp;<a class="small-btn" onclick="selectPointUser();"><span><span>选择</span></span></a></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px"></td>
				  			<td><input type="checkbox" id="dept_checkbox" name="permissionUserCheck" <s:if test="permissionUsers.contains('\"DEPARTMENT\"')">checked=checked</s:if> ></input>具体部门&nbsp;&nbsp;&nbsp;&nbsp;<input id="point_dept"  value="${pointDeptNames }"  style="width:400px;" readonly="readonly"/><input id="point_dept_value" type="hidden" value='${pointDeptValues }'/> &nbsp;<a class="small-btn" onclick="selectPointDept();"><span><span>选择</span></span></a></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px"></td>
				  			<td><input type="checkbox" id="role_checkbox" name="permissionUserCheck" <s:if test="permissionUsers.contains('\"ROLE\"')">checked=checked</s:if> ></input>具体角色&nbsp;&nbsp;&nbsp;&nbsp;<input id="point_role"  value="${pointRoleNames }"  style="width:400px;" readonly="readonly"/><input id="point_role_value" type="hidden" value='${pointRoleValues }'/> &nbsp;<a class="small-btn" onclick="selectPointRole();"><span><span>选择</span></span></a></td>
						</tr> 
						<tr>
							<td class="content-title" style="width:100px"></td>
				  			<td><input type="checkbox" id="wg_checkbox" name="permissionUserCheck" <s:if test="permissionUsers.contains('\"WORKGROUP\"')">checked=checked</s:if> ></input>具体工作组&nbsp;&nbsp;<input id="point_wg"  value="${pointWorkgroupNames }"  style="width:400px;" readonly="readonly"/><input id="point_wg_value" type="hidden" value='${pointWorkgroupValues }'/> &nbsp;<a class="small-btn" onclick="selectPointWorkgroup();"><span><span>选择</span></span></a></td>
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
						<tr>
							<td class="content-title">数据：</td>
							<td ><input id="dataTableName" readonly="readonly" name="dataTableName" <s:if test="permissionDataRule!=null">value="${permissionDataRule.dataTableName }"</s:if> /><input type="hidden" name="dataTableId" id="dataTableId" <s:if test="permissionDataRule!=null">value="${permissionDataRule.dataTableId }"</s:if> ></input><span class="required">*</span> &nbsp;<a class="small-btn" onclick="selectDataTable('fast');"><span><span>选择</span></span></a></td>
						</tr>
						<tr class="" >
							<td class="content-title" id="_list_id">列表：</td>
							<td class="listTd" ><input readonly="readonly" id="listViewName" value="${listViewName }" /><input type="hidden" name="listViewId" id="listViewId" value="${listViewId }"></input> &nbsp;<a class="small-btn" onclick="selectListView('fast');"><span><span>选择</span></span></a>&nbsp;<a class="small-btn" onclick="clearList();"><span><span>清空</span></span></a></td>
						</tr>
						<tr>
							<td class="content-title">数据范围：</td>
							<td >
								<ul style="list-style: none;margin-top: 0px; padding-left: 8px;">
									<s:iterator value="@com.norteksoft.acs.base.enumeration.DataRange@values()"  id="range">
									   <s:if test="#range.code == 'data.range.current.department'">
										   <s:if test='permissionDataRule!=null && #range == permissionDataRule.dataRange'>
											    <li><div style="width:280px"><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();" id="dept"></input><s:text name="%{#range.code}"></s:text>&nbsp;&nbsp;<span style="display: none;float:right;" id="deptImp"><input  type="checkbox" id="deptInheri" <s:if test="permissionDataRule.deparmentInheritable">checked="checked"</s:if> ></input><s:text name="data.range.current.department.inheritable"></s:text></span></div></li>
										   </s:if><s:else>
											    <li><div style="width:280px"><input name="dataRange" type="radio" value="${range}" code="${range.code }" onclick="showImp();" id="dept"></input><s:text name="%{#range.code}"></s:text>&nbsp;&nbsp;<span style="display: none;float:right;" id="deptImp"><input  type="checkbox" id="deptInheri"  <s:if test="permissionDataRule.deparmentInheritable">checked="checked"</s:if> > </input><s:text name="data.range.current.department.inheritable"></s:text></span></div></li>
										   </s:else>
									   </s:if><s:else>
										   <s:if test='permissionDataRule!=null && #range == permissionDataRule.dataRange'>
												<li><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
										   </s:if><s:else>
											   <s:if test="#range.code == 'data.range.myself'">
													<li><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
											   </s:if><s:else>
													<li><input name="dataRange" type="radio" value="${range}" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
											   </s:else>
										   </s:else>
									   </s:else>
									</s:iterator>
								</ul>
							</td>
						</tr>
					</table>
					</form>
					<script type="text/javascript">
						$(document).ready(function() {
							showSelectList();
							showImp();
							allUserChange();
						});
					</script>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>