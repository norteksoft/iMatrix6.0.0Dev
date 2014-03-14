<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<title><s:text name="company.companyManager"/></title>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<div id="opt-content">
			<ul class="ztree" id="_department_tree"></ul>
		</div>
	</div>
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
	var currentDeptId = parent.$("#id").attr("value");
	var isBranch = parent.$("#branchFlag").attr("value");
	node = tree.getSelectNodeId();
	var currentDeptSubCompanyId = parent.$("#subCompanyId").attr("value");
	if(node!='undefined'&&node!=''&&(node.indexOf('USERSBYDEPARTMENT')>=0||node.indexOf('USERSBYBRANCH')>=0)){
		var deptId = node.split(split_one)[1].split(split_two)[0];
		var type = node.split(split_one)[0];
		var deptSubCompanyId = node.split(split_three)[1];
		if(currentDeptId==deptId){
			alert("不能选择当前部门为父部门！");
			return;
		}
		if(type=='USERSBYDEPARTMENT'||type=='USERSBYBRANCH'){
			if(isBranch=="false"){
	            if(!isInSameBranch(currentDeptId,currentDeptSubCompanyId,deptSubCompanyId)){
	               alert("不能跨分支机构选择父部门，请重新选择！");
	               return;
	            }
			}
		}
		
		$.ajax({
			data:{currentDeptId: currentDeptId,deptId: deptId},
			type:"post",
			url: "department!isSubDepartment.action",
			beforeSend:function(XMLHttpRequest){},
			success:function(data, textStatus){
				if(data=="false"){
					var name = node.split(split_two)[1].split(split_three)[0];
					parent.setParentDeptInfo(deptId, name);
					window.parent.$.colorbox.close();
				}else if(data=="no"){
					alert('请先删除分支机构下的部门、人员、工作组、分支机构、分支机构授权管理、角色');
				}else{
					alert("不能设置当前部门的子部门为其父部门！");
				}
			},
			complete:function(XMLHttpRequest, textStatus){},
	        error:function(){

			}
		});
	}else if(node.indexOf('DEPARTMENTS')>=0){//表示公司名称，树的根节点
		if(currentDeptId!=''){//修改
			if(isBranch=="true"){//如果是分支机构
				var name = node.split(split_two)[1];
				parent.setParentDeptName(name);
				window.parent.$.colorbox.close();
			}else{
				if(currentDeptSubCompanyId!=''){//分支机构下的部门
	               alert("您已经跨分支机构，请重新新选择！");
	               return;
				}else{//如果是公司下的部门
					var name = node.split(split_two)[1];
					parent.setParentDeptName(name);
					window.parent.$.colorbox.close();
	
				}
			}
		}else{//新建
			var name = node.split(split_two)[1];
			parent.setParentDeptName(deptId, name);
			window.parent.$.colorbox.close();
		}
	}else{
		alert('请选择部门！');
	}
}

function isInSameBranch(currentDeptId,currentDeptSubCompanyId,deptSubCompanyId){
  if(currentDeptId!=''){//修改
	  if(currentDeptSubCompanyId==''&&deptSubCompanyId!='null'){
            return false;
		}else if(currentDeptSubCompanyId!=''&&deptSubCompanyId=='null'){
			return false;
		}else if(currentDeptSubCompanyId!=''&&deptSubCompanyId!='null'){
			if(currentDeptSubCompanyId==deptSubCompanyId){
               return true;
			}else{
               return false;
			}
		}else{
              return true;
		} 
  }else{
	    return true;
  }
	
}
</script>	
</div>
</body>
</html>
