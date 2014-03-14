<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
	<title><s:text name="role.roleManager"/></title>
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<link href="${resourcesCtx}/widgets/colorbox/colorbox.css" rel="stylesheet" type="text/css"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
    <script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/custom.tree.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${acsCtx}/css/custom.css" />
	
	<script type="text/javascript">
		function loadContent(){}
		function query(){
			if($("#query_type").val()==""){
				$("#query_type").attr("value","ROLE_USER");
			}
			var value = $("#acs_ids").val();
			if($.trim(value).length == 0){
				alert("请选择查询条件");
				return;
			}
			$("#ajax_from").attr("action",webRoot+"/authorization/role!query.action");
			ajaxAnywhere.formName = "ajax_from";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function () {
				getRoleQueryContentHeight();
			};
			ajaxAnywhere.submitAJAX();
		}

		function getRoleQueryContentHeight(){
			var h = $('.ui-layout-center').height();
			$("#opt-content").css("height",h-80); 
		}

		function select(){
			if('ROLE_USER'==$("#query_type").val()||$("#query_type").val()==''){
				/*
				popTree({ title :'选择',
					innerWidth:'400',
					treeType:'MAN_DEPARTMENT_TREE',
					defaultTreeValue:'id',
					leafPage:'false',
					treeTypeJson:null,
					multiple:'true',
					loginNameId:'',
					hiddenInputId:"acs_ids",
					showInputId:"acs_name",
					acsSystemUrl:imatrixRoot,
					isAppend:"false",
					userWithoutDeptVisible:true,
					branchIds:$("#_manageBranchesIds").val()
					});
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
					                //showInput:"acs_name",
					               //hiddenInput:"acs_ids",
					                append:false
						},
						callback: {
							onClose:function(){
								getSelectUser();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}else if('ROLE_DEPARTMENT'==$("#query_type").val()){
				/*
				popTree({ title :'选择',
					innerWidth:'400',
					treeType:'DEPARTMENT_TREE',
					defaultTreeValue:'id',
					leafPage:'false',
					treeTypeJson:null,
					multiple:'true',
					loginNameId:'',
					hiddenInputId:"acs_ids",
					showInputId:"acs_name",
					acsSystemUrl:imatrixRoot,
					isAppend:"false",
					branchIds:$("#_manageBranchesIds").val()
					});
				*/
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
							chkboxType:"{'Y' : 's', 'N' : 's' }",
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
					                //showInput:"acs_name",
					                //hiddenInput:"acs_ids",
					                append:false
						},
						callback: {
							onClose:function(){
								getSelectDepartment();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}else if('ROLE_WORKGROUP'==$("#query_type").val()){
				/*
				popTree({ title :'选择',
					innerWidth:'400',
					treeType:'GROUP_TREE',
					defaultTreeValue:'id',
					leafPage:'false',
					treeTypeJson:null,
					multiple:'true',
					loginNameId:'',
					hiddenInputId:"acs_ids",
					showInputId:"acs_name",
					acsSystemUrl:imatrixRoot,
					isAppend:"false",
					branchIds:$("#_manageBranchesIds").val()
					});
				*/
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "GROUP_TREE",
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
					                //showInput:"acs_name",
					                //hiddenInput:"acs_ids",
					                append:false
						},
						callback: {
							onClose:function(){
							getSelectWorkgroup();
							}
						}			
						};
					    popZtree(zTreeSetting);
			}
		}

		function getSelectUser(){
			$("#acs_name").attr("value",ztree.getNames());
			$("#acs_ids").attr("value",ztree.getIds());
			
		}
		function getSelectDepartment(){
			$("#acs_name").attr("value",ztree.getDepartmentNames());
			$("#acs_ids").attr("value",ztree.getDepartmentIds());
			
		}
		function getSelectWorkgroup(){
			$("#acs_name").attr("value",ztree.getWorkGroupNames());
			$("#acs_ids").attr("value",ztree.getWorkGroupIds());
			
		}

		function checkAll(obj){
			if($(obj).attr('id') == 'role_userNames'){
				checkEveryOne(obj, 'userIds');
			}else if($(obj).attr('id') == 'role_departmentNames'){
				checkEveryOne(obj, 'departmentsIds');
			}else if($(obj).attr('id') == 'role_workgroupNames'){
				checkEveryOne(obj, 'workgroupIds');
			}
		}
		function checkEveryOne(obj, name){
			var ids = $('input[name='+name+']');
			if($(obj).attr('checked')){
				for(var i = 0; i < ids.length; i++){
					$(ids[i]).attr('checked', 'checked');
				}
			}else{
				for(var i = 0; i < ids.length; i++){
					$(ids[i]).attr('checked', '');
				}
			}
		}
		
		function _click_one(boxId,inputName){
			var ones = $("input[name='"+inputName+"']");
			var allChecked = true;
			for(var i=0;i<ones.length;i++){
	          if($(ones[i]).attr("checked")==false){
	              allChecked=false;
	              break;
	          }
			}
			if(allChecked){
				$("#"+boxId).attr("checked","checked");
			}else{
				$("#"+boxId).attr("checked","");
			}
		}

		//详细
		function viewDetail(id,roleId,viewType){
			init_colorbox(webRoot+"/authorization/role-detail.action?roleId="+roleId+"&id="+id+"&viewType="+viewType,"查看权限",750,450);
		}
		//导出
		function exportRoleQuery(){
			var ids="";
			if(""==$("#query_type").val()||"ROLE_USER"==$("#query_type").val()){
				var userIdsLength=$("input[name=userIds]:checked").length;
				if(userIdsLength==0){
					alert("请选择导出项！");
					return;
				}
				ids=getSelectItems("userIds");
			}
			if("ROLE_DEPARTMENT"==$("#query_type").val()){
				var departmentsIdsLength=$("input[name=departmentsIds]:checked").length;
				if(departmentsIdsLength==0){
					alert("请选择导出项！");
					return;
				}
				ids=getSelectItems("departmentsIds");
			}
			if("ROLE_WORKGROUP"==$("#query_type").val()){
				var workgroupLength=$("input[name=workgroupIds]:checked").length;
				if(workgroupLength==0){
					alert("请选择导出项！");
					return;
				}
				ids=getSelectItems("workgroupIds");
			}
			$("#exportQueryIds").attr("value",ids);
			$("#ajax_from").attr("action",webRoot+"/authorization/role-exportRoleQuery.action");
			$("#ajax_from").submit();
		}
		function getSelectItems(name){
			var selectItems="";
			$("input[name="+name+"]:checked").each(function(){
				if(selectItems!=''){
					selectItems+=',';
				}
				selectItems+=$(this).attr("value");
			});
			return selectItems;
		}
	</script>
	<style type="text/css">
		table.full{ 
			border:1px solid #C5DBEC;
			border-collapse:collapse;
			width:100%;
		}
		table.full thead tr {
			background:url("../../images/ui-bg_glass_85_dfeffc_1x400.png") repeat-x scroll 50% 50% #DFEFFC;
			color:#2E6E9E;
			font-weight:bold;
		}
		td ul{ padding-left: 8px; margin: 2px 0; }
		td ul li{ list-style: none; }
	</style>
