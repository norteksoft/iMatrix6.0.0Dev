<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表字段</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script src="${acsCtx}/js/authority-data-rule.js" type="text/javascript"></script>
	<script type="text/javascript">
		function submitDataRule(){
			var id = jQuery("#tableColumnGrid").getGridParam('selrow');
			if(id==null){
				alert("请选择一条数据！");
			}else{
				var currentInputId="${currentInputId }";
				var conditionId=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
				var dataType=jQuery("#tableColumnGrid").jqGrid('getCell',id,"dataType");
				window.parent.$("#"+currentInputId).attr("value",jQuery("#tableColumnGrid").jqGrid('getCell',id,"alias"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"field",jQuery("#tableColumnGrid").jqGrid('getCell',id,"name"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"dataType",jQuery("#tableColumnGrid").jqGrid('getCell',id,"dataType"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"relativeType",null);
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",null);
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"keyValue",null);
				var objectPath="";
				if(dataType=="ENUM"){
					var valueUrl = jQuery("#tableColumnGrid").jqGrid('getCell',id,"valueUrl");
					var dataTableId = jQuery("#tableColumnGrid").jqGrid('getCell',id,"dataTableId");
					objectPath = jQuery("#tableColumnGrid").jqGrid('getCell',id,"objectPath");
					if(objectPath==""||typeof(objectPath)=='undefined'){
						alert("请在数据表管理中配置枚举路径！"); return;
					}else{
						if(valueUrl==""||typeof(valueUrl)=='undefined'){
							alert("请在数据表管理中配置取枚举信息的访问地址！"); return;
						}
						window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",objectPath);
						valueUrl = valueUrl+"?callback=?&enumPath="+objectPath;
						//根据dataTableId将对应系统的地址取出
						$.ajax({
							data:{dataTableId:dataTableId,valueUrl:valueUrl},
							type:"post",
							url:webRoot+"/authority/data-rule-getSystemUrlByTalbeId.htm",
							beforeSend:function(XMLHttpRequest){},
							success:function(data, textStatus){
								getEnumData(data);
							},
							complete:function(XMLHttpRequest, textStatus){},
					        error:function(){}
						});
					}
				}else{
					var tableColumnId = jQuery("#tableColumnGrid").jqGrid('getCell',id,"id");
					$.ajax({
						data:{tableColumnId:tableColumnId},
						type:"post",
						url:webRoot+"/authority/data-rule-getValuesetByTableColumn.htm",
						beforeSend:function(XMLHttpRequest){},
						success:function(data, textStatus){
							//当列表字段信息中值设置不是这几种情况,即是key:'value',...，或是选项组
							if(data.indexOf("enumname:")<0&&data.indexOf("classname:")<0&&data.indexOf("beanname:")<0){
								window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"keyValue",data);
								packagingOperator(dataType,conditionId,data);
								window.parent.$.colorbox.close();
							}else{
								packagingOperator(dataType,conditionId);
								window.parent.$.colorbox.close();
							}
						},
						complete:function(XMLHttpRequest, textStatus){},
				        error:function(){}
					});
				}
				
			}
		}

		function $getExtraParams(){
			return {tableId:"${tableId}",currentInputId:"${currentInputId }"};
		}

		function getEnumData(systemUrl){
			var url=systemUrl;
			$.getJSON(
				url,
				function(data){
					var id = jQuery("#tableColumnGrid").getGridParam('selrow');
					var currentInputId="${currentInputId }";
					var conditionId=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
					window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"keyValue",data.msg);
					packagingOperator("ENUM",conditionId,data);
					window.parent.$.colorbox.close();
				}
			);
		}

		//换页签
		function changeViewSet(opt,obj){
			if(opt=="column"){
				$("#tableId").attr("value","${tableId}");
				$("#currentInputId").attr("value","${currentInputId}");
				ajaxSubmit("defaultForm",webRoot+"/authority/data-rule-selectColumn.htm", "btnZone,viewZone",listColumBack);
			}else if(opt=="standard"){
				$("#currentInputId").attr("value","${currentInputId}");
				ajaxSubmit("defaultForm",webRoot+"/authority/data-rule-selectStandardColumn.htm", "btnZone,viewZone");
			}
		}
		function listColumBack(){
			setFormgridHeight('tableColumnGrid',$(window).height()-160);
		}
		//标准字段/确定
		function submitStandardColumn(){
			var fieldNames = $("input[name='fieldName']:checked");
			if(fieldNames.length<=0){
				alert("请选择一个字段！");
			}else{
				var currentInputId="${currentInputId }";
				var conditionId=currentInputId.substring(0,currentInputId.lastIndexOf("_"));
				window.parent.$("#"+currentInputId).attr("value",$(fieldNames).attr("title"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"field",$(fieldNames).attr("value"));
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"dataType","LONG");
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"relativeType",null);
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"enumPath",null);
				window.parent.jQuery("#conditionGrid").jqGrid('setCell',conditionId,"keyValue",null);
				packagingOperator('LONG',conditionId);
				window.parent.$.colorbox.close();
			}
		}

		function contentResizeCallback(){
			var w=$('#tabs-1').width();
			jQuery("#tableColumnGrid").jqGrid('setGridWidth',w-3);
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
	<input id="tableId" type="hidden" name="tableId"></input>
	<input id="currentInputId" type="hidden" name="currentInputId"></input>
	</form>
	<aa:zone name="main_zone">
		<script type="text/javascript">
			$(document).ready(function() {
				$( "#tabs" ).tabs();
			});
		</script>
		<aa:zone name="btnZone">
			<div class="opt-btn">
				<button class="btn" onclick="submitDataRule();"><span><span >确定</span></span></button>
			</div>
		</aa:zone>
		<div id="opt-content">
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1" onclick="changeViewSet('column',this);">表单字段</a></li>
					<li><a href="#tabs-1" onclick="changeViewSet('standard',this);">标准字段</a></li>
				</ul>
				<div id="tabs-1">
					<aa:zone name="viewZone">
						<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
						<form action="" name="pageForm" id="pageForm" method="post">
							<view:jqGrid url="${acsCtx}/authority/data-rule-selectColumn.htm" code="ACS_DATA_RULE_TABLE_COLUMN" gridId="tableColumnGrid" pageName="tableColumnPage"></view:jqGrid>
						</form>
					</aa:zone>
				</div>
			</div>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
