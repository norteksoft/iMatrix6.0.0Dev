<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	  <title><s:text name="company.companyManager"/></title>
	   <%@ include file="/common/acs-iframe-meta.jsp"%>
	   <script type="text/javascript">
	   $(function createMenuTree(){
			var setting = {
					async : {
						enable: true,
						url: "${acsCtx}/authorization/function-group!loadFunctionTree.action",
						otherParam: { roleId:$('#_roleId').attr('value')
							, isAddOrRomove:$('#_isAddOrRomove').attr('value')}
					},
					check: {
						autoCheckTrigger : true,
						chkboxType : {"Y": "s", "N": "s"},
						chkStyle : "checkbox",
						enable : true,
						nocheckInherit: false
					},
					data: {
						simpleData: {
							enable: true
						}
					},
					callback : {
						//onClick:onClick
						//onDrop:onDrop,
						//beforeDrop:beforeDrop,
						//onAsyncSuccess:onAsyncSuccess
					}
				};
			$.fn.zTree.init($("#menu-tree"), setting);
		});
	   function selecChange(){
			var node = $(".jstree-clicked").parent().attr("id");
			var nodeClass = $("#"+node).attr("calss");
		}
		function selectOk(treeId){
			var treeObj = $.fn.zTree.getZTreeObj("menu-tree");
			var nodes = treeObj.getCheckedNodes(true);
		  	if(nodes.length<=0){
			  alert("请选择资源！");
		       return;
			}
		  	$("#fids").attr("value","");
		    var ids="";
			for(var i=0;i<nodes.length;i++){
				ids=ids+nodes[i].id+(i==nodes.length-1?"":",");
			}
			$("#fids").attr("value",ids);
			ajax_new('functionForm','functionForm','${acsCtx}/authorization/role!roleAddFunction.action','', _calback);
		}
		function _calback(){
			msg = '角色添加资源成功';
			if($('#_isAddOrRomove').attr('value')==1){
				msg = '角色移除资源成功';
			}
			parent.showMessage("role_msg",msg);
			parent.$.colorbox.close();
		}
		function ajax_new(formName, fromId, url, zone, callback){
			$("#"+fromId).attr("action", url);
			$("#"+fromId).ajaxSubmit(callback);
		}
	   </script>
</head>
<body onload="getContentHeight();" style="padding: 15px;">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn" style="margin-bottom: 5px;">
		<button id='submitBtn' class='btn' onclick="selectOk();"><span><span>提交</span></span></button>
	</div>
	<div id="opt-content">
		<form id="functionForm" name="functionForm" action="" method="post">
			<input type="hidden" id="_isAddOrRomove" name="isAddOrRomove" value="${isAddOrRomove}"/>
			<input type="hidden" id="_roleId" name="roleId" value="${roleId}"/>
			<input type="hidden" id="fids" name="fids" value="" />
		</form>
		<div id="menu-tree" class="ztree"></div>
	</div>
	</div>
</div>
</body>
</html>
