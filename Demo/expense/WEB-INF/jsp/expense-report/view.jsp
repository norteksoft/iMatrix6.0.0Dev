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
					aa.submit("defaultForm1", "${ctx}/expense-report/input.htm", 'viewZone');
				}else if(opt=="history"){
					aa.submit("defaultForm1", "${ctx}/expense-report/history.htm", 'viewZone',callback);
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
									<td>报销日期</td>
									<td><s:date name="createdTime" format="yyyy-MM-dd"/></td>
								</tr>
								<tr>
									<td>金额</td>
									<td><input id="money" name="money" value="${money }"  readonly="readonly"/> </td>
								</tr>
								<tr>
									<td>发票张数</td>
									<td><input id="invoiceAmount" name="invoiceAmount" value="${invoiceAmount }"  readonly="readonly"/></td>
								</tr>
								<tr>
									<td>出差日期</td>
									<td>
										<input id="outDate" name="outDate" value="<s:date name="outDate" format="yyyy-MM-dd"/>" readonly="readonly" />
									</td>
								</tr>
								<tr>	
								<td>
									文件：
								</td>
								<td>
									<input id="documentName" readonly="readonly" value="${document.fileName}"/>
								</td>
								</tr>
								<tr>
									<td>陪同人</td>
									<td>
										<input id="companion" name="companion" value="${companion }" readonly="readonly" />
									</td>
								</tr>
								<tr>
									<td>一级审批人</td>
									<td>
										<input id="firstApprover" name="firstApprover" value="${firstApprover }" readonly="readonly"/>
									</td>
								</tr>
								<tr>
									<td>二级审批人</td>
									<td><input id="secondApprover" name="secondApprover" value="${secondApprover }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>三级审批人</td>
									<td><input id="thirdApprover" name="thirdApprover" value="${thirdApprover }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>财务</td>
									<td><input id="cashier" name="cashier" value="${cashier }" readonly="readonly"/></td>
								</tr>
								<tr>
									<td>会签人员：</td>
									<td>
										<input style="width: 300px;" id="signPersons" name="signPersons" value="${signPersons }" readonly="readonly"/>
									</td>
								</tr>
								<tr>
									<td>批示传阅人员：</td>
									<td>
										<input style="width: 300px;" id="readPersons" name="readPersons" value="${readPersons }" readonly="readonly"/>
									</td>
								</tr>
								<tr>
									<td>事由</td>
									<td><textarea cols="10" rows="4" id="reason" name="reason" readonly="readonly">${reason }</textarea></td>
								</tr>
								<!--  
								<tr>
									<td>字段1</td>
									<td><input id="field1" name="field1" value="${field1 }"/></td>
								</tr>
								<tr>
									<td>字段2</td>
									<td><input id="field2" name="field2" value="${field2 }"/></td>
								</tr>
								<tr>
									<td>字段3</td>
									<td><input id="field3" name="field3" value="${field3 }"/></td>
								</tr>
								<tr>
									<td>字段4</td>
									<td><input id="field4" name="field4" value="${field4 }"/></td>
								</tr>
								<tr>
									<td>字段5</td>
									<td><input id="field5" name="field5" value="${field5 }"/></td>
								</tr>
								<tr>
									<td>字段6</td>
									<td><input id="field6" name="field6" value="${field6 }"/></td>
								</tr>
								<tr>
									<td>字段7</td>
									<td><input id="field7" name="field7" value="${field7 }"/></td>
								</tr>
								<tr>
									<td>字段8</td>
									<td><input id="field8" name="field8" value="${field8 }"/></td>
								</tr>
								<tr>
									<td>字段9</td>
									<td><input id="field9" name="field9" value="${field9 }"/></td>
								</tr>
								<tr>
									<td>字段10</td>
									<td><input id="field10" name="field10" value="${field10 }"/></td>
								</tr>
								<tr>
									<td>字段11</td>
									<td><input id="field11" name="field11" value="${field11 }"/></td>
								</tr>
								<tr>
									<td>字段12</td>
									<td><input id="field12" name="field12" value="${field12 }"/></td>
								</tr>
								<tr>
									<td>字段13</td>
									<td><input id="field13" name="field13" value="${field13 }"/></td>
								</tr>
								<tr>
									<td>字段14</td>
									<td><input id="field14" name="field14" value="${field14 }"/></td>
								</tr>
								<tr>
									<td>字段15</td>
									<td><input id="field15" name="field15" value="${field15 }"/></td>
								</tr>
								<tr>
									<td>字段16</td>
									<td><input id="field16" name="field16" value="${field16 }"/></td>
								</tr>
								-->
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
</html>