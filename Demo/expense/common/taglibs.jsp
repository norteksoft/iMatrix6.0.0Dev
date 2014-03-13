<%@ taglib prefix="s" uri="/struts-tags"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net" prefix="aa"%> 
<%@ taglib  prefix="wf" uri="http://www.norteksoft.com/workflow/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.norteksoft.com/search/tags" prefix="ds" %> 
<%@ taglib  prefix="menu" uri="http://www.norteksoft.com/menu/tags"%>
<%@ taglib prefix="grid" uri="http://www.norteksoft.com/view/tags" %>
<%@ taglib prefix="acsTags" uri="http://www.norteksoft.com/acs/tags"%>
<%@ taglib prefix="ztree" uri="http://www.norteksoft.com/ztree/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:url var="resourcesCtx" value='<%=com.norteksoft.product.util.PropUtils.getProp("host.resources")%>'></c:url>
<c:url var="imatrixCtx" value='<%=com.norteksoft.product.util.SystemUrls.getSystemPageUrl("imatrix")%>'></c:url>
<c:url var="tempVersionType" value='<%=com.norteksoft.product.util.PropUtils.getOnlineProp("product.version.type")%>'></c:url>
<s:set id="versionType">${tempVersionType}</s:set>