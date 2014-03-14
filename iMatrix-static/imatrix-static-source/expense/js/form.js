//计算精度:num表示要四舍五入的数,v表示要保留的小数位数。
function decimal(num,v)
{
    var vv = Math.pow(10,v);
    return Math.round(num*vv)/vv;
}

//最大值
function MAX(){
	if(arguments.length==0)
	  return;
	var max_num=arguments[0];
	for(var i=0;i<arguments.length;i++)
	  max_num=Math.max(max_num,arguments[i]);
	return parseFloat(max_num);
}

//最小值
function MIN()
{
	if(arguments.length==0)
	  return;
	var min_num=arguments[0];
	for(var i=0;i<arguments.length;i++)
	  min_num=Math.min(min_num,arguments[i]);
	return parseFloat(min_num);
}

//绝对值
function ABS(val)
{
	if(val=="")val=0;
	return Math.abs(parseFloat(val));
}

//平均值
function AVG()
{
	if(arguments.length==0)
	  return;
	var sum=0;
	for(var i=0;i<arguments.length;i++)
	  sum+=parseFloat(arguments[i]);
	return parseFloat(sum/arguments.length);
}

//人民币大写形式
function RMB(val){
	if(val=="" || isNaN(val))val=0;
	return convertCurrency(parseFloat(val));
}

function convertCurrency(currencyDigits) {
	// Constants:
	var MAXIMUM_NUMBER = 99999999999.99;
	// Predefine the radix characters and currency symbols for output:
	var CN_ZERO = "零";
	var CN_ONE = "壹";
	var CN_TWO = "贰";
	var CN_THREE = "叁";
	var CN_FOUR = "肆";
	var CN_FIVE = "伍";
	var CN_SIX = "陆";
	var CN_SEVEN = "柒";
	var CN_EIGHT = "捌";
	var CN_NINE = "玖";
	var CN_TEN = "拾";
	var CN_HUNDRED = "佰";
	var CN_THOUSAND = "仟";
	var CN_TEN_THOUSAND = "万";
	var CN_HUNDRED_MILLION = "亿";
//	var CN_SYMBOL = "人民币";
	var CN_DOLLAR = "元";
	var CN_TEN_CENT = "角";
	var CN_CENT = "分";
	var CN_INTEGER = "整";

	// Variables:
	var integral; // Represent integral part of digit number.
	var decimal; // Represent decimal part of digit number.
	var outputCharacters; // The output result.
	var parts;
	var digits, radices, bigRadices, decimals;
	var zeroCount;
	var i, p, d;
	var quotient, modulus;

	// Validate input string:
	currencyDigits = currencyDigits.toString();
	if (currencyDigits == "") {
	  alert("Empty input!");
	  return "";
	}
	if (currencyDigits.match(/[^,.\d]/) != null) {
	  alert("Invalid characters in the input string!");
	  return "";
	}
	if ((currencyDigits).match(/^((\d{1,3}(,\d{3})*(.((\d{3},)*\d{1,3}))?)|(\d+(.\d+)?))$/) == null) {
	  alert("Illegal format of digit number!");
	  return "";
	}

	// Normalize the format of input digits:
	currencyDigits = currencyDigits.replace(/,/g, ""); // Remove comma delimiters.
	currencyDigits = currencyDigits.replace(/^0+/, ""); // Trim zeros at the beginning.
	// Assert the number is not greater than the maximum number.
	if (Number(currencyDigits) > MAXIMUM_NUMBER) {
	  alert("超出数值范围!");
	  return "";
	}

	// Process the coversion from currency digits to characters:
	// Separate integral and decimal parts before processing coversion:
	parts = currencyDigits.split(".");
	if (parts.length > 1) {
	  integral = parts[0];
	  decimal = parts[1];
	  // Cut down redundant decimal digits that are after the second.
	  decimal = decimal.substr(0, 2);
	}
	else {
	  integral = parts[0];
	  decimal = "";
	}
	// Prepare the characters corresponding to the digits:
	digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
	radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
	bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
	decimals = new Array(CN_TEN_CENT, CN_CENT);
	// Start processing:
	outputCharacters = "";
	// Process integral part if it is larger than 0:
	if (Number(integral) > 0) {
	  zeroCount = 0;
	  for (i = 0; i < integral.length; i++) {
	   p = integral.length - i - 1;
	   d = integral.substr(i, 1);
	   quotient = p / 4;
	   modulus = p % 4;
	   if (d == "0") {
	    zeroCount++;
	   }
	   else {
	    if (zeroCount > 0)
	    {
	     outputCharacters += digits[0];
	    }
	    zeroCount = 0;
	    outputCharacters += digits[Number(d)] + radices[modulus];
	   }
	   if (modulus == 0 && zeroCount < 4) {
	    outputCharacters += bigRadices[quotient];
	   }
	  }
	  outputCharacters += CN_DOLLAR;
	}
	// Process decimal part if there is:
	if (decimal != "") {
	  for (i = 0; i < decimal.length; i++) {
	   d = decimal.substr(i, 1);
	   if (d != "0") {
	    outputCharacters += digits[Number(d)] + decimals[i];
	   }
	  }
	}
	// Confirm and return the final output string:
	if (outputCharacters == "") {
	  outputCharacters = CN_ZERO + CN_DOLLAR;
	}
	if (decimal == "") {
	  outputCharacters += CN_INTEGER;
	}
//	outputCharacters = CN_SYMBOL + outputCharacters;
	outputCharacters = outputCharacters;
	return outputCharacters;
}

//天数
function DAY(){
	if(arguments.length<2){
		alert("请填写日期");
		return"";
	}
	if((isValidDate(arguments[0])||isValidTime(arguments[0]))&&(isValidDate(arguments[1])||isValidTime(arguments[1]))){	
		var date1 = stringToDate(arguments[0]);
		var date2 = stringToDate(arguments[1]);
		var val=date1.getTime() - date2.getTime();
		var vv = Math.pow(10,1);
	    return Math.round(val/(1000 * 60 * 60 * 24)*vv)/vv;
	}else{
		alert("日期格式无效");
		return"";
	}
}

//小时
function HOUR(){
	if(arguments.length<2){
		alert("请填写日期");
		return"";
	}
	if((isValidDate(arguments[0])||isValidTime(arguments[0]))&&(isValidDate(arguments[1])||isValidTime(arguments[1]))){	
		var date1 = stringToDate(arguments[0]);
		var date2 = stringToDate(arguments[1]);
		var val=date1.getTime() - date2.getTime();
		var vv = Math.pow(10,1);
	    return Math.round(val/(1000 * 60 * 60)*vv)/vv;
	}else{
		alert("日期格式无效");
		return"";
	}
}
function DATE(){
	if(arguments.length<2){
		alert("请填写日期");
		return"";
	}
	if((isValidDate(arguments[0])||isValidTime(arguments[0]))&&(isValidDate(arguments[1])||isValidTime(arguments[1]))){	
		var date1 = stringToDate(arguments[0]);
		var date2 = stringToDate(arguments[1]);
		var val=date1.getTime() - date2.getTime();
		if(val<0){
			alert("日期格式无效");
			return"";
		}else{
			var days = Math.floor(val / (1000 * 60 * 60 * 24)); 
			val -= days * (1000 * 60 * 60 * 24);
			var hours = Math.floor(val / (1000 * 60 * 60)); 
			val -= hours * (1000 * 60 * 60);
			var mins = Math.floor(val / (1000 * 60)); 
//			val -= mins * (1000 * 60);
//			var secs = Math.floor(val / 1000); 
//			return days + "天" + hours + "小时" + mins + "分" + secs + "秒";
			return days + "天" + hours + "小时" + mins + "分";
		}
	}else{
		alert("日期格式无效");
		return"";
	}
}

function isValidDate(dateStr) {
	var datePat = /(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)/; 
	var matchArray = dateStr.match(datePat);
	if (matchArray == null) {
		return false;
	}
	return true;
}
	 
