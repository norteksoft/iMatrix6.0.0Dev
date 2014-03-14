package com.norteksoft.product.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.dao.authority.PermissionExtendBaseDao;
import com.norteksoft.product.api.DataPermissionService;

@Service
@Transactional
public class DataPermissionServiceImpl implements DataPermissionService {
	@Autowired
	private PermissionExtendBaseDao permissionExtendBaseDao;
	

	public boolean deletePermission(Object entity) {
		return permissionExtendBaseDao.deletePermission(entity,LogicOperator.OR,LogicOperator.OR);
	}

	public boolean deletePermission(Object entity, LogicOperator authlink) {
		return permissionExtendBaseDao.deletePermission(entity, authlink,LogicOperator.OR);
	}
	public boolean deletePermission(LogicOperator dataRulelink,Object entity) {
		return permissionExtendBaseDao.deletePermission(entity,LogicOperator.OR,dataRulelink);
	}
	public boolean deletePermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink) {
		return permissionExtendBaseDao.deletePermission(entity, authlink,dataRulelink);
	}

	public boolean deletePermission(Object entity, List<String>  permissionCodes) {
		return permissionExtendBaseDao.deletePermission(entity, LogicOperator.OR,LogicOperator.OR, permissionCodes);
	}
	public boolean deletePermission(Object entity, LogicOperator authlink,
			List<String>  permissionCodes) {
		return permissionExtendBaseDao.deletePermission(entity, authlink,LogicOperator.OR, permissionCodes);
	}
	public boolean deletePermission(LogicOperator dataRulelink, Object entity,
			List<String> permissionCodes) {
		return permissionExtendBaseDao.deletePermission(entity, LogicOperator.OR,dataRulelink, permissionCodes);
	}
	public boolean deletePermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink,
			List<String>  permissionCodes) {
		return permissionExtendBaseDao.deletePermission(entity, authlink,dataRulelink, permissionCodes);
	}

	
	public boolean updatePermission(Object entity) {
		return permissionExtendBaseDao.updatePermission(entity,LogicOperator.OR,LogicOperator.OR);
	}

	public boolean updatePermission(Object entity, LogicOperator authlink) {
		return permissionExtendBaseDao.updatePermission(entity, authlink,LogicOperator.OR);
	}
	public boolean updatePermission(LogicOperator dataRulelink,Object entity) {
		return permissionExtendBaseDao.updatePermission(entity, LogicOperator.OR,dataRulelink);
	}
	public boolean updatePermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink) {
		return permissionExtendBaseDao.updatePermission(entity, authlink,dataRulelink);
	}

	public boolean updatePermission(Object entity, List<String>  permissionCodes) {
		return permissionExtendBaseDao.updatePermission(entity, LogicOperator.OR,LogicOperator.OR, permissionCodes);
	}
	public boolean updatePermission(Object entity, LogicOperator authlink,
			List<String>  permissionCodes) {
		return permissionExtendBaseDao.updatePermission(entity, authlink,LogicOperator.OR, permissionCodes);
	}
	
	public boolean updatePermission(LogicOperator dataRulelink, Object entity,
			List<String> permissionCodes) {
		return permissionExtendBaseDao.updatePermission(entity, LogicOperator.OR,dataRulelink, permissionCodes);
	}
	
	public boolean updatePermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink,
			List<String>  permissionCodes) {
		return permissionExtendBaseDao.updatePermission(entity, authlink,dataRulelink, permissionCodes);
	}

	public boolean viewPermission(Object entity) {
		return permissionExtendBaseDao.viewPermission(entity,LogicOperator.OR,LogicOperator.OR);
	}

	public boolean viewPermission(Object entity, LogicOperator authlink) {
		return permissionExtendBaseDao.viewPermission(entity, authlink,LogicOperator.OR);
	}
	public boolean viewPermission(LogicOperator dataRulelink,Object entity) {
		return permissionExtendBaseDao.viewPermission(entity,LogicOperator.OR,dataRulelink);
	}
	public boolean viewPermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink) {
		return permissionExtendBaseDao.viewPermission(entity, authlink,dataRulelink);
	}

	public boolean viewPermission(Object entity,  List<String> permissionCodes) {
		return permissionExtendBaseDao.viewPermission(entity, LogicOperator.OR,LogicOperator.OR, permissionCodes);
	}
	public boolean viewPermission(Object entity, LogicOperator authlink,
			List<String> permissionCodes) {
		return permissionExtendBaseDao.viewPermission(entity, authlink,LogicOperator.OR, permissionCodes);
	}
	public boolean viewPermission(Object entity, LogicOperator authlink,LogicOperator dataRulelink,
			List<String> permissionCodes) {
		return permissionExtendBaseDao.viewPermission(entity, authlink,dataRulelink, permissionCodes);
	}

	public <T> void addPermissionCondition(String hql, Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql,LogicOperator.OR,LogicOperator.OR,values);
	}

	public <T> void addPermissionCondition(String hql, LogicOperator authlink,
			Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql, authlink,LogicOperator.OR, values);
	}

	public <T> void addPermissionCondition(LogicOperator dataRulelink,
			String hql, Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql, LogicOperator.OR,dataRulelink, values);
		
	}
	public <T> void addPermissionCondition(String hql, LogicOperator authlink,LogicOperator dataRulelink,
			Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql, authlink,dataRulelink, values);
	}

	public <T> void addPermissionCondition(String hql, List<String> permissionCodes,
			Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql,LogicOperator.OR,LogicOperator.OR, permissionCodes, values);
	}
	public <T> void addPermissionCondition(String hql, LogicOperator authlink,
			List<String> permissionCodes,
			Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql,authlink,LogicOperator.OR, permissionCodes, values);
		
	}
	public <T> void addPermissionCondition(LogicOperator dataRulelink,
			String hql, List<String> permissionCodes, Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql,LogicOperator.OR,dataRulelink, permissionCodes, values);
	}
	public <T> void addPermissionCondition(String hql, LogicOperator authlink,
			LogicOperator dataRulelink, List<String> permissionCodes,
			Object... values) {
		permissionExtendBaseDao.addPermissionCondition(hql,authlink,dataRulelink, permissionCodes, values);
		
	}


}
