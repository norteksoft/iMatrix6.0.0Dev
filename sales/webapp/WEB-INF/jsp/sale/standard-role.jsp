<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="role.roleManager"/></title>
<%@ include file="/common/meta.jsp"%>
<link   type="text/css" rel="stylesheet" href="${ctx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${ctx}/widgets/colorbox/jquery.colorbox.js"></script>
</head>
<body>
<form action="" name="exportForm" id="exportForm" method="post">
<input name="ids" type="hidden" id="ids">
<input name="businessSystemId" id="systemId" value="${businessSystemId }" type="hidden">
</form>
<div class="page_margins">
  <div class="page">
 		<%@ include file="/menus/header.jsp"%>
		<%@ include file="/menus/second_menu.jsp"%>
		<div id="main">
		<%@ include file="/menus/left_menu.jsp"%>
    	<div id="col3" style="margin: 0 0 0 150px;">
    	
    		<div class="widget-place" id="widget-place-1">
    			<div class="widget" id="identifierwidget-1">
					<div class="widget-header">
						<h5>角色列表</h5>
					</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
					         <div id="menu"></div>
					         <div id="content">
								<table class="Table changeTR">
								<thead>
									<tr>
										<th style="width: 30px;" align="center"></th>
										<th><b><s:text name="role.roleName"/></b></th>
										<th><b><s:text name="businessSystem.businessName"/></b></th>
										<th><b><s:text name="common.operate"/></b></th>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.result" id="role">
										<tr>
											<td style="width: 30px;" align="center"><input type="checkbox" name="id" value="${id }"></input></td>
											<td>${name}&nbsp;</td>
											<td>${businessSystem.name}&nbsp;</td>
											<td>&nbsp; 
													<a href="standard-role!input.action?id=${id}"><s:text name="common.alter"/></a>|
													<a href="standard-role!delete.action?id=${id}"><s:text name="common.delete"/></a>|
													<a href="${ctx}/sale/standard-role!roleToFunctionList.action?roleId=${id}">添加资源</a>|
													<a href="${ctx}/sale/standard-role!roleRomoveFunctionList.action?roleId=${id}">移除资源</a>
											</td>
										</tr>
									</s:iterator>
								</tbody>
								</table>
							</div>
					         <div id="footer" style="margin-top:10px ">
								<s:text name="common.NO."/>${page.pageNo}<s:text name="common.page"/>, <s:text name="common.total"/>${page.totalPages}<s:text name="common.page"/> 
								<s:if test="page.hasPre">
									<a href="${ctx}/sale/standard-role.action?businessSystemId=${businessSystemId}&page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.upPage"/></a>
								</s:if>
								<s:if test="page.hasNext">
									<a href="${ctx}/sale/standard-role.action?businessSystemId=${businessSystemId}&page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.downPage"/></a>
								</s:if>
								<s:if test="businessSystemId!=null">
								</s:if>
								<a href="standard-role!input.action?businessSystemId=${businessSystemId}">新建角色</a>
								<!--<a href="javascript:history.back()">返回</a>-->
								<a href="/sales/sale/business-system.action">返回</a>
							</div>
						</div>
					<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
				</div>
			</div>
    	</div>
    </div>
  </div>
</div>
</body>
</html>