</head>

<body>
<div class="ui-layout-center">
			<div class="opt-body">
				<div class="opt-btn">
					<input id="acs_name" name="queryName" readonly="readonly" type="text"/>
					<button  class='btn' onclick="select();"><span><span>${queryTitle }</span></span></button>
					<button  class='btn' onclick="query();"><span><span>查询</span></span></button>
					<button  class='btn' onclick="exportRoleQuery();"><span><span>导出</span></span></button>
				</div>
				<form id="ajax_from" name="ajax_from" action="" method="post">
					<div id="queryDiv" class="query_div"  style="padding: 5px;">
						<input id="query_type" name="queryType" type="hidden" value="${queryType }"/> 
						<input name="queryTitle" type="hidden" value="${queryTitle }"/> 
						<input id="acs_ids" name="queryIds" type="hidden"/>
						<input id="exportQueryIds" name="exportQueryIds" type="hidden"/>
						<input type="hidden" id="_manageBranchesIds" value="${manageBranchesIds }" />
						<span style="display: none;" id="message"></span>
					</div>
				</form>
				<div id="opt-content" >
					<aa:zone name="acs_content">
						<div id="result" style="height: 600px;">
						<!-- 用户  -->
							<s:if test="queryType=='ROLE_USER'&&userRoleMap.size>0">
									<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
										<thead>
											<tr>
												<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="role_userNames" onclick="checkAll(this);"/>人员名称</th>
												<th style="text-align:left; padding-left:4px;">角色</th>
												<th style="text-align:left; padding-left:4px;">权限</th>
											</tr>
										</thead>
										<tbody>
											<s:iterator value="userRoleMap.keySet()" id="user" >
												<s:set id="listLength" value="userRoleMap.get(#user).size"></s:set>
												<s:iterator value="userRoleMap.get(#user)" id="roleList" status="index">
												<tr> 
													<s:if test="#index.index==0">
													<td valign="top" rowspan="${listLength }">
														<ul  style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><input type="checkbox" name="userIds" value="${user.id}" onclick="_click_one('role_userNames','userIds');"/>${user.name}<s:if test="containBranches">(${user.subCompanyName})</s:if></li>
														</ul>
													</td>
													</s:if>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;">${roleList.name}(${roleList.businessSystem.name}<s:if test="containBranches">/${roleList.subCompanyName}</s:if>)</li>
														</ul>
													</td>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><a href="#" onclick="viewDetail(${user.id},${roleList.id},'user');">详细</a></li>
														</ul>
													</td>
												</tr>
												</s:iterator>
											</s:iterator>
										</tbody>
									</table>
								
							</s:if>
							<!-- 部门 -->
							<s:elseif test="queryType=='ROLE_DEPARTMENT'&&departmentRoleMap.size>0">
								<table border="1" id="sTable" cellpadding="0" cellspacing="0" class="leadTable" >
									<thead>
										<tr>
											<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="role_departmentNames" onclick="checkAll(this);"/>部门名称</th>
											<th style="text-align:left; padding-left:4px;">角色</th>
											<th style="text-align:left; padding-left:4px;">权限</th>
										</tr>
									</thead>
									<tbody>
										<s:iterator value="departmentRoleMap.keySet()" id="depart">
										<s:set id="listLength" value="departmentRoleMap.get(#depart).size"></s:set>
											<s:iterator value="departmentRoleMap.get(#depart)" id="roleList" status="index">
												<tr>
													<s:if test="#index.index==0">
													<td valign="top" rowspan="${listLength }">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><input type="checkbox" name="departmentsIds" value="${depart.id}" onclick="_click_one('role_departmentNames','departmentsIds');"/>${depart.name}<s:if test="containBranches&&!depart.branch">(${depart.subCompanyName})</s:if></li>
														</ul>
													</td>
													</s:if>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;">${roleList.name}(${roleList.businessSystem.name}<s:if test="containBranches">/${roleList.subCompanyName}</s:if>)</li>
														</ul>
													</td>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><a href="#" onclick="viewDetail(${depart.id},${roleList.id},'department');">详细</a></li>
														</ul>
													</td>
												</tr>
											</s:iterator>
										</s:iterator>
									</tbody>
								</table>
							</s:elseif>
							<!-- 工作组 -->
							<s:elseif test="queryType=='ROLE_WORKGROUP'&&workgroupRoleMap.size>0">
								<table border="1" id="sTable" cellpadding="0" cellspacing="0" class="leadTable" >
									<thead>
										<tr>
											<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="role_workgroupNames" onclick="checkAll(this);"/>工作组名称</th>
											<th style="text-align:left; padding-left:4px;">角色</th>
											<th style="text-align:left; padding-left:4px;">权限</th>
										</tr>
									</thead>
									<tbody>
									<s:iterator value="workgroupRoleMap.keySet()" id="workgroup">
										<s:set id="listLength" value="workgroupRoleMap.get(#workgroup).size"></s:set>
											<s:iterator value="workgroupRoleMap.get(#workgroup)" id="roleList" status="index">
												<tr>
													<s:if test="#index.index==0">
													<td valign="top" rowspan="${listLength }">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><input type="checkbox" name="workgroupIds" value="${workgroup.id}" onclick="_click_one('role_workgroupNames','workgroupIds');"/>${workgroup.name}<s:if test="containBranches">(${workgroup.subCompanyName})</s:if></li>
														</ul>
													</td>
													</s:if>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;">${roleList.name}(${roleList.businessSystem.name}<s:if test="containBranches">/${roleList.subCompanyName}</s:if>)</li>
														</ul>
													</td>
													<td valign="top">
														<ul style="list-style-type:none;margin:0;padding-left: 0px;">
															<li style="margin: 3px;"><a href="#" onclick="viewDetail(${workgroup.id},${roleList.id},'workgroup');">详细</a></li>
														</ul>
													</td>
												</tr>
										</s:iterator>
									</s:iterator>
									</tbody>
								</table>
							</s:elseif>
						</div>
					</aa:zone>
				</div>
			</div>
</div>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</body>
</html>
	