function isValidTime(timeStr) {
	var timePat = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
	var matchArray = timeStr.match(timePat);
	if (matchArray == null) {
		return false;
	}
	return true;
}

function stringToDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,function (a) { return parseInt(a, 10) - 1; }).match(/\d+/g) + ')');
    return date;

}

//computational:计算公式
function init_parseComputational(){
	var inputCals=$("input[pluginType=CALCULATE_COMPONENT]");   
	var inputs=$("input[pluginType=TEXT]");
	for(var j=0;j<inputCals.length;j++){
		$(inputCals[j]).attr("value","");
//		var cal_id = $(inputCals[j]).attr("id");
//		var computational	=	$(inputCals[j]).attr("computational");
//		var arr=new Array();
//		for(var i=0;i<inputs.length;i++){
//			var re  = new RegExp("([\(\+\*\/\^,-]*)"+$(inputs[i]).attr("id")+"([\)\+\*\/\^,-]*)","g");
//			var initCom=computational;
//			computational = computational.replace(re,"$1"+"$('#"+$(inputs[i]).attr("id")+"').attr('value')"+"$2");
//			if(computational!=initCom){
//				arr.push($(inputs[i]).attr("id"));
//			}
//		}
//		$(inputCals[j]).attr("value",decimal(eval(computational),parseInt($(inputCals[j]).attr("prec"))));
//		if(isNaN($(inputCals[j]).attr("value")))$(inputCals[j]).attr("value","");
	}
}

function parseComputational(computational,id){
	try{  
		var inputsText=$("input[pluginType=TEXT]");
		var inputsDate=$("input[pluginType=TIME]");
		var num=inputsText.length+inputsDate.length;
		var inputs=new Array(num);
		var j=0;
		for(var i=0;i<inputsText.length;i++){
			inputs[j]=inputsText[i];
			j++;
		}
		for(var i=0;i<inputsDate.length;i++){
			inputs[j]=inputsDate[i];
			j++;
		}
		for(var i=0;i<inputs.length;i++){
			var re  = new RegExp("([\(\+\*\/\^,-]*)"+$(inputs[i]).attr("id")+"([\)\+\*\/\^,-]*)","g");
			var initCom=computational;
			//if("INTEGER"==$(inputs[i]).attr("datatype")||"LONG"==$(inputs[i]).attr("datatype")||"DOUBLE"==$(inputs[i]).attr("datatype")||"FLOAT"==$(inputs[i]).attr("datatype")){
				computational = computational.replace(re,"$1"+"parseFloat($('#"+$(inputs[i]).attr("id")+"').attr('value'))"+"$2");
			//}else{
			//	computational = computational.replace(re,"$1"+"$('#"+$(inputs[i]).attr("id")+"').attr('value')"+"$2");
			//}
		}
		var floatingReg=new RegExp("^([+-]?)\\d*\\.\\d+$"); //浮点数
		var integerReg=new RegExp("^-?[1-9]\\d*$"); //整数
		var result=eval(computational);
		if(floatingReg.test(result)||integerReg.test(result)){
			$("#"+id).attr("value",decimal(result,parseInt($("#"+id).attr("prec"))));
			if(isNaN($("#"+id).attr("value")))$("#"+id).attr("value","");
		}else{
			$("#"+id).attr("value",result);
		}
	}catch(e){
	    alert("您的计算公式不合法，请参考控件的属性说明修改！错误信息：["+e.message+"]");   
	}
}
//解析表达式
function parse(){
	//init_parseComputational();
}



function selectChange(parentValue,child,cur_val)
{
  var childArray=child.split(",");
  for(var i=0;i<childArray.length;i++)
  {
  	if(childArray[i]!="")
  	{
		$('#'+childArray[i]) .find('option') .remove(); 
		
		var objOption = document.createElement("OPTION");
		objOption.text = '请选择';
		objOption.value = '';
		if(document.getElementById( childArray[i] )!=null){
			document.getElementById( childArray[i] ).options.add(objOption);
		}
  		var arr=eval("arr_"+childArray[i]);		
  		var optionStr=arr[childArray[i]][parentValue];
  		if(optionStr)
  		{
  			var optionArr=optionStr.split(",");	  			
    		for(var j=0;j<optionArr.length;j++)
    		{
    			if(optionArr[j]!="")  
    			{
    				//添加option
  					objOption = document.createElement("OPTION");
  					var valArr=optionArr[j].split(":");
  					var val="";
  					if(valArr.length==1){
  						objOption.text = valArr[0];
  	  					objOption.value = valArr[0];
  	  					val=valArr[0];
  					}else if(valArr.length>1){
  						objOption.text = valArr[1];
  	  					objOption.value = valArr[0];
  	  					val=valArr[0];
  					}
  					document.getElementById( childArray[i] ).options.add(objOption);
  					if(typeof cur_val!='undefined' && cur_val==val) $('#'+childArray[i]).attr("value",cur_val);
  				}
  			}
  		}
		if(parentValue==''){//当前选中的值为空时，清除关联的子下拉菜单的值
			$('#'+childArray[i]).val("");
		}
	}
  }
}

//初始化下拉菜单数组
function initSelect(selstr,parentObj)
{

	var selArray=selstr.split(",");
	for(var i=0;i<selArray.length;i++)
	{
		if(selArray[i]!="")
		{
		   var arr=eval("arr_"+selArray[i]);
	   	   arr[selArray[i]]=new Array();
	   	   var cur_val;
	       var temp = "#"+selArray[i];
	   	   var options = $(temp).children();
	       for(var j=0;j<options.length;j++)
	       {
	         var str=$(options[j]).attr("myvalue");
	         var strtext=$(options[j]).attr("text");
	         if(typeof str!='undefined' && str.indexOf("|")>=0)
	         {
	         	  //更新value和text
	        	 $(options[j]).attr("value",str.substring(0,str.indexOf("|")));
	        	// $(options[j]).attr("text",str.substring(0,str.indexOf("|")));
	         	  
	          	var father=str.substring(str.indexOf("|")+1,str.length);
	          	var optionValue=str.substring(0,str.indexOf("|"));
	          	//记录当前选中值
	   	        if($('#'+selArray[i]).get(0).selectedIndex==j)cur_val=optionValue;
	          	if(typeof arr[selArray[i]][father]=='undefined')
	          		arr[selArray[i]][father]="";
	        		arr[selArray[i]][father]+=optionValue+":"+strtext+",";
	       	 }else{
	       		$(options[j]).attr("value",str);
	       	 }
	       }
	       //重建子菜单 
	      selectChange($("#"+parentObj).attr("value"),selArray[i],cur_val);   
     }
   }
}

