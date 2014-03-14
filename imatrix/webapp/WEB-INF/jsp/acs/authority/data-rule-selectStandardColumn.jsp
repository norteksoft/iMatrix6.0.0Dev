<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表字段</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
	<aa:zone name="main_zone">
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="submitStandardColumn();"><span><span >确定</span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
			<aa:zone name="viewZone">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="" name="pageForm" id="pageForm" method="post">
					<table class="form-table-border-left" style="width: 150px;margin-left: 5px;margin-top: 5px;">
						<tr>
							<td style="width: 25%;"><input name="fieldName" type="radio" value="~~creatorId" title="<s:text name="data.rule.standard.field.creator"></s:text>"/></td>
							<td><s:text name="data.rule.standard.field.creator"></s:text></td>
						</tr>
						<tr>
							<td style="width: 25%;"><input name="fieldName" type="radio" value="~~departmentId" title="<s:text name="data.rule.standard.field.department"></s:text>"/></td>
							<td><s:text name="data.rule.standard.field.department"></s:text></td>
						</tr>
						<tr>
							<td style="width: 25%;"><input name="fieldName" type="radio" value="~~roleId" title="<s:text name="data.rule.standard.field.role"></s:text>"/></td>
							<td><s:text name="data.rule.standard.field.role"></s:text></td>
						</tr>
						<tr>
							<td style="width: 25%;"><input name="fieldName" type="radio" value="~~workgroupId" title="<s:text name="data.rule.standard.field.workgroup"></s:text>"/></td>
							<td><s:text name="data.rule.standard.field.workgroup"></s:text></td>
						</tr>
					</table>
				</form>
			</aa:zone>
		</div>
	</aa:zone>
</div>
</body>
</html>
