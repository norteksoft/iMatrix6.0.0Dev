package com.norteksoft.mms.module.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.dao.MenuDao;
import com.norteksoft.mms.module.dao.ModulePageDao;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.portal.entity.Widget;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;

@Service
@Transactional(readOnly=true)
public class MenuManager {

	private MenuDao menuDao;
	private ModulePageDao modulePageDao;
	private AcsUtils acsUtils;
	@Autowired
	private DataTableManager dataTableManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}
	@Autowired
	public void setModulePageDao(ModulePageDao modulePageDao) {
		this.modulePageDao = modulePageDao;
	}
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	/**
	 * 保存菜单
	 */
	@Transactional(readOnly=false)
	public void saveMenu(Menu menu){
		if(menu.getId()==null){
			Menu parent = menu.getParent();
			if(menu.getSystemId()==null){
				if(parent!=null){
					//二级菜单系统id
					menu.setSystemId(parent.getSystemId());
					menu.setCurrentSystemId(parent.getSystemId());
				}else{
					//自定义一级菜单
					menu.setType(MenuType.CUSTOM);
					Long systemId = ContextUtils.getSystemId("mms");
					menu.setSystemId(systemId);
					menu.setCurrentSystemId(systemId);
					if(!(StringUtils.isNotBlank(menu.getUrl())&&menu.getUrl().startsWith("http:"))){
						String sysUrl=getSysUrl(ContextUtils.getSystemId("mms"));
						menu.setUrl(sysUrl+"/common/list.htm");
					}
				}
				//自定义非一级菜单的url的设置
				if(parent!=null){
					if("#this".equals(menu.getUrl())||StringUtils.isEmpty(menu.getUrl())){
						//标准或自定义系统中的子菜单没设url则为“定义菜单”
						menu.setType(MenuType.CUSTOM);
						//三级和三级以下菜单
						if(parent.getParent()!=null){
							menu.setUrl("/mms/common/list.htm");
						}else{//二级菜单
							menu.setUrl("/mms/common/list.htm");
						}
					}else{
						//子菜单设置了url
						if(parent.getType()==MenuType.STANDARD){
							//标准系统已有子菜单设置为“标准菜单”
							menu.setType(MenuType.STANDARD);
						}else{
							menu.setType(MenuType.CUSTOM);
							if(!menu.getExternalable()){//是否是外部系统，不是外部系统时，则表示是自定义菜单，则需要重新设置url
								//自定义系统中子菜单无论是否设置了url，永远为“自定义菜单”
								menu.setUrl("/mms/common/list.htm");
							}
						}
					}
				}
			}
			menu.setCompanyId(ContextUtils.getCompanyId());
			menu.setCreatedTime(new Date());
			if(parent!=null){
				menu.setLayer(parent.getLayer()+1);
			}
		}
		if(!menu.getLayer().equals(1)){//如果不是一级菜单
			if("/mms/common/list.htm".equals(menu.getUrl())||"/mms/common/list.htm?".equals(menu.getUrl())){
				//当不是一级菜单时，修改了路径为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为自定义
				menu.setType(MenuType.CUSTOM);
			}else{
				//当不是一级菜单时，修改了路径不为/mms/common/list.htm或/mms/common/list.htm?时，该菜单的类型改为标准
				menu.setType(MenuType.STANDARD);
			}
		}
		menuDao.saveMenu(menu);
	}
	
	/**
	 * 获取菜单
	 */
	public Menu getMenu(Long menuId){
		return menuDao.getMenu(menuId);
	}

	/**
	 * 得到公司所有的一级菜单
	 */
	public List<Menu> getRootMenuByCompany() {
		return menuDao.getRootMenuByCompany();
	}
	
	/**
	 * 得到公司所有启用的一级菜单
	 */
	public List<Menu> getEnabledRootMenuByCompany() {
		return menuDao.getEnabledRootMenuByCompany();
	}
	/**
	 * 删除菜单
	 */
	@Transactional(readOnly=false)
	public String deleteMenu(Menu menu) {
		List<DataTable> tables=dataTableManager.getAllDataTablesByMenu(menu.getId());
		if(tables.size()>0){
			return "该菜单已被使用,无法删除";
		}
		List<FormView> formviews=formViewManager.getFormViewsByMenu(menu.getId());
		if(formviews.size()>0){
			return "该菜单已被使用,无法删除";
		}
		menuDao.delete(menu);
		return "success";
	}
	public Menu getRootMenu(Long menuId){
		Menu menu=menuDao.get(menuId);
		return getRootMenu(menu);
	}
	
	private Menu getRootMenu(Menu menu){
		Integer layer = menu.getLayer();
		Menu rootMenu = menu;
		for(int i=layer;i>=1;i--){
			Menu parent = rootMenu.getParent();
			if(parent==null)break;
			rootMenu = parent;
		}
		return rootMenu;
	}
	
	public List<Menu> getMenuByLayer(Integer layer,Long parentId) {
		return menuDao.getMenuByLayer(layer,parentId);
	}
	public List<Menu> getEnableMenuByLayer(Integer layer,Long parentId) {
		return menuDao.getEnableMenuByLayer(layer,parentId);
	}
	
	public Menu getDefaultModulePageBySystem(String code, Long companyId) {
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, companyId);
		Menu firstMenu =null;
		if(menus.size()>0){
			firstMenu = menus.get(0);
		}
		Menu secondMenu = (firstMenu==null?null:firstMenu.getFirstChildren());
		return secondMenu==null?null:secondMenu.getFirstChildren();
	}
	/**
	 * 获得最底层菜单
	 * @param systemId
	 * @param companyId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Menu getLastMenu(Menu menu,Menu firstMenu) {
//		Menu menu=getMenu(menuId);
		if(menu==null)return null;
		BusinessSystem system = businessSystemManager.getBusiness(menu.getSystemId());
		List<Menu> children=menuDao.getChildrenEnabledMenus(menu.getId());
		if(children.size()<=0)return menu;//表示没有二级菜单
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId());
			if(isHasAuth(m,firstMenu,system)&&childrens.size()<=0){
				return m;
			}
			Menu authMenu = getChildLastMenu(childrens,firstMenu,system);
			if(authMenu!=null)return authMenu;
		}
		return null;
	}
	
	/**
	 * 判断是否是自定义系统
	 * @param menu
	 * @return true表示是自定义系统，false表示不是自定义系统
	 */
	public boolean isCustomSystem(Menu menu){
		if(menu.getUrl().indexOf("mms/common/list.htm")>=0){
			return true;
		}
		return false;
	}
	/**
	 * 菜单是否有权限
	 * @param menu
	 * @return
	 */
	public boolean isHasAuth(Menu menu,Menu firstMenu,BusinessSystem business){
		if(menu.getExternalable()!=null&&menu.getExternalable())return true;//表示当是外来系统，例如百度等时，不做权限控制直接显示
		if(isCustomSystem(menu)){//当是第1种情况时
			if(ContextUtils.isAuthority("/common/list.htm", "mms")){//表示有自定义系统的权限
				return true;
			}
		}else{
			if(ContextUtils.isAuthority(getMenuUrlWithoutParam(menu,firstMenu,business), menu.getSystemId())){
				if("ems".equals(firstMenu.getCode())){
					if(isHasEmsAuth(firstMenu)){
						return true;
					}else{
						return false;
					}
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	private Menu getChildLastMenu(List<Menu> children,Menu firstMenu,BusinessSystem system){
		for(Menu m:children){
			if(shouldGetSystem(firstMenu, system)){//是否需要重新获得系统
				system = businessSystemManager.getBusiness(m.getSystemId());
			}
			List<Menu> childrens=menuDao.getChildrenEnabledMenus(m.getId());
			if(isHasAuth(m,firstMenu,system)&&childrens.size()<=0){
				return m;
			}
			Menu menu =  getChildLastMenu(childrens,firstMenu,system);
			if(menu!=null){
				if(isHasAuth(menu,firstMenu,system)){
					return menu;
				}
			}
		}
		return null;
	}
	
	public Menu getGoldMenuByCode(String code, Long systemId, Long companyId) {
		return menuDao.getGoldMenuByCode(code, systemId, companyId);
	}
	/**
	 * 获得ems一级菜单下的第一个有权限的二级菜单
	 * @return
	 */
	public Menu getFirstSecMenuInEms(Menu emsMenu){
		List<Menu> childrens=menuDao.getChildrenEnabledMenus(emsMenu.getId());
		if(childrens.size()>0){
			return childrens.get(0);
		}
		return null;
	}
	/**
	 * 在具有DEMO-ALL资源的权限的前提下，是否具有启用的二级菜单，没有表示没有ems系统的权限
	 * @return
	 */
	private boolean isHasEmsAuth(Menu emsMenu){
		Menu menu = getFirstSecMenuInEms(emsMenu);
		if(menu!=null){
			return true;
		}
		return false;
	}
	
	public ModulePage getDefaultModulePageByMenu(Long menuId) {
		Menu menu=menuDao.get(menuId);
		ModulePage defaultPage=null;
		ModulePage page=modulePageDao.getDefaultDisplayPageByMenuId(menuId);
		if(page==null){
			List<ModulePage> pages=modulePageDao.getEnableModulePagesByMenuId(menuId);
			if(pages.size()>0)defaultPage=pages.get(0);
			if(defaultPage==null){
				List<Menu> menus=menuDao.getChildrenEnabledMenus(menu.getId());
				menu= (menus.size()<=0?null:menus.get(0));
				if(menu!=null){
					defaultPage=getDefaultModulePageByMenu(menu.getId());
					if(defaultPage!=null){
						return defaultPage;
					}
				}
			}
		}else{
			defaultPage=page;
		}
		return defaultPage;
	}
	
	/**
	 * 获得菜单menuId中层级为layer的默认选中的菜单
	 */
	public Menu getDefaultSelectMenuByLayer(Integer layer,Long menuId){
		Menu menu=getMenu(menuId);
		if(menu.getLayer()>=layer){//当前菜单的层级大于等于结果需要的层级时
			return getDefaultSelectMenuByUpLayer(layer,menu);
		}else{
			return getDefaultSelectMenuByLowerLayer(layer,menu);
		}
	}
	
	private Menu getDefaultSelectMenuByUpLayer(Integer layer,Menu menu){
		Integer menulayer = menu.getLayer();
		Menu resultMenu = menu;
		for(int i=menulayer;i>layer;i--){
			resultMenu = resultMenu.getParent();
		}
		return resultMenu;
	}
	private Menu getDefaultSelectMenuByLowerLayer(Integer layer,Menu menu){
		Integer menulayer = menu.getLayer();//2 
		Menu resultMenu = menu;
		for(int i=menulayer;i<layer;i++){
			List<Menu> children = resultMenu.getChildren();
			if(children.size()>0)resultMenu = children.get(0);
		}
		return resultMenu;
	}
	
	
	/**
	 * 获得选中的菜单列表
	 */
	public List<Menu> getSelectMenus(Long menuId,Menu firstMenu){
		Menu menu=getMenu(menuId);
		List<Menu> result = new ArrayList<Menu>();
		result.add(clone(menu));
		//获得当前菜单对应的父节点、父父节点...信息
		getDefaultSelectParentMenuInfo(menu,result);
		//获得当前菜单对应的孩子、孙子...信息
		getDefaultSelectChildrenMenuInfo(menu,result,firstMenu);
		sortMenuByLayer(result);
		return result;
	}
	/**
	 * 按层级升序排序
	 * @param result
	 */
	private void sortMenuByLayer(List<Menu> result){
		Collections.sort(result, new Comparator<Menu>() {
			public int compare(Menu menu1, Menu menu2) {
				if(menu1.getLayer()>menu2.getLayer()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private void getDefaultSelectParentMenuInfo(Menu menu,List<Menu> result){
		Menu parent = menu.getParent();
		while(parent!=null){
			result.add(clone(parent));
			parent = parent.getParent();
		}
	}
	private void getDefaultSelectChildrenMenuInfo(Menu menu,List<Menu> result,Menu firstMenu){
		Menu resultMenu = menu;
		BusinessSystem system = businessSystemManager.getBusiness(menu.getSystemId());
		List<Menu> children = resultMenu.getChildren();
		while(children.size()>0){//获得当前菜单的默认选中的孩子、子孩子...的集合
			boolean isHasAuth = false;
			for(Menu m:children){
				if(shouldGetSystem(m, system)){
					system = businessSystemManager.getBusiness(m.getSystemId());
				}
				if(isHasAuth(m,firstMenu,system)){
					resultMenu = m;
					result.add(clone(resultMenu));
					children = resultMenu.getChildren();
					isHasAuth= true;
				}
			}
			if(!isHasAuth)break;
		}
	}
	
	//得到子菜单及其所有父菜单
	public void getMenuParents(List<Menu> menus,Menu menu){
		if(menu!=null){
			menus.add(menu);
			getMenuParents(menus,menu.getParent());
		}
	}
	
	public List<Menu> getChildrenEnabledMenus(Long menuId){
		return menuDao.getChildrenEnabledMenus(menuId);
	}
	
	/**
	 * 根据父菜单id获得草稿或启用状态的子菜单
	 * @param menuId
	 * @return
	 */
	public List<Menu> getChildrenDraftOrEnabledMenus(Long menuId){
		return menuDao.getChildrenDraftOrEnabledMenus(menuId);
	}
	/**
	 * 获得启用的标准菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledStandardRootMenuByCompany() {
		return menuDao.getEnabledStandardRootMenuByCompany();
	}
	
	/**
	 * 获得启用的自定义菜单一级菜单集合
	 * @return
	 */
	public List<Menu> getEnabledCustomRootMenuByCompany() {
		return menuDao.getEnabledCustomRootMenuByCompany();
	}
	
	public String getSysUrl(Long systemId){
		Menu menu=menuDao.getSysMenu(systemId);
		if(menu!=null){
			String url=menu.getUrl();
			if(url.lastIndexOf("/")==url.length()-1){
				return url.substring(0,url.length()-1);
			}else{
				return url;
			}
		}
		return "";
	}
	
	/**
	 * 初始化一级菜单
	 */
	@Transactional(readOnly=false)
	public void initAllMenus(){
		List<BusinessSystem> bses=acsUtils.getAllBusiness(ContextUtils.getCompanyId());
		List<BusinessSystem> imatrixBs=acsUtils.getParentSystem();
		if(imatrixBs!=null){
			for(BusinessSystem sys:imatrixBs){
				if(!bses.contains(sys)){//底层系统中是否已经包括底层平台系统
					bses.add(sys);
				}
			}
		}
		List<Menu> menus=menuDao.getRootMenuByCompany();
		for(BusinessSystem bs:bses){
			boolean isHasMenu=false;
			Menu mn=null;
			for(Menu menu:menus){
				if(menu.getCode().equals(bs.getCode())){
					isHasMenu=true;
					mn=getMenu(menu.getId());
					break;
				}
			}
			if(!isHasMenu){
				mn=new Menu();
				mn.setType(MenuType.STANDARD);
				mn.setCompanyId(ContextUtils.getCompanyId());
				mn.setSystemId(bs.getId());
				mn.setCurrentSystemId(bs.getId());
				mn.setLayer(1);
				mn.setCode(bs.getCode());
				mn.setName(bs.getName());
				mn.setEnableState(DataState.ENABLE);
				mn.setUrl(bs.getPath());
				menuDao.save(mn);
			}
		}
	}
	/**
	 * 根据系统id获得一级菜单
	 * @return
	 */
	public Menu getDefaultMenuByLayer(String code){
		List<Menu> menus=menuDao.getDefaultMenuByLayer(1, code, ContextUtils.getCompanyId());
		if(menus.size()>0)return menus.get(0);
		return null;
	}
	/**
	 * 获得所有标准菜单
	 * @return
	 */
	public List<Menu> getAllMenus(){
		return menuDao.getAllMenus();
	}
	
	/**
	 * 获得所有菜单(标准和自定义)
	 * @return
	 */
	public List<Menu> getMenus(){
		return menuDao.getMenus();
	}
	
	public Menu getMenuByCode(String code){
		return menuDao.getMenuByCode(code);
	}
	public Menu getMenuByCodeWithoutCompany(String code){
		return menuDao.getMenuByCodeWithoutCompany(code);
	}
	public Menu getMenuByCode(String code,Long companyId){
		return menuDao.getMenuByCode(code,companyId);
	}
	public Menu getUnCompanyMenuByCode(String code){
		return menuDao.getUnCompanyMenuByCode(code);
	}
	
	public List<Menu> getMenuBySystem(String systemIds,Long companyId){
		return menuDao.getMenuBySystem(systemIds,companyId);
	}
	/**
	 * 获得该系统中第一个叶子菜单
	 * @param systemId
	 * @return
	 */
	public Menu getLeafMenuBySystem(Long systemId){
		return menuDao.getLeafMenuBySystem(systemId);
	}
	public Menu getParentMenu(String code) {
		Menu menu = menuDao.getMenuByCode(code);
		return menu.getParent();
	}
	public List<Menu> getChildren(String code) {
		Menu menu = menuDao.getMenuByCode(code);
		return menu.getChildren();
	}
	
	/**
	 *  根据menuId获得自定义系统为草稿或启用状态的一级菜单
	 * @param menuId
	 * @return
	 */
	public Menu getCustomRootMenuById(Long menuId) {
		return menuDao.getCustomRootMenuById(menuId);
	}
	/**
	 * 根据编号获得一级菜单
	 * @param code
	 * @return
	 */
	public Menu getRootMenuByCode(String code) {
		return menuDao.getRootMenuByCode(code, ContextUtils.getCompanyId());
	}
	/**
	 * 根据编号获得启用的一级菜单
	 * @param code
	 * @return
	 */
	public Menu getEnableRootMenuByCode(String code) {
		return menuDao.getEnableRootMenuByCode(code, ContextUtils.getCompanyId());
	}
	/**
	 * 根据id删除菜单
	 * @param id
	 */
	public void deleteMenu(Long id) {
		menuDao.delete(id);
	}
	
	public Menu getDefaultMenuByLayer(Integer layer, String code, MenuType menuType) {
		return menuDao.getDefaultMenuByLayer(layer, code, menuType);
	}
	
	public Menu getMenu(String menuCode, Integer menuLayer,
			String parentMenuCode, MenuType menuType) {
		return menuDao.menuDaogetMenu(menuCode,menuLayer,parentMenuCode,menuType);
	}
	/**
	 * 获得菜单menu是否有权限，有则返回当前菜单，没有权限则递归孩子节点中有权限的菜单，并返回
	 * @param menu
	 * @param systemCode 系统编码，当主子系统时该参数必须的
	 * @return
	 */
	public Menu getAuthMenu(Menu menu,BusinessSystem business,Menu firstMenu){
		if(menu==null)return null;
		boolean isAuth = isHasAuth(menu,firstMenu,business);
		if(isAuth)return menu;
		List<Menu> children = menuDao.getChildrenEnabledMenus(menu.getId());
		return getAuthChildMenu(children,firstMenu,business);
	}
	
	public String getMenuUrlWithoutParam(Menu menu,Menu firstMenu,BusinessSystem business){
		String url = menu.getUrl();
		if(url.contains("?")){
			url=url.substring(0,menu.getUrl().indexOf("?"));
		}
		String functionPath = url;
		String param = null;//获得参数
		if(business!=null){
			if(isFirstMenu(menu,firstMenu)){//表示是一级菜单
				//当是标准系统且该系统下没有二级菜单时，且地址为全路径，例如：http://192.168.1.51:8085/imatrix/portal/index/index.htm?aa=1
				String sysPath =business.getPath();
				if(sysPath.length()>0&&sysPath.lastIndexOf("/")==sysPath.length()-1){//去掉系统路径后的斜线/
					sysPath = sysPath.substring(0,sysPath.length()-1);
				}
				if(menu.getUrl().contains(sysPath)){//sales中的系统地址（http://192.168.1.51:8085/imatrix/portal）要和menu的地址前半部分一样（http://192.168.1.51:8085/imatrix/portal/index/index.htm）中配置的一样
					functionPath = menu.getUrl().substring(sysPath.length(),menu.getUrl().length());//得到 /index/index.htm?aa=1
					if(menu.getUrl().indexOf("?")>=0){
						functionPath = menu.getUrl().substring(sysPath.length(),menu.getUrl().indexOf("?"));//得到 /index/index.htm
						param = menu.getUrl().substring(menu.getUrl().indexOf("?")+1);//得到 /index/index.htm
					}
				}
			}
		}
		//设置路径和参数
		menu.setMenuFunctionPath(functionPath);
		menu.setMenuParam(param);
		return functionPath;
	}
	
	/**
	 * 当平台系统或使用了平台的系统，且有孩子节点的一级菜单或非一级菜单是否有权限
	 * @param menu
	 * @param firstMenu
	 * @param authUrl
	 * @return
	 */
	public String getHasAuthMenuUrl(Menu menu,Menu firstMenu,Menu authMenu){
		String authUrl = authMenu.getUrl();
		if(authMenu.getExternalable()!=null&&authMenu.getExternalable()){
			return authUrl;
		}
		//获得被拖动过的菜单的原属系统编码或没有被拖动过的菜单的系统编码
		String systemCode = getSystemCode(authMenu,firstMenu);
		if(isFirstMenu(menu,firstMenu)){//表示是一级菜单
			if("ems".equals(systemCode)){
				Menu emsMenu = getFirstSecMenuInEms(firstMenu);
				if(emsMenu!=null){
					authUrl = emsMenu.getUrl();
					return SystemUrls.getSystemPageUrl(systemCode)+authUrl;
				}
				return authUrl;
			}
			String functionPath = menu.getMenuFunctionPath()==null?"":menu.getMenuFunctionPath();
			String param = menu.getMenuParam()==null?"":menu.getMenuParam();
			String depModule = PropUtils.getProp("project.model");
			if(StringUtils.isNotEmpty(functionPath)){//说明是全路径,如：http://192.168.1.51:8085/imatrix/portal/index/index.htm
				if(!"developing.model".equals(depModule)){//产品模式下，直接返回当前一级菜单配置的地址
					return authUrl;
				}else{
					if(StringUtils.isNotEmpty(param)){
						return SystemUrls.getSystemPageUrl(systemCode)+functionPath+"?"+param;
					}else{
						return SystemUrls.getSystemPageUrl(systemCode)+functionPath;
					}
				}
			}else{//说明不是全路径，如：http://192.168.1.51:8085/imatrix/mms
				if(StringUtils.isNotEmpty(param)){
					return SystemUrls.getSystemPageUrl(systemCode)+authUrl+"?"+param;
				}else{
					return SystemUrls.getSystemPageUrl(systemCode)+authUrl;
				}
			}
		}else{
			return SystemUrls.getSystemPageUrl(systemCode)+authUrl;
		}
	}
	
	/**
	 * 获得被拖动过的菜单的原属系统编码
	 * @param menu
	 * @return
	 */
	
	private String getSystemCode(Menu menu,Menu firstMenu){
		String systemCode = null;
		if(isCustomSystem(menu))systemCode="imatrix";
		if(StringUtils.isEmpty(systemCode))systemCode = getOriginalSystemCode(menu);
		if(StringUtils.isEmpty(systemCode)){
			systemCode = firstMenu.getCode();
		}
		return systemCode;
	}
	
	/**
	 * 获得被拖动过的菜单的原属系统编码
	 * @param menu
	 * @return
	 */
	
	private String getOriginalSystemCode(Menu menu){
		String systemCode = null;
		if(isDrag(menu)){//拖动过
			//获得被拖动过的菜单的原属系统
			BusinessSystem originalSystem = businessSystemManager.getBusiness(menu.getSystemId());
			if(originalSystem!=null)systemCode = originalSystem.getCode();
		}
		return systemCode;
	}
	
	/**
	 * 是否拖动过，true表示被拖动了
	 * @param menu
	 * @return true表示被拖动了
	 */
	private boolean isDrag(Menu menu){
		if(!menu.getSystemId().equals(menu.getCurrentSystemId())) return true;
		return false;
	}
	
	/**
	 * 设置一级菜单的url，处理参数
	 * @param menu
	 */
	public String getHasAuthFirstMenuUrl(Menu menu,Menu firstMenu,Long lastMenuId){
		String menuUrl = getMenuUrl(menu,firstMenu,lastMenuId);
		if(!menuUrl.contains("_r=1")){//不包含刷新样式的格式参数则需要添加
			//刷新样式用到
			if(menuUrl.contains("?")){
				menuUrl = menuUrl+"&_r=1";
			}else{
				menuUrl = menuUrl+"?_r=1";
			}
		}
		return menuUrl;
	}
	
	public String getMenuUrl(Menu menu,Menu firstMenu,Long lastMenuId){
		String menuUrl = menu.getMenuUrl();
		if(StringUtils.isEmpty(menuUrl)){
			menuUrl = getHasAuthMenuUrl(menu,firstMenu,menu);
		}
		if((menu.getExternalable()==null||!menu.getExternalable())){//表示不是外部系统时
			menuUrl = getMenuUrlByMenu(menuUrl,lastMenuId);
		}
		return menuUrl;
	}
	
	/**
	 * 处理菜单id参数
	 * @param menuUrl
	 * @param lastMenuId
	 * @return
	 */
	private String getMenuUrlByMenu(String menuUrl,Long lastMenuId){
		if(menuUrl.indexOf("menuId=")>=0){//包含menuId参数
			if(menuUrl.indexOf("?menuId=")>=0){
				menuUrl = menuUrl.substring(0,menuUrl.indexOf("?menuId="));
			}else if(menuUrl.indexOf("&menuId=")>=0){
				menuUrl = menuUrl.substring(0,menuUrl.indexOf("&menuId="));
			}
		}
		if(lastMenuId!=null){
			if(menuUrl.indexOf("?")>=0){
				menuUrl = menuUrl+"&menuId="+lastMenuId;
			}else{
				menuUrl = menuUrl+"?menuId="+lastMenuId;
			}
		}
		return menuUrl;
	}
	
	
	
	 /**
	 * 是否是一级菜单
	 * @param menu
	 * @param firstMenu
	 * @return
	 */
	public boolean isFirstMenu(Menu menu,Menu firstMenu){
		return menu.getId().equals(firstMenu.getId());
	}
	
	private Menu getAuthChildMenu(List<Menu> children,Menu firstMenu,BusinessSystem business){
		for(Menu m:children){
			if(shouldGetSystem(m,business)){//是否需要重新获得系统
				business = businessSystemManager.getBusiness(m.getSystemId());
			}
			boolean isAuth = isHasAuth(m,firstMenu,business);
			if(isAuth)return m;
			//如果没有权限则递归查询孩子节点是否有权限
			List<Menu> childrens = menuDao.getChildrenEnabledMenus(m.getId());
			Menu authMenu = getAuthChildMenu(childrens,firstMenu,business);
			if(authMenu!=null)return authMenu;
		}
		return null;
	}
	/**
	 * 是否需要重新获得系统
	 * 为了提高性能
	 * @param menu
	 * @return true表示需要重新获得系统
	 */
	private boolean shouldGetSystem(Menu menu,BusinessSystem business){
		if(!business.getId().equals(menu.getSystemId())) return true;
		return false;
	}
	/**
	 * 由于菜单的menu和parent配置使得在使用JsonParser.object2Json(resultMenus)时会出现死循环，所以使用该方法重新设置菜单属性
	 * @param menu
	 * @return
	 */
	public Menu clone(Menu menu){
		Menu tempMenu = new Menu();
		tempMenu.setId(menu.getId());
		tempMenu.setUrl(menu.getUrl());
		tempMenu.setMenuUrl(menu.getMenuUrl());
		tempMenu.setOpenWay(menu.getOpenWay());
		tempMenu.setEvent(menu.getEvent());
		tempMenu.setLayer(menu.getLayer());
		tempMenu.setCode(menu.getCode());
		tempMenu.setType(menu.getType());
		tempMenu.setImageUrl(menu.getImageUrl());
		tempMenu.setName(menu.getName());
		tempMenu.setLastMenuId(menu.getLastMenuId());
		tempMenu.setSubCompanyId(menu.getSubCompanyId());
		tempMenu.setSystemId(menu.getSystemId());
		tempMenu.setCurrentSystemId(menu.getCurrentSystemId());
		tempMenu.setFunctionId(menu.getFunctionId());
		tempMenu.setMenuFunctionPath(menu.getMenuFunctionPath());
		tempMenu.setMenuParam(menu.getMenuParam());
		tempMenu.setExternalable(menu.getExternalable());
		return tempMenu;
	}

	/**
	 * 获得三级菜单下有5级菜单及孩子的菜单的菜单集合
	 * @param currentMenuId
	 * @return
	 */
	public List<Menu> getThirdMenusChildren(Long currentThirdMenuId){
		List<Menu> result = new ArrayList<Menu>();
		Menu currentMenu = getMenu(currentThirdMenuId);
		//获得当前菜单的子菜单集合
		getMenuChildrens(result,currentMenu);
		return result;
	}
	
	
	private void getMenuChildrens(List<Menu> result,Menu currentMenu){
		Map<String,Boolean> shouldCreateTree = new HashMap<String,Boolean>();
		if(currentMenu!=null&&currentMenu.getEnableState()==DataState.ENABLE){
			Long menuId = currentMenu.getId();
			List<Menu> childrens = menuDao.getChildrenEnabledMenus(menuId);
			result.addAll(childrens);
			getMenuChildren(childrens,result,shouldCreateTree);
		}
		boolean canCreateTree = shouldCreateTree.get("shouldCreateTree")==null?false:shouldCreateTree.get("shouldCreateTree");
		if(!canCreateTree)result.clear();
	}
	
	private void getMenuChildren(List<Menu> childrens,List<Menu> result,Map<String,Boolean> shouldCreateTree){
		for(Menu menu:childrens){
			if(menu.getLayer()>4){//当前三级菜单下有5级及更多子菜单
				shouldCreateTree.put("shouldCreateTree", true);
			}
			List<Menu>  mychildrens = menuDao.getChildrenEnabledMenus(menu.getId());
			result.addAll(mychildrens);
			getMenuChildren(mychildrens,result,shouldCreateTree);
		}
	}
	@Transactional
	public void moveNodes(String msgs, String targetId, String moveType) {
		String[] ids=msgs.split(";");
		Integer len=ids.length;
		Long id=null;
		String menuIds="";
		Menu menu=null;
		Menu target=null;
		target=menuDao.get(Long.parseLong(targetId));
		for(int i=0;i<ids.length;i++){
			String[] msg=ids[i].split(",");
			id=Long.parseLong(msg[0]);
			menuIds=menuIds+id+(i==ids.length-1?"":",");
			menu=menuDao.get(id);
			if(moveType.equals("inner")){
				menu.setParent(target);
				menu.setLayer(target.getLayer()+1);
				menu.setDisplayOrder(getMaxDisplayOrderByPid(target.getId())+3);
			}else if(moveType.equals("prev")){
				menu.setParent(target.getParent());
				menu.setLayer(target.getLayer());
				menu.setDisplayOrder(target.getDisplayOrder()+i);
			}else if(moveType.equals("next")){
				menu.setParent(target.getParent());
				menu.setLayer(target.getLayer());
				menu.setDisplayOrder(target.getDisplayOrder()+i+1);
			}
			menu.setCurrentSystemId(target.getCurrentSystemId());
			setChildLayer(menu);
		}
		sort(moveType,target,len,menuIds);
		
	}
	private void setChildLayer(Menu parent) {
		List<Menu> temp=new ArrayList<Menu>();
		temp=menuDao.find("from Menu m where m.parent.id=? order by m.displayOrder,m.id",parent.getId());
		for(Menu menu:temp){
			menu.setLayer(parent.getLayer()+1);
			menu.setCurrentSystemId(parent.getCurrentSystemId());
			setChildLayer(menu);
		}
	}
	//对受拖拽影响的节点重新排序
	private void sort(String moveType,Menu target,Integer len,String ids) {
		Menu parent=target.getParent();
		if(moveType.equals("prev")){
			if(parent==null){
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>=? and m.parent is null and m.id not in ("+ids+")", target.getDisplayOrder()).executeUpdate();
			}else{
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>=? and m.parent.id=? and m.id not in ("+ids+")", target.getDisplayOrder(),parent.getId()).executeUpdate();
			}
		}else if(moveType.equals("next")){
			if(parent==null){
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>? and m.parent is null and m.id not in ("+ids+")", target.getDisplayOrder()).executeUpdate();
			}else{
				menuDao.createQuery("update Menu m set m.displayOrder=(m.displayOrder+"+(len+3)+") where m.displayOrder>? and m.parent.id=? and m.id not in ("+ids+")", target.getDisplayOrder(),parent.getId()).executeUpdate();
			}
		}
	}
	//获取子菜单最大序号
	private Integer getMaxDisplayOrderByPid(Long id) {
		return menuDao.findUnique("select max(m.displayOrder) from Menu m where m.parent.id=?",id);
	}
	//获取菜单下所有的菜单
	public void getMenuByPid(List<Menu> menus,Long currentId) {
		List<Menu> temp=new ArrayList<Menu>();
		temp=menuDao.find("from Menu m where m.parent.id=? order by m.displayOrder,m.id",currentId);
		for(Menu menu:temp){
			getMenuByPid(menus,menu.getId());
		}
		menus.addAll(temp);
	}
	
	/**
	  * 验证菜单编码是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isMenuCodeExist(String code,Long menuId){
		 Menu menu=menuDao.getMenuByCode(code);
		 if(menu==null){
			 return false;
		 }else{
			 if(menuId==null)return true;
			 if(menu.getId().equals(menuId)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	 }
}
