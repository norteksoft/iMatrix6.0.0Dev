<!--一级菜单-->
<div id="header" class="ui-north">
	<div id="topnav" role="contentinfo">
		<a href="#" class="top-nav-first">
			<span></span>
		<#list menus?if_exists as item>
			<#if item_index<showNum>
			</a><a 
					<#if item.openWay.code=="firstMenu.open.way.currentPageOpen">
						href="${item.menuUrl?if_exists}" 
					<#elseif item.openWay.code=="firstMenu.open.way.newPageOpen">
						target="_blank" href="${item.menuUrl?if_exists}" 
					<#else>
						href="#" onclick="${item.event?if_exists}" 
					</#if>
				<#if item_index==showNum-1>id="lastSys"</#if> 
				    menuInfo="${item.layer?if_exists}_${item.code?if_exists}">
				<#if item.type.code?if_exists=='menu.type.standard'>
					<span><span 
							<#if item.imageUrl?if_exists!="">style="background: url('${imatrixUrl}/icons/${item.imageUrl}') no-repeat;width:0.5cm;height:0.5cm;padding-top:5px;"
							<#else>class="${item.code?if_exists}"
							</#if> 
							></span>${item.name?if_exists}</span>
				<#else>
					<span><span 
							<#if item.imageUrl?if_exists!="">style="background: url('${imatrixUrl}/icons/${item.imageUrl}') no-repeat;width:0.5cm;height:0.5cm;padding-top:5px;"
							<#else>class="custom"
							</#if>
							></span>${item.name?if_exists}</span>
				</#if>
			</#if>
		</#list>
		</a><#if showNum<menuSize><a id="selectNumen" class="top-nav-last">
					<span>${moreSystem}</span></a></#if>
	</div>
	<div id="header-logo">
	</div>
	<div id="honorific">
		<span><span class="man">&nbsp;</span>${honorificTitle}, 您好!</span>
		<span><span class="day">&nbsp;</span>${currentTime}</span>
		<span onclick="updatePassword();"><a href="#"><span class="password">&nbsp;</span>修改密码</a></span>  
		<#if themeChagable?if_exists>
			<span onclick="changeStyle(event, this);"><a href="#"><span class="theme">&nbsp;</span>换肤</a></span> 
		</#if>
		<#if existable?if_exists>
			<span ><a href="${imatrixUrl}/j_spring_security_logout"><span class="exit">&nbsp;</span>退出</a></span> 
		</#if>
	</div>
</div>

<!--二级菜单-->
<#if showSecMenu?if_exists=="true">
<div id="secNav">
		<a class="scroll-left-btn" onclick="_scrollLeft();">&lt;&lt;</a>
		<div class="fix-menu">
		<ul class="scroll-menu">
			<#list secMenus?if_exists as item>
					<li id="${item.code?if_exists}" menuInfo="${item.layer?if_exists}_${item.code?if_exists}">
						<span>
							<span>
								<a 
								<#if item.event?has_content>
									href="#" onclick="${item.event?if_exists}" 
								<#else>
									href="${item.menuUrl?if_exists}"
									<#if item.externalable?if_exists>
										target="_blank"
									</#if>
								</#if>
								>${item.name?if_exists}</a>
							</span>
						</span>
					</li>
			</#list>
		</ul>
		</div>
		<a class="scroll-right-btn" onclick="_scrollRight();">&gt;&gt;</a>
		<div class="hid-header" onclick="headerChange(this);" title="隐藏"></div>
</div>
</#if>
<!--左侧菜单-->
<#if showLeftMenu?if_exists=="true">
	<div class="ui-layout-west">
				<div id="__accordion" >
				  	<#list thirdMenus?if_exists as item>
						<h3><a href="${item.menuUrl?if_exists}" id="${item.code?if_exists}">${item.name?if_exists}</a></h3>
							<div id="div_${item.code?if_exists}">
								<#if showZtree?if_exists=="true">
									<#if item.id?if_exists==thirdMenuId?if_exists>
										<div id="__fourmenuTree" class="ztree" style="background: none;">
										</div>
									</#if>
								<#else>
									<#if fourMenus?if_exists?size gt 0>
										<#list fourMenus?if_exists as being>
												<#if being.event?if_exists=="">
													<div class="four-menu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
														<a href="${being.menuUrl?if_exists}" 
														<#if being.externalable?if_exists>
															target="_blank"
														</#if>>${being.name?if_exists}</a>
													</div>
												<#else>
													<div class="four-menu" menuInfo="${being.layer?if_exists}_${being.code?if_exists}">
														<a href="#this" onclick="${being.event?if_exists}">${being.name?if_exists}</a>
													</div>
												</#if>
										</#list>
									<#else>
										<div class="demo" id="${item.code?if_exists}_content" style="margin-top: 10px;"></div>
									</#if>
								</#if>
							</div>
					</#list>
				</div>
	</div>
</#if>

<script type="text/javascript">
	var __versionType = "${versionType?if_exists}";
	var __imatrixCtx = "${imatrixUrl?if_exists}";
	var __showSecMenu = "${showSecMenu?if_exists}";
	var __selectMenuInfo = ${selectMenuInfo};
	var __firstMenus = ${firstMenus};
	var __showNum = "${showNum?if_exists}";
	var __fourMenuTreeDatas = ${fourMenuTreeDatas};
	var __showZtree = "${showZtree?if_exists}";
	var __menuId="${menuId?if_exists}";
	
</script>
<script src="${resourcesCtx}/templateJs/totalMenuTag.js" type="text/javascript"></script>