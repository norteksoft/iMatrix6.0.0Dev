<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
    <script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript">
		function createLoadBill(){
			openLoadBill();
		}
		function deleteLoadBill(){
			if($('#_folderId').attr('value') == ''){
				alert("请选择左栏的文件夹！");
				return;
			}
			aa.submit('content','${ctx}/loan-bill/loan-bill-delete.htm?id='+$("#_folderId").attr("value"),'main',backTo); 
		}
		function openLoadBill(id,sign){
			var url='${ctx}/loan-bill/loan-bill-input.htm';
			var parentFolderId = $("#_folderId").attr("value");
			if(id!=undefined){
				url=url+'?id='+id+'&parentFolderId='+parentFolderId+'&updateSign='+true;
			}else{
				url=url+'?parentFolderId='+parentFolderId;
			}
			$.colorbox({href:url,iframe:true, innerWidth:400, innerHeight:200,
				overlayClose:false,
				onClosed:function(){
					jQuery("#expenseReportList").trigger("reloadGrid");
				},
				title:"新建文件夹"
				});
		}

		function backTo(){
			window.location="${ctx}/loan-bill/loan-bill-list.htm";
		}

		function updateLoadBill(id,sign){
			if(id==null||id==''){
				alert("请选择左栏的文件夹！");
			}else{
				openLoadBill(id,true);
			}
		}

		function companyTree(){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'COMPANY',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"userName",
//				showInputId:"userName",
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "COMPANY",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {

					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
								showInput:"userName",
				                hiddenInput:"userName",
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function userAndDeptTree(){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"userName",
//				showInputId:"userName",
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
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
					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
								showInput:"userName",
				                hiddenInput:"userName",
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function groupTree(){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'GROUP_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"groupName",
//				showInputId:"groupName",
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
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
					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
								showInput:"groupName",
				                hiddenInput:"groupName",
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function deptAndGroupTree(){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'DEPARTMENT_WORKGROUP_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"groupName",
//				showInputId:"groupName",
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "DEPARTMENT_WORKGROUP_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {

					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
								showInput:"groupName",
				                hiddenInput:"groupName",
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}
		function deptTree(){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:"deptName",
//				showInputId:"deptName",
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
			var zTreeSetting={
					leaf: {
						enable: false
					},
					type: {
						treeType: "DEPARTMENT_TREE",
						noDeparmentUser:true,
						onlineVisible:false
					},
					data: {

					},
					view: {
						title: "选择人员",
						width: 300,
						height:400,
						url:acsSystemUrl,
						showBranch:false
					},
					feedback:{
						enable: true,
								showInput:"deptName",
				                hiddenInput:"deptName",
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		//表单测试新建
		function createTestForm(){
			ajaxSubmit('contentForm','${ctx}/loan-bill/test-form-input.htm' , 'main',testFormValidate); 
		}
		
		//表单测试验证
		function testFormValidate(){
			$("#contentForm").validate({
				submitHandler: function() {
					aa.submit('contentForm','','main',saveCallBack);
				}
			});
		}

		function saveCallBack(){
			testFormValidate();
			showMsg();
		}
		//编辑
		function editTestForm(){
			var ids=jQuery("#main_table").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要编辑的记录！");
				return;
			}else if(ids.length>1){
				alert("请不要选择多条记录！");
				return;
			}
			ajaxSubmit("contentForm", "${ctx}/loan-bill/test-form-input.htm?id="+ids[0], "main",testFormValidate);
			
		}
		//表单测试保存提交
		function submitTestForm(url){
				$('#contentForm').attr('action',url);
				$('#contentForm').submit();
			}
		
		
		function importData(url){
			$.colorbox({href:url,iframe:true, innerWidth:300, innerHeight:100,overlayClose:false,title:'导入'});
		}

		function insertData(url){
		$.ajax({
			type: "POST",
			url: url,
			success: function(data){
				alert(data);
			}
		});

	}
//ztree动态
	function createLoadBillZtree(){
		openLoadBillZtree();
	}
	function openLoadBillZtree(id,sign){
		var url='${ctx}/loan-bill/loan-bill-input.htm';
		var parentFolderId = $("#_ztree_folderId").attr("value");
		if(id!=undefined){
			parentFolderId = parentFolderId.split("_")[1];
			id = id.split("_")[1];
			url=url+'?id='+id+'&parentFolderId='+parentFolderId+'&updateSign='+true+'&isZtree=true';
		}else{
			parentFolderId = parentFolderId.split("_")[1];
			url=url+'?parentFolderId='+parentFolderId+'&isZtree=true';
		}
		$.colorbox({href:url,iframe:true, innerWidth:400, innerHeight:200,
			overlayClose:false,
			onClosed:function(){
			},
			title:"新建文件夹"
			});
	}

	function updateLoadBillZtree(id,sign){
		if(id==null||id==''){
			alert("请选择左栏的文件夹！");
		}else{
			openLoadBillZtree(id,true);
		}
	}

	function deleteLoadBillZtree(){
		if($('#_ztree_folderId').attr('value') == ''){
			alert("请选择左栏的文件夹！");
			return;
		}
		var id = $('#_ztree_folderId').attr('value').split("_")[1];
		aa.submit('content','${ctx}/loan-bill/loan-bill-delete.htm?id='+id,'main',backToZtree); 
	}

	function backToZtree(){
		createDynamicZtree();
	}
	
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="loanBill";
		var thirdMenu="autority-tag";
	</script>
	<%@ include file="/menus/header.jsp"%>
	<%@ page contentType="text/html;charset=UTF-8"%>
	<%@ include file="/common/taglibs.jsp"%>
	<div id="secNav">
		<menu:secondMenu code="ems"></menu:secondMenu>
		<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
		<script>
		    $("#expenseReport").removeClass('sec-selected');
			$("#loanBill").addClass('sec-selected');
		</script>
	</div>
	<div class="ui-layout-west">
		<%@ include file="/menus/loan-bill-menu.jsp"%>
	</div>
	<form  id="content" name="content" method="post" action=""></form>
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<strong>自定义权限标签</strong>
					</div>
					<div id="opt-content" >
					<form  id="contentForm" name="contentForm" method="post" action=""></form>
					<span>您有资源编号为：loan-bill-have-authority的权限,没有loan_bill_no_authority的权限！</span>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
</html>