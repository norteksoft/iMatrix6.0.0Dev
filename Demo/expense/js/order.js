		
//编辑时单击事件
	function applyRoleClick(obj){
		custom_ztree({url:webRoot+'/order/role-tree.htm',
			inputObj:obj.currentInputId,
			width:400,
			height:400,
			title:'选择角色',
			nodeInfo:['type','roleId','roleName'],
			multiple:true,
			onsuccess:function(){
				var roles=getSelectValue("roleName");
				$("#"+obj.currentInputId).attr("value",roles);
			}
		});
	}
	
	//编辑时双击事件
	function applyDepartmentDblclick(obj){
		var acsSystemUrl = webRoot;
//		popTree({ title :'选择部门',
//			innerWidth:'400',
//			treeType:'DEPARTMENT_TREE',
//			defaultTreeValue:'id',
//			leafPage:'false',
//			multiple:'false',
//			hiddenInputId:obj.currentInputId,
//			showInputId:obj.currentInputId,
//			loginNameId:'',
//			acsSystemUrl:acsSystemUrl,
//			isAppend:"false",
//			callBack:function(){}});
		var zTreeSetting={
				leaf: {
					enable: false
				},
				type: {
					treeType: "DEPARTMENT_TREE",
					noDeparmentUser:true,
					onlineVisible:false
				},
				data: {
				},
				view: {
					title: "选择部门",
					width: 300,
					height:400,
					url:acsSystemUrl,
					showBranch:false
				},
				feedback:{
					enable: true,
			        showInput:obj.currentInputId,
			        hiddenInput:obj.currentInputId,
			        append:false
				},
				callback: {
					onClose:function(){
					}
				}			
				};
			    popZtree(zTreeSetting);
	}
	
	//编辑时下拉框切换事件
	function countryChange(obj){
		alert("下拉框切换事件");
	}
	
	//制保留2位小数，如：2，会在2后面补上00.即2.00  
	function toDecimal2(x) {  
	    var f = parseFloat(x);  
	    if (isNaN(f)) {  
	        return false;  
	    }  
	    var f = Math.round(x*100)/100;  
	    var s = f.toString();  
	    var rs = s.indexOf('.');  
	    if (rs < 0) {  
	        rs = s.length;  
	        s += '.';  
	    }  
	    while (s.length <= rs + 2) {  
	        s += '0';  
	    }  
	    return s;  
	}  
	
	//编辑时失去焦点事件
	function moneyBlur(obj){
		var price=$("#"+obj.rowid+"_price").attr("value");
		var amount=$("#"+obj.rowid+"_amount").attr("value");
		$("#"+obj.currentInputId).attr("value",toDecimal2(price*amount));
	}

	//重写(点击单元格触发的事件)
	function $onCellClick(rowid,iCol,cellcontent,e){
		alert("单击行回调事件!rowid:"+rowid);
	}

	//日期选中回调事件
	function $dateOnSelect(obj){
		alert("$dateOnSelect:日期选中回调事件");
	}

	//日期改变年月回调事件
	function $dateOnChangeMonthYear(obj){
		alert("$dateOnChangeMonthYear:日期改变年月回调事件");
	}
	
	//日期控件关闭回调事件
	function $dateOnClose(obj){
		alert("$dateOnClose:日期控件关闭回调事件");
	}
	
	//列表加载完成后回调
	function $gridComplete(){
		//alert("$gridComplete:列表加载完成后回调");
	}

	/***********编辑行后保存的回调方法***********/
	function $editRowSave(rowid,tableId){
		alert("编辑行Enter保存的回调方法");
	}
	
	/***********编辑行后取消的回调方法***********/
	function $editRowRestore(rowid,tableId){
		alert("$editRowSave:编辑行Esc取消后的回调方法");
	}
	
	/************编辑时的回调方法**************/
	function $editClickCallback(rowid,tableId){
		alert("$editClickCallback:编辑时的回调方法");
	}
	
	//双击行事件
	 function $ondblClick(id){
		 //alert("$ondblClick:双击行事件");
	}
		
	//增加行时回调方法
	function $addRowCallBack(newRowId,originalRowId,tableId){
		alert("$addRowCallBack:增加行时回调方法");
	}
	
	//行拖动开始事件
	function $sortableRowsStart(rowId,originalIndex,tableId){
		//alert("$sortableRowsStart:行拖动开始事件");
	}
	
	//行拖动结束事件
	function $sortableRowsStop(rowId,originalIndex,tableId){
		//alert("$sortableRowsStop:行拖动结束事件");
	}
	
	//右击事件
	function $onRightClick(id){
		alert("$onRightClick:右击事件");
	}
	
	//添加自定义按钮
	function $addButton(){
		return "[{name:'aa',className:'ui-icon ui-icon-arrowthick-1-w',click:'menuEvent' }]"; 
	}
	//自定义按钮事件
	function menuEvent(rowid,tableId){
		alert("点击自定义按钮！rowid："+rowid+"tableId:"+tableId);
	}