function mm_saveMonitorParmeter(form,url){
	$("#"+form).attr("action",url);
	$("#"+form).ajaxSubmit(function(data){
		if(data=="error"){
			alert("该系统设置已存在，请修改！");
		}else{
			$("#id").attr("id",data);
			$("#message").show("show");
			setTimeout('$("#message").hide("show");',3000);
		}
		
	});
}


function mm_view_parmeter(ts1,cellval,opts,rwdat,_act){
	var v="<a style=\"color: #4D87C7;text-decoration: none;\" href=\"#\" hidefocus=\"true\" onclick=\"mm_viewParmeter('"+opts.id+"');\">" +ts1 + "</a>";
	return v;
	
}

function mm_viewParmeter(id){
	ajaxAnyWhereSubmit('defaultForm',webRoot+'/mm/monitor-parmeter-input.htm?id='+id,'form_main');
}