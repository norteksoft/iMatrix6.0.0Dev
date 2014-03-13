<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

	<div id="myExpenseReport" class="west-notree"  ><a href="#" onclick="thirdMenuClick('myExpenseReport');">我的报销单</a></div>
	<div id="report" class="west-notree"  ><a href="#" onclick="thirdMenuClick('report');">报销单子流程</a></div>
	<div id="order1" class="west-notree" ><a href="#" onclick="thirdMenuClick('order1');">订单(主子表)</a></div>
	<div id="order2" class="west-notree"  ><a href="#" onclick="thirdMenuClick('order2');">订单(动态列表)</a></div>
	<div id="order3" class="west-notree" ><a href="#" onclick="thirdMenuClick('order3');">订单(列表API)</a></div>
	<div id="order4" class="west-notree" ><a href="#" onclick="thirdMenuClick('order4');">订单(控制行拖动)</a></div>
	<div id="template" class="west-notree"  ><a href="#" onclick="thirdMenuClick('template');">模板(自定义列表)</a></div>
	<div id="product" class="west-notree"  ><a href="#" onclick="thirdMenuClick('product');">产品(增删改)</a></div>
	<div id="productApi" class="west-notree" ><a href="#" onclick="thirdMenuClick('productApi');">产品(列表API)</a></div>
	<div id="productAllPageTotal" class="west-notree" ><a href="#" onclick="thirdMenuClick('productAllPageTotal');">产品(合计所有页)</a></div>
	<div id="customSearch" class="west-notree" ><a href="#" onclick="thirdMenuClick('customSearch');">产品(自定义查询)</a></div>
	<div id="cellMerge" class="west-notree" ><a href="#" onclick="thirdMenuClick('cellMerge');">产品(单元格合并)</a></div>
	<div id="groupHeader" class="west-notree" ><a href="#" onclick="thirdMenuClick('groupHeader');">产品(表头组合)</a></div>
	<div id="queryEvent" class="west-notree" ><a href="#" onclick="thirdMenuClick('queryEvent');">产品(查询事件)</a></div>
	<div id="product-event" class="west-notree"  ><a href="#" onclick="thirdMenuClick('product-event');">产品(列表事件)</a></div>
	<div id="plan" class="west-notree" ><a href="#" onclick="thirdMenuClick('plan');">计划管理(权限1)</a></div>
	<div id="plan-list2" class="west-notree" ><a href="#" onclick="thirdMenuClick('plan-list2');">计划管理(权限2)</a></div>
	<div id="product-dataRule" class="west-notree" ><a href="#" onclick="thirdMenuClick('product-dataRule');">产品(数据类别api)</a></div>
	<div id="orderCombineList" class="west-notree" ><a href="#" onclick="thirdMenuClick('orderCombineList');">订单组合列表</a></div>
	<div id="messageNotice" class="west-notree" ><a href="#" onclick="thirdMenuClick('messageNotice');">系统消息提示</a></div>
	<div class="linee"></div>
<script type="text/javascript">
$().ready(function(){
	if(thirdMenu!=""){
		$('#'+thirdMenu).addClass('west-notree-selected');
	}
});

function thirdMenuClick(menuDivId){
	var url = window.location.href;
	var menuId = "";
	if(url.indexOf("menuId=")>=0){
		if(url.indexOf("&")>=0){
			var params = url.substring(url.indexOf("menuId=")+7);
			if(params.indexOf("&")>=0){//地址如http://....htm?menuId=123&position=list1
				menuId = params.substring(0,params.indexOf("&"));
			}else{//地址如http://....htm?menuId=123
				menuId = params;
			}
		}else{
			menuId = url.substring(url.indexOf("menuId=")+7);
		}
	}
	var menuIdParam = "";
	if(menuId!=""){
		menuIdParam = "menuId="+menuId;
	}
	if(menuDivId=="myExpenseReport"){
		window.location.href = "${ctx }/expense-report/list.htm?"+menuIdParam;
	}else if(menuDivId=="report"){
		window.location.href = "${ctx }/report/list.htm?"+menuIdParam;
	}else if(menuDivId=="order1"){
		window.location.href = "${ctx }/order/list.htm?"+menuIdParam;
	}else if(menuDivId=="order2"){
		window.location.href = "${ctx }/order/dynamic-list.htm?"+menuIdParam;
	}else if(menuDivId=="order3"){
		window.location.href = "${ctx }/order/api-list.htm?"+menuIdParam;
	}else if(menuDivId=="order4"){
		window.location.href = "${ctx }/order/control-list.htm?"+menuIdParam;
	}else if(menuDivId=="template"){
		window.location.href = "${ctx }/template/list.htm?"+menuIdParam;
	}else if(menuDivId=="product"){
		window.location.href = "${ctx }/emsproduct/list.htm?"+menuIdParam;
	}else if(menuDivId=="productApi"){
		window.location.href = "${ctx }/emsproduct/api-list.htm?"+menuIdParam;
	}else if(menuDivId=="productAllPageTotal"){
		window.location.href = "${ctx }/emsproduct/total-list.htm?"+menuIdParam;
	}else if(menuDivId=="customSearch"){
		window.location.href = "${ctx }/emsproduct/custom-search-list.htm?"+menuIdParam;
	}else if(menuDivId=="cellMerge"){
		window.location.href = "${ctx }/emsproduct/cell-merge-list.htm?"+menuIdParam;
	}else if(menuDivId=="groupHeader"){
		window.location.href = "${ctx }/emsproduct/group-header-list.htm?"+menuIdParam;
	}else if(menuDivId=="queryEvent"){
		window.location.href = "${ctx }/emsproduct/query-event-list.htm?"+menuIdParam;
	}else if(menuDivId=="product-event"){
		window.location.href = "${ctx }/emsproduct/list-event.htm?"+menuIdParam;
	}else if(menuDivId=="plan"){
		window.location.href = "${ctx }/plan/list.htm?position=list1&"+menuIdParam;
	}else if(menuDivId=="plan-list2"){
		window.location.href = "${ctx }/plan/list.htm?position=list2&"+menuIdParam;
	}else if(menuDivId=="product-dataRule"){
		window.location.href = "${ctx }/emsproduct/dataRule-list.htm?"+menuIdParam;
	}else if(menuDivId=="orderCombineList"){
		window.location.href = "${ctx }/order/order-combine-list.htm?"+menuIdParam;
	}else if(menuDivId=="messageNotice"){
		window.location.href = "${ctx }/expense-report/expense-message.htm?"+menuIdParam;
	}
}
</script>