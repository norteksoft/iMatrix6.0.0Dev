<table class="leadTable">
	<thead>
		<tr>
			<td>一级审批人</td>
			<td>二级审批人</td>
			<td>三级审批人</td>
			<td>部门</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td><a href="#"  > ${report.firstApprover?if_exists}</a></td>
			<td>${report.secondApprover?if_exists}</td>
			<td>${report.thirdApprover?if_exists}</td>
			<td>${report.department?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>