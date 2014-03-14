<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/task-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="${taskCtx}/task/history-workflow-task.htm?taskCategory=complete" id="historyTask-thirdMenu1">已完成事宜</a></h3>
	<div>
		<ul class="ztree" id="completed_tree_id"></ul>
	</div>
	
	<h3><a href="${taskCtx}/task/history-workflow-task-canceled.htm?taskCategory=cancel" id="historyTask-thirdMenu2">已取消事宜</a></h3>
	<div>
		<ul class="ztree" id="canceled_tree_id"></ul>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		var url=window.location.href;
		if(url.indexOf("/task/history-workflow-task.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 0});
		}else if(url.indexOf("/task/history-workflow-task-canceled.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 1});
		}
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		window.location = url;
	}
</script>
