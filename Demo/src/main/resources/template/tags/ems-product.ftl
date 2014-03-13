<table class="leadTable">
	<thead>
		<tr>
			<td>产品编号</td>
			<td>产品名称</td>
			<td>产品价格</td>
			<td>采购数量</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as report>
		<tr>
			<td><a href="#"  > ${report.productNumber?if_exists}</a></td>
			<td>${report.productName?if_exists}</td>
			<td>${report.price?if_exists}</td>
			<td>${report.amount?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>