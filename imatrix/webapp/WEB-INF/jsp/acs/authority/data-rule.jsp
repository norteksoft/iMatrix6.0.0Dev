<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据规则</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script src="${acsCtx}/js/authority-data-rule.js" type="text/javascript"></script>
	<script src="${acsCtx}/js/authority.js" type="text/javascript"></script>
	<script type="text/javascript">
		//通用消息提示
		function showMessage(id, msg){
			if(msg != ""){
				$("#"+id).html(msg);
			}
			$("#"+id).show("show");
			setTimeout('$("#'+id+'").hide("show");',3000);
		}
		//新建
		function createDataRule(url){
			
			if($("#menuId").val()==''){
				alert("请选择对应的菜单！");
			}else{
				ajaxSubmit("defaultForm",url,"main_zone",validateDataRule);
			}
		}
		//验证
		function validateDataRule(){
			getContentHeight();
			$("#saveForm").validate({
				submitHandler: function() {
					var cansave=false;
					if($("#simple").attr("checked")){//如果是简易设置
						cansave=true;
					}else{//高级设置
						cansave=iMatrix.getFormGridDatas("saveForm","conditionGrid");
					}
					if(cansave){
						cansave = codeRule();
						if(cansave){
							$("#deparmentInheritable").attr("value",$("#deptInheri").attr("checked"));
							$("#saveForm").attr('action','${acsCtx}/authority/validate-only-code.htm');
							$("#saveForm").ajaxSubmit(function (id){
								if(id=='ok'){
									ajaxSubmit('saveForm','${acsCtx}/authority/data-rule-save.htm','main_zone',saveCallback);
								}else if(id=='no'){
									showMessage("message", "<font color=\"red\">保存失败,编号已存在</font>");
								}
							});
						}
					}
				},
				rules: {
					ruleTypeName: "required",
					code: "required",
					name: "required",
					dataTableName: "required"
				},
				messages: {
					ruleTypeName: "必填",
					code: "必填",
					name: "必填",
					dataTableName: "必填"
				}
			});
		}

		function codeRule(){
			var code = $("#code").val();
			if(code.indexOf("dataRule-")==0&&code.length>27){
				alert("默认编号时超出最大长度27");
				return false;
			}
			if(code.indexOf(" ")==0||code.lastIndexOf(" ")==code.length-1){//最前面有空格
				alert("编码前后不能包含空格!");
				return false;
			}
			if(code.indexOf(">")>=0||code.indexOf("<")>=0||code.indexOf("\"")>=0||code.indexOf("'")>=0
					||code.indexOf("/")>=0){//包含特殊字符
				alert("编码不能包含符号>、<、\"、'、/");
				return false;
			}
			return true;
		}
		function saveCallback(){
			showMsg();
			validateDataRule();
		}
		//保存
		function saveDataRule(url){
			$("#saveForm").attr("action",url);
			$("#saveForm").submit();
		}
		//修改
		function updateDataRule(url){
			var ids = jQuery("#dataRuleTable").getGridParam('selarrrow');
			if(ids==""){
				showMessage("message", "<font color=\"red\">请选择一条数据</font>");
			}else if(ids.length > 1){
				showMessage("message", "<font color=\"red\">只能选择一条数据</font>");
			}else if(ids.length == 1){
				ajaxSubmit("defaultForm",url+"?dataRuleId="+ids[0],"main_zone",validateDataRule);
			}
		}
		//删除
		function deleteDataRule(url){
			var ids = jQuery("#dataRuleTable").getGridParam('selarrrow');
			if(ids.length<=0){
				showMessage("message", "<font color=\"red\">请选择数据</font>");
			}else {
				$.ajax({
					data:{ids:ids.join(",")},
					type:"post",
					url:webRoot+"/authority/data-rule-validateDelete.htm",
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						if(data=='ok'){
							if(confirm("确定要删除吗？")){
								confirmDelete(ids,url);
							}
						}else{
							if(confirm(data)){
								confirmDelete(ids,url);
							}
						}
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
			}
		}

		function confirmDelete(ids,url){
			
				$.ajax({
					data:{ids:ids.join(",")},
					type:"post",
					url:url,
					beforeSend:function(XMLHttpRequest){},
					success:function(data, textStatus){
						ajaxSubmit("defaultForm",webRoot+'/authority/data-rule.htm',"main_zone",deleteCallBack);
					},
					complete:function(XMLHttpRequest, textStatus){},
			        error:function(){
		
					}
				});
		}

		function deleteCallBack(){
			showMessage("message", "<font color=\"green\">删除成功</font>");
		}

		function backRuleType(ruleTypeId,ruleTypeName){
			$("#ruleTypeId").attr("value",ruleTypeId);
			$("#ruleTypeName").attr("value",ruleTypeName);
		}
		function selectDataTable(pos){
			var dataTableName=$("#dataTableName").val();
			var type = $("input[name='simplable']:checked");
			var settingval = $(type).val();
			var sign=false;
			if(dataTableName != ''&&settingval=="false"){//当修改数据表且是高级设置时
				if(confirm("确定修改数据表吗？所选择的规则条件将被删除！")){
					$("#dataTableId").attr("value","");
					$("#dataTableName").attr("value","");
					jQuery("#conditionGrid").jqGrid('clearGridData');
					_add_row('conditionGrid');
					sign=true;
				}
			}else{
				sign=true;
			}
			if(sign){
				//pos:值为fast或dataRule
				var dataRuleId = $("#dataRuleId").val();
				var url = webRoot+"/authority/data-rule-selectDataTable.htm?selectPageFlag="+true+"&position="+pos;
				if(dataRuleId!=""&&typeof(dataRuleId)!='undefined'){
					url = url+"&dataRuleId="+dataRuleId;
				}
				$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"数据表"});
			}
		}

		
		//obj:{rowid:id,currentInputId:id_fieldName}
		function fieldNameClick(obj){
			var dataTableId=$("#dataTableId").val();
			if($("#dataTableId").val()!=''){
				$.colorbox({href:webRoot+"/authority/data-rule-selectColumn.htm?tableId="+dataTableId+"&currentInputId="+obj.currentInputId,iframe:true, innerWidth:600, innerHeight:400,overlayClose:false,title:"数据表"});
			}else{
				alert("请先选择表单！");
			}
		}

		//点击value值事件
		function conditionValueEvent(rowId){
			var keyValue = jQuery("#conditionGrid").jqGrid('getCell',rowId,"keyValue");
			if(typeof(keyValue)!='boolean'&&keyValue!=""){
				if(keyValue.indexOf(":")>=0){//枚举类型和键值对情况,即是key:'value',...时
					$.colorbox({href:webRoot+"/authority/data-rule-setValue.htm?dataValue="+keyValue+"&currentInputId="+rowId,iframe:true, innerWidth:300, innerHeight:300,overlayClose:false,title:"请选择"});
				}else{//选项组
					$.ajax({
						data:{dataValue:keyValue,currentInputId:rowId},
						type:"post",
						url:webRoot+"/authority/data-rule-getOptionValue.htm",
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							var values = data.split("-");
							$.colorbox({href:webRoot+"/authority/data-rule-setValue.htm?dataValue="+values[0]+"&currentInputId="+values[1],iframe:true, innerWidth:300, innerHeight:300,overlayClose:false,title:"请选择"});
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){}
					});
				}
			}
		}

		//验证d字符参数
		function validateFieldString(obj,conditionId){
			var value = $(obj).val().replace(' ', '');
			if(value.length>20){
				var val = value.substring(0,20);
				alert("条件长度不能大于20！");
				$(obj).attr("value", val);
				$("#"+conditionId+"_conditionValue").attr("value",val);
			}else{
				var b;
				if((b=value.indexOf("{"))>=0 || (b=value.indexOf("}"))>=0 || (b=value.indexOf("\'"))>=0 || (b=value.indexOf("\""))>=0){
					var val = value.substring(0, b);
					alert("条件不能包含空格、单引号、双引号和大括号");
					$(obj).attr("value", val);
					$("#"+conditionId+"_conditionValue").attr("value",val);
				}else{
					$("#"+conditionId+"_conditionValue").attr("value",value);
				}
			}
		}

		function $editClickCallback(rowid,tableId){
			var fieldName=$("#"+rowid+"_fieldName").val();
			if(fieldName != ''){
				var dataType=jQuery("#"+tableId).jqGrid('getCell',rowid,"dataType");
				packagingOperatorUpdate(dataType,rowid,tableId);
			}
		}

		//在线试用删除
		function testOnlineValidateDelete(){
			var deleteFlag = true;
			var ids = jQuery("#dataRuleTable").getGridParam('selarrrow');
			for(var i=0;i<ids.length;i++){
				var code = jQuery("#dataRuleTable").jqGrid('getCell',ids[i],"code");
				if(code=='PRODUCT_RULE1' || code=='PRODUCT_RULE2'){
					deleteFlag = false; break;
				}
			}
			if(deleteFlag){
				deleteDataRule('${acsCtx}/authority/data-rule-delete.htm');
			}else{
				alert("为确保系统的正常演示,不能删除编号为“PRODUCT_RULE1”和“PRODUCT_RULE2”的数据！");
			}
		}

		function settingType(){
			var type = $("input[name='simplable']:checked");
			var settingval = $(type).val();
			if(settingval=="true"){//简易设置时
				//显示简易设置选项
				$(".listTd").show();
				//隐藏高级设置选项
				$(".conditonTd").hide();
				$("#advancedGrid").hide();
			}else{
				//显示高级设置选项
				$(".conditonTd").show();
				$("#advancedGrid").show();
				//隐藏简易设置选项
				$(".listTd").hide();
			}
		}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body >
<div class="ui-layout-center">
<div class="opt-body">
	<form id="defaultForm" name="defaultForm"action="" method="post" >
		<input type="hidden" id="menuId" name="sysMenuId" value="${sysMenuId }"/>
	</form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
			<button class="btn" onclick='createDataRule("${acsCtx}/authority/data-rule-input.htm");'><span><span >新建</span></span></button>
			<button class="btn" onclick="updateDataRule('${acsCtx}/authority/data-rule-input.htm');"><span><span >修改</span></span></button>
			<s:if test='#versionType=="online"'>
				<button class="btn" onclick="testOnlineValidateDelete();"><span><span >删除</span></span></button>
			</s:if><s:else>
				<button class="btn" onclick="deleteDataRule('${acsCtx}/authority/data-rule-delete.htm');"><span><span >删除</span></span></button>
			</s:else>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${acsCtx}/authority/data-rule.htm?sysMenuId=${sysMenuId }" code="ACS_DATA_RULE" gridId="dataRuleTable" pageName="page"></view:jqGrid>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>
