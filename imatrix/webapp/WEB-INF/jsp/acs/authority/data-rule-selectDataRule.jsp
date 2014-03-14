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
			$("#treeDiv").bind("select_node.jstree",function(e){
				//selectNode($("#"+treeId).find(".jstree-clicked").parent().attr("id"),treeId);
			}).jstree({
				"json_data":{
						"ajax" : { "url" : webRoot+"/authority/data-rule-selectDataRule.htm",
							"data" : function (n) {  
								return { currentMenuId : n!=-1 ? n.attr("id") : 0 };   
							}
						}
			   },
			   "themes" : {  
					  "theme" : "default",  
					  "dots" : true,  
					  "icons" : true 
					 },
					 "ui":{ "select_multiple_modifier" : "alt"},
					 "plugins" : [ "themes", "json_data" ,"ui"]
				});
		}

		function select(){
			var data = $("#treeDiv").find(".jstree-clicked").parent().attr("id");
			if(typeof(data)!='undefined'&& data!="menu"){
				var values = data.split("_");
				window.parent.$("#_dataRuleId").attr("value",values[0]);
				window.parent.$("#data_Rule").attr("value",values[1]);
				window.parent.$.colorbox.close();
			}else{
				alert("请选择数据分类！");
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
			<div id="treeDiv" class="demo"></div>
		</div>
	</aa:zone>
</div>
</div>
</body>
</html>