//数据获取/点击操作b,id,c,,age,a
function ajaxGetData(referenceControlId, code,version,dataControl,dataField,controlId){
	var root=webRoot;if(typeof(appRoot)!='undefined'&&appRoot!=''){root=appRoot;}
	if(controlsExistFun(referenceControlId) && controlsExistFun(dataControl)){
		$.ajax({
			   type: "POST",
			   dataType:"text",
			   url: root+"/portal/get-data.action",
			   data:'referenceControlValue='+$("#"+referenceControlId).attr("value")+'&code='+code+'&version='+version+'&formControlId='+controlId,
			   success: function(text, textStatus){
				   //text为json格式的数据
				if(text!=""){
					var json = eval("(" + text + ")");
					$.each(json, function(key,value){
						var arr=dataField.split(",");
						var dcArr=dataControl.split(",");
						for(var i=0;i<arr.length;i++){
							if(arr[i]!=""){
								if(arr[i].indexOf("dt_")==0 && arr[i].toLowerCase()==key.toLowerCase()){//自定义表单
									if(value==null){
										value="";
									}
									$("#"+dcArr[i]).attr("value",value);
									
									var disabled=$("#"+dcArr[i]).attr("disabled");
									if(disabled){//如果当前控件禁止编辑的(disabled为真(DATE/TIME))时，则给当前控件后添加的input设值，该input框的值才是实际存到数据库的。该input是dynamic.validate.js中添加的
										var resultInput = $("#"+dcArr[i]).next("input[disablefield='true']");
										$(resultInput).attr("value",value);
									}
								}else if(arr[i]==key){
									$("#"+dcArr[i]).attr("value",value);
									
									var disabled=$("#"+dcArr[i]).attr("disabled");
									if(disabled){//如果当前控件禁止编辑的(disabled为真(DATE/TIME))时，则给当前控件后添加的input设值，该input框的值才是实际存到数据库的。该input是dynamic.validate.js中添加的
										var resultInput = $("#"+dcArr[i]).next("input[disablefield='true']");
										$(resultInput).attr("value",value);
									}
								}
							}
						}
					});
				}else{
					var arr=dataField.split(",");
					var dcArr=dataControl.split(",");
					for(var i=0;i<arr.length;i++){
						if(arr[i]!=""){
							$("#"+dcArr[i]).attr("value","");
							
							var disabled=$("#"+dcArr[i]).attr("disabled");
							if(disabled){//如果当前控件禁止编辑的(disabled为真(DATE/TIME))时，则给当前控件后添加的input设值，该input框的值才是实际存到数据库的。该input是dynamic.validate.js中添加的
								var resultInput = $("#"+dcArr[i]).next("input[disablefield='true']");
								$(resultInput).attr("value",value);
							}
						}
					}
				}
		      },
				error : function(XMLHttpRequest, textStatus) {
					alert(textStatus);
				}
		  }); 
	}
}

//部门人员组件
//liudongxia		
function addUsers(treeType,multiple,value,resultId,hiddenResultId,inputType){
	if(resultId!=""){
		inputType=="textArea"?$("#"+resultId).html(""):$("#"+resultId).attr("value","");
	}
	var type="";
	if(treeType=="COMPANY" || treeType.substring(0,treeType.indexOf("_"))=="MAN"){
		type="user";
	}else if(treeType.substring(0,treeType.indexOf("_"))=="DEPARTMENT"){
		type="department";
	}else if(treeType.substring(0,treeType.indexOf("_"))=="GROUP"){
		type="workGroup";
	}
	var arr=eval(value);
	if(multiple=="true"){
		for(var i=0;i<arr.length;i++){
			if(type=="user" && (arr[i].type=="user" || arr[i].type=="allDepartment" || arr[i].type=="company")){
				if(arr[i].type=="user"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].loginName+",");
				}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html("所有人员,"):$("#"+resultId).attr("value","所有人员,");
					}
					$("#"+hiddenResultId).attr("value","all_user,");
					break;
				}
			}else if(type=="department" && (arr[i].type=="department" || arr[i].type=="allDepartment" || arr[i].type=="company")){
				if(arr[i].type=="department"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].id+",");
				}else if(arr[i].type=="allDepartment" || arr[i].type=="company"){
					inputType=="textArea"?$("#"+resultId).html("所有部门,"):$("#"+resultId).attr("value","所有部门,");
					$("#"+hiddenResultId).attr("value","all_department,");
					break;
				}
			}else if(type=="workGroup" && (arr[i].type=="workGroup" || arr[i].type=="allWorkGroup" || arr[i].type=="company")){
				if(arr[i].type=="workGroup"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html($("#"+resultId).html()+arr[i].name+","):$("#"+resultId).attr("value",$("#"+resultId).attr("value")+arr[i].name+",");
					}
					$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value")+arr[i].id+",");
				}else if(arr[i].type=="allWorkGroup" || arr[i].type=="company"){
					if(resultId!=""){
						inputType=="textArea"?$("#"+resultId).html("所有工作组,"):$("#"+resultId).attr("value","所有工作组,");
					}
					$("#"+hiddenResultId).attr("value","all_workGroup,");
					break;
				}
			}
		}
		if(inputType=="textArea"){
			if(resultId!="" && $("#"+resultId).html()!=""){
				$("#"+resultId).html($("#"+resultId).html().substring(0,$("#"+resultId).html().length-1));
			}
		}else{
			if(resultId!="" && $("#"+resultId).attr("value")!="" && $("#"+resultId).attr("value")!=null ){
				$("#"+resultId).attr("value",$("#"+resultId).attr("value").substring(0,$("#"+resultId).attr("value").length-1));
			}
		}
		if($("#"+hiddenResultId).attr("value")!="" && $("#"+hiddenResultId).attr("value")!=null){
			$("#"+hiddenResultId).attr("value",$("#"+hiddenResultId).attr("value").substring(0,$("#"+hiddenResultId).attr("value").length-1));
		}
	}else if(multiple=="false"){
		if(resultId!=""){
			if(inputType=="textArea"){
				$("#"+resultId).html(arr[0].name);
			}else{
				$("#"+resultId).attr("value",arr[0].name);
			}
		}
		if(type=="user" && arr[0].type=="user"){
			$("#"+hiddenResultId).attr("value",arr[0].loginName);
		}else if(type=="department" && arr[0].type=="department"){
			$("#"+hiddenResultId).attr("value",arr[0].id);
		}else if(type=="workGroup" && arr[0].type=="workGroup"){
			$("#"+hiddenResultId).attr("value",arr[0].id);
		}
	}
}

//隐藏所有的列表控件和查看带有列表控件的表单时的初始化
function ___initHiddenListControl(controlId){
	var listControls=$("input[pluginType=LIST_CONTROL]");
	for(var i=0;i<listControls.length;i++){
		if($(listControls[i]).attr("data_source")==controlId){
			$(listControls[i]).css("display","none");
		}
	}
}

//列表控件的初始化
//fieldRight:用户编辑字段的权限
function parseListControl(fieldRight){
	var listControls=$("input[pluginType=LIST_CONTROL]");
	var ww = $(window).width()-260;
	for(var i=0;i<listControls.length;i++){
		var id=$(listControls[i]).attr("id");
		if($("table[id='tb_"+id+"']").length<=0){
			$(listControls[i]).css("display","none");
			var childForm="<input type=hidden name='dataSrc_"+$(listControls[i]).attr("id")+"' id='dataSrc_"+$(listControls[i]).attr("id")+"' value='"+$(listControls[i]).attr("data_source")+"'>";
			$(listControls[i]).after(childForm);
			var lv_title=$(listControls[i]).attr("lv_title");
			var lv_size=$(listControls[i]).attr("lv_size");
			var lv_sum=$(listControls[i]).attr("lv_sum");
			var lv_cal=$(listControls[i]).attr("lv_cal");
			var lv_field=$(listControls[i]).attr("lv_field");
			var table="<div style='width:"+ww+"px;overflow: auto;'>";
			table+="<table class='form-table-border-left' id='tb_"+id+"'><thead><tr>";
			var lv_titles=lv_title.split(",");
			var lv_sizes=lv_size.split(",");
			for(var j=0;j<lv_titles.length;j++){
				if(lv_titles[j]!=""){
					table+="<th style='width:"+lv_sizes[j]+"px;white-space: nowrap;'>";
					table+=lv_titles[j]+"</th>";
				}
			}
			table+="<th style='width:30px;white-space: nowrap;'>&nbsp;&nbsp;操&nbsp;作&nbsp;&nbsp;</th></tr></thead><tbody>";
			table+=getListControlRow('tb_'+id,lv_sum,lv_field,lv_cal,fieldRight);
			if(isNeedSum(lv_sum)){
				table+=listControlAddSumRow('tb_'+id,lv_sum);
			}
			table+="</tbody></table></div>";
			$('#dataSrc_'+$(listControls[i]).attr("id")).after(table);
		}
	}
}

