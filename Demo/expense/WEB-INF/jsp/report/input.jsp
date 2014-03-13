<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	<script type="text/javascript" src="${ctx}/js/loan-bill.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	
		<script type="text/javascript">
			
			 //保存提交验证
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						aa.submit('expenseReportForm','','main',callback);
					}
				});
			}

			function validateForm(){
				addFormValidate('${fieldPermission}', 'expenseReportForm');
				expenseReportFormValidate();
			}
			//保存提交
			var submitType;
			function submitForm(url,type){
				submitType = type;
				$('#taskTransact').val("SUBMIT");
				if(type=='submit'&& $('#documentName').attr('value')==''){
					alert("请先上传文件！");
					return;
				}
				$('#expenseReportForm').attr('action',url);
				$('#expenseReportForm').submit();
			}

			//保存提交回调
			function callback(){
				var result = $("#dealResultInput").val();
				//处理任务提交结果
				//dealResult(result);
				expenseReportFormValidate();
				$( "#tabs" ).tabs();
				getContentHeight();
				var results=result.split(";");
				if(typeof results[1]=='undefined'||results[1]==null||results[1]==''){//如果不是选择环节、选择办理人、填写意见时，关闭窗口
					$("#message").show("show");
					setTimeout('$("#message").hide("show");',3000);
					if(submitType=='submit'){
						window.parent.$.colorbox.close();
						window.close();
					}
				}
			}

			function changeViewSet(opt){
				if(opt=="basic"){
					aa.submit("defaultForm1", "${ctx}/expense-report/input.htm", 'btnZone,viewZone');
				}else if(opt=="history"){
					aa.submit("defaultForm1", "${ctx}/expense-report/history.htm", 'btnZone,viewZone');
				}
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<s:if test="ifHasStartedProcess">
						<div class="opt-btn">
						<aa:zone name="btnZone">
							<s:if test="urgenFlag">
								<button class='btn' onclick="submitForm('${ctx}/report/save.htm','save')"><span><span>保存</span></span></button>
							</s:if><s:else>
								<button class='btn' onclick="submitForm('${ctx}/report/save.htm','save')"><span><span>保存</span></span></button>
								  <s:if test="workflowInfo!=null && workflowInfo.processState.code=='process.submit'">
								  	<button class='btn' onclick="submitForm('${ctx}/report/complete-task.htm','submit')"><span><span>提交</span></span></button>
								  </s:if><s:else>
								 	<button class='btn' onclick="submitForm('${ctx}/report/submit-process.htm','submit')"><span><span>提交</span></span></button>
								  </s:else>
							</s:else>
						</aa:zone>
						</div>
					</s:if>
					<form id="defaultForm1" name="defaultForm1"action="">
						<input type="hidden" name="id" id="id" value="${id }"  />
						<input name="taskId" id="taskId" value="${taskId }" type="hidden"/>
					</form>
					<div id="tabs">
					<s:if test="urgenFlag">
						<ul>
							<li><a href="#tabs-1" onclick="changeViewSet('basic');">表单信息</a></li>
							<li><a href="#tabs-1" onclick="changeViewSet('history');">流转历史</a></li>
						</ul>
					</s:if>
					<div id="tabs-1">
					<span id="message" style="display: none;"><font class="onSuccess"><nobr>操作成功！</nobr></font></span>
					<div id="proceseMessage"><s:actionmessage theme="mytheme" /></div>	
					<div id="opt-content" class="form-bg">
					<aa:zone name="viewZone">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="submitFlag" id="submitFlag" value="${submitFlag }"></input>
							<input type="hidden" name="taskTransact" id="taskTransact" />
							<input type="hidden" id="dealResultInput" value="${submitResult }"></input>
							<table>
								<tr>
									<td>报销人</td>
									<td>${name }</td>
								</tr>
								<tr>
									<td>部门</td>
									<td><input id="department" name="department" value="${department }"></input></td>
								</tr>
								<tr>
									<td>金额</td>
									<td><input id="money" name="money" value="${money }" class="{required:true,number:true, messages:{number:'请输入数字'}}"/> </td>
								</tr>
								<tr>
									<td>发票金额</td>
									<td><input id="invoiceAmount" name="invoiceAmount" value="${invoiceAmount }" class="{required:true,number:true, messages:{number:'请输入数字'}}"/> </td>
								</tr>
							</table>
						</form>
						<span>意见：</span>
							<wf:opinion companyId="${companyId}" taskId="${taskId}"></wf:opinion>
						</aa:zone>
					</div>
				</div>
				</div>
				</aa:zone>
					<script type="text/javascript">
						//流程环节设置的必填字段
						$(document).ready(function(){
							$( "#tabs" ).tabs();
							$("#outDate").datepicker();
							addFormValidate('${fieldPermission}','expenseReportForm');
							expenseReportFormValidate();
						});
					</script>
			</div>
		</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>