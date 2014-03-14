<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div id="col1" style="width: 150px;">
  	<div id="accordion">
		<h3><a href="/sales/sale/tenant.action">租户管理</a></h3>
		<div>
		
		</div>	
		<h3><a href="/sales/sale/product.action">产品管理</a></h3>
		<div>
			
		</div>
		<!--<h3><a href="/sales/sale/sales-module.action">销售包管理</a></h3>
		<div>
			
		</div>
		--><h3><a href="/sales/sale/business-system.action">业务系统管理</a></h3>
		<div>
			
		</div>
		<h3><a href="/sales/sale/basic-data.action">导出导入数据</a></h3>
		<div style="width:auto;">
			<div style="height:5px;"></div>
			<div class="leftCol">
					<a href="/sales/sale/basic-data.action">导出基础数据</a>
			</div>
			<div  class="leftCol">
					<a href="/sales/sale/basic-data!showImportData.action">初始化系统</a>
			</div>
			<div  class="leftCol">
					<a href="/sales/sale/basic-data!showInitData.action">初始化租户</a>
			</div>
		</div>
		<h3><a href="/sales/sale/data-move.action">正文附件迁移</a></h3>
		<div>
			
		</div>
	</div>
</div>

<script>
    $(function() {
    	$("#accordion").accordion({
    		fillSpace: true,
    		active: getIndex('#accordion'),
    		change: function(event, ui) {
    			location.href=$($(ui.newHeader[0]).children()[1]).attr('href').split(',')[0];
    		}
    	});
    });
    function getIndex(id){
    	var strs = location.href.replace('#','').split('/');
    	var href0 = strs[strs.length-1];
    	if(href0.indexOf("!")>=0){
    		href0=href0.substring(href0.indexOf("/")+1,href0.indexOf("!"));
    	}else{
	    	href0=href0.substring(href0.indexOf("/")+1,href0.indexOf("."));
    	}
    	if(href0=="function"||href0=="function-group"||href0=="standard-role"){//如果是资源、资源组、角色使三级菜单【业务系统管理】选中
    		return 2;
		}
    	var subs = $(id).children("h3");
    	for(var i = 0; i < subs.length; i++){
    		var hs0 = $($(subs[i]).children('a')[0]).attr('href').split(',');
    		for(var l = 0; l < hs0.length; l++){
        		
    			if(hs0[l].indexOf(href0) != -1){
    				return i;
    			}
    		}
    	}
    	return 0;
    }
</script>
</div>