function ___executeDatepicker(id){
	$("#"+id).datepicker({
		"dateFormat":'yy-mm-dd',
		changeMonth:true,
		changeYear:true,
		showButtonPanel:"true"
	});
}
function ___executeDatetimepicker(id){
	$("#"+id).datepicker({
		"dateFormat":'yy-mm-dd',
		changeMonth:true,
		changeYear:true,
		showSecond: false,
		showMillisec: false,
		"timeFormat": 'hh:mm'
	});
}

//获得自定义列表控件Row
function getListControlRow(tb_id,lv_sum,lv_field,lv_cal,fieldRight){
	var lv_fields=lv_field.split(",");
	var tr_num=$('"'+'tr[id^=\'listControl_tr_\']'+'"').length>0?$('"'+'tr[id^=\'listControl_tr_\']'+'"').length:0;
	var lv_cals=lv_cal.split(",");
	var lv_sums=lv_sum.split(",");
	var tr = "<tr id='listControl_tr_"+tb_id+"_"+tr_num+"'>";
	for(var i=0;i<lv_fields.length;i++){
		//lv_fields[i]:enName:dataType
		if(lv_fields[i]!=""){
			var id=tb_id.substring(tb_id.indexOf("_")+1,tb_id.length);
			tr+="<td>";
			var dataType=lv_fields[i].substring(lv_fields[i].indexOf(":")+1,lv_fields[i].length);
//			onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',el:aa})" //日期控件
			var currentId="listControl_"+tb_id+"_"+tr_num+"_"+(i+1);
			if(dataType=="DATE" || dataType=="TIME"){
				if(lv_cals[i]=='0'){
					if(dataType=="DATE"){
						tr+="<input style='width:95%;' rowid='' name='listControl_"+id+"_"+lv_fields[i]+"' id='"+currentId+"' readonly";
						tr+="></input>";
						tr+='<script type="text/javascript">'
				        +'$("#'+currentId+'").datepicker({'
				        +'"dateFormat":"yy-mm-dd",'
						    +'  changeMonth:true,'
						     +' changeYear:true,'
							+'	showButtonPanel:"true"'
				        +'});'                                       
				     +'</script>';
					}else{
						tr+="<input style='width:95%;' rowid='' name='listControl_"+id+"_"+lv_fields[i]+"' id='"+currentId+"' readonly";
						tr+="></input>";
						tr+='<script type="text/javascript">'
					        +'$("#'+currentId+'").datetimepicker({'
						    +'"dateFormat":"yy-mm-dd",'
							 +'      changeMonth:true,'
							  +'     changeYear:true,'
							  +'     showSecond: false,'
								+'	showMillisec: false,'
								+'	"timeFormat": "hh:mm"'
				        +'});'
				        +'</script>';
					}
				}else{
					tr+="<input style='width:95%;' rowid='' name='listControl_"+id+"_"+lv_fields[i]+"' id='listControl_"+tb_id+"_"+tr_num+"_"+(i+1)+"' readonly";
					tr+="></input>";
				}
			}else{
				tr+="<input style='width:95%;' rowid='' name='listControl_"+id+"_"+lv_fields[i]+"' id='listControl_"+tb_id+"_"+tr_num+"_"+(i+1)+"'";
				if(lv_cals[i]!='0'){
					tr+=" readonly";
				}else{
					tr+=customListAddEven(id,lv_fields[i],lv_sums[i],tr_num,(i+1),lv_cal,lv_field);
				}
				tr+="></input>";
			}
			tr+="</td>";
		}
	}
	tr+="<td>";
	if(fieldRight=="true"){
		tr+="<a href=\"#pos\" class=\"small-button-bg\" onclick=\"listControlAddRow(this,'"+tb_id+"','"+lv_sum+"','"+lv_field+"','"+lv_cal+"','"+fieldRight+"');\"><span class=\"ui-icon ui-icon-plusthick\"></span></a>" ;
		tr+="&nbsp;";
		tr+="<a href=\"#pos\" class=\"small-button-bg\" onclick=\"listControlDelRow(this,'"+tb_id+"','"+lv_sum+"','"+lv_field+"','','');\"><span class=\"ui-icon ui-icon-minusthick\"></span></a>" ;
	}
	tr+="</td></tr>";
	return tr;
}

function customListAddEven(id,lv_field,lv_sum,tr_num,td_num,lv_cal,lv_fields){
	var cellType=(lv_field.split(":"))[1];
	if("INTEGER"==cellType||"LONG"==cellType||"DOUBLE"==cellType||"FLOAT"==cellType){
		return " onkeyup='customListAddCellEven(\""+id+"\",\""+lv_field+"\",\""+lv_sum+"\",\""+tr_num+"\",\""+td_num+"\",\""+lv_cal+"\",\""+lv_fields+"\");'";
	}
	return "";
}

function customListAddCellEven(id,lv_field,lv_sum,tr_num,td_num,lv_cal,lv_fields){//listControl_tb_sevenlist_0_3
	var totalColumnId="listControlSum_tb_"+id+"_td_"+td_num;
	var fieldsName="listControl_"+id+"_"+lv_field;
	var fieldsId="listControl_tb_"+id+"_"+tr_num+"_"+td_num;
	var cellType=(lv_field.split(":"))[1];
	if("INTEGER"==cellType||"LONG"==cellType){
		if(!(_isInteger($("#"+fieldsId).attr("value")))){
			$("#"+fieldsId).attr("value","");
		}
	}else if("DOUBLE"==cellType||"FLOAT"==cellType){
		if(!(_isFloating($("#"+fieldsId).attr("value")))){
			$("#"+fieldsId).attr("value","");
		}
	}
	if("1"==lv_sum){
		customListEven(totalColumnId,fieldsName);
	}
	customListAddCalculateEven(id,lv_field,tr_num,td_num,lv_cal,lv_fields);
}

function customListAddCalculateEven(id,lv_field,tr_num,td_num,lv_cal,lv_fields){
	var calculate=lv_cal.substring(0,lv_cal.length-1);
	var calculates=calculate.split(",");
	var fieldDetails=lv_fields.substring(0,lv_fields.length-1);//数据库字段:字段类型,数据库字段:字段类型,...
	var tempCalculates="";
	for(var i=0;i<calculates.length;i++){
		if(calculates[i]!='0'){
			//[1]+[2]+[3];
			tempCalculates=calculates[i];
			var cal=customListParseCalculateFormula(calculates[i]);
			if(cal!=""){
				cal=cal.substring(0,cal.length-1);
				var cals=cal.split(",");
				for(var j=0;j<cals.length;j++){
					var tempCellValue=getCustomListCalculateCellValue(fieldDetails,cals[j],id,tr_num);
					tempCalculates=tempCalculates.replace("["+cals[j]+"]", tempCellValue);
				}
			}
			if(tempCalculates!=""){
				var calculateFieldCellId="listControl_tb_"+id+"_"+tr_num+"_"+(i+1);
				$("#"+calculateFieldCellId).attr("value",eval(tempCalculates));
				var totalColumnId="listControlSum_tb_"+id+"_td_"+(i+1);
				var fieldsName=$("#"+calculateFieldCellId).attr("name");
				customListEven(totalColumnId,fieldsName);
			}
		}
	}
}

function getCustomListCalculateCellValue(fieldDetails,index,id,tr_num){
	var fields=fieldDetails.split(",");
	var result="";
	for(var i=0;i<fields.length;i++){
		if((i+1)==parseInt(index)){
			result=parseCustomListCalculateValue(fields[i],id,tr_num,index);
			break;
		}
	}
	return result;
}

function parseCustomListCalculateValue(field,id,tr_num,td_num){
	var cellType=field.split(":")[1];
	var cellId="listControl_tb_"+id+"_"+tr_num+"_"+td_num;
	var cellValue=$("#"+cellId).attr("value");
	if("INTEGER"==cellType||"LONG"==cellType){
		return "parseInt("+(cellValue==""?0:cellValue)+")";
	}else if("DOUBLE"==cellType||"FLOAT"==cellType){
		return "parseFloat("+(cellValue==""?0:cellValue)+")";
	}
	return "parseInt(0)";
}

