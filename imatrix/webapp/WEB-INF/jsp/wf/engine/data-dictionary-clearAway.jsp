<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>移除办理人</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	
	<script>
	$(function () {
		$("#opt-content").css("height",$(".ui-layout-center").height()-66);
		init();
	});
	function init(){
		var users=parent.$("input[name=userNames]");
		var departments=parent.$("input[name=deptNames]");
		var workgroups=parent.$("input[name=groupNames]");
		var ranks=parent.$("input[name=rankNames]");
		if(users.length>0){
			for(var i=0;i<users.length;i++){
				var value=$(users[i]).attr('value');
				var values=value.split(";");
				if(values.length>1){
					$("#userUl").append(getColumn('userNames',value,value.substring(value.indexOf(";")+1, value.indexOf("[")),'dictionary_users'));
				}
			}
		}
		if(departments.length>0){
			for(var i=0;i<departments.length;i++){
				var value=$(departments[i]).attr('value');
				$("#departmentUl").append(getColumn('deptNames',value,value.substring(value.indexOf(";")+1, value.indexOf("[")),'dictionary_depts'));
			}
		}
		if(workgroups.length>0){
			for(var i=0;i<workgroups.length;i++){
				var value=$(workgroups[i]).attr('value');
				$("#workgroupUl").append(getColumn('groupNames',value,value.substring(value.indexOf(";")+1, value.indexOf("[")),'dictionary_workgroups'));
			}
		}
		if(ranks.length>0){
			for(var i=0;i<ranks.length;i++){
				var value=$(ranks[i]).attr('value');
				$("#rankUl").append(getColumn('rankNames',value,value.substring(value.indexOf(";")+1, value.indexOf("[")),'dictionary_ranks'));
			}
		}
	}

	function getColumn(name,value,showValue,checkAllId){
		var html='<li>'
			+'<input type="checkbox" name="'+name+'" value="'+value+'" onclick="_click_one(\''+checkAllId+'\',\''+name+'\');"/>'
			+'<span">'+showValue+'</span>'
			+'</li>';
		return html;
	}

	function checkAll(obj){
		if($(obj).attr('id') == 'dictionary_users'){
			checkEveryOne(obj, 'userNames');
		}else if($(obj).attr('id') == 'dictionary_depts'){
			checkEveryOne(obj, 'deptNames');
		}else if($(obj).attr('id') == 'dictionary_workgroups'){
			checkEveryOne(obj, 'groupNames');
		}else if($(obj).attr('id') == 'dictionary_ranks'){
			checkEveryOne(obj, 'rankNames');
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
	var tempNames='';
	var tempHtml='';
	function OK(){
		var usersLength=$("input[name=userNames]:checked").length;
		var departmentsLength=$("input[name=deptNames]:checked").length;
		var workgroupsLength=$("input[name=groupNames]:checked").length;
		var ranksLength=$("input[name=rankNames]:checked").length;
		if(usersLength==0&&departmentsLength==0&&workgroupsLength==0&&ranksLength==0){
			alert("请选择办理人");
		}else{
			if(confirm("确定要移除所选项?")){
				var userNamesView="";
				var html="";
				var value="";
				packagingTransactor('userNames');
				packagingTransactor('deptNames');
				packagingTransactor('groupNames');
				packagingTransactor('rankNames');
				parent.$("#userNamesView").html(tempNames);
				parent.$("#slcMan").html(tempHtml);
			}else{ return; }
			window.parent.$.colorbox.close();
		}
	}
	function packagingTransactor(name){
		$("input[name="+name+"]:checkbox").each(function(){
			if(!$(this).attr("checked")){
				var value=$(this).attr("value");
				if(tempNames!=''){
					tempNames+=',';
				}
				tempNames+=value.substring(value.indexOf(";")+1, value.indexOf("["));
				tempHtml+=getHtml(name,value);
			}
		});
	}
	function getHtml(name,value){
		return '<input name="'+name+'" type="text" value="'+value+'" /><br/>';
	}
	</script>
								
</head>
<body>
<div class="ui-layout-center">
<form action="" id="pageForm" method="post"></form>
    <div class="opt-body">
		<div class="opt-btn">
			<button class="btn" onclick="OK();"  hidefocus="true"><span><span>确定</span></span></button>
		</div>
		<div id="opt-content">
			<table border="1" cellpadding="0" cellspacing="0" class="leadTable">
				<thead>
					<tr>
						<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="dictionary_users" onclick="checkAll(this)"/>用户</th>
						<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="dictionary_depts" onclick="checkAll(this)"/>部门</th>
						<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="dictionary_workgroups" onclick="checkAll(this)"/>工作组</th>
						<th style="text-align:left; padding-left:4px;"><input type="checkbox" id="dictionary_ranks" onclick="checkAll(this)"/>用户上下级关系</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td valign="top">
							<ul id="userUl" style="list-style-type:none;margin:0;padding-left: 0px;">
							<s:iterator value="users">
							<li>
							    <input type="checkbox" name="userNames" value="0;${name}[${loginName}]" onclick="_click_one('dictionary_users','userNames');"/>
								<span">${name}</span>
							</li>
							</s:iterator>
							</ul>
						</td>
						<td valign="top">
							<ul id="departmentUl" style="list-style-type:none;margin:0;padding-left: 0px;">
							</ul>
						</td>
						<td valign="top">
							<ul id="workgroupUl" style="list-style-type:none;margin:0;padding-left: 0px;">
							</ul>
						</td>
						<td valign="top">
							<ul id="rankUl" style="list-style-type:none;margin:0;padding-left: 0px;">
							</ul>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>
