<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.ajaxanywhere.AAUtils"%> 
<%     
  response.setHeader("Pragma","No-cache");     
  response.setHeader("Cache-Control","no-cache");     
  response.setDateHeader("Expires",   0);     
  %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Mini-Web 帐号管理</title>
<%@ include file="/common/meta.jsp"%>
<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<link rel="stylesheet" type="text/css" href="${ctx}/widgets/jquerytree/tree_component.css" />
<script type="text/javascript" src="${ctx}/widgets/jquerytree/_lib/css.js"></script>
<script type="text/javascript" src="${ctx}/widgets/jquerytree/tree_component.js"></script>
<script src="${ctx}/js/aa.js" type="text/javascript"></script>
<script>
$(document).ready(function(){
	validate();
	multipleTree();
});
//验证框架
function validate(){
	$.formValidator.initConfig({formid:"inputForm",onsuccess: function() {},onerror:function(){}});
	$("#moduleName").formValidator({onshow:'请输入',onfocus:'请输入',oncorrect:'成功'}).inputValidator({min:1,empty:{emptyerror:'错误'},onerror:'错误'});

}

//提交方法
function save(){
	 var funList=$("#functionTree").find("a.checked");
	 if(funList.length<=0){
		 alert("请选择资源！");
	       return;
		  }
	     var ids="";
		for(var i=0;i<funList.length;i++){
	       var data=$(funList[i]).parents("li").attr("id");
	       var type=data.substring(0,data.indexOf("_"));
	       if(type!="system"){
	         ids+=data.substring(data.indexOf("_")+1,data.indexOf("="))+",";
	       }
			}
		var selector = document.getElementById("systemslist");
		var index = selector.selectedIndex;
		var value=selector.options[index].value;
		$("#systemId").attr("value",value);
		$("#ids").attr("value",ids.substring(0,ids.length-1));
	    $("#inputForm").submit();
}

	function multipleTree(){
		var selector = document.getElementById("systemslist");
		var index = selector.selectedIndex;
		var value=selector.options[index].value;
		var checkdeFunctionIds=$("#functionids").attr("value");
		checkdeFunctionIds=checkdeFunctionIds.substring(0,checkdeFunctionIds.length-1);
		$.ajaxSetup({cache:false});
		$("#functionTree").tree({
			data:{
			    type:"json",
			    url: "${ctx}/sale/sales-module!sysFuntionsTree.action?systemId="+value,
			    async:true,
			    async_data:function (NODE){ return {currentId:$(NODE).attr("id") || 0}}
					  },
			rules : {
		            droppable : [ "tree-drop" ],
		            multiple : true,
		            deletable : "all",
		            draggable : "all"
		    },
			ui : {
				theme_name : "checkbox",
				context:[]
			},
			callback : { 
			    onselect    : function(NODE,TREE_OBJ) { },
			    onopen      : function(NODE, TREE_OBJ) { 
			    	var list = $(NODE).find("li");
			    	 var data="";
			    	 var id="";
			    	for(var i=0;i<list.length;i++){
			    		data=$(list[i]).attr("id");
			    		id=data.substring(data.indexOf("_")+1,data.indexOf("="));
			    		if(checkdeFunctionIds.indexOf(id)!=-1&&checkdeFunctionIds.length>0){
			    			$(list[i]).children("a").addClass("checked");
				    		}
				    	}
			    
				    },
				onchange : function (NODE, TREE_OBJ) {
					if(TREE_OBJ.settings.ui.theme_name == "checkbox") {
						var state;
						var $this = $(NODE).is("li") ? $(NODE) : $(NODE).parent();
						if($this.children("a.unchecked").size() == 0) {
							TREE_OBJ.container.find("a").addClass("unchecked");
						}
						$this.children("a").removeClass("clicked");
						if($this.children("a").hasClass("checked")) {
							$this.find("li").andSelf().children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
							state = 0;
						}
						else {
							   
							    $this.find("li").andSelf().children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");
								state = 1;
						}
						$this.parents("li").each(function () { 
							if(state == 1) {
										if($(this).find("a.unchecked, a.undetermined").size() + 1 > 0) {
											$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
											return false;
										}
										else $(this).children("a").removeClass("unchecked").removeClass("undetermined").addClass("checked");		
								
								
							}
							else {
								if($(this).find("a.checked, a.undetermined").size() - 1 > 0) {
									$(this).parents("li").andSelf().children("a").removeClass("unchecked").removeClass("checked").addClass("undetermined");
									return false;
								}
								else $(this).children("a").removeClass("checked").removeClass("undetermined").addClass("unchecked");
							}
						});
					}
				}
				
			}
		});

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
							<form id="functionsbysystem" name="functionsbysystem" action="${ctx}/sale/sales-module!getFunctions.action">
								<input id="system_id" type="hidden" name="systemId" value=""/>
							</form>
							<div id="content">
							<h3><s:if test="id == null">创建</s:if><s:else>修改</s:else>销售包</h3>
							<form id="inputForm" action="sales-module!save.action" method="post">
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" id="systemId" name="systemId" value="" />
							<input type="hidden" id="ids" name="ids" value=""></input>
							<table class="inputView">
								<tr>
									<td>销售包名称:</td>
									<td><input type="text" id="moduleName" name="moduleName" size="40" value="${moduleName}" /><span id="moduleNameTip" style="width:250px"></span></td>
								</tr>
							
								<tr>
									<td style="color:blue;">系统：</td>
									<td>
										<select id="systemslist" size="1" name="select_system"  onchange="multipleTree()">
										<s:if test="id == null"><option value="0"></option></s:if>
										<s:iterator	value="allSystems">
											<option  value="${id}">${name}</option>
										</s:iterator>
										</select>
									</td>
								</tr>
								<tr><td style="color:blue;">功能列表：</td></tr>
							</table>
							<div  id="functionTree" style=""></div>
							   <input id="functionids" type="hidden" name="functionids" value="${functionids}"/ >
							<div style="position:absolute; left:420px">
							  <p class="buttonP">
						       <input class="btnStyle" type="button" value="提交" onclick="save()"/>&nbsp; 
							   <input class="btnStyle" type="button" value="取消" onclick="history.back()"/>
							  </p> 
							</div> 
							</form>
							</div>
						</div>
					<b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
				</div>
			</div>
    	</div>
    </div>





</body>
</html>