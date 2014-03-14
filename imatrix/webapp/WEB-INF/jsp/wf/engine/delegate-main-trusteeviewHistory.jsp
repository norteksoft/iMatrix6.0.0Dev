<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>我的流程</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>

	<script src="${wfCtx}/js/opinion.js" type="text/javascript"></script>
	
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${resourcesCtx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${resourcesCtx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
	
	<link href="${imatrixCtx}/widgets/workflow-swfupload/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/workflow-attachment-handlers.js"></script>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflow-swfupload/swfupload.js"></script>
	
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
	
	<script type="text/javascript" src="${wfCtx}/js/util.js"></script>
	<script language="javascript" type="text/javascript" src="${wfCtx}/js/delegate-main.js"></script>
	<script type="text/javascript">

	function viewTask(ts1,cellval,opts,rwdat,_act){
		var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"openViewTask('"+opts.id+"');\">" + ts1 + "</a>";
		return v;
	}
	
	</script>
</head>
<body>
<div class="ui-layout-center">
<div class="opt-body">
	<aa:zone name="myprocess">
		<div id="opt-content" >
			<view:jqGrid url="${wfCtx}/engine/delegate-main!superviseAsTrustee.htm?isDone=true&inHistory=true" pageName="historyTasks" code="WORKFLOW_HISTORY_TASK_TRUSTEE" gridId="main_table"></view:jqGrid>
		</div>
	</aa:zone>
	
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
