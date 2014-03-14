<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>${modulePage.name }</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<!--上传js-->
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/swfupload/otherHandlers.js"></script>
	
	<script src="${imatrixCtx}/widgets/formeditor/kindeditor.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/util.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/text.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/wf/js/opinion.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	
	<script type="text/javascript" src="${imatrixCtx}/wf/js/workflowTag.js"></script>
	<script src="${imatrixCtx}/mms/js/mmsapi.js" type="text/javascript"></script>
	<script src="${imatrixCtx}/mms/js/mms-workflow.js" type="text/javascript"></script>
	<view:JC code="${formCode}" version="${formVersion}"></view:JC>
	
	<script type="text/javascript">
	  isUsingFormLayout=false;
		$().ready(function() {
			$( "#tabs" ).tabs();
			format_Validate();
		});

		function format_Validate(){
			addFormValidate($("#_validate_string").attr("value"),'default_submit_form');
			_formValidate();
		}
		
		//启用流程
		workflowButtonGroup.btnStartWorkflow.click= function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				getRichTextValue();
				getCustomListControlValue();
				__parseCustomDateTypeValue();
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", needChoose);
			}
		};
		//提交流程
		workflowButtonGroup.btnSubmitWorkflow.click = function(taskId){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  alert("请您填写意见！");
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'SUBMIT');
					getRichTextValue();
					getCustomListControlValue();
					__parseCustomDateTypeValue();
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		
		//保存按钮
		workflowButtonGroup.btnSaveForm.click= function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				getRichTextValue();
				__parseCustomDateTypeValue();
				getCustomListControlValue();
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/save-task.htm", "default_refresh_zone", needChoose);
			}
		};
		//提交按钮
		workflowButtonGroup.btnSubmitTask.click= function(){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  alert("请您填写意见！");
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'SUBMIT');
					getRichTextValue();
					getCustomListControlValue();
					__parseCustomDateTypeValue();
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		//同意按钮
		workflowButtonGroup.btnApproveTask.click = function(taskId){
			var opinionRight = $("#opinionRight").val();
			var opinionSize = $("#opinionSize").val();
			var doneOpinion = $("#doneOpinion").val(); 
			if((opinionRight.indexOf('must')!=-1)&&(opinionSize==0)&&(doneOpinion=='')){
                  alert("请您填写意见！");
			}else{
				$("#default_submit_form").submit();
				if($('#_is_validate_ok').attr('value')=="TRUE"){
					$('#__transact').attr('value', 'APPROVE');
					getRichTextValue();
					getCustomListControlValue();
					__parseCustomDateTypeValue();
					ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
				}
			}
		};
		//不同意按钮
		workflowButtonGroup.btnRefuseTask.click = function(taskId){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'REFUSE');
				getRichTextValue();
				getCustomListControlValue();
				__parseCustomDateTypeValue();
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};

		//抄送
		workflowButtonGroup.btnCopyTache.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
//			popTree({ title :'抄送人员',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'true',
//				hiddenInputId:"assignee",
//				showInputId:"assignee",
//				acsSystemUrl:imatrixRoot,
//				callBack:function(){copyPersonCallBack();}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:false,
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
						url:imatrixRoot,
						showBranch:true
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
				$('#assignee').attr("value",ztree.getIds());
				$("#default_submit_form").attr("action","${mmsCtx}/common/copy-tache.htm");
				$("#default_submit_form").ajaxSubmit(function (id){
					alert(id);
				});
			}
			
		//指派
		workflowButtonGroup.btnAssign.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
