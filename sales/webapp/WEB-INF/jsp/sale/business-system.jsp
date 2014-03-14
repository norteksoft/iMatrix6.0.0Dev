<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title><s:text name="businessSystem.businessManager"/>(list)</title>
    <%@ include file="/common/meta.jsp"%>
    <link   type="text/css" rel="stylesheet" href="${ctx}/widgets/colorbox/colorbox.css" />
	<script type="text/javascript" src="${ctx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript">
	function updatUrlCache(){
		$.post("${ctx}/sale/business-system!updateUrlCache.action", "", postSuccess);	
	}
	function postSuccess(msg){
		$('#message').html("<span id='slogan' style='color: red;'>已更新系统URL缓存</span>");
		setTimeout('$("#slogan").hide("slow");',3000);
	}
	
	function deleteSystem(url){
		var f=confirm("确定要删除整个系统？");
		if(f){
			window.location.href=url;
		}else{
            return;
		}
	}
	function updatFunctionCache(){
		$.post("${ctx}/sale/business-system!updateFunctionCache.action", "", postFuncSuccess);	
	}

	function postFuncSuccess(msg){
		$('#message').html("<span id='slogan' style='color: red;'>已更新资源缓存</span>");
		setTimeout('$("#slogan").hide("slow");',3000);
	}
</script>
</head>
<body> 
<form action="" name="exportForm" id="exportForm" method="post">
<input name="systemIds" type="hidden" id="systemIds">
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
								<h5>业务系统管理</h5>
							</div>
						<div class="widget-content">
						     <div id="message"></div>
                                <div id="search" >
								   <form action="business-system!search.action" method="post">
								       <table style="position:relative; left:0">
								          	  <tr>
									               <td><h5><s:text name="businessSystem.businessCode"/></h5></td><td><input type="text" name="code"/>&nbsp;&nbsp;&nbsp;</td>
									               <td><h5><s:text name="businessSystem.businessName"/></h5></td><td><input type="text" name="name"/>&nbsp;&nbsp;&nbsp;</td>
									               <td><h5><s:text name="function.functionPath"/></h5></td><td><input type="text" name="path"/></td>
									               <td align="center" colspan="4"><p class="buttonP"><input class="btnStyle" type="submit" value='<s:text name="common.search"/>'/>&nbsp;&nbsp;&nbsp;<input class="btnStyle"  type="reset" value='<s:text name="common.reset"/>'/></p></td>
									          </tr> 
								        </table>
								    </form>
								</div>
								<div style="width:500px; height:5px;"></div>
                                <div id="content">
									<table class="Table changeTR">
									<thead>
									<tr>
										<th style="width: 30px;" align="center"></th>
										<th><b><s:text name="businessSystem.businessCode"/></b></th>
									    <th><b><s:text name="businessSystem.businessName"/></b></th>
										<th><b><s:text name="businessSystem.businessPath"/></b></th>
										<th><b><s:text name="common.operate"/></b></th>
									</tr>
									</thead>	
									<tbody>
									   <s:iterator value="page.result">		
											<tr>
												<td style="width: 30px;" align="center"><input type="checkbox" name="id" value="${id }"></input></td>
												<td>${code}&nbsp;</td>
											    <td>${name}&nbsp;</td>
												<td>${path}&nbsp;</td>
												<td>
													<a href="business-system!input.action?id=${id}"><s:text name="common.alter"/></a>
													<a  id="deleteSystem" onclick='deleteSystem("business-system!delete.action?id=${id}");' href="#"><s:text name="common.delete"/></a>
													<a href="standard-role.action?businessSystemId=${id}">角色列表</a>
													<a href="function-group!getFuncGroupsBySystem.action?systemId=${id}">资源组列表</a>
													<a href="function!getFunctionsBySystem.action?systemId=${id}">资源列表</a>
												</td>
											</tr>
									  </s:iterator>	
									</tbody> 	
									</table>
								</div>
                                <div id="footer" style="margin-top:10px">
									<s:text name="common.NO."/>${page.pageNo}<s:text name="common.page"/>, <s:text name="common.total"/><s:if test="page.totalPages<=-1">0</s:if><s:else>${page.totalPages}</s:else><s:text name="common.page"/> 
									<s:if test="page.hasPre">
										<a href="business-system.action?page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.upPage"/></a>
									</s:if>
									<s:if test="page.hasNext">
										<a href="business-system.action?page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}"><s:text name="common.downPage"/></a>
									</s:if>
									<a href="business-system!input.action"><s:text name="businessSystem.addBusiness"/></a>
									<a href="#" onclick="updatUrlCache();">更新URL缓存</a>
									<a href="#" onclick="updatFunctionCache();">更新资源缓存</a>
								</div>
								<form name="deleteSystem_form" id="deleteSystem_form" method="post"  action="" ></form>
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