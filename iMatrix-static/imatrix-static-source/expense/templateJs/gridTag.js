function __getPrmNames(pageName){
	var tempPrmNames="";
	if(pageName!=""){
		tempPrmNames+="prmNames:{";
		tempPrmNames+="rows:'"+pageName+".pageSize',";
		tempPrmNames+="page:'"+pageName+".pageNo',";
		tempPrmNames+="sort:'"+pageName+".orderBy',";
		tempPrmNames+="order:'"+pageName+".order'";
		tempPrmNames+="},";
	}
	return tempPrmNames;
}
function __getColModelCellattr(listColumn){
	var cellattr="";
	cellattr+=",cellattr: function(rowId, tv, rawObject, cm, rdata) {";
	if(listColumn.mergerCell){
		cellattr+="return 'id=\""+listColumn.tableColumn.name+"' + rowId + '\"';";
	}else{
		cellattr+="return 'style=\"padding-left:15px;\"';";
	}
	cellattr+="}";
	return cellattr;
}
function __getColModelEditable(listColumn){
	var colModelAttr="";
	if(listColumn.editable){
		colModelAttr+=",editable:true";
		var editRules="";
		var tableColumnDataType=listColumn.tableColumn.dataType;
		if(listColumn.editRules!=""){
			editRules=listColumn.editRules;
		}
		if(tableColumnDataType=='NUMBER'||tableColumnDataType=='AMOUNT'||tableColumnDataType=='DOUBLE'||tableColumnDataType=='FLOAT'){
			if(editRules!="")editRules+=",";
			editRules+="number:true";
		}else if(tableColumnDataType=='INTEGER'||tableColumnDataType=='LONG'){
			if(editRules!="")editRules+=",";
			editRules+="integer:true";
		}
		if(editRules!=""){
			colModelAttr+=",editrules:{"+editRules+"}";
		}
	}
	return colModelAttr;
}

function __getBooleanCustomFormat(formatSetting){
	var colModelAttr="";
	var names=formatSetting.split(',');
	if(names.length==2){
		colModelAttr+=",formatter:"+names[0].replace("func:","");
		colModelAttr+=",unformat:"+names[1].replace("unfunc:","");
	}else{
		if(formatSetting.indexOf("unfunc:")!=-1){
			colModelAttr+=",unformat:"+names[1].replace("unfunc:","");
		}else{
			colModelAttr+=",formatter:"+names[0].replace("func:","");
		}
	}
	colModelAttr+="";
	return colModelAttr;
}
function __getBooleanDefaultFormat(){
	var colModelAttr=",edittype:'checkbox'";
	colModelAttr+=",formatter:formatCheckbox";
	colModelAttr+=",unformat:unFormatCheckbox";
	colModelAttr+=",editoptions: {value:\"true:false\"}";
	return colModelAttr;
}

function __getBooleanFormat(listColumn){
	var colModelAttr="";
	var formatSetting=listColumn.format;
	if(formatSetting!=""){
		if(formatSetting.indexOf("func:")!=-1){
			colModelAttr+=__getBooleanCustomFormat(formatSetting);
		}else{
			colModelAttr+=__getBooleanDefaultFormat();
		}
	}else{
		colModelAttr+=__getBooleanDefaultFormat();
	}
	return colModelAttr;
}

function __getCurrencyFormat(formatSetting,type){
	var colModelAttr=",formatter:'currency'";
	var currencyType="";
	if(type="dollar"){
		currencyType="$";
	}else{
		currencyType="￥";
	}
	if(formatSetting.indexOf(".")!=-1){
		var decimalPlaces=formatSetting.length-7;
		colModelAttr+=",formatoptions:{decimalSeparator:\".\", thousandsSeparator: \",\", decimalPlaces: "+decimalPlaces+", prefix: \""+currencyType+"\"}";
	}else{
		colModelAttr+=",formatoptions:{decimalSeparator:\" \", thousandsSeparator: \",\", decimalPlaces: 0, prefix: \""+currencyType+"\"}";
	}
	return colModelAttr;
}

