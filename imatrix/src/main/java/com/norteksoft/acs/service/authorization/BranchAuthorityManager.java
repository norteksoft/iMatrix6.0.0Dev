package com.norteksoft.acs.service.authorization;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.product.util.ContextUtils;
/**
 * 分支机构授权管理
 * 
 */
@Service
@Transactional
public class BranchAuthorityManager {
	private SimpleHibernateTemplate<BranchAuthority, Long> branchAuthorityDao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		branchAuthorityDao = new SimpleHibernateTemplate<BranchAuthority, Long>(sessionFactory,
				BranchAuthority.class);
	}

	/**
	 * 根据人员id获得该人员所管理的分支机构
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BranchAuthority> getBranchByUser(Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		hql.append("where b.companyId=? and b.dataId=? and b.branchDataType=?");
		return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), userId,BranchDataType.USER);
	}
	
	/**
	 * 根据人员id获得该人员所管理的分支机构
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getBranchIdsByUser(Long userId) {
		List<BranchAuthority> list = getBranchByUser(userId);
		if(!list.isEmpty()){
			String result="";
			for(BranchAuthority b:list){
				result+=b.getBranchesId()+",";
			}
			return result.substring(0,result.length()-1);
		}
		return "";
	}

	/**
	 * 根据分支机构id获得该分支机构下的所有角色
	 * @param branchesId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BranchAuthority> getRolesByBranch(Long branchesId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		if(branchesId==null){
			hql.append("where b.companyId=? and b.branchDataType=?");
			return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(),BranchDataType.ROLE);
		}else{
			hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=?");
			return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.ROLE);
		}
	}
	
	/**
	 * 根据分支机构id获得分支机构授权
	 * @param branchesId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BranchAuthority> getBranchAuthorityByBranch(Long branchesId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchesId=? ");
		return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), branchesId);
	}

	public BranchAuthority getBranchAuthority(Long id) {
		return branchAuthorityDao.get(id);
	}
	
	/**
	 * 根据分支机构id获得该分支机构下的所有管理员
	 * @param branchesId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BranchAuthority> getUsersByBranch(Long branchesId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		if(branchesId==null){
			hql.append("where b.companyId=? and b.branchDataType=?");
			return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(),BranchDataType.USER);
		}else{
			
			hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=?");
			return branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.USER);
		}
	}

	/**
	 * 根据分支机构id和角色id获得分支机构授权管理
	 * @param branchesId
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BranchAuthority getBranchAuthority(Long branchesId, Long roleId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=? and b.dataId=? ");
		List<BranchAuthority> list = branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.ROLE,roleId);
		if(list != null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 根据分支机构id和人员id获得分支机构授权管理
	 * @param branchesId
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BranchAuthority getBranchAuthorityUser(Long branchesId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("from BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=? and b.dataId=? ");
		List<BranchAuthority> list = branchAuthorityDao.find(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.USER,userId);
		if(list != null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 保存分支机构授权管理
	 * @param branchAuthority
	 */
	public void saveBranchAuthority(BranchAuthority branchAuthority) {
		branchAuthorityDao.save(branchAuthority);
	}

	/**
	 * 根据分支机构id和角色id删除分支机构授权管理
	 * @param branchesId
	 * @param roleId
	 */
	public void deleteRoleByBranchesId(Long branchesId, Long roleId) {
		StringBuilder hql = new StringBuilder();
		hql.append("delete BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=? and b.dataId=? ");
		branchAuthorityDao.executeUpdate(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.ROLE,roleId);
	}
	
	/**
	 * 根据角色id删除分支机构授权管理
	 * @param roleId
	 */
	public void deleteRoleByBranchesId(Long roleId) {
		StringBuilder hql = new StringBuilder();
		hql.append("delete BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchDataType=? and b.dataId=? ");
		branchAuthorityDao.executeUpdate(hql.toString(), ContextUtils.getCompanyId(),BranchDataType.ROLE,roleId);
	}

	/**
	 * 根据分支机构id和人员id删除分支机构授权管理
	 * @param branchesId
	 * @param userId
	 */
	public void deleteUserByBranchesId(Long branchesId, Long userId) {
		StringBuilder hql = new StringBuilder();
		hql.append("delete BranchAuthority b ");
		hql.append("where b.companyId=? and b.branchesId=? and b.branchDataType=? and b.dataId=? ");
		branchAuthorityDao.executeUpdate(hql.toString(), ContextUtils.getCompanyId(), branchesId,BranchDataType.USER,userId);
	}
	
	public boolean hasBranchAdminRole(Long branchId,Long userId){
		BranchAuthority branchAuth = getBranchAuthorityUser(branchId, ContextUtils.getUserId());
		if(branchAuth==null)return false;
		return true;
	}
	
}
