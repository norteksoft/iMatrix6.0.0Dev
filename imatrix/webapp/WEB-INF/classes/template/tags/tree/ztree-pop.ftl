<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js"></script>
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.excheck-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/templateJs/ztree-pop.js"></script>
</head>
<script type="text/javascript">
var ztreeId="zTreeId",nodeList = [], parentNodeList = [],checkNodeList = [] ;
var checkUserNodeList = [] , checkDeparmentNodeList = [], checkWorkgroupNodeList = [];
var currentCheckedNode, currentClickNode ,currentClickParentNode;
var isCheckAll=false;
var companyName ;
var openNode = {},closeNode = {};
var expandNodes;
var searchMsgs;
var showCachContent="";
var hiddenCachContent="";
var leafType ;
var chkboxType="${chkboxType}"
var chkStyle="${chkStyle}";
var actionUrl="${actionUrl}";
var searchUrl="${searchUrl}";
var treeType="${treeType}";
var userWithoutDeptVisible="${userWithoutDeptVisible}";
var showBranch="${showBranch}";
var webapp="${webapp}";
var showThing="${showThing}";
var hiddenThing="${hiddenThing}";
var showInput="${showInput}";
var hiddenInput="${hiddenInput}";
var staticVar=[];
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
});
</script>
<body onload="getContentHeight_ColorIframe();">
<div class="opt-body">
	<div class="cbox-btn">
		<table>
		<tr>
		<td style="width:80px;">
			<button type='button' class="btn" onclick="_ok_ztree();" id="ok"><span><span>确定</span></span></button>
		</td>
		<td >
			<input id="searchInput" /></td><td ><a class="search-btn" href="#" onclick="ajaxSearch()" ><b class="ui-icon ui-icon-search"></b></a>
		</td>
		</tr>
		</table>
    </div>
    <div id="message"></div>
    <div id="opt-content">
	<div id="tabs" style="height:350px;">
	<div id="tabs-1">
	<ul id="zTreeId" class="ztree"></ul>
	</div>
	</div>
	</div>
</div>
</body>
</html>