function __getCellFormat(listColumn){
	var formatSetting=listColumn.format;
	if(formatSetting==""){
		return "";
	}
	var colModelAttr="";
	if(formatSetting.indexOf("$#,##")!=-1){
		colModelAttr+=__getCurrencyFormat(formatSetting,'dollar');
	}else if(formatSetting.indexOf("￥#,##")!=-1){
		colModelAttr+=__getCurrencyFormat(formatSetting,'renminbi');
	}else if(formatSetting.indexOf("%")!=-1){
		colModelAttr+=",iMatrix_formatter:'"+formatSetting+"'";
		colModelAttr+=",formatter:formatPercent";
		colModelAttr+=",unformat:unFormatPercent";
	}else if(formatSetting=="yyyy-m-d"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d'}";
	}else if(formatSetting=="yyyy-m-d hh:mm:ss"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m-d H:i:s'}";
	}else if(formatSetting=="yyyy-m"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y-m'}";
	}else if(formatSetting=="m-d"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m-d'}";
	}else if(formatSetting=="yyyy年m月d日"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月d日'}";
	}else if(formatSetting=="yyyy年m月d日hh时mm分ss秒"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月d日H时i分s秒'}";
	}else if(formatSetting=="yyyy年m月"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'Y年m月'}";
	}else if(formatSetting=="m月d日"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'m月d日'}";
	}else if(formatSetting=="h:mm"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i'}";
	}else if(formatSetting=="h:mm:ss"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H:i:s'}";
	}else if(formatSetting=="h时mm分"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H时i分'}";
	}else if(formatSetting=="h时mm分ss秒"){
		colModelAttr+=",formatter:'date'";
		colModelAttr+=",formatoptions:{srcformat:'Y-m-d H:i:s',newformat:'H时i分s秒'}";
	}else if(formatSetting.indexOf("func:")!=-1){
		colModelAttr+=__getBooleanCustomFormat(formatSetting);
	}else{
		colModelAttr+=",formatter:'number'";
		if(formatSetting.indexOf("#,##")!=-1){
			if(formatSetting.indexOf(".")!=-1){
				colModelAttr+=",formatoptions:{decimalSeparator:\".\", thousandsSeparator: \",\", decimalPlaces: "+(formatSetting.length-6)+", defaultValue: '0.00'}";
			}else{
				colModelAttr+=",formatoptions:{decimalSeparator:\" \", thousandsSeparator: \",\", decimalPlaces: 0, defaultValue: '0'}";
			}
		}else{
			if(formatSetting.indexOf(".")!=-1){
				colModelAttr+=",formatoptions:{decimalSeparator:\".\", thousandsSeparator: \"\", decimalPlaces: "+(formatSetting.length-2)+", defaultValue: '0.00'}";
			}else{
				colModelAttr+=",formatoptions:{decimalSeparator:\" \", thousandsSeparator: \"\", decimalPlaces: 0, defaultValue: '0'}";
			}
		}
	}
	return colModelAttr;
}

function __getOptionSet(jsonObj){
	var attr="";
	for(var key in jsonObj){
		if(attr!="")attr+=",";
		attr+="'"+key+"':'"+jsonObj[key]+"'";
	}
	return attr;
}

