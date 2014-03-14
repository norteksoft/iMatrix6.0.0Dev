package com.norteksoft.product.util;

import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.enumeration.DataState;


/**
 * 菜单工具类
 * 
 */
public class SystemUrls {
	
	private static final String SYSTEM_MEMCACHE_KEY = "_system_url_infos";
	private static final Integer CODE_INDEX = 0;
	private static final Integer URL_INDEX = 1;
	private static final Integer NAME_INDEX = 2;
	private static final Integer IS_PRODUCT_INDEX = 3;
	private static final Integer ENABLE_STATE_INDEX = 4;
	
	
	private SystemUrls(){}
	
	/**
	 * 更新缓存
	 */
	public static synchronized void updateUrls(){
		init();
	}
	
	/**
	 * 初始化系统URL缓存
	 * key : SYSTEM_MEMCACHE_KEY
	 * value: [code, URL, name, isProduct]
	 */
	private static synchronized void init() {
		List<com.norteksoft.product.api.entity.Menu> menus = ApiFactory.getMmsService().getTopMenus();
		String[][] systemInfos = new String[menus.size()][5];
		for(int i = 0; i < menus.size(); i++){
			systemInfos[i][CODE_INDEX] = menus.get(i).getCode();
			systemInfos[i][URL_INDEX] = menus.get(i).getUrl();
			systemInfos[i][NAME_INDEX] = menus.get(i).getName();
			systemInfos[i][IS_PRODUCT_INDEX] = "true";
			systemInfos[i][ENABLE_STATE_INDEX] = menus.get(i).getEnableState().toString();
		}
		MemCachedUtils.add(SYSTEM_MEMCACHE_KEY, systemInfos);
	}
	
	/**
	 * 获取portal主题
	 * @return
	 */
	public static String getPortalTheme(){
		return (String)MemCachedUtils.get("THEME_"+ContextUtils.getUserId());
	}
	
	/**
	 * 获取portal的访问地址
	 * @return
	 */
	public static String getPortalUrl(){
		return (String)MemCachedUtils.get("PORTALURL");
	}
	
	private static Object getMenusFromCache(){
		Object obj = MemCachedUtils.get(SYSTEM_MEMCACHE_KEY);
		if(obj == null) init();
		return MemCachedUtils.get(SYSTEM_MEMCACHE_KEY);
	}
	
	/**
	 * 查询业务系统的访问路径
	 * @param systemCode 系统code
	 * @return 业务系统的访问路径
	 */
	public static String getBusinessPath(String systemCode){
		String[][] menus = (String[][]) getMenusFromCache();
		for(String[] menu : menus){
			if(systemCode.equalsIgnoreCase(menu[CODE_INDEX])){
				return getSystemPath(systemCode,menu);
			}
		}
		return "";
	}

