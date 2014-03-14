<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>归档实例</title>
<%@ include file="/common/wf-iframe-meta.jsp"%>
<script src="${wfCtx }/js/util.js" type="text/javascript"></script>
</head>
<body>
<div class="ui-layout-center">
	<aa:zone name="monitorButton">
		<div class="opt-btn">
			<security:authorize ifAnyGranted="wf_engine_wf_definition_search">
				<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_urgen_done">
				<button class='btn' onclick="urgen_done_history();" hidefocus="true"><span><span>应急处理</span></span></button>
			</security:authorize>
			<security:authorize ifAnyGranted="wf_engine_wf_definition_deleteWorkflow">	
				<button  class='btn' onclick="delete_monitor_workflow_history();" hidefocus="true"><span><span>删除</span></span></button>
			</security:authorize>
			<button  class='btn' onclick="goBack('backForm','${wfCtx }/engine/workflow-definition-data.htm','wfd_main','wfdPage');" hidefocus="true"><span><span>返回</span></span></button>
		</div>
	</aa:zone>
	<aa:zone name="monitorList">
	<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
	<form id="wf_form" name="wf_form" method="post">
		<input id="type" type="hidden" name="type" value="${type}" />
		<input id="system_id" name="sysId" type="hidden" value="${sysId}"/>
		<input id="vertion_type" name="vertionType" type="hidden" value="${vertionType}"/>
		<input name="position" id="position" type="hidden"/>
		<view:jqGrid url="${wfCtx}/engine/workflow-definition!monitorHistory.htm?wfdId=${wfdId}" pageName="wiPage" code="WF_HISTORY_INSTANCE" gridId="main_table"></view:jqGrid>
	</form>
		
	</aa:zone>
</div>
</body>
</html>
