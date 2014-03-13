<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>测试同步组织结构</title>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript">
		function addUser(){
			var url=webRoot+"/test/toAddUserPage.htm";
			$.colorbox({href:encodeURI(url),iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"添加用户",onClosed:function(){
			}});
		}
		function delUser(){
			var url=webRoot+"/test/toDelUserPage.htm";
			$.colorbox({href:encodeURI(url),iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"删除用户",onClosed:function(){
			}});
		}
		function addDept(){
			var url=webRoot+"/test/toAddDeptPage.htm";
			$.colorbox({href:encodeURI(url),iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"添加部门",onClosed:function(){
			}});
		}
		function delDept(){
			var url=webRoot+"/test/toDelDeptPage.htm";
			$.colorbox({href:encodeURI(url),iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"删除部门",onClosed:function(){
			}});
		}
		function refreshTree(){
			currentClickNode=null;
			var treeObj = $.fn.zTree.getZTreeObj("DepartmentTree");
			treeObj.reAsyncChildNodes(null, "refresh");
		}
	</script>
</head>

<body>
	<%@ include file="/menus/header.jsp" %>
	<div class="ui-layout-west">
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="addUser()"><span><span>添加用户</span></span></button>
						<button class='btn' onclick="delUser()"><span><span>删除用户</span></span></button>
						<button class='btn' onclick="addDept()"><span><span>添加部门</span></span></button>
						<button class='btn' onclick="delDept()" ><span><span >删除部门</span></span></button>
					</div>
					<div id="opt-content">
					<table>
						<tr>
							<td>
								<ztree:ztree treeType="MAN_DEPARTMENT_TREE" treeId="DepartmentTree">
								</ztree:ztree>
							</td>
						</tr>
					</table>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>