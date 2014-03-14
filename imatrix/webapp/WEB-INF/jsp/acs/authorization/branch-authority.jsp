<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<link href="${resourcesCtx}/widgets/colorbox/colorbox.css" rel="stylesheet" type="text/css"/>
	<script src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js" type="text/javascript"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>
	<script src="${resourcesCtx}/js/staff-tree.js" type="text/javascript"></script>
	<script src="${resourcesCtx}/js/custom.tree.js" type="text/javascript"></script>
	<link   type="text/css" rel="stylesheet" href="${acsCtx}/css/custom.css" />
      
	<script type="text/javascript">
		//通用消息提示
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
		function addRole(){
			$.ajax({
				data:{branchesId:$("#branchesId").val()},
				type:"post",
				url:webRoot+"/authorization/branch-authority-validateAuthority.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						init_colorbox(webRoot+"/authorization/branch-authority-addRole.action?selectPageFlag="+true+"&branchesId="+$("#branchesId").val(),"添加角色",600,500);
					}else{
						alert("无权限为此分支机构添加角色！");
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}

		//添加管理员
		function addBranchesManager(){
			$.ajax({
				data:{branchesId:$("#branchesId").val()},
				type:"post",
				url:webRoot+"/authorization/branch-authority-validateAuthority.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						openBranchesManagerTree();
					}else{
						alert("无权限为此分支机构添加管理员！");
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}

		function openBranchesManagerTree(){
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
						title: "选择人员",
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
				data:{userIds:$("#_userIds").val(),branchesId:$("#branchesId").val()},
				type:"post",
				url:webRoot+"/authorization/branch-authority-addManager.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ALLCOMPANYID"==data){
						showMessage("message", "<font class=\"onError\"><nobr>不能把公司中的所有用户添加为分支机构管理员</nobr></font>");
					}else if("ok"!=data){
						showMessage("message", "<font class=\"onError\"><nobr>"+data+"</nobr></font>");
					}
					ajaxSubmit('defaultForm', webRoot+'/authorization/branch-authority.action', 'acs_content',getAddManagerContentHeight);
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
			*/
			$.ajax({
				data:{userIds:ztree.getIds(),branchesId:$("#branchesId").val()},
				type:"post",
				url:webRoot+"/authorization/branch-authority-addManager.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ALLCOMPANYID"==data){
						showMessage("message", "<font class=\"onError\"><nobr>不能把公司中的所有用户添加为分支机构管理员</nobr></font>");
					}else if("ok"!=data){
						showMessage("message", "<font class=\"onError\"><nobr>"+data+"</nobr></font>");
					}
					ajaxSubmit('defaultForm', webRoot+'/authorization/branch-authority.action', 'acs_content',getAddManagerContentHeight);
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
		}

		function getAddManagerContentHeight(){
			var h = $('.ui-layout-center').height();
			$("#opt-content").css("height",h-65); 
		}
		
		function checkAll(obj){
			if($(obj).attr('id') == 'branchAuthority_users'){
				checkEveryOne(obj, 'userIds');
			}else if($(obj).attr('id') == 'branchAuthority_roles'){
				checkEveryOne(obj, 'roleIds');
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

		function clearAway(){
			$.ajax({
				data:{branchesId:$("#branchesId").val()},
				type:"post",
				url:webRoot+"/authorization/branch-authority-validateAuthority.action",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						var userIdsLength=$("input[name=userIds]:checked").length;
						var roleIdsLength=$("input[name=roleIds]:checked").length;
						if(userIdsLength==0&&roleIdsLength==0){
							alert("请选择移除项！");
						}else{
							if(confirm("确定要移除所选项?")){
								$.ajax({
									data:{userIds:getSelectItems("userIds"),roleIds:getSelectItems("roleIds"),branchesId:$("#branchesId").val()},
									type:"post",
									url:webRoot+"/authorization/branch-authority-clearAway.action",
									beforeSend:function(XMLHttpRequest){},
									success:function(data, textStatus){
										showMessage("message", "<font class=\"onSuccess\"><nobr>"+data+"</nobr></font>");
										ajaxSubmit('defaultForm', webRoot+'/authorization/branch-authority.action', 'acs_content');
									},
									complete:function(XMLHttpRequest, textStatus){},
							        error:function(){
						
									}
								});
							}
						}
					}else{
						alert("无权限移除此分支机构下的人员和角色！");
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){
	
				}
			});
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
</head>

<body onload="getContentHeight();">
<div class="ui-layout-center">
<div class="opt-body">
	<s:if test="branchesId != null">	
		<form id="defaultForm" name="defaultForm"action="" method="post" >
			<input type="hidden" id="branchesId" name="branchesId" value="${branchesId }"/>
			<input type="hidden" id="_userIds" />
			<input type="hidden" id="_manageBranchesIds" value="${manageBranchesIds }" />
		</form>	
		<div class="opt-btn">
			<security:authorize ifAnyGranted="authorization-branch-authority-addManager"><button  class='btn' onclick="addBranchesManager();"><span><span>添加管理员</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="authorization-branch-authority-addRole"><button  class='btn' onclick="addRole();"><span><span>添加角色</span></span></button></security:authorize>
			<security:authorize ifAnyGranted="authorization-branch-authority-clearAway"><button  class='btn' onclick="clearAway();"><span><span>移除</span></span></button></security:authorize>
		</div>
		<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
		<aa:zone name="acs_content">
		<div id="opt-content">
			<div style="padding: 5px;">
				<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
					<thead>
						<tr>
							<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="branchAuthority_users" onclick="checkAll(this);"/>人员名称</th>
							<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="branchAuthority_roles" onclick="checkAll(this);"/>角色</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td valign="top">
								<ul style="list-style-type:none;margin:0;padding-left: 0px;">
								<s:iterator value="users">
								<li><input type="checkbox" name="userIds" value="${id}" onclick="_click_one('branchAuthority_users','userIds');"/>${name}(${subCompanyName })</li>
								</s:iterator>
								</ul>
							</td>
							<td>
								<ul style="list-style-type:none;margin:0;padding-left: 0px;">
								<s:iterator value="roles">
								<li><input type="checkbox" name="roleIds" value="${id}" onclick="_click_one('branchAuthority_roles','roleIds');"/><span>${name}(${businessSystem.name }/${subCompanyName })</span></li>
								</s:iterator>
								</ul>
							</td>
						</tr>
					</tbody>
				</table>
			</div>	
		</div>
		<style type="text/css">.leadTable{ background-color: #FCFDFD; } .leadTable tbody tr:hover, .leadTable tbody td:hover a{ background-color: #FCFDFD; } </style>
		</aa:zone>
	</s:if><s:else>
		<div style="height: 80px;"></div>
	    <div style="font-size: 20px;text-align: center;letter-spacing: 4px;">
	  		  请选择分支机构
	    </div>
	</s:else>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
	