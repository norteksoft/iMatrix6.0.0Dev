package com.norteksoft.cas.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.jasig.cas.util.PropUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class LoginSettngService {
	
	private final static String SETTING_SQL = "select s.value,s.fail_set_type,s.locked_time " +
			"from acs_security_setting s join acs_user u on s.fk_company_id=u.fk_company_id " +
			"where s.name='login-security' and s.deleted=0 and u.id=?";
	
	private final static String LOCK_USER_SQL = "update acs_user set account_locked=? where id=?";
	private final static String IS_LOCKED_SQL = "select account_locked from acs_user where id=? and deleted=0";
	private final static String COMPANY_ID_SQL = "select fk_company_id from acs_user where login_name=? and deleted=0";
	private final static String USER_ENABLE_SQL = "select enabled from acs_user where id=? and deleted=0";
	private final static String COMPANY_SQL = "select id from acs_company where code=? and deleted=0";
	private final static String DEPARTMENT_SQL = "select id,fk_company_id from acs_department where code=? and deleted=0";
	
	protected final Logger log = LoggerFactory.getLogger(LoginSettngService.class);
	
	@NotNull
    private SimpleJdbcTemplate jdbcTemplate;
    
    @NotNull
    private DataSource dataSource;
    
    /**
     * 锁定用户
     * @param username
     */
    public void lockUser(Long userId){
    	jdbcTemplate.update(LOCK_USER_SQL, 1, userId);
    }
    
    /**
     * 解锁用户
     * @param username
     */
    public void unlockUser(Long userId){
    	jdbcTemplate.update(LOCK_USER_SQL, 0, userId);
    }
    
    /**
     * 根据用户名查询公司id
     * @param username
     * @return
     */
    public Long getCompanyId(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(COMPANY_ID_SQL, username);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("fk_company_id");
    	return Long.valueOf(obj.toString());
    }
    /**
     * 根据公司编码查询公司id
     * @param username
     * @return
     */
    public Long getCompanyIdByCode(String companyCode){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(COMPANY_SQL, companyCode);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("id");
    	return Long.valueOf(obj.toString());
    }
    /**
     * 根据分支机构编码查询分支机构信息
     * @param username
     * @return object[]:result[0]是分支机构id，result[1]是分支机构所在租户id
     */
    public Object[] getBranchInfoByCode(String companyCode){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(DEPARTMENT_SQL, companyCode);
    	if(list.isEmpty()) return null;
    	Object[] result = new Object[2];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("fk_company_id");
    	return result;
    }
    /**
     * 根据用户名查询用户是否被禁用
     * @param username
     * @return
     */
    public Boolean getUserEnabled(Long userId){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_ENABLE_SQL, userId);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("enabled");
    	String database = PropUtils.getDataBase();
    	if(PropUtils.DATABASE_ORACLE.equals(database)||PropUtils.DATABASE_SQLSERVER.equals(database)){//oracle和sqlserver时
    		if(obj.toString().equals("1")){
    			return true;
    		}else{
    			return false;
    		}
    	}else{//mysql时
    		return Boolean.valueOf(obj.toString());
    	}
    }
    
    public Date getLastTime(Long companyId) {
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(
    			"select max(login_time) maxdate from acs_login_log where fk_company_id=?", companyId);
    	if(list.isEmpty()) return new Date();
        return (Date) list.get(0).get("maxdate");
    }
    
    /**
     * 用户是否已经锁定
     * @param username
     * @return null,不存在
     */
    public Boolean isUserLocked(Long userId){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(IS_LOCKED_SQL, userId);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("account_locked");
    	return getBoolean(obj);
    }
    
    private boolean getBoolean(Object obj){
    	if(obj instanceof Number){
    		return ((Number)obj).intValue()==1;
    	}else if(obj instanceof Boolean){
    		return (Boolean)obj;
    	}
    	return false;
    }
    
    /**
     * 用户是否已经解锁
     * @return
     */
    public boolean isUserUnlock(Long userId){
    	return !jdbcTemplate.queryForObject(IS_LOCKED_SQL, Boolean.class, userId);
    }
    
    /**
     * 查询登陆设置
     * @param username
     * @return KEY:[value: 允许失败次数; fail_set_type: 失败后设置(0,验证码; 1,锁定用户); locked_time:锁定时间(分钟);]
     */
    public Map<String, Object> getSecuritySetting(Long userId){
    	Map<String, Object> result = new HashMap<String, Object>();
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(SETTING_SQL, userId);
    	if(list.isEmpty()){
        	result.put("value", 3);
        	result.put("fail_set_type", 0);
        	result.put("locked_time", 30);
    	}else{
        	result.put("value", list.get(0).get("value"));
        	result.put("fail_set_type", list.get(0).get("fail_set_type"));
        	result.put("locked_time", list.get(0).get("locked_time"));
    	}
    	return result;
    }
    
    public final void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
	
    protected final SimpleJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
    protected final DataSource getDataSource() {
        return this.dataSource;
    }
    
    /**
     * 记录用户登录日志
     * @param user:obj[0]是用户id，obj[1]是用户姓名，obj[2]是用户所在公司id，obj[3]是分支机构id,obj[4]是用户密码,obj[5]是用户登录名
     * @param username
     */
    public void loginLog(Object[] user, String ip){
//    	Object[] user = getUser(username,companyId);
    	Long userId = Long.parseLong(user[0].toString());
    	Long companyId = Long.parseLong(user[2].toString());
    	Long subcompanyId = null;
    	if(user[3]!=null)subcompanyId = Long.parseLong(user[3].toString());
    	Long id = null;
    	try {
			String rawUrl = getJdbcUrl();
			if(rawUrl.startsWith("jdbc:oracle:")){
				id = jdbcTemplate.queryForLong("select hibernate_sequence.nextval from dual");
			}
			//给当前用户的未退出的登录日志设置退出时间
			jdbcTemplate.update(LOGIN_LOG_SELECT_SQL,new Date(),user[0],false,companyId);
			//增加登录日志
			if(id == null){
				jdbcTemplate.update(LOGIN_LOG_SQL, false, new Date(),false, companyId, ip, new Date(), user[0], user[1], getUserType(userId),subcompanyId,user[5],user[6]);
			}else{
				jdbcTemplate.update(LOGIN_LOG_SQL_CONTAINS_ID, id, false, new Date(),false, companyId, ip, new Date(), user[0], user[1], getUserType(userId),subcompanyId,user[5],user[6]);
			}
    	} catch (Exception e) {
			log.error("get datasource metadata error or query oracle sequence error.", e);
		}
    }
    
    /**
     * 返回值根据角色参考ACS系统中的  com.norteksoft.acs.base.enumeration.OperatorType
     * @param username
     * @return
     */
    private Integer getUserType(Long userId){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_ROLES_SQL, userId, false,false,false);
    	for(Map<String, Object> map : list){
    		if(SYSTEM_ADMIN.equals(map.get("code"))) return 1;
    		if(SECURITY_ADMIN.equals(map.get("code"))) return 2;
    		if(AUDIT_ADMIN.equals(map.get("code"))) return 3;
    	}
    	return 0;
    }
    
    /**
     * 在总公司（非分支机构内）内查询用户ID和用户姓名
     * @param username
     * @return null 用户不存在
     */
    public Object[] getUser(String username,Long companyId){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_COMPANY_SQL, username,companyId);
    	if(list.size()<=0)return null;
    	Object[] result = new Object[7];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("name");
    	result[2] = list.get(0).get("fk_company_id");
    	result[3] = list.get(0).get("sub_company_id");
    	result[4] = list.get(0).get("password");
    	result[5] = list.get(0).get("login_name");
    	result[6] = list.get(0).get("sub_company_name");
    	return result;
    }
    /**
     * 在分支机构中查询用户ID和用户姓名
     * @param username
     * @return null 用户不存在
     */
    public Object[] getUserInBranch(String username,Long subCompanyId,Long companyId){
    	List<Map<String, Object>> list  = jdbcTemplate.queryForList(USER_DEPARTMENT_SQL, username,subCompanyId,companyId);
    	if(list.size()<=0)return null;
    	Object[] result = new Object[7];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("name");
    	result[2] = list.get(0).get("fk_company_id");
    	result[3] = list.get(0).get("sub_company_id");
    	result[4] = list.get(0).get("password");
    	result[5] = list.get(0).get("login_name");
    	result[6] = list.get(0).get("sub_company_name");
    	return result;
    }
    /**
     * 查询数据库中是否有与username重名的用户
     * @param username
     * @return true 表示数据库中有与username登录名重复的用户， false表示没有重复的用户
     */
    public boolean isUserRepeat(String username){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_SQL, username);
    	if(list.size()>1) return true;
    	return false;
    }
    
	private final static String SYSTEM_ADMIN = "acsSystemAdmin";
	private final static String SECURITY_ADMIN = "acsSecurityAdmin";
	private final static String AUDIT_ADMIN = "acsAuditAdmin";
	private final static String USER_SQL = "select id, name from acs_user where login_name=? and deleted=0";
	private final static String USER_COMPANY_SQL = "select id, name,fk_company_id,sub_company_id,password,login_name,sub_company_name from acs_user where login_name=? and deleted=0 and sub_company_id is null and fk_company_id=?";
	private final static String USER_DEPARTMENT_SQL = "select id, name,fk_company_id,sub_company_id,password,login_name,sub_company_name from acs_user where login_name=? and deleted=0 and sub_company_id=? and fk_company_id=?";
	private final static String LOGIN_LOG_SQL = 
		"insert into acs_login_log(deleted, ts, admin_log, fk_company_id, ip_address, login_time," +
		"user_id, user_name, operator_type,sub_company_id,login_name,sub_company_name) values(?,?,?,?,?,?,?,?,?,?,?,?)";
	private final static String LOGIN_LOG_SQL_CONTAINS_ID = 
		"insert into acs_login_log(id, deleted, ts, admin_log, fk_company_id, ip_address, login_time," +
		"user_id, user_name, operator_type,sub_company_id,login_name,sub_company_name) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final static String USER_ROLES_SQL = "SELECT acs_role.code FROM acs_role " +
			"join acs_role_user on acs_role.id=acs_role_user.fk_role_id " +
			"join acs_user on acs_role_user.fk_user_id=acs_user.id where acs_user.id=? and acs_user.deleted=? and acs_role_user.deleted=? and acs_role.deleted=?";
	private final static String LOGIN_LOG_SELECT_SQL="update acs_login_log set exit_time=? where user_id=? and deleted=? and fk_company_id=? and exit_time is null";
	
	private static String JDBC_URL;
	
	static String getJdbcUrl(){
		if(JDBC_URL == null){
			JDBC_URL = getProp("hibernate.connection.url");
		}
		return JDBC_URL;
	}
	
	private static String getProp(String key){
		Properties propert = new Properties();
		try {
			propert.load(LoginSettngService.class.getClassLoader().getResourceAsStream("cas.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return propert.getProperty(key);
	}
}
