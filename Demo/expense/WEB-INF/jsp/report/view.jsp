<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	<!--上传js-->
	<script type="text/javascript" src="${ctx}/widgets/swfupload/swfupload.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/swfupload/handlers.js"></script>
	<script type="text/javascript" src="${ctx}/js/loan-bill.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/widgets/workflowEditor/swfobject.js"></script>
		<script type="text/javascript">
		isUsingComonLayout=false;
			$().ready(function() {
				$( "#tabs" ).tabs();
			});

			function changeViewSet(opt){
				if(opt=="basic"){
					aa.submit("defaultForm1", "${ctx}/report/input.htm", 'viewZone');
				}else if(opt=="history"){
					aa.submit("defaultForm1", "${ctx}/report/history.htm", 'viewZone',callback);
				}
			}
			function callback(){
				if($(window).height()<=500){
					$("#flashcontent").css("height",450);
			    }
				
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div style="display: none;" class="onSuccess" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
					<div id="tabs">
					<form id="defaultForm1" name="defaultForm1"action="">
						<input type="hidden" name="id" id="id" value="${id }"  />
						<input type="hidden" name="taskId" id="taskId" value="${taskId }" />
					</form>
					<ul>
						<li><a href="#tabs-1" onclick="changeViewSet('basic');">表单信息</a></li>
						<li><a href="#tabs-1" onclick="changeViewSet('history');">流转历史</a></li>
					</ul>
					<div id="tabs-1">
					<aa:zone name="viewZone">
					<span>注：只有未提交流程才可编辑！</span><br></br>
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<table>
								<tr>
									<td>报销人</td>
									<td>${name }</td>
								</tr>
								<tr>
									<td>部门</td>
									<td><input id="department" name="department"  readonly="readonly" value="${department }"></input></td>
								</tr>
								<tr>
									<td>金额</td>
									<td><input id="money" name="money" value="${money }"  readonly="readonly"/> </td>
								</tr>
								<tr>
									<td>发票金额</td>
									<td><input id="invoiceAmount" name="invoiceAmount" value="${invoiceAmount }"  readonly="readonly"/></td>
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
		</div>
	</div>
	</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
</html>