//			popTree({ title :'指派任务',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"assignee",
//				showInputId:"assignee",
//				acsSystemUrl:imatrixRoot,
//				callBack:function(){btnAssignCallBack();}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:false,
						onlineVisible:false
					},
					data: {
						//chkStyle:"checkbox",
						//chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
					},
					view: {
						title: "指派任务",
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                showInput:"assignee",
				                hiddenInput:"assignee",
				                append:false
					},
					callback: {
						onClose:function(){
							btnAssignCallBack();;
						}
					}			
					};
				    popZtree(zTreeSetting);
			};

		function btnAssignCallBack(){
			$('#assignee').attr("value",ztree.getId());
			$("#default_submit_form").attr("action","${mmsCtx}/common/assign-tree.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				alert(id);
				window.parent.close();
			});
		}

		//加签
		workflowButtonGroup.btnAddCountersign.click = function(taskId){
			var acsSystemUrl = "${mmsCtx}";
//			popTree({ title :'选择加签人员',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'true',
//				hiddenInputId:"assignee",
//				showInputId:"assignee",
//				acsSystemUrl:imatrixRoot,
//				callBack:function(){addCountersignCallBack();}});
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
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                showInput:"assignee",
				                hiddenInput:"assignee",
				                append:false
					},
					callback: {
						onClose:function(){
							addCountersignCallBack();
						}
					}			
					};
				    popZtree(zTreeSetting);
			};

		function addCountersignCallBack(){
			var userIds = ztree.getIds();
			if(userIds==""||typeof(userIds)=='undefined'){
				$('#assignee').attr("value","all_user");
			}else{
				$('#assignee').attr("value",userIds);
			}
			$("#default_submit_form").attr("action","${mmsCtx}/common/add-assign.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				alert(id);
			});
		}
		
		//减签
		workflowButtonGroup.btnDeleteCountersign.click = function(taskId){
				custom_ztree({url:webRoot+'/common/remove-assign-tree.htm',
					onsuccess:function(){removeCountersignCallBack();},
					width:300,
					height:400,
					title:'选择减签人员',
					postData:{taskId:$("#taskId").attr("value")},
					nodeInfo:['type','name','loginName','transactorId'],
					multiple:true,
					webRoot:imatrixRoot
				});
			};

		function removeCountersignCallBack(){
			var transactorIds=getSelectValue("transactorId");
			var resultNames="";
			for(var i=0;i<transactorIds.length;i++){
				if(transactorIds[i]!="company"){
					resultNames=resultNames+transactorIds[i]+",";
				}
			}
			if(resultNames.indexOf(",")>=0){
				resultNames=resultNames.substring(0,resultNames.lastIndexOf(","));
			}
			$('#assignee').attr("value",resultNames);
			$("#default_submit_form").attr("action","${mmsCtx}/common/remove-assign.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				alert(id);
			});
		}
			
		function needChoose(){
			closePopbox();
			var url = $('#_choose_url').val();
			if('choose_user'==url){
				$( "#tabs" ).tabs();
				popbox("select_transactor", 300, 400, "请选择办理人");
				return;
			}
			if(typeof(url)!='undefined'){
				if(url.length > 0){
					$( "#tabs" ).tabs();
					format_Validate();
					$.colorbox({href:webRoot+$('#_choose_url').val(),iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:"请选择",onClosed:function(){format_Validate();}});
				}else{
					$( "#tabs" ).tabs();
					format_Validate();
					__show_message('opt_message','操作成功！','onSuccess');
				}
			}
		}
		function ___show(){
			__show_message('opt_message');
		}
		//阅读按钮
		workflowButtonGroup.btnReadTask.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'READED');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",needChoose);
			}
		};
		
		//选择办理人
		workflowButtonGroup.btnAssignTransactor.click = function(){
			format_Validate();
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SUBMIT');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",assignTransactorCallback);
			}
		};

		function assignTransactorCallback(){
			needChoose();
			format_Validate();
		}
		
		//选择环节
		workflowButtonGroup.btnChoiceTache.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SUBMIT');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone",needChoose);
			}
		};
		
		//赞成票按钮
		workflowButtonGroup.btnVoteAgreement.click = function(){ // 投票
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'AGREEMENT');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//反对票按钮
		workflowButtonGroup.btnVoteOppose.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'OPPOSE');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//弃权按钮
		workflowButtonGroup.btnVoteKiken.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'KIKEN');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		// 分发按钮
		workflowButtonGroup.btnDistributeTask.click = function(){ // 分发
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'DISTRIBUTE');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//交办指派按钮
		workflowButtonGroup.btnAssignTask.click = function(){//交办
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'ASSIGN');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};
		
		//签收任务按钮
		workflowButtonGroup.btnSignoffTask.click = function(){
			$("#default_submit_form").submit();
			if($('#_is_validate_ok').attr('value')=="TRUE"){
				$('#__transact').attr('value', 'SIGNOFF');
				ajaxAnyWhereSubmit("default_submit_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", needChoose);
			}
		};

		/*
		 * 领取任务
		 */
		workflowButtonGroup.btnDrawTask.click = function(taskId){
			$("#default_submit_form").attr("action","${mmsCtx}/common/drawTask.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				if(id=='task.not.need.receive'){
					//window.location.reload(true);
					$("#drawTask_message").html("<font class=\"onError\"><nobr>不需要领取,可能已被他人领取</nobr></font>");
					$("#drawTask_message").show("show");
					setTimeout('$("#drawTask_message").hide("show");',3000);
					setTimeout('window.location.reload(false);',2000);
				}else if(id=='task.receive.success'){
					window.location.reload(false);
					//alert("领取成功");
				}else{
					alert("领取出错");
				}
			});
		};

		/*
		 * 放弃领取的任务
		 */
		workflowButtonGroup.btnAbandonTask.click = function(taskId){
			$("#default_submit_form").attr("action","${mmsCtx}/common/abandonReceive.htm");
			$("#default_submit_form").ajaxSubmit(function (id){
				if(id=="task.abandon.receive.success"){
					window.location.reload(false);
				}else{
					alert("放弃领取出错");
				}
			});
		};
		
		function getTaskId(){
			return $("#taskId").attr("value");
		}


	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="default_refresh_zone">
			<style type="text/css">
				#tabs,.ui-tabs .ui-tabs-nav li,.ui-jqgrid,.ui-jqgrid .ui-jqgrid-htable th div,.ui-jqgrid .ui-jqgrid-view,.ui-jqgrid .ui-jqgrid-hdiv,.ui-jqgrid .ui-jqgrid-bdiv{ position: static; }
			</style>
			<aa:zone name="button_zone">
				<div class="opt-btn">
					<wf:workflowButtonGroup taskId="${taskId}"></wf:workflowButtonGroup>
				</div>
			</aa:zone>
			<div id="opt-content">
				<div id="opt_message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<div id="drawTask_message" style="display:none;"></div>
				<form id="defaultForm1" name="defaultForm1"action="">
					<input type="hidden" name="id" id="id" value="${data.id }"  />
					<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
					<input type='hidden' value="" id="_is_validate_ok"/>
				</form>
				<div id="tabs">
					<ul >
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/task.htm','button_zone,content_zone',format_Validate);">表单信息</a></li>
						<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/history.htm','button_zone,content_zone');">流转历史</a></li>
						<s:if test="permission.countersignResultVisible">
							<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/countersign.htm','button_zone,content_zone');">会签结果</a></li>
						</s:if>
						<s:if test="permission.voteResultVisible">
							<li><a href="#tabs-1"  onclick="ajaxAnyWhereSubmit('defaultForm1','${mmsCtx}/common/vote.htm','button_zone,content_zone');">投票结果</a></li>
						</s:if>
					</ul>
					<div id="tabs-1">
						<aa:zone name="content_zone">
							<form action="" id="default_submit_form" name="default_submit_form" method="post">
								<input type="hidden" id="_choose_url" value="${chooseUrl}">
								<input type='hidden' id="__transact" name="transact" value="">
								<input type="hidden"  id="taskId" name="taskId" value="${taskId}">
								<input type='hidden' value='${validateString}' id="_validate_string" />
								<input type='hidden' value='${richString}' id="_rich_string" name="richString" />
								<input type='hidden' id="id"  value="${data.id}"/>
								<input type="hidden"  name="assignee" id="assignee" ></input>
								<input type="hidden" id="opinionRight"  value="${opinionRight}"/>
								<input type="hidden" id="opinionSize"  value="${opinionSize}"/>
								<input type="hidden" id="doneOpinion"  value=""/>
								<aa:zone name="history_refresh_zone">
									<view:formView code="${formCode}" entity="${data}" version="${formVersion}" taskId="${taskId}"></view:formView>
								</aa:zone>
								</form>
									<p>办理意见：
										<a href="#" onclick="_view_opinion(this,'${taskId}');" state="1"><img id="viewImg" src="${imatrixCtx}/mms/images/x1.png"></a> 
									</p>
									<aa:zone name="default_opinion_zone"></aa:zone>
									
									<p>正&emsp;&emsp;文：
									   <a href="#" onclick="_view_text(this,'${taskId }');" state="1"><img id="textImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_text_zone"></aa:zone>
									
									<p>附&emsp;&emsp;件：
										<a href="#" onclick="_view_accessory(this,'${taskId }');" state="1"><img id="accImg" src="${mmsCtx}/images/x1.png"></a> 
									</p>
									<aa:zone name="default_accessory_zone"></aa:zone>
								<form action="" name="workflow_attachments_form" id="workflow_attachments_form" method="post">
									<input type="hidden" name="taskId" value="${taskId }"/>
									<input type="hidden" id="companyId" value="${data.company_id }"/>
								</form>
								
								<form id="officeForm1" name="officeForm1" action="" method="post">
									<input type="hidden" id="workflowId" name="workflowId" value="${workflowId}">
									<input type="hidden" id="taskId" name="taskId" value="${taskId}">
						        </form>
						        <form action="" id="opinion_form" name="opinion_form" method="post">
						        	<input type="hidden" name="taskId" value="${taskId }"/>
						        </form>
								<form action="" name="officeForm" id="officeForm" method="post">
									<input type="hidden" name="workflowId" value="${workflowId}"/>
									<input type="hidden" name="taskId" value="${taskId}"/>
									<input type="hidden" name="opinion" id="approveOpinion"/>
								</form>
						</aa:zone>
					</div>
				</div>
			</div>
			<div id="select_transactor" style="display: none;">
					<s:if test="choiceTransactor.size()>0">
					<div class="opt-btn">
						<button class="btn" type="button" onclick="selectTransactorOk();"><span><span>确定</span></span></button> 
					</div>
					<form action="#" id="select_transactor_form" name="select_transactor_form" method="post">
						<s:iterator value="choiceTransactor" id="user">
							<div style="padding: 4px 8px;"> <input type="radio" value="<s:property value="#user.key"/>" name="transactor"/><s:property value="value.name"/> </div>
						</s:iterator>
						<input name="taskId" id="transact_task_id" type="hidden" value="${taskId }"/>
					</form>
					<script type="text/javascript">
						function selectTransactorOk(){
							if($("input[name='transactor']:checked").length==1){
								$('#transact_task_id').attr('value', $('#taskId').val());
								ajaxAnyWhereSubmit("select_transactor_form", webRoot+"/common/submit-task.htm", "default_refresh_zone", closeSelectTransactor);
							}else{
								alert('请选择办理人');
							}
						}

						function closeSelectTransactor(){
							window.parent.close();
						}
					</script>
					</s:if>
				</div>
		</aa:zone>
	</div>
</div>
</body>

</html>
