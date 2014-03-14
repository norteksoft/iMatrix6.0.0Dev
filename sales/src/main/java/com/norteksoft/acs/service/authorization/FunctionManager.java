package com.norteksoft.acs.service.authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.base.utils.log.LogUtilDao;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.mms.base.OpenWay;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.WebContextUtils;
import com.norteksoft.product.util.tree.ZTreeNode;


/**
*资源管理roleName
*@author 陈成虎
*2009-3-2上午11:40:38
*/
@Service
@Transactional
public class FunctionManager{
	
	private static String DELETED = "deleted";
	private static String BUSINESSSYSTEM_ID ="businessSystem.id";
	private static String FUNCTIONGROUP_ID ="functionGroup.id";
	private static String ROLE_ID ="role.id";
	private static String FUNCTION_ID = "function.id";
	private static String COMPANYID = "companyId";
	private static String NAME ="name";
	private static String COMPANY_ID ="company.id";
	private static String hql = "from Function f where f.deleted=? and f.businessSystem.id=?";
	private static String customRolehql = "select role from CustomRole role join role.role_Function r_f where r_f.function.id=? and r_f.companyId=? and role.deleted=? and r_f.deleted=? ";
	
	private SimpleHibernateTemplate<Function, Long> functionDao;	
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<RoleFunction, Long> role_fDao;
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<BusinessSystem, Long> businessSystemDao;
	private SimpleHibernateTemplate<Menu, Long> menuDao;
	private SimpleHibernateTemplate<Company, Long> companyDao;
	private LogUtilDao logUtilDao;
	
	
	private Long companyId;
	
