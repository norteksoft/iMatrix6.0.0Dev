<%@ taglib prefix="s" uri="/struts-tags"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net" prefix="aa"%> 
<%@ taglib  prefix="menu" uri="http://www.norteksoft.com/menu/tags"%>
<%@ taglib prefix="grid" uri="http://www.norteksoft.com/view/tags"%>
<%@ taglib prefix="ztree" uri="http://www.norteksoft.com/ztree/tags"%>

<c:set var="portalCtx" value="${pageContext.request.contextPath}/portal"/>
<c:url var="resourcesCtx" value='<%=com.norteksoft.product.util.PropUtils.getProp("host.resources")%>'></c:url>
<c:url var="imatrixCtx" value='<%=com.norteksoft.product.util.SystemUrls.getSystemPageUrl("imatrix")%>'></c:url>
<c:url var="appCtx" value='<%=com.norteksoft.product.util.PropUtils.getProp("host.app")%>'></c:url>
<c:url var="tempVersionType" value='<%=com.norteksoft.product.util.PropUtils.getOnlineProp("product.version.type")%>'></c:url>
<s:set id="versionType">${tempVersionType}</s:set>