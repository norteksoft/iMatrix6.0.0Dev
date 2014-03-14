<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/lang/zh_CN.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/widgets/formeditor/formeditor.js" type="text/javascript"></script>
	<link href="${imatrixCtx}/widgets/formeditor/formeditor.css" rel="stylesheet" type="text/css" />
	
	<script src="${imatrixCtx}/widgets/formeditor/pullDownMenu.js" type="text/javascript" charset="UTF-8"></script>
	<script type="text/javascript">
	function generateHtml(){
		var dataType = $("#dataType").val();
		var controlType=$("#controlType").attr("value") ;
		var classStyle="";
		var styleContent="";
		if($("#classId").attr("value")!=""){
			classStyle=" class='"+$("#classId").attr("value")+"'";
		}
		if($("#styleId").attr("value")!=""){
			styleContent=" style='"+$("#styleId").attr("value")+"'";
		}
		var html;
		if($("#macroType").attr("value").indexOf("SYS_LIST_")>-1){
			html ='<select pluginType="MACRO"  dataType="TEXT"'
				+" id='"+($("#controlId").attr("value")==""?$("#name").attr("value"):$("#controlId").attr("value"))+"' "
				+' name="'+$("#name").attr("value")+'" ' 
				+' title="'+$("#title").attr("value")+'" '
				+' macroType="'+$("#macroType").attr("value")+'" '
				+' macroHide="'+$("#macroHide").attr('checked')+'" '
				+classStyle
				+styleContent
				+'>'
				+'<option selected="selected" value="">{MACRO}</option>';
			html=html+"</select>";
		}else{
			html ="<input pluginType='MACRO'  type='TEXT' dataType='TEXT' value='{MACRO}'"
				+" id='"+($("#controlId").attr("value")==""?$("#name").attr("value"):$("#controlId").attr("value"))+"' "
				+" name='"+$("#name").attr("value")+"' " 
				+" title='"+$("#title").attr("value")+"' "
				+" macroType='"+$("#macroType").attr("value")+"' "
				+" macroHide='"+$("#macroHide").attr('checked')+"' "
				+classStyle
				+styleContent;
			html=html+"/>";
		}
		parent.html(html);
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:80%;
	}
	</style>
	</head>
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class="btn" onclick="$('#macroForm').submit();"><span><span>确定</span></span></button>
				<button class="btn" onclick='parent.$.colorbox.close();'><span><span >取消</span></span></button>
			</div>
			<div id="opt-content">
				<aa:zone name="controlContent">
					<form name="macroForm" id="macroForm" action="${mmsCtx }/form/form-view!text.htm">
						<s:hidden name="id"></s:hidden>
						<s:hidden id="formId" name="formId"></s:hidden>
						<s:hidden id="code" name="code"></s:hidden>
						<s:hidden id="version" name="version"></s:hidden>
						<s:hidden id="standard" name="standard"></s:hidden>
						<s:hidden id="occasion" name="occasion" value="changeSource"></s:hidden>
						<table class="form-table-without-border">
							<tr>
								<td class="content-title" style="width: 22%;">控件类型：</td>
								<td>
									<s:property value="formControl.controlType.code"/>
									<s:hidden theme="simple" id="controlType" name="formControl.controlType" ></s:hidden>
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title">字段名：</td>
								<td>
									<s:textfield theme="simple" id="name" maxlength="27" name="formControl.name" onblur="fieldNameOk(this);" cssClass="{required:true,messages: {required:'必填'}}" ></s:textfield>
									<span class="required">*</span>
								</td>
								<td>
									<span id="nameTip"></span>
								</td>	
							</tr>
							<tr>	
							<td class="content-title">宏控件类型：</td>	
							<td>
						 		<select id="macroType" style="width: 300px;">
								<optgroup label="----单行输入框----">
								<option value="SYS_DATE">当前日期，形如：1999-01-01</option>
								<option value="SYS_DATE_CN" >当前日期，形如：2009年1月1日</option>
								<option value="SYS_DATE_CN_SHORT3">当前日期，形如：2009年</option>
								<option value="SYS_DATE_CN_SHORT4">当前年份，形如：2009</option>
								<option value="SYS_DATE_CN_SHORT1">当前日期，形如：2009年1月</option>
								<option value="SYS_DATE_CN_SHORT2">当前日期，形如：1月1日</option>
								<option value="SYS_TIME">当前时间</option>
								<option value="SYS_DATETIME">当前日期+时间</option>
								<option value="SYS_WEEK">当前星期中的第几天，形如：星期一</option>
								<option value="SYS_USERID">当前用户ID</option>
								<option value="SYS_USERNAME">当前用户姓名</option>
								<option value="SYS_DEPTNAME">当前用户部门名称</option>
								<option value="SYS_DEPTNAME_SHORT">当前用户部门简称</option>
								<option value="SYS_USERROLE">当前用户角色</option>
								<option value="SYS_USERNAME_DATE">当前用户姓名+日期</option>
								<option value="SYS_USERNAME_DATETIME">当前用户姓名+日期+时间</option>
								<option value="SYS_FORMNAME">表单名称</option>
								<option value="SYS_MANAGER_NAME">当前用户直属上级姓名</option>
								<option value="SYS_MANAGER_ID">当前用户直属上级ID</option>
								<option value="SYS_MANAGER_DEPTNAME">当前用户直属上级部门名称</option>
								<option value="SYS_MANAGER_DEPTNAME_SHORT">当前用户直属上级部门简称</option>
								<option value="SYS_TOPDEPTNAME">当前用户顶级部门名称</option>
								<option value="SYS_TOPDEPTNAME_SHORT">当前用户顶级部门简称</option>
								</optgroup>
								<optgroup label="----下拉菜单----">
								<option value="SYS_LIST_ROLE">当前用户所属角色</option>
								<option value="SYS_LIST_SUBORDINATE">当前用户的直属下级</option>
								</optgroup>
							</select>
							</td>
							</tr>
							<tr>
								<td class="content-title">字段别名：</td>
								<td>
								<input id="title" name="formControl.title" class="{required:true,messages: {required:'必填'}}" value="${formControl.title }"/><span class="required">*</span>
								<input id="controlId" type="hidden" name="formControl.controlId" class="" value="${formControl.controlId }"/>
								</td>
								<td><span id="titleTip"></span></td>	
							</tr>
							<tr>
								<td class="content-title">样式类名：</td>
								<td>
									<s:textfield theme="simple" id="classId" name="formControl.classStyle"></s:textfield>
									<br/>注：多个类名以空格分隔
								</td>
								<td></td>	
							</tr>
							<tr>
								<td class="content-title">内联样式：</td>
								<td>
									<s:textfield theme="simple" id="styleId" name="formControl.styleContent"></s:textfield>
									<br/>例如：color: red;
								</td>
								<td></td>	
							</tr>	
							<tr>
								<td class="content-title">单行文本框隐藏：</td>
								<td>
									<input type="checkbox" id="macroHide" name="formControl.macroHide" value="true" <s:if test="formControl.macroHide">checked="checked"</s:if>/>
								</td>
								<td></td>	
							</tr>	
						</table>
					</form>
					<script type="text/javascript">
					$(document).ready(function(){
						var _macroType="${formControl.macroType }";
						if(_macroType!=""){
							$("#macroType").attr("value",_macroType);
						}
					});
					function validateText(){
						$("#macroForm").validate({
							submitHandler: function() {
								generateHtml();
							}
						});
					}
					validateText();
					</script>
			</aa:zone>
			</div>
		</div>
	</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
