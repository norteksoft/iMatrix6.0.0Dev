<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		validateMessage();
	});
	function sendMessage(){
		$.ajax({
			type: "POST",
			url: "${ctx}/expense-report/validate-process.htm",
			success: function(data){
				if(data=="zero"){
					$("#message").html("<font class=\"onError\"><nobr>没有启用的报销流程</nobr></font>");
					showMsg("message");
				}else{
					$("#contentForm").submit();
				}
			}
		}); 
	}
	//表单验证
	function validateMessage(){
		$("#contentForm").validate({
			submitHandler: function() {
				$(".opt_btn").find("button.btn").attr("disabled","disabled");
				aa.submit('contentForm', '${ctx}/expense-report/send-message.htm', 'main',backWay); 
			},
			rules: {
				"recievePerson": {
					required:true
				},
				"sendType": {
					required:true
				},
				"sendContent": {
					required:true
				}
			},
			messages: {
				"recievePerson": {
					required:"必填"
				},
				"sendType": {
					required:"必填"
				},
				"sendContent": {
					required:"必填"
				}
			}
		});
	}

	function backWay(){
		$("#message").show("show");
		setTimeout('$("#message").hide("show");',3000);
	}

	//选择接收人
	function selectRecievePerson(){
		var acsSystemUrl = "${ctx}";
//		popTree({ title :'选择接收人',
//			innerWidth:'400',
//			treeType:'MAN_DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'false',
//			hiddenInputId:"recievePersonId",
//			showInputId:"recievePerson",
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
					title: "选择接收人",
					width: 300,
					height:400,
					url:acsSystemUrl,
					showBranch:false
				},
				feedback:{
					enable: true,
			                showInput:"recievePerson",
			                hiddenInput:"recievePersonId",
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
		$('#recieveLoginName').attr("value",ztree.getLoginName());
	}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="messageNotice";
	</script>
	<%@ include file="/menus/header.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp"%>
	</div>
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button class='btn' onclick="sendMessage();"><span><span>发送报销消息</span></span></button>
					</div>
					<div id="opt-content">
					<span id="message" style="display: none;"><font class="onSuccess"><nobr>发送成功！</nobr></font></span>
						<form id="contentForm" name="contentForm" method="post"  action="">
						<table>
							<tr>
								<td>发送人：</td>
								<td>
									<input id="sendPerson" name="sendPerson" value="${sendPerson }"></input>
									<input id="sendLoginName" name="sendLoginName" value="${sendLoginName }" type="hidden"></input>
								</td>
							</tr>
							<tr>
								<td>接收人：</td>
								<td>
									<input id="recievePerson" name="recievePerson"></input>
									<input id="recievePersonId" type="hidden"></input><input id="recieveLoginName" name="recieveLoginName" type="hidden"></input><button onclick="selectRecievePerson()" type="button">选择</button>
								</td>
							</tr>
							<tr>
								<td>类型：</td>
								<td><input name="sendType" value=""></input></td>
							</tr>
							<tr>
								<td>内容：</td>
								<td><input name="sendContent" value=""></input></td>
							</tr>
						</table>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>