<table id="${gridId}"></table>
<div id="${gridId}_pager"></div>
<input id="_login_name" type="hidden" value="${loginName}"/>
<input id="_user_name" type="hidden" value="${userName}"/>
<input id="_exportable_sign" type="hidden" value="${export}"/>
<input id="_main_grid_id" type="hidden" value="${gridId}"/>
<#if subGrid?if_exists !="">
	<input id="_have_sub_grid" type="hidden" value="${subGrid}"/>
<#else>
	<input id="_have_sub_grid" type="hidden" value=""/>
</#if>

<script type="text/javascript">
var _gridTagParams={
	_grid_id:"${gridId}",
	_grid_url:"${url}",
	_page_name:"${pageName}",
	_sub_grid:"${subGrid}",
	_merger_cell:"${mergerCell}",
	_list_columns:"${columns}",
	_dynamic_columns:"${dynamicColumn}",
	_frozen_column:${frozenColumn},
	_row_numbers:"${rowNumbers}",
	_custom_property:"${customProperty}",
	_edit_url:"${editurl}",
	_ctx:"${ctx}",
	_row_num:"${rowNum}",
	_row_list:"${rowList}",
	_multi_select:"${multiselect}",
	_multibox_select_only:"${multiboxSelectOnly}",
	_sort_name:"${sortname}",
	_sort_order:"${sortorder}",
	_pagination:"${pagination}",
	_total:"${total}",
	_list_code:"${_list_code}",
	_dynamic_columns_postData:'${dynamicColumns}',
	_delete_url:"${deleteUrl}",
	_drag_row_url:"${dragRowUrl}",
	_group_header_sign:"${groupHeaderSign}",
	_group_header:'${groupHeader}',
	_start_query_sign:'${startQuerySign}'
};
<#if subGrid?if_exists =="">
	//以下的编码方式是为了解决IE8下，代码document.body.appendChild(gridTagJs);报脚本错误
	if(document.readyState == "complete"){ //当页面加载状态为完全结束时进入
		__loadingGridTagJs();
	}else{
		document.onreadystatechange =function(){//当页面加载状态改变的时候执行这个方法. 
			if(document.readyState == "complete"){ //当页面加载状态为完全结束时进入
				__loadingGridTagJs();
			}
		}
	}
	function __loadingGridTagJs(){ 
		var gridTagJs=document.createElement("script");
		gridTagJs.type = "text/javascript";
		gridTagJs.src = "${resourceCtx}/templateJs/gridTag.js";
		document.body.appendChild(gridTagJs);
		<#if startQuerySign?if_exists =="INSIDE_QUERY"||startQuerySign?if_exists =="CUSTOM_QUERY">
		var searchTagJs=document.createElement("script");
		searchTagJs.type = "text/javascript";
		searchTagJs.src = "${resourceCtx}/templateJs/searchTag.js";
		document.body.appendChild(searchTagJs);
		</#if>
	} 
</#if>
</script>

