<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>费用管理</title>
</head>
<body>
	<aa:zone name="main">
		<div class="opt-btn">
			<button class='btn' onclick="iMatrix.showSearchDIV(this);"><span><span>查询</span></span></button>
			<button class='btn' onclick="iMatrix.export_Data('${ctx}/loan-bill/task-export.htm');"><span><span>导出</span></span></button>
			<button class='btn' onclick="importData('${ctx}/loan-bill/task-import.htm');"><span><span>导入(无回调)</span></span></button>
			<button class='btn' onclick="insertData('${ctx}/loan-bill/insert-data.htm');"><span><span>插入</span></span></button>
		</div>
		<div id="opt-content" >
			<form  id="contentForm" name="contentForm" method="post" action="">
				<grid:jqGrid gridId="planTaskList" url="${ctx}/loan-bill/task-list-data.htm"  code="ES_PLAN_TASK" ></grid:jqGrid>
			</form>
		</div>
	</aa:zone>
	
</body>
</html>