package com.norteksoft.acs.service.authority;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.dao.authority.PermissionItemDao;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class PermissionItemManager {
	@Autowired
	private PermissionItemDao permissionItemDao;
	@Autowired
	private PermissionItemConditionManager permissionItemConditionManager;
	public void getPermissionItems(Page<PermissionItem> page,Long permissionId){
		permissionItemDao.getPermissionItems(page, permissionId);
	}
	
	public void deletePermissionItem(Long id){
		//删除人员条件项
		permissionItemConditionManager.deletePermissionItemConditions(id);
		//删除人员项
		permissionItemDao.delete(id);
	}
	
	public List<PermissionItem> getPermissionItemsByPermission(Long permissionId,ItemType itemType){
		return permissionItemDao.getPermissionItemsByPermission(permissionId,itemType);
	}
	public List<String> getPermissionUserNamesByPermission(Long permissionId,ItemType itemType){
		return permissionItemDao.getPermissionUserNamesByPermission(permissionId,itemType);
	}
	public List<String> getPermissionItemConditionNameByItem(Long permissionItemId,ConditionValueType valueType){
		return permissionItemDao.getPermissionItemConditionNameByItem(permissionItemId,valueType);
	}
	public List<String> getPermissionItemConditionValueByItem(Long permissionItemId,ConditionValueType valueType){
		return permissionItemDao.getPermissionItemConditionValueByItem(permissionItemId,valueType);
	}
	public List<PermissionItem> getPermissionItemsByPermission(Long permissionId){
		return permissionItemDao.getPermissionItemsByPermission(permissionId);
	}
	
	public void deletePermissionItemByPermissionId(Long permissionId){
		permissionItemConditionManager.deleteAllPermissionItemConditions(permissionId);
		//删除人员条件项
		permissionItemDao.deleteAllPermissionItems(permissionId);
	}
	
	public  List<PermissionItem> getItemTypeNotAllUserPermissionItems(Long permissionId){
		return permissionItemDao.getItemTypeNotAllUserPermissionItems(permissionId);
	}
}
