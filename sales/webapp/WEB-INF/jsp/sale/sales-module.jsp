<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 产品管理</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
function deleteSalesModule(url){
	alert("是否正的要删除？");
	$("#deleteSalesModule_form").attr("action",url);
	$("#deleteSalesModule_form").submit();
}
</script>
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
								<h5>销售包管理</h5>
							</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
								<table class="Table changeTR">
								    <thead>
									<tr>
										<th><b>销售包名称</b></th>
										<th><b>操作</b></th>
									</tr>
								    </thead>
								    <tbody>
									<s:iterator value="page.result">
										<tr>
											<td>${moduleName}&nbsp;</td>
											<td>&nbsp; 
													<a href="sales-module!input.action?id=${id}">修改</a>、
													<a href='javaScript:deleteSalesModule("sales-module!delete.action?id=${id}")'>删除</a>、
											</td>
										</tr>
									</s:iterator>
									</tbody>
								</table>
                                <form name="deleteSalesModule_form" id="deleteSalesModule_form" action="" method="post"></form>
								<div id="footer" style="margin-top:10px">
								
									第${page.pageNo}页, 共<s:if test="page.totalPages<=-1">0</s:if><s:else>${page.totalPages}</s:else>页 
									<s:if test="page.hasPre">
										<a href="subsciber.action?page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}">上一页</a>
									</s:if>
									<s:if test="page.hasNext">
										<a href="tsubsciber.action?page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}">下一页</a>
									</s:if>
									<a href="sales-module!input.action">增加销售包</a>
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
