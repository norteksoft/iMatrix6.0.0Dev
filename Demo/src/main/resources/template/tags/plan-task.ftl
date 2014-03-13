<table class="leadTable">
	<thead>
		<tr>
			<td>任务名称</td>
			<td>任务级别</td>
			<td>任务编号</td>
			<td>责任部门</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as order>
		<tr>
			<td><a href="#"> ${order.taskName?if_exists}</a></td>
			<td>${order.layer?if_exists}</td>
			<td>${order.code?if_exists}</td>
			<td>${order.document?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>