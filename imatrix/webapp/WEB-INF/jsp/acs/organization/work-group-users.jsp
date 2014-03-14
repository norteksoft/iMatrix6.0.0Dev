<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/custom.tree.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${acsCtx}/css/custom.css" />
	
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript">
		//工作组增加用户
		function addUsersToWorkgroup(){
			/*
			popTree({ title :'选择',
				innerWidth:'400',
				treeType:'MAN_DEPARTMENT_TREE',
				defaultTreeValue:'id',
				leafPage:'false',
				treeTypeJson:null,
				multiple:'true',
				loginNameId:'',
				hiddenInputId:"_userIds",
				showInputId:"",
				acsSystemUrl:imatrixRoot,
				isAppend:"false",
				userWithoutDeptVisible:true,
				branchIds:$("#_manageBranchesIds").val(),
				callBack:function(){
					saveBranchesManager();
				}});
			*/
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
						branchIds:$("#_manageBranchesIds").val()
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
						saveBranchesManager();
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function saveBranchesManager(){
			/*
			$.ajax({
				data:{workGroupId:$("#workGroupId").val(),ids:$("#_userIds").val()},
				type:"post",
				url:webRoot+"/organization/work-group!workgroupAddUser.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						ajaxSubmit('ajax_from', webRoot+'/organization/work-group!getUserByWorkGroup.action', 'acs_content');
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
			*/
			$.ajax({
				data:{workGroupId:$("#workGroupId").val(),ids:ztree.getIds()},
				type:"post",
				url:webRoot+"/organization/work-group!workgroupAddUser.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						ajaxSubmit('ajax_from', webRoot+'/organization/work-group!getUserByWorkGroup.action', 'acs_content');
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}
		//工作组增加用户
		function addUsersToWorkgroup1111111111(){
			var workgroupId = $('#workGroupId').attr('value');
			if(workgroupId==''){
				alert('请在左边树上选择工作组');
				return;
			}
			ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!addUsersToWorkgroup.action', 'acs_content',getContentHeight);
		}
		//提交为工作组添加的用户
		function workgroupAddUserSubmit(){
			var users = getInfo("user");
			var userArr = eval(users);
			if(typeof(userArr)=='undefined'){
                alert("请选择用户！");return;
			}
			var resultids="";
			var hasEffectiveUser = false;//是否已有用户，true：表示有，false表示没有
			for(var i=0;i<userArr.length;i++){
				if(userArr[i].type=="company"){
					resultids="0";
					var inpt = document.createElement("input");
					inpt.setAttribute("name", "userIds");
					inpt.setAttribute("value", "0");
					inpt.setAttribute("type", "hidden");
					document.getElementById("workgroupAddUserForm").appendChild(inpt);
					break;
				}else if(userArr[i].type=="user"){
					if(resultids.indexOf(userArr[i].id+",")<0){
						resultids = resultids+","+userArr[i].id;
						hasEffectiveUser=false;
					}else{
						hasEffectiveUser=true;
					}
					if(!hasEffectiveUser){
						var inpt = document.createElement("input");
						inpt.setAttribute("name", "userIds");
						inpt.setAttribute("value", userArr[i].id);
						inpt.setAttribute("type", "hidden");
						document.getElementById("workgroupAddUserForm").appendChild(inpt);
					}
				}
			}
			if(resultids!=""){
				$('#workgroupAddUserForm').submit();
			}else{
				alert("请选择用户");
			}
			
		}

		//移除用户
		function removeUsersToWorkgroup(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var isSelectedUser = false; 
			if(uIds==''){
				alert('请选择要移除的用户');
				return;
			}else{
				$('#formName').attr('action', '${acsCtx}/organization/work-group!removeWorkgroupToUsers.action');
				var workGroupId = $('#workGroupId').attr('value');
				var inpt = document.createElement("input");
				inpt.setAttribute("name", "workGroupId");
				inpt.setAttribute("value", workGroupId);
				inpt.setAttribute("type", "hidden");
				document.getElementById("formName").appendChild(inpt);
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
		//查看用户表
		function viewUser(ts1,cellval,opts,rwdat,_act){
			var v="<a  href=\"#\" hidefocus=\"true\" onclick=\"_click_fun("+opts.id+");\">" + ts1 + "</a>";
			return v;
		}
		
		function _click_fun(id){
			$("#ajaxId").attr("value",id);
			$("#look").attr("value","LOOK");
			$("#fromWorkgroup").attr("value","fromWorkgroup");
			ajaxSubmit("ajax_from", webRoot+'/organization/user!inputLook.action', "acs_content", getContentHeight);
		}
		//取消
		function cancel(){
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    if(t=='DELETED_USER'){
			    $('#cancelForm').attr('action', '${acsCtx}/organization/user!deleteList.action');
			}
			ajaxAnywhere.formName = "cancelForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				$('input').attr('disabled', '');
				$('select').attr('disabled', '');
				//initUserTable();
			};
			ajaxAnywhere.submitAJAX(); 
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		 <form id="ajax_from" name="ajax_from" action="" method="post">  
		    <input type="hidden" name="userId" id="ajaxId" />
		    <input type="hidden" name="look" id="look" />
		    <input type="hidden" name="fromWorkgroup" id="fromWorkgroup" >
	        <input type="hidden" name="workGroupId" id="workGroupId" value="${workGroupId}">
	        <input type="hidden" id="_userIds" />
	        <input type="hidden" id="_manageBranchesIds" value="${manageBranchesIds }" />
		 </form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="getUserByWorkGroup ">
					<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="addWorkGroupToUser">
			        <button  class='btn' onclick="addUsersToWorkgroup();"><span><span><s:text name="workGroup.addUser" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="workGroupRemoveUser">
				    <button  class='btn' onclick="removeUsersToWorkgroup();"><span><span><s:text name="workGroup.removeUser" /></span></span></button>
				</security:authorize>
			</div>
			<div id="notice"></div>	
			<div id="opt-content" >
			   <aa:zone name="acs_list">
				<form id="formName" name="formName" action="" method="post">
					<s:if test="containBranches">
				     <view:jqGrid url="${acsCtx}/organization/work-group!getUserByWorkGroup.action?workGroupId=${workGroupId }" pageName="userPage" code="ACS_USER_SUB_COMPANY" gridId="main_table"></view:jqGrid>
					</s:if><s:else>
				     <view:jqGrid url="${acsCtx}/organization/work-group!getUserByWorkGroup.action?workGroupId=${workGroupId }" pageName="userPage" code="ACS_USER" gridId="main_table"></view:jqGrid>
					</s:else>
				</form>
				</aa:zone>
			</div>
		</aa:zone>
	</div>
</div>  	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>

</html>
