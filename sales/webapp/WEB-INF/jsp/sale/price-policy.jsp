<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 产品管理</title>
<%@ include file="/common/meta.jsp"%>
</head>

<body>
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
						<h5>价格策略</h5>
					</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
				             <table class="Table changeTR">
								<thead>
									<tr>
										<th><b>策略名称</b></th>
										<th><b>策略价格</b></th>
										<th><b>备注</b></th>
										<th><b>操作</b></th>
									</tr>
								</thead>
								  <tbody>
									<s:iterator value="page.result">
										<tr>
											<td>${priceName}&nbsp;</td>
											<td>${amount}&nbsp;</td>
											<td>${remark}&nbsp;</td>
											<td>&nbsp; 
												<a href="price-policy!input.action?id=${id}">修改</a>、
												<a href="price-policy!delete.action?id=${id}&productId=${productId}">删除</a>
											</td>
										</tr>
									</s:iterator>
								</tbody>
							</table>
							<div id="footer" style="margin-top:10px">
								第${page.pageNo}页, 共${page.totalPages}页 
								<s:if test="page.hasPre">
									<a href="price-policy.action?page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}">上一页</a>
								</s:if>
								<s:if test="page.hasNext">
									<a href="price-policy.action?page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}">下一页</a>
								</s:if>
								<a href="price-policy!input.action?productId=${productId}">增加价格策略</a>
								<a href="product.action">返回</a>
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
