package com.norteksoft.bs.rank.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.bs.rank.dao.RankDao;
import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Workgroup;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Service
@Transactional
public class RankManager {
	private Log log = LogFactory.getLog(RankManager.class);
	private RankDao dataDictionaryRankDao;
	private RankUserDao dataDictionaryRankuserDao;
	private UserManager userManager;
	@Autowired
	private DepartmentManager departmentManager;
	@Autowired
	private AcsServiceImpl acsServiceImpl;
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	@Autowired
	public void setDataDictionaryRankDao(RankDao dataDictionaryRankDao) {
		this.dataDictionaryRankDao = dataDictionaryRankDao;
	}
	@Autowired
	public void setDataDictionaryRankuserDao(
			RankUserDao dataDictionaryRankuserDao) {
		this.dataDictionaryRankuserDao = dataDictionaryRankuserDao;
	}

	public Long getSystemId(){
    	return ContextUtils.getSystemId();
    }
	
	public Long getCompanyId(){
		return ContextUtils.getCompanyId();
	}
	public String getLoginName(){
		return ContextUtils.getLoginName();
	}
	
	public void getDataDictRanksPage(Page<Superior> dictRankPage){
		dataDictionaryRankDao.getDataDictRanksPage(dictRankPage,getCompanyId());
	}
	
	public Superior getDataDictRankById(Long id){
		return dataDictionaryRankDao.get(id);
	}
	
