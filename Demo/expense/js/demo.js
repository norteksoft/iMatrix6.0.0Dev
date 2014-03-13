//显示提示信息，3秒后隐藏
function showMsg(id,time){
	if(id==undefined)id="message";
	$("#"+id).show();
	if(time==undefined)time=3000;
	setTimeout('$("#'+id+'").hide();',time);
}

/*---------------------------------------------------------
函数名称:_add_SWf
参          数:无
功          能:查看流转历史
------------------------------------------------------------*/
function _add_SWf(){
	var so = new SWFObject(
			webRoot+"/widgets/workflowEditor/FlowChartProject.swf",
			"FlowChartProject", "100%", "100%", "9", "#CCCCCC");
	so.addParam("quality", "high");
	so.addParam("name", "FlowChartProject");
	so.addParam("id", "FlowChartProject");
	so.addParam("AllowScriptAccess", "always");
	so.addParam("menu", "false");
	so.addVariable("webRoot", webRoot);
	so.addVariable("companyId", $("#companyId").val());
	so.addVariable("wfId", $("#wfdId").val());
	so.addVariable("instanceId", $("#instanceId").val());
	if($("#localeLang").val() == "en"){
		so.addVariable("localeLanguage", "en_US");
	}else if($("#localeLang").val() == "zh"){
		so.addVariable("localeLanguage", "zh_CN");
	}
	so.addVariable("page", "viewHistoryProcess");
	so.write("flashcontent");
}

