package com.norteksoft.tags.menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.util.tree.ZTreeNode;
/**
 * 一级菜单和二级菜单合并标签
 * @author ldx
 *
 */
public class TotalMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(TotalMenuTag.class);
	private MenuManager menuManager;
	private Long menuId;
	private Integer showNum=1;
	private String code;//系统code
	private boolean themeChagable = true;
	private boolean existable = true;
	private String selectMenuInfo="";//json格式的字符串，[{菜单实体属性},{菜单实体},...]
	private List<Menu> selectMenus = new ArrayList<Menu>();//选中的菜单列表
	private BusinessSystemManager businessSystemManager;
	@Override
	public int doStartTag() throws JspException {
		try {
			menuManager=(MenuManager)ContextUtils.getBean("menuManager");
			businessSystemManager = (BusinessSystemManager)ContextUtils.getBean("businessSystemManager");
			String lastMenuIdStr=pageContext.getRequest().getParameter("menuId");
			if(lastMenuIdStr!=null){
				menuId=Long.parseLong(lastMenuIdStr);
			}else{
				if(pageContext.getRequest().getAttribute("menuId")!=null){
					menuId =  Long.parseLong(pageContext.getRequest().getAttribute("menuId").toString());
				}else{
					String url=(String)pageContext.getRequest().getAttribute("struts.request_uri");
					String[] urls=url.split("/");
					//底层系统应用地址
					String systemCode=ContextUtils.getSystemCode();
					String code=urls[1];
					if(urls.length>=3){
						String tempCode = urls[2];
						Menu tempMenu=menuManager.getMenuByCode(tempCode);
						if(tempMenu !=null){
							code=tempCode;
						}
					}
					Menu lastMenu=menuManager.getDefaultMenuByLayer(StringUtils.isEmpty(code)?systemCode:code);
					if(lastMenu!=null){
						menuId =  lastMenu.getId();
					}
				}
			}
			if(menuId!=null){
				firstMenu = menuManager.getRootMenu(menuId);
				selectMenus = menuManager.getSelectMenus(menuId,firstMenu);
				firstMenu = menuManager.clone(firstMenu);//因为selectMenus是按层级排序的,所以第一个元素就是一级菜单
				selectMenuInfo = JsonParser.object2Json(selectMenus);
			}
			 JspWriter out=pageContext.getOut(); 
			 String result = readScriptTemplate();
			 out.print(result);
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	private Menu firstMenu = null;
	//读取脚本模板
	private String readScriptTemplate() throws Exception{
		String webapp=((HttpServletRequest)pageContext.getRequest()).getContextPath();
		menuManager.initAllMenus();
		// 获得有权限的一级菜单集合
		List<Menu> resultMenus=getHasAuthFirstMenus();
		Map<String, Object> root=new HashMap<String, Object>();
		
		//交换一级菜单中显示的和更多中的菜单（开始） 
		int lastIndexOf=resultMenus.lastIndexOf(firstMenu);
		if(lastIndexOf>=showNum){
			Menu temp=menuManager.clone(resultMenus.get(showNum-1));
			firstMenu.setLastMenuId(menuId);
			
			String menuUrl = menuManager.getHasAuthFirstMenuUrl(temp,firstMenu,temp.getLastMenuId());
			temp.setMenuUrl(menuUrl);
			Menu menu = menuManager.getMenu(menuId);
			menuUrl = menuManager.getHasAuthFirstMenuUrl(menu,firstMenu,menuId);
			firstMenu.setMenuUrl(menuUrl);
			
			resultMenus.set(showNum-1, firstMenu);
			resultMenus.set(lastIndexOf, temp);
		}
		//交换一级菜单中显示的和更多中的菜单（ 结束）
		String imatrixUrl=SystemUrls.getSystemPageUrl("imatrix");
		root.put("showNum", showNum);
		root.put("menuId", menuId);
		root.put("moreSystem", "更多");
		root.put("menus", resultMenus);
		root.put("firstMenus", JsonParser.object2Json(resultMenus));
		root.put("menuSize", resultMenus.size());
		root.put("ctx", webapp);
		root.put("imatrixUrl", imatrixUrl);
		root.put("honorificTitle", ContextUtils.getHonorificTitle());
		root.put("currentTime", getCurrentTime());
		root.put("themeChagable", themeChagable);
		root.put("existable", existable);
		String versionType = PropUtils.getProp("product.version.type");
		root.put("versionType",versionType==null?"":versionType );
		root.put("selectMenuInfo",selectMenuInfo );
		String resourcesCtx = PropUtils.getProp("host.resources");
		root.put("resourcesCtx",resourcesCtx==null?"":resourcesCtx );
		//设置模板中二级菜单需要的参数
		Long firstMenuId = 0L;
		if(firstMenu!=null){
			firstMenuId = firstMenu.getId();
		}
		List<Menu> secMenus=menuManager.getEnableMenuByLayer(2,firstMenuId);
		
		setParamsForSecondMenu(root,firstMenu,secMenus);
		//设置模板中左侧菜单需要的参数
		Long secondMenuId = 0L;
		Long thirdMenuId = 0L;
		if(selectMenus.size()>=2){//因为selectMenus是按层级排序的
			secondMenuId = selectMenus.get(1).getId();
		}
		if(selectMenus.size()>=3){//因为selectMenus是按层级排序的
			thirdMenuId = selectMenus.get(2).getId();
		}
		root.put("thirdMenuId",thirdMenuId);
		List<Menu> thirdMenus=menuManager.getEnableMenuByLayer(3,secondMenuId);
		List<Menu> treeMenus = menuManager.getThirdMenusChildren(thirdMenuId);
		List<Menu> fourMenus=new ArrayList<Menu>();
		if(treeMenus.isEmpty()){//表示不需要拼ztree树才需要查找四级菜单集合
			fourMenus = menuManager.getEnableMenuByLayer(4,thirdMenuId);
		}
		setParamsForLeftMenu(root,firstMenu,thirdMenus,fourMenus);
		//将四级及其孩子拼接为Ztree树
		getZtreeFourMenusAndChildren(treeMenus,root);
			
		String result = TagUtil.getContent(root, "menu/totalMenuTag.ftl");
		return result;
	}
	
	private String getCurrentTime(){
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日");
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		String weekDay = null;
		switch(day){
			case Calendar.MONDAY: weekDay="星期一";break;
			case Calendar.TUESDAY: weekDay="星期二";break;
			case Calendar.WEDNESDAY: weekDay="星期三";break;
			case Calendar.THURSDAY: weekDay="星期四";break;
			case Calendar.FRIDAY: weekDay="星期五";break;
			case Calendar.SATURDAY: weekDay="星期六";break;
			case Calendar.SUNDAY: weekDay="星期日";break;
		}
		return fmt.format(cal.getTime())+"  "+weekDay;
	}
	/**
	 * 获得有权限的一级菜单
	 * @return
	 */
	private List<Menu> getHasAuthFirstMenus(){
		List<Menu> menus=menuManager.getEnabledRootMenuByCompany();
		List<Menu> resultMenus=new ArrayList<Menu>();
		for(Menu menu:menus){
			boolean isAuth = shouldShowMenu(menu,menu);
			if(isAuth){//表示有权限
				Menu tempMenu =menuManager.clone(menu);
				Menu lastMenu = menuManager.getLastMenu(menu,menu);
				if(lastMenu!=null)tempMenu.setLastMenuId(lastMenu.getId());
				setHasAuthFirstMenuUrl(tempMenu,menu);
				resultMenus.add(tempMenu);
			}
		}
		return resultMenus;
	}
	
	/**
	 * 
	 * @param menu 一级菜单
	 * @return true:表示有权限，false表示没有权限
	 */
	private boolean shouldShowMenu(Menu menu,Menu firstMenu){//是否是使用了平台的系统：底层的系统和使用了底层的系统（例如：OA，url为/common/list.htm的自定义系统）均属于使用了平台的系统
		if(menu.getExternalable()!=null&&menu.getExternalable()){
			menu.setMenuUrl(menu.getUrl());
			return true;
		}
		if(StringUtils.isEmpty(menu.getUrl())){
			// 一级菜单的url为空，菜单有事件则不做权限判断
			return true;
		}
		BusinessSystem business = businessSystemManager.getSystemBySystemCode(firstMenu.getCode());
		if(business!=null){//当是自定义的系统时，business为空
			Menu authMenu = menuManager.getAuthMenu(menu,business,firstMenu);
			String authUrl = authMenu==null?null:authMenu.getUrl();
			if(StringUtils.isEmpty(authUrl)){//表示没有权限
				return false;
			}else{//表示有权限
				String menuUrl = menuManager.getHasAuthMenuUrl(menu,firstMenu,authMenu);
				menu.setMenuUrl(menuUrl);
				return true;
			}
		}else{//自定义系统时
			//自定义系统分为两种情况，
			//1  平台级别的自定义系统，
			//2  例如百度等系统，即系统的url不为/common/list.htm的自定义系统
			//对于第1种情况，可以判断系统的权限，第2种则无法做判断，则默认为有权限显示在一级菜单中
			if(menuManager.isCustomSystem(menu)){//当是第1种情况时
				if(ContextUtils.isAuthority("/common/list.htm", "mms")){//表示有自定义系统的权限
					String menuUrl = getCustomSystemMenu(menu,firstMenu);
					menu.setMenuUrl(menuUrl);
					return true;
				}
			}else{//第2种则无法做判断，则默认为有权限显示在一级菜单中
				return true;
			}
		}
		return false;
	}
	/**
	 * 获得自定义系统中的菜单地址
	 * @return
	 */
	private String getCustomSystemMenu(Menu menu,Menu firstMenu){
		if(firstMenu!=null&&isFirstMenu(menu,firstMenu)){
			String menuUrl = menu.getUrl();
			String[] menuinfos = menu.getUrl().split("/mms");
			String functionPath = "";
			if(menuinfos.length>=2)functionPath = menuinfos[1];//获得/common/list.htm
			menuUrl = SystemUrls.getSystemPageUrl("mms")+functionPath;//http://.../mms/common/list.htm
			return menuUrl;
		}else{
			return SystemUrls.getSystemPageUrl("imatrix")+menu.getUrl();
		}
	}
	
	/**
	 * 是否是一级菜单
	 * @param menu
	 * @param firstMenu
	 * @return
	 */
	private boolean isFirstMenu(Menu menu,Menu firstMenu){
		return menu.getId().equals(firstMenu.getId());
	}
	
	
	//设置模板中二级菜单需要的参数
	private void setParamsForSecondMenu(Map<String, Object> root,Menu firstMenu,List<Menu> secMenus) throws Exception{
		if(firstMenu!=null){
			if(secMenus!=null&&secMenus.size()>0){
				//获得有权限的二级菜单集合
				List<Menu> resultMenus=getHasAuthSecMenus(firstMenu,secMenus);
				
				root.put("secMenus", resultMenus);
				root.put("showSecMenu", "true");
			}else{
				root.put("showSecMenu", "false");
			}
		}else{
			root.put("showSecMenu", "false");
		}
	}
	/**
	 * 获得有权限的二级菜单集合
	 * @param firstMenu
	 * @return
	 */
	private List<Menu> getHasAuthSecMenus(Menu firstMenu,List<Menu> secMenus){
		return getHasAuthMenus(secMenus,firstMenu);
	}
	/**
	 * 获得有权限的菜单集合
	 * @param menus
	 * @return
	 */
	private List<Menu> getHasAuthMenus(List<Menu> menus,Menu firstMenu){
		List<Menu> resultMenus=new ArrayList<Menu>();
		for(Menu menu:menus){
			boolean isAuth = shouldShowMenu(menu,firstMenu);
			if(isAuth){//表示有权限
				Menu lastMenu = menuManager.getLastMenu(menu,firstMenu);
				menu.setLastMenuId(lastMenu.getId());
				setHasAuthMenuUrl(menu,firstMenu);
				resultMenus.add(menu);
			}
		}
		return resultMenus;
	}
	
	/**
	 * 设置菜单的url，处理参数
	 * @param menu
	 */
	private void setHasAuthMenuUrl(Menu menu,Menu firstMenu){
		String menuUrl = menuManager.getMenuUrl(menu,firstMenu,menu.getLastMenuId());
		menu.setMenuUrl(menuUrl);
	}
	
	/**
	 * 设置一级菜单的url，处理参数
	 * @param menu
	 */
	private void setHasAuthFirstMenuUrl(Menu menu,Menu firstMenu){
		String menuUrl =  menuManager.getHasAuthFirstMenuUrl(menu,firstMenu,menu.getLastMenuId());
		menu.setMenuUrl(menuUrl);
	}
	
	//设置模板中左侧菜单需要的参数
	private void setParamsForLeftMenu(Map<String, Object> root,Menu firstMenu,List<Menu> threeMenus,List<Menu> fourMenus) throws Exception{
		if(threeMenus==null){
			root.put("showLeftMenu", "false");
		}else{
			root.put("thirdMenus", getHasAuthThreeMenus(firstMenu,root,threeMenus));
		}
		if(fourMenus!=null){//四级菜单集合存在，且不需要拼接ztree树
			root.put("fourMenus", getHasAuthFourMenus(firstMenu,fourMenus));
		}
	}
	
	/**
	 * 获得有权限的三级菜单集合
	 * @param firstMenu
	 * @return
	 */
	private List<Menu> getHasAuthThreeMenus(Menu firstMenu,Map<String, Object> root,List<Menu> threeMenus){
		if(threeMenus!=null&&threeMenus.size()>0){
			root.put("showLeftMenu", "true");
		}else{
			root.put("showLeftMenu", "false");
		}
		return getHasAuthMenus(threeMenus,firstMenu);
	}
	/**
	 * 获得有权限的四级菜单集合
	 * @param firstMenu
	 * @return
	 */
	private List<Menu> getHasAuthFourMenus(Menu firstMenu,List<Menu> fourMenus){
		return getHasAuthMenus(fourMenus,firstMenu);
	}
	/**
	 * 将四级及其孩子拼接为Ztree树
	 */
	private void getZtreeFourMenusAndChildren(List<Menu> treeMenus,Map<String, Object> root){
		List<ZTreeNode> ztreeNodes=getHasAuthTreeMenus(treeMenus,firstMenu);
		root.put("showZtree", ztreeNodes.isEmpty()?"false":"true");
		if(!treeMenus.isEmpty()){
			root.put("fourMenuTreeDatas", JsonParser.object2Json(ztreeNodes));
		}else{
			root.put("fourMenuTreeDatas", "\"\"");
		}
	}
	
	/**
	 * 获得有权限的菜单集合
	 * @param menus
	 * @return
	 */
	private List<ZTreeNode> getHasAuthTreeMenus(List<Menu> treeMenus,Menu firstMenu){
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		ZTreeNode zNode=null;
		for(Menu menu:treeMenus){
			boolean isAuth = shouldShowMenu(menu,firstMenu);
			if(isAuth){//表示有权限
				Menu lastMenu = menuManager.getLastMenu(menu,firstMenu);
				menu.setLastMenuId(lastMenu.getId());
				Menu parent = menu.getParent();
				Long parentId = parent==null?0:parent.getId();
				zNode = new ZTreeNode(menu.getId().toString(), parentId.toString(), menu.getName(), "true", "false", "", "", "", "");
				Menu cloneMenu = menuManager.clone(menu);
				String menuUrl = null;
				if(menuManager.isCustomSystem(menu)){//如果是自定义系统
					menuUrl = getCustomSystemMenu(menu,null);
				}else{//表示不是自定义系统
					menuUrl = menuManager.getHasAuthMenuUrl(cloneMenu,firstMenu,menu);
				}
				cloneMenu.setMenuUrl(menuUrl);
				zNode.setData("["+JsonParser.object2Json(cloneMenu)+"]");
				ztreeNodes.add(zNode);
			}
		}
		return ztreeNodes;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public Integer getShowNum() {
		return showNum;
	}

	public void setShowNum(Integer showNum) {
		this.showNum = showNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setThemeChagable(boolean themeChagable) {
		this.themeChagable = themeChagable;
	}

	public void setExistable(boolean existable) {
		this.existable = existable;
	}
}
