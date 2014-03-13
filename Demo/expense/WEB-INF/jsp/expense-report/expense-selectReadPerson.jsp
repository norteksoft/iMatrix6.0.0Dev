<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>选择加签人</title>
		<%@include file="/common/iframe-meta.jsp" %>	
	
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
		<script type="text/javascript">
		//提交
		function submitForm(url){
			var names= $("input[name='userid']:checked");
			var userIds="";
			if(names.length>1){
				for(i=0;i<names.length;i++){
					userIds+=$(names[i]).attr("value")+"_";
				}
				userIds=userIds.substring(0,userIds.length-1);
			}
			if(userIds!=""){
				$("#userIds").attr("value",userIds);
			}
			$("#expenseLeaderForm").attr("action",url);
			$("#expenseLeaderForm").ajaxSubmit(function (id){
				window.parent.$.colorbox.close();
				window.parent.parent.close();
				window.parent.parent.location.reload(true);
			});
		}
		//全选
		function _select_all(boxId,inputName){
			if($("#"+boxId).attr("checked")){
				$("input[name='"+inputName+"']").attr("checked","checked");
			}else{
				$("input[name='"+inputName+"']").removeAttr("checked");
			}
		}
		function _click_one(boxId,inputName){
			var ones = $("input[name='"+inputName+"']");
			var allChecked = true;
			for(var i=0;i<ones.length;i++){
              if($(ones[i]).attr("checked")==false){
                  allChecked=false;
                  break;
              }
			}
			if(allChecked){
				$("#"+boxId).attr("checked","checked");
			}else{
				$("#"+boxId).attr("checked","");
			}
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/expense-distributeTask.htm')"><span><span>提交</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseLeaderForm" name="expenseLeaderForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"></input>
							<input type="hidden"  name="opinionflag" id="opinionflag" value="${opinionflag }"></input>
							<input type="hidden"  name="submitflag" id="submitflag" value="${submitflag }"></input>
							<input type="hidden"  name="userIds" id="userIds" ></input>
							<table>
								<tr>
									<td><input id="_all" type="checkbox" onclick="_select_all('_all','userId');"></input></td>
									<td>人员名称</td>
								</tr>
								<s:iterator value="userList" var="bean" status="index">
								<s:if test="userId!=id">
								<tr>
									<td><input name="userid" type="checkbox" value="${id }" onclick="_click_one('_all','userid');"></input></td>
									<td>${name }</td>
								</tr>
								</s:if>
								</s:iterator>
							</table>
							<table>
								<tr>
									<td>意见：</td>
								</tr>
								<tr>
									<td>
										<textarea name="opinion" id="opinion" rows="5" cols="40" style="width: 400px;"></textarea>
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