function customListParseCalculateFormula(cal){
	if(cal.indexOf("[")==-1)return "";
	var result=cal.substring(cal.indexOf("[")+1,cal.indexOf("]"));
	cal=cal.replace("["+result+"]","");
	return result+","+customListParseCalculateFormula(cal);
}

function _isInteger(val){
	var integerReg=new RegExp("^-?[1-9]\\d*$"); //整数
	return integerReg.test(val);
}

function _isFloating(val){
	var floatingReg=new RegExp("^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)([eE][+-]?[0-9]+)?$"); //浮点数
	return floatingReg.test(val);
}
function customListEven(totalColumnId,fieldsName){
	var cellType=(fieldsName.split(":"))[1];
	var cellValue=$('input[name="'+fieldsName+'"]');
	var totalValue=0;
	for(var i=0;i<cellValue.length;i++){
		totalValue+=parseCustomListCellValue(cellType,$(cellValue[i]).val());
	}
	$("#"+totalColumnId).attr("value",totalValue);;
}

function parseCustomListCellValue(cellType,cellValue){
	if("INTEGER"==cellType||"LONG"==cellType){
		return parseInt(cellValue==""?0:cellValue);
	}else if("DOUBLE"==cellType||"FLOAT"==cellType){
		return parseFloat(cellValue==""?0:cellValue);
	}
	return 0;
}

//列表控件行
function listControlAddRow(obj,tb_id,lv_sum,lv_field,lv_cal,fieldRight){
	$(obj).parent().parent().after(getListControlRow(tb_id,lv_sum,lv_field,lv_cal,fieldRight));
}

//添加计算事件
function addCalEvent(tb_id,lv_sum,tr_num,lv_cal){
	var lv_cals=lv_cal.split(",");
	var trs=$("#"+tb_id+" tbody tr");
	var arr;
	if(isNeedSum(lv_sum)){
		arr=$(trs[trs.length-2]).find("input");
	}else{
		arr=$(trs[trs.length-1]).find("input");
	}
	for(var i=0;i<arr.length;i++){
		if($(arr[i]).attr("readonly")){
			var result=getLCComponentInputs(tb_id,lv_cals[i],$(arr[i]).attr("id"),tr_num);
			parseLCComputational(result[0],result[1],$(arr[i]).attr("id"),lv_sum,"#listControlSum_"+tb_id+"_td_","listControl_"+tb_id+"_",tb_id);
		}
	}
}

//是否需要合计
function isNeedSum(lv_sum){
	var lv_sums=lv_sum.split(",");
	for(var i=0;i<lv_sums.length;i++){
		if(lv_sums[i]!=""){
			if(lv_sums[i]=='1'){
				return true;
			}
		}
	}
	return false;
}

function getLCComponentInputs(tb_id,computational,id,tr_num){
	var inputs=$('"'+'input[id^=\'listControl_'+tb_id+"_"+tr_num+'\']'+'"');
	var initCom=computational;
	var result=new Array();
	var componentInputs=new Array();
	for(var i=1;i<50;i++){
		if(i>inputs.length){
			break;
		}else{
			if(!$(inputs[i-1]).attr("readonly")){
				var re  = new RegExp("([\(\+\*\/\^,-]*)[\[]+"+i+"[\]]+([\)\+\*\/\^,-]*)","g");
				var inputId=$(inputs[i-1]).attr("id");
				computational = computational.replace(re,"$1"+"(isNaN(parseInt($('#"+inputId+"').attr('value')))?0:parseInt($('#"+inputId+"').attr('value')))"+"$2");
				if(computational!=initCom){
					componentInputs.push($(inputs[i-1]));
				}
			}
		}
	}
	result.push(componentInputs);
	result.push(computational);
	return result;
}

//列表控件/解析表达式
function parseLCComputational(componentInputs,computational,id,lv_sum,preSumId,preInputCeil,tb_id){
	for(var i=0;i<componentInputs.length;i++){
		$(componentInputs[i]).change(function(){
			$("#"+id).attr("value",eval(computational));
			
			var lv_sums=lv_sum.split(",");
			for(var j=0;j<lv_sums.length;j++){
				if(lv_sums[j]!=""||lv_sums[j]!='0'){
					if(lv_sums[j]=='1'){
						var sum=0;
						var trs=$("#"+tb_id+" tbody tr");
						for(var n=0;n<trs.length-1;n++){
							var inputCeil=isNaN(parseInt($("#"+preInputCeil+n+"_"+(j+1)).attr("value")))?0:parseInt($("#"+preInputCeil+n+"_"+(j+1)).attr("value"));
							$(this).attr("value");
							sum+=inputCeil;
						}
						$(preSumId+(j+1)).attr("value",sum);
					}
				}
			}
		});
	}
}

//列表控件增加"合计"行
function listControlAddSumRow(tb_id,lv_sum){
	lv_sum=lv_sum.substring(0,lv_sum.length-1);
	var lv_sums=lv_sum.split(",");
	var tr = "<tr id='listControlSum_"+tb_id+"'>";
	for(var i=0;i<lv_sums.length;i++){
		if("1"==lv_sums[i]){
			tr+="<td><input style='width:95%;' readonly " ;
			var id=tb_id.substring(tb_id.indexOf("_")+1,tb_id.length);
			tr+="id='listControlSum_"+tb_id+"_td_"+(i+1)+"' name='listControlSum_"+id+"_"+(i+1)+"' value='0'";
			tr+="></input></td>";
		}else{
			tr+="<td></td>";
		}
	}
	tr+="<td>合计</td></tr>";
	return tr;
}

//列表控件的"删除"一行
function listControlDelRow(obj,tb_id,lv_sum,lv_field,rowId,dataTableName){
	var tr_all_arr=$("#"+tb_id+" tbody tr");
	var tr_num=$('"'+'tr[id^=\'listControl_tr_\']'+'"').length>0?$('"'+'tr[id^=\'listControl_tr_\']'+'"').length:0;
	if(tr_num>1){
		$(obj).parent().parent().remove();  
		if(isNeedSum(lv_sum)){
			totalAllRow(tb_id,lv_sum,lv_field);
		}
	}
	if(rowId!=""&&dataTableName!=""){
		listControlDeleteRow(rowId,dataTableName);
	}
}
function listControlDeleteRow(rowId,dataTableName){
	$.ajax({
		   type: "POST",
		   url: webRoot+"/common/custom-list-control-deleteRow.htm",
		   data:{dataTableName:dataTableName,dataId:rowId},
		   success: function(text, textStatus){
			   
	      },
			error : function(XMLHttpRequest, textStatus) {
				alert(textStatus);
			}
	  }); 
}

function totalAllRow(tb_id,lv_sum,lv_field){
	var lv_fields=lv_field.split(",");
	var lv_sums=lv_sum.split(",");
	for(var i=0;i<lv_sums.length;i++){
		if("1"==lv_sums[i]){
			var id=tb_id.substring(tb_id.indexOf("_")+1,tb_id.length);
			var sumid="listControlSum_"+tb_id+"_td_"+(i+1);
			var fieldName="listControl_"+id+"_"+lv_fields[i];
			customListEven(sumid,fieldName);
		}
	}
}

function controlsExistFun(dataControls){
	var arr=dataControls.split(",");
	var isControlsExist=true;
	for(var i=0;i<arr.length;i++){
		if(arr[i]!=""){
			if($("#"+arr[i]).size()<=0 || $("input[id='"+arr[i]+"']").length<=0){
				alert("id为"+arr[i]+"的单行文本控件存在,请在表单编辑界面增加该控件.");
				isControlsExist=false;
				return;
			}
		}
	}
	return isControlsExist;
}

