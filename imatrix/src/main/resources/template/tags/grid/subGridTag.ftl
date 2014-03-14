<table id="${gridId}"></table>
<div id="${gridId}_pager"></div>
<script type="text/javascript">
var _subGridTagParams={
	_sub_grid_id:"${gridId}",
	_sub_grid_url:"${url}",
	_sub_grid_url_Parameter:"${urlParameter}",
	_sub_grid_page_name:"${pageName}",
	_sub_grid_list_columns:"${columns}",
	_sub_grid_row_numbers:"${rowNumbers}",
	_sub_grid_edit_url:"${editurl}",
	_sub_grid_ctx:"${ctx}",
	_sub_grid_row_num:"${rowNum}",
	_sub_grid_row_list:"${rowList}",
	_sub_grid_multi_select:"${multiselect}",
	_sub_grid_multibox_select_only:"${multiboxSelectOnly}",
	_sub_grid_sort_name:"${sortname}",
	_sub_grid_sort_order:"${sortorder}",
	_sub_grid_pagination:"${pagination}",
	_sub_grid_total:"${total}",
	_sub_grid_list_code:"${_list_code}"
};
//以下的编码方式是为了解决IE8下，代码document.body.appendChild(subGridTagJs);报脚本错误
if(document.readyState == "complete"){ //当页面加载状态为完全结束时进入
	__loadingSubGridTagJs();
}else{
	document.onreadystatechange =function(){//当页面加载状态改变的时候执行这个方法. 
		if(document.readyState == "complete"){ //当页面加载状态为完全结束时进入
			__loadingSubGridTagJs();
		}
	}
}
function __loadingSubGridTagJs(){ 
	var gridTagJs=document.createElement("script");
	gridTagJs.type = "text/javascript";
	gridTagJs.src = "${resourceCtx}/templateJs/gridTag.js";
	document.body.appendChild(gridTagJs);
	
	var searchTagJs=document.createElement("script");
	searchTagJs.type = "text/javascript";
	searchTagJs.src = "${resourceCtx}/templateJs/searchTag.js";
	document.body.appendChild(searchTagJs);
}
</script>
