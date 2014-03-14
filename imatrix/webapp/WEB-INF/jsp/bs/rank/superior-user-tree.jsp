<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/setting-colorbox-meta.jsp"%>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	function selectSuperiorUsers(){
		ztree.selectTreeValue(function(){
			window.parent.$("#superiorUserId").attr("value",ztree.getId());
			window.parent.$("#name").attr("value",ztree.getName());
			window.parent.$("#superiorLoginName").attr("value",ztree.getLoginName());
			window.parent.$("#title").focus();
			window.parent.$("#selectSuperiorUser").colorbox.close();
		});
			
	}
	</script>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
		<input type="hidden" id="isbranch" value="${isbranch }"/>
		<div style="margin-left: 10px;margin-top: 10px;">
			<div class="opt-btn">
				<button class="btn" onclick="selectSuperiorUsers();"><span><span>确定</span></span></button>
			</div>
			<div style="margin-top: 20px;">
				<ztree:ztree
							treeType="COMPANY"
							treeId="treeDemo" 
							userWithoutDeptVisible="true" 
							showBranch="true"
							>
				</ztree:ztree>
			</div>
		</div>
		<style type="text/css"> #user_tree{background: none;} </style>
	</aa:zone>
</body>
</html>