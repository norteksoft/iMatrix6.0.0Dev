<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/js/custom.tree.js"></script>
	<script type="text/javascript">
		function submitParent(){
			var id = jQuery("#dataTableParentId").getGridParam('selrow');
			if(id==null){
				alert("请选择一条数据！");
			}else{
				window.parent.$("#parentName").attr("value",jQuery("#dataTableParentId").jqGrid('getCell',id,"alias"));
				window.parent.$("#parentId").attr("value",id);
				window.parent.$.colorbox.close();
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
	<form id="defaultForm" name="defaultForm"action="" method="post" ></form>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="submitParent();"><span><span >确定</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form action="${mmsCtx}/authority/data-rule-selectDataTable.htm" name="pageForm" id="pageForm" method="post">
				<view:jqGrid url="${mmsCtx}/form/data-table-selectParent.htm?menuId=${menuId }&tableId=${tableId }" code="MMS_DATA_TABLE_PARENT" gridId="dataTableParentId" pageName="dataTables"></view:jqGrid>
			</form>
		</div>
	</aa:zone>
</div>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
</html>