function __getColModelOptionSet(listColumn){
	var colModelAttr="";
	if(listColumn.optionSet!=""){
		var optionSet=__getOptionSet(listColumn.optionSet);
		colModelAttr+=",edittype:'select'";
		colModelAttr+=",formatter:'select'";
		if(listColumn.controlValue=='MULTISELECT'){
			colModelAttr+=",editoptions:{value:{"+optionSet+"},multiple:true}";
		}else{
			colModelAttr+=",editoptions:{value:{"+optionSet+"}}";
		}
	}else if(listColumn.tableColumn.dataType=='BOOLEAN'){
		colModelAttr+=__getBooleanFormat(listColumn);
	}else if(listColumn.controlValue=='TEXTAREA'){
		colModelAttr+=",edittype:'textarea'";
	}else if(listColumn.controlValue=='CUSTOM'){
		colModelAttr+=",edittype:'custom'";
		colModelAttr+=",editoptions: {custom_element: "+listColumn.columnName+"Element, custom_value:"+listColumn.columnName+"Value}";
	}else{
		colModelAttr+=__getCellFormat(listColumn);
	}
	return colModelAttr;
}
function __getColModelDefaultValue(listColumn){
	var colModelAttr="";
	var defaultValue=listColumn.defaultValue;
	if('CURRENT_DATE'==defaultValue){
		colModelAttr+=",unformat:unFormatCurrentDate";
	}else if('CURRENT_TIME'==defaultValue){
		colModelAttr+=",unformat:unFormatCurrentTime";
	}else if('CURRENT_USER_NAME'==defaultValue){
		colModelAttr+=",unformat:unFormatUserName";
	}else if('CURRENT_LOGIN_NAME'==defaultValue){
		colModelAttr+=",unformat:unFormatLoginName";
	}
	return colModelAttr;
}
function __temporaryFormat(listColumn){
	var colModelAttr="";
	var formatSetting=listColumn.format;
	if(formatSetting.indexOf("func:")!=-1){
		var names=formatSetting.split(',');
		if(names[0].indexOf("unfunc:")==-1&&names[0].indexOf("func:")!=-1){
			colModelAttr=",formatter:"+names[0].replace("func:","");
		}
	}
	return colModelAttr;
}
function __getColModels(){
	var frozenColumn_amount=0;
	var listColumn=eval("("+_gridTagParams._list_columns+")");
	var colModel="";
	for(var i=0;i<listColumn.length;i++){
		var tableColumnName=listColumn[i].tableColumn.name;
		colModel+="{";
		if(listColumn[i].tableColumn!=""){
			colModel+="name:'"+tableColumnName+"'";
			colModel+=",index:'"+tableColumnName+"'";
			colModel+=__getColModelCellattr(listColumn[i]);
			colModel+=__getColModelEditable(listColumn[i]);
			colModel+=__getColModelOptionSet(listColumn[i]);
			colModel+=__getColModelDefaultValue(listColumn[i]);
		}else{
			colModel+="name:'_temporary"+i+"'";
			colModel+=",index:'_temporary"+i+"'";
			colModel+=",sortable:false";
			colModel+=__temporaryFormat(listColumn[i]);
		}
		if(listColumn[i].headStyle!=""){
			colModel+=", width:"+listColumn[i].headStyle;
		}
		if(listColumn[i].sortable){
			colModel+=",sortable:true";
		}else{
			colModel+=",sortable:false";
		}
		if(listColumn[i].visible){
			colModel+=",hidden:false";
			frozenColumn_amount++;
		}else{
			colModel+=",hidden:true";
		}
		if(_gridTagParams._frozen_column>=frozenColumn_amount){
			colModel+=",frozen : true";
		}
		colModel+="},";
	}
	
	colModel+=__getDynamicColModel();
	colModel="colModel:["+colModel+"],";
	return colModel;
}
function __getDynamicColumnEditableAttr(dynamicColumn){
	var colModelAttr="";
	if(dynamicColumn.editable){
		colModelAttr+=",editable:true";
		var editRules="";
		var dataType=dynamicColumn.type;
		if(dynamicColumn.editRules!=""){
			editRules=listColumn.editRules;
		}
		if(dataType=='NUMBER'||dataType=='AMOUNT'||dataType=='DOUBLE'||dataType=='FLOAT'){
			if(editRules!="")editRules+=",";
			editRules+="number:true";
		}else if(dataType=='INTEGER'||dataType=='LONG'){
			if(editRules!="")editRules+=",";
			editRules+="integer:true";
		}
		if(editRules!=""){
			colModelAttr+=",editrules:{"+editRules+"}";
		}
	}else{
		colModelAttr+=",editable:false";
	}
	return colModelAttr;
}
function __getDynamicColModel(){
	if(_gridTagParams._dynamic_columns=="")return "";
	var dynamicColumns=eval("("+_gridTagParams._dynamic_columns+")");
	var colModel="";
	for(var i=0;i<dynamicColumns.length;i++){
		colModel+="{";
		colModel+="name:'"+dynamicColumns[i].name+"',index:'"+dynamicColumns[i].name+"'";
		colModel+=__getDynamicColumnEditableAttr(dynamicColumns[i]);
		if(dynamicColumns[i].editoptions!=""){
			colModel+=",edittype:'select'";
			colModel+=",formatter:'select'";
			colModel+=",editoptions:{value:{"+dynamicColumns[i].editoptions+"}}";
		}else if(dynamicColumns[i].type=="BOOLEAN"){
			colModel+=",edittype:'checkbox'";
			colModel+=",formatter:formatCheckbox";
			colModel+=",unformat:unFormatCheckbox";
			colModel+=",editoptions: {value:\"true:false\"}";
		}else if(dynamicColumns[i].type=="TEXTAREA"){
			colModel+=",edittype:'textarea'";
		}
		if(dynamicColumns[i].defaultValue=="CURRENT_DATE"){
			colModel+=",unformat:unFormatCurrentDate";
		}else if(dynamicColumns[i].defaultValue=="CURRENT_TIME"){
			colModel+=",unformat:unFormatCurrentTime";
		}else if(dynamicColumns[i].defaultValue=="CURRENT_USER_NAME"){
			colModel+=",unformat:unFormatUserName";
		}else if(dynamicColumns[i].defaultValue=="CURRENT_LOGIN_NAME"){
			colModel+=",unformat:unFormatLoginName";
		}
		if(dynamicColumns[i].colWidth!=""){
			colModel+=", width:"+dynamicColumns[i].colWidth;
		}
		if(dynamicColumns[i].visible){
			colModel+=",hidden:false";
		}else{
			colModel+=",hidden:true";
		}
		colModel+=",sortable:false";
		colModel+="},";
	}
	return colModel;
}

