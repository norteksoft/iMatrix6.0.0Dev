<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%@ page import="com.norteksoft.product.util.WebContextUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	  <%@ include file="/common/acs-iframe-meta.jsp"%>
	  <title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
  <div class="ui-layout-center">
  	<div class="opt-body">
		<div class="opt-btn" style="margin-bottom: 6px;">
			  <button class='btn' onclick="selectDepartment('_department_tree');"><span><span>提交</span></span></button>
		</div>
		<div id="opt-content">
			<ul class="ztree" id="_department_tree"></ul>
			<script type="text/javascript">
			var split_one = "|#";
			var split_two = "==";
			var split_three = "*#";
			//treeType:3 表示部门树，控制父子选中样式问题
			$(function () {
				$.ajaxSetup({cache:false});
				//treeId:,url:,data(静态树才需要该参数):,multiple:,callback:
				tree.initTree({treeId:"_department_tree",
					url:"${acsCtx}/organization/load-tree!loadDepartment.action?currentId=INITIALIZED",
					type:"ztree",
					treeType:3,
					multiple:true,
					chkboxType:{"Y": "s", "N": "s"}});
			});
			function selectDepartment(treeId){
				var arr = tree.getSelectNodeIds();
				var mainDepartmentName = window.parent.$("#mainDepartmentName").val();
				var mainDepartSubCompanyId = window.parent.$("#departSubCompanyId").val();
				var mainDepartmentId = window.parent.$("#oneDid").val();
				var userLoginName = window.parent.$("#loginName").val();
				var userId = window.parent.$("#uid").val();
					
				if(arr.length <= 0){
					alert('请选择部门！');
					return;
				}
				var departId="";
				var departName="";
				var departSubCompanyId="";
				var flag=false;
				for(var i=0; i<arr.length; i++){ //USERSBYDEPARTMENT-3978=火箭总师办
					var type = arr[i].split(split_one)[0]; 
					if(type=="USERSBYDEPARTMENT"){
						var name = arr[i].split(split_one)[1].split(split_two)[1].split(split_three)[0];
						var id = arr[i].split(split_one)[1].split(split_two)[0];
						if(mainDepartmentId!=id){
							departId += arr[i].split(split_one)[1].split(split_two)[0]+"=";
							departSubCompanyId += arr[i].split(split_three)[1]+",";
						    departName+=name+",";
						}else{
							flag=true;
	                        alert(mainDepartmentName+"已被选为正职部门!"); 
						}
					}
				}
				var departIds=departId.substring(0,departId.length-1);
				var departNames=departName.substring(0,departName.length-1);
				if(departIds==''&&!flag){
					alert('只能选择部门，请选择部门！');
					return;
				}else if(departIds==''&&flag){
                    return; 
				}
				departSubCompanyId = departSubCompanyId.substring(0,departSubCompanyId.length-1);
				if(!isInSameBranch(mainDepartSubCompanyId,departSubCompanyId)){
                    alert("您选择的兼职部门和正职部门不属同一分支机构，请重新选择！");
					return;
				}else if(mainDepartSubCompanyId==''&&departSubCompanyId!=''&&!isInSameBranchWhenOnlySub(departSubCompanyId)){
					 alert("您选择的兼职部门不属同一分支机构，请重新选择！");
					 return;
				}else{
					$.ajax( {
	    				data : {
						  userLoginName : userLoginName,chooseDepartmentIds:departIds,uusId:userId
	    				},
	    				type : "post",
	    				url : "${acsCtx}/organization/user!validateLoginNameRepeatByDepartIds.action",
	    				beforeSend : function(XMLHttpRequest) {
	    				},
	    				success : function(data, textStatus) {
		    				if(data=='true'){
		    				    window.parent.$("#dids").attr("value",departIds);
		    				    window.parent.$("#departmentName").attr("value",departNames);
		    				    window.parent.$("#secondDepartSubCompanyId").attr("value",departSubCompanyId);
		    				    window.parent.$.colorbox.close();
		    				}else{
                                alert("部门："+data+"所在的分支机构中存在同名用户，请重新选择！");
			    			}
	    				},
	    				error : function(XMLHttpRequest, textStatus) {
	    					alert(textStatus);
	    				}
	    			});
				
				}
				
			}
			//验证兼职是否在同一分支机构(不选择正职部门时)
			//12,null,22,null....
			function isInSameBranchWhenOnlySub(departSubCompanyId){
				var strArr = departSubCompanyId.split(",");
				var first = strArr[0];
				for(i=0;i<strArr.length;i++){
                    if(first!=strArr[i]){
                      return false;
                    }
				}
                return true;
			}
			
			//验证兼职和主职部门是否在同一分支机构
			function isInSameBranch(mainSubId,subIdStr){
				if(mainSubId==''){
                    return true;
				}else{
					if(mainSubId=='null'&&isUserIdNull(subIdStr)){
	                    return true;
					}else if(mainSubId!='null'&&!isUserIdNull(subIdStr)){
						var strArr = subIdStr.split(",");
						for(i=0;i<strArr.length;i++){
                            if(mainSubId!=strArr[i]){
                              return false;
                            }
						}
						return true;
					}else if(mainSubId!='null'&&isUserIdNull(subIdStr)){
	                    return false;
					}else if(mainSubId=='null'&&!isUserIdNull(subIdStr)){
	                    return false;
					}
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
			</script>
			</div>
		</div>
	</div>
</body>
</html>
