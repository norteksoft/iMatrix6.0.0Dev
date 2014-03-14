<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 公司管理</title>
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
								<h5>导入基础数据</h5>
							</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
								<div id="content">
									<form action="import-data!save.action" method="post" enctype="multipart/form-data">
										<table class="inputView">
											<tr>
												<td>表名：</td>
												<td><input type="text" name="tableName"/></td>
											</tr>
											<tr>
												<td><s:file name="file" label="File"></s:file></td>
											</tr>
											<tr>
												<td colspan="2"><p class="buttonP" ><input class="btnStyle" type="submit" value="提交"/></p></td>
											</tr>
										</table>
									</form>
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