	/**
	 * 根据系统编号获取系统URL
	 * @param code
	 * @return
	 */
	public static String getSystemUrl(String code){
		String url = null;
		String[][] menus = (String[][]) getMenusFromCache();
		for(String[] menu : menus){
			if(code.equalsIgnoreCase(menu[0])){
				url = getSystemPath(code,menu);
				break;
			}
		}
		if(url == null){
			if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){//表示开发模式,当缓存中没有相应编号对应的菜单时，走该判断，即不走上面的for循环时
				if(PropUtils.getProp("host.imatrix")!=null && PropUtils.isBasicSystem("/"+code)){
					String imatrixUrl = PropUtils.getProp("host.imatrix");
					if(imatrixUrl.lastIndexOf("/")==imatrixUrl.length()-1){//如果有斜线
						url = imatrixUrl+code;
					}else{
						url = imatrixUrl+"/"+code;
					}
				}else if(PropUtils.getProp("host."+code)!=null){
					url = PropUtils.getProp("host."+code);
				}
			}
		}
		if(url == null){
			url = ApiFactory.getMmsService().getTopMenuUrl(code);
		}
		return url;
	}
	
	public static String getSystemPageUrl(String code){
		String url = getSystemUrl(code);
		if(url.lastIndexOf("/")==url.length()-1)url = url.substring(0,url.length()-1);//去掉最后的斜线
		return url;
	}
	
	/**
	 * 获取系统菜单
	 * @return
	 */
	public static String getMenus(){
		StringBuilder resultMenu = new StringBuilder();
		String[][] menus = (String[][]) getMenusFromCache();
		for(String[] menu : menus){
			if(menu.length==5){//新加了一个菜单状态
				if(DataState.ENABLE.toString().equals(menu[ENABLE_STATE_INDEX])){
					if(Boolean.parseBoolean(menu[IS_PRODUCT_INDEX])){
						resultMenu.append(processProductMenuHtml(menu));
					}else{
						resultMenu.append(processCommonMenuHtml(menu));
					}
				}
			}else{//旧版没有菜单状态
				if(Boolean.parseBoolean(menu[IS_PRODUCT_INDEX])){
					resultMenu.append(processProductMenuHtml(menu));
				}else{
					resultMenu.append(processCommonMenuHtml(menu));
				}
			}
		}
		return resultMenu.toString();
	}
	
	/*
	 * values[0] = menu Id
	 * values[1] = menu URL
	 * values[2] = menu name
	 * <li id='code' ONMOUSEMOVE='mouseMove(this,3);' ONMOUSEOUT='mouseOut(this,4);'><a HREF='URL'>name</a></li>
	 */
	private static String processCommonMenuHtml(String[] values){
		if(PropUtils.isBasicSystem(values[URL_INDEX])){
			String redirectUrl=PropUtils.getProp("redirectUrl.properties",values[CODE_INDEX]);
			if(ContextUtils.isAuthority(redirectUrl,values[CODE_INDEX])){
				return new StringBuilder("<li id='")
				.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], true))
				.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
				.toString();
			}
		}else{
			return new StringBuilder("<li id='")
			.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], true))
			.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
			.toString();
		}
		return "";
	}
	/*
	 * values[0] = menu Id
	 * values[1] = menu URL
	 * values[2] = menu name
	 * <li id='code' ONMOUSEMOVE='mouseMove(this,3);' ONMOUSEOUT='mouseOut(this,4);'><a HREF='URL'>name</a></li>
	 */
	private static String processProductMenuHtml(String[] values){
		if(PropUtils.isBasicSystem(values[URL_INDEX])){
			String redirectUrl=PropUtils.getProp("redirectUrl.properties",values[CODE_INDEX]);
			if(ContextUtils.isAuthority(redirectUrl,values[CODE_INDEX])){
				return new StringBuilder("<li id='")
				.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], false))
				.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
				.toString();
			}
		}else{
			return new StringBuilder("<li id='")
			.append(values[CODE_INDEX]).append(getStyle(values[CODE_INDEX], false))
			.append("<a href=\'").append(values[URL_INDEX]).append("'>").append(values[NAME_INDEX]).append("</a></li>")
			.toString();
		}
		return "";
	}
	
	private static String getStyle(String sysCode, boolean isSystem){
		String result = "";
		String code = ContextUtils.getSystemCode();
		if(isSystem){
			if(sysCode.equals(code)){
				result = "' class='system fristBg'>";
			}else{
				result = "' class='system'>";
			}
		}else{
			if(sysCode.equals(code)){
				result = "' class='navPrdt navPrdt1'>";
			}else{
				result = "' class='navPrdt'>";
			}
		}
		return result;
	}
	
	/**
	 * 根据uri获得系统编码
	 * @param uri 如:/imatrix/mms/module/menu.htm(底层平台的应用),/oa/notice/list.htm(项目应用),80端口时/engine/textHistory.htm(查看流转历史)
	 * @return
	 */
	public static String getSysCodeFromUri(String uri){
		String[] split = uri.split("/");
        String sysCode = "portal";
        String imatrixUrl=SystemUrls.getSystemUrl("imatrix");
        String[] imatrixUrlSplit = imatrixUrl.split("/");
    	if(split.length>1){
    		if(imatrixUrlSplit.length>3){//如果imatrixCode为空则表示为80端口，例如：/engine/textHistory.htm(查看流转历史)
    			//80端口时imatrix配置为：http://192.168.1.51,则imatrixUrlSplit的长度为3
    			String imatrixCode=imatrixUrl.substring(imatrixUrl.lastIndexOf("/")+1);
    			if(imatrixCode.equals(split[1])){
    				if(split.length>2){
    					sysCode = split[2];
    					if(!PropUtils.isBasicSystem("/"+sysCode)){//如果不是底层系统，则默认为portal，如：/imatrix/j_spring_security_logout时，就会出现问题
    						sysCode = "portal";
    					}
    				}
    			}else{
    				sysCode = split[1];
    			}
    		}
    	}
        return sysCode;
	}
	
	public static String getSystemPath(String code, String[] menu) {
		//Long systemId = ContextUtils.getSystemId(code);
		String url = null;
		if(PropUtils.getProp("project.model")!=null&&PropUtils.getProp("project.model").equals("developing.model")){//表示开发模式
			if(PropUtils.getProp("host.imatrix")!=null && PropUtils.isBasicSystem("/"+code)){
				String imatrixUrl = PropUtils.getProp("host.imatrix");
				if(imatrixUrl.lastIndexOf("/")==imatrixUrl.length()-1){//如果有斜线
					url = imatrixUrl+code;
				}else{
					url = imatrixUrl+"/"+code;
				}
			}else if(PropUtils.getProp("host."+code)!=null){
				url = PropUtils.getProp("host."+code);
			}else if(menu[URL_INDEX].toString().indexOf("mms/common/list.htm")>0){
				String imatrixUrl = PropUtils.getProp("host.imatrix");
				if(imatrixUrl.lastIndexOf("/")==imatrixUrl.length()-1){//如果有斜线
					if(menu[URL_INDEX].toString().indexOf("?")>0){
						url = imatrixUrl+"mms/common/list.htm?";
					}else{
						url = imatrixUrl+"mms/common/list.htm";
					}
				}else{
					if(menu[URL_INDEX].toString().indexOf("?")>0){
						url = imatrixUrl+"/mms/common/list.htm?";
					}else{
						url = imatrixUrl+"/mms/common/list.htm";
					}
				}
			}else{
				url = menu[URL_INDEX];
			}
		}else{
			url = menu[URL_INDEX];
		}
		return url;
	}
}
