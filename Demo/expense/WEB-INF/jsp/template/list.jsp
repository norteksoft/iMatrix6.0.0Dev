<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
	<%@include file="/common/meta.jsp" %>
	<script src="${resourcesCtx}/widgets/validation/validate-all-1.0.js" type="text/javascript"></script>
<script src="${resourcesCtx}/widgets/validation/dynamic.validate.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/myMessage.js"></script>
	<script type="text/javascript">
		function createTemplate(){
			openTemplate();
		}
		function editTemplate(){
			var ids=jQuery("#templateList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要编辑的记录！");
				return;
			}else if(ids.length>1){
				alert("请不要选择多条记录！");
				return;
			}
			openTemplate(ids[0]);
		}
		function deleteTemplate(){
			var ids=jQuery("#templateList").getGridParam('selarrrow');
			if(ids.length<=0){
				alert("请选择需要删除的记录！");
				return;
			}
			
			aa.submit('contentForm', '${ctx}/template/delete.htm?deleteIds='+ids.join(','), 'main'); 
		}
		function openTemplate(id){
			var url='${ctx}/template/input.htm';
			if(id!=undefined){
				url=url+'?id='+id;
			}
			$.colorbox({href:url,iframe:true, innerWidth:600, innerHeight:480,
				overlayClose:false,
				onClosed:function(){
					jQuery("#templateList").trigger("reloadGrid");
				},
				title:"模板"
				});
		}
	</script>
</head>

<body onclick="$('#sysTableDiv').hide(); $('#styleList').hide();" >
	<script type="text/javascript">
		var secMenu="expenseReport";
		var thirdMenu="template";
	</script>
	
	<%@ include file="/menus/header.jsp" %>

	 
	
	<div class="ui-layout-west">
		<%@ include file="/menus/expense-report-menu.jsp" %>
	</div>
	
	<div class="ui-layout-center">
			<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button class='btn' onclick="createTemplate();"><span><span>新建</span></span></button>
						<button class='btn' onclick="editTemplate();"><span><span>编辑</span></span></button>
						 <button class='btn' onclick="deleteTemplate();"><span><span>删除</span></span></button>
					</div>
					<div style="display: none;" id="message"><font class=onSuccess><nobr>删除成功</nobr></font></div>
					
					<div id="opt-content">
						<form id="contentForm" name="contentForm" method="post"  action="">
							<grid:jqGrid gridId="templateList" url="${ctx}/template/list-datas.htm" code="ES_PLAN_TEMPLATE"></grid:jqGrid>
						</form>
					</div>
				</aa:zone>
			</div>
	</div>
	
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>