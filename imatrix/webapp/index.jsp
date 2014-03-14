<%@page import="com.norteksoft.product.util.SystemUrls"%>
<% String imatrix = SystemUrls.getSystemPageUrl("imatrix");
		response.sendRedirect(imatrix+"/portal/index/redirect-into-system.htm?code=portal"); 
%>