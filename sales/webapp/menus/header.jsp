<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!-- 一级菜单 -->
	<div id="header">
	<ul class="firstNav">
		<li class="logo"><img src="${ctx}/images/nortek.png"/></li>
		<li id="shop"><a href="/sales/sale/tenant.action">sales</a></li>
	</ul>
	<ul class="helpNav">
		<li class="start">&emsp;您好!&emsp;&emsp; </li>
		<!--<li><a href="${ctx}/j_spring_security_logout">退出</a></li>-->
	</ul>
</div>
<script type="text/javascript">
	var firstMenu = 'shop';
	var secondMenu='custom-info';
	var leftMenu = 'addressInfo';
	if(firstMenu == "oa-main"){
		$("#"+firstMenu).addClass("navPrdt1");
	}else{
		$("#"+firstMenu).addClass("fristBg");
	}
</script>


