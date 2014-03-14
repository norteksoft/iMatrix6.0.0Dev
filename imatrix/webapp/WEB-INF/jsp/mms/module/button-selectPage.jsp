<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script src="${mmsCtx}/js/module-page.js" type="text/javascript" charset="UTF-8"></script>		
	<script>
		var id="";
		$(document).ready(function() {
			viewTree();
		});
		
		function viewTree(){
			$.ajaxSetup({cache:false});
			tree.initTree({treeId:"viewTree",
				url:getTreeUrl,
				type:"ztree",
				callback:{
						onClick:selectNode
					}});
		}
		function selectNode(){
			id = tree.getSelectNodeId();
		}

		function getTreeUrl(treeId, treeNode) {
			var actionUrl = "${mmsCtx}/module/button!pageTree.htm?menuId=${menuId}&pageId=${pageId}";
			var param = "";
			if(typeof(treeNode)!="undefined"&&treeNode!=null){
				param = "currentId="+treeNode.id;
	        }else{
	        	var param = "currentId=0";
	        }
			return actionUrl+"&" + param;
		}
		function selectView(){
			if(id!=""){
				var arr=id.split(";");//0:id;1:code;2:type
				if(arr[2]=="view"){
					window.parent.addViewValues(arr[0],arr[1],'${rowid}');
					window.parent.$("input[id='${rowid}']").focus();
					window.parent.$.colorbox.close();
				}else{
					alert("不能选择菜单,请选择页面");
				}
			}else{
				alert("请选择页面");
			}
		}
	</script>
								
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div  class="opt-body" >
		<div class="opt-btn">
				<button class="btn" onclick="selectView();"><span><span >确定</span></span></button>
		</div>
		<div  id="opt-content">
			<ul id="viewTree" class="ztree"></ul>
		</div>
	</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
