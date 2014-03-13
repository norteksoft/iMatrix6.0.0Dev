<table class="leadTable">
	<thead>
		<tr>
			<td>订单编号</td>
			<td>顾客</td>
			<td>邮编</td>
			<td>电话</td>
		</tr>
	</thead>
	<tbody>
		<#list page.result as order>
		<tr>
			<td><a href="#"  onclick="popWindow(this,'${order.url?if_exists}');"> ${order.orderNumber?if_exists}</a></td>
			<td>${order.customer?if_exists}</td>
			<td>${order.postCode?if_exists}</td>
			<td>${order.phone?if_exists}</td>
		</tr>
		</#list>
	</tbody>
<table>