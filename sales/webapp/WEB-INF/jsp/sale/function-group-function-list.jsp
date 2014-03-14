<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page language="java"  pageEncoding="UTF-8"%>
 <%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><s:text name="function.functionManager"/>list)</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/js/validate/jquery.validate.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/js/validate/jquery.validate.js" type="text/javascript"></script>
<script src="${ctx}/js/validate/messages_cn.js" type="text/javascript"></script>
<script type="text/javascript">
function itde()
{
	var checkset=document.getElementsByName("functionIds");
	var result=0;
	var itemId=0;

	for(var i=0;i<checkset.length;i++){
	    if(checkset[i].checked==true){
	      result++;
	      itemId=checkset[i].value;
	     
	    }
	}

	if(result==0){
       alert('<s:text name="selectOne"/>');
      
       return false;
	}
	return true;
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
						<h5><s:text name="function.functionList"/></h5>
					</div>
						<div class="widget-content">
						     <div id="message"><s:actionmessage theme="mytheme"/></div>
					           <div id="search">
								   <form action="function!search.action" method="post">
								   		<input type="hidden" name="paternId" value="${paternId}"/>
										<input type="hidden" name="addOrRemove" value="${addOrRemove }"/>
										<input type="hidden" name="systemId" value="${systemId }"/>
								       <table>
									          <tr>
									               <td><s:text name="function.functionCode"/></td><td><input type="text" name="code"/>&nbsp;&nbsp;&nbsp;</td>
									               <td><s:text name="function.functionName"/></td><td><input type="text" name="name"/>&nbsp;&nbsp;&nbsp;</td>
									               <td><s:text name="function.functionPath"/></td><td><input type="text" name="path"/></td>
									               <td align="center" colspan="4"><input type="submit" value="<s:text name="common.search"/>"/>&nbsp;<input type="reset" value="<s:text name="common.reset"/>"/><input type="button" value="<s:text name="返回"/>" onclick="location.href='function-group!getFuncGroupsBySystem.action?systemId=${systemId}'"/></td>
									          </tr>
								        </table>
								    </form>
								</div>
								<div id="content">
								<form action="function-group!saveFunction.action" name="form" method="post" onsubmit="return itde()">
								<input type="hidden" name="paternId" value="${paternId}"/>
								<input type="hidden" name="addOrRemove" value="${addOrRemove }"/>
								<table class="Table changeTR">
									<tr>
									    <th></th>
									    <th><b><s:text name="function.functionCode"/></b></th>
										<th><b><s:text name="function.functionName"/></b></th>
										<th><b><s:text name="function.functionPath"/></b></th>
									</tr>
								   <s:iterator value="pageFunction.result">	
								   		<s:if test="addOrRemove==0">
											<tr>
											    <td>
											        <s:if test="functionGroup.id==paternId">
											            <input type="checkbox" name="functionIds" value="${id}" checked="checked" disabled="disabled"/>
											       </s:if>
											       <s:else>
											            <input type="checkbox" name="functionIds" value="${id}"/>
											       </s:else>
											    </td>
											    <td>${code}&nbsp;</td>
												<td>${name}&nbsp;</td>
												<td>${path}&nbsp;</td>
											</tr>
										</s:if>
										<s:elseif test="addOrRemove==1">
											<tr>
												<td><input type="checkbox" name="functionIds" value="${id}"/></td>
											    <td>${code}&nbsp;</td>
												<td>${name}&nbsp;</td>
												<td>${path}&nbsp;</td>
											</tr>
										</s:elseif>
								  </s:iterator>	
								</table>
								</form>
								</div>
								<div id="footer" style="margin-top:10px">
										<s:text name="common.NO."/>${pageFunction.pageNo}<s:text name="common.page"/>, <s:text name="common.total"/>${pageFunction.totalPages}<s:text name="common.page"/>
											<s:if test="addOrRemove==0">
												<s:if test="pageFunction.hasPre">
												<a href="${ctx}/sale/function-group!inputFunction.action?systemId=${systemId}&paternId=${paternId}&pageFunction.pageNo=${pageFunction.prePage}&pageFunction.orderBy=${pageFunction.orderBy}&pageFunction.order=${pageFunction.order}"><s:text name="common.upPage"/></a>
											</s:if>
											<s:if test="pageFunction.hasNext">
												<a href="${ctx}/sale/function-group!inputFunction.action?systemId=${systemId}&paternId=${paternId}&pageFunction.pageNo=${pageFunction.nextPage}&pageFunction.orderBy=${pageFunction.orderBy}&pageFunction.order=${pageFunction.order}"><s:text name="common.downPage"/></a>
											</s:if>
										</s:if>
										<s:elseif test="addOrRemove==1">
											<s:if test="pageFunction.hasPre">
												<a href="${ctx}/sale/function-group!removeFunction.action?systemId=${systemId}&paternId=${paternId}&pageFunction.pageNo=${pageFunction.prePage}&pageFunction.orderBy=${pageFunction.orderBy}&pageFunction.order=${pageFunction.order}"><s:text name="common.upPage"/></a>
											</s:if>
											<s:if test="pageFunction.hasNext">
												<a href="${ctx}/sale/function-group!removeFunction.action?systemId=${systemId}&paternId=${paternId}&pageFunction.pageNo=${pageFunction.nextPage}&pageFunction.orderBy=${pageFunction.orderBy}&pageFunction.order=${pageFunction.order}"><s:text name="common.downPage"/></a>
											</s:if>
										</s:elseif>
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