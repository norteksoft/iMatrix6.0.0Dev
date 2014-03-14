<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>流程管理</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>
	
	<script src="${resourcesCtx}/js/public.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<script type="text/javascript">
	function okBtn(){
		ztree.selectTreeValue(function(){
			var arr=ztree.getIds().split(",");
			if(arr!=""){
				$("#transactorForm").html("");
				$("#transactorForm").append('<input type="hidden" name="workflowId" value="${workflowId}"/>');
				$("#transactorForm").append('<input type="hidden" name="backto" value="${backto }"/>');
				for(var i=0;i<arr.length;i++){
					$("#transactorForm").append('<input type="hidden" name="transactors" value="'+arr[i]+'"/>');
				}
				ajaxSubmit('transactorForm','${wfCtx }/engine/task!goback.htm','wf_task',postSuccess);
			}else{
				window.parent.$.colorbox.close();
			}
		});
	}
	function postSuccess(){
		window.parent.backViewClose('${wfdId}','${position}','${type}','${definitionCode}');
		window.parent.$.colorbox.close();
	}
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="wf_task">
			<div class="opt-btn">
				<button id="wf_ok" class='btn' onclick="okBtn();" hidefocus="true"><span><span>确定</span></span></button>
			</div>
			<div id="opt-content">
				<div style="margin-top: 10px;">
					<ztree:ztree
							treeType="MAN_DEPARTMENT_TREE"
							chkboxType="{'Y' : 'ps', 'N' : 'ps'}" 
							chkStyle="checkbox"
							treeId="treeDemo" 
							>
					</ztree:ztree>
				</div>
			</div>
			<form id="transactorForm" action="post" name="transactorForm">
			</form>
		</aa:zone>
	</div>
</div>
</body>
</html>