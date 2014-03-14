<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>流转历史</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${imatrixCtx}/widgets/workflowEditor/swfobject.js"></script>
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div id="opt-content">
				<aa:zone name="button_zone"></aa:zone>
				<aa:zone name="content_zone">
					<wf:history companyId="${companyId}"
						url="${imatrixCtx}/widgets/workflowEditor/FlowChartProject.swf" 
						taskId="${taskId}"></wf:history>
						
						<script>
							//显示子流程流转历史
							function showSubWorkflowHistory(companyId,taskId){
								init_colorbox(webRoot+'/common/history.htm?companyId='+companyId+'&taskId='+taskId,"流转历史");
							}
						</script>
				</aa:zone>
			</div>
		</div>
	</div>
</body>
</html>
