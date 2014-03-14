<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ACS 公司管理</title>
<%@ include file="/common/meta.jsp"%>
	<link type="text/css" rel="stylesheet" href="${ctx}/widgets/formValidator/validator.css"></link>
	<script src="${ctx}/widgets/formValidator/formValidator.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/widgets/formValidator/formValidatorRegex.js" type="text/javascript" charset="UTF-8"></script>
<style type="text/css">
	.actionMessage{ list-style-type: none; color: red;}
</style>
<script type="text/javascript">
function changeTab(obj,url){
	select(obj);
	if($(obj).attr("id")=="importA"){
		ajaxSubmit("inputForm",url,"data-zoon",validate);
	}else if($(obj).attr("id")=="initA"){
		ajaxSubmit("inputForm",url,"data-zoon",validateInit);
	}else{
		ajaxSubmit("inputForm",url,"data-zoon");
	}
	
}

function exportData(){
	var ids="";
	var idInputs=$("input[name='bsId']:checked");
	for(var i=0;i<idInputs.length;i++){
		ids=ids+$(idInputs[i]).attr("value")+",";
	}
	$("#systemIds").attr("value",ids);
	ids="";
	idInputs=$("input[name='dataCode']:checked");
	for(var i=0;i<idInputs.length;i++){
		ids=ids+$(idInputs[i]).attr("value")+",";
	}
	$("#dataCodes").attr("value",ids);
	if(typeof($("#companyList").val())!="undefined"){
		$("#companyId").attr("value",$("#companyList").val());
	}
	ajaxSubmit("defaultForm","${ctx}/sale/basic-data!exportData.action");
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
								<h5>导出基础数据</h5>
							</div>
						<div class="widget-content">
								<div id="content">
									<aa:zone name="data-zoon">
									<div id="message"><s:actionmessage theme="mytheme"/></div>
										<form name="defaultForm" id="defaultForm" method="post" action="">
											<input name="systemIds" id="systemIds" type="hidden">
											<input name="dataCodes" id="dataCodes" type="hidden">
											<input name="companyId" id="companyId" value="${companyId }" type="hidden"/>
										</form>
										<p class="buttonP">
			                               	<input class="btnStyle" type="button" value='<s:text name="common.export"/>' onclick="exportData();"/>
			                           	</p>
										<form name="inputForm" id="inputForm" action="" method="post">
											<s:if test="showFlag">
												<table>
													<tr>
														<td>公司</td><td>
														<select id="companyList"><s:iterator value="companys">
														<option value="${id }">${name }</option>
														</s:iterator></select>
														</td>
													</tr>
												</table>
											</s:if>
											<table class="Table changeTR">
												<tr><th>系统</th><th>数据</th></tr>
												<tr>
													<td>
														<table class="Table changeTR">
															<s:iterator value="systems">
															<tr><td style="width: 30px;" align="center"><input name="bsId" value="${id }" type="checkbox"></input></td><td>${name }</td></tr>
															</s:iterator>
														</table>
													</td>
													<td>
														<table class="Table changeTR">
															<s:iterator value="basicDataTypes">
															<tr><td style="width: 30px;" align="center"><input name="dataCode" value="${data }" type="checkbox"></input></td><td>${title }</td></tr>
															</s:iterator>
														</table>
													</td>
												</tr>
											</table>
										</form>
									</aa:zone>
								</div>
						 </div>
						 <b class="xbottom"><b class="xb5"></b><b class="xb4"></b><b class="xb3"></b><b class="xb2"></b><b class="xb1"></b></b>
					  </div>
				   </div>
		        </div>
		   </div>
  </div>
</div>
</body>
</html>


