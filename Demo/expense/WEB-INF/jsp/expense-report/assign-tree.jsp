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
			ztree.selectTreeValue(function(){
				submit_Ok(url);
			});
		}
		function submit_Ok(url){
			var userid=ztree.getId();
			$("#assignee").attr("value", userid);
			$("#expenseAssignForm").attr("action",url);
			$("#expenseAssignForm").ajaxSubmit(function (id){
				window.parent.beforeCloseWindow();
				window.parent.$.colorbox.close();
				window.parent.parent.close();
				window.parent.location.reload(true);
			});
			
		}
		</script>
	</head>
	
	<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
							<button class='btn' onclick="submitForm('${ctx}/expense-report/assign.htm')"><span><span>提交</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseAssignForm" name="expenseAssignForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"></input>
							<input type="hidden" name="taskId" id="taskId" value="${taskId }"></input>
							<input type="hidden"  name="assignee" id="assignee" ></input>
							<ztree:ztree
										treeType="MAN_DEPARTMENT_TREE"
										treeId="companyTree" 
										userWithoutDeptVisible="false"  
										>
							</ztree:ztree>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>