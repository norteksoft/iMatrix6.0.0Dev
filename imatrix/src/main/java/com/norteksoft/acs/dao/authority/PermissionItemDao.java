package com.norteksoft.acs.dao.authority;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.LeftBracket;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.RightBracket;
import com.norteksoft.acs.base.enumeration.UserOperator;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;
@Repository
public class PermissionItemDao extends HibernateDao<PermissionItem,Long>{

	public void getPermissionItems(Page<PermissionItem> page,Long permissionId){
		this.searchPageSubByHql(page, "from PermissionItem pi where pi.permission.id=?", permissionId);
	}
	public List<PermissionItem> getAllPermissionItems(Long permissionId){
		return this.find("from PermissionItem pi where pi.permission.id=? and pi.companyId=?", permissionId,ContextUtils.getCompanyId());
	}
	public PermissionItem getPermissionItem(ItemType itemType,UserOperator operator, LogicOperator joinType, String conditionValue,Long permissionId,LeftBracket leftBracket,RightBracket rightBracket) {
		List<PermissionItem> permissionItems=this.find("from PermissionItem p where p.companyId=? and p.itemType=? and p.operator=? and p.joinType=? and p.conditionValue=? and p.permission.id=? and p.leftBracket=? and p.rightBracket=?",ContextUtils.getCompanyId(),itemType,operator,joinType,conditionValue,permissionId,leftBracket,rightBracket);
		if(permissionItems!=null&&permissionItems.size()>0){
			return permissionItems.get(0);
		}else{
			return null;
		}
	}
	
	public void deleteAllPermissionItems(Long permissionId){
		this.createQuery("delete from PermissionItem pi where pi.permission.id=? and pi.companyId=?", permissionId,ContextUtils.getCompanyId()).executeUpdate();
	}
	
	public List<PermissionItem> getPermissionItemsByPermission(Long permissionId,ItemType itemType){
		return this.find("select pi from PermissionItem pi join pi.permission p where p.id=? and pi.itemType=? order by pi.id", permissionId,itemType);
	}
	public List<String> getPermissionUserNamesByPermission(Long permissionId,ItemType itemType){
		return this.find("select pi.conditionName from PermissionItem pi join pi.permission p where p.id=? and pi.itemType=?  order by pi.id", permissionId,itemType);
	}
	public List<String> getPermissionItemConditionNameByItem(Long permissionItemId,ConditionValueType valueType){
		return this.find("select pic.conditionName from PermissionItemCondition pic where pic.valueType=? and pic.dataId=? order by pic.id",valueType, permissionItemId);
	}
	public List<String> getPermissionItemConditionValueByItem(Long permissionItemId,ConditionValueType valueType){
		return this.find("select pic.conditionValue from PermissionItemCondition pic where pic.valueType=? and pic.dataId=? order by pic.id", valueType,permissionItemId);
	}
	/**
	 * 获得指定授权条件集合
	 * @param permissionId
	 * @return
	 */
	public List<PermissionItem> getPermissionItemsByPermission(Long permissionId){
		return this.find("select pi from PermissionItem pi join pi.permission p where p.id=? order by pi.id", permissionId);
	}
	
	public void deleteItemTypeAllUserPermissionItems(Long permissionId){
		this.createQuery("delete from PermissionItem pi where pi.permission.id=? and pi.companyId=? and pi.itemType=? order by pi.displayOrder", permissionId,ContextUtils.getCompanyId(),ItemType.ALL_USER).executeUpdate();
	}
	public void deleteItemTypeNotAllUserPermissionItems(Long permissionId){
		this.createQuery("delete from PermissionItem pi where pi.permission.id=? and pi.companyId=? and pi.itemType<>? order by pi.displayOrder", permissionId,ContextUtils.getCompanyId(),ItemType.ALL_USER).executeUpdate();
	}
	public List<PermissionItem> getItemTypeNotAllUserPermissionItems(Long permissionId){
		return  this.find("from PermissionItem pi where pi.permission.id=? and pi.companyId=? and pi.itemType<>? order by pi.displayOrder", permissionId,ContextUtils.getCompanyId(),ItemType.ALL_USER);
	}
}
