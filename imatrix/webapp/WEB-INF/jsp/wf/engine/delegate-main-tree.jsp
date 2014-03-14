<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>委托管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/_lib/css.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/tree/tree_component.css" />
	<script type="text/javascript" src="${resourcesCtx}/widgets/tree/tree_component.js"></script>
	<link href="${wfCtx}/css/style.css" rel="stylesheet" type="text/css"/>
	
	<script type="text/javascript">
	

	function selectMan(id){
		ztree.selectTreeValue(function(){
			var id=ztree.getId();
			if(id&&id!=""){
				window.parent.insertInputValue(ztree.getLoginName(),ztree.getName(),id);
				window.parent.$("#selectBtn").colorbox.close();
			}
		});
	}
	</script>
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button id="wf_ok" class="btn" onclick="selectMan();"><span><span >确定</span></span></button>
			</div>
			<div id="opt-content">
				<div id="treeContect" style="text-align: left;overflow: autow;height: 500px">
					<ztree:ztree
						treeType="MAN_DEPARTMENT_TREE"
						treeId="aTree"
						showBranch="true"
						>
					</ztree:ztree>
				</div>
			</div>
		</div>
	</div>
</body>
</html>