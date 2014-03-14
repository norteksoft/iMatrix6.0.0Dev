<ul class="ztree" id="taskType"></ul>
<script type="text/javascript">
//初始化树
$(document).ready(function (){
	loadTree();
});
//树脚本
function loadTree(){     
	$.ajaxSetup({cache:false});
	tree.initTree({treeId:"taskType",
		url:"${taskCtx}/task/task-type-tree-portal.htm?companyId=${companyId}&userId=${userId}",
		type:"ztree",
		callback:{
				onClick:selectNode
			}});
}

function selectNode(){
	var node=tree.getSelectNode();
	var nodeId=node.id;
	var taskType="";
	if(node.getParentNode()!=null)taskType=node.getParentNode().id;
	if(nodeId=="complete_task"||nodeId=="active_task"||nodeId=="cancel_task")nodeId="";
	location.href=encodeURI("${taskCtx}/task/task.htm?typeName="+nodeId+"&completed=false&currentNodeId="+nodeId+"&taskType="+taskType);
}
</script>