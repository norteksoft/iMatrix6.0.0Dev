function __getSearchHeader(containerId,advancedSearch,fixedSearchSign,submitForm){
	var html='<div id="search_header">';
	html+='<div id="search_title">查询条件</div>';
	html+='<div id="search_close" onclick="removeSearchBox();"> </div>';
	html+='</div>';
	html+='<input id="containerSearchInput" value="'+containerId+'" type="hidden" />';
	html+='<input id="advancedSearchInput" value="'+advancedSearch+'" type="hidden" />';
	html+='<input id="fixedSearchSignInput" value="'+fixedSearchSign+'" type="hidden" />';
	html+='<input id="submitForm" value="'+submitForm+'" type="hidden" />';
	return html;
}

function __getAdvancedSearchTable(){
	var html='';
	html+='<form id="default_Form" name="default_Form">';
	html+='<table id="advanced_search_table_id" style="border: 0px; WIDTH: 100%;float:center;padding:0 10px;">';
	html+='<tr>';
	html+='<td style="border: 0px;">';
	html+='<div  class="searchtable">';
	html+='<table class="" id="search_Table">';
	html+='<tr class="str">';
	html+='<th width="15px" style="border-right: 0px;"></th>';
	html+='<th width="140px" style="border-left: 0px;"> 字段</th>';
	html+='<th width="60px">运算符</th>';
	html+='<th style="border-right: 0px;">值</th>';
	html+='<th  width="15px" style="border-left: 0px;"></th>';
	html+='<th  width="60px">关系符</th>';
	html+='<th  width="70px">删除&nbsp;&nbsp;</th>';
	html+='</tr>';
	html+='</table>';
	html+='</div>';
	html+='</td>';
	html+='</tr>';
	html+='</table>';
	html+='</form>';
	return html;
}

function __getSearchZoon(fieldList,gridId,gridUrl,fixedField,submitForm){
	var html='<div id="searchZoon" style="display: none; margin-top: 6px;">';
	html+='<div id="_query_smessage" style="color: red; margin: 0px 0 0 20px;font-size:13px;"></div>';
	if(fieldList!=""){
		html+='<textarea style="display:none;" id="field_list" >'+fieldList+'</textarea>';
	}
	html+='<p style="margin: 2px 0 2px 0;*margin-top:-14px;">&nbsp;';
	html+='<select id="left_bracket"><option value="" selected="selected"></option><option value="(">(</option><option value="((">((</option></select>&nbsp;';
	html+='<select id="search_name" onchange="changeField();" style="width:100px"></select>&nbsp;';
	html+='<select id="num_sign" onchange="_optChange(this);" style="width:80px"></select>&nbsp;';
	html+='<span id="search_value" style="width:160px;"></span>&nbsp;';
	html+='<select id="right_bracket"><option value="" selected="selected"></option><option value=")">)</option><option value="))">))</option></select>&nbsp;';
	html+='<select id="rel_sign" style="width:50px"><option value="and">并且</option><option value="or">或者</option></select>&nbsp;';
	html+='<button class="btn" onclick="addParameter();"><span><span>添加</span></span></button>';
	if(""==gridId&&""==gridUrl){//单独search插件
		html+='<button class="btn" onclick="commonSingleSearchSubmit({submitForm:\''+submitForm+'\',type:\'advanced\'});"><span><span>确认</span></span></button>';
	}else{
		html+='<button class="btn" onclick="doSearch(\''+gridId+'\', \''+gridUrl+'\');"><span><span>确认</span></span></button>';
	}
	html+='<button class="btn" onclick="resetParameter();"><span><span>清空</span></span></button>';
	html+='</p>';
	html+='<form id="search_From" name="search_From" method="post">';
	html+='<input type="hidden" id="search_Parameters" name="searchParameters" />';
	html+='</form>';
	html+=__getAdvancedSearchTable();
	if(fixedField!=""){
		html+='<a onclick="dynamicToFixed();" href="#" style="padding-left:12px;"> <<固定查询 </a>';
	}
	html+='</div>';
	return html;
}

function __getEnName(enName){
	if(enName=="")return"";
	var arr=enName.split('.');
	return arr[(arr.length-1)];
}