function __getGridComplete(){
	var attr="gridComplete:function(){contentResize();validatePageInput();savePageInfo();";
	if(_gridTagParams._merger_cell=="true"){
		attr+="__mergerCell();";
	}
	if(_gridTagParams._sub_grid!=""){
		attr+="defaultSelectFirstRow();";
	}
	attr+="totalPageSetting();$gridComplete();},";
	return attr;
}

function __getLoadBeforeSend(){
	var attr="loadBeforeSend:function(xhr, settings){";
	attr+="updateGridPageInfo(xhr, settings);";
	attr+="$loadBeforeSend(xhr, settings);},";
	return attr;
}

function __getColNames(listCols,dynamicCols){
	var attr="colNames:[";
	var listColumns=eval("("+listCols+")");
	
	for(var i=0;i<listColumns.length;i++){
		if(listColumns[i].headerName!=""){
			attr+="'"+listColumns[i].internationName+"',";
		}else{
			attr+="'',";
		}
	}
	if(dynamicCols!=""){
		var dynamicColumns=eval("("+dynamicCols+")");
		for(var i=0;i<dynamicColumns.length;i++){
			if(dynamicColumns[i].colName!=""){
				attr+="'"+dynamicColumns[i].colName+"',";
			}
		}
	}
	attr+="],";
	return attr;
}
function __getDynamicColumnNames(){
	var attr="";
	if(_gridTagParams._dynamic_columns!=""){
		attr+="dynamicColumnNames:[";
		var dynamicColumns=eval("("+_gridTagParams._dynamic_columns+")");
		for(var i=0;i<dynamicColumns.length;i++){
			if(dynamicColumns[i].colName!=""&&dynamicColumns[i].exportable){
				attr+="'"+dynamicColumns[i].colName+"',";
			}
		}
		attr+="],";
	}else{
		attr+="dynamicColumnNames:[],";
	}
	return attr;
}
function __getOnSelectRow(){
	var attr="onSelectRow: function(ids) {";
	if(_gridTagParams._sub_grid!=""){
		attr+="if(ids == null) { ids=0;";
		attr+="if(jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid('getGridParam','records') >0 ) {";
		attr+="jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid('setGridParam',{url:'"+_subGridTagParams._sub_grid_url+"?q=1&id='+(ids)+'"+__getUrlParameter()+"',mtype:'post',postData:_temp_search_parameters,page:1}).trigger('reloadGrid');";
		attr+="}";
		attr+="} else { jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid('setGridParam',{url:'"+_subGridTagParams._sub_grid_url+"?q=1&id='+(ids)+'"+__getUrlParameter()+"',mtype:'post',postData:_temp_search_parameters,page:1}).trigger('reloadGrid'); }";
	}
	attr+="},";
	return attr;
}
function __getSerializeRowData(){
	var attr="serializeRowData:function(data){";
	attr+="if(data.id==0){";
	attr+="data.id=\"\";";
	attr+="}";
	attr+="var arr=$processRowData(data);";
	attr+="return arr;";
	attr+="},";
	return attr;
}
function __getPostData(){
	var attr="postData: { _list_code:'"+_gridTagParams._list_code+"'";
	if(_gridTagParams._dynamic_columns_postData!=""){
		attr+=",dynamicColumns:'"+_gridTagParams._dynamic_columns_postData+"'";
	}
	attr+=",searchParameters:searchParameters";
	attr+="}";
	return attr;
}

