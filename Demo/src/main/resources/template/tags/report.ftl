<table class="leadTable">
	<thead>
		<tr>
			<td>报销人</td>
			<td>部门</td>
			<td>发票金额</td>
			<td>金额</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td><a href="#"  > ${report.name}</a></td>
			<td>${report.department}</td>
			<td>${report.invoiceAmount?string('###0')}</td>
			<td>${report.money?string('###0')}</td>
		</tr>
		</#list>
	</tbody>
<table>