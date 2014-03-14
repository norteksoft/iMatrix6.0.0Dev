<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>租户管理</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
function encryotionByMD5(){
	ajaxSubmit("tenantForm",  "${ctx}/sale/tenant!encryotionByMD5.action","tenantZone",showMsg);
}
function deleteTenant(url){
	var f=confirm("确定要删除租户？");
	if(f){
		window.location.href=url;
	}else{
        return;
	}
}
</script>
</head>

<body>
<form action="" name="tenantForm" id="tenantForm" method="post">
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
						<h5>租户管理</h5>
					</div>
					<aa:zone name="tenantZone">
						<div class="widget-content">
						    <p class="buttonP">
                               	<input class="btnStyle" type="button" value='<s:text name="common.md5"/>' onclick="encryotionByMD5();"/>
                           	</p>
						     <div id="message" style="color: red;"><s:actionmessage theme="mytheme"/></div>
							 <table class="Table changeTR">
			                   <thead>
								 <tr>
									<th>
									 <a href="${ctx}/sale/tenant.action?page.orderBy=tenantName&page.order=
											<s:if test="page.orderBy=='tenantName'">${page.inverseOrder}</s:if><s:else>desc</s:else>">
											<b><s:text name="tenantName"/></b>
									 </a>
									</th>
									<th><b>联系人</b></th>
									<th><b>联系人电话</b></th>
									<th><b>Email</b></th>
									
									<th><b>公司名称</b></th>
									<th><b>国家</b></th>
									<th><b>城市</b></th>
									<th><b>公司地址</b></th>
									<th><b>所属行业</b></th>
									<th><b>公司人数</b></th>
									<th><b>公司备注</b></th>
									<th><b>操作</b></th>
								 </tr>
								</thead>
							  <tbody>
								<s:iterator value="page.result">
								<tr>
									<td>${tenantName}&nbsp;</td>
									<td>${linkman}&nbsp;</td>
									<td>${telephone}&nbsp;</td>
									<td>${email}&nbsp;</td>
									
									<td>${company.name}&nbsp;</td>
									<td>${company.country}&nbsp;</td>
									<td>${company.city}&nbsp;</td>
									<td>${company.address}&nbsp;</td>
									<td>${company.industry}&nbsp;</td>
									<td>${company.peopleNumber}&nbsp;</td>
									<td>${company.remark}&nbsp;</td>
									<td>&nbsp; 
											<a href="tenant!input.action?id=${id}">修改</a>|
											<a id="deleteTenant" onclick='deleteTenant("tenant!delete.action?id=${id}");' href="#">删除</a>|
											<a href="subsciber!input.action?tenantId=${id}">增加订单</a>|
											<a href="subsciber.action?tenantId=${id}">查看订单</a>
									</td>
								</tr>
								</s:iterator>
							  </tbody>
		                 </table>
		                 
		                 <div id="footer" style="margin-top:10px">
							第${page.pageNo}页, 共${page.totalPages}页 
							<s:if test="page.hasPre">
								<a href="tenant.action?page.pageNo=${page.prePage}&page.orderBy=${page.orderBy}&page.order=${page.order}">上一页</a>
							</s:if>
							<s:if test="page.hasNext">
								<a href="tenant.action?page.pageNo=${page.nextPage}&page.orderBy=${page.orderBy}&page.order=${page.order}">下一页</a>
							</s:if>
								<a href="tenant!input.action">增加租户</a>
							</div>
						</div>
					</aa:zone>
					<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
				</div>
			</div>
    	</div>
    </div>
  </div>
</div>
</body>
</html>
