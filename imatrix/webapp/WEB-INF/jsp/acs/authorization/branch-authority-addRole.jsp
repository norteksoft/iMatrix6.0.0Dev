<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>添加角色</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
		$(document).ready(function(){
			create_tree();
		});
		function create_tree(){
			$.ajaxSetup({cache:false});
			$.ajaxSetup({cache:false});
			tree.initTree({treeId:"treeDiv",
				url:webRoot+"/authorization/branch-authority-addRole.action",
				type:"ztree",
				multiple:true,
				initiallySelect:"",
				callback:{
						//onClick:select
			}});
		}	
		function select(){
			var branchesId=$("#branchesId").val();
			var zTree = $.fn.zTree.getZTreeObj("treeDiv");
			var datas = zTree.getCheckedNodes(true);
			var roleIds="";
			for(var i=0; i<datas.length; i++){
				if(datas[i].pId!=null){
					if(roleIds != "")roleIds+=",";
					var roleId=datas[i].id.split("_");
					roleIds+=roleId[1];
				}
			}
			if(roleIds==""){
				alert("请选择角色！");
			}else{
				$.ajax({
					data:{roleIds:roleIds,branchesId:branchesId},
					type:"post",
					url:webRoot+"/authorization/branch-authority-saveRole.action",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						if("ok"==data){
							window.parent.$.colorbox.close();
							window.parent.ajaxSubmit('defaultForm', webRoot+'/authorization/branch-authority.action', 'acs_content');
						}
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
				
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
	<input id="branchesId" type="hidden" value="${branchesId }"/>
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
