<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
		<script type="text/javascript">
		//提交
		function submitForm(url){
			var loginNames=$("input[name='userIdInput']:checked");
			if(loginNames.length==0){
				alert("请选择人员！");
				return;
			}
			$("#expenseLeaderForm").attr("action",url);
			var cutPersons = "";
			for(i=0;i<loginNames.length;i++){
				cutPersons+= $(loginNames[i]).attr("value")+",";
			}
			$("#cutPersons").attr("value",cutPersons.substring(0,cutPersons.length-1));
			$("#expenseLeaderForm").ajaxSubmit(function (msg){
				alert(msg);
				window.parent.$.colorbox.close();
				window.parent.location.reload(true);
			});
				
		}

		//全选
		function selectAll(){
			var a = $("[name='userIdInput']");
			for(i=0;i<a.length;i++){
				$(a[i]).attr("checked",true);
			}
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/cut.htm')"><span><span>提交</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseLeaderForm" name="expenseLeaderForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"/>
							<input type="hidden" name="cutPersons" id="cutPersons"></input>
							<table class="form-table-border-left" style="width: 250px;">
								<tr>
									<td><input type="checkbox" onclick="selectAll();" /></td>
									<td>人员名称</td>
								</tr>
								<s:iterator value="cutsignList" >
									<s:if test="userId!=transactorId">
										<tr>
											<td><input name="userIdInput" type="checkbox" value="${transactorId}"/></td>
											<s:if test="trustorName==null">
											    <td>${transactorName }</td>
											</s:if>
											<s:else>
											    <td>${transactorName }(受托于${trustorName })</td>
											</s:else>
										</tr>
									</s:if>
								</s:iterator>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>