function __jqGridOption(){
	var gridOption="{";
	gridOption+="url:encodeURI(\""+_gridTagParams._grid_url+"\"),";
	gridOption+=__getPrmNames(_gridTagParams._page_name);
	gridOption+=__getGridComplete();
	gridOption+=__getLoadBeforeSend();
	gridOption+=__getColNames(_gridTagParams._list_columns,_gridTagParams._dynamic_columns);
	gridOption+=__getDynamicColumnNames();
	gridOption+=__getColModels();
	if(_gridTagParams._row_numbers=="true"){
		gridOption+="rownumbers:true,";
	}
	if(_gridTagParams._custom_property!=""){
		gridOption+=_gridTagParams._custom_property+",";
	}
	gridOption+=__getOnSelectRow();
	gridOption+="onCellSelect:$onCellClick,";
	gridOption+="ondblClickRow: editRow,";
	if(_gridTagParams._edit_url!=""){
		gridOption+="editurl: '"+_gridTagParams._ctx+_gridTagParams._edit_url+"',";
	}
	if(_gridTagParams._row_num!=""){
		gridOption+="rowNum:"+_gridTagParams._row_num+",";
	}
	if(_gridTagParams._row_list!=""){
		gridOption+="rowList:["+_gridTagParams._row_list+"],";
	}
	if(_gridTagParams._multi_select!=""){
		gridOption+="multiselect:"+_gridTagParams._multi_select+",";
	}
	if(_gridTagParams._multibox_select_only!=""){
		gridOption+="multiboxonly:"+_gridTagParams._multibox_select_only+",";
	}
	if(_gridTagParams._sort_name!=""){
		gridOption+="sortname:'"+_gridTagParams._sort_name+"',";
		gridOption+="sortorder:'"+_gridTagParams._sort_order+"',";
	}
	if(_gridTagParams._pagination!=""){
		gridOption+="pager:'#"+_gridTagParams._grid_id+"_pager',";
	}
	gridOption+=__getSerializeRowData();
	if(_gridTagParams._total=="true"){
		gridOption+="footerrow : true,";
		gridOption+="userDataOnFooter : true,";
	}
	gridOption+=__getPostData();
	gridOption+="}";
	return gridOption;
}

function __getInitJqGridParameter(){
	var obj= new Object();
	obj.gridId=_gridTagParams._grid_id;
	obj.deleteUrl=_gridTagParams._ctx+_gridTagParams._delete_url;
	if(_gridTagParams._pagination!=""){
		obj.sortUrl="";
		obj.havePage=true;
	}else{
		if(_gridTagParams._drag_row_url!=""){
			obj.sortUrl=_gridTagParams._ctx+_gridTagParams._drag_row_url;
		}else{
			obj.sortUrl="";
		}
		obj.havePage=false;
	}
	obj.extraParams={_list_code:_gridTagParams._list_code};
	return obj;
}

function __getGroupHeaders(){
	var arr=new Array();
	if(_gridTagParams._group_header=="")return arr;
	var groupHeader=eval("("+_gridTagParams._group_header+")");
	for(var i=0;i<groupHeader.length;i++){
		var gh=new Object();
		gh.startColumnName=groupHeader[i].startColumnName;
		gh.numberOfColumns=groupHeader[i].numberOfColumns;
		gh.titleText=groupHeader[i].titleText;
		arr.push(gh);
	}
	return arr;
}

var jqGridOption={};

function __init_JqGrid(){
	jqGridOption=eval("("+__jqGridOption()+")");
	var searchParameters="";
	if($("#___searchParameters").attr("id")=="___searchParameters"){
		searchParameters=$("#___searchParameters").attr("value");
		jqGridOption.postData.searchParameters=searchParameters;
	}
	var initJqGridParameter=__getInitJqGridParameter();
	initJqGrid(initJqGridParameter);
	if(_gridTagParams._frozen_column>0){
		jQuery("#"+_gridTagParams._grid_id).jqGrid('setFrozenColumns');
	}
	if(_gridTagParams._group_header_sign!=""){
		jQuery("#"+_gridTagParams._grid_id).jqGrid('destroyGroupHeader');
		jQuery("#"+_gridTagParams._grid_id).jqGrid('setGroupHeaders', {
		 useColSpanStyle: true, 
		 groupHeaders:__getGroupHeaders()
		});
	}
}

