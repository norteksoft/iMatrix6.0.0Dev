<table class="leadTable">
	<thead>
		<tr>
			<td>名称</td>
			<td>显示顺序</td>
			<td>单价</td>
			<td>编辑时切换事件</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td><a href="#"  > ${report.productName?if_exists}</a></td>
			<td>${report.displayIndex?if_exists}</td>
			<td>${report.price?if_exists}</td>
			<td>${report.country?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>