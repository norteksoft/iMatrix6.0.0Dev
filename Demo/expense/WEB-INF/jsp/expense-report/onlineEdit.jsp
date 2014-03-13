<%@ page contentType="text/html; charset=utf-8" %>
<%@page import="com.norteksoft.product.util.SystemUrls"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title>文档预览</title>
<script language="javascript" type="text/javascript">
	var webRoot="${ctx}";
	var imatrixRoot="${imatrixCtx}";
</script>
<script type="text/javascript" src="${resourcesCtx}/js/jquery-all-1.0.js"></script>
	
<script language="javascript" for=WebOffice event="OnMenuClick(vIndex,vCaption)">

   if (vIndex==1){  //打开本地文件
      WebOpenLocal();
   }
   if (vIndex==2){  //保存本地文件
      WebSaveLocal();
   }
   if (vIndex==3){  //保存到服务器上
	saveDocument();    //保存正文
   }
   if (vIndex==5){  //签名印章
      WebOpenSignature();
   }
   if (vIndex==6){  //验证签章
      WebShowSignature();
   }
   if (vIndex==8){  //保存版本
      WebSaveVersion();
   }
   if (vIndex==9){  //打开版本
      WebOpenVersion();
   }
   if (vIndex==11){  //测试菜单一
     alert('菜单编号:'+vIndex+'\n\r'+'菜单条目:'+vCaption+'\n\r'+'请根据这些信息编写菜单具体功能');
   }
   if (vIndex==12){  //测试菜单二
     alert('菜单编号:'+vIndex+'\n\r'+'菜单条目:'+vCaption+'\n\r'+'请根据这些信息编写菜单具体功能');
   }
   if (vIndex==14){  //保存并退出
     SaveDocument();    //保存正文
     webform.submit();
   }

