<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><s:text name="functionGroup.functionGroupManager"/>(list)</title>
<%@ include file="/common/meta.jsp"%>
<link href="${ctx}/js/validate/jquery.validate.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/js/validate/jquery.validate.js" type="text/javascript"></script>
<script src="${ctx}/js/validate/messages_cn.js" type="text/javascript"></script>
<script type="text/javascript">
function itde()
{
	var checkset=document.getElementsByName("functionGroupIds");
	var result=0;
	var itemId=0;

	for(var i=0;i<checkset.length;i++){
	    if(checkset[i].checked==true){
	      result++;
	      itemId=checkset[i].value;
	     
	    }
	}

	if(result==0){
       alert('<s:text name="common.selectOne"/>');
      
       return false;
	}
	return true;
}

</script>
</head>
<body> 

   <div id="menu">
<h3><s:text name="functionGroup.functionGroupList"/> </h3>
</div>
<div id="aaa"> 
 <s:text name="businessSystem.businessName"/>:${name}
 </div>
<div id="message"><s:actionmessage theme="mytheme"/></div>

<div id="search">
   <form action="business-system!inputFunctionGroup.action" method="post">
       <table>
	          <tr>
	               <td><s:text name="functionGroup.functionGroupCode"/></td><td><input type="text" name="code"/>&nbsp;&nbsp;&nbsp;</td>
	               <td><s:text name="functionGroup.functionGroupName"/></td><td><input type="text" name="name"/>&nbsp;&nbsp;&nbsp;</td>
	               <td><s:text name="function.functionPath"/></td><td><input type="text" name="path"/></td>
	               <td align="center" colspan="4"><input type="submit" value='<s:text name="common.search"/>'/>&nbsp;<input type="reset" value='<s:text name="common.reset"/>'/></td>
	          </tr>
        </table>
    </form>
</div>



<div id="content">
<form action="business-system!systemAddFunctionGroup.action" method="post" onsubmit=" return itde();">
<input type="hidden" name="businessSystemId" value="${businessSystemId}"/>
<table class="listView">                           
<tr>
           <th></th>
         <th><a href="${ctx}/authorization/business-system!inputFunctionGroup.action?pageFunctionGroup.orderBy=code&pageFunctionGroup.order=
		<s:if test="pageFunctionGroup.orderBy=='code">${pageFunctionGroup.inverseOrder}</s:if><s:else>desc</s:else>
		"><b><s:text name="functionGroup.functionGroupCode"/></b></a></th>
		<th><a href="${ctx}/authorization/business-system!inputFunctionGroup.action?pageFunctionGroup.orderBy=name&pageFunctionGroup.order=
		<s:if test="pageFunctionGroup.orderBy=='name'">${pageFunctionGroup.inverseOrder}</s:if><s:else>desc</s:else>
		"><b><s:text name="functionGroup.functionGroupName"/></b></a></th>
</tr>

<s:set name="isId" value="businessSystemId"/>

  <s:iterator value="pageFunctionGroup.result">	
		<tr><td>
		       <s:if test="#isId.equals(businessSystem.id)">
		            <input type="checkbox" name="functionGroupIds" value="${id}" checked="checked"/>
		       </s:if>
		       <s:else>
		             <input type="checkbox" name="functionGroupIds" value="${id}" />
		       </s:else>
		   </td>
		    <td>${code}&nbsp;</td>
			<td>${name}&nbsp;</td>
			
		</tr>
  </s:iterator>	
  	<tr><td colspan="3">
	  	<p class="buttonP"> 
  	         <input class="btnStyle" type="submit" value="<s:text name="common.submit"/>"/>
  	         <input class="btnStyle" type="reset" value="<s:text name="common.reset"/>"/>
  	         <input class="btnStyle" type="button" value="<s:text name="common.cancel"/>" onclick="history.back()"/> 
	  	</p>         
  	   </td>
  	</tr>
</table>
</form>
</div>

<div id="footer" style="margin-top:10px">
	<s:text name="common.NO."/>${pageFunctionGroup.pageNo}<s:text name="common.page"/>, <s:text name="common.total"/>${pageFunctionGroup.totalPages}<s:text name="common.page"/>
	<s:if test="pageFunctionGroup.hasPre">
		<a href="${ctx}/authorization/business-system!inputFunctionGroup.action?pageFunctionGroup.pageNo=${pageFunctionGroup.prePage}&pageFunctionGroup.orderBy=${pageFunctionGroup.orderBy}&pageFunctionGroup.order=${pageFunctionGroup.order}"><s:text name="common.upPage"/></a>
	</s:if>
	<s:if test="pageFunctionGroup.hasNext">
		<a href="${ctx}/authorization/business-system!inputFunctionGroup.action?pageFunctionGroup.pageNo=${pageFunctionGroup.nextPage}&pageFunctionGroup.orderBy=${pageFunctionGroup.orderBy}&pageFunctionGroup.order=${pageFunctionGroup.order}"><s:text name="common.downPage"/></a>
	</s:if>
		
</div>
</body>
</html>