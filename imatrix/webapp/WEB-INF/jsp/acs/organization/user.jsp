<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title>用户管理</title>
	<script  type="text/javascript" src="${imatrixCtx}/widgets/calendar/WdatePicker.js"></script>
	
	<script type="text/javascript">
		//新建
		function addUser(id, url_, opt) {
			$("#ajaxId").attr("value","");
			checkUserRegister(id, url_, opt);
		}

		function checkUserRegister(id, url_, opt) {
			$.ajax( {
				data : {
					weburl : url_
				},
				type : "post",
				url : "${acsCtx}/organization/user!checkUserRegister.action",
				beforeSend : function(XMLHttpRequest) {
				},
				success : function(data, textStatus) {
					if (data == '1') {
						alert('<s:text name="user.userAlert"/>');
					} else {
						var deId = $("#departId").val();
						var oldType = $("#oType").val();
						if(oldType!='USERSBYDEPARTMENT'&&oldType!='USERSBYBRANCH'){
							$("#departId").attr("value",'');
						}
						$("#ajax_from").attr("action", data);
						ajaxAnywhere.formName = "ajax_from";
						ajaxAnywhere.getZonesToReload = function() {
							return "acs_content";
						};
						ajaxAnywhere.onAfterResponseProcessing = function() {
							HideSearchBox();
							var deId = $("#departId").val();
							var oldType = $("#oType").val();
							var bId = $('#brcheId').val();

							if(bId==''){
								bId = $("#fromBracnhId").val();
							}
							
							$("#oneDid").attr("value",deId);
							$("#oldDid").attr("value",deId);
							$("#oldType").attr("value",oldType);
							$("#bId").attr("value",bId);
							ruleInput();
							getContentHeight();
						};
						ajaxAnywhere.submitAJAX();
					}
				},
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
			});
		}
		
		//名称是否不包含下等号(=)、竖线(|)、加号(+)、波浪线(~),包括时返回false,不含时返回true
		function validateName(name){
			if(name.indexOf("=")>=0||name.indexOf("|")>=0
			    ||name.indexOf("+")>=0||name.indexOf("~")>=0
			    ||name.indexOf("@")>=0||name.indexOf("#")>=0
			    ||name.indexOf("$")>=0||name.indexOf("%")>=0
				||name.indexOf("^")>=0||name.indexOf("&")>=0
				||name.indexOf("!")>=0||name.indexOf("~")>=0
				||name.indexOf("(")>=0||name.indexOf(")")>=0
				||name.indexOf("<")>=0||name.indexOf(">")>=0
				||name.indexOf("?")>=0||name.indexOf("/")>=0
				||name.indexOf("*")>=0){
				
				return false;
			}
			return true;
		}
		function isAllNumber(name){
			 re = new RegExp("[0-9]","g");
			 var j=0;
			 for(var i=0;i<name.length;i++){
                 var unit = name.charAt(i);
                 if(unit=='-'||unit=='_')j++;
                 if(unit.match(re)!=null){
                     j++;
                 }
		     }
		     if(name.length==j){
			     return true;
		     }else{
                 return false;
			 }
		}
		function ruleInput(){
    		$("#inputForm").validate({
    			submitHandler: function() {
    			//先登录名验证
    			var mainDepartmentId = $("#departSubCompanyId").val();
    			var bId = $('#brcheId').val();
    			var fromBracnhId="${fromBracnhId}";
    			var validateLoginName = $("#loginName").val();
    			$.ajax( {
    				data : {
    					id:$("#id").attr("value"),dscId:mainDepartmentId,validateLoginName:validateLoginName,fromBracnhId:fromBracnhId,branId:bId
    				},
    				type : "post",
    				url : "${acsCtx}/organization/user!validateEamil.action",
    				beforeSend : function(XMLHttpRequest) {
    				},
    				success : function(data, textStatus) {
    					if(data=="loginNameFlase"){
    						 alert("此登录名已被注册！");
        			    }else{
    						if(!validateName($("#loginName").val())){
    			    			$("#loginName").parent().append('<label  class="error">登录名必填,登录名中不能包含除下划线(_)、横线(-)外的特殊字符，也不能全是数字！</label>');
    		    			}
    		    			//else if(!validateName($("#trueName1").val())){
    			    		//	$("#trueName1").parent().append('<label  class="error">姓名、登录名必填且用户名、登录名中不能包含下划线(_)、等号(=)、横线(-)、竖线(|)、加号(+)、波浪线(~)</label>');
    				    	//}
    				    	else if($("#mailboxDeploy").val()==''){
    							alert("请选择邮箱配置！");
    					    }else{
        					    if(data=="loginNameMessage"){
            					    if(confirm("你的登录名在其他分支机构也存在，确定保存吗？")){
    		    			 	      saveUser(); 
            					    }
        					    }else{
        					    	 saveUser(); 
            					}
    		    			}
    					}
    				},
    				error : function(XMLHttpRequest, textStatus) {
    					alert(textStatus);
    				}
    			});

        			
	    			
    			},
				rules: {
	    			passwordConfirm: {
	    				required: true,
	    				equalTo: "#password"
	    			},
	    			telephone: {
	    				digits: true
	    			},
	    			high: {
	    				number: true
	    			},
	    			weight: {
	    				number: true
	    			},
	    			IDcard: {
	    				creditcard: true
	    			},
	    			homePostCode: {
	    				digits: true
	    			},
	    			matePostCode: {
	    				digits: true
	    			},
	    			mateTelephone: {
	    				digits: true
	    			},
	    			FMPostCode: {
	    				digits: true
	    			}
			     },
				   messages: {
			    	 'user.loginName':"必填",
			    	 'user.name':"必填",
			    	'user.password':"必填",
			    	passwordConfirm:{
			    	 	required:"必填",
			    	 	equalTo:"密码不一致"
			     	},
			    	'user.email':{
			    	 	required:"必填",
			    	 	email : "请输入正确的邮件地址"
				    },
					'user.mailSize':{
				    	required:"必填",
				    	number : "请输入8位以下的数字"
					},
					'IDcard':{
						creditcard:"请输入合法的身份证号码"
					},
					'homePostCode':{
						digits:"请只输入数字"
					},
					'matePostCode':{
						digits:"请只输入数字"
					},
					'mateTelephone':{
						digits:"请只输入数字"
					},
					'FMPostCode':{
						digits:"请只输入数字"
					}
				}
			});
		}

		//修改页面提交方法
		function submitForm() {
			$('#inputForm').submit();
		}

		function saveUser(){
			var departmentIds=$("#dids").attr("value");
			var departmentId=$("#deId").attr("value");
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    //如果正职部门的名称是空，表示没有选择正职部门，则设置正职部门id控件为空
		    var mainDeptName = $("#mainDepartmentName").val();
		    if(mainDeptName==""||typeof(mainDeptName)=='undefined'){
		    	$("#oneDid").attr("value","");
		    }
		    $("#oldDid").attr("value",i);
		    $("#oldType").attr("value",t);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				ruleInput();
			    $('#message').show();
			    setTimeout("$('#message').hide()",3000);
			    getContentHeight();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//取消
		function cancel(){
		    var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    if(t=='DELETED_USER'){
			    $('#cancelForm').attr('action', '${acsCtx}/organization/user!deleteList.action');
			}
			if(t=='USERSBYDEPARTMENT'){
				var url=$('#cancelForm').attr('action');
				url+="?oldType="+t;
				$('#cancelForm').attr('action', url);
			}
			ajaxAnywhere.formName = "cancelForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
				$('input').attr('disabled', '');
				$('select').attr('disabled', '');
				//initUserTable();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//保存用户状态
		function saveUserState(){
			setPageState();

			 var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    $("#oldType").attr("value",t);
			
			var webroot="${acsCtx}";
			var enable = $("input[name='_states_enable']:checked").val();
			var accountUnlock = $("#accountUnlock").attr("checked");
			var result = "";
			if(accountUnlock==true){
				result="accountUnLock";
			}else{
				result="accountLock";
			}
			result=result+","+enable;
			$("#states").attr("value",result);
			
			 var url=webroot+"/organization/user!saveUserState.action";		 
			$("#inputForm").attr("action",url);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		function cancelUserState(){
			setPageState();
			var i=$("#departId").val();
		    var t=$("#oType").val();
		    var b=$('#brcheId').val(); 
		    $("#departmId").attr("value",i);
		    $("#departmType").attr("value",t);
		    $("#branchId").attr("value",b);
		    $("#oldType").attr("value",t);
		    
		    var url="${acsCtx}/organization/user.action";		 
			$("#inputForm").attr("action",url);
			ajaxAnywhere.formName = "inputForm";
			ajaxAnywhere.getZonesToReload = function() {
				return "acs_content";
			};
			ajaxAnywhere.onAfterResponseProcessing = function() {
				initButtonGroup();
			};
			ajaxAnywhere.submitAJAX(); 
		}

		//ajax提交方法
		//修改
		function opt(url_, opt, id,id2) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			var bId = $('#brcheId').val();
			if(uIds==''){
				alert("请先选择");
			}else if(uIds.length > 1){
				 alert("只能选择一条！");
			}else{
				if(opt!="LOOK"){
					$("#ajaxId").val(uIds);
					$("#edit").val(opt);
					$("#look").val(opt);
				}else{
					$("#ajaxId").val(id);
					$("#look").val(opt);
				}
				if(id!=''){
					$("#ajax_from").attr("action", url_+'?did='+id);
				}else{
					$("#ajax_from").attr("action", url_);
				}
				ajaxAnywhere.formName = "ajax_from";
				ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				};
				ajaxAnywhere.onAfterResponseProcessing = function() {
					HideSearchBox();
					$("#look").attr("value","");
					if(opt=="LOOK"){
						$('input').attr('disabled', 'disabled');
						$('select').attr('disabled', 'disabled');
					}else{
						var bId = $('#brcheId').val();
						$("#bId").attr("value",bId);
						ruleInput();
					}
					getContentHeight();
				};
				ajaxAnywhere.submitAJAX();
			}
		}

		//删除用户
		function opt_delete(url_, opt) {
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				if(confirm("确认移除吗？")){
					setPageState();
					ajaxSubmit("ajax_from",webRoot+"/organization/user!falseDelete.action?ids="+uIds+"&departmType="+$("#oType").val(),"acs_list");
				}
			}
		} 

		function initButtonGroup(){
			initUpdateBtnGroup();
			initExportBtnGroup();
		}

		//验证是否启动LDAP集成
		function ldapValidate(){
			$.ajax({
				   type: "POST",
				   url: "${acsCtx}/organization/user!validateLdapStart.action",
				   data:{},
				   success: function(data, textStatus) {
					   if( data == "true" ){
						   ldapSynchronous();
						}else{   
			            	alert('请配置LDAP信息 并启动LDAP集成');
						}
			      },
			      error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}
		function ldapSynchronous(){
			$.ajax({
				   type: "POST",
				   url: "${acsCtx}/organization/user!synchronous.action",
				   data:{},
				   success: function(data, textStatus) {
					   parent.reinitUserTree();
					   alert(data);
					},
					error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}

		//导入
		function importUser(){
			$.colorbox({href:'${acsCtx}/organization/user!showImportUser.action',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入用户"});
		} 

		//导出
		function exportUser(){
			$("#ajax_from").attr("action","${acsCtx}/organization/user!exportUser.action");
			$("#ajax_from").submit();
		}  

		//选择
		function Dtree2(treeStyle){
			if(treeStyle=='multiple'){
				$.colorbox({href:'${acsCtx}/organization/user!chooseDepartments.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
			}else{
				$.colorbox({href:'${acsCtx}/organization/user!chooseOneDepartment.action?type=old',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
			}
		}

		function checkLoginPassword(pass) {
			$.ajax({
				   type: "POST",
				   url: "user!checkLoginPassword.action",
				   data:{orgPassword:pass.value},
				   success: function(msg, textStatus){
					   if(msg!=""){
						   alert(msg);
						   $("#password").val("");
						   $("#password").blur();
					   }
			      },
					error : function(XMLHttpRequest, textStatus) {
						alert(textStatus);
					}
			  }); 
		}

		/**
		 *修改密码
		 *liudongxia
		 */
	  	function modifyPassWord(id,url_) {
		  	if(versionType=="online"){
		  		demonstrateOperate();
		  	}else{
		  		$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:640, innerHeight:150,overlayClose:false,title:"修改密码"});
		  	}
	  		//$.colorbox({href:url_+'?id='+id,iframe:true, innerWidth:500, innerHeight:160,overlayClose:false,title:"修改密码"});
	  	}

	  	/**
		 *密码弹框”确定“按钮,设置密码
		 *liudongxia
		 */
	  	function setPassWord(password) {
	  		$("#password").attr("value",password);
	  		$("#passWordChange").attr("value","yes");
	  	}
	  	
	  	function shiftCheckbox(obj) {
			//checkset = document.getElementsByName("states");
			//if(indexs==0){
			//	checkset[1].checked = false;
			//}else{
			//	checkset[0].checked = false;
			//}
	  	  var objValue = $(obj).attr("checked");
          if(objValue==true){
       	   $(obj).attr('value','invocation');
          }else{
       	   $(obj).attr('value','enabled');
          }
		}
		function shiftCheckboxThree(obj){
           var objValue = $(obj).attr("checked");
           if(objValue==true){
        	   $(obj).attr('value','accountNonExpired');
           }else{
        	   $(obj).attr('value','accountNonExpiredNotChecked');
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
			ajaxSubmit("ajax_from", webRoot+'/organization/user!inputLook.action', "acs_content",_click_fun_callback);
		}

		function _click_fun_callback(){
			getContentHeight();
			initButtonGroup();
		}

		function checkUserName(pass) {
	        var temp_Name = $("#temp_Name").val();
	        if(temp_Name==pass.value){
	        }else{
	        	$.ajax({
	 			   type: "POST",
	 			   url: "user!checkUserName.action",
	 			   data:{userName:pass.value},
	 			   success: function(data, textStatus){
	 				   if( data == "true" ){
	 	 				   
	 	 			   }else{   
	 		            	$("#loginName").val("");
	 		            	$("#loginName").blur();
	 		            	alert(data+'<s:text name="user.registerAlready"/>');
	 					}
	 			    
	 		      },
	 				error : function(XMLHttpRequest, textStatus) {
	 					alert(textStatus);
	 				}
	 		  }); 
	        }
			
		}

		function clearInput(inputId){
			$("#"+inputId).attr("value","");
		}

		function unlockUser(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				if(confirm("确认解锁吗？")){
					$.ajax({
						   type: "POST",
						   url: "user!unlockUser.action?ids="+uIds,
						   success: function(data, textStatus){
									jQuery("#main_table").jqGrid().trigger("reloadGrid"); 
					                alert(data);
					                return;
						},error : function(XMLHttpRequest, textStatus) {
								alert(textStatus);
						}
					});
				}
			}
		}
		//显示提示信息，3秒后隐藏
		function showMsg(id,time){
			if(id==undefined)id="message";
			$("#"+id).show();
			if(time==undefined)time=3000;
			setTimeout('$("#'+id+'").hide();',time);
		}	
        //批量更换主职部门
		function changeMainDepartment(){
			 var uIds = jQuery("#main_table").getGridParam('selarrrow');
				if(uIds.length==0){
					 $("#notice").html("<span style='color: red;'>请选择用户！</span>");
					 $("#notice").children("span").fadeOut(5000);
					 return;
				}else{
					$.colorbox({href:'${acsCtx}/organization/user!toDepartmentToUsersDel.action?ids='+uIds+'&fromChangeMainDepartment=true',iframe:true, innerWidth:600, innerHeight:500,overlayClose:false,title:"请选择部门"});
				}
		}
		function changeBatchUserMainDepartment(deptId){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				 alert("请先选择");
			}else{
				var userIds = "";
				for(var i=0;i<uIds.length;i++){
                   userIds+=uIds[i]+",";
				}
				 userIds=userIds.substring(0,userIds.length-1);
				 var departmentId = $("#departId").val();
			     $("#formName").attr("action","${acsCtx}/organization/user!batchChangeUserMainDepartment.action?ids="+userIds+"&newMainDepartmentId="+deptId+"&departmentId="+departmentId);
		         ajaxAnywhere.formName = "formName";
				 ajaxAnywhere.getZonesToReload = function() {
					return "acs_content";
				 };
				 ajaxAnywhere.onAfterResponseProcessing = function () {
					 initButtonGroup();
				 };
				 ajaxAnywhere.submitAJAX();
			}
		}
		//提交为工作组添加的用户
		function workgroupAddUserSubmit(){
			var lists =allUsers("user_tree") ;
			if(lists.length <= 0){
				alert('<s:text name="user.seleteUser"/>');
				return;
			}
			var hasEffectiveUser = false;
			for(var i=0; i<lists.length; i++){
				    var type=lists[i].substring(0,lists[i].indexOf("_"));
				  if(type=="user"){  
					var parentLi = lists[i].substring(lists[i].indexOf("~")+1,lists[i].length);
					var parentDeptId = $('#tree_selected_id').attr('value');
					if(parentLi == parentDeptId){
						continue;
					}
					var userId= lists[i].substring(lists[i].indexOf("_")+1,lists[i].indexOf("="));
					if(userId.length > 0){
						var inpt = document.createElement("input");
						inpt.setAttribute("name", "userIds");
						inpt.setAttribute("value", userId);
						inpt.setAttribute("type", "hidden");
						document.getElementById("workgroupAddUserForm").appendChild(inpt);
						hasEffectiveUser = true;
					}
				  }
			}
			if(hasEffectiveUser){
				$('#workgroupAddUserForm').submit();
			}else alert('所选用户已在该部门');
		}

		
		//验证用户邮箱唯一
		/*
		function validateEmail(){
			$.ajax( {
				data : {
					userEmail : $("#email").attr("value"),id:$("#id").attr("value")
				},
				type : "post",
				url : "${acsCtx}/organization/user!validateEamil.action",
				beforeSend : function(XMLHttpRequest) {
				},
				success : function(data, textStatus) {
					if(data=="false"){
						alert("此邮箱地址已被注册！");
					}
				},
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
			});
		}
		*/
		function clearUser(){
			var uIds = jQuery("#main_table").getGridParam('selarrrow');
			if(uIds==''){
				alert('<s:text name="common.selectOne"/>');
				return;
			}else{
				if(confirm("确认删除吗？")){
					$.ajax({
						   type: "POST",
						   url: "user!checkIsAdmin.action?ids="+uIds,
						   success: function(data, textStatus){
							   if( data == "yes" ){
					                alert('<s:text name="common.delete.info"/>');
					                return;
								}else{
									setPageState();
									ajaxSubmit("ajax_from",webRoot+"/organization/user!clearUser.action?ids="+uIds+"&departmType="+$("#oType").val()+"&branchId="+$("#brcheId").val(),"acs_list");
								}
						},error : function(XMLHttpRequest, textStatus) {
								alert(textStatus);
						}
					});
				}
			}
		}

		//更新用户缓存
		function updatUserCache(){
			ajaxSubmit("defaultForm",  "${acsCtx}/organization/update-user-cache.action","",updatUserCacheCallback);
		}

		function updatUserCacheCallback(){//notice
			$('#notice').css("display","block");
			$('#notice').html("<font class=\"onSuccess\"><nobr>更新用户缓存成功</nobr></font>");
			setTimeout('$("#notice").css("display","none");',3000);
		}

		/**下拉按钮效果 ****/
		function initUpdateBtnGroup(){//默认按钮效果  
			$("#parentUpdateBtn")
					.button()
					.click(function() {
						}).next()
						.button( {
							text: false,
							icons: {
								primary: "ui-icon-triangle-1-s"
							}
						})
						.click(function() {
							removeSearchBox();
							$("#exportbtn").hide();
							showUpdateBtnDiv();
						})
						.parent()
						.buttonset();

			}
		function initExportBtnGroup(){//默认按钮效果  
			$("#parentExportBtn")
				.button()
				.click(function() {
				})
				.next()
				.button( {
					text: false,
					icons: {
						primary: "ui-icon-triangle-1-s"
					}
				})
				.click(function() {
					removeSearchBox();
					$("#updatebtn").hide();
					showExportbtnDiv();
				})
				.parent()
				.buttonset();
			}
		
		function showUpdateBtnDiv(){//显示更多按钮效果位置  
			if($("#updatebtn").css("display")=='none'){
				$("#updatebtn").show();
				var position = $("#_updatebtn").position();
				$("#updatebtn").css("left",position.left+0);
				$("#updatebtn").css("top",position.top+24);
			}else{
				$("#updatebtn").hide();
			};
			$("#updatebtn").hover(
				function (over ) {
					$("#updatebtn").show();
				},
				function (out) {
					 $("#updatebtn").hide();
				}
			); 

		}

		function showExportbtnDiv(){//显示更多按钮效果  位置
			if($("#exportbtn").css("display")=='none'){
				$("#exportbtn").show();
				var position = $("#_exportbtn").position();
				$("#exportbtn").css("left",position.left+0);
				$("#exportbtn").css("top",position.top+24);
			}else{
				$("#exportbtn").hide();
			};
			$("#exportbtn").hover(
				function (over ) {
					$("#exportbtn").show();
				},
				function (out) {
					 $("#exportbtn").hide();
				}
			); 
		}

	</script>
</head>
<body>
<div class="ui-layout-center">
	<div class="opt-body">
		 <form id="ajax_from" name="ajax_from" action="" method="post">  
            <input type="hidden" name="userId" id="ajaxId" />
	        <input type="hidden" name="departmentIds" id="departId" value="${oldDid }" />
	        <input type="hidden" name="oType" id="oType" value="${oldType }" />
	        <input type="hidden" name="look" id="look" />
	        <input type="hidden" name="comy" id="comy" value="${comy }"/>
	        <input type="hidden" name="edit" id="edit" />
	        <input type="hidden" name="fromWorkgroup" id="fromWorkgroup" value="${fromWorkgroup}">
	        <input type="hidden" name="workGroupId" id="workGroupId" value="${workGroupId}">
	        <input type="hidden" id="brcheId" value="${branchId}">
	        <input type="hidden" id="fromBracnhId" value="${fromBracnhId}">
		</form>
		<form action="#" name="defaultForm" id="defaultForm"></form>
		<form action="${acsCtx}/organization/user!synchronous.action" id="ldapFormId"></form>
		<script type="text/javascript">
			$(document).ready(function() {
				initUpdateBtnGroup();
			});
		</script>
		<script type="text/javascript">
			$(document).ready(function() {
				initExportBtnGroup();
			});
		</script>
		<aa:zone name="acs_content">
			<s:if test="look==null">
			<div class="opt-btn">
				<security:authorize ifAnyGranted="query_queryUser">
				<button  id="searchButton" class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
				</security:authorize>
					<security:authorize ifAnyGranted="addUser">
					<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if> onclick="addUser('null','${acsCtx}/organization/user!input.action','ADD');"><span><span>新建</span></span></button>
					</security:authorize>
					<security:authorize ifAnyGranted="addUser">
					<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if> onclick="updatUserCache();"><span><span>更新用户缓存</span></span></button>
					</security:authorize>
					<s:if test="canEditUser">
						<security:authorize ifAnyGranted="addUser">
							<div class="btndiv" id="_updatebtn" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentUpdateBtn" <s:if test="canEditUser">onclick="opt(webRoot+'/organization/user!input.action','NEW')"</s:if>>
									<span class="ui-button-text">修改</span>
								</button>
								<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text">更多</span>
								</button>
							</div>
							<div id="updatebtn" class="flag" >
								<ul >
									<security:authorize ifAnyGranted="falseDelete">
										<s:if test='#versionType=="online"'>
											<li id="clear_department_sign" ><a href="#" onclick="demonstrateOperate();">移除部门</a></li>
										</s:if><s:else>
										    <s:if test='oldType=="USERSBYDEPARTMENT"'>
										    	<li id="clear_department_sign" ><a href="#" onclick="opt_delete('${acsCtx}/organization/user!falseDelete.action','NEW');">移除部门</a></li>
											</s:if>
										</s:else>
									</security:authorize>
									<security:authorize ifAnyGranted="userManager">
											<li ><a href="#"  onclick="opt('${acsCtx}/organization/user!userManger.action','NEW','${departmentId}');">启用/禁用</a></li>
									</security:authorize>
									<security:authorize ifAnyGranted="addUser">
											<li ><a href="#" onclick="unlockUser();">用户解锁</a></li>
									</security:authorize>
									<s:if test="departmType!='NODEPARTMENT'&&departmType!='NODEPARTMENT_USER'&&departmType!='NOBRANCH'">
										<security:authorize ifAnyGranted="acs_organization_changeMainDepartment">
											<li ><a href="#" onclick="changeMainDepartment();">更换正职</a></li>
								        </security:authorize>
							        </s:if>
								</ul>
							</div>
							<script type="text/javascript">
								$(document).ready(function() {
									if($("#departId").val() != ''){
										$( "#clear_department_sign" ).show();
									}else{
										$( "#clear_department_sign" ).hide();
									}
								});
							</script>
						</security:authorize>
					</s:if>
						
					<security:authorize ifAnyGranted="acs_clear_user">
						<s:if test='#versionType=="online"'>
							<button  class='btn' <s:if test="!canEditUser">style="display: none;"</s:if>  onclick="demonstrateOperate();"><span><span>删除</span></span></button>
						</s:if><s:else>
							<button  class='btn'  <s:if test="!canEditUser">style="display: none;"</s:if> onclick="clearUser();"><span><span>删除</span></span></button>
						</s:else>
			        </security:authorize>
					<security:authorize ifAnyGranted="acs_validateLdapStart">
					<button  class='btn'  <s:if test="!canEditUser">style="display: none;"</s:if> onclick="ldapValidate();"><span><span>同步LDAP</span></span></button>
					</security:authorize>
					
					<s:if test="canEditUser">
						<security:authorize ifAnyGranted="acs_organization_user_showImportUser">
							<div class="btndiv" id="_exportbtn" style="*top:-2px;">
								<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentExportBtn" <s:if test="canEditUser">onclick="importUser();"</s:if>>
									<span class="ui-button-text"><s:text name="user.import"/></span>
								</button>
								<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
									<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
									<span class="ui-button-text">更多</span>
								</button>
							</div>
							<div id="exportbtn" class="flag" >
								<ul>
									<security:authorize ifAnyGranted="acs_user_exportUser">
										<li><a href="#" onclick="exportUser();"><s:text name="user.export"/></a></li>
									</security:authorize>
								</ul>
							</div>
						</security:authorize>
					</s:if>
			</div>
			</s:if><s:else>
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
			</s:else>
			<div id="notice"> <s:actionmessage /> </div>	
			<div id="opt-content" >
			   <aa:zone name="acs_list">
				<form id="formName" name="formName" action="" method="post">
				     <input type="hidden" name="olDid" id="olDid" value="">
				     <input type="hidden" name="olType" id="olType" value="">
				     <input type="hidden" name="companyId" id="companyId" value="${companyId}">
				     <s:if test="containBranches">
					     <s:if test="comeFrom=='workgroup'">
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="userPage" code="ACS_USER_SUB_COMPANY" gridId="main_table"></view:jqGrid>
					     </s:if>
					     <s:else>
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="page" code="ACS_USER_SUB_COMPANY" gridId="main_table"></view:jqGrid>
					     </s:else>
				     </s:if><s:else>
				     	 <s:if test="comeFrom=='workgroup'">
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="userPage" code="ACS_USER" gridId="main_table"></view:jqGrid>
					     </s:if>
					     <s:else>
					     <view:jqGrid url="${acsCtx}/organization/user.action?workGroupId=${workGroupId }&departmentId=${departmentId}&departmentIds=${oldDid}&oType=${oldType }&departmType=${departmType }&branchId=${branchId}" pageName="page" code="ACS_USER" gridId="main_table"></view:jqGrid>
					     </s:else>
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
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
