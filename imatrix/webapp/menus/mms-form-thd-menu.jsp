<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>
<div id="accordion" >
	<h3><a href="list-data.htm" id="form-manager">表单管理</a></h3>
	<div>
		<ul class="ztree" id="form_manage_content"></ul>
	</div>
	<h3><a href="list-view.htm" id="list-manager">列表管理</a></h3>
	<div>
		<ul class="ztree" id="list_manage_content"></ul>
	</div>
	<h3><a href="data-table.htm" id="datatable-manager">数据表管理</a></h3>
	<div>
		<ul class="ztree" id="data_table_manage_content"></ul>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		$("#accordion").accordion({fillSpace:true, change:accordionChange});
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		if(url=="list-data.htm"){
				createViewTree("form_manage_content");
		}else if(url=="list-view.htm"){
				createViewTree("list_manage_content");
		}else if(url=="data-table.htm"){
				createViewTree("data_table_manage_content");
		}
		$("#myIFrame").attr("src",url);
	}
</script>
