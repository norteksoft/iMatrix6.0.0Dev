<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>数据表</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script type="text/javascript">
	function submitForm(){
		var listViewId = getView();
		if(listViewId==""||listViewId==null){
			alert("请选择列表！");
		}else{
			window.parent.$("#listViewId").attr("value",listViewId);
			window.parent.$("#listViewName").attr("value",$("input[value='"+listViewId+"']").attr("viewname"));
			window.parent.$.colorbox.close();
		}
	}

	function getView(){
		var rds = $("input[name='view']");
		var tacheCode = "";
		for(var i = 0; i < rds.length; i++){
			if($(rds[i]).attr("checked")){
				return $(rds[i]).attr("value");
			}
		}
	}
	</script>
	<style type="text/css">
	.form-table-without-border td input{
		width:200px;
	}
	</style>
</head>
<body onload="getContentHeight();">
	<div class="ui-layout-center">
		<div class="opt-body">
				<aa:zone name="main">
					<div class="opt-btn">
						<button class='btn' onclick="submitForm();"><span><span>确定</span></span></button>
					</div>
					<div style="display: none;" id="message"><s:actionmessage theme="mytheme" /></div>
					<div id="opt-content" class="form-bg">
						<form  id="viewForm" name="viewForm" method="post" action="">
							<table class="form-table-border-left" style="width: 500px;">
								<s:iterator value="listViews" var="listView">
									<tr>
										<td>
											<input type="radio" name="view" value="${id}" viewname="${name }"/>${name}	 
										</td>
									</tr>
								</s:iterator>
							</table>
						</form>
					</div>
				</aa:zone>
			</div>
		</div>
	</body>
</html>
