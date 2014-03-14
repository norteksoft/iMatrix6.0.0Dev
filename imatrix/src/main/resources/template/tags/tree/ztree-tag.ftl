<table>
<tr>
<td >
	<input id="searchInput" /></td><td ><a class="search-btn" href="#" onclick="ajaxSearch()" ><b class="ui-icon ui-icon-search"></b></a>
</td>
</tr>
</table>
<div id="message"></div>
<script type="text/javascript" src="${resourcesCtx}/templateJs/ztree-tag.js"></script>
<div id="${treeId}" class="ztree">
<script type="text/javascript">
var ztreeId="${treeId}";
var nodeList = [], parentNodeList = [],checkNodeList = [] ;
var checkUserNodeList = [] , checkDeparmentNodeList = [], checkWorkgroupNodeList = [];
var currentCheckedNode, currentClickNode ,currentClickParentNode;
var isCheckAll=false;
var companyName ;
var openNode = {},closeNode = {};
var expandNodes;
var searchMsgs;
var showCachContent="";
var hiddenCachContent="";
var objs={};
var actionUrl="${actionUrl}";
var searchUrl="${searchUrl}";
var treeType="${treeType}";
var chkboxType="${chkboxType}";
var chkStyle="${chkStyle}";
var treeType="${treeType}";
var userWithoutDeptVisible="${userWithoutDeptVisible}";
var showBranch="${showBranch}";
var webapp="${webapp}";
var setting = {
	async: {
		enable: true,
		url: getUrl
	},
	check: check()
	,
	data: {
		simpleData: {
			enable: true
		},
		treeType:getTreeType()
	},
	view: {
		expandSpeed: "",
		fontCss: getFontCss
	},
	callback: {
		beforeExpand: beforeExpand,
		beforeAsync: beforeAsync,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError,
		onExpand: onExpand,
		onClick: onClick,
		onCheck: onCheck
	}
};
$(function(){
	$.fn.zTree.init($("#"+ztreeId), setting);
})
</script>
</div>

