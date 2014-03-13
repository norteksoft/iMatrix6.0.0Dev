<table class="leadTable">
	<thead>
		<tr>
			<td>文件夹名称</td>
			<td>父文件夹名称</td>
			<td>文件夹创建者名称</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td>${report.name?if_exists}</td>
			<td>${report.parentName?if_exists}</td>
			<td>${report.creatorName?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>