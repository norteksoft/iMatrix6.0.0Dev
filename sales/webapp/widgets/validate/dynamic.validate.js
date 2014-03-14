
function addFormValidate(setting,formId){
	if(typeof(setting)=='undefined' || setting=="") return;
	
	if('[{request:"false",readonly:"true",controltype:"allreadolny"}]'==setting.toLowerCase()){
		allFieldForbidden(formId);
	}else{
		var json = eval("(" + setting + ")");
		$.each(json, function(key,values){
			validateHandler(this);
		});
	}
}
function validateHandler(obj){
	if(obj.controlType.toLowerCase()=="radio"  ){
		//单选多选按钮
		var name = obj.id;// 当控件类型是radio或checkbox时 id存的是控件的name属性
		var inputs = $("input:radio[name="+name+"]");
		if(inputs.length==0)return;
		if(obj.request=="true"){
			$(inputs[0]).after('<span style="color:red;">*</span>');
			addRule(inputs[0], 'required', '必填');
		}
		if(obj.readonly=="true"){
			$(inputs).attr('disabled', 'disabled');
		}
	}else if(obj.controlType.toLowerCase()=="checkbox"){
		var name = obj.id;// 当控件类型是radio或checkbox时 id存的是控件的name属性
		var inputs = $("input:checkbox[name="+name+"]");
		if(inputs.length==0)return;
		if(obj.request=="true"){
			$(inputs[0]).after('<span style="color:red;">*</span>');
			addRule(inputs[0], 'required', '必填');
		}
		if(obj.readonly=="true"){
			$(inputs).attr('disabled', 'disabled');
		}	
	}else{
		if(obj.id==""){ return ; }
		var inputobj = $("input[name="+obj.id+"]");
		if(obj.controlType.toLowerCase()=="textarea"){
			inputobj = $("textarea[name="+obj.id+"]");
		}else if(obj.controlType.toLowerCase()=="select"){
			inputobj = $("select[id="+obj.id+"]");
		}
		if(obj.request=="true"){
			var formatType = obj.formatType;
			if(formatType=='null'){
				if(obj.datatype=="DATE" || obj.datatype=="TIME"){
					addRule($(inputobj), 'required', '必填');
				}else if(obj.datatype=="INTEGER"){
					addRule($(inputobj), 'required', '必填');
				}else if(obj.datatype=="LONG"){
					addRule($(inputobj), 'required', '必填');
				}else if(obj.datatype=="DOUBLE"){
					addRule($(inputobj), 'required', '必填');
				}else{
					addRule($(inputobj), 'required', '必填');
				}
			}else if(formatType=='string'||formatType=='enum'){
				addRule($(inputobj), 'required', '必填');
			}else{
				addRule($(inputobj), 'required', '必填');
			}
			$(inputobj).after('<span style="color:red;">*</span>');
		}
		if(obj.readonly=="true"){
			if(obj.controlType.toLowerCase()=="select"||obj.controlType.toLowerCase()=="radio"||obj.controlType.toLowerCase()=="checkbox"){
				$(inputobj).attr('disabled', 'disabled');
			}else if(obj.datatype=="DATE" || obj.datatype=="TIME"){
				$(inputobj).attr('onclick','');
			}else{
				$(inputobj).attr('readonly','readonly');
			}
		}
	}
}

function allFieldForbidden(formId){
	var inputs = $("#"+formId+" input[name!='transitionName']");
	for(var j = 0; j < inputs.length; j++){
		if($(inputs[j]).attr("type").toLowerCase()=="radio"||$(inputs[j]).attr("type").toLowerCase()=="checkbox"){
			$(inputs[j]).attr('disabled', 'disabled');
		}else{
			$(inputs[j]).attr( "onfocus","" );
			$(inputs[j]).attr('onclick','');
			$(inputs[j]).attr('readonly', 'readonly');
		}
		if($("#"+$(inputs[j]).attr("id")+"Div").length==1){
			$("#"+$(inputs[j]).attr("id")+"Div").hide();
		}
	}
	inputs = $("#"+formId+" textarea");
	for(var j = 0; j < inputs.length; j++){
		$(inputs[j]).attr('readonly', 'readonly');
	}
	inputs = $("#"+formId+" select");
	for(var j = 0; j < inputs.length; j++){
		$(inputs[j]).attr('disabled', 'disabled');
	}
}
function addRule(obj, type, msg){
	var c = $(obj).attr('class');
	if(typeof(c)=="undefined"){ c=""; }
	var vc = c.match(/\{[\W\w]+\}/ig);
	if(vc==null||vc==''){
		c = c+' {'+type+':true, messages:{'+type+':\''+msg+'\'}}';
	}else{
		var vo = eval('('+vc+')');
		vo[type]=true;
		if(typeof(vo['messages'])=='undefined'){
			vo['messages']={};
		}
		vo['messages'][type]=msg;
		c = c.replace(vc, json2str(vo));
	}
	$(obj).attr('class', c);
}
function json2str(o) {
	var arr = [];
	var fmt = function(s) {
		if (typeof s == 'object' && s != null) return json2str(s);
		return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
	};
	for (var i in o) arr.push("" + i + ":" + fmt(o[i]));
	return '{' + arr.join(',') + '}';
}