	public Long getCompanyId() {
		if(companyId == null){
			return WebContextUtils.getCompanyId();
		}else 
			return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		functionDao = new SimpleHibernateTemplate<Function, Long>(sessionFactory, Function.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(sessionFactory, Role.class);
		role_fDao = new SimpleHibernateTemplate<RoleFunction, Long>(sessionFactory,RoleFunction.class);
		logUtilDao = new LogUtilDao(sessionFactory);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(sessionFactory,FunctionGroup.class);
		businessSystemDao = new SimpleHibernateTemplate<BusinessSystem, Long>(sessionFactory,BusinessSystem.class);
		menuDao = new SimpleHibernateTemplate<Menu, Long>(sessionFactory,Menu.class);
		companyDao=new SimpleHibernateTemplate<Company, Long>(sessionFactory,Company.class);
	}
	/**
	 * 查询所有资源信息
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Function> getAllFunction(){
		return functionDao.findByCriteria(Restrictions.eq(DELETED, false));

	}
	
	/**
	 * 获取单条资源信息
	 */
	@Transactional(readOnly = true)
	public Function getFunction(Long id) {
		List<Function> funs =  functionDao.find("from Function f where f.id=?", id);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	
	/**
	 * 角色添加功能 
	 */
	@Transactional(readOnly = true)
	public List<FunctionGroup> listFunctions(Long sysId){
		return functionGroupDao.findByCriteria(
				 Restrictions.eq("businessSystem.id", sysId), Restrictions.eq("deleted", false));
	}
	/**
	 * 分页查询所有资源信息
	 */
	@Transactional(readOnly = true)
	public Page<Function> getAllFunction(Page<Function> page, Long sysId) {
			return functionDao.findByCriteria(
					page,Restrictions.eq(DELETED, false),Restrictions.eq(BUSINESSSYSTEM_ID, sysId));
	}
	/**
	  * 保存资源信息
	  */	
	public void saveFunction(Function function){
		functionDao.save(function);
	}
	
	/**
	  * 保存资源信息
	  */	
	public void saveFunction(Function function,Boolean ischenge){
		Integer maxOrdinal=null;
		Long parentId=function.getPid();
		if(function.getId()!=null){
			if(parentId==null){
				if(!function.getIsmenu()){
					function.setMenulevel(1);
					if(ischenge){
						maxOrdinal=functionDao.findInt("select max(f.ordinal) from Function f  where f.ismenu=? and f.deleted=? and f.pid is null",false,false);
						function.setOrdinal(maxOrdinal==null?0:maxOrdinal+1);
					}
				}
			}
			functionDao.save(function);
		}else{
			if(parentId==null){
				function.setMenulevel(1);
				if(function.getIsmenu()){
						maxOrdinal=functionDao.findInt("select max(f.ordinal) from Function f  where f.ismenu=? and f.deleted=? and f.pid is null",true,false);
						function.setOrdinal(maxOrdinal==null?0:maxOrdinal+1);
				}else{
					maxOrdinal=functionDao.findInt("select max(f.ordinal) from Function f where f.ismenu=? and f.deleted=? and f.pid is null",false,false);
					function.setOrdinal(maxOrdinal==null?0:maxOrdinal+1);
				}
				
			}else{
				function.setMenulevel(functionDao.get(parentId).getMenulevel()+1);
				maxOrdinal=(Integer)functionDao.findUnique("select max(f.ordinal) from Function f where f.deleted=? and f.pid=?",false,parentId);
				function.setOrdinal(maxOrdinal==null?0:maxOrdinal+1);
				
			}
			functionDao.save(function);
		}
	}
	/**
	 * 删除资源信息
	 */
	public void deleteFunction(Long id) {
		Function function = functionDao.get(id);
		function.setDeleted(true);
		functionDao.save(function);
	}
	/**
	 * 批量删除
	 */
	public void deleteFunction(String ids) {
		functionDao.createQuery("update Function f set f.deleted=? where f.id in ("+ids+")", true).executeUpdate();
	}
	/**
	 * 按条件检索资源
	 */
	@Transactional(readOnly = true)
	public Page<Function> getSearchFunction(Page<Function> page,Long bsId, Function function,boolean deleted) {
         StringBuilder functionHql = new StringBuilder(hql);
		 if (function != null) {
			String functionId = function.getCode().trim();
			String functionName = function.getName().trim();
			String functionPath=function.getPath().trim();
			
			if (!"".equals(functionId) && !"".equals(functionName)&&!"".equals(functionPath)) {
				functionHql.append(" and f.code like ?");
				functionHql.append(" and f.name like ?");
				functionHql.append(" and f.path like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionId+ "%", "%" + functionName + "%","%" + functionPath + "%");
			}
			
			if (!"".equals(functionId) && !"".equals(functionName)) {
				functionHql.append(" and f.code like ?");
				functionHql.append(" and f.name like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionId+ "%", "%" + functionName + "%");
			}
			
			if (!"".equals(functionPath) && !"".equals(functionName)) {
				functionHql.append(" and f.path like ?");
				functionHql.append(" and f.name like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionPath+ "%", "%" + functionName + "%");
			}
			
			if (!"".equals(functionId) && !"".equals(functionPath)) {
				functionHql.append(" and f.code like ?");
				functionHql.append(" and f.path like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionId+ "%", "%" + functionPath + "%");
			}
			
			if (!"".equals(functionPath)) {
				functionHql.append(" and f.path like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionPath+ "%");
			}
			
			if (!"".equals(functionId)) {
				functionHql.append(" and f.code like ?");
				return functionDao.find(page, functionHql.toString(),
						                false, bsId, "%" + functionId+ "%");
			}
			
			if (!"".equals(functionName)) {
				functionHql.append(" and f.name like ?");
				return functionDao.find(page, functionHql.toString(),
						                false,  bsId, "%" + functionName + "%");
			}
		}
        
		return functionDao.find(page, hql, false, bsId);
	}
	  
    /**
	 *查询资源添加的角色
	 */
	public Page<Role> functionToRoleList(Page<Role> page, Role entity,Long sysId) {

		if (entity != null) {

			String roleName = entity.getName();
			if (roleName != null && !"".equals(roleName)) {

				return roleDao.findByCriteria(page, 
											  Restrictions.eq(BUSINESSSYSTEM_ID, sysId), 
											  Restrictions.like(NAME, "%" + roleName + "%"),
											  Restrictions.eq(DELETED, false));

			}

		}

		return roleDao.findByCriteria(page, 
				                      Restrictions.eq(BUSINESSSYSTEM_ID, sysId), 
				                      Restrictions.eq(COMPANY_ID, getCompanyId()),
				                      Restrictions.eq(DELETED, false));

	}
	  /**
	   * 
	   * 查询资源要移除的角色
	   */
	  public Page<Role> functionToRomoveRoleList(Page<Role> page,Role entity,Long sysId,Long funId){
		 
		  if(entity!=null){
	    	  
			  String roleName = entity.getName();
	    	  if(roleName!=null&&!"".equals(roleName)){
	    		  StringBuilder hqL = new StringBuilder(customRolehql);
	    		  hqL.append(" and role.name like ? ");
	    		  return roleDao.find(page, hqL.toString(), funId,getCompanyId(),false,false,"%"+roleName+"%");
                                               
	    	 }
	    	
	     }
	    
		  return roleDao.find(page, customRolehql, funId,getCompanyId(),false,false);
	
	  }
	  
	  public List<Function> getFunctionByFunctionGruopId(Long functionGroupId){
		  
		  return functionDao.findByCriteria( Restrictions.eq(FUNCTIONGROUP_ID, functionGroupId), Restrictions.eq(DELETED, false));
		  
	  }
	 
	//查处资源拥有的角色
	@SuppressWarnings("unchecked")
	public List<Long> getRoleIds(Long function_Id){
		  List<Long> functionIds = new ArrayList<Long>();
		  List<RoleFunction> role_f = role_fDao.findByCriteria(Restrictions.eq(FUNCTION_ID, function_Id),
										                        Restrictions.eq(COMPANYID, getCompanyId()),
											                    Restrictions.eq(DELETED, false));
		  for (Iterator iterator = role_f.iterator(); iterator.hasNext();) {
			  RoleFunction role_Function = (RoleFunction) iterator.next();
			functionIds.add(role_Function.getRole().getId());
			
		}
		  return functionIds;
	  }
	  //保存资源和角色的关系
	  public void functionAddRole(Long function_Id,List<Long> roleIds,Integer isAdd){
		//查出要加入角色的资源
		  Function function = getFunction(function_Id);
		 StringBuilder roleName = new StringBuilder();
		// 资源添加角色
		 if(isAdd==0){
			 RoleFunction role_Function;
			 Role role = null;
			  for (Long rId : roleIds) {
				  role_Function = new RoleFunction();
				  role = roleDao.get(rId);
				  role_Function.setRole(role);
				  role_Function.setFunction(function);
 
				  role_Function.setCompanyId(getCompanyId());
				  role_fDao.save(role_Function);
				  roleName.append(role.getName());
				  roleName.append(",");
			} 
			  roleName.deleteCharAt(roleName.length()-1);
		 }
		// 资源移除角色
		 if(isAdd==1){
			 
			 List<RoleFunction>  list = role_fDao.findByCriteria(Restrictions.in(ROLE_ID, roleIds),
											                      Restrictions.eq(FUNCTION_ID, function_Id),
											                      Restrictions.eq(COMPANYID, getCompanyId()),
											                      Restrictions.eq(DELETED, false));
											                     
			 for (RoleFunction role_Function : list) {
				// 根据选中的角色ID查处资源具有的角色
				
				 //改变删除标志字段
				 role_Function.setDeleted(true);
				 role_fDao.save(role_Function);
				 roleName.append(role_Function.getRole().getName());
				 roleName.append(",");
			}
		    roleName.deleteCharAt(roleName.length()-1);
		 }
		  
	  }
	  
	public Page<Function> getFunctionsByFunctionGroup(Page<Function> page, Long functionGroupId) {
		return functionDao.findByCriteria(page, Restrictions.eq(FUNCTIONGROUP_ID, functionGroupId));
	}
	
	public SimpleHibernateTemplate<Function, Long> getFunctionDao() {
		return functionDao;
	}

	public SimpleHibernateTemplate<RoleFunction, Long> getRole_fDao() {
		return role_fDao;
	}
	
	/**
	 * 获取系统下所有的资源(分页)
	 */
	public Page<Function> getFunctionsBySystem(Page<Function> page, Long systemId){
		return functionDao.find(page, "from Function f where f.businessSystem.id=? and f.deleted=?", systemId, false);
	}
	
	/**
	 * 获取系统下所有的资源
	 */
	public List<Function> getFunctionsBySystem(Long systemId){
		return functionDao.findList("from Function f where businessSystem.id=? and f.deleted=? order by f.menulevel", systemId, false);
	}
	
	/**
	 * 获取资源组中能移除的的资源
	 */
	public Page<Function> getFunctionsCanRemoveFromFunctionGroup(Page<Function> page, Long functionGroupId){
		return functionDao.findByCriteria(page, Restrictions.eq(FUNCTIONGROUP_ID, functionGroupId),
				Restrictions.eq(DELETED, false));
	}
	
	public List<Function> getFunctionsByGroup(Long functionGroupId) {
		return functionDao.find("from Function f where f.functionGroup.id=?", functionGroupId);
	}
	public List<Function> getUnGroupFunctions(Long systemId) {
		return functionDao.find("from Function f where f.functionGroup=null and (f.businessSystem!=null and f.businessSystem.id=?)", systemId);
	}
	
	public Function getFunctionByPath(String path,Long systemId,String funId){
		List<Function> funs= functionDao.find("from Function f where f.path=? and f.code=? and (f.businessSystem!=null and f.businessSystem.id=?)", path,funId,systemId);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Function getFunctionByPath(String path,Long systemId){
		List<Function> funs= functionDao.find("from Function f where f.path=? and (f.businessSystem!=null and f.businessSystem.id=?)", path,systemId);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	
	/**
	 * 验证角色编号，不能重复
	 * @param entity
	 * @return
	 */
	public boolean validateRoleCode(Role entity) {
		StringBuilder hql = new StringBuilder();
		List<Role> rolses = new ArrayList<Role>();
		hql.append("from Role r where r.code=? and r.deleted=?");
		if(entity.getId()!=null){
			hql.append(" and r.id!=? ");
			rolses = roleDao.find(hql.toString(), entity.getCode(),false,entity.getId());
		}else{
			rolses = roleDao.find(hql.toString(), entity.getCode(),false);
		}
		if(rolses.size()>0){
			return false;
		}else{
			return true;
		}
		
	}
	
	/**
	 *  创建资源树
	 * @param systemId
	 * @return
	 */
	public String createFunctionTree(Long systemId) {
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		BusinessSystem bs=businessSystemDao.get(systemId);
		List<Function> functions=functionDao.findList("from Function f where f.businessSystem.id=? and  ismenu=? and pid is null and deleted=? order by ordinal,name",systemId,true,false);
		//系统节点
		ZTreeNode node=new ZTreeNode("business_"+bs.getId().toString(),"0", bs.getName(),"true","true",bs.getPath(),"","system","system","false","true","false","true","false");
		if(functions!=null&&functions.size()==0){
			node.setIsParent("false");
		}
		ztreeNodes.add(node);
		for(Function function:functions){
			List<Function> cf=functionDao.findList("from Function f where f.businessSystem.id=? and pid=? and ismenu is not null and deleted=? order by ordinal,name", systemId,function.getId(),false);
			if(cf!=null&&cf.size()>0){
				node=new ZTreeNode(function.getId().toString(),"business_"+bs.getId().toString(), function.getName(),"true","true",bs.getPath(),bs.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
				appendNodes(cf,ztreeNodes,systemId,node.getId().toString());
			}else{
				node=new ZTreeNode(function.getId().toString(),"business_"+bs.getId().toString(), function.getName(),"false","false",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
			}
			ztreeNodes.add(node);
		}
		//未分类节点
		node=new ZTreeNode("void_"+bs.getId().toString(),"0", "未分类","false","false","root",bs.getPath(),"void","void","false","true","false","true","false");
		functions=functionDao.findList("from Function f where f.businessSystem.id=? and  ismenu=? and pid is null and deleted=? order by ordinal,name",systemId,false,false);
		if(functions!=null&&functions.size()>0){
			node.setIsParent("true");
			node.setOpen("true");
		}
		ztreeNodes.add(node);
		for(Function function:functions){
			node=new ZTreeNode(function.getId().toString(),"void_"+bs.getId().toString(), function.getName(),"false","false",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","true","false","true","true","true");
			appendNodes(ztreeNodes,function,node);
		}
		return JsonParser.object2Json(ztreeNodes);
	}
	/**
	 * 拼接未分类下面的节点
	 * @param function
	 */
	private void appendNodes(List<ZTreeNode> ztreeNodes,Function function,ZTreeNode node) {
		List<Function> functions=null;
		functions=getFunctionsByPid(function.getId());
		if(functions.size()>0){
			node.setIsParent("true");
			node.setOpen("true");
		}
		ztreeNodes.add(node);
		for(Function f:functions){
			node=new ZTreeNode(f.getId().toString(),function.getId().toString(), f.getName(),"false","false",f.getPath(),f.getCode(),getStringType(f),getStringType(f),"true","false","true","true","true");
			appendNodes(ztreeNodes,f,node);
		}
	}

	/**拼接已分类下面的节点
	 * 
	 * @param functions
	 * @param ztreeNodes
	 * @param systemId
	 * @param pid
	 */
	private void appendNodes(List<Function> functions, List<ZTreeNode> ztreeNodes,Long systemId,String pid) {
		List<Function> cfs=new ArrayList<Function>();
		ZTreeNode node=null;
		for(Function function:functions){
			if(function.getIsmenu()){
				cfs=functionDao.findList("from Function f where f.businessSystem.id=? and pid=? and ismenu is not null and deleted=? order by ordinal,name", systemId,function.getId(),false);
				if(cfs.size()>0){
					node=new ZTreeNode(function.getId().toString(),pid, function.getName(),"true","true",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
					appendNodes(cfs,ztreeNodes,systemId,function.getId().toString());
				}else{
					node=new ZTreeNode(function.getId().toString(),pid, function.getName(),"false","false",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
				}
			}else{
				cfs=functionDao.findList("from Function f where f.businessSystem.id=? and f.pid=? and f.ismenu=? and deleted=? order by ordinal,name", systemId,function.getId(),false,false);
				if(cfs.size()>0){
					node=new ZTreeNode(function.getId().toString(),pid, function.getName(),"true","true",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
					appendNodes(cfs,ztreeNodes,systemId,function.getId().toString());
				}else{
					node=new ZTreeNode(function.getId().toString(),pid, function.getName(),"false","false",function.getPath(),function.getCode(),function.getIsmenu()?"menu":"function",function.getIsmenu()?"menu":"function","");
				}
			}
			ztreeNodes.add(node);
		}
	}
	/**
	 * 移动节点时查找移动后的最大序数
	 * @param parent
	 * @param moveType
	 * @param target
	 * @param systemId
	 * @return
	 */
	private Integer getMaxOrdinal(Function parent,String moveType,Function target,Long systemId){
		Integer maxOrdinal=0;
		if(moveType.equals("inner")){
			if(parent==null){
				maxOrdinal=functionDao.findInt("select max(f.ordinal) from Function f where f.deleted=? and f.pid is null and f.businessSystem.id=?",false,systemId);
			}else{
				maxOrdinal=(Integer)functionDao.findUnique("select max(f.ordinal) from Function f where f.deleted=? and f.pid=? and f.businessSystem.id=?",false,parent.getId(),systemId);
			}
		}else if(moveType.equals("prev")){
			if(parent==null){
				maxOrdinal=(Integer)functionDao.findUnique("select max(f.ordinal) from Function f where f.deleted=? and f.pid is null and f.ordinal<? and f.businessSystem.id=?",false,target.getOrdinal(),systemId);
			}else{
				maxOrdinal=(Integer)functionDao.findUnique("select max(f.ordinal) from Function f where f.deleted=? and f.pid=? and f.ordinal<? and f.businessSystem.id=?",false,parent.getId(),target.getOrdinal(),systemId);
			}
			
		}else if(moveType.equals("next")){
			maxOrdinal=target.getOrdinal();
		}
		return (maxOrdinal==null?0:maxOrdinal);
		
	}
	/**
	 * 执行拖拽后的后台处理方法
	 */
	public void execute(String msg, String targetId, String moveType,Long systemId) {
		String[] ids=msg.split(";");
		String addIds="";
		Integer maxOrdinal=0;
		int len=ids.length;
		String[] temp;
		String id="";
		//String ordinal="";
		String level="";
		Function function=null;
		Function parent=null;
		Function target=null;
		if(moveType.equals("inner")){
			parent=targetId.matches("^[1-9]\\d*")?functionDao.get(Long.parseLong(targetId)):null;
			target=parent;
		}else{
			target=functionDao.get(Long.parseLong(targetId));
			parent=(target.getPid()==null?null:functionDao.get(target.getPid()));
		}
		
		maxOrdinal=getMaxOrdinal(parent,moveType,target,systemId);
		for(int i=0;i<len;i++){
			temp=ids[i].split(",");
			id=temp[0];
			//ordinal=temp[1];
			level=temp[2];
			function=functionDao.get(Long.parseLong(id));
			if(parent!=null){
				function.setMenulevel(parent.getMenulevel()+1);
				function.setPid(parent.getId());
			}else{
				function.setPid(null);
				function.setMenulevel(1);
			}
			setSubMenuLevel(Integer.parseInt(level),function.getId(),function.getBusinessSystem().getId());
			function.setOrdinal(maxOrdinal+1+i);
			setChildLayer(function);
			addIds=addIds+id+(i==len-1?"":",");
		}
		if(parent==null){
			functionDao.createQuery("update Function f set f.ordinal=(f.ordinal+"+len+2+") where f.deleted=? and f.pid is null and f.ordinal>? and f.businessSystem.id=? and id not in("+addIds+")" ,false,maxOrdinal,systemId).executeUpdate();
		}else{
			functionDao.createQuery("update Function f set f.ordinal=(f.ordinal+"+len+2+") where f.deleted=? and f.pid=?  and f.ordinal>? and f.businessSystem.id=?  and id not in("+addIds+")",false,parent.getId(),maxOrdinal,systemId).executeUpdate();
		}
	}
	private void setChildLayer(Function parent) {
		List<Function> temp=new ArrayList<Function>();
		temp=functionDao.findList("from Function f where f.pid=? and f.deleted=? order by f.ordinal",parent.getId(),false);
		for(Function f:temp){
			f.setMenulevel(parent.getMenulevel()+1);
			setChildLayer(f);
		}
	}
	@SuppressWarnings("unused")
	private void setRemoveNodeState(String msg,Long systemId) {
		List<Function> fs=functionDao.findList("from Function f where f.deleted=? and f.businessSystem.id=? and id in ("+msg+")", false,systemId);
		toVoidFunction(fs);
	}

	private void toVoidFunction(List<Function> fs) {
		List<Function> c=new ArrayList<Function>();
		for(Function f:fs){
			c=getFunctionsByPid(f.getId());
			if(c.size()>0){
				toVoidFunction(c);
			}
			f.setPid(null);
			f.setMenulevel(null);
			f.setOrdinal(null);
		}
		
	}

	private void setSubMenuLevel(Integer plevel,Long id,Long systemId) {
		List<Function> functions=functionDao.findList("from Function f where f.deleted=? and f.pid=? and f.businessSystem.id=?", false,id,systemId);
		for(Function function:functions){
			function.setMenulevel(plevel+1);
			setSubMenuLevel(plevel+1,function.getId(),systemId);
		}
	}

	public List<Function> getFunctionsByPid(Long id) {
		List<Function> functions=functionDao.findList("from Function f where f.deleted=? and f.pid=? order by f.ordinal,f.name ", false,id);
		return functions;
	}
	//获取跟目录下function 的最大序号
	public Integer getLastOrdinal(Long systemId) {
		Integer temp=functionDao.findInt("select max(f.ordinal) from Function f where f.deleted=? and f.pid is null and f.businessSystem.id=?",false,systemId);
		return temp==null?0:temp+1;
	}
	//获取未分类下function 的最大序号
	public Integer getLastOrdinalAsVoid(Long systemId) {
		Integer temp=functionDao.findInt("select max(f.ordinal) from Function f where f.deleted=? and f.ismenu=? and f.pid is null and f.businessSystem.id=?",false,false,systemId);
		return temp==null?0:temp+1;
	}

	public void asyncMenu(Long systemId) {
		List<Function> functions=functionDao.findList("from Function f where f.deleted=? and f.ismenu=? and businessSystem.id=? order by f.menulevel", false,true,systemId);
		List<Company> companys=companyDao.findList("from Company c where c.deleted=?", false);
		for(Company c:companys){
			List<Menu> menus=menuDao.findList("from Menu m where m.systemId=? and m.type=? and m.parent is not null and m.companyId=? and m.externalable=?", systemId,MenuType.STANDARD,c.getId(),false);
			List<Function> adds=new ArrayList<Function>();
			List<Menu> dels=new ArrayList<Menu>();
			pushValue(adds,dels,functions,menus,c);
			if(hasMenuBySys(systemId,c)){
				deleteMenu(dels,systemId,c);
				addMenu(adds,systemId,c);
			}
		}
	}

	private void addMenu(List<Function> adds,Long systemId,Company c) {
		Long pid=null;
		Menu menu=null;
		Menu parent=null;
		for(Function f:adds){
			menu=new Menu();
			menu.setName(f.getName());
			menu.setCode(f.getCode());
			menu.setEnableState(DataState.ENABLE);
			menu.setType(MenuType.STANDARD);
			menu.setCurrentSystemId(systemId);
			menu.setSystemId(systemId);
			menu.setUrl(f.getPath());
			menu.setOpenWay(OpenWay.CURRENT_PAGE_OPEN);
			menu.setFunctionId(f.getId());
			pid=f.getPid();
			if(pid!=null){
				parent=(Menu)menuDao.findUnique("from Menu m where m.functionId=? and m.companyId=?",pid,c.getId());
				if(parent==null){
					parent=(Menu)menuDao.findUnique("from Menu m where m.systemId=? and m.parent is null and m.companyId=? and m.layer=?",systemId,c.getId(),1);
				}
			}else{
				parent=(Menu)menuDao.findUnique("from Menu m where m.systemId=? and m.parent is null and m.companyId=? and m.layer=? and m.type=?",systemId,c.getId(),1,MenuType.STANDARD);
			}
			menu.setParent(parent);
			menu.setLayer(parent.getLayer()+1);
			menu.setDisplayOrder(f.getOrdinal());
			menu.setCompanyId(c.getId());
			menuDao.save(menu);
		}
	}

	private void deleteMenu(List<Menu> dels,Long systemId,Company c) {
		Menu parent=null;
		Collections.sort(dels,new Comparator<Menu>(){
			public int compare(Menu m1, Menu m2) {
				return m1.getLayer()-m2.getLayer();
			}});
		for(Menu m:dels){
			parent=m.getParent();
			List<Menu> childrens=menuDao.findList("from Menu m where m.parent.id=? and m.companyId=?",m.getId(),c.getId());
			for(Menu m1:childrens){
				m1.setParent(parent);
				if(m1.getType()==MenuType.CUSTOM){
					m.setLayer(parent.getLayer()+1);
					setChildrenLayer(m1,c,parent);
					menuDao.delete(m1.getId());
				}else{
					setChildrenLayer(m1,c,m1);
				}
			}
			menuDao.delete(m.getId());
		}
	}
	private void setChildrenLayer(Menu m1,Company c,Menu parent) {
		List<Menu> childrens=menuDao.findList("from Menu m where m.parent.id=? and m.companyId=?",m1.getId(),c.getId());
		for(Menu m:childrens){
			m.setParent(parent);
			m.setLayer(parent.getLayer()+1);
			setChildrenLayer(m,c,m);
		}
	}

	private void pushValue(List<Function> adds, List<Menu> dels,List<Function> functions, List<Menu> menus,Company c) {
		Map<Long,Boolean> ids=new HashMap<Long,Boolean>();
		for(Function fs:functions){
			for(Menu m:menus){
				if(fs.getId().equals(m.getFunctionId())){
					menuDao.createQuery("update Menu m set m.code=?,m.url=? where m.id=? and m.companyId=?",fs.getCode(),fs.getPath(),m.getId(),c.getId()).executeUpdate();
					ids.put(fs.getId(), false);
					break;
				}
			}
		}
		pushAdd(adds,ids,functions);
		pushDel(dels,ids,menus);
	}


	private void pushDel(List<Menu> dels, Map<Long, Boolean> ids, List<Menu> menus) {
		for(Menu m:menus){
			if(ids.get(m.getFunctionId())==null){
				dels.add(m);
			}
		}
	}

	private void pushAdd(List<Function> adds, Map<Long, Boolean> ids, List<Function> functions) {
		for(Function f:functions){
			if(ids.get(f.getId())==null){
				adds.add(f);
			}
		}
	}


	private boolean hasMenuBySys(Long systemId,Company c) {
		Long count=(Long)menuDao.findUnique("select count(*) from Menu m where m.systemId=? and m.type=? and companyId=?", systemId,MenuType.STANDARD,c.getId());
		return count>0?true:false;
	}

	public boolean isVoidFunction(Long paternId) {
		Function f=functionDao.get(paternId);
		if(f.getPid()==null&&!f.getIsmenu()){
			return true;
		}
		return false;
	}
	//创建移除资源树
	public String createFunctionTreeByRoleRemove(Role entity, Long systemId) {
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		List<Function> fs = new ArrayList<Function>();
		BusinessSystem bs=businessSystemDao.get(systemId);
		List<Function> functions=functionDao.findList("select distinct f from RoleFunction rf join rf.role r join rf.function f where r.id=? and f.businessSystem.id=? and rf.deleted=? and f.deleted=? order by f.ordinal,f.name",entity.getId(),systemId,false,false);
		//系统节点
		ZTreeNode business_=new ZTreeNode("business_"+bs.getId().toString(),"0", bs.getName(),"false","false",bs.getPath(),"","system","system","false","true","false","true","false");
		ZTreeNode void_=new ZTreeNode("void_"+bs.getId().toString(),"0", "未分类","false","false","root",bs.getPath(),"void","void","false","true","false","true","false");
		//递归拼树
		for(Function function:functions){
			setNodes(function,functions,fs);
		}
		fs.addAll(functions);
		addNodes(functions,fs,ztreeNodes);
		if(hasChildInBusiness(ztreeNodes,business_.getId())){
			business_.setIsParent("true");
			business_.setOpen("true");
		}
		ztreeNodes.add(business_);
		ztreeNodes.add(void_);
		return JsonParser.object2Json(ztreeNodes);
	}

	
	/**
	 * 获取系统下所有的父资源
	 */
	public List<Function> getParentFunctionsBySystem(Long systemId){
		return functionDao.findList("from Function f where businessSystem.id=? and f.deleted=? and f.pid is  null", systemId, false);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Function getFunctionByCode(String code,Long systemId){
		List<Function> funs= functionDao.find("from Function f where f.code=? and (f.businessSystem!=null and f.businessSystem.id=?)", code,systemId);
		if(funs.size()>0)return funs.get(0);
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<Function> getChildrenFunction(Long parentFunctionId){
		List<Function> funs= functionDao.find("from Function f where f.pid=?", parentFunctionId);
		return funs;
	}
	//创建添加资源树
	public String createFunctionTreeByRoleAdd(Role entity, Long systemId) {
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		List<Function> fs=new ArrayList<Function>();
		BusinessSystem bs=businessSystemDao.get(systemId);
		List<Function> functions=functionDao.findList("from Function f where f.id not in (select distinct rf.function.id from RoleFunction rf where rf.role.id=? and rf.deleted=?) and f.businessSystem.id=? and f.deleted=? order by f.ordinal,f.name",entity.getId(),false,systemId,false);
		//系统节点
		ZTreeNode business_=new ZTreeNode("business_"+bs.getId().toString(),"0", bs.getName(),"false","false",bs.getPath(),"","system","system","false","true","false","true","false");
		ZTreeNode void_=new ZTreeNode("void_"+bs.getId().toString(),"0", "未分类","false","false","root",bs.getPath(),"void","void","false","true","false","true","false");
		//递归拼树
		for(Function function:functions){
			setNodes(function,functions,fs);
		}
		fs.addAll(functions);
		addNodes(functions,fs,ztreeNodes);
		if(hasChildInBusiness(ztreeNodes,business_.getId())){
			business_.setIsParent("true");
			business_.setOpen("true");
		}
		ztreeNodes.add(business_);
		ztreeNodes.add(void_);
		return JsonParser.object2Json(ztreeNodes);
	}
	private void addNodes(List<Function> functions, List<Function> fs,
			List<ZTreeNode> ztreeNodes) {
		ZTreeNode node=null;
		Collections.sort(fs, new Comparator<Function>() {
			public int compare(Function o1, Function o2) {
				int result=o1.getMenulevel()-o2.getMenulevel();
				return result==0?o1.getOrdinal()-o2.getOrdinal():result;
			}
		});
		for(Function f:fs){
			if(functions.contains(f)){
				node=new ZTreeNode(f.getId().toString(),f.getPid()==null?(f.getIsmenu()?"business_"+f.getBusinessSystem().getId().toString():"void_"+f.getBusinessSystem().getId().toString()):f.getPid().toString(), f.getName(),"true","false",f.getPath(),f.getCode(),getStringType(f),getStringType(f),"false","false","false","true","false");
			}else{
				node=new ZTreeNode(f.getId().toString(),f.getPid()==null?(f.getIsmenu()?"business_"+f.getBusinessSystem().getId().toString():"void_"+f.getBusinessSystem().getId().toString()):f.getPid().toString(), f.getName(),"true","false",f.getPath(),f.getCode(),getStringType(f),getStringType(f),"false","true","false","true","false");
			}
			ztreeNodes.add(node);
		}
		
	}
	
	private boolean hasChildInBusiness(List<ZTreeNode> ztreeNodes,String Business_) {
		for(ZTreeNode zNode:ztreeNodes){
			if(zNode.getpId().equals(Business_)){
				return true;
			}
		}
		return false;
	}

	//拼接已选资源节点
	private void setNodes(Function function,List<Function> functions,List<Function> fs) {
		Function parent=function.getPid()==null?null:functionDao.get(function.getPid());
		if(parent!=null){
			if(!fs.contains(parent)&&!functions.contains(parent)){
				fs.add(parent);
				setNodes(parent,functions,fs);
			}
		}
	}

	//根据function菜单属性返回
	private String getStringType(Function function){
		if(function.getIsmenu()){
			return "menu";
		}else{
			return "function";
		}
	}

	public String createMenuTree(Long systemId) {
		List<ZTreeNode> ztreeNodes=new ArrayList<ZTreeNode>();
		List<Function> functions=functionDao.findList("from Function f where f.businessSystem.id=? and f.deleted=? and f.ismenu=?", systemId,false,true);
		BusinessSystem bs=businessSystemDao.get(systemId);
		ZTreeNode node=new ZTreeNode("business_"+bs.getId().toString(),"0", bs.getName(),"true","true",bs.getPath(),"","system","system","false","true","false","true","false");
		if(functions!=null&&functions.size()==0){
			node.setIsParent("false");
		}
		ztreeNodes.add(node);
		Collections.sort(functions, new Comparator<Function>() {
			public int compare(Function o1, Function o2) {
				int result=o1.getMenulevel()-o2.getMenulevel();
				return result==0?o1.getOrdinal()-o2.getOrdinal():result;
			}
		});
		for(Function function:functions){
			node=new ZTreeNode(function.getId().toString(),function.getPid()==null?"business_"+systemId.toString():function.getPid().toString(), function.getName(),"true","false",function.getPath(),function.getCode(),"menu","menu","");
			ztreeNodes.add(node);
		}
		return JsonParser.object2Json(ztreeNodes);
	}
	public String addFunctionsToMenu(String msg, String menuId, Long systemId) {
		Function f=functionDao.get(Long.parseLong(menuId));
		functionDao.createQuery("update Function f set f.pid=?,f.functionGroup.id=null,f.menulevel=?,f.ordinal=f.id where f.ismenu=? and f.functionGroup.id in ("+msg+")",Long.parseLong(menuId),f.getMenulevel()+1,false).executeUpdate();
		functionDao.createQuery("update Function f set f.functionGroup.id=null where  f.ismenu=? and f.functionGroup.id in ("+msg+")",true).executeUpdate();
		functionGroupDao.createQuery("delete FunctionGroup f where f.id in ("+msg+")").executeUpdate();
		return "ok";
	}

	public String validata(Long id, String code) {
		if(id==null){
			Long count=(Long)functionDao.findUnique("select count(f.id)from Function f where f.deleted=? and f.code=?", false,code);
			if(count>0){
				return "error";
			}else{
				return "ok";
			}
		}else{
			Function f=functionDao.get(id);
			Long count=(Long)functionDao.findUnique("select count(*)from Function f where f.deleted=? and f.code=?", false,code);
			if(!f.getCode().equals(code)&&count>0){
				return "error";
			}else{
				return "ok";
			}
		}
	}

}
