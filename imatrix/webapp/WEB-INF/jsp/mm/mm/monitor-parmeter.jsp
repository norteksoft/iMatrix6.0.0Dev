<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/mm-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html >
<head>
	<title>性能监控参数设置</title>
	<%@ include file="/common/mm-meta.jsp"%>
</head>
<body>
	<script type="text/javascript">
	var secMenu="mm_monitor_parmeter";
	var thirdMenu="_mm_parmeter";
	</script>
	
	<%@ include file="/menus/header.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/mm-thd-menu.jsp"%>
	</div>
	<form action="" id="defaultForm" name="defaultForm" method="post"></form>
	<div class="ui-layout-center">
	<aa:zone name="form_main">
			<div class="opt-body">
				<div class="opt-btn">
					<a id="btnSearch"  href="#" class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></a>
					<a   href="#" class='btn' onclick="ajaxAnyWhereSubmit('defaultForm','${mmCtx}/mm/monitor-parmeter-input.htm','form_main');"><span><span>增加</span></span></a>
				</div>
				<div id="opt-content" >
					<div id="message"><s:actionmessage theme="mytheme" /></div>
					<script type="text/javascript">setTimeout('$("#message").hide("show");',3000);</script>
					<form id="contentForm" name="contentForm"  method="post"  action="">
						<grid:jqGrid gridId="monitorParmeterList" url="${mmCtx}/mm/monitor-parmeter-getList.htm" code="MONITOR_PARMETER" pageName="pages"></grid:jqGrid>
					</form>
				</div>
			</div>
	</aa:zone>
	</div>
</body>
</html>