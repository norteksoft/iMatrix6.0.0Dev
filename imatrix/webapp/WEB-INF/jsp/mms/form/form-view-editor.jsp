<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>表单管理</title>
	
</head>
<body style="height:100%;width: 100%;text-align: left;">
<div class="ui-layout-center">
	  	<aa:zone name="form_main">
	  		<div id="message" style="display:none;"></div>
			<table width="100%" align="center" height="100%" class="TableBlock">
				<tbody>
					<tr bgcolor="#DDDDDD">
						<td valign="top" align="center" >
							<div id="controlDivId" style="width:185px;overflow: auto;">
							<table cellspacing="0" cellpadding="0" border="0" align="center" class="TableBlock">
								<tbody>
									<tr class="TableHeader">
										<td align="center">表单控件</td>
									</tr>
									<tr class="TableData">
									  	<td align="center">
									  	<button onclick="controlClick('TEXT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/textfield.gif"/> 单行输入框</button><br>
										<button onclick="controlClick('TEXTAREA');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/textarea.gif"/> 多行输入框</button><br>
										<button onclick="controlClick('PULLDOWNMENU');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/listmenu.gif"/> 下拉菜单</button><br>
										<button onclick="controlClick('RADIO');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/checkbox.gif"/> 单选/复选框</button><br>
										<button onclick="controlClick('TIME');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/calendar.gif"/> 日历控件</button><br>
										<button onclick="controlClick('PLACEHOLDER');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/label.gif"/> 占位符控件</button><br>
										<button onclick="controlClick('MACRO');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/auto.gif"/> 宏控件</button><br>
										<button onclick="controlClick('CALCULATE_COMPONENT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/calc.gif"/> 计算控件</button><br>
										<button onclick="controlClick('SELECT_MAN_DEPT');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/user.gif"/> 部门人员控件</button><br>
										<button onclick="controlClick('BUTTON');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/button.gif"/> 按钮控件</button><br>
										<button onclick="controlClick('LABEL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/label.gif"/> 标签控件</button><br>
										<button onclick="controlClick('STANDARD_LIST_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/standardList.gif"/> 标准列表控件</button><br>
										<button onclick="JCControlClick('JAVASCRIPT_CSS');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/js_css.gif"/> JavaScript/CSS</button><br>
										<button onclick="controlClick('ATTACH_UPLOAD');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/attach.gif"/> 附件上传控件</button><br>
										<button onclick="imageControlClick('IMAGE');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/imgupload.gif"/> 图片控件</button><br>
										<button onclick="controlClick('IMAGE_UPLOAD');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/imgupload.gif"/> 图片上传控件</button><br>
										<button onclick="controlClick('SIGNATURE_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/seal.gif"/> 签章控件</button><br>
										<button onclick="controlClick('LIST_CONTROL');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/customList.gif"/> 自定义列表控件</button><br>
										<button onclick="controlClick('DATA_SELECTION');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/selectData.gif"/> 数据选择控件</button><br>
										<button onclick="controlClick('DATA_ACQUISITION');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/getData.gif"/> 数据获取控件</button><br>
										<button onclick="controlClick('URGENCY');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/urgen.gif"/> 紧急程度设置控件</button><br>
										<button onclick="controlClick('CREATE_SPECIAL_TASK');" class="btnTool"><img src="${imatrixCtx}/widgets/formeditor/form/urgen.gif"/> 特事特办控件</button><br>
										<button onclick="editorSave(editor);" class="btnControl">保存表单</button><br>
										<button onclick="toPreview();" class="btnControl">预览表单</button><br>
										<button onclick="cancelClick();" class="btnControl">关闭设计器</button>
										</td>
									</tr>
								</tbody>
							</table>
							</div>
						</td>
						<td width="100%" valign="top" bgcolor="#DDDDDD" height="100%">
							<div class="editor" style="width:100%;margin: 5px 5px 5px 5px;">
								<textarea id="content"  style="visibility:hidden;">${htmlCode}</textarea>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<s:actionmessage/>
			<div style="display: none;">
				<form id="inputForm" name="inputForm" action="${mmsCtx }/form/form-view!save.htm" method="post">
					<s:hidden id="formId" name="formId"></s:hidden>
					<s:hidden id="operation" name="operation"></s:hidden>
					<s:textfield id="menuId" name="menuId" theme="simple"></s:textfield>
					<s:textfield id="isStandard" name="isStandard" theme="simple"></s:textfield>
					<s:textfield id="code" name="code" theme="simple"></s:textfield>
					<s:textfield id="name" name="name" theme="simple"></s:textfield> 
					<s:textfield id="version" name="version" theme="simple"></s:textfield>
					<s:textfield id="formStates" name="formState" theme="simple"></s:textfield> 
					<s:textarea theme="simple"  name="remark" id="remark"  cols="55" rows="5" ></s:textarea>
					<input id="html" name="htmlResult"></input>
				</form>			
			</div>	
			<div style="display: none;">
				<div id="saveChoice" style="margin-top: 5px">
					<div class="opt-btn" style="margin-bottom: 5px">
						<button class="btn" onclick="updateVersion();$.colorbox.close();"><span><span>更新当前版本</span></span></button>
						<button class="btn" onclick="saveNewVersion();$.colorbox.close();"><span><span>保存为新版本</span></span></button>
						<button class="btn" onclick="$.colorbox.close();"><span><span >取消</span></span></button>
					</div>
					<div style="margin-left: 5px">
						<font color="red">更新表单后,请注意更新流程图！</font> 
					</div>
				</div>
			</div>
			<form name="backForm" id="backForm" action="${mmsCtx }/form/list-data.htm" method="post">
				<s:hidden id="menuId" name="menuId"></s:hidden>
				<s:hidden name="dataTableId"></s:hidden>
			</form>
			<script type="text/javascript" >
				setControlDivStyle();
				function setControlDivStyle(){
					var h = $(window).height()+50;
					$("#controlDivId").css("height",h);
				}
			</script>
			
		</aa:zone>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
