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
		$( "#tabs" ).tabs();
		$("#outDate").datepicker();
		validateForm();
		$("#workflowButtonGroup_btnAssign").hide();
	});

	function validateForm(){
		addFormValidate('${fieldPermission}', 'expenseReportForm');
		expenseReportFormValidate();
		if(("${task.active }"==0 || "${task.active }"==1|| "${task.active }"==4|| "${task.active }"==6)&&"${taskPermission.documentCreateable}"=="true"){//当待办理、等待设置办理人、待领取、等待选择环节并且有创建正文权限时
			uploadDocument();
		}
		$("#workflowButtonGroup_btnAssign").hide();
	}

	//提交form
	function completeTask(taskTransact) {
		$('#taskTransact').val(taskTransact);
		$("#expenseReportForm").attr("action","${ctx }/report/complete-task.htm");
		$('#expenseReportForm').submit();
	}
	
	//提交form
	function saveTask() {
		$("#expenseReportForm").attr("action","${ctx }/report/save.htm");
		$("#saveTaskFlag").attr("value","true");
		$('#expenseReportForm').submit();
	}

	//表单验证
	function expenseReportFormValidate() { 
		$("#expenseReportForm").validate({
			submitHandler : function() {
				$(".opt_btn").find("button.btn").attr("disabled", "disabled");
				//aa.submit("expenseReportForm", "", 'main', showMsg);
				$("#expenseReportForm").ajaxSubmit(function (id){
					dealResult(id);
				});
			}
		});
	}
	//流转历史和表单信息切换
	function changeViewSet(opt){
		if(opt=="basic"){
			aa.submit("defaultForm1", "${ctx}/report/process-task.htm", 'btnZone,viewZone', validateForm);
		}else if(opt=="history"){
			aa.submit("defaultForm1", "${ctx}/report/history.htm", 'btnZone,viewZone');
		}
	}

	//办理完任务关闭窗口前执行
	function beforeCloseWindow(opt){
		aa.submit("defaultForm1", "${ctx}/report/process-task.htm", 'btnZone,viewZone');
	}
	
	//下载文档
	function downloadDoc(id){
			window.open(webRoot+"/report/download-docment.htm?id="+id);
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
			                //showInput:"superiorDepartment",
			                hiddenInput:"addSignPerson",
			                append:false
				},
				callback: {
					onClose:function(){
						addSignCallBack();;
					}
				}			
				};
			    popZtree(zTreeSetting);
	}
	function addSignCallBack(){
		if(ztree.getNames().indexOf("全公司")>=0){
			$('#addSignPerson').attr("value","all_user");
		}else{
			$('#addSignPerson').attr("value",ztree.getLoginNames());
		}
		$("#expenseReportForm").attr("action","${ctx}/report/add-sign.htm");
		$("#expenseReportForm").ajaxSubmit(function (id){
			alert(id);
		});
		validateForm();
	}
	//选择减签人员
	function cutTask(){
		$.colorbox({href:webRoot+"/report/cutsign.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+$("#id").attr("value"),iframe:true, innerWidth:400, innerHeight:500,overlayClose:false,title:"请选择减签人"});
	}

	//领取回调
	function receiveback(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
		$( "#tabs" ).tabs();
	}
	//上传后走
	function rewriteMethod(){
		ajaxSubmit("expenseReportForm", webRoot+"/report/process-task.htm", "main",uploadBack);
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
					//chkStyle:"checkbox",
					//chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
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

	//减签
	workflowButtonGroup.btnSaveForm.click = function(taskId){
		saveTask();
	};

	//取回
	workflowButtonGroup.btnGetBackTask.click = function(taskId){
		$("#expenseReportForm").attr("action","${ctx}/report/retrieve.htm");
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
		aa.submit("defaultForm1", "${ctx}/report/receive-task.htm", 'btnZone');
	};
	//放弃领取
	workflowButtonGroup.btnAbandonTask.click = function(taskId){
		aa.submit("defaultForm1", "${ctx}/report/abandonReceive.htm", 'btnZone');
	};

	//指派
	workflowButtonGroup.btnAssign.click = function(taskId){
		$.colorbox({href:webRoot+"/report/assign-tree.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+$("#id").attr("value"),iframe:true, innerWidth:400, innerHeight:500,overlayClose:false,title:"指派人员"});
	};

	//已阅
	workflowButtonGroup.btnReadTask.click = function(taskId){
		$('#taskTransact').val('READED');
		aa.submit("expenseReportForm", "${ctx}/report/complete-task.htm", 'main', readTaskCallback);
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
				$('#assignee').attr("value",ztree.getLoginNames());
			}
			$("#expenseReportForm").attr("action","${ctx}/report/copy-tasks.htm");
			$("#expenseReportForm").ajaxSubmit(function (id){
				alert(id);
			});
		}
		function gobackTask(){
			$("#expenseReportForm").attr("action","${ctx}/report/goback.htm");
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
<aa:zone name="main">
			<aa:zone name="btnZone">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId }"></wf:workflowButtonGroup>
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
								action="${ctx }/report/complete-task.htm">
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
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>