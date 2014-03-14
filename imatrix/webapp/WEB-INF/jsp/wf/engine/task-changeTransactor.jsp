<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>更改办理人</title>
	<%@ include file="/common/wf-colorbox-meta.jsp"%>
	
	<script src="${resourcesCtx}/js/public.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<link type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/formValidator/validator.css"></link>
	
	<style type="text/css">
		a{text-decoration:none;}
	</style>
	<script type="text/javascript">
	

	function selectMan(id){
		var man = id.split(",");
		if(man[0] == "USER"){
			$("#wf_transactor").attr("value", man[2]);
		}
	}

	var taskid ;
	
	//指派
	function assignTo(){
		var obj=ztree.getCurrentClickNode();
		if(obj){
			var type=ztree.getType();
			if(!type||type!="user"){
				showmmm("请选择用户");
				return;
			}else{
				var info=ztree.getInfo();
				var taskid = $('#current_task option:selected').attr('value');
				var taskName = $('#current_task option:selected').attr('taskName');
				if(taskid==null||taskid==''){
					showmmm("请选择任务");
					return;
				}
				$("#changeform_taskId").attr("value",taskid);
				$("#changeforn_transactor").attr("value",info.id);
				if(validateChangeUser(info.loginName,info.id,taskName)){
				   ajaxSubmit("changeForm", webRoot + "/engine/task!changeTransactorSave.htm", "changeZone", closeWin);
				}else{
					showmmm("不能选择当前环节的其他办理人");
				}
			}
		}else{
			showmmm("请选择用户");
		}
	}
    function validateChangeUser(validateLoginName,validateTransactorId,taskName){
        var option = $("#current_task").find('option');
        for(var i=0;i<option.length;i++){
          var loginName = $(option[i]).attr("loginName");
          var transactorId = $(option[i]).attr("transactorId");
          var taskname = $(option[i]).attr("taskName");
          if(taskname!='undefined'&&transactorId!='undefined'&&transactorId!=''){
            if(transactorId==validateTransactorId&&taskname==taskName)return false;
          }else if(taskname!='undefined'&&loginName!='undefined'){
            if(loginName==validateLoginName&&taskname==taskName)return false;
          }
        }
        return true;
    }
	
	function closeWin(){
		window.parent.$.colorbox.close();
	}

	function showmmm(msg){
		$("#message").html("<span class='onError' >"+msg+"</span>");
		$("#message").show();
		setTimeout('$("#message").hide("show");',3000);
	}

	</script>
</head>
<body style="padding: 5px; text-align: left" onload="getContentHeight();" >
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="changeZone">
			<form action="" id="changeForm" name="changeForm" method="post">
				<input type="hidden" name="taskId" value="" id="changeform_taskId">
				<input type="hidden" name="transactor" value="" id="changeforn_transactor">
			</form>
			<div class="opt-btn">
				<s:if test="hasActivitySubProcess">
					<button class='btn' onclick="window.parent.$.colorbox.close();" hidefocus="true"><span><span>关闭</span></span></button>
				</s:if><s:else>
					<button id="wf_ok" class='btn' onclick="assignTo();" hidefocus="true"><span><span>确定</span></span></button>
				</s:else>
			</div>
			<div id="opt-content">
				<s:if test="hasActivitySubProcess">
					<font color="red">当前任务为子流程，不能改变办理人</font>
				</s:if><s:else>
					<div id="message"><s:actionmessage theme="mytheme" /></div>
					<div style="margin: 5px">
					选择具体环节：<select id="current_task" >
									<option transactor="" value="">---请选择任务---</option>
									<s:iterator value="taskList">
										<s:if test="hasBranch">
										    <s:if test="trustorName==null">
											   <option id="${id }"  value="${id }" transactor="${transactorName }" loginName="${transactor}" transactorId ="${transactorId }"  taskName="${name}">${name }(${transactorName }/${transactorSubCompanyName })</option>
											</s:if><s:else>
											   <option id="${id }"  value="${id }" transactor="${transactorName }" loginName="${transactor}" transactorId ="${transactorId }" taskName="${name}">${name }(${transactorName }/${transactorSubCompanyName }  受托于${trustorName }/${trustorSubCompanyName })</option>
											</s:else>
										</s:if><s:else>
										    <s:if test="trustorName==null">
											   <option id="${id }"  value="${id }" transactor="${transactorName }" loginName="${transactor}" transactorId ="${transactorId }"  taskName="${name}">${name }(${transactorName })</option>
											</s:if><s:else>
											   <option id="${id }"  value="${id }" transactor="${transactorName }" loginName="${transactor}" transactorId ="${transactorId }" taskName="${name}">${name }(${transactorName }  受托于${trustorName })</option>
											</s:else>
										</s:else>
									</s:iterator>
								</select>
					<br/>			
					</div>
					<input id="wf_transactor" type="hidden" name="transactor">
					<div style="margin-bottom: 10px">
								<ztree:ztree  
									treeType="MAN_DEPARTMENT_TREE" 
									treeId="treeDemo" 
									userWithoutDeptVisible="true"  
									treeNodeShowContent="[{'company':'name','department':'name','user':'name','workgroup':'name'}]"
									>
								</ztree:ztree>
					</div>
				</s:else>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>