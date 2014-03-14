<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
	  <div id="opt-content">
		<ul class="ztree" id="_department_tree"></ul>
			<script type="text/javascript">
			var split_one = "|#";
			var split_two = "==";
			var split_three = "*#";
			$(function () {
				$.ajaxSetup({cache:false});
				//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
				tree.initTree({treeId:"_department_tree",
					url:"${acsCtx}/organization/load-tree!loadDepartment.action?currentId=INITIALIZED",
					type:"ztree",
					callback:{
							onClick:selectDepartment
						}});
				
			});
			function selectDepartment(){
			    node = tree.getSelectNodeId();
			    var departmentName = window.parent.$("#departmentName").val();
			    //添加可以把一个用户的正职部门从一个分支机构的部门修改成另一个分支机构的部门，
			    //但是修改时提示用户必须从新选择兼职部门
			    var userSubCompanyId = window.parent.$("#subCompanyId").val();
			    var departmentSubCompanyId = node.substring(node.indexOf(split_three)+2,node.length);
			    window.parent.$("#departSubCompanyId").attr("value",departmentSubCompanyId);
			    var secondDepartSubCompanyId = window.parent.$("#secondDepartSubCompanyId").val();
			    var userLoginName = window.parent.$("#loginName").val();
			    var chooseDepartmentId = node.substring(node.indexOf(split_one)+2,node.indexOf(split_two));
			    var isCrossBranch=false;
			    if(node!='undefined'&&node!=''&&node.indexOf('USERSBYDEPARTMENT')>=0){
                    if(!isInSameBranch(userSubCompanyId,departmentSubCompanyId)||!departIsInSameBranch(departmentSubCompanyId,secondDepartSubCompanyId)){
                    	isCrossBranch=true;
                    	if( window.parent.$("#dids").val()!=''){
                    		alert("您跨分支机构选择了部门，请重新选择同属一个分支机构的兼职部门！");
                    		 //清空新兼职部门id
                            window.parent.$("#dids").attr("value","");
                            //清空兼职部门
                            window.parent.$("#departmentName").attr("value","");
                    	}
                    }
			    	var deptId = node.split(split_one)[1].split(split_two)[0];
					var name = node.split(split_two)[1].split(split_three)[0];
                    //兼职部门dids
					var partTimeDepartmentIds = window.parent.$("#dids").val();
					if(isHasRepeat(deptId,partTimeDepartmentIds)){
		                 alert(name+"已被选为兼职部门!");
		                 return;
					}

					$.ajax( {
	    				data : {
						  userLoginName : userLoginName,chooseDepartmentId:chooseDepartmentId,isCrossBranch:isCrossBranch
	    				},
	    				type : "post",
	    				url : "${acsCtx}/organization/user!validateLoginNameRepeat.action",
	    				beforeSend : function(XMLHttpRequest) {
	    				},
	    				success : function(data, textStatus) {
		    				if(data=='true'){
		    				   window.parent.$("#oneDid").attr("value",deptId);
	    	    			   window.parent.$("#mainDepartmentName").attr("value",name);
	    	    			   window.parent.$.colorbox.close();
		    				}else{
                               alert("您的用户名在新的分支机构内有重复，请重新选择部门!"); 
                               return; 
			    			}
	    				},
	    				error : function(XMLHttpRequest, textStatus) {
	    					alert(textStatus);
	    				}
	    			});
					
			    }else{
			    	 window.parent.$("#departSubCompanyId").attr("value","");
			    	 alert('请选择部门！');
			    	 return; 
				}
		    }
		    //验证主职部门和兼职部门是否在同一分支机构
		    function departIsInSameBranch(mainSubId,secondSubId){
		    	if(secondSubId==''){
                   return true;
			    }else{
                   if(mainSubId=='null'&&isUserIdNull(secondSubId)){
                         return true;
                   }else if(mainSubId!='null'&&!isUserIdNull(secondSubId)){
                	    var strArr = secondSubId.split(",");
						for(i=0;i<strArr.length;i++){
                           if(mainSubId!=strArr[i]){
                             return false;
                           }
						}
						return true;
                   }else {
                         return false; 
                   }
				}
			}
		    //验证用户和主职部门是否在同一分支机构
		    function isInSameBranch(userSubId,departSubId){
			    var userId = window.parent.$("#id").val();
			    if(userId!=''){//修改时
			    	if(userSubId==''&&departSubId!='null'){
		                return false;
					}else if(userSubId!=''&&departSubId=='null'){
						return false;
					}else if(userSubId!=''&&departSubId!='null'){
						if(userSubId==departSubId){
	                         return true;
						}else{
	                         return false;
						}
					}else{
		                  return true;
					} 
			    }else{//新建
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
		    function isHasRepeat(target,testStr){
	         var test = testStr.split(",");
	         var flag = false;
	         for(var i=0;i<test.length;i++){
	            if(test[i]==target) flag=true;   
	         }
	         return flag;
			}
			</script>
		</div>
  </div>
</div>	
</body>
</html>
