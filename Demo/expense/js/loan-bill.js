/************************************************************
 上传正文
************************************************************/
var otherswfu;
/*---------------------------------------------------------
函数名称:uploadFile_d
参          数:url
功          能:正文上传初始化
------------------------------------------------------------*/
function uploadDocument(){
	otherswfu = new SWFUpload({
		upload_url: webRoot+"/expense-report/uploadDocument.htm",
		post_params: {"name" : "参数"},
		
		file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
		
		file_size_limit : "30 MB",	// 1000MB
		file_types : "*.docx;*.doc;*.pdf;*.xls;*.wps;*.et;",
		file_types_description : "word,excel,pdf,wps,et",
		file_upload_limit : "0",
		
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
		file_queued_handler : fileQueued,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,
		
		// Button Settings
		button_image_url : webRoot + "/images/annex.gif",
		button_placeholder_id : "spanButtonPlaceholder",
		button_width: 250,
		button_height: 18,
		button_text : '<span class="button">请上传文件(最大100MB)</span>',
		button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
		button_text_top_padding: 0,
		button_text_left_padding: 18,
		button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
		button_cursor: SWFUpload.CURSOR.HAND,
		
		// Flash Settings
		flash_url : webRoot + "/widgets/swfupload/swfupload.swf",
		
		custom_settings : {
			upload_target : "divFileProgressContainer",
			isUpload : true
		},
		// Debug Settings
		debug: false  //是否显示调试窗口
	});
}

/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:所好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore(file,swfObject){
	var id=$("#id").attr("value");
	var taskId=$("#taskId").attr("value");
	if(id==""||id==null){
		otherswfu.customSettings.isUpload=false;
		alert("请先保存,再上传!");
		otherswfu.eventQueue=[];
	}else{
		otherswfu.customSettings.isUpload=true;
		otherswfu.setPostParams({"id":id,"taskId":taskId});
		otherswfu.startUpload();
	}
}

/*---------------------------------------------------------
函数名称:rewriteMethod
参          数:url
功          能:所有文件上传之后调用
------------------------------------------------------------*/
function rewriteMethod(file,swfObject){
	ajaxSubmit("expenseReportForm", webRoot+"/expense-report/input.htm", "main",uploadBack);
}
 
/**
 * 上传之后回调
 * @return
 */
function uploadBack(){
	$( "#tabs" ).tabs();
	expenseReportFormValidate();
	$("#outDate").datepicker();
	validateForm();
	uploadDocument();
	getContentHeight();
}

function dealResult(id){
	if(id!=""&&typeof id!='undefined'&&id!=null){
		var ids=id.split(";");
		$("#id").attr("value",ids[0]);
		if(ids[1]!=undefined&&ids[1]!=null&&ids[1]!=''){
			if(ids[1].indexOf('{')!=-1){//选择环节
				$.colorbox({href:webRoot+"/expense-report/select-tache.htm"+"?taskId="+$("#taskId").attr("value")+"&id="+ids[0]+"&taches="+ids[1],iframe:true, innerWidth:700, innerHeight:400,overlayClose:false,title:"请选择",onClosed:function(){
					aa.submit("defaultForm1", webRoot+"/expense-report/process-task.htm", 'btnZone,viewZone', validateForm);
				}});
			}else if(ids[1].indexOf('selectLeader')!=-1||ids[1].indexOf('selectReadPerson')!=-1){
				if(ids[1].indexOf('?')!=-1){
					$.colorbox({href:webRoot+ids[1]+"&taskId="+$("#taskId").attr("value")+"&id="+ids[0],iframe:true, innerWidth:700, innerHeight:430,overlayClose:false,title:"请选择"});
				}else{
					$.colorbox({href:webRoot+ids[1]+"?taskId="+$("#taskId").attr("value")+"&id="+ids[0],iframe:true, innerWidth:700, innerHeight:500,overlayClose:false,title:"请选择"});
				}
			}else{//填意见
				$.colorbox({href:webRoot+ids[1]+"&taskId="+$("#taskId").attr("value")+"&id="+ids[0],iframe:true, innerWidth:700, innerHeight:400,overlayClose:false,title:"请选择"});
			}
		}else{
			$("#message").show("show");
			setTimeout('$("#message").hide("show");',3000);
			aa.submit("defaultForm1", webRoot+"/expense-report/process-task.htm", 'btnZone,viewZone');
		}
	}
}
//新建Word
function openDocument(fileType,workflowId,taskId,id,viewFlag){
	if(typeof workflowId =="undefined" || workflowId==""){
		alert("请先保存表单再创建附件。");
		return false;
	}
	if(typeof id=='undefined') id='';
	var url = webRoot+"/engine/office!createOffice.htm?fileType="+fileType+"&workflowId="+workflowId+"&taskId="+taskId+"&id="+id;
	window.open(url,'',"top=0,left=0,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=no,width="+screen.availWidth+",height="+screen.availHeight);
}


