<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/wf-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">s
<html>
<head>
	
	<title>流程管理</title>
	<%@ include file="/common/wf-iframe-meta.jsp"%>
	<script src="${resourcesCtx}/js/public.js" type="text/javascript"></script>
	<script src="${wfCtx }/js/wf.js" type="text/javascript"></script>
	
	<script type="text/javascript">
	function okBtn(){
		var item =$("input[name='tranName']:checked");
		if(item.length<=0){
			alert("请选择办理人");
		}else{
			for(var i=0;i<item.length;i++){
				$("#choiceForm").append('<input type="hidden" name="transactors" value="'+$(item[i]).attr("value")+'"/>');
			}
			ajaxSubmit('choiceForm','${wfCtx }/engine/task!goback.htm','wf_task',postSuccess);
		}
	}
	function postSuccess(){
		window.parent.backViewClose('${wfdId}','${position}','${type}','${definitionCode}');
		window.parent.$.colorbox.close();
	}

	//全选
	function _select_all(boxId,inputName){
		if($("#"+boxId).attr("checked")){
			$("input[name='"+inputName+"']").attr("checked","checked");
		}else{
			$("input[name='"+inputName+"']").removeAttr("checked");
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
	</script>
</head>
<body onload="getContentHeight();">
<div class="ui-layout-center">
	<div class="opt-body">
		<aa:zone name="wf_task">
			<div class="opt-btn">
				<s:if test="canChoiceTransactor.size>0">
					<button id="submitFormButton" class='btn' onclick="okBtn();" hidefocus="true"><span><span>确定</span></span></button>
				</s:if>
				<button class='btn' onclick="back_main();" hidefocus="true"><span><span>返回</span></span></button>
			</div>
			<div id="opt-content" class="form-bg">
				<div id="successMessage" class="onSuccess"><s:actionmessage theme="mytheme"/></div>
				<s:if test="canChoiceTransactor.size>0">
					<form name="choiceForm" id="choiceForm">
						<input id="wfdId" name="wfdId" value="${wfdId}" type="hidden"/>
						<input id="workflowId" name="workflowId" value="${workflowId}" type="hidden"/>
						<input id="backto" name="backto" value="${backto}" type="hidden"/>
					</form>
					请为环节【${backto }】选择具体办理人：
					<table class="form-table-border-left">
						<thead>
							<tr>
							<th><input id="_all" type="checkbox" onclick="_select_all('_all','tranName');"></input></th>
							<th>用户名</th>
							<th>用户登录名</th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="canChoiceTransactor" id="user">
								<tr> 
								<td>
									<input type="checkbox" name="tranName" value="<s:property value="#user.key"/>" onclick = "_click_one('_all','tranName');"/>
								</td><td><s:property value="value.name"/></td><td><s:property value="value.loginName"/></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
			</div>
		</aa:zone>
	</div>
</div>
</body>
</html>
