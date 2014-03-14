<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/task-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<%@ include file="/common/task-meta.jsp"%>
	
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css"><link>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.excheck-3.5.js"></script>
	<script src="${resourcesCtx}/js/z-tree.js" type="text/javascript"></script>	
	<script type="text/javascript">
	function selectMan(){
		ztree.selectTreeValue(function(){
			assignTo();
		});
	}
	//指派
	function assignTo(){
		var userid=ztree.getId();
		if(userid!=""){
			$("#wf_transactor").attr("value", userid);
		}else{
			alert("请选择人员");
		}
		var tor = $("#wf_transactor").attr("value");
		if(typeof tor == "undefined" || tor == ""){
			showMsg("请选择用户");
		}else{
			$.post(webRoot + "/task/task!assignTo.htm", 
					"ids=" + window.parent.getTaskIds() + "&transactor=" + tor, 
					postSuccess);
		}
	}


	function postSuccess(msg){
		$("#wf_ok").hide();
		$('#taskbutton',window.parent.document).hide();
		window.parent.$.colorbox.close();
		window.parent.taskListAssgin();
	}
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div style="padding:5px 0px 0px 5px; ">
			<div class="opt-btn">
				<button class="btn" onclick="assignTo();" id="wf_ok"><span><span>确定</span></span></button>
			</div>
			<div id="opt-content">
				<input id="wf_transactor" type="hidden" name="transactor">
				
				<ztree:ztree
							treeType="MAN_DEPARTMENT_TREE"
							treeId="user_tree" 
							userWithoutDeptVisible="false"  
							>
				</ztree:ztree>
			</div>
		</div>
	</div>
</div>
</body>
</html>