function dataSelectionClick(url,title,dataControls){
	if(controlsExistFun(dataControls)){
		$.colorbox({href:url,iframe:true, width:500, height:400,overlayClose:false,title:title});
//		init_tb(url,title);
	}
}

//部门人员控件回调方法(老版本)
function deptSelectCallback(hiddenInputId,showInputId,treeType,multiple,hiddenInputType){
	if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_GROUP_TREE'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
		if(hiddenInputType=='userId'){
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getId());
			}else{
				var loginNames = $("#"+hiddenInputId).val();
				//loginNames!="ALLCOMPANYID" 判断当不是公司所有人员时(MAN_DEPARTMENT_GROUP_TREE,MAN_DEPARTMENT_TREE)
				//loginNames!="ALLWORKGROUP" 判断当不是所有工作组人员时(MAN_GROUP_TREE)
				if(loginNames!="ALLCOMPANYID"&&loginNames!="ALLWORKGROUP"){
					$("#"+hiddenInputId).attr("value",jstree.getIds());
				}
			}
		}else{
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getLoginName());
			}else{
				var loginNames = $("#"+hiddenInputId).val();
				//loginNames!="ALLCOMPANYID" 判断当不是公司所有人员时(MAN_DEPARTMENT_GROUP_TREE,MAN_DEPARTMENT_TREE)
				//loginNames!="ALLWORKGROUP" 判断当不是所有工作组人员时(MAN_GROUP_TREE)
				if(loginNames!="ALLCOMPANYID"&&loginNames!="ALLWORKGROUP"){
					$("#"+hiddenInputId).attr("value",jstree.getLoginNames());
				}
			}
		}
	}else if(treeType=='DEPARTMENT_TREE'){
		if(hiddenInputType=='userId'){
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getDepartmentId());
			}else{
				$("#"+hiddenInputId).attr("value",jstree.getDepartmentIds());
			}
		}else{
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getDepartmentName());
			}else{
				$("#"+hiddenInputId).attr("value",jstree.getDepartmentNames());
			}
		}
	}else if(treeType=='GROUP_TREE'){
		if(hiddenInputType=='userId'){
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getWorkgroupId());
			}else{
				$("#"+hiddenInputId).attr("value",jstree.getWorkgroupIds());
			}
		}else{
			if(multiple=='false'){
				$("#"+hiddenInputId).attr("value",jstree.getWorkgroupName());
			}else{
				$("#"+hiddenInputId).attr("value",jstree.getWorkgroupNames());
			}
		}
	}
}

//部门人员控件回调方法
function deptZtreeSelectCallback(hiddenInputId,showInputId,treeType,multiple,hiddenInputType){
	if(hiddenInputId){
		if(treeType=='COMPANY'||treeType=='MAN_DEPARTMENT_TREE'||treeType=='MAN_GROUP_TREE'){
			if(hiddenInputType=='userId'){
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getId());
				}else{
					var loginNames = $("#"+hiddenInputId).val();
					//loginNames!="ALLCOMPANYID" 判断当不是公司所有人员时(MAN_DEPARTMENT_GROUP_TREE,MAN_DEPARTMENT_TREE)
					//loginNames!="ALLWORKGROUP" 判断当不是所有工作组人员时(MAN_GROUP_TREE)
					if(loginNames!="ALLCOMPANYID"&&loginNames!="ALLWORKGROUP"){
						$("#"+hiddenInputId).attr("value",ztree.getIds());
					}
				}
			}else{
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getLoginName());
				}else{
					var loginNames = $("#"+hiddenInputId).val();
					//loginNames!="ALLCOMPANYID" 判断当不是公司所有人员时(MAN_DEPARTMENT_GROUP_TREE,MAN_DEPARTMENT_TREE)
					//loginNames!="ALLWORKGROUP" 判断当不是所有工作组人员时(MAN_GROUP_TREE)
					if(loginNames!="ALLCOMPANYID"&&loginNames!="ALLWORKGROUP"){
						$("#"+hiddenInputId).attr("value",ztree.getLoginNames());
					}
				}
			}
		}else if(treeType=='DEPARTMENT_TREE'){
			if(hiddenInputType=='userId'){
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getDepartmentId());
				}else{
					$("#"+hiddenInputId).attr("value",ztree.getDepartmentIds());
				}
			}else{
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getDepartmentName());
				}else{
					$("#"+hiddenInputId).attr("value",ztree.getDepartmentNames());
				}
			}
		}else if(treeType=='GROUP_TREE'){
			if(hiddenInputType=='userId'){
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getWorkGroupId());
				}else{
					$("#"+hiddenInputId).attr("value",ztree.getWorkGroupIds());
				}
			}else{
				if(multiple=='false'){
					$("#"+hiddenInputId).attr("value",ztree.getWorkGroupName());
				}else{
					$("#"+hiddenInputId).attr("value",ztree.getWorkGroupNames());
				}
			}
		}
	}
}

function calTextareaLen(value,maxlength,obj){
	value=value.length>maxlength?value.substring(0,maxlength):value;
	$("#"+obj.id).attr("value",value);
}

//所有控件的初始化处理
function ___initControls(signatureVisible,viewable){
	//显示签章时,处理签章控件的隐藏 显示
	__initSignatureControl(signatureVisible);
	//图片上传控件的显示处理
	__imageControls();
	//脚本样式控件的显示处理
	__hiddenJCControls();
	//当是查看页面时，不显示按钮类型的控件
	if(viewable){
		__btnControls();
	}
	//表单处理标签控件
	initLabelControl();
}

//显示签章时,处理签章控件的隐藏 显示
function __initSignatureControl(signatureVisible){
	$("input[pluginType='SIGNATURE_CONTROL']").css("display","none");
	if(signatureVisible){
	 var  signatures = __signatureFields.split(",");//签章字段集合，以逗号隔开的
	 for(var j=0;j<signatures.length;j++){
		var signs = $("input[signaturevisible=true]");//获得文本控件中“是否是签章”设置为“是”的控件集合
		for(var i=0;i<signs.length;i++){
		 if(signatures[j]==$(signs[i]).attr("name")){//签章字段名等于签章控件的[字段名称]属性时，则签章控件隐藏
			$(signs[i]).css("display","none");
			//隐藏部门人员控件
			$("input[resultid='"+signatures[j]+"']").css("display","none");
		 }			
		}
		signs = $("input[pluginType='SIGNATURE_CONTROL']");//获得签章控件集合
		var searchSignControlId="";
		var showSignControlId="";
		for(var i=0;i<signs.length;i++){
		 if(signatures[j]==$(signs[i]).attr("id")){//签章控件的id属性等于签章控件的[id]属性时，则签章控件隐藏
			$(signs[i]).css("display","none");
			
			searchSignControlId = $(signs[i]).attr("hiddenId");
			showSignControlId = $(signs[i]).attr("resultId");
			//隐藏部门人员控件
			$("input[id='"+searchSignControlId+"']").css("display","none");
			$("input[id='"+showSignControlId+"']").css("display","none");
			$("input[hiddenId='"+searchSignControlId+"']").css("display","none");
			$("input[resultid='"+showSignControlId+"']").css("display","none");
		 }			
		}
		
	  }
	}
}

//图片上传控件的显示处理
function __imageControls(){
	var imgs = $("img[pluginType='IMAGE']");
	for(var j=0;j<imgs.length;j++){
		var filename = $(imgs[j]).attr("filename");
		$(imgs[j]).attr("src",imatrixRoot+"/"+filename);
	}
}

//脚本样式控件的显示处理
function __hiddenJCControls(){
	$("img[pluginType='JAVASCRIPT_CSS']").css("display","none");
}
//按钮控件处理
function __btnControls(){
	$("input[plugintype='DATA_ACQUISITION']").css("display","none");
	$("input[plugintype='DATA_SELECTION']").css("display","none");
	$("input[plugintype='BUTTON']").css("display","none");
	$("input[plugintype='STANDARD_LIST_CONTROL']").css("display","none");
}

