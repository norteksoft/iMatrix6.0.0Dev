<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>报销单</title>
		<%@include file="/common/iframe-meta.jsp" %>	
		<script type="text/javascript">
			function submitForm(){
				var amount=$("#amount").val();
				if(amount==''){
					alert("请填写数量！");
				}else{
					var currentInputId="${currentInputId }";
					parent.$("#"+currentInputId).attr("value",amount);
					window.parent.$.colorbox.close();
				}
			}
		</script>
	</head>
	
	<body onload="getContentHeight();">
		<div class="ui-layout-center">
		<div class="opt-body">
			<div class="opt-btn">
				<button class='btn' onclick="submitForm()"><span><span>确定</span></span></button>
			</div>
			<div id="opt-content" class="form-bg">
				<form  id="expenseReportForm" name="expenseReportForm" method="post" action="">
					<table>
						<tr>
							<td>数量</td>
							<td><input id="amount" name="amount" value="" class="{required:true,number:true, messages:{number:'请输入数字'}}"/> </td>
						</tr>
						</table>
				</form>
			</div>
		</div>
		</div>
	</body>
</html>