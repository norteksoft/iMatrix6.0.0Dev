<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body>
<div class="ui-layout-center">
<aa:zone name="acs_content">
<div class="opt-btn">
<button  type="button" class='btn' onclick="deptAddUserSubmit();"><span><span><s:text name="common.submit"/></span></span></button>
<button  type="button" class='btn' onclick="setPageState();cancel('${departmentId}');"><span><span><s:text name="common.cancel"/></span></span></button>
<script type="text/javascript">
function cancel(id){
    location.href=webRoot+"/organization/department.action?departmentId="+id;
}
</script>
</div>
<div id="opt-content">
<form id="deptAddUserForm" name="inputForm" action="department!departmentAddUser.action" method="post">
   	<input type="hidden" id="departmentId" name="departmentId" value="${departmentId}" />
	<div class="content">
		<ztree:ztree
						treeType="MAN_DEPARTMENT_TREE"
						chkboxType="{'Y' : 'ps', 'N' : 'ps'}" 
						chkStyle="checkbox"
						treeId="treeDemo" 
						userWithoutDeptVisible="true"  
						branchIds="${branchIds}"
						>
		</ztree:ztree>
	</div>
</form>
</div>
</aa:zone>
</div>
</body>
</html>
