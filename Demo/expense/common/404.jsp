<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<TITLE>404 - 页面不存在</TITLE>
<META http-equiv=Content-Type content="text/html; charset=urf-8">
<STYLE type=text/css>
A:link {
	COLOR: #555555; TEXT-DECORATION: none
}
A:visited {
	COLOR: #555555; TEXT-DECORATION: none
}
A:active {
	COLOR: #555555; TEXT-DECORATION: none
}
A:hover {
	COLOR: #6f9822; TEXT-DECORATION: none
}
.text {
	FONT-SIZE: 12px; COLOR: #555555; FONT-FAMILY: ""; TEXT-DECORATION: none
}
.STYLE1 {font-size: 13px}
.STYLE2 {font-size: 12px}
.STYLE3 {font-size: 11px}
</STYLE>

</head>
<BODY>
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%"
	align=center border=0>

	<TR>
		<TD vAlign="center" align="middle">
		<TABLE cellSpacing=0 cellPadding=0 width=500 align=center border=0>

			<TR>
				<TD width=17 height=17><IMG height=17 src="${ctx}/images/co_01.gif"
					width=17></TD>
				<TD width=316 background="${ctx}/images/bg01.gif"></TD>
				<TD width=17 height=17><IMG height=17 src="${ctx}/images/co_02.gif"
					width=17></TD>
			</TR>
			<TR>
				<TD background=${ctx}/images/bg02.gif></TD>
				<TD>
				<TABLE class=text cellSpacing=0 cellPadding=10 width="100%"
					align=center border=0>
					<TR>
						<TD>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TR>
								<TD width=20></TD>
								<TD><IMG height=50 src="${ctx}/images/404.jpg" width=400></TD>
							</TR>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TD>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TR>
								<TD background="${ctx}/images/dot_01.gif" height=1></TD>
							</TR>
							</TBODY>
						</TABLE>
						<BR>
						<TABLE class=text cellSpacing=0 cellPadding=0 width="100%"
							border=0>
							<TR>
								<TD width=20></TD>
								<TD>
								<P><STRONG><FONT color=#ba1c1c>HTTP404错误</FONT></STRONG><BR><BR>
								没有找到您要访问的页面，请检查您是否输入正确URL。
								<div><a href="<c:url value="/"/>">返回首页</a></div>
								</TD>
							</TR>
						</TABLE>
						</TD>
					</TR>
				</TABLE>
				</TD>
				<TD background="${ctx}/images/bg03.gif"></TD>
			</TR>
			<TR>
				<TD width=17 height=17><IMG height=17 src="${ctx}/images/co_03.gif"
					width=17></TD>
				<TD background="${ctx}/images/bg04.gif" height=17></TD>
				<TD width=17 height=17><IMG height=17 src="${ctx}/images/co_04.gif"
					width=17></TD>
			</TR>
		</TABLE>
		<TABLE class=text cellSpacing=0 cellPadding=0 width=500 align=center
			border=0>
			<TR>
				<TD></TD>
			</TR>
			<TR>
				<TD align="middle"></TD>
			</TR>
		</TABLE>
		</TD>
	</TR>
	</TBODY>
</TABLE>
</BODY>
</html>