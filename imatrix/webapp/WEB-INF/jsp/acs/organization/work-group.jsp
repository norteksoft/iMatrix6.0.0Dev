<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	<link rel="stylesheet" type="text/css" href="${resourcesCtx}/widgets/validation/cmxform.css"/>
	
	<script type="text/javascript">
		//新建
		function createWorkgroup(){                 
			ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!inputWorkGroup.action', 'acs_content', validateWork);
		}

		function validateWork(){
			$("#inputForm").validate({
				submitHandler: function() { saveWorkgroup(); },
				rules: {
				    code: "required",
				    name: "required",
				    description: {
	    				maxlength : 60
	    			}
    		     },messages: {
			    	'code':"必填",
			    	'name': "必填",
			    	description:{
			     		maxlength : "最长60字符"
				    }
				}
				});
		}
		
		//修改
		function updateWorkgroup(){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				ajaxSubmit('ajax_from', '${acsCtx}/organization/work-group!inputWorkGroup.action?id='+ids, 'acs_content', validateWork);
			}
		}

		//删除
		function deleteWorkgroup(){     
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else{
				if(confirm("确定删除工作组？")){
					$.ajax({
						data:{ides:ids.join(",")},
						type:"post",
						url:'${acsCtx}/organization/work-group!delete.action',
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							setPageState();
							ajaxSubmit("defaultForm",webRoot+'/organization/work-group.action?wfType='+$("#_wfType").val()+"&branchesId="+$("#branchesId").val(),"acs_content",refreshAfterDeleteWG);
							
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){
			
						}
					});
				}
			}
		}
		function refreshAfterDeleteWG(){
			//parent.jQuery.jstree._reference("#company_group").destroy();
			parent.initWorkgroupTree();
		}

		//增加用户
		function optAdd(id, opt){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				$("#id").attr("value",ids[0]);
				$("#_wg_Id").attr("value",ids[0]);
				$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!addWorkGroupToUsers.action");
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function () {
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//移除用户
		function optRemove(id, opt){
			var ids = jQuery("#main_table").getGridParam('selarrrow');
			if(ids==''){
				alert("请先选择");
			}else if(ids.length > 1){
				 alert("只能选择一条！");
			}else{
				$("#id").attr("value",ids[0]);
				$("#_wg_Id").attr("value",ids[0]);
				$("#ajax_from").attr("action", "${acsCtx}/organization/work-group!removeWorkGroupToUsers.action");
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function () {
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		function refreshWg(){
			//parent.jQuery.jstree._reference("#company_group").destroy();
			parent.initWorkgroupTree();
		}
		//工作组管理-增加用户-提交
		function submitForm(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('没有选中的用户！');
				return;
			}else{
				$("#ids").attr("value",uIds);
				var workGroupId = $("#_wg_Id").val();
				$("#inputForm").append("<input  type='hidden' name='workGroupId' value='"+workGroupId+"'> ");
				$("#inputForm").submit(); 
			}
		}

		function submitWorkgroup(){
			$('#inputForm').submit();
		}

		function saveWorkgroup(){
			var code = $("#workGroupCode").val();
			if(code.indexOf("role-")==0&&code.length>27){
				alert("默认编号时超出最大长度27");
				return;
			}
			if(code.indexOf(" ")==0||code.lastIndexOf(" ")==code.length-1){//最前面有空格
				alert("编码前后不能包含空格!");
				return;
			}
			if(code.indexOf(">")>=0||code.indexOf("<")>=0||code.indexOf("\"")>=0||code.indexOf("'")>=0
					||code.indexOf("/")>=0){//包含特殊字符
				alert("编码不能包含符号>、<、\"、'、/");
				return;
			}
			$.ajax({
				type : "POST",
				url : "work-group!checkWorkCode.action",
				data:{workGroupCode:$("#workGroupCode").val(),id:$("#_workgroupId").val(),workGroupName:$("#workGroupName").val(),branchesId:$("#subCompanyId").val()},
				success : function(data){
					if("ok"==data){
						ajaxSubmit('inputForm', '${acsCtx}/organization/work-group!saveWorkGroup.action', 'acs_content', saveCallback);
					}else if("codeNameRepeat"==data){
						alert("工作组编号重复！\n工作组名称重复！");
					}else if("codeRepeat"==data){
						alert("工作组编号重复！");
					}else if("nameRepeat"==data){
						alert("工作组名称重复！");
					}
				},
				error: function(){alert("错误");},
				onerror : "",
				onwait : "正在对工作组编号进行合法性校验，请稍候..."
			});
		}

		function saveCallback(){
			validateWork();
			//parent.jQuery.jstree._reference("#company_group").destroy();
			if($("#branchesId").val()!=''){
				parent.initWorkgroupTree("BRANCHES-"+$("#branchesId").val());
			}else{
				parent.initWorkgroupTree("COMPANY-"+$("#_companyId").val());
			}
		}
	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
	<input id="_wfType" type="hidden" value="${wfType }"> 
	<s:if test='wfType != "DEPARTMENT" && wfType != "" && wfType != null'>
		<input id="_companyId" type="hidden" value="${companyId }"> 
		<form action="" id="defaultForm" name="defaultForm" method="post"></form>
		<form id="ajax_from" name="ajax_from" action="" method="post">
			<input id="_wg_Id" type="hidden" name="workGroupId" value="${workGroupId}"> 
			<input id="branchesId" type="hidden" name="branchesId" value="${branchesId }"> 
		</form>
		<aa:zone name="acs_content">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="createWorkGroup">
				    <button  class='btn' onclick="createWorkgroup();"><span><span><s:text name="common.create" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="editWorkGroup">
				    <button  class='btn' onclick="updateWorkgroup();"><span><span><s:text name="common.alter" /></span></span></button>
				</security:authorize>
				<security:authorize ifAnyGranted="deleteWorkGroup">
				    <s:if test='#versionType=="online"'>
			        	<button  class='btn' onclick="demonstrateOperate();"><span><span><s:text name="common.delete" /></span></span></button>
			        </s:if><s:else>
				    	<button  class='btn' onclick="deleteWorkgroup();"><span><span><s:text name="common.delete" /></span></span></button>
			        </s:else>
				</security:authorize>
			</div>
			<div id="opt-content" >
				<form id="contentForm" name="contentForm" method="post">
				<view:jqGrid url="${acsCtx}/organization/work-group.action?branchesId=${branchesId }&wfType=${wfType }" pageName="page" code="ACS_WORKGROUPS" gridId="main_table"></view:jqGrid>
				</form>
			</div>
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