function __getSelectTag(field,num,_enName){
	var html='<select id="condition_'+num+'" ';
	html+='name="'+field.enName+'" dbName="'+field.dbName+'" myType="xiala" ';
	html+='dataType="'+field.propertyType+'" class="searchInputClass" ';
	if("ENUM"==field.propertyType){
		html+=' enumName="'+field.enumName+'" ';
	}
	if(field.eventType!=""&&"ONCHANGE"==field.eventType){
		html+=' onchange="'+_enName+'QueryChange(\'condition_'+num+'\');"';
	}
	html+='>';
	html+='<option value="" selected="selected">请选择</option>';
	if(field.defaultValues!=""){
		var defaultValues=field.defaultValues;
		for(var i=0;i<defaultValues.length;i++){
			html+='<option value="'+defaultValues[i].value+'">'+defaultValues[i].name+'</option>';
		}
	}
	html+='</select>';
	return html;
}

function __getInputTag(field,num,_enName){
	var html='<input id="condition_'+num+'" ';
	html+='name="'+field.enName+'" dbName="'+field.dbName+'" ';
	html+='dataType="'+field.propertyType+'" class="searchInputClass" ';
	if("INTEGER"==field.propertyType||"LONG"==field.propertyType){
		html+=' onkeyup="value=value.replace(/[^0-9]/g,\'\');"';
	}else if("DOUBLE"==field.propertyType||"FLOAT"==field.propertyType||"NUMBER"==field.propertyType||"AMOUNT"==field.propertyType){
		html+=' onkeyup="value=value.replace(/[^0-9\.]/g,\'\');"';
	}
	if(field.eventType!=""){
		if("ONCLICK"==field.eventType){
			html+=' onclick="'+_enName+'QueryClick(\'condition_'+num+'\');" ';
			html+=' readonly="readonly" ';
		}else if("BLUR"==field.eventType){
			html+=' onblur="'+_enName+'QueryBlur(\'condition_'+num+'\');" ';
		}else if(field.eventType.indexOf("/")!=-1){
			var eventTypes=field.eventType.split('/');
			html+=' onclick="formGridTree({currentInputId:\'condition_'+num+'\',treeType:\''+eventTypes[0]+'\',isMutiply:\''+eventTypes[1]+'\',useType:\'false\'});" ';
			html+=' readonly="readonly" ';
		}
	}
	html+='/>';
	return html;
}

function __getDateInputTag(field,num,idSign,dataTypeSign){
	var html='<input id="condition_'+num+'_'+idSign+'" ';
	html+='name="'+field.enName+'" dbName="'+field.dbName+'" ';
	html+='dataType="'+field.propertyType+'-'+dataTypeSign+'" class="searchInputClass" ';
	html+=' readonly="readonly" size="10" ';
	html+='/>';
	return html;
}

function __getDateScript(fieldId){
	var html='$("#'+fieldId+'").datepicker({';
	html+='"dateFormat":"yy-mm-dd",';
	html+='changeMonth:true,';
	html+='changeYear:true,';
	html+='showButtonPanel:"true"';
	html+='});';
	return html;
}

function __getTimeScript(fieldId){
	var html='$("#'+fieldId+'").datetimepicker({';
	html+='"dateFormat":"yy-mm-dd",';
	html+='changeMonth:true,';
	html+='changeYear:true,';
	html+='showSecond: false,';
	html+='showMillisec: false,';
	html+='"timeFormat": "hh:mm"';
	html+='});';
	return html;
}

function __getDateTag(field,num){
	var html='从';
	html+=__getDateInputTag(field,num,"b","first");
	html+='<br/>';
	html+='到';
	html+=__getDateInputTag(field,num,"e","second");
	html+='<script type="text/javascript">';
	var fieldId='condition_'+num;
	if("DATE"==field.propertyType){
		html+=__getDateScript(fieldId+'_b');
		html+=__getDateScript(fieldId+'_e');
	}else if("TIME"==field.propertyType){
		html+=__getTimeScript(fieldId+'_b');
		html+=__getTimeScript(fieldId+'_e');
	}
	html+='</script>';
	return html;
}

function __getTdByPropertyType(field,num){
	var _enName=__getEnName(field.enName);
	var html='';
	html+='<td>';
	if("BOOLEAN"==field.propertyType||"ENUM"==field.propertyType){
		html+=__getSelectTag(field,num,_enName);
	}else if("DATE"==field.propertyType||"TIME"==field.propertyType){
		html+=__getDateTag(field,num);
	}else{
		if(field.optionsCode!=""){
			html+=__getSelectTag(field,num,_enName);
		}else{
			html+=__getInputTag(field,num,_enName);
		}
	}
	html+='</td>';
	return html;
}

