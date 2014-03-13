<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
		<script type="text/javascript">
		function submitForm(url){
			$("#expenseLeaderForm").attr("action",url);
				$("#expenseLeaderForm").ajaxSubmit(function (id){
					var ids=id.split(";");
					$("#id").attr("value",ids[0]);
					if(ids[1]!=undefined&&ids[1]!=null&&ids[1]!=''){
							if(ids[1].indexOf('{')!=-1){
								$.colorbox({href:webRoot+"/expense-report/select-tache.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+ids[0]+"&taches="+ids[1],iframe:true, innerWidth:500, innerHeight:330,overlayClose:false,title:"请选择"});
							}else{
								window.parent.$.colorbox.close();
								window.parent.parent.close();
								window.parent.parent.location.reload(true);
							}
					}else{
						window.parent.$.colorbox.close();
						window.parent.parent.close();
						window.parent.parent.location.reload(true);
					}
					
				});
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
							<input type="hidden" name="leaderName" id="leaderName" value="${leaderName }"></input>
							<input type="hidden"  name="opinionflag" id="opinionflag" value="${opinionflag }"></input>
							<input type="hidden"  name="submitflag" id="submitflag" value="${submitflag }"></input>
							<input id="selecttacheFlag" type="hidden" value="false"/>
							<table class="form-table-without-border" >
								<tr>
									<td>意见：</td>
								</tr>
								<tr>
									<td>
										<textarea name="opinion" id="opinion" rows="8" cols="50" style="width: 600px;overflow: auto;" onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);"></textarea>
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