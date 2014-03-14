<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>我的流程</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>

	<script src="${wfCtx}/js/opinion.js" type="text/javascript"></script>
</head>
<body>
<div class="ui-layout-center">
<form id="defaultForm" action="" name="defaultForm" method="post">
	<input id="wf_instanceState" type="hidden" name="end" />
</form>
<div class="opt-body">
	<aa:zone name="endProcess">
			  <view:jqGrid url="${wfCtx}/engine/delegate-main!myHistoryDelegate.htm?isEnd=${isEnd }" pageName="historyTasks" code="WORKFLOW_HISTORY_TASK_TRUSTOR" gridId="main_table"></view:jqGrid>
	</aa:zone>
</div>
</div>
</body>
</html>
