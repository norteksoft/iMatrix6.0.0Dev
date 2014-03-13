<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>计划管理</title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript">
		function createPlan(){
			openPlan();
		}
		function editPlan(permissionFlag){
			var ids=jQuery("#planList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要编辑的记录！");
				return;
			}else if(ids.length>1){
				alert("请不要选择多条记录！");
				return;
			}
			$.ajax({
				data:{id:ids[0],permissionFlag:permissionFlag},
				type: "POST",
				url: "${ctx}/plan/validate-permission.htm",
				success: function(data){
					var values = data.split("_");
					if(values[0]=="true"){
						openPlan(values[1]);
					}else{
						$("#message").html("<font class=\"onError\"><nobr>您没有权限修改此记录！</nobr></font>");
						showMsg("message");
					}
				}
			}); 
			
		}
		function deletePlan(){
			var ids=jQuery("#planList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要删除的记录！");
				return;
			}
			
			aa.submit('contentForm', '${ctx}/plan/delete.htm?deleteIds='+ids.join(',')+"&position=${position}", 'main'); 
		}
		function openPlan(id){
			var url='${ctx}/plan/input.htm';
			if(id!=undefined){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:800, innerHeight:500,
				overlayClose:false,
				onClosed:function(){
					jQuery("#planList").trigger("reloadGrid");
				},
				title:"计划"
				});
		}

		function viewPlan(ts1,cellval,opts){
			var v = "<a href='#' onclick='planInfo("+opts.id+");'>"+ts1+"</a>";
			return v;
		}

		//跳转到文档信息
		function planInfo(id){
			$.ajax({
				data:{id:id,permissionFlag:'view'},
				type: "POST",
				url: "${ctx}/plan/validate-permission.htm",
				success: function(data){
					var values = data.split("_");
					if(values[0]=="true"){
						openPlanView(values[1]);
					}else{
						$("#message").html("<font class=\"onError\"><nobr>您没有权限查看此记录！</nobr></font>");
						showMsg("message");
					}
				}
			}); 
		}

		function openPlanView(id){
			var url='${ctx}/plan/view.htm';
			if(id!=undefined){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:800, innerHeight:600,
				overlayClose:false,
				onClosed:function(){
					jQuery("#planList").trigger("reloadGrid");
				},
				title:"计划"
				});
		}
	</script>
</head>

<body>
	<s:if test="position=='list2'">
		<script type="text/javascript">
			var secMenu="expenseReport";
			var thirdMenu="plan-list2";
		</script>
	</s:if><s:else>
		<script type="text/javascript">
			var secMenu="expenseReport";
			var thirdMenu="plan";
		</script>
	</s:else>
	
	<%@ include file="/menus/header.jsp" %>

	 
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button  class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
						<button class='btn' onclick="createPlan();"><span><span>新建</span></span></button>
						<button class='btn' onclick="editPlan('update');"><span><span>编辑</span></span></button>
						 <button class='btn' onclick="deletePlan();"><span><span>删除</span></span></button>
					</div>
					<div id="opt-content">
					<div id="message"><s:actionmessage theme="mytheme" /></div>	
					<script type="text/javascript">setTimeout("$('#message').hide('show');",3000);</script>
						<form id="contentForm" name="contentForm" method="post"  action="">
							<s:if test="position=='list2'">
								<grid:jqGrid gridId="planList" url="${ctx}/plan/list-datas.htm" code="ES_PLAN2" subGrid="planItemList"></grid:jqGrid>
									<div style="height: 8px;"></div>
								<grid:subGrid gridId="planItemList" url="${ctx}/plan/list-plan-item.htm" code="ES_PLAN_ITEM2" pageName="pageItem"></grid:subGrid>
							</s:if><s:else>
								<grid:jqGrid gridId="planList" url="${ctx}/plan/list-datas.htm" code="ES_PLAN" subGrid="planItemList"></grid:jqGrid>
									<div style="height: 8px;"></div>
								<grid:subGrid gridId="planItemList" url="${ctx}/plan/list-plan-item.htm" code="ES_PLAN_ITEM" pageName="pageItem"></grid:subGrid>
							</s:else>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>