	//验证用户上下级，确保一个下级只有一个上级
	public int countSup(String usec,Long id,Long usid){
		int countp = 0;
		if(id!=null){
			Superior usper=dataDictionaryRankDao.get(id);
			if(usper!=null){
				if(usper.getUserId().equals(usid)){
					String[] uslist=usec.split(",");
					if(uslist.length>0){
						for(int i=0;i<uslist.length;i++){
							String[] infos=uslist[i].split(";");
							if(infos.length==4){
								List<Subordinate> slist=dataDictionaryRankuserDao.find("select ddr from Subordinate ddr where ddr.loginName=? and ddr.name=?",infos[3],infos[0]);
								if(slist.size()>0){
									if(!slist.get(0).getDataDictionaryRank().getUserId().equals(usid)){
										countp++;
									}
								}
								if(countp>0){
									break;
								}
							}else{
								List<Subordinate> slist1=dataDictionaryRankuserDao.find("select ddr from Subordinate ddr where ddr.name=?",infos[0]);
								if(slist1.size()>0){
									if(!slist1.get(0).getDataDictionaryRank().getUserId().equals(usid)){
										countp++;
									}
								}
								if(countp>0){
									break;
								}
							}
						}
					}
				}
			}
		}else{
			String[] uslist=usec.split(",");
			if(uslist.length>0){
				for(int i=0;i<uslist.length;i++){
					String[] infos=uslist[i].split(";");
					if(infos.length==4){
						List<Subordinate> slist=dataDictionaryRankuserDao.find("select ddr from Subordinate ddr where ddr.loginName=? and ddr.name=?",infos[3],infos[0]);
						if(slist.size()>0){
							if(!slist.get(0).getDataDictionaryRank().getUserId().equals(usid)){
								countp++;
							}
						}
						if(countp>0){
							break;
						}
					}else{
						List<Subordinate> slist1=dataDictionaryRankuserDao.find("select ddr from Subordinate ddr where ddr.name=?",infos[0]);
						if(slist1.size()>0){
							if(!slist1.get(0).getDataDictionaryRank().getUserId().equals(usid)){
								countp++;
							}
						}
						if(countp>0){
							break;
						}
					}
				}
			}
		}
		
		return countp;
	}
	@Transactional(readOnly=false)
	public void saveDataDictRank(Superior dataDictionaryRank,List<String> userInfos){
		dataDictionaryRank.setCompanyId(getCompanyId());
		dataDictionaryRank.setSystemId(getSystemId());
		dataDictionaryRank.setCreator(getLoginName());
		dataDictionaryRankDao.save(dataDictionaryRank);
		List<Subordinate> ddrus=dataDictionaryRankuserDao.getDataDictRankUsersByRankId(dataDictionaryRank.getId());
		for(Subordinate ddru:ddrus){
			dataDictionaryRankuserDao.delete(ddru);
		}
		String[] info=userInfos.get(0).split(";");
		String[] ids=info[0].split(",");
		String[] names=info[1].split(",");
		String[] loginName=info[2].split(",");
		Integer type=Integer.parseInt(info[3]);
		for(int i=0;i<ids.length;i++){
			Subordinate dataDictRankUser=new Subordinate();
			dataDictRankUser.setCompanyId(getCompanyId());
			dataDictRankUser.setSystemId(getSystemId());
			dataDictRankUser.setName(names[i]);
			dataDictRankUser.setTargetId(Long.parseLong(ids[i]));
			dataDictRankUser.setLoginName((!loginName[0].equals(""))?loginName[i]:"");
			if(type==0){
				dataDictRankUser.setSubordinateType(SubordinateType.USER);
			}else if(type==1){
				dataDictRankUser.setSubordinateType(SubordinateType.DEPARTMENT);
			}else{
				dataDictRankUser.setSubordinateType(SubordinateType.WORKGROUP);
			}
			dataDictRankUser.setDataDictionaryRank(dataDictionaryRank);
			dataDictionaryRankuserDao.save(dataDictRankUser);
		}
	}
	@Transactional(readOnly=false)
	public void deleteDataDictRanks(String dictRankIds){
		List<Long> dids=getList(dictRankIds);;
	    for(int i=0;i<dids.size();i++){
	    	List<Subordinate> ddrus=dataDictionaryRankuserDao.getDataDictRankUsersByRankId(dids.get(i));
			for(Subordinate ddru:ddrus){
				dataDictionaryRankuserDao.delete(ddru);
			}
	    	dataDictionaryRankDao.delete(dids.get(i));
	    }
	}
	public User getDirectLeader(Long userId,String loginName) {
		if(userId==null){
			return getDirectLeader(loginName);
		}else{
			return getDirectLeader(userId, getCompanyId());
			
		}
	}
	public User getDirectLeader(Long userId) {
		return getDirectLeader(userId, getCompanyId());
	}

	
	/**
     * 根据用户ID查询该用户的直属领导
     * @param userId
     * @return
     */
	public User getDirectLeader(Long userId, Long companyId) {
		if(userId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 人员ID. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 公司ID. ");
		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(userId, companyId);
		Long leaderId = null;
		if(!dicts.isEmpty()){
			leaderId = getLeaderIdFromDict(dicts);
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			User user = userManager.getUserById(userId);
			Long subCompanyId = null;
			if(user!=null) subCompanyId = user.getSubCompanyId();
			List<Long> deptIds = ApiFactory.getAcsService().getDepartmentIds(userId);
			if(subCompanyId!=null){
				deptIds.add(subCompanyId);//将分支机构id放入部门id集合中
			}
			dicts = dataDictionaryRankDao.getDirectLeaderByDeptId(deptIds, companyId);
			if(!dicts.isEmpty()){
				leaderId = getLeaderIdFromDict(dicts);
			}
		}
		if(leaderId == null) return null;
		else return userManager.getUserById(leaderId);
	}

	/**
     * 根据用户登录名查询该用户的直属领导列表
     * @param loginName
     * @return
     */
	public List<User> getDirectLeaders(String loginName) {
		return getDirectLeaders(loginName,getCompanyId());
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public List<User> getDirectLeaders(String loginName,Long companyId) {
		if(loginName == null) throw new RuntimeException("没有给定查询直属领导集合的查询条件： 人员登录名. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导集合的查询条件：  公司ID. ");
 		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(loginName,companyId);
		Long leaderId = null;
		List<User> directs=new ArrayList<User>();
		if(!dicts.isEmpty()){
			for(Superior rank:dicts){
				leaderId =rank.getUserId();
				User user=userManager.getUserById(leaderId);
				directs.add(user);
			}
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			List<Department> depts = userManager.getDepartments(loginName);
			dicts = dataDictionaryRankDao.getDirectLeader(depts, companyId);
			if(!dicts.isEmpty()){
				for(Superior rank:dicts){
					leaderId =rank.getUserId();
					User user=userManager.getUserById(leaderId);
					directs.add(user);
				}
			}
		}
		if(directs.size()<=0) return null;
		else return directs;
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public User getDirectLeader(String loginName) {
		return getDirectLeader(loginName,getCompanyId());
	}
	/**
     * 根据用户登录名查询该用户的直属领导
     * @param loginName
     * @return
     */
	public User getDirectLeader(String loginName,Long companyId) {
		if(loginName == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 人员登录名. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属领导的查询条件： 公司ID. ");
		List<Superior> dicts = dataDictionaryRankDao.getDirectLeader(loginName,companyId);
		Long leaderId = null;
		if(!dicts.isEmpty()){
			leaderId = getLeaderIdFromDict(dicts);
		}else{
			ThreadParameters parameters = new ThreadParameters(companyId);
			ParameterUtils.setParameters(parameters);
			List<Department> depts = userManager.getDepartments(loginName);
			dicts = dataDictionaryRankDao.getDirectLeader(depts, companyId);
			if(!dicts.isEmpty()){
				leaderId = getLeaderIdFromDict(dicts);
			}
		}
		if(leaderId == null) return null;
		else return userManager.getUserById(leaderId);
	}
	
	private Long getLeaderIdFromDict(List<Superior> dicts){
		Long leaderId = null;
		if(dicts.size() == 1){
			leaderId = dicts.get(0).getUserId();
		}else{
			log.debug(" *** query direct leader error. DataDictionaryRank num [" + dicts.size() + "] *** ");
		}
		return leaderId;
	}
	
	public List<Superior> getDataDictRanks(String value){
		 return dataDictionaryRankDao.getDataDictRanks(getCompanyId(),value);
	}
	
	public static List<Long> getList(String ids){
		String[] dids=ids.split(",");
		List<Long> id=new ArrayList<Long>();
		for(int i=0;i<dids.length;i++){
			id.add(Long.parseLong(dids[i]));
		}
		return id;
	}

	public List<Superior> getRanks(Long companyId){
		return dataDictionaryRankDao.getRanks(companyId);
	}
	public Superior getRankByTitle(String rankTitle){
		return dataDictionaryRankDao.getRankByTitle(rankTitle);
	}
	@Transactional(readOnly=false)
	public void saveDataDictRank(Superior rank){
	    	dataDictionaryRankDao.save(rank);
    }
	
	/**
     * 根据用户id查询该用户的直属下级
     * @param userId
     * @return
     */
	public Set<User> getDirectLower(Long userId,Long companyId) {
		if(userId == null) throw new RuntimeException("没有给定查询直属下级的查询条件： 人员id. ");
		if(companyId == null) throw new RuntimeException("没有给定查询直属下级的查询条件： 公司ID. ");
		Set<User> lowers = new HashSet<User>();
		List<Superior> dicts = dataDictionaryRankDao.getDirectLower(userId,companyId);
		for(Superior s:dicts){
			List<Subordinate> subordinates = dataDictionaryRankuserDao.getDataDictRankUsersByRankId(s.getId());
			for(Subordinate sn:subordinates){
				if(sn.getSubordinateType()==SubordinateType.USER){
					User user = userManager.getUserById(sn.getTargetId());
					lowers.add(user);
				}else if(sn.getSubordinateType()==SubordinateType.DEPARTMENT){
					List<User> users = new ArrayList<User>();
					
					Department dept = departmentManager.getDepartment(sn.getTargetId());
					if(dept!=null){
						if(dept.getBranch()){//如果是分支
							users = acsServiceImpl.getAllUsersByBranchId(sn.getTargetId());
						}else{
							users = userManager.getUsersByDeptId(sn.getTargetId());
						}
					}
					//直属下级不应包含直属上级用户
					User u = userManager.getUserById(userId);
					if(users.contains(u))users.remove(u);
					//将所有用户添加到直属下级集合中
					if(!users.isEmpty())lowers.addAll(users);
				}else if(sn.getSubordinateType()==SubordinateType.WORKGROUP){
					List<User> users = userManager.getUsersByWorkgroupId(sn.getTargetId());
					//直属下级不应包含直属上级用户
					User u = userManager.getUserById(userId);
					if(users.contains(u))users.remove(u);
					//将所有用户添加到直属下级集合中
					if(!users.isEmpty())lowers.addAll(users);
				}
			}
		}
		return lowers;
	}
}
