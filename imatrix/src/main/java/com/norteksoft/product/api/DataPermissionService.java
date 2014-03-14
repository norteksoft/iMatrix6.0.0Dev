package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.acs.base.enumeration.LogicOperator;


/**
 * 数据授权api
 * @author Administrator
 *
 * @param <T>
 */
public interface DataPermissionService{
	
	/**
	 * 查看权限
	 *  数据授权和规则关系的关系均为默认OR的关系
	 * @param entity
	 * @return
	 */
	public boolean viewPermission(Object entity);
	
	/**
	 * 根据规则关系获取查看权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @return
	 */
	public boolean viewPermission(Object entity,LogicOperator authlink);
	
	/**
	 * 根据规则关系获取查看权限
	 * @param entity
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * 数据授权之间默认为OR的关系
	 * @return
	 */
	public boolean viewPermission(LogicOperator dataRulelink,Object entity);
	/**
	 * 根据规则关系获取查看权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param  dataRulelink 数据分类之间关系配置:与 或
	 * @return
	 */
	public boolean viewPermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink);
	/**
	 * 根据规则关系和数据授权编号集合获取查看权限
	 * 数据分类和数据授权之间默认为OR的关系
	 * @param entity
	 * @param permissionCodes
	 * @return
	 */
	public boolean viewPermission(Object entity, List<String> permissionCodes);
	
	/**
	 * 根据规则关系和数据授权编号集合获取查看权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @param permissionCodes
	 * @return
	 */
	public boolean viewPermission(Object entity,LogicOperator authlink, List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取查看权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * @param permissionCodes
	 * @return
	 */
	public boolean viewPermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink, List<String> permissionCodes);
	
	/**
	 * 修改权限
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param entity
	 * @return
	 */
	public boolean updatePermission(Object entity);
	
	/**
	 * 根据规则关系获取修改权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @return
	 */
	public boolean updatePermission(Object entity,LogicOperator authlink);
	/**
	 * 根据规则关系获取修改权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * @return
	 */
	public boolean updatePermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink);
	
	/**
	 * 根据规则关系和数据授权编号集合获取修改权限
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param entity
	 * @param permissionCodes
	 * @return
	 */
	public boolean updatePermission(Object entity, List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取修改权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @param permissionCodes
	 * @return
	 */
	public boolean updatePermission(Object entity, LogicOperator authlink,List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取修改权限
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * 数据授权之间默认为OR的关系
	 * @param entity
	 * @param permissionCodes
	 * @return
	 */
	public boolean updatePermission(LogicOperator dataRulelink,Object entity, List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取修改权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * @param permissionCodes
	 * @return
	 */
	public boolean updatePermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink, List<String> permissionCodes);
	
	/**
	 * 删除权限
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param entity
	 * @return
	 */
	public boolean deletePermission(Object entity);
	
	/**
	 * 根据规则关系获取删除权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @return
	 */
	public boolean deletePermission(Object entity,LogicOperator authlink);
	/**
	 * 根据规则关系获取删除权限
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * 数据授权之间默认为OR的关系
	 * @param entity
	 * @return
	 */
	public boolean deletePermission(LogicOperator dataRulelink,Object entity);
	/**
	 * 根据规则关系获取删除权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * @return
	 */
	public boolean deletePermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink);
	
	/**
	 * 根据规则关系和数据授权编号集合获取删除权限
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param entity
	 * @param permissionCodes
	 * @return
	 */
	public boolean deletePermission(Object entity,List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取删除权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * 数据分类之间默认为OR的关系
	 * @param permissionCodes
	 * @return
	 */
	public boolean deletePermission(Object entity,LogicOperator authlink, List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取删除权限
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * 数据授权之间默认为OR的关系
	 * @param entity
	 * @param permissionCodes
	 * @return
	 */
	public boolean deletePermission(LogicOperator dataRulelink,Object entity, List<String> permissionCodes);
	/**
	 * 根据规则关系和数据授权编号集合获取删除权限
	 * @param entity
	 * @param authlink 数据授权之间关系配置:与 或
	 * @param dataRulelink 数据分类之间关系配置:与 或
	 * @param permissionCodes
	 * @return
	 */
	public boolean deletePermission(Object entity,LogicOperator authlink,LogicOperator dataRulelink, List<String> permissionCodes);
	
	
	/**
	 * 添加查询条件
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param 
	 * @return
	 */
	public <T> void addPermissionCondition(String hql, Object... values);
	
	/**
	 * 根据规则关系获取查询条件
	 * @param entity
	 * @param dataRulelink 数据分类之间的关系配置:与 或
	 * 数据授权之间的关系为默认OR
	 * @return
	 */
	public <T> void addPermissionCondition(String hql, LogicOperator authlink, Object... values);
	/**
	 * 根据规则关系获取查询条件
	 * @param authlink 数据授权之间的关系配置:与 或
	 * 数据分类之间的关系为默认OR
	 * @return
	 */
	public <T> void addPermissionCondition(LogicOperator dataRulelink,String hql,  Object... values);
	/**
	 * 根据规则关系获取查询条件
	 * @param entity
	 * @param dataRulelink 数据分类之间的关系配置:与 或
	 * 数据授权之间的关系为默认OR
	 * @return
	 */
	public <T> void addPermissionCondition(String hql,LogicOperator authlink, LogicOperator dataRulelink, Object... values);
	
	/**
	 * 根据规则关系和数据授权编号集合添加查询条件
	 * @param authlink 数据授权之间的关系配置:与 或
	 * @param dataRulelink 数据分类之间的关系配置:与 或
	 * @return
	 */
	public <T> void addPermissionCondition(String hql, LogicOperator authlink, LogicOperator dataRulelink,List<String> permissionCodes,Object... values);
	/**
	 * 根据规则关系和数据授权编号集合添加查询条件
	 * @param authlink 数据授权之间的关系配置:与 或
	 * 数据分类之间的关系为默认OR
	 * @return
	 */
	public <T> void addPermissionCondition(String hql, LogicOperator authlink, List<String> permissionCodes,Object... values);
	/**
	 * 根据规则关系和数据授权编号集合添加查询条件
	 * @param dataRulelink 数据分类之间的关系配置:与 或
	 * 数据授权之间的关系为默认OR
	 * @return
	 */
	public <T> void addPermissionCondition(LogicOperator dataRulelink,String hql,  List<String> permissionCodes,Object... values);
	/**
	 * 根据规则关系和数据授权编号集合添加查询条件
	 * 数据授权和规则关系的关系均为默认OR的关系
	 * @param <T>
	 * @param hql
	 * @param permissionCodes
	 * @param values
	 */
	public <T> void addPermissionCondition(String hql, List<String> permissionCodes, Object... values);
}
