function updatePassword(){
		if(__versionType=="online"){//在线试用版本
			alert("为确保系统的正常演示，屏蔽了该功能");
		}else{
			window.open(__imatrixCtx+"/acs/organization/user!updateUserPassword.action",'',"top=300,left=400,toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=400,height=300");
		}
}
function selectSystems(id){
	$('#styleList').hide();
	if($('#sysTableDiv').attr('id')!='sysTableDiv'){
		var table = "<div id='sysTableDiv'><table id='systemTable'><tbody>";
			table = table+appendFirstMenus();
			table = table+"</tbody></table></div>";
		$('body').append(table);
	}
	$('#sysTableDiv').show();
	var position = $("#"+id).position();
	$('#sysTableDiv').css('top', (position.top+36)+'px');
	$('#sysTableDiv').css('right', '0px');
}
function appendFirstMenus(){
	var tableInfo = "";
	if(__firstMenus!=""){
		var firstMenuInfos = eval(__firstMenus);
		for(var i=__showNum;i<firstMenuInfos.length;i++){
			tableInfo = tableInfo+"<tr><td>"+
			"<a ";
			if(firstMenuInfos[i]['openWay']=='CURRENT_PAGE_OPEN'){
				tableInfo = tableInfo+"href=\""+firstMenuInfos[i]['menuUrl']+"\" ";
			}else if(firstMenuInfos[i]['openWay']=='NEW_PAGE_OPEN'){
				tableInfo = tableInfo+"target=\"_blank\" href=\""+firstMenuInfos[i]['menuUrl']+"\" ";
			}else{
				tableInfo = tableInfo+"href=\"#\" onclick=\""+firstMenuInfos[i]['event']+"\" ";
			}
			if(i==__showNum-1){
				tableInfo = tableInfo+"id=\"lastSys\"";
			}
			tableInfo = tableInfo+"menuInfo=\""+firstMenuInfos[i]['layer']+"_"+firstMenuInfos[i]['code']+"\">";
			if(firstMenuInfos[i]['type']=='STANDARD'){
				tableInfo = tableInfo+"<span><span></span>"+firstMenuInfos[i]['name']+"</span>";
			}else{
				tableInfo = tableInfo+"<span><span class=\"custom\"></span>"+firstMenuInfos[i]['name']+"</span>";
			}
			tableInfo = tableInfo+"</a>"+
			"</td></tr>";
		}
	}
	return tableInfo;
}
var __thirdMenu = "";
$(document).ready(function(){
	if(__selectMenuInfo!=""){
		var selectInfos = eval(__selectMenuInfo);
		for(var i=0;i<selectInfos.length;i++){
			var menuinfo = selectInfos[i];
			var layer = menuinfo['layer'];
			if(layer=="1"){//一级菜单选中状态
				$("a[menuInfo='"+layer+"_"+menuinfo['code']+"']").attr("class","top-selected");
			}else if(layer=="2"){//二级菜单
				$("li[menuInfo='"+layer+"_"+menuinfo['code']+"']").attr("class","sec-selected");
			}else if(layer=="3"){//三级菜单
				__thirdMenu = menuinfo['code'];
			}else if(layer=="4"){//四级菜单
				$("div[menuInfo='"+layer+"_"+menuinfo['code']+"']").attr("class","four-menu-selected");
			}
		}
	}
	if(__thirdMenu!=""){
		__initAccordion();
		if(__showZtree=="true"){
			__createFourMenuTree();
		}
	}
});

function __initAccordion(){
	$("#__accordion").accordion({
		fillSpace: true,
		active: __getIndex('#__accordion'),
		change: function(event, ui) {
			location.href=$($(ui.newHeader[0]).children()[1]).attr('href');
		}
	});
}
function __getIndex(id){
	var subs = $(id).children("h3");
	for(var i = 0; i < subs.length; i++){
		var hs0 = $($(subs[i]).children('a')[0]).attr('id');
			if(__thirdMenu==hs0){
				return i;
			}
	}
	return 0;
}

//创建四级菜单及其孩子树
function __createFourMenuTree(){
	var setting = {
			data: {
				simpleData: {
					enable: true
				}
			},
			view: {
				showLine: false
			},
			callback : {
				onClick:__onClick
			}
		};
	$.fn.zTree.init($("#__fourmenuTree"), setting,__fourMenuTreeDatas);
	__getMenuSelectNode();
}

function __onClick(event, treeId, treeNode){
	var treedata = eval(treeNode.data);
	var menu = treedata[0];
	if(menu.externalable){
		window.open(menu.menuUrl+"?menuId="+menu.id, 'write');
	}else{
		window.location.href=menu.menuUrl+"?menuId="+menu.id;
	}
}

function __getMenuSelectNode(){
	var treeObj = $.fn.zTree.getZTreeObj("__fourmenuTree");
	if(treeObj!=null&&typeof(treeObj)!='undefined'){
		var nodes = treeObj.getNodes();
		var __selectNode = treeObj.getNodesByFilter(function(node){
			if(node.id==__menuId){
				return true;
			}
			return false;
		},true);
		treeObj.selectNode(__selectNode);
	}
}
