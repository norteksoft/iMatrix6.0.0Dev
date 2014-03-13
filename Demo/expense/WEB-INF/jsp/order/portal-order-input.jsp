<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
		<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js" ></script>
	<script type="text/javascript" src="${ctx}/js/order.js" ></script>
		<script type="text/javascript">
			function expenseReportFormValidate(){
				$("#expenseReportForm").validate({
					submitHandler: function() {
						$(".opt_btn").find("button.btn").attr("disabled","disabled");
						var result = iMatrix.getFormGridDatas("expenseReportForm","orderItemsId");
						if(result){
						  aa.submit('expenseReportForm','','main',saveBack);
						}
					},
					rules: {
						"orderNumber": {
							required:true
						},
						"customer": {
							required:true
						}
					},
					messages: {
						"orderNumber": {
							required:"必填"
						},
						"customer": {
							required:"必填"
						}
					}
				});
			}

			function saveBack(){
				showMsg();
				jQuery('#orderItemsId').jqGrid('setGridHeight',gridheight-160);
			}
			
			function submitForm(url){
				$('#expenseReportForm').attr('action',url);
				$('#expenseReportForm').submit();
			}
			
			function callback(){
				addFormValidate('${fieldPermission}','expenseReportForm');
				expenseReportFormValidate();
				showMsg();
			}
			function selectPerson(){
				var acsSystemUrl = "${ctx}";
//				popTree({ title :'选择人员',
//					innerWidth:'400',
//					treeType:'MAN_DEPARTMENT_TREE',
//					defaultTreeValue:'id',
//					leafPage:'false',
//					multiple:'false',
//					hiddenInputId:'customer1',
//					showInputId:'customer',
//					loginNameId:'',
//					acsSystemUrl:acsSystemUrl,
//					isAppend:"false",
//					callBack:function(){}});
				var zTreeSetting={
						leaf: {
							enable: false
						},
						type: {
							treeType: "MAN_DEPARTMENT_TREE",
							noDeparmentUser:true,
							onlineVisible:false
						},
						data: {

						},
						view: {
							title: "选择人员",
							width: 300,
							height:400,
							url:acsSystemUrl,
							showBranch:false
						},
						feedback:{
							enable: true,
									showInput:"customer",
					                hiddenInput:"customer1",
					                append:false
						},
						callback: {
							onClose:function(){
							}
						}			
						};
					    popZtree(zTreeSetting);
			}
			var gridheight;	
			var loadFlag = false;
			//列表加载完成后回调
			function $gridComplete(tableId){
				if(loadFlag){
					jQuery('#'+tableId).jqGrid('setGridHeight',gridheight-160);	
				}
			}

			function loadGridHeight(){
				loadFlag = true;
				gridheight = $("#opt-content").height();
				jQuery('#orderItemsId').jqGrid('setGridHeight',gridheight-160);	
			}
		</script>
	</head>
	
	<body onload="getContentHeight();loadGridHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="cbox-btn">
						<s:if test="task==null">
							<button class='btn' onclick="submitForm('${ctx}/order/save.htm')"><span><span>保存</span></span></button>
						</s:if><s:else>
							<button class='btn' onclick="retriveTask()">
								<span><span>取回</span>
								</span>
							</button>
						</s:else>
						
					</div>
					<div style="display: none;" id="message"><font class='onSuccess'><nobr>保存成功！</nobr></font></div>
					<div id="opt-content" class="form-bg">
						<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
							<input type="hidden" name="id" id="id" value="${id }"/>
							<table>
								<tr>
									<td>订单编号</td>
									<td><input id="orderNumber" name="orderNumber" value="${orderNumber }"/> </td>
									<td>用户名称</td>
									<td><input id="customer" name="customer" value="${customer }" readonly="readonly"/><input id="customer1" type="hidden"/><button onclick="selectPerson()" type="button">选择</button></td>
								</tr>
								<tr>
									<td>邮编</td>
									<td><input id="postCode" name="postCode" value="${postCode }"/> </td>
									<td>电话</td>
									<td><input id="phone" name="phone" value="${phone }" /></td>
								</tr>
								<tr>
									<td>订单值</td>
									<td><input id="weight" name="weight" value="${weight }" onkeyup="value=value.replace(/[^\d.]/g,'')" /></td>
									<td>类型</td>
									<td>
										<select id="type" name="type" style="width: 155px;">
										<s:iterator value="@com.example.expense.base.enumeration.OrderType@values()" var="bean">
											<option <s:if test="#bean==type">selected="selected"</s:if> value="${bean}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
										</select>
									</td>
								</tr>
							</table>
								<grid:formGrid gridId="orderItemsId" code="ES_ORDER_ITEM1" entity="${order}" attributeName="orderItems"></grid:formGrid>
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