/************************************************************
附件上传
************************************************************/
/*---------------------------------------------------------
函数名称:uploadFile_d
参          数:
	  uploadUrl:上传附件的url
	  fileSize：文件大小
	  fileType：文件类型
	  fileTypeDescription：文件类型的描述
	  tableId：附件列表的表格id
      viewable 表示是否是只读页面，true：表示只读，false表示不是只读。只读时附件上传控件不显示
      controlId:附件上传控件的id
      pluginType:上传控件的类型：ATTACH_UPLOAD（附件上传控件） 、 IMAGE_UPLOAD（图片上传控件）
功          能:正文上传初始化
------------------------------------------------------------*/
function ___initUploadControl(uploadUrl,fileSize,fileType,fileTypeDescription,tableId,viewable,controlId,pluginType,imageWidth,imageHeight){
	if(viewable=="false"){
		var index = tableId.substring(tableId.lastIndexOf("_")+1,tableId.length);
		var button_placeholder_id = "spanButtonPlaceholder_"+index;
		var targetElement = document.getElementById(button_placeholder_id);
		var fileTip = "请上传文件";
		if(pluginType=="IMAGE_UPLOAD"){//图片上传控件的上传的图片大小最大为50M
			fileSize = 50;
			fileTip = "请上传图片";
			fileType = "*.png;*.jpg;*.jpeg;*.bmp;*.gif";
			fileTypeDescription = "请上传png,jpg,jpeg,bmp,gif格式的图片";
		}
		if(targetElement!=undefined){
			new SWFUpload({
				upload_url: uploadUrl,
				post_params: {"name" : "参数"},
				
				file_post_name : "Filedata", //是POST过去的$_FILES的数组名   () 建议使用这个默认值
				
				file_size_limit : fileSize+" MB",	// 1000MB
				file_types : fileType,
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
				button_image_url : imatrixRoot + "/images/annex.gif",
				button_placeholder_id : button_placeholder_id,
				button_width: 250,
				button_height: 18,
				button_text : '<span class="button">'+fileTip+'(最大'+fileSize+'MB)</span>',
				button_text_style : '.button {border:1px solid #91B8D2;color:#2970A6;  }',
				button_text_top_padding: 0,
				button_text_left_padding: 18,
				button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
				button_cursor: SWFUpload.CURSOR.HAND,
				
				// Flash Settings
				flash_url : imatrixRoot + "/widgets/swfupload/swfupload.swf",
				
				custom_settings : {
					upload_target : "divFileProgressContainer_"+index,
					tableId:tableId,
					controlId:controlId,
					pluginType:pluginType,
					imageWidth:imageWidth,
					imageHeight:imageHeight,
					isUpload : true
				},
				// Debug Settings
				debug: false  //是否显示调试窗口
			});
		}
	}
}

/*---------------------------------------------------------
函数名称:fileDialogBefore
参          数:url
功          能:所好文件之后调用
------------------------------------------------------------*/
function fileDialogBefore(file,swfObject){
	formAttachFileDialogBefore(file,swfObject);
}
/*---------------------------------------------------------
函数名称:formAttachFileDialogBefore
参          数:url
功          能:表单编辑器中的附件上传控件的，选好文件后调用的回调，当表单中既有附件上传控件，又有用户自己定义的原生的上传控件时，用户需要自己手动调用一下该方法
------------------------------------------------------------*/
function formAttachFileDialogBefore(file,swfObject){
	var upload_target = swfObject.customSettings.upload_target;
	if(typeof(upload_target)!='undefined'&&upload_target.indexOf("divFileProgressContainer_")>=0){
		var id = $("#id").attr("value");
		var attachInfo = $("#__attachInfo").attr("value");
		if(id==""||typeof(id)=='undefined'){
			alert("请先保存");
		}else{
			var controlId = swfObject.customSettings.controlId;
			var pluginType = swfObject.customSettings.pluginType;
			swfObject.customSettings.isUpload=true;
			swfObject.setPostParams({"attachInfo":attachInfo,"controlId":controlId,"pluginType":pluginType});
			swfObject.startUpload();
		}
	}
}

/*---------------------------------------------------------
函数名称:rewriteMethod
参          数:url
功          能:所有文件上传之后调用
------------------------------------------------------------*/
function rewriteMethod(file,swfObject){
	formAttachRewriteMethod(file,swfObject);
}

function formAttachRewriteMethod(file,swfObject){
	var upload_target = swfObject.customSettings.upload_target;
	if(typeof(upload_target)!='undefined'&&upload_target.indexOf("divFileProgressContainer_")>=0){
		//清空上传提示信息
		$("#"+upload_target).html("");
		//显示列表
		var tableId = swfObject.customSettings.tableId;
		var controlId = swfObject.customSettings.controlId;
		var pluginType = swfObject.customSettings.pluginType;
		var imageWidth = swfObject.customSettings.imageWidth;
		var imageHeight = swfObject.customSettings.imageHeight;
		var companyId = $("#"+tableId).attr("companyId");
		var userId = $("#"+tableId).attr("userId");
		var attachInfo = $("#__attachInfo").attr("value");
		$.getJSON(
				imatrixRoot + "/mms/form/getAttachments.htm?fileCompanyId="+companyId+"&fileUserId="+userId+"&attachInfo="+attachInfo+"&controlId="+controlId+"&pluginType="+pluginType+"&callback=?",
				function(data){
					$("#"+tableId+"_body").find("tr").remove();
					
					var files = eval(data.data);
					if(pluginType=="ATTACH_UPLOAD"){
						 _____getAttachTbContent(files,tableId,pluginType);
					}else if(pluginType=="IMAGE_UPLOAD"){
						
						_____getImageTbContent(files,tableId,imageWidth,imageHeight,pluginType);
					}
				}
		);
	}
}

function _____getAttachTbContent(files,tableId,pluginType){
	
	for(var i=0;i<files.length;i++){
		var row = "<tr id="+files[i].id+" >";
		var col2 = "<td >"+files[i].fileName+"</td>";
		var col4 = "<td ><a href='#' onclick='____downloadFile(\""+files[i].id+"\",\""+pluginType+"\");'>下载</a>&nbsp;&nbsp;<a href='#' onclick='____deleteFile(\""+files[i].id+"\",\""+tableId+"\",\""+pluginType+"\");'>删除</a></td>";
		var row1="</tr>";
		$("#"+tableId+"_body").append(row+col2+col4+row1); 
	}
}
function _____getImageTbContent(files,tableId,imageWidth,imageHeight,pluginType){
	for(var i=0;i<files.length;i++){
		var row = "<tr id="+files[i].id+" >";
		var col2 = "<td >"+files[i].fileName+"</td>";
		var col3 = "<td ><img src='"+imatrixRoot+"/"+files[i].filePath+"' width='"+imageWidth+"' height='"+imageHeight+"'/></td>";
		var col4 = "<td ><a href='#' onclick='____downloadFile(\""+files[i].id+"\",\""+pluginType+"\");'>下载</a>&nbsp;&nbsp;<a href='#' onclick='____deleteFile(\""+files[i].id+"\",\""+tableId+"\",\""+pluginType+"\");'>删除</a></td>";
		var row1="</tr>";
		$("#"+tableId+"_body").append(row+col2+col3+col4+row1); 
	}
}

function ____deleteFile(fileId,tableId,pluginType){
	if(confirm("确认删除?")){
		$.getJSON(
				imatrixRoot + "/mms/form/deleteAttachment.htm?fileId="+fileId+"&pluginType="+pluginType+"&callback=?",
				function(data){
					//用表格显示
					$("#"+tableId+"_body").find("tr[id='"+fileId+"']").remove();
					alert(data.msg);
				}
		);
	}
}

function ____downloadFile(fileId,pluginType){
	window.open(imatrixRoot + "/mms/form/downloadAttachment.htm?fileId="+fileId+"&pluginType="+pluginType);
}

