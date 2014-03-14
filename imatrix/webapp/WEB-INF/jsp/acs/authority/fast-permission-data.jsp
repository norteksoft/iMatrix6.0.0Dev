<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据权限</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<script type="text/javascript" src="${resourcesCtx }/widgets/jstree/jquery.jstree.js"></script>
	<script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/custom.tree.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${acsCtx}/css/custom.css" />
	<script src="${acsCtx}/js/authority.js" type="text/javascript"></script>

	
	<script type="text/javascript">
	function createPermission(){
		var menuId=$("#menuId").val();
		if(menuId!='' && typeof (menuId)!='undefined'){
			$("#entiyId").attr("value","");
			ajaxSubmit("defaultForm", "${acsCtx}/authority/fast-permission-input.htm", "pageTable",validatePermission);
		}else{
			alert("请选择对应的系统!");
		}
	}
	function validatePermission(){
		getContentHeight();
		$("#viewSaveForm").validate({
			submitHandler: function() {
				var boxes = $("input[name='permissionUserCheck']:checked");
				if(boxes<=0){
					alert("请选择人员");
				}else{
					var permissionUsers = getPermissionUser();
					if(permissionUsers=="[]"){//表示只选中了复选框但没有选择具体值
						alert("请选择人员");
					}else{
						var checkedAuths=$("input[name='docAuthes']:checked");
						if(checkedAuths.length<=0){
							alert("请选择操作权限");
						}else{
							//设置"子部门是否继承该权限"的值
							var deparmentInheritable = $("#deptInheri").attr("checked");
							$("#deparmentInheritable").attr("value",deparmentInheritable);
							//设置"人员"的值
							$("#permissionUsers").attr("value",permissionUsers);
							ajaxSubmit('viewSaveForm','${acsCtx}/authority/fast-permission-save.htm','pageTablelist',saveCallback);
						}
					}
				}
			},
			rules: {
				code:"required",
				name:"required",
				dataTableName:"required"
			},
			messages: {
				code:"必填",
				name:"必填",
				dataTableName:"必填"
			}
		});
	}

	//返回值:{{type:itemType,value:{{conditionName:,conditionValue:},{conditionName:,conditionValue:},...}},{type:itemType,value:{{conditionName:,conditionValue:},{conditionName:,conditionValue:},...}},...}
	function getPermissionUser(){
		var permissionUsers="";
		var boxes = $("input[name='permissionUserCheck']:checked");
		for(var i=0;i<boxes.length;i++){
			if($(boxes[i]).attr("id")=="all_user_checkbox"){//选中了所有人
				permissionUsers="[";
				permissionUsers+='{"itemType":"ALL_USER"}';
				permissionUsers+="]";
				return permissionUsers;
			}else{
				if($(boxes[i]).attr("id")=="user_checkbox"){//选中了"具体人"
					var pointUsers = $("#point_user_value").val();//{"conditionName":...,"conditionValue":...},{"conditionName":...,"conditionValue":...},...
					if(pointUsers!=""&&typeof(pointUsers)!='undefined')permissionUsers = getPermissionUsers(permissionUsers,"USER",pointUsers);
				}
				if($(boxes[i]).attr("id")=="dept_checkbox"){//选中了"具体部门"
					var pointDepts = $("#point_dept_value").val();//{"conditionName":...,"conditionValue":...},{"conditionName":...,"conditionValue":...},...
					if(pointDepts!=""&&typeof(pointDepts)!='undefined')permissionUsers = getPermissionUsers(permissionUsers,"DEPARTMENT",pointDepts);
				}
				if($(boxes[i]).attr("id")=="role_checkbox"){//选中了"具体角色"
					var pointRoles = $("#point_role_value").val();//{"conditionName":...,"conditionValue":...},{"conditionName":...,"conditionValue":...},...
					if(pointRoles!=""&&typeof(pointRoles)!='undefined')permissionUsers = getPermissionUsers(permissionUsers,"ROLE",pointRoles);
				}
				if($(boxes[i]).attr("id")=="wg_checkbox"){//选中了"具体工作组"
					var pointWgs = $("#point_wg_value").val();//{"conditionName":...,"conditionValue":...},{"conditionName":...,"conditionValue":...},...
					if(pointWgs!=""&&typeof(pointWgs)!='undefined')permissionUsers = getPermissionUsers(permissionUsers,"WORKGROUP",pointWgs);
				}
			}
		}
		return "["+permissionUsers+"]";
	}
	//是否应该加逗号
	function shouldAddComma(permissionUsers){
		if(permissionUsers!="[")return true;
		return false;
	}

	function getPermissionUsers(permissionUsers,itemType,pointPermissionUsers){
		if(permissionUsers!=""){
			permissionUsers+=',{"itemType":"'+itemType+'","permissionValues":'+pointPermissionUsers+'}';
		}else{
			permissionUsers+='{"itemType":"'+itemType+'","permissionValues":'+pointPermissionUsers+'}';
		}
		return permissionUsers;
	}
	

	function saveCallback(){
		showMsg();
		validatePermission();
	}
	function updatePermission(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			alert("请选择记录");
			return;
		}else if(boxes.length>1){
			alert("只能选择一条记录");
			return;
		}else{
			$("#entiyId").attr("value",boxes[0]);
			ajaxSubmit("defaultForm", "${acsCtx}/authority/fast-permission-input.htm", "pageTable",validatePermission);
		}
	}
	function deletePermission(){
		var boxes = jQuery("#page").jqGrid("getGridParam",'selarrrow');
		if(boxes.length<=0){
			alert("请选择记录");
			return;
		}else{
			if(confirm("确定删除?")){
				$("#ids").attr("value",boxes.join(','));
				ajaxSubmit("defaultForm", "${acsCtx}/authority/fast-permission-delete.htm", "pageTablelist",showMsg);
			}
		}
	}
	function savePermission(){
		$("#viewSaveForm").submit();
	}

	function backPage(){
		setPageState();
		ajaxSubmit("viewSaveForm", "${acsCtx}/authority/fast-permission-data.htm", "pageTable");
	}


	function  validatePermissionSave(){
		if($("#code").attr("value")==""){
			savePermission();
		}else{
			if(existable){//当焦点在编号文本框时,点击保存会走2次isPermissionCodeExist方法,为了预防该种情况设置了该条件
				alert("该授权编号已存在");
			}else{
				isPermissionCodeExist("save");
			}
		}
	}

	//pos:值为fast或dataRule
	function selectDataTable(pos){
		var dataTableId = $("#dataTableId").val();
		var url = webRoot+"/authority/data-rule-selectDataTable.htm?selectPageFlag="+true+"&position="+pos;
		var permissionId = $("#permissionId").val();
		if(permissionId!=""&&typeof(permissionId)!='undefined'){
			url = url+"&permissionId="+permissionId;
		}
		var dataTableId = $("#dataTableId").val();
		if(dataTableId!=""&&typeof(dataTableId)!='undefined'){
			url = url+"&dataTableId="+dataTableId;
		}
		var listViewId = $("#listViewId").val();
		if(listViewId!=""&&typeof(listViewId)!='undefined'){
			url = url+"&listViewId="+listViewId;
		}
		$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"数据表"});
	}
	//选择具体人
		function selectPointUser(){
//			popTree({ title :'选择',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				treeTypeJson:null,
//				multiple:'true',
//				loginNameId:'',
//				hiddenInputId:"point_user_value",
//				showInputId:"point_user",
//				acsSystemUrl:imatrixRoot,
//				isAppend:"false",
//				userWithoutDeptVisible:true,
//				callBack:function(){
//					getPermissionUserInfo();
//				}});
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
						chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
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
							getPermissionUserInfo();
						}
					}			
					};
				    popZtree(zTreeSetting);
			
		}
	//[{conditionName:,conditionValue:},{conditionName:,conditionValue:},...]
	function getPermissionUserInfo(){
		var usernames = ztree.getNames();//用户姓名(部门/分支机构简称\名称)
		var userIds = ztree.getIds();//不是无部门人员时:userId~deptId;无部门人员时:userId
		var usernameArr = usernames.split(",");
		var useridArr = userIds.split(",");
		$("#point_user").attr("value",usernames);
		if(usernames=="所有人员"){
			$("#point_user_value").attr("value","["+getPointPermissionInfo(["ALL_USER"],["所有人员"])+"]");
		}else{
			$("#point_user_value").attr("value","["+getPointPermissionInfo(useridArr,usernameArr)+"]");
		}
	}

	//选择具体部门
	function selectPointDept(){
//		popTree({ title :'选择',
//		innerWidth:'400',
//		treeType:'DEPARTMENT_TREE',
//		defaultTreeValue:'id',
//		leafPage:'false',
//		treeTypeJson:null,
//		multiple:'true',
//		loginNameId:'',
//		hiddenInputId:"point_dept_value",
//		showInputId:"point_dept",
//		acsSystemUrl:imatrixRoot,
//		isAppend:"false",
//		callBack:function(){
//			getPermissionDeptInfo();
//		}});
	var zTreeSetting={
			leaf: {
				enable: false
			},
			type: {
				treeType: "DEPARTMENT_TREE",
				noDeparmentUser:false,
				onlineVisible:false
			},
			data: {
				chkStyle:"checkbox",
				chkboxType:"{'Y' : 's', 'N' : 's' }"
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
		                //showInput:"point_dept",
		                //hiddenInput:"point_dept_value",
		                append:false
			},
			callback: {
				onClose:function(){
					getPermissionDeptInfo();
				}
			}			
			};
		    popZtree(zTreeSetting);
	}

	function getPermissionDeptInfo(){
		var deptnames = ztree.getDepartmentNames();//部门名称(分支机构简称\名称)
		var deptnameArr = deptnames.split(",");
		var deptids = "";
		if(deptnames=="所有部门"){
			deptids="ALL_DEPARTMENT";
		}else{
			deptids = ztree.getDepartmentIds();
		}
		var deptidArr = deptids.split(",");
		$("#point_dept").attr("value",deptnames);
		$("#point_dept_value").attr("value","["+getPointPermissionInfo(deptidArr,deptnameArr)+"]");
	}
	//选择具体角色
	function selectPointRole(){
		custom_ztree({url:webRoot+'/authority/select-role-tree.htm',
			onsuccess:function(){getPermissionRoleInfo();},//回调方法
			width:500,
			height:400,
			title:'角色',
			nodeInfo:['type','id','name'],
			multiple:true,
			webRoot:imatrixRoot
		});
		
	}
	function getPermissionRoleInfo(){
		var roleInfos = getPointRole();
		var rolename=roleInfos[0];//角色名称(系统名称/分支机构简称\名称)
		var roleid=roleInfos[1];
		if(rolename!=""){
			var rolenameArr = rolename.split(",");
			var roleidArr = roleid.split(",");
			$("#point_role_value").attr("value","["+getPointPermissionInfo(roleidArr,rolenameArr)+"]");
			$("#point_role").attr("value",rolename);
		}
	}
	
	//选择具体工作组
	function selectPointWorkgroup(){
//		popTree({ title :'选择',
//		innerWidth:'400',
//		treeType:'GROUP_TREE',
//		defaultTreeValue:'id',
//		leafPage:'false',
//		treeTypeJson:null,
//		multiple:'true',
//		loginNameId:'',
//		hiddenInputId:"point_wg_value",
//		showInputId:"point_wg",
//		acsSystemUrl:imatrixRoot,
//		isAppend:"false",
//		callBack:function(){
//			getPermissionWgInfo();
//		}});
	var zTreeSetting={
			leaf: {
				enable: false
			},
			type: {
				treeType: "GROUP_TREE",
				noDeparmentUser:false,
				onlineVisible:false
			},
			data: {
				chkStyle:"checkbox",
				chkboxType:"{'Y' : 'ps', 'N' : 'ps' }"
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
		                //showInput:"point_wg",
		                //hiddenInput:"point_wg_value",
		                append:false
			},
			callback: {
				onClose:function(){
					getPermissionWgInfo();
				}
			}			
			};
		    popZtree(zTreeSetting);
	}

	function getPermissionWgInfo(){
		var wgnames = ztree.getWorkGroupNames();//工作组名称(分支机构简称\名称)
		var wgids = ztree.getWorkGroupIds();
		var wgnameArr = wgnames.split(",");
		var wgidArr = wgids.split(",");
		if(wgidArr.length>1&&wgnames=="所有工作组"){//如果工作组不是只有一个，且显示为所有工作组，则显示所有工作组
			wgids="ALL_WORKGROUP";
		}
		wgidArr = wgids.split(",");
		$("#point_wg").attr("value",wgnames);
		$("#point_wg_value").attr("value","["+getPointPermissionInfo(wgidArr,wgnameArr)+"]");
	}

	function getPointPermissionInfo(infoIdArr,infonameArr,type,typeArr){
		var permissionInfo = "";
		for(var i=0;i<infoIdArr.length;i++){
			if(permissionInfo==""){
				permissionInfo = "{\"conditionName\":\""+infonameArr[i]+"\",\"conditionValue\":\""+infoIdArr[i]+"\"}";
			}else{
				permissionInfo += ",{\"conditionName\":\""+infonameArr[i]+"\",\"conditionValue\":\""+infoIdArr[i]+"\"}";
			}
		}
		return permissionInfo;
	}

	function allUserChange(){
		if($("#all_user_checkbox").attr("checked")){
			$("#user_checkbox").removeAttr("checked");
			$("#user_checkbox").attr("disabled","disabled");
			
			$("#dept_checkbox").removeAttr("checked");
			$("#dept_checkbox").attr("disabled","disabled");
			
			$("#role_checkbox").removeAttr("checked");
			$("#role_checkbox").attr("disabled","disabled");
			
			$("#wg_checkbox").removeAttr("checked");
			$("#wg_checkbox").attr("disabled","disabled");
			
		}else{
			$("#user_checkbox").removeAttr("disabled");
			
			$("#dept_checkbox").removeAttr("disabled");
			
			$("#role_checkbox").removeAttr("disabled");
			
			$("#wg_checkbox").removeAttr("disabled");
		}
	}

	</script>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
			<input type="hidden" id="menuId"  name="sysMenuId" value="${sysMenuId }"/>
			<input type="hidden" id="entiyId"  name="permissionId" />
			<input type="hidden" id="ids"  name="ids" />
		</form>
	<aa:zone name="pageTable">
		<div class="opt-btn">
			<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
			<button class="btn" onclick='createPermission();' id="create"><span><span >新建</span></span></button>
			<button class="btn" onclick="updatePermission();"><span><span >修改</span></span></button>
			<button class="btn" onclick="deletePermission();"><span><span >删除</span></span></button>
		</div>
		<aa:zone name="pageTablelist">
			<div id="opt-content">
				<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
				<form action="${acsCtx}/authority/permission-data.htm" name="pageForm" id="pageForm" method="post">
					<view:jqGrid url="${acsCtx}/authority/fast-permission-data.htm?sysMenuId=${sysMenuId }" code="ACS_FAST_PERMISSION" gridId="page" pageName="page"></view:jqGrid>
				</form>
			</div>
		</aa:zone>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
