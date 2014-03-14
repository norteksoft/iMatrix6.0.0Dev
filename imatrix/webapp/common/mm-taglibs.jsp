<%@ taglib prefix="s" uri="/struts-tags"  %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://ajaxanywhere.sourceforge.net" prefix="aa"%> 
<%@ taglib  prefix="acsTags" uri="http://www.norteksoft.com/acs/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib  prefix="button" uri="http://www.norteksoft.com/button/tags"%>
<%@ taglib prefix="view" uri="http://www.norteksoft.com/view/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.norteksoft.com/search/tags" prefix="ds" %>
<c:set var="mmCtx" value="${pageContext.request.contextPath}/mm"/>
<c:url var="resourcesCtx" value='<%=com.norteksoft.product.util.PropUtils.getProp("host.resources")%>'></c:url>
<c:url var="imatrixCtx" value='<%=com.norteksoft.product.util.SystemUrls.getSystemPageUrl("imatrix")%>'></c:url>
<c:url var="appCtx" value='<%=com.norteksoft.product.util.PropUtils.getProp("host.app")%>'></c:url>
<c:url var="tempVersionType" value='<%=com.norteksoft.product.util.PropUtils.getOnlineProp("product.version.type")%>'></c:url>
<s:set id="versionType">${tempVersionType}</s:set>