<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据字典</title>
	<%@ include file="/common/setting-colorbox-meta.jsp"%>
	
	<link rel="stylesheet" type="text/css" href="${settingCtx}/css/style.css" />
	
	<style type="text/css">
		a{text-decoration:none;}
		#secNav{
			margin:0;
			padding: 0;
			background: url(../images/sec-background.jpg);
			border-bottom: 9px #f0f0f0 solid;
			height: 26px;
		}
		#secNav li{
			display: inline;
			border-right: 1px #9ea2a3 solid;
			margin:0px;
			padding: 0px;
			font-size: 10.5pt;
		    line-height: 26px;
		    text-align:center;
			float:left;
		}
		#secNav li a{
			font-size: 10.5pt;
			text-decoration: none;
			margin: 0 10px;
			color: #000;
		}
		#secNav li a:ACTIVE, #secNav li a:VISITED{
			color: #000;
		}
		#secNav li.selected{
			background-color: #f1f1f1;
		}
		.ui-widget-content {
		    background: none;
		    border: 0px;
		}
	</style>
	<script type="text/javascript">

	var selLi="companyli";
	//选择
	function selectContact(id){
		if(id=="companyli"){
			selLi="companyli";
			$("#companyli").attr("class","selected");
			$("#userli").attr("class","");
			$("#groupli").attr("class","");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/user-tree.htm', 'wf_rank_tree');
		}else if(id=="userli"){
			selLi="userli";
			$("#companyli").attr("class","");
			$("#userli").attr("class","selected");
			$("#groupli").attr("class","");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/dept-tree.htm', 'wf_rank_tree');
		}else if(id=="groupli"){
			selLi="groupli";
			$("#companyli").attr("class","");
			$("#userli").attr("class","");
			$("#groupli").attr("class","selected");
			ajaxSubmit('defaultForm', '${settingCtx}/rank/group-tree.htm', 'wf_rank_tree');
		}
	}


		
	
	
	function selectUsers(){
		ztree.selectTreeValue(function(){
			var ids="";
			var names="";
			var type="";
			var loginNames="";
			if(selLi=="companyli"){
				type="0";
				ids=ztree.getIds();
				names=ztree.getNames();
				loginNames=ztree.getLoginNames();
			}else if(selLi=="userli"){
				type="1";
				ids=ztree.getDepartmentIds();
				names=ztree.getDepartmentNames();
			}else if(selLi=="groupli"){
				type="2";
				ids=ztree.getWorkGroupIds();
				names=ztree.getWorkGroupNames();
			}
			if(names=='所有人员'||names=='所有部门'){
				alert("不能选择所有人员");
				return;
			}
			if(ids&&ids!=""){
				window.parent.$("#userDiv").html("");
				window.parent.$("#userDiv").append("<input name='userInfos' value='"+ids+";"+names+";"+loginNames+";"+type+"'/>");
				window.parent.$("#userNames").attr("value",names);
				window.parent.$("#title").focus();
			}
			window.parent.$("#selectUser").colorbox.close();
		});
	
	}
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			$( "#tabs" ).tabs({select:function(event,ui){}});
		});
		function pageUlChange(x){
			if('a'==x){
				selectContact('companyli');
			}else if('b'==x){
				selectContact('userli');
			}
		}
	</script>
</head>
<body style="padding: 5px;">
	<aa:zone name="wf_task">
	<form id="defaultForm" name="defaultForm"></form>
		<input type="hidden" id="isbranch" value="${isbranch }"/>
		<div id="tabs" >
			<ul>
				<li><a href="#tabs-1" onclick="pageUlChange('a');">选择人员</a></li>
				<li><a href="#tabs-1" onclick="pageUlChange('b')">选择部门</a></li>
			</ul>
			<div id="tabs-1">
				<div class="opt-btn">
					<button class="btn" onclick="selectUsers();"><span><span>确定</span></span></button>
				</div>
				<aa:zone name="wf_rank_tree">
					<ztree:ztree
							treeType="MAN_DEPARTMENT_TREE"
							treeId="treeDemo" 
							userWithoutDeptVisible="true" 
							showBranch="true"
							chkStyle="checkbox"
							chkboxType="{'Y' : 'ps', 'N' : 'ps' }"
							>
				</ztree:ztree>
				</aa:zone>
			</div>
		</div>
		<style type="text/css"> .jstree-classic.jstree-focused{background: none;} </style>
	</aa:zone>
</body>
</html>