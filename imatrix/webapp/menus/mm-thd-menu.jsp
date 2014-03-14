<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/mms-taglibs.jsp"%>

<div id="accordion" >
	<h3><a href="${mmCtx}/mm/monitor-infor.htm" id="_mm_infor">信息监控</a></h3>
	<div>
		<div class="demo" id="dynamic-menu" style="margin-top: 10px;"></div>
	</div>
	<h3><a href="${mmCtx}/mm/monitor-parmeter.htm" id="_mm_parmeter">参数设置</a></h3>
	<div>
	</div>
</div>

<script type="text/javascript">
	$(function () {
		var url=window.location.href;
		if(url.indexOf("/mm/monitor-infor.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 0});
			mm_folderTreeMenu();
		}else if(url.indexOf("/mm/monitor-parmeter.htm")>=0){
			$("#accordion").accordion({fillSpace:true, change:accordionChange,active: 1});
		}
	});
	function accordionChange(event,ui){
		var url=ui.newHeader.children("a").attr("href");
		window.location = url;
	}

	/**
	 * 加载动态树
	 */
	function mm_folderTreeMenu(){
		$("#dynamic-menu").jstree({
				"json_data":{
					"ajax" : { "url" : "${mmCtx}/mm/monitor-infor-tree.htm",
								"data" : function (n) {  
								 return { parentFolderId : n!=-1 ? n.attr("id") : -1};   
							}
						}
		   },
		   "themes" : {
				 "theme" : "default",  
				 "dots" : true,  
				 "icons" : true 
			},  
			"ui" : {    
				"initially_select" : [ "loadbill-menu" ]  
			},  
	   		"plugins" : [ "themes", "json_data","ui" ]
		 }).bind("select_node.jstree",function(e){
			 var id = $("#dynamic-menu").find(".jstree-clicked").parent().attr("id");
			 mm_treeback(id);
			});
	    }
		
	function mm_treeback(id){
			if(id!="root"){
				var ids=id.split("_");
				ajaxAnyWhereSubmit('defaultForm',webRoot+'/mm/monitor-infor-getList.htm?systemCode='+ids[0]+"&jwebType="+ids[1],'form_main',getContentHeight);
			}
	}
</script>

