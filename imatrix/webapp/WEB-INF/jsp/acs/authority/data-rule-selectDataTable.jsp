<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
		$(document).ready(function(){
			create_tree();
		});
		function create_tree(){
			$.ajaxSetup({cache:false});
			tree.initTree({treeId:"treeDiv",
				url: webRoot+"/authority/data-rule-selectDataTable.htm",
				type:"ztree",
				initiallySelect:"",
				callback:{
						//onClick:select
				}});
		}

		function select(){
			var data = tree.getSelectNodeId();
			var node=tree.getSelectNode();
			if(typeof(data)!='undefined'&& node!=""&&node.pId!=null){
				var values = data.split("=");
				if("${dataTableId}"!=""&&"${listViewId}"!=""){//快速授权中
					if("${dataTableId}"!=values[0]){
						alert("请修改该快速授权对应的列表");
						window.parent.$("#listViewName").attr("value","");
						window.parent.$("#listViewId").attr("value","");
					}
				}

				if("${dataTableId}"==""&&"${listViewId}"==""){//数据分类中
					if("${messageTip}"!=""){
						alert("${messageTip}");
					}
				}
				window.parent.$("#dataTableId").attr("value",values[0]);
				window.parent.$("#dataTableName").attr("value",values[1]);
				window.parent.$('label[for="dataTableName"]').hide();
				window.parent.$.colorbox.close();
			}else{
				alert("请选择数据表！");
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="select();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<ul id="treeDiv" class="ztree"></ul>
		</div>
	</aa:zone>
</div>
</div>
</body>
</html>