function __getFixedSearchZoonTr(fixedFields){
	var html='';
	var fieldNumber=fixedFields.length;
	var columnNumber=4;
	var remainder=fieldNumber%columnNumber;
	
	for(var i=0;i<fieldNumber;i++){
		var field=fixedFields[i];
		
		if(i%columnNumber==0){
			html+='<tr>';
		}
		html+='<td class="content-title" align="right">'+field.chName+'</td>';
		html+=__getTdByPropertyType(field,i);
		if((i+1)%columnNumber==0){
			html+='</tr>';
		}
	}
	if(remainder>0){
		html+='<td colspan="'+((columnNumber-remainder)*2)+'"></td>';
		html+='</tr>';
	}
	return html;
}

function __getFixedSearchZoon(fixedFields,advancedSearch,gridId,gridUrl,submitForm){
	var html='<div id="fixedSearchZoon" style="display: block;padding:6px 10px 6px 10px;">';
	html+='<table id="parameter_Table"  class="fix-searchtable" style="width:100%;">';
	html+=__getFixedSearchZoonTr(fixedFields);
	html+='<tr>';
	if(fixedFields.length>2){
		html+='<td colspan="8" align="center" >';
	}else{
		html+='<td colspan="'+(fixedFields.length*2)+'" align="center">';
	}
	if(""==gridId&&""==gridUrl){//单独search插件
		html+='<button class="btn" onclick="commonSingleSearchSubmit({submitForm:\''+submitForm+'\',type:\'common\'});"><span><span>确认</span></span></button>';
	}else{
		html+='<button class="btn" onclick="fixedSearchSubmit(\''+gridId+'\', \''+gridUrl+'\');"><span><span>确认</span></span></button>';
	}
	html+='&nbsp;<button class="btn" onclick="clearParameter();"><span><span>清空</span></span></button>';
	if(advancedSearch=="true"){
		html+='<span ><a onclick="fixedToDynamic();" href="#"  > 高级查询>></a></span>';
	}
	html+='</td>';
	html+='</tr>';
	html+='</table>';
	html+='</div>';
	return html;
}

function __getSearchBox(){
	var html='<div id="search_shade"></div>';
	html+='<div id="search_box" style="display: none;">';
	var containerId=_search_params._container_id;
	var advancedSearch=_search_params._advanced_search;
	var fixedSearchSign=_search_params._fixed_search_sign;
	var submitForm=_search_params._submit_form;
	var fieldList=_search_params._field_list;
	var gridId=_gridTagParams._grid_id;
	var gridUrl=_gridTagParams._grid_url;
	var fixedField=_search_params._fixed_field;
	var advancedSearch=_search_params._advanced_search;
	html+=__getSearchHeader(containerId,advancedSearch,fixedSearchSign,submitForm);
	if(fixedField!=""){
		html+=__getFixedSearchZoon(fixedField,advancedSearch,gridId,gridUrl,"");
	}
	html+=__getSearchZoon(fieldList,gridId,gridUrl,fixedField,"");
	html+='</div>';
	return html;
}

//原生查询使用
function singleSearch(obj){
	if(__dealWithSearchButtonOneself(obj.oneself))return;
	if(!(__singleSearchValidateRequired(obj.listCode)&&__singleSearchValidateRequired(obj.submitForm)&&__singleSearchValidateRequired(obj.placeId))){
		alert("请给参数listCode、submitForm、placeId赋值");
		return;
	}
	__singleSearch({
		listCode:obj.listCode,
		submitForm:obj.submitForm,
		placeId:obj.placeId
	});
}

//原生查询使用
function commonSingleSearchSubmit(obj){
	if(__searchValueToSubmitForm(obj)){
		singleSearchSubmit(obj);
	}else{
		alert("查询条件中的左右小括号不匹配！");
		return;
	}
}

function singleSearchSubmit(obj){}

function __dealWithSearchButtonOneself(obj){
	if($(obj).html()=="取消查询"){
		$(obj).html("查询");
		__removeSingleSearchBox();
		return true;
	}else if($(obj).html()=="查询"){
		$(obj).html("取消查询");
		return false;
	}
}

