//默认长度设置
function defautMaxlengthSet(){
	var dataType = $("#dataType").val();
	var maxlen=$("#maxLengthExist").val();
	if(typeof(maxlen)=="undefined"||maxlen==""||maxlen==0){
		//控制最大长度
		if(dataType=="TEXT"){
			$("#maxLength").attr("value",255);
		}else if(dataType=="DOUBLE"||dataType=="FLOAT"||dataType=="AMOUNT"){
			$("#maxLength").attr("value",25);
		}else if(dataType=="INTEGER"||dataType=="NUMBER"){
			$("#maxLength").attr("value",10);
		}else if(dataType=="LONG"){
			$("#maxLength").attr("value",19);
		}else if(dataType=="BOOLEAN"){
			$("#maxLength").attr("value",1);
		}else if(dataType=="BLOB"||dataType=="CLOB"||dataType=="COLLECTION"||dataType=="ENUM"||dataType=="REFERENCE"||dataType=="TIME"){
			$("#maxLength").attr("value","");
		}
	}
}

/************************************************图片控件、JC控件***************************************************************/
var folderPosition = "";
/*---------------------------------------------------------
函数名称:uploadFile_d
参          数:url
功          能:正文上传初始化
------------------------------------------------------------*/
function initUploadControl(fileTypes,fileTypeDescription){
		new SWFUpload({
			upload_url: webRoot+"/form/upload-image.htm",
			post_params: {"name" : "参数"},
			
			file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
			
			file_size_limit : "200 K",	// 1000MB
			file_types : fileTypes,
			file_types_description : fileTypeDescription,
			file_upload_limit : "0",
			
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,//选择好文件后提交
			file_queued_handler : fileQueued,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess,
			upload_complete_handler : uploadComplete,
			
			// Button Settings
			button_image_url : imatrixRoot+"/images/annex.gif",
			button_placeholder_id : "spanButtonPlaceholder",
			button_width: 250,
			button_height: 18,
			button_text : '<span class="button">请上传文件(最大200k)</span>',
			button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
			button_text_top_padding: 0,
			button_text_left_padding: 18,
			button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			button_cursor: SWFUpload.CURSOR.HAND,
			button_action:SWFUpload.BUTTON_ACTION.SELECT_FILE,
			
			// Flash Settings
			flash_url : imatrixRoot+"/widgets/swfupload/swfupload.swf",
			
			custom_settings : {
				upload_target : "divFileProgressContainer",
				isUpload : true
			},
			// Debug Settings
			debug: false  //是否显示调试窗口
		});
}

var fileName = "";
/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:所好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore(file,swfObject){
	fileName = getFileName(file.type,file.name,new Date().getTime());
	fileName = folderPosition+$("#companyCode").val()+"/"+fileName;
	
	swfObject.customSettings.isUpload=true;
	swfObject.setPostParams({"myFileName":fileName});
	swfObject.startUpload();
}
/*---------------------------------------------------------
函数名称:rewriteMethod
参          数:url
功          能:所有文件上传之后调用
------------------------------------------------------------*/
function rewriteMethod(file,swfObject){
	$("#divFileProgressContainer").html("");
	var tableId = "filesTB";
	$.getJSON(
			webRoot+"/form/getFiles.htm?myFileName="+folderPosition+$("#companyCode").val()+"&callback=?",
			function(data){
				$("#"+tableId).find("tr").remove();

				var type = "radio";
				var name = "images";
				if(folderPosition=="formResources/formJCs/"){//JC控件
					type = "checkbox";
					name = "jcs";
				}
				var files = eval(data.data);
				for(var i=0;i<files.length;i++){
					var row = "<tr filePath='"+files[i].filePath+"' >";
					var col1 = "<td ><input name='"+name+"' value='"+files[i].filePath+"' type='"+type+"' fileName='"+files[i].fileName+"'></input>"+"</td>";
					if(fileName==files[i].filePath){
						col1 = "<td ><input name='"+name+"' value='"+files[i].filePath+"' checked='checked' type='"+type+"' fileName='"+files[i].fileName+"'></input>"+"</td>";
					}
					var col2 = "<td >"+files[i].fileName+"</td>";
					var col4 = "<td ><a href='#' onclick='downloadImage(\""+files[i].filePath+"\",\""+files[i].fileName+"\");'>下载</a>&nbsp;&nbsp;<a href='#' onclick='deleteFile(\""+files[i].filePath+"\");'>删除</a></td>";
					var row1="</tr>";
					$("#"+tableId).append(row+col1+col2+col4+row1); 
				}
			}
	);
}

function myDestroyUploadControl(){
	for(var i=0;i<SWFUpload.movieCount;i++){
		SWFUpload.instances["SWFUpload_"+i].destroy();//销毁上传对象
	}
}

function getFileName(fileType,file_name,currenttime){
	return file_name+"~~"+currenttime+fileType;
}

function deleteFile(filePath){
	if(confirm("确认删除?")){
		$.ajax({
			   type: "POST",
			   url: webRoot+"/form/delete-upload-file.htm",
			   data: "srcFileName="+filePath,
			   success: function(msg){
					$("#filesTB").find("tr[filePath='"+filePath+"']").remove();
			   }
		});
	}
}

function downloadImage(filePath,file_name){
	window.open(imatrixRoot + "/mms/form/download-upload-file.htm?srcFileName="+filePath+"&fileName="+file_name);
}


