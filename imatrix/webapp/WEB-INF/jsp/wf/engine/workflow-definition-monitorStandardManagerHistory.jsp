<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>流程监控</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	<script src="${wfCtx }/js/workflow-definition.js" type="text/javascript"></script>
	
	<script language="javascript" type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<script type="text/javascript">
	/**下拉按钮效果 ****/
	$(function() {//默认按钮效果 
		initBtnGroup();
	});
	</script>
</head>
	<body>
	<div class="ui-layout-center">
		<form id="defaultForm" action="" name="defaultForm" method="post">
			<input id="wf_type" name="type" type="hidden" value="${type }"/>
			<input id="wf_name" name="definitionCode" type="hidden" value="${definitionCode }"/>
			<input id="workflowId" name="workflowId" type="hidden"/>
		</form>
		<div class="opt-body">
		<aa:zone name="wf_definition">	
			<div class="opt-btn">
				<security:authorize ifAnyGranted="wf_engine_wf_definition_search">
				<button class='btn' onclick="iMatrix.showSearchDIV(this);" hidefocus="true"><span><span>查询</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="wf_urgen_done">
				<button class='btn' onclick="urgen_done_history();" hidefocus="true"><span><span>应急处理</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="wf_engine_wf_definition_deleteWorkflow">	
				<button class='btn' onclick="delete_monitor_workflow_history('standardManager');" hidefocus="true"><span><span>删除</span></span></button>
				</security:authorize>
			</div>
									
								
			<aa:zone name="monitorList">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
											
			<form id="wf_form" name="wf_form" method="post" action="">
				<input id="wf_type1" name="type" type="hidden" value="${type }"/>
				<input id="wf_name1" name="definitionCode" type="hidden" value="${definitionCode }"/>
				<input name="position" id="position" type="hidden"/>
			</form>
			
			<div id="opt-content" >
				<form id="searchSubmit" name="searchSubmit" action="" method="post">
					<view:jqGrid url="${wfCtx}/engine/workflow-definition!monitorDefintionHistory.htm?type=${type}&definitionCode=${definitionCode}" pageName="wiPage" code="WF_HISTORY_INSTANCE" gridId="main_table"></view:jqGrid>
				</form>	
			</div>
			</aa:zone>
	</aa:zone>
	</div>
	</div>							
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