function __mergerCell(){
	var listColumns=eval("("+_gridTagParams._list_columns+")");
	for(var i=0;i<listColumns.length;i++){
		if(listColumns[i].mergerCell&&listColumns[i].tableColumn!=""){
			if(listColumns[i].mainKeyName!=""){
				merger(_gridTagParams._grid_id, listColumns[i].tableColumn.name,listColumns[i].mainKeyName);
			}else{
				merger(_gridTagParams._grid_id, listColumns[i].tableColumn.name,'');
			}
		}
	}
}

function __getTagName(controlValue){
	if('CHECKBOX'==controlValue){
		return "checkbox";
	}else if('MULTISELECT'==controlValue||'SELECT'==controlValue){
		return "select";
	}else if('TEXTAREA'==controlValue){
		return "textarea";
	}else {
		return "input";
	}
}

function __executeMultiselectControl(_tagName,id,tableColumnName){
	jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).multiselect({
		checkAllText:"全选",
		uncheckAllText:"清除",
		noneSelectedText:"请选择",
		selectedList:4
	});
}
function __executeDateControl(_tagName,id,tableColumnName){
	jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).attr("readonly","readonly");
	jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).datepicker({
       	"dateFormat":'yy-mm-dd',
	      changeMonth:true,
	      changeYear:true,
	      showButtonPanel:"true",
	      onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_'+tableColumnName,dateText:dateText});},
	      onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_'+tableColumnName});},
	      onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_'+tableColumnName});}
       });
}
function __executeTimeControl(_tagName,id,tableColumnName){
	jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).attr("readonly","readonly");
	jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).datetimepicker({
	      "dateFormat":'yy-mm-dd',
	       changeMonth:true,
	       changeYear:true,
	       showSecond: false,
			showMillisec: false,
			"timeFormat": 'hh:mm',
			onSelect:function(dateText, inst){$dateOnSelect({rowid:id,currentInputId:id+'_'+tableColumnName,dateText:dateText});},
		    onChangeMonthYear:function(){$dateOnChangeMonthYear({rowid:id,currentInputId:id+'_'+tableColumnName});},
		    onClose:function(){$dateOnClose({rowid:id,currentInputId:id+'_'+tableColumnName});}
	   });
}
function __executeCellEvent(_tagName,id,tableColumnName,eventType,columnName){
	var eventItems=eventType.split(',');
	for(var i=0;i<eventItems.length;i++){
		__executeCellEventByType(_tagName,id,tableColumnName,eventItems[i],columnName);
	}
}
function __executeCellOnclickEvent(_tagName,id,tableColumnName,eventType,columnName){
	return "jQuery(\""+_tagName+"[id='"+id+"_"+tableColumnName+"']\",\"#"+_gridTagParams._grid_id+"\").click(function(){"+columnName+"Click({rowid:\""+id+"\",currentInputId:\""+id+'_'+tableColumnName+"\"});});";
}
function __executeCellOnchangeEvent(_tagName,id,tableColumnName,eventType,columnName){
	return "jQuery(\""+_tagName+"[id='"+id+"_"+tableColumnName+"']\",\"#"+_gridTagParams._grid_id+"\").change(function(){"+columnName+"Click({rowid:\""+id+"\",currentInputId:\""+id+'_'+tableColumnName+"\"});});";
}
function __executeCellOndblclickEvent(_tagName,id,tableColumnName,eventType,columnName){
	return "jQuery(\""+_tagName+"[id='"+id+"_"+tableColumnName+"']\",\"#"+_gridTagParams._grid_id+"\").dblclick(function(){"+columnName+"Click({rowid:\""+id+"\",currentInputId:\""+id+'_'+tableColumnName+"\"});});";
}
function __executeCellBlurEvent(_tagName,id,tableColumnName,eventType,columnName){
	return "jQuery(\""+_tagName+"[id='"+id+"_"+tableColumnName+"']\",\"#"+_gridTagParams._grid_id+"\").blur(function(){"+columnName+"Click({rowid:\""+id+"\",currentInputId:\""+id+'_'+tableColumnName+"\"});});";
}
function __executeCellEventByType(_tagName,id,tableColumnName,eventType,columnName){
		if("ONCLICK"==eventType){
			jQuery(_tagName+"[id='"+id+"_"+tableColumnName+"']","#"+_gridTagParams._grid_id).attr("readonly","readonly");
			eval(__executeCellOnclickEvent(_tagName,id,tableColumnName,eventType,columnName));
		}else if("ONCHANGE"==eventType){
			eval(__executeCellOnchangeEvent(_tagName,id,tableColumnName,eventType,columnName));
		}else if("ONDBLCLICK"==eventType){
			eval(__executeCellOndblclickEvent(_tagName,id,tableColumnName,eventType,columnName));
		}else if("BLUR"==eventType){
			eval(__executeCellBlurEvent(_tagName,id,tableColumnName,eventType,columnName));
		}
}
function __executeSelectTree(_tagName,id,tableColumnName,controlValue){
	var vals=controlValue.split(',');
	jQuery(_tagName+'[id="'+id+'_'+tableColumnName+'"]','#'+_gridTagParams._grid_id).attr("readonly","readonly");
	jQuery(_tagName+'[id="'+id+'_'+tableColumnName+'"]','#'+_gridTagParams._grid_id).click(function(){formGridTree({rowid:id,currentInputId:id+'_'+tableColumnName,hiddenField:"'"+vals[1]+"'",treeType:"'"+vals[2]+"'",isMutiply:"'"+vals[3]+"'",useType:true});});
}

