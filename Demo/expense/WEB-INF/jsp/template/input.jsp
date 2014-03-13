<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/format.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"> </script>
		<script type="text/javascript">
			$(document).ready(function(){
				//jQuery("#planTaskId").jqGrid("setGridHeight",20);
			});
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						$(".opt_btn").find("button.btn").attr("disabled","disabled");
						var cansave=iMatrix.getFormGridDatas("expenseReportForm","planTaskId");
						if(cansave){
							var name = $('#name').attr("value");
							var id = $('#id').attr("value");
					    	$.ajax({
								data:{tempName:name,id:id},
								type:"post",
								url:webRoot+"/template/validate-name.htm",
								beforeSend:function(XMLHttpRequest){},
								success:function(data, textStatus){
								var values = data.split("_");
								if(values[0]=='false' ){
									alert("模版名称已存在");
							    }else{
							    	aa.submit('expenseReportForm','','main',showMsg);
								}
								},
								complete:function(XMLHttpRequest, textStatus){},
						        error:function(){

								}
							});
						}
					},
					rules: {
						"name": {
							required:true
						}
					},
					messages: {
						"name": {
							required:"必填"
						}
					}
				});
			}
			function submitForm(url){
				$('#expenseReportForm').attr('action',url);
				$('#expenseReportForm').submit();
			}

			//自定义树选择部门
			function selectDept(){
				custom_ztree({url:webRoot+'/template/department-tree.htm',
					inputObj:'department',
					width:500,
					height:400,
					title:'选择部门',
					postData:{},
					nodeInfo:['type','id','name'],
					multiple:true
				});
			}
		</script>
		<style type="text/css">
		#gbox_planTaskId{width: 578px;}
		</style>
	</head>
	<body onload="getContentHeight_ColorIframe();getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
				<form id="expenseReportForm1" name="expenseReportForm1" method="post" action=""></form>
					<div class="cbox-btn">
						<button class='btn' onclick="submitForm('${ctx}/template/save.htm')"><span><span>保存</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<input  name="id" id="id" value="${id }" type="hidden"/>
							<table>
								<tr>
									<td>模板名称</td>
									<td><input id="name" name="name" value="${name }" /> </td>
									<td>&nbsp;&nbsp;&nbsp;部门名称</td>
									<td><input id="department" name="department" value="${department }" readonly="readonly"/><button onclick="selectDept()" type="button">选择</button> </td>
								</tr>
								<tr>
									<td>分数</td>
									<td><input id="layer" name="layer" value="${layer }" /></td>
									<td>&nbsp;&nbsp;&nbsp;类型</td>
									<td>
									<select name="type">
									<s:iterator value="types">
										<s:if test="type==value">
											<option value="${value}" selected="selected">${name }</option>
										</s:if><s:else>
											<option value="${value}">${name }</option>
										</s:else>
										
									</s:iterator>
									</select></td>
								</tr>
							</table>
							<grid:customGrid gridId="planTaskId" tableName="ES_PLAN_TASK" listCode="${listCode}"></grid:customGrid>
						</form>
					</div>
					<script type="text/javascript">
						//流程环节设置的必填字段
						addFormValidate('${fieldPermission}','expenseReportForm');
						expenseReportFormValidate();
					</script>
				</aa:zone>
			</div>
			</div>
	</body>
	<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>