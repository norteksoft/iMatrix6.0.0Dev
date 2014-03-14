<%@ page contentType="text/html;charset=UTF-8" import="java.util.*"%>
<%@ include file="/common/setting-taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
	<%@ include file="/common/setting-iframe-meta.jsp"%>
	<script type="text/javascript" src="${resourcesCtx}/widgets/validation/validate-all-1.0.js"></script>
	<script type="text/javascript" src="${resourcesCtx}/widgets/multiselect/jquery.multiselect.min.js"></script>
	<link   type="text/css" rel="stylesheet" href="${resourcesCtx}/widgets/multiselect/jquery.multiselect.css" />
	
	<!-- 树 -->
	<script type="text/javascript" src="${resourcesCtx}/js/staff-tree.js"></script>
	<script type="text/javascript" src="${ctx}/js/item.js"></script>
	
	<title>定时设置</title>
	<script type="text/javascript">
	$(document).ready(function(){
		validateTimer();
		checkBoxSelect('everyWeek');
		timeFormat('everyDate');
		dateFormat('appointTime');
	});
	function checkBoxSelect(id){
		$("#"+id).multiselect({
		 	multiple: true,
		  /*header: "Select an option",
		   noneSelectedText: "Select an Option",*/
		   header: true,
		   selectedList: 1
		});
	}

	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function timeFormat(id){
		$('#'+id).timepicker({
			timeOnlyTitle: '时间',
			beforeShow:function(input, inst){
				if($("#"+id).attr("value")==""||typeof ($("#"+id).attr("value"))=='undefined'){
					$("#"+id).attr("value","00:00");
				}
			}
		});
	}


	/**
	 * 日期初始化
	 * @param id
	 * @return
	 */
	function dateFormat(id){
		$('#'+id).datetimepicker({
			"dateFormat":'yy-mm-dd',
			changeMonth:true,
			changeYear:true
		});
	}

	function typeChange(value){
		clearForm();
		if(value=="everyDate"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="everyWeek"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").show();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="everyMonth"){
			$("#tr_everyDate").show();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").show();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_appointSet_discription").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
		}else if(value=="appointTime"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").show();
			$("#tr_appointSet").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet_discription").hide();
		}else if(value=="appointSet"){
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_intervalTime_type").hide();
			$("#tr_intervalTime_hour").hide();
			$("#tr_intervalTime_second").hide();
			$("#tr_appointSet").show();
			$("#tr_appointSet_discription").show();
		}else if(value=="intervalTime"){//时间间隔
			$("#tr_everyDate").hide();
			$("#tr_everyWeek").hide();
			$("#tr_everyMonth").hide();
			$("#tr_appointTime").hide();
			$("#tr_appointSet").hide();
			$("#tr_appointSet_discription").hide();
			$("#tr_intervalTime_type").show();
		}
	}

	function clearForm(){
     $("#everyDate").attr("value","");
     $("#appointTime").attr("value","");
     $("#appointSet").attr("value","");
     $("#everySecond").attr("value","");
     $("#everyHour").attr("value","");
     $("#everyWeek").attr("value","");
     $("#everyMonth").attr("value","");
    }

	function intervalTypeChange(type){
          if(type=='secondType'){
        	$("#tr_intervalTime_second").show();
  			$("#tr_intervalTime_hour").hide();
          }else if(type=='hourType'){
        	  $("#tr_intervalTime_second").hide();
    		  $("#tr_intervalTime_hour").show();
          }
    }

	//提交
	function submitJobInfo(){
		$("#jobInfoFrom").attr("action",'${settingCtx}/options/job-info-save.htm');
		$("#jobInfoFrom").submit();
	}

		$.validator.addMethod("customRequired", function(value, element) {
			var $element = $(element);
			if($element.val()!=null&&$element.val()!=''&&typeof ($element.val())!='undefined'){
				return true;
			}
			
			if($("#typeEnum").val()=='everyDate'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyMonth'){
				if(($("#everyMonth").val()!=null&&$("#everyMonth").val()!=''&&typeof ($("#everyMonth").val())!='undefined')
						&&($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined')){
					return true;
				}
			}
			if($("#typeEnum").val()=='everyWeek'){
				if($("#everyDate").val()!=null&&$("#everyDate").val()!=''&&typeof ($("#everyDate").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointTime'){
				if($("#appointTime").val()!=null&&$("#appointTime").val()!=''&&typeof ($("#appointTime").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='appointSet'){
				if($("#appointSet").val()!=null&&$("#appointSet").val()!=''&&typeof ($("#appointSet").val())!='undefined'){
					return true;
				}
			}
			if($("#typeEnum").val()=='intervalTime'){
				if(($("#everySecond").val()!=null&&$("#everySecond").val()!=''&&typeof ($("#everySecond").val())!='undefined')
						||($("#everyHour").val()!=null&&$("#everyHour").val()!=''&&typeof ($("#everyHour").val())!='undefined')){
					return true;
				}
			}
		}, "必填");

		$.validator.addMethod("intervalTimeValidate", function(value, element) {
			if($("#typeEnum").val()=='intervalTime'){
				if(($("#everySecond").val()!=null&&$("#everySecond").val()!=''&&typeof ($("#everySecond").val())!='undefined'&&$("#everySecond").val().indexOf(0)!=0)
						||($("#everyHour").val()!=null&&$("#everyHour").val()!=''&&typeof ($("#everyHour").val())!='undefined'&&$("#everyHour").val().indexOf(0)!=0)){
					return true;
				}
			}else{
                    return true; 
			}
		}, "你输入不合法");
		
	function validateTimer(){
		$("#jobInfoFrom").validate({
			submitHandler: function() {
				if($("#typeEnum").val()=='everyWeek'){//每周时
					if($("#everyWeek").val()==null||$("#everyWeek").val()==''||typeof ($("#everyWeek").val())=='undefined'){
						$("#tr_everyWeek td").append('<label  class="error">必填</label>');
					}else{
						$("#tr_everyWeek td label").remove();
						$("#jobInfoFrom").ajaxSubmit(function (id){
							$("#id").attr("value",id);
							$("#message").show();
							setTimeout('$("#message").hide("show");',3000);
							parent.backPage();
							parent.$.colorbox.close();
						});
					}
				}else{//非每周
					$("#jobInfoFrom").ajaxSubmit(function (id){
						$("#id").attr("value",id);
						$("#message").show();
						setTimeout('$("#message").hide("show");',3000);
						parent.backPage();
						parent.$.colorbox.close();
					});
				}
			
			},
			rules: {
				runAsUser: "required",
				code: "required",
				url: "required",
				typeEnum:"required",
			    everySecond: {
				    digits: true,
				    maxlength:7,
				    min:1
			    },
			    everyHour: {
			    	digits: true,
				    maxlength:7,
				    min:1
			    }
			},
			messages: {
				runAsUser: "必填",
				code: "必填",
				url: "必填",
				typeEnum: "必填",
				everySecond:{
				   digits : "请输入7位以下的整数",
				   maxlength:"请输入7位以下的整数",
				   min:"请输入大于1的整数"
			    },
			    everyHour:{
			    	digits : "请输入7位以下的整数",
				   maxlength:"请输入7位以下的整数",
				   min:"请输入大于1的整数"
				}
			}
		});
	}

	/*---------------------------------------------------------
	函数名称:selectPrincipal
	参    数:id
	功    能：负责人树
	------------------------------------------------------------*/
	function selectPrincipal(name,id){
//		popTree({ title :'选择',
//		innerWidth:'400',
//		treeType:'MAN_DEPARTMENT_TREE',
//		defaultTreeValue:'id',
//		leafPage:'false',
//		treeTypeJson:null,
//		multiple:'false',
//		hiddenInputId:id,
//		showInputId:name,
//		loginNameId:'',
//		acsSystemUrl:imatrixRoot,
//		isAppend:"false",
//		callBack:function(){
//			getUserInformation();
//		}});
		var zTreeSetting={
				leaf: {
					enable: false
				},
				type: {
					treeType: "MAN_DEPARTMENT_TREE",
					noDeparmentUser:false,
					onlineVisible:false
				},
				data: {
				},
				view: {
					title: "选择",
					width: 300,
					height:400,
					url:imatrixRoot,
					showBranch:true
				},
				feedback:{
					enable: true,
			                //showInput:"point_user",
			                //hiddenInput:"point_user_value",
			                append:false
				},
				callback: {
					onClose:function(){
						getUserInformation(name,id);
					}
				}			
				};
			    popZtree(zTreeSetting);
	}

	function getUserInformation(name,id){
		$("#"+name).attr("value",ztree.getName());
		$("#"+id).attr("value",ztree.getId());
		$("#runAsUser").attr("value",ztree.getLoginName());
	}

	function showDiscription(){
		$("#discription").show();
		 $.colorbox({href:"#discription",inline:true, innerWidth:550, innerHeight:450,overlayClose:false,title:"说明",onClosed:function(){$("#discription").hide();}});
	}
	function secondOrHour(obj){
        if(obj.id=='everySecond'){
            $("#everyHour").attr("value","");
         }else{
        	$("#everySecond").attr("value","");
         }
	}
	</script>
</head>
<body onload="">
<div class="ui-layout-center">
<div class="opt-body">
	<div class="opt-btn">
		<a class="btn" href="#" onclick="submitJobInfo();"><span><span>提交</span></span></a>
	</div>
	<div id="opt-content" >
		<div id="message" style="display: none;"><font class='onSuccess'><nobr>保存成功！</nobr></font></div>
		<form action="" name="jobInfoFrom" id="jobInfoFrom" method="post">
			<input  type="hidden" name="id" id="id" value="${id}"/>
			<input type="hidden" name="systemId" id="systemId" value="${systemId}"/>
			<table>
				<tbody>
				<s:if test="id==null">
					<tr>
						<td>
							定时运行身份：<input readonly="readonly" type="text" id="runAsUserName" name="runAsUserName" value="${runAsUserName }"/>
						<input readonly="readonly" type="hidden" id="runAsUserNameId" />
						<input type="hidden" id="runAsUser" name="runAsUser" value="${runAsUser }"/>
						<a href="#" onclick="selectPrincipal('runAsUserName','runAsUserNameId')" title="追加"  class="small-btn" id="selectBtn">
							<span><span>选择</span></span>
						</a>
						</td>
					</tr>
					<tr>
						<td>
							定时任务编号：<input type="text" name="code" id="jobCode" value="${code}" maxlength="60"/><span style="color: red;margin: 40px;">(必须是唯一的)</span>
						</td>
					</tr>
					<tr>
						<td>
							定时请求类型：<select name="applyType" id="applyType">
										<s:iterator value="@com.norteksoft.bs.options.enumeration.ApplyType@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr>
						<td>
							定时任务地址：<input type="text" name="url" id="url" value="${url}" maxlength="60"/><span style="color: red;margin: 40px;">(如:"/rest/wf/delegate")</span>
						</td>
					</tr>
					<tr>
						<td>
							定时任务备注：<input type="text" name="description" id="urlInfo" value="${description}" maxlength="60"/>
						</td>
					</tr>
				</s:if>
					<tr>
						<td>
							定时任务方式：<select name="typeEnum" id="typeEnum" onchange="typeChange(this.value);">
											<option value="">请选择</option>
										<s:iterator value="@com.norteksoft.bs.options.enumeration.TimingType@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyMonth" style="display: none;">
						<td>
							定时任务每月：<select id="everyMonth" name="everyMonth"  class="customRequired">
										<option value="">请选择</option>
										<s:iterator value="@com.norteksoft.bs.options.enumeration.DateEnum@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyWeek" style="display: none;">
						<td>
							定时任务每周：<select id="everyWeek" name="everyWeek" multiple="multiple">
										<s:iterator value="@com.norteksoft.bs.options.enumeration.WeekEnum@values()" var="FK">
											<option value="${FK}"><s:text name="%{code}"></s:text></option>
										</s:iterator>
									</select>
						</td>
					</tr>
					<tr id="tr_everyDate" style="display: block;">
						<td>
							定时任务每天：<input id="everyDate" name="everyDate" value="" readonly="readonly" class="customRequired"/>
						</td>
					</tr>
					<tr id="tr_appointTime" style="display: none;">
						<td>
							定时指定日期：<input id="appointTime" name="appointTime" value="" readonly="readonly" class="customRequired"/>
						</td>
					</tr>
					<tr id="tr_appointSet" style="display: none;">
						<td>
							定时高级设置：<input id="appointSet" name="appointSet" value="" maxlength="30" class="customRequired"/><span style="color: red;margin: 40px;">(必须按照高级设置说明设置)</span>
						</td>
					</tr>
					<tr id="tr_appointSet_discription" style="display: none;">
						<td>
							高级设置说明：<a href="#" onclick="showDiscription();">查看</a>
						</td>
					</tr>
					<tr id="tr_intervalTime_type" style="display: none;">
						<td>
							时间间隔单位：<select id="intervalType" onchange="intervalTypeChange(this.value);" class="customRequired">
							                <option value="">请选择</option>
											<option value="secondType">分钟</option>
											<option value="hourType">小时</option>
									  </select>
						</td>
					</tr>
					<tr id="tr_intervalTime_second" style="display: none;">
						<td>
							时间间隔分钟：<input id="everySecond" name="everySecond" value=""  onclick="secondOrHour(this);" class="customRequired intervalTimeValidate"/></br>
						</td>
					</tr>
					<tr id="tr_intervalTime_hour" style="display: none;">
						<td>
							时间间隔小时：<input id="everyHour" name="everyHour" value="" onclick="secondOrHour(this);" class="customRequired intervalTimeValidate"/>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
</div>
<div id="discription" style="display: none;margin: 15px;">
	<table class="form-table-border-left">
		<tr>
			<td>
				定时器的配置:按照Quartz表达式的规则配置，表达式从左到右每一位分别表示：秒（0-59） 、分（0-59）、时（0-23）、日期（0-31）、月份（1-12）、星期（1-7）、年（1907-2099），其中“年”可选，其他必填，每一位都有取值范围，不能随意取值，详细规则查阅Quartz表达式的相关资料，以下是范例说明:<br/>
				"0 0 12 * * ?" 每天中午12点触发<br/>
				"0 15 10 ? * *" 每天上午10:15触发<br/>
				"0 15 10 * * ?" 每天上午10:15触发<br/>
				"0 15 10 * * ? *" 每天上午10:15触发 <br/>
				"0 15 10 * * ? 2005" 2005年的每天上午10:15触发 <br/>
				"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发><br/>
				"0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 <br/>
				"0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发<br/>
				"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 <br/>
				"0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 <br/>
				"0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发<br/>
				"0 15 10 15 * ?" 每月15日上午10:15触发 <br/>
				"0 15 10 L * ?" 每月最后一日的上午10:15触发 <br/>
				"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发<br/>
				"0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发<br/>
				"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发<br/>
			</td>
		</tr>
	</table>
<br></br>
</div>
</body>
<script type="text/javascript" src="${resourcesCtx}/widgets/colorbox/jquery.colorbox.js"></script>
<script type="text/javascript" src="${resourcesCtx}/widgets/timepicker/timepicker-all-1.0.js"></script>
</html>