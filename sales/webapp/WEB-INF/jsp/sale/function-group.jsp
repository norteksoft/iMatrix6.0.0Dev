<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><s:text name="functionGroup.functionGroupManager"/>(list)</title>
<%@ include file="/common/meta.jsp"%>
	<link   type="text/css" rel="stylesheet" href="${ctx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${ctx}/widgets/colorbox/jquery.colorbox.js"></script>
	<script>
		var ids=null;
		var msg="";
		function addFunctionToMenu(){
			ids=$("input[name='id']:checked");
			if(ids.length==0){
				alert("请选择需要合并的资源组!");
				return;
			}
			for(var i=0;i<ids.length;i++){
				msg=msg+ids[i].value+(i==ids.length-1?"":",");
			}
			init_tb("${ctx}/sale/function!menuTree.action?systemId=${systemId}&msg="+msg+"&TB_iframe=true&width=300&height=400","合并资源");
			msg="";
		}
	</script>
</head>
<body> 
<form action="" name="exportForm" id="exportForm" method="post">
<input name="funcGroupIds" type="hidden" id="funcGroupIds">
<input name="systemId" id="systemId" value="${systemId }" type="hidden">
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
						<h5><s:text name="functionGroup.functionGroupList"/></h5>
					</div>
						<div class="widget-content">
						    <div id="message"><s:actionmessage theme="mytheme"/></div>
						    <div id="search" >
							   <form action="function-group!search.action" method="post">
							   <input name="systemId" id="_systemId" value="${systemId }" type="hidden">
							       <table>
								          <tr>
								               <td><s:text name="functionGroup.functionGroupCode"/></td><td><input type="text" name="code"/>&nbsp;&nbsp;&nbsp;</td>
								               <td><s:text name="functionGroup.functionGroupName"/></td><td><input type="text" name="name"/>&nbsp;&nbsp;&nbsp;</td>
								               <td align="center" colspan="4"><p class="buttonP"><input class="btnStyle"  type="submit" value="<s:text name="common.search"/>"/>&nbsp;<input class="btnStyle" type="reset" value="<s:text name="common.reset"/>"/></p></td>
								          </tr>
							        </table>
							    </form>
							</div>
							<div id="content">
							<table class="Table changeTR">
							<tr>
									<th style="width: 30px;" align="center"></th>
							        <th><b><s:text name="functionGroup.functionGroupCode"/></b></th>
									<th><b><s:text name="functionGroup.functionGroupName"/></b></th>
									<th><b><s:text name="common.operate"/></b></th>
							</tr>
								
							   <s:iterator value="page.result">	
									<tr>
										<td style="width: 30px;" align="center"><input type="checkbox" name="id" value="${id }"></input></td>
									    <td>${code}&nbsp;</td>
										<td>${name}&nbsp;</td>
										<td>
											<a href="function-group!delete.action?id=${id}"><s:text name="common.delete"/></a>
											<a href="function-group!removeFunction.action?paternId=${id}&systemId=${systemId}">查看资源</a>
										</td>
									</tr>                       
							  </s:iterator>	
							</table>
							</div>
							<div id="footer" style="margin-top:10px">
								<s:text name="common.NO."/>${page.pageNo}<s:text name="common.page"/>, <s:text name="common.total"/>${page.totalPages}<s:text name="common.page"/> 
								<s:if test="page.hasPre">
									<a href="function-group.action?systemId=${systemId}&page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.upPage"/></a>
								</s:if>
								<s:if test="page.hasNext">
									<a href="function-group.action?systemId=${systemId}&page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.downPage"/></a>
								</s:if>
								<a href="javascript:void(0)" onclick="addFunctionToMenu()">合并资源到菜单</a>
								<a href="business-system.action">返回</a>
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