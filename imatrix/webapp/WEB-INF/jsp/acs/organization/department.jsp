<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>部门管理</title>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	
	<script type="text/javascript">
		//新建部门
		function createDept(){
			var did = $('#tree_selected_id').attr('value');
			ajaxSubmit('ajax_from', '${acsCtx}/organization/department!input.action', 'acs_content', updateDeptCallBack);
		}

		function updateDeptCallBack(){
			ruleInput();
		}
		//名称是否不包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~),包括时返回false,不含时返回true
		function validateName(name){
			//if(name.indexOf("_")>=0||name.indexOf("=")>=0||name.indexOf("-")>=0
			//		||name.indexOf("|")>=0||name.indexOf("+")>=0||name.indexOf("~")>=0){
			//	return false;
			//}
			return true;
		}
		//名称是否不包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~),包括时返回false,不含时返回true
		function validateName(name){
			//if(name.indexOf("_")>=0||name.indexOf("=")>=0||name.indexOf("-")>=0
			//		||name.indexOf("|")>=0||name.indexOf("+")>=0||name.indexOf("~")>=0){
			//	return false;
			//}
			return true;
		}

		function ruleInput(){
    		$("#inputForm").validate({
    			submitHandler: function() { 
	    			saveDept(); 
    			},
				rules: {
    			    code: "required",
    			    name: "required",
    			    weight: {
	    				required: true
	    			},
	    			shortTitle: {
	    				maxlength: 30
	    			},
	    			summary: {
	    				maxlength: 60
	    			}
    		     }
		     	,messages: {
			    	'code':"必填",
			    	'name': "必填",
			    	'weight':"必填",
			    	shortTitle:{
		     			maxlength: "最长30字符"
			     	},
			    	summary:{
			     		maxlength : "最长60字符"
				    }
				}
				});
	     }

		//提交
		function submitFormDept(){
			checkDeptName();
		}
		function saveDept(){
			$("#inputForm").attr("action", "${acsCtx}/organization/department!saveDepartment.action");
			ajaxSubmit('inputForm', '${acsCtx}/organization/department!saveDepartment.action', 'acs_content', refreshAfterDeleteDept);
		}

		function checkDeptName(){
			var id = $("#id").val();
			var	branchOrNot=$("#branchFlag").val();
			var parentDepartmentId=$("#parentDepartmentId").val();

			var code = $("#departmentCode").val();
			if(code.indexOf(">")>=0||code.indexOf("<")>=0||code.indexOf("\"")>=0||code.indexOf("'")>=0
					||code.indexOf("/")>=0||code.indexOf(" ")>=0){//包含特殊字符
				alert("编码不能包含符号>、<、\"、'、/、空格");
				return;
			}
			$.ajax({
				type : "POST",
				url : "department!checkDeptName.action",
				data:{departmentName:$("#departmentName").val(),id:id,departmentCode:$("#departmentCode").val(),branchFlag:branchOrNot,parentDepartmentId:parentDepartmentId,departmentShortName:$("#shortTitle").val()},
				success : function(data){
					if(data!=''){
                       if(data=='departmentCodeFalse'){
                          alert("部门编码重复!");
                       }else if(data=='departmentNameFalse'){
                    	   alert("部门名称重复!");
                       }else if(data=='branchCodeFalse'){
                    	   alert("分支机构编码重复!");
                       }else if(data=='branchNameFalse'){
                    	   alert("分支机构名称重复!");
                       }else if(data=='branchNameFalseOfCompany'){
                    	   alert("分支机构名称不能与集团重名!");
                       }else if(data=='branchShortNameFalse'){
                    	   alert("分支机构简称重复!");
                       }
					}else{
						$('#inputForm').submit();
					}
		           /* if( data != "true" ){   
		            	$("#departmentName").val("");
		            	alert(data+"已注册，请更换部门名称 ");
					}else{
						if( typeof(callback)=="function"){
							callback(function(){$('#inputForm').submit();});
						}
					}*/
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对部门名称进行合法性校验，请稍候..."
			});
		}


		//修改部门
		function updateDept(){
			var deptId = $('#tree_selected_id').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			$('#tree_selected_id').attr('value', '');
			ajaxSubmit('ajax_from', '${acsCtx}/organization/department!input.action?id='+deptId, 'acs_content', updateDeptCallBack);
		}

		//删除部门
		function deleteDept(){
			var deptId = $('#tree_selected_id').attr('value');
			var nodeId = $('#tree_selected_node').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}
			//var child=parent.$("#company_department").find(".jstree-clicked").parent().find("ul");
			var child=parent.tree.getSelectNode().children;
			var childLength=0;
			if(child)childLength=child.length;
			//var selectNode=parent.$("#company_department").find(".jstree-clicked").parent().attr("id");
			//var selectNode=nodeId;
			var selectNode=parent.tree.getSelectNodeId();
			var selectNodeType=selectNode.split("-")[0];
			var confirmContent="";
			if(childLength>0){
				if("USERSBYDEPARTMENT"==selectNodeType){
					alert('请先删除子部门或分支机构');
				}else{
					alert('请先删除分支机构下的部门、人员、工作组、分支机构、分支机构授权管理、角色');
				}
				return;
			}
			if("USERSBYDEPARTMENT"==selectNodeType){
				confirmContent="确定要删除部门吗？";
				confirmDelete(confirmContent,deptId);
			}else{
				confirmContent="确定要删除分支机构吗？";
				$.ajax({
					data:{deptId:deptId},
					type:"post",
					url:webRoot+"/organization/department!validateDepartmentDelete.action",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						if("ok"==data){
							confirmDelete(confirmContent,deptId);
						}else{
							alert('请先删除分支机构下的部门、人员、工作组、分支机构、分支机构授权管理、角色');
						}
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
			}
			
		}

		function confirmDelete(confirmContent,deptId){
			if(confirm(confirmContent)){
				//$("#ajax_from").attr("action", "${acsCtx}/organization/department!delete.action?departmentId=" + deptId);
				//setPageState();
				ajaxSubmit('ajax_from', '${acsCtx}/organization/department!delete.action?departmentId='+deptId, 'acs_content', refreshAfterDeleteDept);
			}
		}

		function refreshAfterDeleteDept(){
			//parent.jQuery.jstree._reference("#company_department").destroy();
			parent.initDepartmentTree();
		}

		//添加用户
		function addUsers(){
			var deptId = $('#tree_selected_id').attr('value');
			var deptType = $('#tree_selected_node').val().split('-')[0];
			if(deptId==''||deptType=='USERSBYBRANCH'){
				alert('请在左边树上选择部门');
				return;
			}
			//ajaxSubmit('ajax_from', '${acsCtx}/organization/department!addDepartmentToUsers.action?departmentId='+deptId, 'acs_content',getContentHeight);
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "MAN_DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {
						chkStyle:"checkbox",
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }",
						branchIds:"${branchIds}"
					},
					view: {
						title: "标准树",
						width: 300,
						height:400,
						url:imatrixRoot,
						showBranch:true
					},
					feedback:{
						enable: true,
				                //showInput:"point_user",
				                //hiddenInput:"point_user_value",
				                append:false
					},
					callback: {
						onClose:function(){
						deptAddUserSubmit();
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		//提交为部门添加的用户，过滤掉已在该部门的用户
		function deptAddUserSubmit(){
			var users = ztree.getIds();
			if(typeof(users)=='undefined'||users==''){
                alert("请选择用户!");  
                return;
			}
			var userSubCompanyIds=ztree.getSubCompanyIds();
			var departmentSubCompanyId=$('#tree_selected_node').val().split("=")[1];
			var deptId = $('#tree_selected_id').attr('value');
			if(users!=""){
				if(!isAllInSameBranch(departmentSubCompanyId,userSubCompanyIds)){
                     if(confirm("您已经跨分支机构选人，如果继续执行原来的分支机构将不存在这些人!")){
                    	 $.ajax( {
	     	    				data : {
	     						  choseUserIds : users,chooseDepartmentId:deptId
	     	    				},
	     	    				type : "post",
	     	    				url : "${acsCtx}/organization/department!getRepeatUserNames.action",
	     	    				beforeSend : function(XMLHttpRequest) {
	     	    				},
	     	    				success : function(data, textStatus) {
		     	    				if(data!=''){
                                       alert("如下用户登录名:"+data+"当前分支机构已存在,已被自动过滤,不能被添加!");
			     	    			}
	     	    					
	     	    					var treeSelectedNode = $('#tree_selected_node').val();
	     	    					//ajaxSubmit('deptAddUserForm', '${acsCtx}/organization/department!departmentAddUser.action?treeSelectedNode='+treeSelectedNode, 'acs_content,',add_user_call_back);
	     	    					departmentAddUser(users);
	     	    				},
	     	    				error : function(XMLHttpRequest, textStatus) {
	     	    					alert(textStatus);
	     	    				}
	     	    			});
                   	 
                     }
				}else{
					 $.ajax( {
  	    				data : {
  						  choseUserIds : users,chooseDepartmentId:deptId
  	    				},
  	    				type : "post",
  	    				url : "${acsCtx}/organization/department!getRepeatUserNames.action",
  	    				beforeSend : function(XMLHttpRequest) {
  	    				},
  	    				success : function(data, textStatus) {
     	    				if(data!=''){
                               	alert("如下用户登录名:"+data+"当前分支机构已存在,已被自动过滤,不能被添加!");
	     	    			}
  	    					//var treeSelectedNode = $('#tree_selected_node').val();
  	    					//ajaxSubmit('deptAddUserForm', '${acsCtx}/organization/department!departmentAddUser.action?treeSelectedNode='+treeSelectedNode, 'acs_content,',add_user_call_back);
  	    					departmentAddUser(users);
  	    				},
  	    				error : function(XMLHttpRequest, textStatus) {
  	    					alert(textStatus);
  	    				}
  	    			});
				}
			}else{
				alert("请选择用户");
			}
		}

		function departmentAddUser(users){
			$.ajax({
				type : "POST",
				url : "${acsCtx}/organization/department!departmentAddUser.action",
				data:{treeSelectedNode:$('#tree_selected_node').val(),departmentId:$("#tree_selected_id").val(),addUserIds:users},
				success : function(data){
					setPageState();
					ajaxSubmit('ajax_from', '${acsCtx}/organization/department.action?departmentId='+$("#tree_selected_id").val(), 'acs_content',add_user_call_back);
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对部门名称进行合法性校验，请稍候..."
			});
		}

		//移除用户
		function removeUsers(){
			var deptId = $('#tree_selected_id').attr('value');
			var node = $('#tree_selected_node').attr('value');
			if(deptId==''){
				alert('请在左边树上选择部门');
				return;
			}

			if(node!=''){
               type=node.split('-')[0];
               if(type=='USERSBYBRANCH'){
            	   alert('请选择左边树上的部门节点');
   				   return;
               }
			}
			
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var isSelectedUser = false; 
			if(uIds==''){
				alert('请选择要移除的用户');
				return;
			}else{
				$('#formName').attr('action', '${acsCtx}/organization/department!removeDepartmentToUsers.action');
				var deptId = $('#tree_selected_id').attr('value');
				var inpt = document.createElement("input");
				inpt.setAttribute("name", "departmentId");
				inpt.setAttribute("value", deptId);
				inpt.setAttribute("type", "hidden");
				document.getElementById("formName").appendChild(inpt);
				var inpt2 = document.createElement("input");
				inpt2.setAttribute("name", "treeSelectedNode");
				inpt2.setAttribute("value", node);
				inpt2.setAttribute("type", "hidden");
				document.getElementById("formName").appendChild(inpt2);
				for(var i=0;i<uIds.length;i++){
					var inpt1 = document.createElement("input");
					inpt1.setAttribute("name", "userIds");
					inpt1.setAttribute("value", uIds[i]);
					inpt1.setAttribute("type", "hidden");
					document.getElementById("formName").appendChild(inpt1);
				}
				$('#formName').submit();
			}
		}

		

		function  add_user_call_back(){
			 getContentHeight();
		}
		

		function isAllInSameBranch(target,idStr){
			if(target=='null'&&!isUserIdNull(idStr)){
                return false;
			}else if(target!='null'&&isUserIdNull(idStr)){
				return false;
			}else if(target!='null'&&idStr!=''){
				 var ids = idStr.split(",");
				 var result = true;
				  for(var i=0;i<ids.length;i++){
				      if(target!=ids[i]){
				        result = false;
				        break;
				      }
				  }
				  return result;
			}else{
                  return true;
			} 
		}

		function isUserIdNull(ids){
			 var idStr = ids.split(",");
			 var result = true;
			  for(var i=0;i<idStr.length;i++){
			      if(idStr[i]!='null'){
			        result = false;
			        break;
			      }
			  }
			  return result;
	    }

		function isContain(target,ids){
			 var idStr = ids.split(",");
			 var result = false;
			  for(var i=0;i<idStr.length;i++){
			      if(target==idStr[i]){
			        result = true;
			        break;
			      }
			  }
			  return result;
		}
		function allUsers(id){
	    	var lists = $("#"+id).find("li.jstree-checked");
			var v="" ;
			for(var i=0; i<lists.length; i++){
				v+=$(lists[i]).attr("id");
				if(i!=lists.length-1)
					v+=";";
			}
			if(v!=""){
				var arr=v.split(";");
				return arr;
			}else{
				return "";
			}
		}

		function setParentDeptInfo(id, name){
			$('#parentDepartmentId').attr('value', id);
			$('#parentDepartmentName').attr('value', name);
		}

		function setParentDeptName(name){
			$('#parentDepartmentId').attr('value', '');
			$('#parentDepartmentName').attr('value', name);
		}
		function checkClick(code,id){
			var oldcode=code;
           var checked = $("#branch").attr("checked");
           if(checked){
        	   $("#branchFlag").attr("value","true");
           }else{
        	   $("#branchFlag").attr("value","false");
           }
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<form action="#" id='defaultForm' name="defaultForm"></form>
		<form id="ajax_from" name="ajax_from" action="" method="post">
			<aa:zone name="acs_deptid">
				<input type="hidden" id="tree_selected_id" name="parentId" value="${departmentId}">
				<input type="hidden" id="tree_selected_node" name="treeSelectedNode" value="${treeSelectedNode }">
			</aa:zone>
		</form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="createDepartment">
			        <button  class='btn' onclick="createDept();"><span><span><s:text name="common.create" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="editDepartment">
			        <button  class='btn' onclick="updateDept();"><span><span><s:text name="common.alter" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="deleteDepartment">
					<s:if test='#versionType=="online"'>
			        	<button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="common.deleteDepartment" /></span></span></button>
			        </s:if><s:else>
			        	<button  class='btn' onclick="deleteDept();"><span><span><s:text name="common.deleteDepartment" /></span></span></button>
			        </s:else>
				</security:authorize>
				<security:authorize ifAnyGranted="addDepartmentToUser">
			        <button  class='btn' onclick="addUsers();"><span><span><s:text name="department.addUser" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="departmentRemoveUser">
			        <button  class='btn' onclick="removeUsers();"><span><span><s:text name="department.removeUser" /></span></span></button>
				</security:authorize>
			</div>
			<div id="opt-content" >
				<form name="formName" id="formName"></form>
				<view:jqGrid url="${acsCtx}/organization/department.action?departmentId=${departmentId}" pageName="userPage" code="DEPART_LIST_USER" gridId="main_table"></view:jqGrid>
			</div>
		</aa:zone>
	</div>
</div>   	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
