<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>产品管理</title>
	<%@include file="/common/iframe-meta.jsp" %>
</head>

<body onload="getContentHeight();">
	<div class="ui-layout-center">
	<aa:zone name="main">
		<div class="opt-btn">
			<button class='btn' onclick="setPageState();back();"><span><span>返回</span></span></button>
		</div>
		<div id="opt-content">
			<form id="contentForm" name="contentForm" method="post"  action="">
				<table>
					<tr>
						<td>产品编号:</td>
						<td>${productNumber }</td>
					</tr>
					<tr>
						<td>产品名称:</td>
						<td>${productName }</td>
					</tr>
					<tr>
						<td>产品数量:</td>
						<td>${amount }</td>
					</tr>
					<tr>
						<td>采购日期:</td>
						<td><s:date name="buyDate" format="yyyy-MM-dd"/></td>
					</tr>
					<tr>
						<td>颜色:</td>
						<td><s:text name="%{color.code}"></s:text></td>
					</tr>
				</table>
			</form>
		</div>
	</aa:zone>
	</div>
</body>
</html>