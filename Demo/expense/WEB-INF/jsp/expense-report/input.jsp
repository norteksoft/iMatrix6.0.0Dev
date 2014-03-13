<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	<!--上传js-->
	<script type="text/javascript" src="${ctx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${ctx}/js/loan-bill.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	
		<script type="text/javascript">
			 //保存提交验证
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						$(".opt_btn").find("button.btn").attr("disabled","disabled");
						aa.submit('expenseReportForm','','main',__callback);
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
			function __callback(){
				var result = $("#dealResultInput").val();
				//处理任务提交结果
				//dealResult(result);
				expenseReportFormValidate();
				$( "#tabs" ).tabs();
				uploadDocument();
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

			//选择陪同人员
			function selectPerson(){
				var acsSystemUrl = "${ctx}";
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "MAN_DEPARTMENT_TREE",
							noDeparmentUser:true,
							onlineVisible:false
						},
						data: {
						},
						view: {
							title: "选择人员",
							width: 300,
							height:400,
							url:acsSystemUrl,
							showBranch:false
						},
						feedback:{
							enable: true,
			                showInput:"companion",
			                hiddenInput:"userId",
			                append:false
						},
						callback: {
							onClose:function(){
							}
						}			
						};
					    popZtree(zTreeSetting);
			}

			//选择一级审批人
			function selectFirstPerson(){
				var acsSystemUrl = "${ctx}";
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "MAN_DEPARTMENT_TREE",
							noDeparmentUser:true,
							onlineVisible:false
						},
						data: {
							chkStyle:"checkbox",
							chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
						},
						view: {
							title: "选择人员",
							width: 300,
							height:400,
							url:acsSystemUrl,
							showBranch:false
						},
						feedback:{
							enable: true,
					                showInput:"firstApprover",
					                hiddenInput:"userId",
					                append:false
						},
						callback: {
							onClose:function(){
								firstCallBack();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}
			function firstCallBack(){
				$('#firstLoginName').attr("value",ztree.getLoginName());
				$('#firstApproverId').attr("value",ztree.getId());
			}
			
			//选择批示传阅人员
			function selectReadPerson(){
				var acsSystemUrl = "${ctx}";
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "MAN_DEPARTMENT_TREE",
							noDeparmentUser:true,
							onlineVisible:false
						},
						data: {
							chkStyle:"checkbox",
							chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
						},
						view: {
							title: "选择批示传阅人员",
							width: 300,
							height:400,
							url:acsSystemUrl,
							showBranch:false
						},
						feedback:{
							enable: true,
			                showInput:"readPersons",
			                hiddenInput:"readPersonIds",
			                append:false
						},
						callback: {
							onClose:function(){
								readPersonCallBack();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}
			function readPersonCallBack(){
				$('#readPersons').attr("title",ztree.getNames());
				$('#readLoginNames').attr("value",ztree.getLoginNames());
				$('#readPersonIds').attr("value",ztree.getIds());
			}

			//选择会签人员
			function selectSignPerson(){
				var acsSystemUrl = "${ctx}";
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "MAN_DEPARTMENT_TREE",
							noDeparmentUser:true,
							onlineVisible:false
						},
						data: {
							chkStyle:"checkbox",
							chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
						},
						view: {
							title: "选择批示传阅人员",
							width: 300,
							height:400,
							url:acsSystemUrl,
							showBranch:false
						},
						feedback:{
							enable: true,
			                showInput:"signPersons",
			                hiddenInput:"signPersonIds",
			                append:false
						},
						callback: {
							onClose:function(){
								signPersonCallBack();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}
			function signPersonCallBack(){
				$('#signPersons').attr("title",ztree.getNames());
				$('#signLoginNames').attr("value",ztree.getLoginNames());
				$('#signPersonIds').attr("value",ztree.getIds());
			}

			function changeViewSet(opt){
				if(opt=="basic"){
					aa.submit("defaultForm1", "${ctx}/expense-report/input.htm", 'btnZone,viewZone');
				}else if(opt=="history"){
					aa.submit("defaultForm1", "${ctx}/expense-report/history.htm", 'btnZone,viewZone');
				}
			}

			function unloadFunction(){
				if(otherswfu!=undefined){//销毁上传对象
					otherswfu.destroy();
				}
			}
				
		</script>
	</head>
	
	<body onload="getContentHeight();" onunload="unloadFunction();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
					<aa:zone name="btnZone">
						<s:if test="urgenFlag">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/urgen-save.htm','save')"><span><span>保存</span></span></button>
						</s:if><s:else>
							<button class='btn' onclick="submitForm('${ctx}/expense-report/save.htm','save')"><span><span>保存</span></span></button>
							  <s:if test="workflowInfo!=null && workflowInfo.processState.code=='process.submit'">
							  	<button class='btn' onclick="submitForm('${ctx}/expense-report/complete-task.htm','submit')"><span><span>提交</span></span></button>
							  </s:if><s:else>
							 	<button class='btn' onclick="submitForm('${ctx}/expense-report/submit-process.htm','submit')"><span><span>提交</span></span></button>
							  </s:else>
						</s:else>
					</aa:zone>
					</div>
					<form id="defaultForm1" name="defaultForm1"action="">
						<input type="hidden" name="id" id="id" value="${id }"  />
						<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
						<input name="urgenFlag" id="urgenFlag" value="${urgenFlag }" type="hidden"/>
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
					<div id="opt-content" class="form-bg">
					<aa:zone name="viewZone">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="submitFlag" id="submitFlag" value="${submitFlag }"></input>
							<input name="urgenFlag" id="urgenFlag" value="${urgenFlag }" type="hidden"/>
							<input type="hidden" name="taskTransact" id="taskTransact" />
							<input type="hidden" id="dealResultInput" value="${submitResult }"></input>
							<table>
								<tr>
									<td>报销人</td>
									<td>${name }</td>
								</tr>
								<tr>
									<td>报销日期</td>
									<td><s:date name="createdTime" format="yyyy-MM-dd"/></td>
								</tr>
								<tr>
									<td>金额</td>
									<td><input id="money" name="money" value="${money }" class="{required:true,number:true, messages:{number:'请输入数字'}}"/> </td>
								</tr>
								<tr>
									<td>发票张数</td>
									<td><input id="invoiceAmount" name="invoiceAmount" value="${invoiceAmount }"  class="{number:true, messages:{number:'请输入数字'}}"/></td>
								</tr>
								<tr>
									<td>出差日期</td>
									<td>
										<input id="outDate" name="outDate" value="<s:date name="outDate" format="yyyy-MM-dd"/>" readonly="readonly" />
									</td>
								</tr>
								<tr>	
								<td>
									文件：
								</td>
								<td>
									<input id="documentName" readonly="readonly" value="${document.fileName}"/>
									<s:if test="document.fileName!=null">
										<a class='btn' href="#" onclick="openDocument('${document.fileType }','${workflowInfo.workflowId}','${taskId}','${document.id }');"><span><span >在线查看</span></span></a>
									</s:if>
									<br/>
									<s:if test="taskPermission.documentCreateable"><div id="spanButtonPlaceholder" style="margin-left: 65px;"></div></s:if>
									<s:else><div id="spanButtonPlaceholder" style="display: none;margin-left: 65px;"></div></s:else>
								</td>
								</tr>
								<tr>
									<td></td>
									<td><span id="divFileProgressContainer"></span></td>
								</tr>
								<tr>
									<td>陪同人</td>
									<td>
										<input id="companion" name="companion" value="${companion }" readonly="readonly" />
										<input id="userId" type="hidden"/><button onclick="selectPerson()" type="button">选择</button>
									</td>
								</tr>
								<tr>
									<td>一级审批人</td>
									<td>
										<input id="firstApprover" name="firstApprover" value="${firstApprover }" readonly="readonly"/>
										<input id="firstLoginName" name="firstLoginName" value="${firstLoginName }" type="hidden" />
										<input id="firstApproverId" name="firstApproverId" value="${firstApproverId }" type="hidden" />
										<button onclick="selectFirstPerson()" type="button">选择</button>
									</td>
								</tr>
								<tr>
									<td>二级审批人</td>
									<td><input id="secondApprover" name="secondApprover" value="${secondApprover }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>三级审批人</td>
									<td><input id="thirdApprover" name="thirdApprover" value="${thirdApprover }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>财务</td>
									<td><input id="cashier" name="cashier" value="${cashier }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>会签人员：</td>
									<td>
										<input style="width: 300px;" id="signPersons" name="signPersons" value="${signPersons }" readonly="readonly"/>
										<input id="signPersonIds" name="signPersonIds" value="${signPersonIds }" type="hidden" />
										<input id="signLoginNames" name="signLoginNames" value="${signLoginNames }" type="hidden" />
										<button onclick="selectSignPerson()" type="button">选择</button>
									</td>
								</tr>
								<tr>
									<td>批示传阅人员：</td>
									<td>
										<input style="width: 300px;" id="readPersons" name="readPersons" value="${readPersons }" readonly="readonly"/>
										<input id="readPersonIds" name="readPersonIds" value="${readPersonIds }" type="hidden" />
										<input id="readLoginNames" name="readLoginNames" value="${readLoginNames }" type="hidden" />
										<button onclick="selectReadPerson()" type="button">选择</button>
									</td>
								</tr>
								<tr>
									<td>事由</td>
									<td><textarea cols="10" rows="4" id="reason" name="reason">${reason }</textarea></td>
								</tr>
								<!--<tr>
									<td>字段1</td>
									<td><input id="field1" name="field1" value="${field1 }"/></td>
								</tr>
								<tr>
									<td>字段2</td>
									<td><input id="field2" name="field2" value="${field2 }"/></td>
								</tr>
								<tr>
									<td>字段3</td>
									<td><input id="field3" name="field3" value="${field3 }"/></td>
								</tr>
								<tr>
									<td>字段4</td>
									<td><input id="field4" name="field4" value="${field4 }"/></td>
								</tr>
								<tr>
									<td>字段5</td>
									<td><input id="field5" name="field5" value="${field5 }"/></td>
								</tr>
								<tr>
									<td>字段6</td>
									<td><input id="field6" name="field6" value="${field6 }"/></td>
								</tr>
								<tr>
									<td>字段7</td>
									<td><input id="field7" name="field7" value="${field7 }"/></td>
								</tr>
								<tr>
									<td>字段8</td>
									<td><input id="field8" name="field8" value="${field8 }"/></td>
								</tr>
								<tr>
									<td>字段9</td>
									<td><input id="field9" name="field9" value="${field9 }"/></td>
								</tr>
								<tr>
									<td>字段10</td>
									<td><input id="field10" name="field10" value="${field10 }"/></td>
								</tr>
								<tr>
									<td>字段11</td>
									<td><input id="field11" name="field11" value="${field11 }"/></td>
								</tr>
								<tr>
									<td>字段12</td>
									<td><input id="field12" name="field12" value="${field12 }"/></td>
								</tr>
								<tr>
									<td>字段13</td>
									<td><input id="field13" name="field13" value="${field13 }"/></td>
								</tr>
								<tr>
									<td>字段14</td>
									<td><input id="field14" name="field14" value="${field14 }"/></td>
								</tr>
								<tr>
									<td>字段15</td>
									<td><input id="field15" name="field15" value="${field15 }"/></td>
								</tr>
								<tr>
									<td>字段16</td>
									<td><input id="field16" name="field16" value="${field16 }"/></td>
								</tr>
							--></table>
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
						uploadDocument();
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