function editFun(id){
	var listColumns=eval("("+_gridTagParams._list_columns+")");
	for(var i=0;i<listColumns.length;i++){
		var listCol=listColumns[i];
		var tableCol=listCol.tableColumn;
		if(tableCol=="")break;
		var _tagName=__getTagName(listCol.controlValue);
		if('MULTISELECT'==listCol.controlValue){
			__executeMultiselectControl(_tagName,id,tableCol.name);
		}
		if('DATE'==tableCol.dataType){
			__executeDateControl(_tagName,id,tableCol.name);
		}else if('TIME'==tableCol.dataType){
			__executeTimeControl(_tagName,id,tableCol.name);
		}else if(''!=listCol.eventType){
			__executeCellEvent(_tagName,id,tableCol.name,listCol.eventType,listCol.columnName);
		}else if(listCol.controlValue.indexOf("SELECT_TREE")>-1){
			__executeSelectTree(_tagName,id,tableCol.name,listCol.controlValue);
		}
	}
	if(_gridTagParams._dynamic_columns!=""){
		__dynamicColumnEditFun(id);
	}
}

function __executeDynamicColumnEvent(controlType,id,name,eventType){
	if('TEXT'==controlType){
		if("ONCHANGE"!=eventType){
			__executeCellEventByType("input",id,name,eventType,name);
		}
	}else if("SELECT"==controlType){
		if("ONCHANGE"==eventType){
			__executeCellEventByType("select",id,name,eventType,name);
		}
	}
}

function __dynamicColumnEditFun(id){
	var dynamicColumns=eval("("+_gridTagParams._dynamic_columns+")");
	for(var i=0;i<dynamicColumns.length;i++){
		var dynamicCol=dynamicColumns[i];
		if('DATE'==dynamicCol.type){
			__executeDateControl("input",id,dynamicCol.name);
		}else if('TIME'==dynamicCol.type){
			__executeTimeControl("input",id,dynamicCol.name);
		}else{
			__executeDynamicColumnEvent(dynamicCol.edittype,id,dynamicCol.name,dynamicCol.eventType);
		}
	}
}

