<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/mms-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>菜单管理</title>
	<%@ include file="/common/mms-iframe-meta.jsp"%>
	
	<script src="${mmsCtx}/js/menu.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			createMenuTree();
			 var windowHeigth = $(window).height();
			 $("#opt-content").css("overflow-y","hidden");
			 $("#menu-tree").height(windowHeigth-60);
		});
		function getTreeObj(){
			var obj=$.fn.zTree.getZTreeObj("menu-tree");
			return obj;
		}
		function exportMenu(){
			ajaxSubmit("defaultForm",  webRoot+"/module/export-menu.htm");
			buttonFlag=0;
		}
		function importMenu(){
			$.colorbox({href:'${mmsCtx}/module/show-import-menu.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入菜单"});
		}
		function updatUrlCache(){
			ajaxSubmit("defaultForm",  "${mmsCtx}/module/update-url-cache.htm","",updateUrlCacheCallback);
		}

		function updateUrlCacheCallback(){
			$('#message').css("display","block");
			$('#message').html("<font class=\"onSuccess\"><nobr>更新URL缓存成功</nobr></font>");
			location.hash="anchor";
			setTimeout('$("#message").css("display","none");',3000);
		}

		function exportCustomSystem(){
			if(!currentMenuId||typeof(currentMenuId)=="undefined"){
				alert("请选择自定义系统的一级菜单！");return;
			}
			$.ajax({
				data:{menuId:currentMenuId},
				type:"post",
				url:webRoot+"/module/validate-menu.htm",
				beforeSend:function(XMLHttpRequest){},
				success:function(data, textStatus){
					if("ok"==data){
						$("#defaultForm").attr("action",webRoot+"/module/export-custom-system.htm?menuId="+currentMenuId);
						$("#defaultForm").submit();
					}else{
						alert("请选择自定义系统为草稿状态或启用状态的一级菜单！");
					}
				},
				complete:function(XMLHttpRequest, textStatus){},
		        error:function(){}
			});
		}

		function importCustomSystem(){
			$.colorbox({href:'${mmsCtx}/module/show-import-custom-system.htm',
				iframe:true, innerWidth:350, innerHeight:100,overlayClose:false,title:"导入自定义系统",onClosed:function(){
					refreshTree();
				}});
		}

		/**下拉按钮效果 ****/
		function initNewBtnGroup(){//默认按钮效果  
			$("#parentNewBtn")
				.button()
					.click(function() {
						createSystem();
						//$("#enablebtn").hide();
						$("#importbtn").hide();
						})
				.next()
					.button( {
						text: false,
						icons: {
							primary: "ui-icon-triangle-1-s"
						}
					})
					.click(function() {
						//$("#enablebtn").hide();
						$("#importbtn").hide();
						showNewBtnDiv();
					})
					.parent()
					.buttonset();
		}
		function showNewBtnDiv(){//显示更多按钮效果位置  
			if($("#newbtn").css("display")=='none'){
				$("#newbtn").show();
				var position = $("#_newbtn").position();
				$("#newbtn").css("left",position.left+0);
				$("#newbtn").css("top",position.top+24);
			}else{
				$("#newbtn").hide();
			};
			$("#newbtn").hover(
				function (over ) {
					$("#newbtn").show();
				},
				function (out) {
					 $("#newbtn").hide();
				}
			); 

		}
		/**下拉按钮效果 ****/
		function initEnableBtnGroup(){//默认按钮效果  
			$("#parentEnableBtn")
				.button()
					.click(function() {
						enableMenu();
						$("#newbtn").hide();
						$("#importbtn").hide();
						})
				.next()
					.button( {
						text: false,
						icons: {
							primary: "ui-icon-triangle-1-s"
						}
					})
					.click(function() {
						$("#newbtn").hide();
						$("#importbtn").hide();
						showEnableBtnDiv();
					})
					.parent()
					.buttonset();
		}
		function showEnableBtnDiv(){//显示更多按钮效果位置  
			if($("#enablebtn").css("display")=='none'){
				$("#enablebtn").show();
				var position = $("#_enablebtn").position();
				$("#enablebtn").css("left",position.left+0);
				$("#enablebtn").css("top",position.top+24);
			}else{
				$("#enablebtn").hide();
			};
			$("#enablebtn").hover(
				function (over ) {
					$("#enablebtn").show();
				},
				function (out) {
					 $("#enablebtn").hide();
				}
			); 

		}
		/**下拉按钮效果 ****/
		function initImportBtnGroup(){//默认按钮效果  
			$("#parentImportBtn")
				.button()
					.click(function() {
						if("online"=="${versionType}"){
							demonstrateOperate();
						}else{
							importMenu();
						}
						$("#newbtn").hide();
						//$("#enablebtn").hide();
						})
				.next()
					.button( {
						text: false,
						icons: {
							primary: "ui-icon-triangle-1-s"
						}
					})
					.click(function() {
						$("#newbtn").hide();
						//$("#enablebtn").hide();
						showImportBtnDiv();
					})
					.parent()
					.buttonset();
		}
		function showImportBtnDiv(){//显示更多按钮效果位置  
			if($("#importbtn").css("display")=='none'){
				$("#importbtn").show();
				var position = $("#_importbtn").position();
				$("#importbtn").css("left",position.left+0);
				$("#importbtn").css("top",position.top+24);
			}else{
				$("#importbtn").hide();
			};
			$("#importbtn").hover(
				function (over ) {
					$("#importbtn").show();
				},
				function (out) {
					 $("#importbtn").hide();
				}
			); 

		}
	</script>
	<style type="text/css">
		.tree ul{
			z-index: 0;
		}
		.tree ul li{
			z-index: 0;
		}
		
	</style>
</head>
<body onload="">
<div class="ui-layout-center">
	<div class="opt-body">
	<form action="" method="post" name="defaultForm" id="defaultForm"></form>
		<div class="opt-btn">
			<div class="btndiv" id="_newbtn" style="*top:-2px;">
				<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentNewBtn">
					<span class="ui-button-text">新建系统</span>
				</button>
				<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
					<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
					<span class="ui-button-text">更多</span>
				</button>
			</div>
			<div id="newbtn" class="flag">
				<ul >
					<li><a href="#" onclick="createMenu();">新建菜单</a></li>
					<li><a href="#"  onclick="updateMenu();">修改</a></li>
					<li><a href="#"  onclick="deleteMenu();">删除</a></li>
				</ul>
			</div>
			<script type="text/javascript">
				$(document).ready(function() {
					initNewBtnGroup();
					//alert($("#opt-content").height());
				});
			</script>
			<button class="btn" onclick="updatUrlCache();"><span><span >更新URL缓存</span></span></button>
			<button class="btn" onclick="enableMenu();"><span><span >启用</span></span></button>
			<s:if test='#versionType=="online"'>
				<button class="btn" onclick="demonstrateOperate();"><span><span >禁用</span></span></button>
			</s:if><s:else>
				<button class="btn" onclick="disableMenu();"><span><span >禁用</span></span></button>
			</s:else>
			<!-- 
			<div class="btndiv" id="_enablebtn" style="*top:-2px;">
				<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentEnableBtn">
					<span class="ui-button-text">启用</span>
				</button>
				<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
					<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
					<span class="ui-button-text">更多</span>
				</button>
			</div>
			<div id="enablebtn" class="flag">
				<ul >
					<s:if test='#versionType=="online"'>
						<li><a href="#" onclick="demonstrateOperate();">禁用</a></li>
					</s:if><s:else>
						<li><a href="#" onclick="disableMenu();">禁用</a></li>
					</s:else>
				</ul>
			</div>
			<script type="text/javascript">
				$(document).ready(function() {
					initEnableBtnGroup();
				});
			</script>
			 -->
			 <div class="btndiv" id="_importbtn" style="*top:-2px;">
				<button  class="ui-button ui-widget ui-state-default ui-button-text-only ui-corner-left" id="parentImportBtn">
					<span class="ui-button-text">导入</span>
				</button>
				<button  title="更多"  class="ui-button ui-widget ui-state-default ui-button-icon-only ui-corner-right" id="select">
					<span class="ui-button-icon-primary ui-icon ui-icon-triangle-1-s"></span>
					<span class="ui-button-text">更多</span>
				</button>
			</div>
			<div id="importbtn" class="flag">
				<ul style="width: 120px;">
					<li><a href="#" onclick="exportMenu();">导出</a></li>
					<s:if test='#versionType=="online"'>
						<li><a href="#" onclick="demonstrateOperate();">导入自定义系统</a></li>
					</s:if><s:else>
						<li><a href="#" onclick="importCustomSystem();">导入自定义系统</a></li>
					</s:else>
					<li><a href="#" onclick="exportCustomSystem();">导出自定义系统</a></li>
				</ul>
			</div>
			<script type="text/javascript">
				$(document).ready(function() {
					initImportBtnGroup();
				});
			</script>
			<!-- 
				<s:if test='#versionType=="online"'>
					<button class="btn" onclick="demonstrateOperate();"><span><span >导入</span></span></button>
				</s:if><s:else>
					<button class="btn" onclick="importMenu();"><span><span >导入</span></span></button>
				</s:else>
			 -->
		</div>
		<div id="opt-content" >
			<div id="message" style="display:none; ">&nbsp;</div>
			<div class="ztree" id="menu-tree" style="overflow-y:auto;overflow-x:hidden; border:1px;background: none;"></div>
		</div>
	</div>
</div>
</body>
</html>