function __removeSingleSearchBox(){
	if($('#search_shade').length>0){
		$('#search_shade').removeClass('searchOver');
		$('#search_box').removeClass('searchBox');
		$('#search_box').css('display', 'none');		
		$('#search_box').css('display', 'none');		
		singleSearchContentResize();
	}	
}

function singleSearchContentResize(){}

function __singleSearchValidateRequired(param){
	if(typeof(param)=='undefined'||param==''){
		return false;
	}else{
		return true;
	}
}

function __singleSearch(obj){
	var rootUrl=webRoot;if(typeof(appRoot)!='undefined'&&appRoot!=''){rootUrl=appRoot;}
	$.ajax({
		data:{listCode:obj.listCode,url:obj.url,submitForm:obj.submitForm,placeId:obj.placeId},
		type:"post",
		url:rootUrl+"/portal/singleSearch.action",
		beforeSend:function(XMLHttpRequest){},
		success:function(data, textStatus){
			if("noQuery"==data){
				alert("请在列表设置中启用查询功能！");
			}else{
				__parseSingleSearchData(data);
			}
		},
		complete:function(XMLHttpRequest, textStatus){},
        error:function(){

		}
	});
}

function __parseSingleSearchData(data){
	var obj=eval("("+data+")");
	var html='<div id="search_shade"></div>';
	html+='<div id="search_box" style="display: none;">';
	var containerId=obj.containerId;
	var advancedSearch=obj.advancedSearch;
	var fixedSearchSign=obj.fixedSearchSign;
	var submitForm=obj.submitForm;
	var fieldList=obj.fieldList;
	var gridId="page";//待修改
	var fixedField=obj.fixedField;
	html+=__getSearchHeader(containerId,advancedSearch,fixedSearchSign,submitForm);
	if(fixedField!=""){
		html+=__getFixedSearchZoon(fixedField,advancedSearch,"","",submitForm);
	}
	html+=__getSearchZoon("","","",fixedField,submitForm);
	html+='</div>';
	$("#testdiv").html(html);
	__parseSingleSearchFieldJson(fieldList);
	__showSingleSearchDiv(obj.placeId);
}

function __parseSingleSearchFieldJson(fieldList){
	jsonFields = fieldList;
	__publicParseFieldJson(fieldList);
}

function __showSingleSearchDiv(placeId){
	var containerSearchId = $("#containerSearchInput").val();
	$('#'+placeId).css('display', 'block');
	if(containerSearchId!="false"){//嵌入式
		//消除页面滚动条
		$('#search_box').css('roll', 'block');
		$('#search_box').css('display', 'block');
		$('#search_header').css('display', 'none');		
		$("#search_box").css("width",$('#opt-content').width()+12);
		$('#opt-content').css('height',$('.opt-body').height()-55);
		$('#search_box').css('min-width', 820);
		fixedToInputWidth();
		if($('#fixedSearchSignInput').val()<=0){$("#searchZoon").css("display", "block");}//没有固定查询时显示高级查询
		if($('#parameter_Table').height()==0){
			contentResizeForAdvanced();
		}else{
			contentResizeForFixed();
		}
	}else{//弹框式
		$('#search_box').addClass('searchBox');
		$('#search_box').css('display', 'block');
		$('#search_header').css('display', 'block');
		$('#search_shade').addClass('searchOver');
		if($('#fixedSearchSignInput').val()<=0){$("#searchZoon").css("display", "block");}//没有固定查询时显示高级查询
		$('#search_box').css('width', $('#opt-content').width()*4/5);
		$('#search_box').css('min-width', 820);
		$('#search_box').css('margin-left', -$("#search_box").width()/2);
		fixedToInputWidth();
	}
}

function __searchValueToSubmitForm(obj){
	$('#__search_parameters').remove();
	var searchParameters ="";
	var result=true;
	if("common"==obj.type){
		fixedSearchDealWithParameter();
		searchParameters = $("#search_Parameters").val();
	}else{
		submitParameter();
		searchParameters = $("#search_Parameters").val();
		//验证查询条件中的左右小括号是否匹配
		if(!validateBracket(searchParameters))result=false;
		if('' != searchParameters)searchParameters=processDate(searchParameters);
	}
	if(result){
		creatSearchParametersInput(obj.submitForm,searchParameters);
		return true;
	}else{
		return false;
	}
}
