<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/meta.jsp" %>	
	<!--上传js-->
	<script type="text/javascript" src="${ctx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${ctx}/js/loan-bill.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
		<script type="text/javascript">
		isUsingComonLayout=false;
			 //保存提交验证
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						$(".opt_btn").find("button.btn").attr("disabled","disabled");
						aa.submit('expenseReportForm','','main',_callback);
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
			function _callback(){
				expenseReportFormValidate();
				$("#message").show("show");
				setTimeout('$("#message").hide("show");',3000);
				if(submitType=='submit'){
					window.parent.$.colorbox.close();
					window.close();
				}
				$( "#tabs" ).tabs();
				uploadDocument();
			}

			//选择陪同人员
			function selectPerson(){
				var acsSystemUrl = "${ctx}";
//				popTree({ title :'选择人员',
//					innerWidth:'400',
//					treeType:'MAN_DEPARTMENT_TREE',
//					defaultTreeValue:'id',
//					leafPage:'false',
//					multiple:'false',
//					hiddenInputId:"userId",
//					showInputId:"companion",
//					acsSystemUrl:acsSystemUrl,
//					callBack:function(){}});
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
//				popTree({ title :'选择一级审批人',
//					innerWidth:'400',
//					treeType:'MAN_DEPARTMENT_TREE',
//					defaultTreeValue:'id',
//					leafPage:'false',
//					multiple:'false',
//					hiddenInputId:"userId",
//					showInputId:"firstApprover",
//					acsSystemUrl:acsSystemUrl,
//					callBack:function(){firstCallBack();}});
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
			}
			
			//选择批示传阅人员
			function selectReadPerson(){
				var acsSystemUrl = "${ctx}";
//				popTree({ title :'选择批示传阅人员',
//					innerWidth:'400',
//					treeType:'MAN_DEPARTMENT_TREE',
//					defaultTreeValue:'id',
//					leafPage:'false',
//					multiple:'true',
//					hiddenInputId:"readPersonIds",
//					showInputId:"readPersons",
//					acsSystemUrl:acsSystemUrl,
//					callBack:function(){readPersonCallBack();}});
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
			}

			//选择会签人员
			function selectSignPerson(){
				var acsSystemUrl = "${ctx}";
//				popTree({ title :'选择会签人员',
//					innerWidth:'400',
//					treeType:'MAN_DEPARTMENT_TREE',
//					defaultTreeValue:'id',
//					leafPage:'false',
//					multiple:'true',
//					hiddenInputId:"signPersonIds",
//					showInputId:"signPersons",
//					acsSystemUrl:acsSystemUrl,
//					callBack:function(){signPersonCallBack();}});
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
							title: "选择会签人员",
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
			}

			function changeViewSet(opt){
				if(opt=="basic"){
					aa.submit("defaultForm1", "${ctx}/expense-report/urgenDonePage.htm", 'btnZone,viewZone',uploadDocument);
				}else if(opt=="history"){
					aa.submit("defaultForm1", "${ctx}/expense-report/history.htm", 'btnZone,viewZone');
				}
			}

			/*---------------------------------------------------------
			函数名称:rewriteMethod
			参          数:url
			功          能:所有文件上传之后调用
			------------------------------------------------------------*/
			function rewriteMethod(){
				ajaxSubmit("expenseReportForm", webRoot+"/expense-report/urgenDonePage.htm", "main",uploadBack);
			}
		</script>
	</head>
	
	<body>
	<div class="ui-layout-center" >
		<div class="opt-body" >
				<aa:zone name="main">
					<div class="opt-btn">
					<aa:zone name="btnZone">
						<s:if test="urgenFlag">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/urgen-save.htm','save')"><span><span>保存</span></span></button>
						</s:if>
					</aa:zone>
					</div>
					<form id="defaultForm1" name="defaultForm1"action="">
						<input type="hidden" name="id" id="id" value="${id }"  />
						<input name="taskId" id="taskId" value="${taskId }" type="hidden"/>
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
					<div id="opt-content" style="height: 650px;">
					<aa:zone name="viewZone">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="submitFlag" id="submitFlag" value="${submitFlag }"></input>
							<input name="urgenFlag" id="urgenFlag" value="${urgenFlag }" type="hidden"/>
							<input type="hidden" name="taskTransact" id="taskTransact" />
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
										<a class='btn' href="#" onclick="openDocument('${document.fileType }','${workflowInfo.workflowId}','${taskId}','${document.id }');"><span><span >在线查看</span></span></a>
									<br/>
									<s:if test="taskPermission.documentCreateable"><div id="spanButtonPlaceholder" style="margin-left: 65px;"></div></s:if>
									<s:else><div id="spanButtonPlaceholder" style="margin-left: 65px;" style="display: none;"></div></s:else>
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
							uploadDocument();
							getContentHeight();
						});
					</script>
			</div>
		</div>
	</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
<script type="text/javascript" src="${resourcesCtx}/js/search.js"></script>
</html>