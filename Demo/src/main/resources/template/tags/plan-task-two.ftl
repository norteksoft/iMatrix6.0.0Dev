<table class="leadTable">
	<thead>
		<tr>
			<td>负责人</td>
			<td>任务状态</td>
			<td>显示顺序</td>
			<td>计划支出</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as order>
		<tr>
			<td><a href="#"> ${order.responseMan?if_exists}</a></td>
			<td>${order.status?if_exists}</td>
			<td>${order.displayOrder?if_exists}</td>
			<td>${order.money?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>