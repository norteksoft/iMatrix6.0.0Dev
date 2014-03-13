<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript">
		function createExpenseReport(){
				$.ajax({
					type: "POST",
					url: "${ctx}/expense-report/validate-process.htm",
					success: function(data){
						if(data=="zero"){
							$("#message").html("<font class=\"onError\"><nobr>没有启用的报销流程</nobr></font>");
							showMsg("message");
						}else{
							openExpenseReport();
						}
					}
				}); 
		}

		//删除
		function deleteExpenseReport(){
			var ids=jQuery("#expenseReportList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要删除的记录！");
				return;
			}
			
			aa.submit('contentForm', '${ctx}/expense-report/delete.htm?deleteIds='+ids.join(','), 'main'); 
		}

		function openExpenseReport(id){
			var url='${ctx}/expense-report/input.htm';
			if(id!=undefined){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:900, innerHeight:500,
				overlayClose:false,
				onClosed:function(){
					jQuery("#expenseReportList").trigger("reloadGrid");
				},
				title:"报销单"
				});
		}


		//点报销人事件，绑定在列表里
		function expenseView(ts1,cellval,opts){
			//opts['workflowInfo.currentActivityName']  引用对象的某属性，该属性必须在数据表对应的字段中设置了
			var v = "<a href='#' onclick='expenseInfo("+opts.id+");'>"+ts1+"</a>";
			return v;
		}

		//跳转到文档信息
		function expenseInfo(a){
			openExpenseReport(a);
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var thirdMenu="myExpenseReport";
	</script>
	<%@ include file="/menus/header.jsp"%>
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp"%>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button class='btn' onclick="createExpenseReport();"><span><span>新建</span></span></button>
						<button class='btn' onclick="deleteExpenseReport();"><span><span>删除</span></span></button>
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
					</div>
					
					
					<div id="opt-content">
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<script type="text/javascript">setTimeout("$('#message').hide('show');",3000);</script>
						<form id="contentForm"  method="post"  action="">
							<grid:jqGrid gridId="expenseReportList" url="${ctx}/expense-report/list-datas.htm" code="ES_EXPENSE_REPORT"></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
</html>