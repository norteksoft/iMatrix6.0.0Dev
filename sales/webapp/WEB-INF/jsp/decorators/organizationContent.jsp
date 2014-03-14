<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
	<link href="${ctx}/css/common.css" type="text/css" rel="stylesheet"/>
	<title>北科汇智 - <decorator:title>Welcome to NortekSoft!</decorator:title> </title>
	
	<decorator:head/>
</head>
<body>
	<!-- header -->
	<page:applyDecorator name="header"/>
	<div id="acs_empty"></div>

	<!-- sidebar -->
	<page:applyDecorator name="organizationSidebar"/>
	<div id="acs_empty"></div>
	
	
	
	<!-- center content start -->
	<div id="acs_content">
		<table width="100%" height="59" border="0" cellpadding="0" cellspacing="0">
		  <tr>
			<td bgcolor="#638658">&nbsp;&nbsp;<span class="STYLE1"></span></td>
		  </tr>
		</table>
		<table width="100%" height="8" border="0" cellpadding="0" cellspacing="0">
		  <tr>
			<td bgcolor="#ffffff"></td>
		  </tr>
		</table>
		<decorator:body></decorator:body>
	</div>
	
	
	
	<!-- footer -->
	<br/><br/>
	<page:applyDecorator name="footer"/>
	<br/><br/>
</body>
</html>