<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>选择办理人</title>
	<script type="text/javascript" src="${resourceCtx}/js/jquery-all-1.0.js"></script>
	<script type="text/javascript" src="${resourceCtx}/widgets/jstree/jquery.jstree.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/css/${theme}/jquery-ui-1.8.16.custom.css" id="_style"/>
	<script type="text/javascript" src="${resourceCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourceCtx}/js/public.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourceCtx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${resourceCtx}/templateJs/jstree-pop-multiple.js"></script>
</head>
<body  onload="getContentHeight_ColorIframe();">
<div class="opt-body">
<div class="cbox-btn">
	<button class="btn" onclick="_ok_tree();" id="wf_ok"><span><span>确定</span></span></button>
	<div style="display:inline;float:right;*margin-top:-28px;" >
		<table><tr><td >
			<input id="searchInput" /></td><td ><a class="search-btn" href="#" onclick="search_fun();" ><b class="ui-icon ui-icon-search"></b></a>
		</td></tr></table>
	</div>
</div>
<div id="opt-content">
<input id="defaultId" type="hidden" name="defaultId" >
<input id="defaultTreeType" type="hidden" value="${treeType}" >
<input id="defaultTreeValue" type="hidden" value="${defaultTreeValue}" >
<input id="leafPageId" type="hidden" value="${leafPage}" >
<input id="isAppend" type="hidden" value="${isAppend}" >
<input id="loginNameId" type="hidden" value="${loginNameId}" >
<input id="hiddenInputId" type="hidden" value="${hiddenInputId}" >
<input id="showInputId" type="hidden" value="${showInputId}" >
<input id="formId" type="hidden" value="${formId}" >
<input id="mode" type="hidden" value="${mode}" >

<div id="tabs" style="height:350px;">
<#if leafPage=='true'>
    <ul>
    <#list leafPageList as page >
    <li ><a href="#tabs-1" onclick="changeSelected('${page.type}','${page.value}');">${page.name}</a></li>
    </#list>
	</ul>
</#if>	
<div id="tabs-1">
<div id="${treeId}" class="demo" type="${treeType}">
<script type="text/javascript">
//<---------------解析树节点的分隔符-------------->
var split_one = "~~";
var split_two = "==";
var split_three = "*#";
var split_four = "|#";
var split_five = "+#";
var split_six = "~#";
var split_seven = "**";
var split_eight = "=#";
var split_night = "##";
var split_nine = "~*";
var split_ten = "~+";
var isSingleCompany="${isSingleCompany}";
var treeId="${treeId}";
var treeType="${treeType}";
var actionUrl="${actionUrl}";
var searchUrl="${searchUrl}";
var resourceCtx="${resourceCtx}";
var ctx="${ctx}";
var branchIds="${branchIds}";
var defaultTreeType="${defaultTreeType}";
var defaultTreeValue="${defaultTreeValue}"
var hiddenInputId="${hiddenInputId}";
var showInputId="${showInputId}";
//<---------------多选树标签第三版-------------->
//初始化树
</script>
</div>
</div>
</div>
</div>
</div>
</body>
<script type="text/javascript" src=resourceCtx+"/widgets/colorbox/jquery.colorbox.js"></script>
</html>