function destroyUploadControl(){
	for(var i=0;i<SWFUpload.movieCount;i++){
		SWFUpload.instances["SWFUpload_"+i].destroy();//销毁上传对象
	}
}
function fieldNameOk(obj){
	$('#controlId').attr('value', $(obj).attr('value'));
}

//表单打印隐藏控件
function formPrintHideControl(){
	$("input").css("display","none");
	$("select[pluginType=PULLDOWNMENU]").css("display","none");
	$("textarea[pluginType=textarea]").css("display","none");
}
//表单处理标签控件
function initLabelControl(){
	var labels = $("input[pluginType=LABEL]");
	for(var i=0;i<labels.length;i++){
		$(labels[i]).after("<span style='"+$(labels[i]).attr("style")+"' class='"+$(labels[i]).attr("class")+"'>"+$(labels[i]).attr("value")+"</span>");
	}
	$("input[pluginType=LABEL]").css("display","none");
}

//打印表单处理标签控件
function initPrintLabelControl(){
	var labels = $("input[pluginType=LABEL]");
	for(var i=0;i<labels.length;i++){
		if($(labels[i]).attr("printable")=='true'){
			$(labels[i]).after("<span style='"+$(labels[i]).attr("style")+"' class='"+$(labels[i]).attr("class")+"'>"+$(labels[i]).attr("value")+"</span>");
		}
	}
	$("input[pluginType=LABEL]").css("display","none");
}

//解析自定义日期格式
function __parseCustomDateTypeValue(){
	$("input[parseafter='parseafter']").remove();
	var dateControls=$("input[pluginType=TIME]");
	if(dateControls.length==0)return;
	$.each(dateControls,function(i){
		var customtype = $(dateControls[i]).attr("customtype");
		if("CUSTOM"==customtype){
			__parseDateValueByCustomType($(dateControls[i]),$(dateControls[i]).attr("format"));
		}
	});
}
function __parseDateValueByCustomType(inputobj,format){
	if("yyyy-m"==format){
		timeFormatYearMonthStandard(inputobj);
	}else if("m-d"==format){
		timeFormatMonthDayStandard(inputobj);
	}else if("yyyy年m月d日"==format){
		timeFormatYearMonthDayCommon(inputobj);
	}else if("yyyy年m月"==format){
		timeFormatYearMonthCommon(inputobj);
	}else if("m月d日"==format){
		timeFormatMonthDayCommon(inputobj);
	}else if("二O一四年一月一日"==format){
		timeFormatYearMonthDayCH(inputobj);
	}else if("二O一四年一月"==format){
		timeFormatYearMonthCH(inputobj);
	}else if("一月一日"==format){
		timeFormatMonthDayCH(inputobj);
	}else if("h:mm"==format){
		timeFormatHourMinuteStandard(inputobj);
	}else if("h时mm分"==format){
		timeFormatHourMinuteCommon(inputobj);
	}
}
function timeFormatHourMinuteCommon(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=__getCurrentYearMonthDay()+" "+value.replace("时", ":").replace("分", "");
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatHourMinuteStandard(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=__getCurrentYearMonthDay()+" "+value;
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatMonthDayCH(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		var month=value.substring(0, value.indexOf("月"));
		var day=value.substring(value.indexOf("月")+1, value.indexOf("日"));
		var result="";
		result+=__getCurrentYear()+"-";
		result+=month.length==1?"0"+__hanziToShuzi(month):__hanziToShuzi(month);
		result+="-";
		result+=day.length==1?"0"+__hanziToShuzi(day):__hanziToShuzi(day);
		result+=" 00:00";
		resettingParseAfterValue(inputobj,result);
	}
}

function timeFormatYearMonthCH(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		var year=value.substring(0, value.indexOf("年"));
		var month=value.substring(value.indexOf("年")+1, value.indexOf("月"));
		var result="";
		for(var i=0;i<year.length;i++){
			result+=__hanziToShuzi(year.charAt(i));
		}
		result+="-";
		result+=month.length==1?"0"+__hanziToShuzi(month):__hanziToShuzi(month);
		result+="-01 00:00";
		resettingParseAfterValue(inputobj,result);
	}
}

function timeFormatYearMonthDayCH(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		var year=value.substring(0, value.indexOf("年"));
		var month=value.substring(value.indexOf("年")+1, value.indexOf("月"));
		var day=value.substring(value.indexOf("月")+1, value.indexOf("日"));
		var result="";
		for(var i=0;i<year.length;i++){
			result+=__hanziToShuzi(year.charAt(i));
		}
		result+="-";
		result+=month.length==1?"0"+__hanziToShuzi(month):__hanziToShuzi(month);
		result+="-";
		result+=day.length==1?"0"+__hanziToShuzi(day):__hanziToShuzi(day);
		result+=" 00:00";
		resettingParseAfterValue(inputobj,result);
	}
}

function timeFormatMonthDayCommon(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=__getCurrentYear()+"-"+value.replace("月", "-").replace("日", "")+" 00:00";
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatYearMonthCommon(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=value.replace("年", "-").replace("月", "-")+"01 00:00";
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatYearMonthDayCommon(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=value.replace("年", "-").replace("月", "-").replace("日", "")+" 00:00";
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatMonthDayStandard(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=__getCurrentYear()+"-"+value+" 00:00";
		resettingParseAfterValue(inputobj,value);
	}
}

function timeFormatYearMonthStandard(inputobj) {
	var value=$(inputobj).val();
	if(value!=""){
		value=value+"-01 00:00'";
		resettingParseAfterValue(inputobj,value);
	}
}

function resettingParseAfterValue(inputobj,value){
	var html="<input parseafter='parseafter' type='hidden' name='"+$(inputobj).attr("name")+"' value='"+value+"'/>";
	$(inputobj).after(html);
	$(inputobj).attr("name","");
}

function __getCurrentYear(){
	var date=new Date();
	return date.getFullYear();
}

function __getCurrentYearMonthDay(){
	var date=new Date();
	var year=date.getFullYear();
	var month=date.getMonth()+1;
	month =(month<10 ? "0"+month:month); 
	var day=date.getDate();
	return year.toString()+"-"+month.toString()+"-"+day;
}

/**
 * 把汉语的数字转换成阿拉伯数字，即把“O”转换成“0”、“一”转换成“1”、“二”转换成“2”、...、“三十一”转换成“31”。
 * @param val
 * @return
 */
function __hanziToShuzi(val){
	if("O"==val){
		return "0";
	}else if("一"==val){
		return "1";
	}else if("二"==val){
		return "2";
	}else if("三"==val){
		return "3";
	}else if("四"==val){
		return "4";
	}else if("五"==val){
		return "5";
	}else if("六"==val){
		return "6";
	}else if("七"==val){
		return "7";
	}else if("八"==val){
		return "8";
	}else if("九"==val){
		return "9";
	}else if("十"==val){
		return "10";
	}else if("十一"==val){
		return "11";
	}else if("十二"==val){
		return "12";
	}else if("十三"==val){
		return "13";
	}else if("十四"==val){
		return "14";
	}else if("十五"==val){
		return "15";
	}else if("十六"==val){
		return "16";
	}else if("十七"==val){
		return "17";
	}else if("十八"==val){
		return "18";
	}else if("十九"==val){
		return "19";
	}else if("二十"==val){
		return "20";
	}else if("二十一"==val){
		return "21";
	}else if("二十二"==val){
		return "22";
	}else if("二十三"==val){
		return "23";
	}else if("二十四"==val){
		return "24";
	}else if("二十五"==val){
		return "25";
	}else if("二十六"==val){
		return "26";
	}else if("二十七"==val){
		return "27";
	}else if("二十八"==val){
		return "28";
	}else if("二十九"==val){
		return "29";
	}else if("三十"==val){
		return "30";
	}else if("三十一"==val){
		return "31";
	}
	return "";
}