function __getUrlParameter(){
	if(_subGridTagParams._sub_grid_url_Parameter!=""){
		return "&"+_subGridTagParams._sub_grid_url_Parameter;
	}else{
		return "";
	}
}
function __getSubGridColModels(){
	var listColumn=eval("("+_subGridTagParams._sub_grid_list_columns+")");
	var colModel="";
	for(var i=0;i<listColumn.length;i++){
		var tableColumnName=listColumn[i].tableColumn.name;
		colModel+="{";
		if(listColumn[i].tableColumn!=""){
			colModel+="name:'"+tableColumnName+"'";
			colModel+=",index:'"+tableColumnName+"'";
			colModel+=__getColModelEditable(listColumn[i]);
			colModel+=__getColModelOptionSet(listColumn[i]);
			colModel+=__getColModelDefaultValue(listColumn[i]);
		}else{
			colModel+="name:'_temporary"+i+"'";
			colModel+=",index:'_temporary"+i+"'";
			colModel+=",sortable:false";
			colModel+=__temporaryFormat(listColumn[i]);
		}
		if(listColumn[i].headStyle!=""){
			colModel+=", width:"+listColumn[i].headStyle;
		}
		if(listColumn[i].sortable){
			colModel+=",sortable:true";
		}else{
			colModel+=",sortable:false";
		}
		if(listColumn[i].visible){
			colModel+=",hidden:false";
		}else{
			colModel+=",hidden:true";
		}
		colModel+="},";
	}
	colModel="colModel:["+colModel+"],";
	return colModel;
}
function __getSubGridOnSelectRow(){
	var attr="onSelectRow: function(ids) {";
	attr+="if(ids && ids!==lastselsub){";
	attr+="jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid('restoreRow',lastselsub);";
	attr+="jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid('editRow',ids,true);";
	attr+="lastselsub=ids;";
	attr+="}";
	attr+="},";
	return attr;
}

function __subGridOption(){
	var subGridOption="{";
	if(_subGridTagParams._sub_grid_url_Parameter!=""){
		subGridOption+="url:encodeURI(\""+_subGridTagParams._sub_grid_url+"?"+_subGridTagParams._sub_grid_url_Parameter+"\"),";
	}else{
		subGridOption+="url:encodeURI(\""+_subGridTagParams._sub_grid_url+"\"),";
	}
	subGridOption+=__getPrmNames(_subGridTagParams._sub_grid_page_name);
	subGridOption+=__getColNames(_subGridTagParams._sub_grid_list_columns,"");
	subGridOption+=__getSubGridColModels();
	if(_subGridTagParams._sub_grid_row_numbers=="true"){
		subGridOption+="rownumbers:true,";
	}
	subGridOption+=__getSubGridOnSelectRow();
	if(_subGridTagParams._sub_grid_edit_url!=""){
		subGridOption+="editurl: '"+_subGridTagParams._sub_grid_ctx+_subGridTagParams._sub_grid_edit_url+"',";
	}
	if(_subGridTagParams._sub_grid_row_num!=""){
		subGridOption+="rowNum:"+_subGridTagParams._sub_grid_row_num+",";
	}
	if(_subGridTagParams._sub_grid_row_list!=""){
		subGridOption+="rowList:["+_subGridTagParams._sub_grid_row_list+"],";
	}
	if(_subGridTagParams._sub_grid_multi_select!=""){
		subGridOption+="multiselect:"+_subGridTagParams._sub_grid_multi_select+",";
	}
	if(_subGridTagParams._sub_grid_multibox_select_only!=""){
		subGridOption+="multiboxonly:"+_subGridTagParams._sub_grid_multibox_select_only+",";
	}
	if(_subGridTagParams._sub_grid_sort_name!=""){
		subGridOption+="sortname:'"+_subGridTagParams._sub_grid_sort_name+"',";
		subGridOption+="sortorder:'"+_subGridTagParams._sub_grid_sort_order+"',";
	}
	if(_subGridTagParams._sub_grid_pagination!=""){
		subGridOption+="pager:'#"+_subGridTagParams._sub_grid_id+"_pager',";
	}
	if(_subGridTagParams._sub_grid_total=="true"){
		subGridOption+="footerrow : true,";
		subGridOption+="userDataOnFooter : true,";
	}
	subGridOption+="postData: { _list_code:'"+_subGridTagParams._sub_grid_list_code+"'}";
	subGridOption+="}";
	return subGridOption;
}
var lastselsub;
function _init_subGrid(){
	var attr="jQuery('#"+_subGridTagParams._sub_grid_id+"').jqGrid(";
	attr+=__subGridOption();
	attr+=")";
	if(_subGridTagParams._sub_grid_pagination!=""){
		attr+=".navGrid('#"+_subGridTagParams._sub_grid_id+"_pager',{edit:false,add:false,del:false,search:false})";
	}
	return attr;
}

$(document).ready(function(){
	if(_gridTagParams._sub_grid!=""){
		eval("("+_init_subGrid()+")");
	}
	__init_JqGrid();
});

