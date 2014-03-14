<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>租户管理</title>
<%@ include file="/common/meta.jsp"%>
<script type="text/javascript">
function dataMove(){
	if(typeof($("#companyList").val())!="undefined"){
		$("#companyId").attr("value",$("#companyList").val());
	}
	if($("#companyId").val()==""){
		if(confirm("确认移到所有公司的文件吗?")){
			$("#submitBtn").attr("disabled","disabled");
			ajaxSubmit("tenantForm",  "${ctx}/sale/data-move!dataMove.action","tenantZone",showMsg);
		}
	}else{
		$("#submitBtn").attr("disabled","disabled");
		ajaxSubmit("tenantForm",  "${ctx}/sale/data-move!dataMove.action","tenantZone",showMsg);
	}
}

function dataInsert(){
	$("#submitBtn1").attr("disabled","disabled");
	ajaxSubmit("tenantForm",  "${ctx}/sale/data-move!dataInsert.action","tenantZone",showMsg);
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
						<h5>正文附件迁移</h5>
					</div>
					<aa:zone name="tenantZone">
						<form action="" name="tenantForm" id="tenantForm" method="post">
							<input name="companyId" id="companyId" value="${companyId }" type="hidden"/>
						</form>
						<div class="widget-content">
							<s:if test="showFlag">
								<table>
									<tr>
										<td>公司</td><td>
										<select id="companyList">
										<option value="">请选择</option>
										<s:iterator value="companys">
										<option value="${id }">${name }</option>
										</s:iterator></select>
										</td>
									</tr>
								</table>
							</s:if>
						    <p class="buttonP">
                               	<input class="btnStyle" type="button" id="submitBtn" value='迁移' onclick="dataMove();"/>
                               	<!--<input class="btnStyle" type="button" id="submitBtn1" value='导入文件' onclick="dataInsert();"/>  -->
                           	</p>
						    <div id="message" style="color: red;"><s:actionmessage theme="mytheme"/></div>
							<table class="Table changeTR">
		                 	</table>
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