</script>
<script type="text/javascript">
//作用：保存文档
function saveDocument(){
  //webform.WebOffice.WebSetMsgByName("MyDefine1","自定义变量值1");  //设置变量MyDefine1="自定义变量值1"，变量可以设置多个  在WebSave()时，一起提交到OfficeServer中
  webform.WebOffice.WebSetMsgByName("id","${id}");
  webform.WebOffice.WebSetMsgByName("ISITEM","2");//是否是从项目空间中打开的
  if (!webform.WebOffice.WebSave(true)){ //交互OfficeServer的OPTION="SAVEFILE"  注：WebSave()是保存复合格式文件，包括OFFICE内容和手写批注文档；如只保存成OFFICE文档格式，那么就设WebSave(true)
    StatusMsg(webform.WebOffice.Status);
    return false;
  }else{
    StatusMsg(webform.WebOffice.Status);
    return true;
  }
}
</script>
</head>
<body> 
		<s:if test="#docSuffix == null">
			<s:set var="docSuffix" value="expenseReport.suffix"></s:set>
		</s:if>
		<s:if test='".doc".equals(#docSuffix) || ".xls".equals(#docSuffix) || ".wps".equals(#docSuffix) || ".et".equals(#docSuffix) || ".docx".equals(#docSuffix) || ".xlsx".equals(#docSuffix)'>
			<form id="webform" name="webform" method="post" action=""  >
			    <input type=hidden name=StatusBar readonly class="IptStyleBlack" style="WIDTH:75%">
				
				 <OBJECT id="WebOffice" width="100%" height="100%" 
					classid="clsid:23739A7E-5741-4D1C-88D5-D50B18F7C347" 
					codebase="${ctx}/widgets/iWebOffice/iWebOffice2003.ocx#version=8,2,0,0" >
				</OBJECT>
			<script type="text/javascript">
			function StatusMsg(mString){
				webform.StatusBar.value=mString;
			}
			$(document).ready(function(){
				try{
					//以下属性必须设置，实始化iWebOffice
					webform.WebOffice.WebUrl="${ctx}/WebOffice";
					webform.WebOffice.MaxFileSize = 4 * 1024;
					webform.WebOffice.Language="CH";
					if('${expenseReport.id}' == ""){
						webform.WebOffice.RecordID="${id}";
						webform.WebOffice.FileName="${documentName}"+"${suffix}";
						webform.WebOffice.FileType="${suffix}";
						webform.WebOffice.UserName="${creator}";
						webform.WebOffice.EditType="0";
						webform.WebOffice.ShowToolBar=0;    //  0|2
						webform.WebOffice.ShowMenu="0";
					}else{
						webform.WebOffice.RecordID="${expenseReport.id}";
						webform.WebOffice.FileName="${expenseReport.documentName}"+"${expenseReport.suffix}";
						webform.WebOffice.FileType="${expenseReport.suffix}";
						webform.WebOffice.UserName="${expenseReport.creator}";
						webform.WebOffice.EditType="1";
						webform.WebOffice.ShowToolBar=0;    //  0|2
						
					    webform.WebOffice.ShowWindow = true;                  //控制显示打开或保存文档的进度窗口，默认不显示
	
					    if($("#printSetting").attr("value")=='true'){
						    webform.WebOffice.Print="1";	//允许打印
					    }else{
					    	webform.WebOffice.Print="0";	//不允许打印
					    }
					    	webform.WebOffice.ShowMenu="1";
					        webform.WebOffice.AppendMenu("1","打开本地文件(&L)");
					        webform.WebOffice.AppendMenu("2","保存本地文件(&S)");	
					        webform.WebOffice.AppendMenu("3","保存到服务器(&U)");	
					        webform.WebOffice.AppendMenu("4","-");	
					        webform.WebOffice.AppendMenu("5","显示痕迹");	
					        webform.WebOffice.AppendMenu("6","隐藏痕迹");
					        webform.WebOffice.AppendMenu("7","-");	
					        webform.WebOffice.AppendMenu("8","关闭窗口");
					        webform.WebOffice.DisableMenu("宏(&M);选项(&O)...");    //禁止菜单
					        webform.WebOffice.DisableMenu("关于我们(&A)...");
					        webform.WebOffice.EditType=$("#editType").attr("value");
					        if($("#editType").attr("value").split(',')[1]=='0'){
						    	webform.WebOffice.EnableMenu("保存到服务器(&U)");    //禁止菜单
						    }else{
						    	webform.WebOffice.DisableMenu("保存到服务器(&U)");    //禁止菜单
							 }
					        if($("#fileType").attr("value")==".doc"&&$("#editType").attr("value").split(',')[2]=='1'){
					        	 webform.WebOffice.DisableMenu("显示痕迹");    //禁止菜单
					        }else{
					        	 webform.WebOffice.DisableMenu("显示痕迹");    //禁止菜单
					        	 webform.WebOffice.DisableMenu("隐藏痕迹");    //禁止菜单
					        }       
					        if($("#ifDownLoad").attr("value")=='true'){
					        	webform.WebOffice.EnableMenu("保存本地文件ererere(&S)");    //禁止菜单
					        }else{
					        	 webform.WebOffice.DisableMenu("保存本地文件(&S)");    //禁止菜单
					        }
						
						webform.WebOffice.WebSetMsgByName("ISTEMP","0");
						webform.WebOffice.WebSetMsgByName("ISITEM","2");//是否是从项目空间中打开的
					}
					
					//webform.WebOffice.ShowWindow = true; //控制显示打开或保存文档的进度窗口，默认不显示
					
					
					//webform.WebOffice.CopyType="0"; // 0|1  //会影响当前word使用
					
					webform.WebOffice.WebOpen(); 
					StatusMsg(webform.WebOffice.Status); 
				} catch(e){
					alert(e.description); 
				}
			});
			</script>
		</form>
	</s:if><s:elseif test='".pdf".equals(#docSuffix)'>
		<form name="webform" method="post" >
		 <OBJECT id="WebPDF" width="100%" height="100%" classid="clsid:39E08D82-C8AC-4934-BE07-F6E816FD47A1" 
			codebase="${ctx}/widgets/iWebOffice/iWebPDF.cab#version=7.2.0.246" VIEWASTEXT>
		</OBJECT>
		<script type="text/javascript">
		function StatusMsg(mString){
			window.status=mString;
		}
		$(document).ready(function(){
			try{
			    //以下属性必须设置，实始化iWebPDF
			    webform.WebPDF.WebUrl="<%=SystemUrls.getSystemUrl("ems")%>" + "/WebPdf";    //WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档 
			    webform.WebPDF.RecordID="${expenseReport.id}";
				webform.WebPDF.FileName="${expenseReport.documentName}"+"${expenseReport.suffix}";
				webform.WebPDF.FileType="${expenseReport.suffix}";
				webform.WebPDF.UserName="${expenseReport.creator}";

			    webform.WebPDF.ShowTools = 0;               //工具栏可见（1,可见；0,不可见）
			    webform.WebPDF.EnableTools("关闭文档", 0);
			    webform.WebPDF.EnableTools("添加水印;批量验证", 0);
			    if($("#ifDownLoad").attr("value")=='true'){
			    	webform.WebPDF.EnableTools("另存为", 1);             //是否允许保存当前文档（1,允许；0,不允许）
			    }else{
			    	webform.WebPDF.EnableTools("另存为", 0);              //是否允许保存当前文档（1,允许；0,不允许）
			    }
			    
			    if($("#printSetting").attr("value")=='true'){
			    	 webform.WebPDF.PrintRight = 1;              //是否允许打印当前文档（1,允许；0,不允许）
			    }else{
			    	 webform.WebPDF.PrintRight = 0;              //是否允许打印当前文档（1,允许；0,不允许）
			    }
			   
			    webform.WebPDF.AlterUser = false;           //是否允许由控件弹出提示框 true表示允许  false表示不允许
			  
			    webform.WebPDF.ShowBookMark = 1;			//是否显示书签树按钮（1,显示；0,不显示）
			    webform.WebPDF.ShowSigns = 0;         	    //设置签章工具栏当前是否可见（1,可见；0,不可见）
			    webform.WebPDF.SideWidth = 100;             //设置侧边栏的宽度
			   
				webform.WebPDF.WebOpen();                   //打开该文档    交互OfficeServer的OPTION="LOADFILE"    <参考技术文档>
			    
			    StatusMsg(webform.WebPDF.Status);           //状态信息
			    
			    webform.WebPDF.Zoom = 100;                  //缩放比例
			    webform.WebPDF.Rotate = 360;                //当显示页释放角度
			    webform.WebPDF.CurPage = 1;                 //当前显示的页码
			    
			   
			  }catch(e){
			    alert(e.description);                       //显示出错误信息
			  }
		});
		</script>
		</form>
	</s:elseif>
	<input type="hidden" id="companyId" name="companyId" value="${companyId }"/>
    <input type="hidden" id="ifDownLoad" name="ifDownLoad" value="${taskPermission.documentDownloadable }"/>
    <input type="hidden" id="fileType" name="fileType" value="${expenseReport.suffix}">
    <input type="hidden" id="editType" name="editType" value="${editType }"/>
    <input type="hidden" id="taskId" name="taskId" value="${taskId }"/>
</body>
</html>