$(function(){
  $.fn.EasyWidgets({
    behaviour : {
      useCookies : true
    },
    callbacks : {
      onAdd : null,
      onEdit : null,
      onShow : null,
      onHide : null,
      onClose : null,
      onEnable : null,
      onExtend : null,
      onDisable : null,
      onDragStop : function(){automaticHeight(); return true;},
      onCollapse : null,
      onAddQuery : null,
      onEditQuery : null,
      onShowQuery : null,
      onHideQuery : null,
      onCloseQuery : null,
      onCancelEdit : null,
      onEnableQuery : null,
      onExtendQuery : function(){automaticHeight(); return true;},
      onDisableQuery : null,
      onCollapseQuery : function(){automaticHeight(); return true;},
      onCancelEditQuery : null,
      onChangePositions : null,
      onRefreshPositions : null
    },
    i18n : {
	      closeText : '<img src="../../images/cls-1.png" alt="关闭" width="22" height="16" />',
	      collapseText : '<img src="../../images/test1.png" alt="收缩" width="22" height="16" />',
	      extendText : '<img src="../../images/test1.png" alt="收缩" width="22" height="16" />'
	    }

  });
  

  
  function hehe(){
	 // $.GetCookie('ew-close');
	  var aCookie = document.cookie.split("; "); 
      for(var i=0; i < aCookie.length; i++) {
    	  //alert(aCookie[i]);
          //closeName : 'ew-close',
          //disableName : 'ew-disable',
          //positionName : 'ew-position',
          //collapseName : 'ew-collapse'
      }       
  }
  
});