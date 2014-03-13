<table class="leadTable">
	<thead>
		<tr>
			<td>计划名称</td>
			<td>计划编码</td>
			<td>计划数目</td>
			<td>计划金额</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as order>
		<tr>
			<td><a href="#"> ${order.name?if_exists}</a></td>
			<td>${order.code?if_exists}</td>
			<td>${order.amount?if_exists}</td>
			<td>${order.money?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>