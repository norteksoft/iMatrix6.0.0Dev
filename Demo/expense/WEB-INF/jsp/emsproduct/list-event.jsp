<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>产品管理</title>
	<%@include file="/common/meta.jsp" %>
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<script type="text/javascript">
		//单击事件
		//obj:{rowid:id,currentInputId:id_buyer}
		function buyerClick(obj){
			var acsSystemUrl = "${ctx}";
//			popTree({ title :'选择人员',
//				innerWidth:'400',
//				treeType:'MAN_DEPARTMENT_TREE',
//				defaultTreeValue:'id',
//				leafPage:'false',
//				multiple:'false',
//				hiddenInputId:obj.currentInputId,
//				showInputId:obj.currentInputId,
//				acsSystemUrl:acsSystemUrl,
//				callBack:function(){}});
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
				                showInput:obj.currentInputId,
				                hiddenInput:obj.currentInputId,
				                append:false
					},
					callback: {
						onClose:function(){
						}
					}			
					};
				    popZtree(zTreeSetting);
		}

		//双击事件
		function productNumberDblclick(obj){
			alert("productNumberDblclick:双击了产品编号！");
		}

		//下拉框切换事件
		function colorChange(obj){
			alert("colorChange:下拉框值变化时切换事件！");
		}

		//失去焦点事件
		function sumBlur(obj){
			var amount=$("#"+obj.rowid+"_amount").attr("value");
			var price=$("#"+obj.rowid+"_price").attr("value");
			$("#"+obj.currentInputId).attr("value",price*amount);
		}

		//重写(点击单元格触发的事件)
		function $onCellClick(rowid,iCol,cellcontent,e){
			//alert("单击行回调事件!rowid:"+rowid+"单元格content:"+cellcontent);
		}

		//日期选中回调事件
		function $dateOnSelect(obj){
			alert("$dateOnSelect:日期选中回调事件");
		}

		//日期改变年月回调事件
		function $dateOnChangeMonthYear(obj){
			alert("$dateOnChangeMonthYear:日期改变年月回调事件");
		}
		//日期控件关闭回调事件
		function $dateOnClose(obj){
			alert("$dateOnClose:日期控件关闭回调事件");
		}
		//列表加载完成后回调
		function $gridComplete(){
			//alert("$gridComplete:列表加载完成后回调");
		}
		//双击行事件
		function $ondblClickRow(rowId,iRow,iCol,e){
			alert("$ondblClickRow:双击行事件");
		}
		
		function $successfunc(response){
			alert("$successfunc:单行保存时请求成功之后回调方法");
			return true;
		}
		function $oneditfunc(rowId){
			alert("$oneditfunc:启动行编辑成功之后回调方法");
		}

		function $beforeEditRow(rowId,iRow,iCol,e){
			alert("$beforeEditRow:编辑行前回调事件");
			return true;
		}
		//增加grid选项回调方法
		function $addGridOption(jqGridOption){
			alert("$addGridOption:增加grid选项回调方法");
		}
		//在XMLHttpRequest被发送前，用于修改对象属性回调方法
		function $loadBeforeSend(xhr, settings){
			alert("$loadBeforeSend:在XMLHttpRequest被发送前，用于修改对象属性回调方法");
		}

		//重写(单行保存前处理行数据)
		function $processRowData(data){
			alert("$processRowData:单行保存前处理行数据");
			return data;
		}
		
		//重写(在请求中附加额外的参数)
		function $getExtraParams(){
			alert("$getExtraParams:在请求中附加额外的参数");
			return {};
		}


		
		function $aftersavefunc(rowId,data){
			alert("$aftersavefunc:保存数据成功之后回调方法");
		}
		function $afterrestorefunc(rowId){
			alert("$afterrestorefunc:在回滚当前editRow后回调方法");
		}

	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="product-event";
	</script>
	
	<%@ include file="/menus/header.jsp" %>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
			<form id="sForm" name="sForm" method="post"  action=""></form>
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					</div>
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
					<div id="opt-content" >
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="productEventList" url="${ctx}/emsproduct/list-datas.htm" submitForm="sForm" code="ES_PRODUCT_EVENT" ></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
				
			</div>
			
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>