<table class="leadTable">
	<thead>
		<tr>
			<td>采购数量</td>
			<td>显示顺序</td>
			<td>选项组</td>
			<td>键值对</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td><a href="#"  > ${report.sum?if_exists}</a></td>
			<td>${report.displayIndex?if_exists}</td>
			<td>${report.groupName?if_exists}</td>
			<td>${report.keyValue?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>