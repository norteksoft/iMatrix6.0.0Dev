<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>报销单</title>
<%@include file="/common/meta.jsp"%>
<!--上传js-->
	<script type="text/javascript" src="${ctx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${ctx}/js/loan-bill.js"></script>
	<script type="text/javascript" src="${ctx}/js/workflowTag.js"></script>

<script type="text/javascript">
isUsingComonLayout=false;
	$().ready(function() {
		initFunction();
	});

	function initFunction(){
		$( "#tabs" ).tabs();
		$("#outDate").datepicker();
		validateForm();
	}

	function validateForm(){
		addFormValidate($("#fieldPermission").attr("value"), 'expenseReportForm');
		expenseReportFormValidate();
		if(("${task.active }"==0 || "${task.active }"==1|| "${task.active }"==4|| "${task.active }"==6)&&"${taskPermission.documentCreateable}"=="true"){//当待办理、等待设置办理人、待领取、等待选择环节并且有创建正文权限时
			if($("#documentName").val()==""){
				uploadDocument();
			}
		}
	}

	//提交form
	function completeTask(taskTransact) {
		$('#taskTransact').val(taskTransact);
		$("#expenseReportForm").attr("action","${ctx }/expense-report/complete-task.htm");
		$('#expenseReportForm').submit();
	}
	
	//提交form
	function saveTask() {
		$("#expenseReportForm").attr("action","${ctx }/expense-report/save.htm");
		$("#saveTaskFlag").attr("value","true");
		$('#expenseReportForm').submit();
	}

	//表单验证
	function expenseReportFormValidate() { 
		$("#expenseReportForm").validate({
			submitHandler : function() {
				$(".opt_btn").find("button.btn").attr("disabled", "disabled");
				//aa.submit("expenseReportForm", "", 'main', showMsg);
				if("save"==$("#save_sign").val()){
					aa.submit("expenseReportForm", "", 'viewZone', showMsg);
					$("#save_sign").attr("value","");
				}else{
					$("#expenseReportForm").ajaxSubmit(function (id){
						dealResult(id);
					});
				}
			}
		});
	}

	//流转历史和表单信息切换
	function changeViewSet(opt){
		if(opt=="basic"){
			aa.submit("defaultForm1", "${ctx}/expense-report/process-task.htm", 'btnZone,viewZone', validateForm);
		}else if(opt=="history"){
			aa.submit("defaultForm1", "${ctx}/expense-report/history.htm", 'btnZone,viewZone');
		}
	}

	//办理完任务关闭窗口前执行
	function beforeCloseWindow(opt){
		aa.submit("defaultForm1", "${ctx}/expense-report/process-task.htm", 'btnZone,viewZone');
	}
	
	//下载文档
	function downloadDoc(id){
			window.open(webRoot+"/expense-report/download-docment.htm?id="+id);
	}

	//选择加签人员
	function addTask(){
		var acsSystemUrl = "${ctx}";
//		popTree({ title :'选择加签人员',
//			innerWidth:'400',
//			treeType:'MAN_DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'true',
//			hiddenInputId:"addSignPerson",
//			acsSystemUrl:acsSystemUrl,
//			callBack:function(){addSignCallBack();}});
		
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
					title: "选择加签人员",
					width: 300,
					height:400,
					url:acsSystemUrl,
					showBranch:false
				},
				feedback:{
					enable: true,
			                hiddenInput:"addSignPerson",
			                append:false
				},
				callback: {
					onClose:function(){
						addSignCallBack();
					}
				}			
				};
			    popZtree(zTreeSetting);
		
	}
	function addSignCallBack(){
		var userIds = ztree.getIds();
		if(userIds==""||typeof(userIds)=='undefined'){
			$('#addSignPerson').attr("value","all_user");
		}else{
			$('#addSignPerson').attr("value",userIds);
		}
		$("#expenseReportForm").attr("action","${ctx}/expense-report/add-sign.htm");
		$("#expenseReportForm").ajaxSubmit(function (id){
			alert(id);
		});
		validateForm();
	}
	//选择减签人员
	function cutTask(){
		$.colorbox({href:webRoot+"/expense-report/cutsign.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+$("#id").attr("value"),iframe:true, innerWidth:400, innerHeight:500,overlayClose:false,title:"请选择减签人"});
	}

	//领取回调
	function receiveback(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
		$( "#tabs" ).tabs();
	}
	//上传后走
	function rewriteMethod(){
		ajaxSubmit("expenseReportForm", webRoot+"/expense-report/process-task.htm", "main",uploadBack);
	}

	//选择一级审批人
	function selectFirstPerson(){
		var acsSystemUrl = "${ctx}";
//		popTree({ title :'选择一级审批人',
//			innerWidth:'400',
//			treeType:'MAN_DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'false',
//			hiddenInputId:"userId",
//			showInputId:"firstApprover",
//			acsSystemUrl:acsSystemUrl,
//			callBack:function(){firstCallBack();}});
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
					title: "选择一级审批人",
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

	//提交
	workflowButtonGroup.btnSubmitTask.click = function(taskId){
		completeTask('SUBMIT');
	};
	//同意
	workflowButtonGroup.btnApproveTask.click = function(taskId){
		completeTask('APPROVE');
	};
	//不同意
	workflowButtonGroup.btnRefuseTask.click = function(taskId){
		completeTask('REFUSE');
	};
	//加签
	workflowButtonGroup.btnAddCountersign.click = function(taskId){
		addTask();
	};
	//减签
	workflowButtonGroup.btnDeleteCountersign.click = function(taskId){
		cutTask();
	};

	//保存
	workflowButtonGroup.btnSaveForm.click = function(taskId){
		$("#save_sign").attr("value","save");
		saveTask();
	};

	//取回
	workflowButtonGroup.btnGetBackTask.click = function(taskId){
		$("#expenseReportForm").attr("action","${ctx}/expense-report/retrieve.htm");
		$("#expenseReportForm").ajaxSubmit(function (id){
			if(id=="任务已取回"){
				window.location.reload(false);
			}else{
				alert(id);
			}
		});
	};

	//领取
	workflowButtonGroup.btnDrawTask.click = function(taskId){
		aa.submit("defaultForm1", "${ctx}/expense-report/receive-task.htm", 'main',initFunction);
	};
	//放弃领取
	workflowButtonGroup.btnAbandonTask.click = function(taskId){
		aa.submit("defaultForm1", "${ctx}/expense-report/abandonReceive.htm", 'main',initFunction);
	};

	//指派
	workflowButtonGroup.btnAssign.click = function(taskId){
		$.colorbox({href:webRoot+"/expense-report/assign-tree.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+$("#id").attr("value"),iframe:true, innerWidth:400, innerHeight:500,overlayClose:false,title:"指派人员"});
	};

	//已阅
	workflowButtonGroup.btnReadTask.click = function(taskId){
		$('#taskTransact').val('READED');
		aa.submit("expenseReportForm", "${ctx}/expense-report/complete-task.htm", 'main', readTaskCallback);
	};
	//选择环节
	workflowButtonGroup.btnChoiceTache.click = function(){
		completeTask('READED');
	};
	
	function readTaskCallback(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
		window.parent.close();
	}

	//抄送
	workflowButtonGroup.btnCopyTache.click = function(taskId){
		var acsSystemUrl = "${ctx}";
//		popTree({ title :'抄送人员',
//			innerWidth:'400',
//			treeType:'MAN_DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'true',
//			hiddenInputId:"assignee",
//			showInputId:"assignee",
//			acsSystemUrl:acsSystemUrl,
//			callBack:function(){copyPersonCallBack();}});
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
					title: "抄送人员",
					width: 300,
					height:400,
					url:acsSystemUrl,
					showBranch:false
				},
				feedback:{
					enable: true,
							showInput:"assignee",
			                hiddenInput:"assignee",
			                append:false
				},
				callback: {
					onClose:function(){
						copyPersonCallBack();
					}
				}			
				};
			    popZtree(zTreeSetting);
		};

		function copyPersonCallBack(){
			if(ztree.getNames().indexOf("全公司")>=0){
				$('#assignee').attr("value","all_user");
			}else{
				$('#assignee').attr("value",ztree.getIds());
			}
			$("#expenseReportForm").attr("action","${ctx}/expense-report/copy-tasks.htm");
			$("#expenseReportForm").ajaxSubmit(function (id){
				alert(id);
			});
		}
		function gobackTask(){
			$("#expenseReportForm").attr("action","${ctx}/expense-report/goback.htm");
			$("#expenseReportForm").ajaxSubmit(function (id){
				alert(id);
				changeViewSet('basic');
				if(id.indexOf("成功")>=0){
				  window.parent.close();
				}
			});
		}
</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();"  >
	<div class="opt-body">
	<input id="save_sign" type="hidden" value=""/>
<aa:zone name="main">
			<aa:zone name="btnZone">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId }"></wf:workflowButtonGroup>
					<s:if test='task.processingMode.condition != "阅"&&(task.active != 2&&task.active != 3&&task.active != 5&&task.active != 7)'>
						<button class='btn' onclick="gobackTask();">
							<span><span>退回</span>
							</span>
						</button>
					</s:if>
				</div>
				<span id="message" style="display: none;"><font class="onSuccess"><nobr>操作成功！</nobr></font></span>
			</aa:zone>
			<div style="display: none;" id="message">
				<s:actionmessage theme="mytheme" />
			</div>
			<div id="opt-content" class="form-bg">
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${id }"  />
					<input name="taskId" id="taskId" value="${taskId }" type="hidden"/>
					<input id="fieldPermission" value='${fieldPermission }' type="hidden"/>
					<input id="selecttacheFlag" type="hidden" value="true"/>
				</form>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic');">表单信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('history');">流转历史</a></li>
					</ul>
					<div id="tabs-1">
						<aa:zone name="viewZone">
							<form id="expenseReportForm" name="expenseReportForm" method="post"
								action="${ctx }/expense-report/complete-task.htm">
								<input type="hidden" name="id" id="id" value="${id }" />
								<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
								<input type="hidden" name="taskTransact" id="taskTransact" />
								<input  type="hidden"  name="addSignPerson" id="addSignPerson" />
								<input id="saveTaskFlag" name="saveTaskFlag" type="hidden"/>
								<input type="hidden"  name="assignee" id="assignee" ></input>
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
										<td><input id="money" name="money" value="${money }" /></td>
									</tr>
									<tr>
										<td>发票张数</td>
										<td><input id="invoiceAmount" name="invoiceAmount"
											value="${invoiceAmount }" />
										</td>
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
											<s:if test="task.active==0 ||task.active==1||task.active==4||task.active==6">
												<s:if test='document.fileType==".doc"||document.fileType==".wps"||document.fileType==".xls"||document.fileType==".et"||document.fileType==".docx"||document.fileType==".xlsx"||document.fileType==".pdf"'>
													<a class='btn' href="#" onclick="openDocument('${document.fileType }','${workflowInfo.workflowId}','${taskId}','${document.id }');"><span><span >在线查看</span></span></a>&nbsp;<s:if test="taskPermission.documentDownloadable"><a class='btn' href="#" onclick="downloadDoc(${document.id });"><span><span >下载</span></span></a></s:if>
												</s:if>
											</s:if>
											<br/>
											<s:if test="taskPermission.documentCreateable && (task.active==0 ||task.active==1||task.active==4||task.active==6)"><div id="spanButtonPlaceholder" style="margin-left: 65px;"></div></s:if>
											<s:else><div id="spanButtonPlaceholder" style="margin-left: 65px;display: none;"></div></s:else>
										</td>
									</tr>
									<tr>
										<td></td>
										<td><span id="divFileProgressContainer"></span></td>
									</tr>
									<tr>
									<td>一级审批人</td>
									<td>
										<input id="firstApprover" name="firstApprover" value="${firstApprover }" readonly="readonly"/>
										<input id="firstLoginName" name="firstLoginName" value="${firstLoginName }" type="hidden" /><input id="userId" type="hidden"/>
										<input id="firstApproverId" name="firstApproverId" value="${firstApproverId }" type="hidden" />
										<s:if test="ifFirst&&(task.active==0||task.active==1||task.active==4||task.active==6)"><button onclick="selectFirstPerson();" type="button" >选择</button></s:if>
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
										<td>一级审批说明：</td>
										<td><textarea id="firstOpinion" name="firstOpinion">${firstOpinion }</textarea>
										</td>
									</tr>
									<tr>
									<td>会签人员：</td>
									<td>
										<input style="width: 300px;" id="signPersons" name="signPersons" value="${signPersons }" readonly="readonly"/>
									</td>
									</tr>
									<tr>
										<td>批示传阅人员：</td>
										<td>
											<input style="width: 300px;" id="readPersons" name="readPersons" value="${readPersons }" readonly="readonly"/>
										</td>
									</tr>
									<tr>
										<td>事由</td>
										<td><textarea id="reason" name="reason">${reason }</textarea>
										</td>
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
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>