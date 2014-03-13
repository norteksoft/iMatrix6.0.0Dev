<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>计划管理</title>
		<%@include file="/common/iframe-meta.jsp" %>	
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
		<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/format.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
		<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
		<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
		<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
		<script type="text/javascript">
			$(document).ready(function(){
				getContentHeight();
			});
			

		</script>
	</head>
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="mainZone">
					<div id="opt-content">
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<script type="text/javascript">setTimeout("$('#message').hide('show');",3000);</script>
						<form  id="planForm" name="planForm" method="post" action="">
							<input  name="id" id="id" value="${id }" type="hidden"/>
							<table class="form-table-border-left">
								<tr>
									<td>计划名称</td>
									<td><input id="name" name="name" value="${name }" readonly="readonly"/> </td>
									<td>计划编号</td>
									<td><input id="code" name="code" value="${code }" readonly="readonly"/> </td>
								</tr>
								<tr>
									<td>开始日期</td>
									<td>
										<input id="beginDate" name="beginDate" value="<s:date name="beginDate" format="yyyy-MM-dd"/>" readonly="readonly" />
									</td>
									<td>结束日期</td>
									<td>
										<input id="endDate" name="endDate" value="<s:date name="endDate" format="yyyy-MM-dd"/>" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<td>计划数量</td>
									<td>
										<input id="amount" name="amount" value="${amount }" onkeyup="value=value.replace(/[^0-9]/g,'')" readonly="readonly"/>
									</td>
									<td>计划金额</td>
									<td>
										<input id="money" name="money" value="${money }"   readonly="readonly"/>
									</td>
								</tr>
								<tr>
									<td>是否完成</td>
									<td>
										<input name="finished" id="finished" type="hidden" value="${finished }"></input><input id="ifFinished" <s:if test="finished">checked="checked"</s:if> type="checkbox" disabled="disabled"></input>
									</td>
									<td>计划状态</td>
									<td>
										<select id="planState" name="planState" disabled="disabled">
											<s:iterator value="@com.example.expense.base.enumeration.PlanState@values()" var="state">
												<option <s:if test="#state==planState">selected="selected"</s:if> value="${state}"><s:text name="%{code}"></s:text></option>
											</s:iterator>
										</select>
									</td>
								</tr>
								<tr>
									<td>用户登录名</td>
									<td>
										<input id="loginName" name="loginName" value="${loginName }" readonly="readonly" />
									</td>
									<td>部门</td>
									<td>
										<input id="department" name="department" value="${department }" readonly="readonly" />
										<input type="hidden" id="departmentId" name="departmentId" value="${departmentId }" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<td>角色</td>
									<td>
										<input id="role" name="role" value="${role }"  readonly="readonly"/>
										<input type="hidden" id="roleId" name="roleId" value="${roleId }" readonly="readonly"/>
									</td>
									<td>工作组</td>
									<td>
										<input id="workgroup" name="workgroup" value="${workgroup }"  readonly="readonly"/>
										<input type="hidden" id="workgroupId" name="workgroupId" value="${workgroupId }"  readonly="readonly"/>
									</td>
								</tr>
								<tr>
									<td>上级部门</td>
									<td>
										<input id="parentDepartment" name="parentDepartment" value="${parentDepartment }" readonly="readonly"/>
										<input type="hidden" id="parentDepartmentId" name="parentDepartmentId" value="${parentDepartmentId }" readonly="readonly"/>
									</td>
									<td>顶级部门</td>
									<td>
										<input id="topDepartment" name="topDepartment" value="${topDepartment }" readonly="readonly" />
										<input type="hidden" id="topDepartmentId" name="topDepartmentId" value="${topDepartmentId }" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<td>直属上级登录名</td>
									<td>
										<input id="superiorLoginName" name="superiorLoginName" value="${superiorLoginName }" readonly="readonly"/>
									</td>
									<td>直属上级部门</td>
									<td>
										<input id="superiorDepartment" name="superiorDepartment" value="${superiorDepartment }" readonly="readonly" />
										<input type="hidden" id="superiorDepartmentId" name="superiorDepartmentId" value="${superiorDepartmentId }" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<td>直属上级角色</td>
									<td>
										<input id="superiorRole" name="superiorRole" value="${superiorRole }" readonly="readonly"/>
										<input type="hidden" id="superiorRoleId" name="superiorRoleId" value="${superiorRoleId }" readonly="readonly"/>
									</td>
									<td>直属上级工作组</td>
									<td>
										<input id="superiorWorkgroup" name="superiorWorkgroup" value="${superiorWorkgroup }" readonly="readonly" />
										<input type="hidden" id="superiorWorkgroupId" name="superiorWorkgroupId" value="${superiorWorkgroupId }" readonly="readonly" />
									</td>
								</tr>
							</table>
						</form>
							<grid:formGrid gridId="planItemsId" code="ES_PLAN_ITEM_EDIT" entity="${plan}" attributeName="planItems" editable="false"></grid:formGrid>
					</div>
				</aa:zone>
			</div>
			</div>
	</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>