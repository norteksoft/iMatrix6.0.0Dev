<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
		<script type="text/javascript">
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						$(".opt_btn").find("button.btn").attr("disabled","disabled");
						aa.submit('expenseReportForm','','main',callback);
					},
					rules: {
						"name": {
							required:true
						}
					},
					messages: {
						"name": {
							required:"必填"
						}
					}
				});
			}
			function submitForm(url){
				$('#expenseReportForm').attr('action',url);
				$('#expenseReportForm').submit();
			}
			function callback(){
				var isZtree = $("#isZtree").val();
				showMsg();
				if(isZtree=='true'){
					window.parent.createDynamicZtree();
					window.parent.$("#_ztree_folderId").attr("value","");
					window.parent.$("#ztree_dynamic_id").attr("value","");
					window.parent.$("#ztree_dynamic_name").attr("value","");
					window.parent.$("#ztree_dynamic_pid").attr("value","");
				}else{
					window.parent.location="${ctx}/loan-bill/loan-bill-list.htm";
				}
				window.parent.$.colorbox.close();
				
			}
			
			//显示提示信息，3秒后隐藏
			function showMsg(id,time){
				if(id==undefined)id="message";
				$("#"+id).show();
				if(time==undefined)time=3000;
				setTimeout('$("#'+id+'").hide();',time);
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm('${ctx}/loan-bill/loan-bill-save.htm')"><span><span>保存</span></span></button>
					</div>
					<span id="message" style="display: none;"><font class="onSuccess"><nobr>保存成功！</nobr></font></span>
					<div id="opt-content" class="form-bg">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
						 <div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
							<input type="hidden"   name="id" id="id" value="${id }" />
							<input  type="hidden" name="isZtree" id="isZtree" value="${isZtree }" />
							<table>
								<s:if test="!updateSign">
								<tr>
									<td>父文件夹：</td>
									<td>
										<s:if test="parentFolderName==null">
											<input id="parentName" name="parentName" value="根目录" readonly="readonly"/>
											<input id="parentId" name="parentId" value="0"  type="hidden"/>
										</s:if><s:else>
											<input id="parentName" name="parentName" value="${parentFolderName }" readonly="readonly" />
											<input id="parentId" name="parentId" value="${parentFolderId }"  type="hidden"/>
										</s:else>
									</td>
								</tr>
								</s:if>
								<tr>
									<td>名称：</td>
									<td><input id="name" name="name" value="${name }" /></td>
								</tr>
							</table>
						
						</form>
					</div>
					<script type="text/javascript">
						$(document).ready(function(){
							expenseReportFormValidate();
						});
					</script>
				</aa:zone>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>