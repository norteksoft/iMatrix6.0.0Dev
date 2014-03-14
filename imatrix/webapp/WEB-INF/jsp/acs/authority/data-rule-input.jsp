<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>数据规则</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
</head>
<body>
	<aa:zone name="main_zone">
		<div class="opt-btn">
			<button class="btn" onclick="saveDataRule('${acsCtx}/authority/data-rule-save.htm');"><span><span>保存</span></span></button>
			<button class="btn" onclick='setPageState();ajaxSubmit("defaultForm","${acsCtx}/authority/data-rule.htm","main_zone");'><span><span >返回</span></span></button>
		</div>
		<div id="opt-content">
			<div id="message" style="display:none;"><s:actionmessage theme="mytheme" /></div>
			<form id="saveForm" name="saveForm" action="" method="post">
				<input type="hidden" name="dataRuleId" id="dataRuleId" value="${dataRuleId}"/>
				<input type="hidden" id="menuId" name="sysMenuId" value="${sysMenuId }"/>
				<input type="hidden" id="deparmentInheritable" name="deparmentInheritable"/>
				<table class="form-table-without-border" style="width: auto;">
					<tr>
						<td class="content-title">编&nbsp;&nbsp;&nbsp;号：</td>
						<td><input type="text" id="code" name="code" maxlength="255" value="${code }" style="width:400px;" onblur="codeRule();"/>
						<span class="required">*</span></td>
					</tr>
					<tr>
						<td class="content-title">名&nbsp;&nbsp;&nbsp;称：</td>
						<td><input type="text" id="name" name="name" maxlength="60" value="${name }" style="width:400px;"/>
						<span class="required">*</span></td>
					</tr>
					<tr>
						<td  class="content-title">数据表：</td>
						<td ><input type="text" id="dataTableName" name="dataTableName"readonly="readonly" style="width:400px;" value="${dataTableName }"/>
							<span class="required">*</span>
							<input type="hidden" id="dataTableId" name="dataTableId" value="${dataTableId }"/>
							<a href="#"  class="small-btn" onclick="selectDataTable('dataRule');"><span id="choose-user"><span>选择</span></span></a>
						</td>
					</tr>
					<tr>
						<td class="content-title">备&nbsp;&nbsp;&nbsp;注：</td>
						<td>
						<textarea id="remark" onkeyup="if(this.value.length>60)this.value=this.value.substring(0,60);" cols="50" rows="2" name="remark">${remark }</textarea>
						</td>
					</tr>
					<tr>
						<td class="content-title">设置类型：</td>
						<td>
							<input type="radio" name="simplable" value="true" <s:if test="simplable">checked=checked</s:if> onclick="settingType();" id="simple"></input>简易设置&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="radio" name="simplable" value="false" <s:if test="!simplable">checked=checked</s:if> onclick="settingType();" id="advanced"></input>高级设置
						</td>
					</tr>
					<tr >
						<td class="content-title listTd" >数据范围：</td>
						<td class="listTd" >
							<ul style="list-style: none;margin-top: 0px; padding-left: 8px;">
								<s:iterator value="@com.norteksoft.acs.base.enumeration.DataRange@values()"  id="range">
								   <s:if test="#range.code == 'data.range.current.department'">
									   <s:if test='#range == dataRange'>
										    <li><div style="width:280px"><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();" id="dept"></input><s:text name="%{#range.code}"></s:text>&nbsp;&nbsp;<span style="display: none;float:right;" id="deptImp"><input  type="checkbox" id="deptInheri" <s:if test="deparmentInheritable">checked="checked"</s:if> ></input><s:text name="data.range.current.department.inheritable"></s:text></span></div></li>
									   </s:if><s:else>
										    <li><div style="width:280px"><input name="dataRange" type="radio" value="${range}" code="${range.code }" onclick="showImp();" id="dept"></input><s:text name="%{#range.code}"></s:text>&nbsp;&nbsp;<span style="display: none;float:right;" id="deptImp"><input  type="checkbox" id="deptInheri"  <s:if test="deparmentInheritable">checked="checked"</s:if> > </input><s:text name="data.range.current.department.inheritable"></s:text></span></div></li>
									   </s:else>
								   </s:if><s:else>
									   <s:if test='#range == dataRange'>
											<li><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
									   </s:if><s:else>
										   <s:if test="#range.code == 'data.range.myself'">
												<li><input name="dataRange" type="radio" value="${range}" checked="checked" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
										   </s:if><s:else>
												<li><input name="dataRange" type="radio" value="${range}" code="${range.code }" onclick="showImp();"><s:text name="%{#range.code}"></s:text></li>
										   </s:else>
									   </s:else>
								   </s:else>
								</s:iterator>
							</ul>
						</td>
					</tr>
				</table>
			</form>	
			<div  id="advancedGrid">
				&nbsp;规则条件：<br/><br/>
				<view:formGrid gridId="conditionGrid" code="ACS_CONDITION" entity="${dataRule}" attributeName="conditions"></view:formGrid>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
				settingType();
				showImp();
			});
		</script>
	</aa:zone>
</body>
</html>