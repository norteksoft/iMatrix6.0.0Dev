<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/acs-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<title>查看条件</title>
	<%@ include file="/common/acs-iframe-meta.jsp"%>
	<script src="${acsCtx}/js/authority.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			getConditionValue();
		});
		function getConditionValue(){
			var conditionValue="";
			var rows = window.parent.jQuery("#childGridId").jqGrid('getRowData');
			for(var i=0;i<rows.length;i++){
				var row=rows[i];
				var leftBracket=row['leftBracket'];//左括号
				var itemType=row['itemType'];//条件类型
				var operator=row['operator'];//操作符
				var conditionName=row['conditionName'];//条件
				var rightBracket=row['rightBracket'];//右括号
				var joinType=row['joinType'];//连接符
				if(itemType!='' && operator!='' && conditionName!=''){
					if(leftBracket != ''){
						if(leftBracket=='LEFTSINGLE'){//单层括号
							conditionValue+='(';
						}else if(leftBracket=='LEFTDOUBLE'){//双层括号
							conditionValue+='((';
						}
					}
					if(itemType=='USER'){//人员
						conditionValue+='人员&nbsp;';
					}else if(itemType=='DEPARTMENT'){//部门
						conditionValue+='部门&nbsp;';
					}else if(itemType=='ROLE'){//角色
						conditionValue+='角色&nbsp;';
					}else if(itemType=='WORKGROUP'){//工作组
						conditionValue+='工作组&nbsp;';
					}else if(itemType=='CURRENT_USER_SUPERIOR_DEPARTMENT'){//当前用户上级部门
						conditionValue+='当前用户上级部门&nbsp;';
					}else if(itemType=='CURRENT_USER_TOP_DEPARTMENT'){//当前用户顶级部门
						conditionValue+='当前用户顶级部门&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_ID'){//直属上级
						conditionValue+='直属上级&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_DEPARTMENT'){//直属上级的部门
						conditionValue+='直属上级的部门&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_ROLE'){//直属上级的角色
						conditionValue+='直属上级的角色&nbsp;';
					}else if(itemType=='CURRENT_USER_DIRECT_SUPERIOR_WORKGROUP'){//直属上级的工作组
						conditionValue+='直属上级的工作组&nbsp;';
					}else if(itemType=='ALL_USER'){//所有人
						conditionValue+='所有人&nbsp;';
					}
					if(operator=='ET'){//等于
						conditionValue+='等于&nbsp;';
					}else if(operator=='NET'){//不等于
						conditionValue+='不等于&nbsp;';
					}
					conditionValue+=conditionName+'&nbsp;';
					if(rightBracket != ''){
						if(rightBracket=='RIGHTSINGLE'){//单层括号
							conditionValue+=')';
						}else if(rightBracket=='RIGHTDOUBLE'){//双层括号
							conditionValue+='))';
						}
					}
					if(joinType != '' && i<rows.length-1){
						if(joinType=='AND'){//并且
							conditionValue+='&nbsp;并且&nbsp;';
						}else if(joinType=='OR'){//或者
							conditionValue+='&nbsp;或者&nbsp;';
						}
					}
					$("#opt-content").html(conditionValue);
				}
			}
		}
	</script>
</head>
<body>
	<div class="ui-layout-center">
		<div class="opt-body">
			<div id="opt-content">
			</div>
		</div>
	</div>
</body>
</html>
