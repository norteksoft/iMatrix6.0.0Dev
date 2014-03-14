/************************************************************
 模块名称: 公用JS
 编写时间: 2010年6月13日
 编    程: 张清欣
 说    明: 无
 ************************************************************/

/*---------------------------------------------------------
函数名称:
参          数:
功          能:onLoad
------------------------------------------------------------*/
$(document).ready( function() {
	automaticHeight();
	hideDashedBox();
});

/*---------------------------------------------------------
函数名称:automaticHeight
参          数:
功          能:自动算高度(col1\col2\col3)document.documentElement.clientHeight
------------------------------------------------------------*/
function automaticHeight() {
 	var col1 = $("#col1").height();
	var col2 = $("#col2").height();
	var col3 = $("#col3").height();
	if (col1 != null && col1 > col2 && col1 > col3) {
		if (col1 < $(document).height() - 102)
			col1 = $(document).height() - 102;
		$("#col1").height(col1);
		$("#col2").height(col1);
		$("#col3").height(col1);
	} else if (col2 != null && col2 > col1 && col2 > col3) {
		if (col2 < $(document).height() - 102)
			col2 = $(document).height() - 102;
		$("#col2").height(col2);
		$("#col1").height(col2);
		$("#col3").height(col2);
	} else if (col3 != null && col3 > col1 && col3 > col2) {
		if (col3 < $(document).height() - 102)
			col3 = $(document).height() - 102;
		$("#col3").height(col3);
		$("#col1").height(col3);
		$("#col2").height(col3);
	} else {
		col3 = $(document).height() - 102;
		$("#col3").height(col3);
		$("#col1").height(col3);
		$("#col2").height(col3);
	}
}
/*---------------------------------------------------------
函数名称:hideDashedBox
参          数:
功          能:IE下自动去掉虚框
------------------------------------------------------------*/
function hideDashedBox(){
	var as = $("a");
	for(var i = 0; i < as.length; i++){
		$(as[i]).attr('hideFocus', 'true');
	}
}

function ajaxSubmit(formName,url,zones,callback){
	$("#"+formName).attr("action",url);
	ajaxAnywhere.formName = formName;
	ajaxAnywhere.getZonesToReload = function() {
		return zones;
	};
	ajaxAnywhere.onAfterResponseProcessing = function () {
		if(typeof(callback) == "function"){
			callback();
		}
	};
	ajaxAnywhere.submitAJAX();
}

function select(obj){
	if(obj!=null){
		$(".index").removeClass("selectind");
		$(obj).parent("li").addClass("selectind");
	}
}

/*---------------------------------------------------------
函数名称:showMsg
参          数:id 显示信息的元素的id
功          能:显示提示信息，3秒后隐藏
------------------------------------------------------------*/
function showMsg(id){
	if(id==undefined)id="message";
	$("#"+id).show();
	setTimeout('$("#'+id+'").hide();',3000);
}
