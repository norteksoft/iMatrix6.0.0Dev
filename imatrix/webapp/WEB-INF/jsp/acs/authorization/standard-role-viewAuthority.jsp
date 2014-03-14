<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/common/acs-taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
   
    <%@ include file="/common/acs-iframe-meta.jsp"%>
	
	<style type="text/css">
		/*  block型表格样式  */
		.TableBlock{
		   border:1px #cccccc solid;
		   line-height:20px;
		   font-size:9pt;
		   border-collapse:collapse;
		}
		.TableBlock .TableHeader td,
		.TableBlock td.TableHeader{
		   height:30px !important;
		   height:32px;
		   background:#C3E8FB;
		   border:1px #9cb269 solid;
		   font-weight:bold;
		   color:#383838;
		   line-height:23px;
		   padding:0px;
		   padding-left:5px;
		}
		.TableBlock .TableHeader td.TableCorner{
		   background:#99c729;
		   color:#fff;
		   font-weight:bold;
		}
		.TableBlock .TableLine1 td,
		.TableBlock td.TableLine1{
		   background:#FFFFFF;
		   border-bottom:1px #cccccc solid;
		   border-right:1px #cccccc solid;
		   padding:3px;
		   height:30px;
		}
		.TableBlock .TableLine2 td,
		.TableBlock td.TableLine2{
		   background:#FFFFFF;
		   border-bottom:1px #cccccc solid;
		   border-right:1px #cccccc solid;
		   padding:3px;
		   height:30px;
		}
		.TableBlock .TableData td,
		.TableBlock td.TableData{
		   background:#FFFFFF;
		   border-bottom:1px #cccccc solid;
		   border-right:1px #cccccc solid;
		   padding:3px;
		   height:30px;
		}
		.TableBlock .TableContent td,
		.TableBlock td.TableContent{
		   background:#f2f2f2;
		   border-bottom:1px #cccccc solid;
		   border-right:1px #cccccc solid;
		   padding:3px;
		   height:30px;
		}
		.TableBlock .TableFooter td,
		.TableBlock .TableControl td,
		.TableBlock td.TableFooter,
		.TableBlock td.TableControl{
		   background: #FFFFFF;
		   border:1px #cccccc solid;
		   padding:3px;
		   height:30px;
		}
		.TableBlock .TableRed td,
		.TableBlock td.TableRed
		{
		   background:#edf6db;
		   padding:3px;
		   height:30px;
		}
		
		.TableBlock .TableLeft td,
		.TableBlock td.TableLeft{
		   background:#c4de83;
		   border-bottom:1px #a7bd74 solid;
		   padding:3px;
		   height:30px;
		}
	</style>
</head>

<body>
<div class="ui-layout-center">
	<div class="opt-body">
		<div class="cbox-btn">
			<div style="height: 6px;"></div>
			<span style="padding-top: 20px;">角色：${entity.name }</span>
		</div>
		<div id="opt-content" style="height: 420px;  overflow: auto;">
			<table class="" cellspacing="2" cellpadding="3" border="0" >
				<tbody>
					<tr class="TableContent">
						<td valign="top">
							<table class="TableBlock" align="center" style="">
								<tbody>
									<tr class="TableHeader" >
										<td><label ><b>资源名称</b></label></td>
										<td><label ><b>资源编号</b></label></td>
										<td><label ><b>资源路径</b></label></td>
									</tr>
									<s:iterator value="functions" id="function">
									<tr>
										<td class="TableData"><label>${function.name }</label></td>
										<td class="TableData"><label>${function.code }</label></td>
										<td class="TableData"><label>${function.path }</label></td>
									</tr>
									</s:iterator>
								</tbody>
							</table>
						</td>
					</tr>			
				</tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>
	