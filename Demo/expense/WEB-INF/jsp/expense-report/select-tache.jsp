<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
		<script type="text/javascript">
			function submitForm(url){
				getTache();
				var code = $("#tacheCode").attr("value");
				if(code==""||code==null){
					alert("请选择下一环节！");
				}else{
					$("#expenseLeaderForm").attr("action",url);
					$("#expenseLeaderForm").ajaxSubmit(function (id){
						if(window.parent.$("#selecttacheFlag").attr("value")=='true'){
							window.parent.$.colorbox.close();
							window.parent.parent.close();
							window.parent.parent.location.reload(true);
						}else{
							window.parent.$.colorbox.close();
							window.parent.parent.$.colorbox.close();
							window.parent.parent.parent.close();
							window.parent.parent.location.reload(true);
						};

					});
				}
			}
	
			function getTache(){
				var rds = $("input[name='selectTacheCode']");
				var tacheCode = "";
				for(var i = 0; i < rds.length; i++){
					if($(rds[i]).attr("checked")){
						$("#tacheCode").attr("value",$(rds[i]).attr("value"));
					}
				}
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<s:if test="task==null">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/expense-distributeTask.htm')"><span><span>提交</span></span></button>
						</s:if>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseLeaderForm" name="expenseLeaderForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"></input>
							<input type="hidden" name="tacheCode" id="tacheCode" value="${tacheCode }"></input>
							<input type="hidden"  name="opinionflag" id="opinionflag" value="${opinionflag }"></input>
							<input type="hidden"  name="submitflag" id="submitflag" value="true"></input>
							<table class="form-table-border-left" style="width: 500px;">
								<tr>
									<td>选择下一环节</td>
								</tr>
								<s:iterator value="tacheList" var="tache">
									<tr>
										<td>
											<input type="radio" name="selectTacheCode" value="${tache[0]}" />${tache[1]}	 
										</td>
									</tr>
								</s:iterator>
							</table>
							<table class="form-table-without-border" >
								<tr>
									<td>意见：</td>
								</tr>
								<tr>
									<td>
										<textarea  name="opinion" id="opinion" rows="8" cols="50" style="width: 600px;overflow: auto;" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);"></textarea>
									</td>
								</tr>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>