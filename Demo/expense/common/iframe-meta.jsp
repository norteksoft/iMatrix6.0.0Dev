<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>

    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
       
	<script language="javascript" type="text/javascript">
		var webRoot="${ctx}";
		var resourceRoot="${resourcesCtx}";
		var imatrixRoot="${imatrixCtx}";
		var topMenu="";
		var versionType="${versionType}";
	</script>
	<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/form-layout.js"></script>	
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/css/<%=com.norteksoft.product.util.WebContextUtils.getTheme()%>/jquery-ui-1.8.16.custom.css" id="_style"/>
	
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqgrid-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/jqgrid/jqGrid.custom.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/jqgrid/ui.jqgrid.css" />
	
	<script type="text/javascript" src="${resourcesCtx}/widgets/jstree/jquery.jstree.js"></script>

	<script type="text/javascript" src="${resourcesCtx}/js/aa.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/form.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/search.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/js/public.js" ></script>
	<script type="text/javascript" src="${ctx}/js/demo.js"></script>
	
	<link rel="stylesheet" href="${resourcesCtx}/widgets/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css"></link>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.core-3.5.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/ztree/js/jquery.ztree.excheck-3.5.js"></script>
	<script src="${resourcesCtx}/js/z-tree.js" type="text/javascript"></script>
