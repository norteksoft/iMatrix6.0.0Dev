package com.norteksoft.cas.authentication;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class AuthenticationPatternHandler {
	
    private static final String PATTERN_SQL =
    	// sqlserver 使用
    	// "SELECT c.ldap_invocation,c.ldap_type,c.ldap_url,c.ldap_username,c.ldap_password,c.rtx_invocation,c.rtx_url,c.[external], c.external_type,c.external_url " +
    	"SELECT c.ldap_invocation,c.ldap_type,c.ldap_url,c.ldap_username,c.ldap_password,c.rtx_invocation,c.rtx_url,c.extern, c.external_type,c.external_url " +
    	"FROM acs_server_config c join acs_user u on c.company_id=u.fk_company_id where u.deleted=0 and u.id=?";
    
    private static final String LINK_CHAR="~~";
    
	@NotNull
    private SimpleJdbcTemplate jdbcTemplate;
    
    @NotNull
    private DataSource dataSource;
    
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
    
    public AuthenticationPattern getAuthenticationPattern(String username){
        try {
        	Object[] user = getUser(username);
        	if(user==null)return null;
        	String loginName = user[5].toString();
        	if(isAdmin(loginName)) return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        	List<Map<String, Object>> setting = getJdbcTemplate().queryForList(PATTERN_SQL, Long.parseLong(user[0].toString()));
        	if(setting.isEmpty()){
        		return null;
        	}
        	Map<String, Object> prmt = setting.get(0);
        	Object ldapInvocation = prmt.get("ldap_invocation");
        	Object rtxInvocation = prmt.get("rtx_invocation");
        	Object external = prmt.get("extern");
        	if(getBoolean(ldapInvocation)){ // ldap 认证
        		return createLdapPattern(prmt);
        	}else if(getBoolean(rtxInvocation)){ // rtx 认证
        		return new AuthenticationPattern(
        				AuthenticationPattern.Pattern.RTX,
        				objToString(prmt.get("rtx_url")));
        	}else if(getBoolean(external)){ // 其他方式认证
        		return createExternalPattern(prmt);
        	}
        	return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        } catch (final IncorrectResultSizeDataAccessException e) {
        	return new AuthenticationPattern(AuthenticationPattern.Pattern.DATABASE);
        }
    }
    
    private boolean isAdmin(String loginName){
    	return loginName!=null && (
    			loginName.endsWith(".systemAdmin") || 
    			loginName.endsWith(".securityAdmin") || 
    			loginName.endsWith(".auditAdmin"));
    }
    
    private AuthenticationPattern createLdapPattern(Map<String, Object> prmt){
    	AuthenticationPattern.Pattern pattern = null;
    	String type = objToString(prmt.get("ldap_type"));
    	String cn = objToString(prmt.get("ldap_username"));
    	if("APACHE".equals(type)){
    		pattern = AuthenticationPattern.Pattern.LDAP;
    		cn = "UID="+cn+",OU=system";
    	}else if("DOMINO".equals(type)){
    		pattern = AuthenticationPattern.Pattern.DOMINO;
    		cn = "cn="+cn;
    	}else if("WINDOWS_AD".equals(type)){
    		pattern = AuthenticationPattern.Pattern.WINDOWS_AD;
    	}
		return new AuthenticationPattern(pattern,
				objToString(prmt.get("ldap_url")), 
				cn, objToString(prmt.get("ldap_password")));
    }
    
    private AuthenticationPattern createExternalPattern(Map<String, Object> prmt){
    	String type = objToString(prmt.get("external_type"));
    	String url = objToString(prmt.get("external_url"));
    	if("HTTP".equals(type)){
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.HTTP, url);
    	}else if("RESTFUL".equals(type)){
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.RESTFUL, url);
    	}else {
    		return new AuthenticationPattern(AuthenticationPattern.Pattern.WEBSERVICE, url);
    	}
    }
    
    private boolean getBoolean(Object obj){
    	if(obj instanceof Number){
    		return ((Number)obj).intValue()==1;
    	}else if(obj instanceof Boolean){
    		return (Boolean)obj;
    	}
    	return false;
    }
    
    private String objToString(Object obj){
    	if(obj == null) return "";
    	return obj.toString();
    }
    
  //前台传过来的登录名是否带有公司或分支机构编码，并获得用户和公司、分支机构信息
	private String[] getUserInfo(String username){
		String[] names = new String[2];
		if(username.indexOf(LINK_CHAR)>=0){
			names = username.split(LINK_CHAR);//loginName==companyCode
			return names;
		}else{
			names[0] = username;
			names[1] = "";
		}
		return names;
	}
	
	/**
	 * 
	 * @param username:loginName或loginName+=companyCode
	 * @return object[]:obj[0]是用户id，obj[1]是用户姓名，obj[2]是用户所在公司id，obj[3]是分支机构id,obj[4]是用户密码,obj[5]是用户登录名
	 */
	private Object[] getUser(String username){
		String companyCode = "";
		String[] names = getUserInfo(username);
		String loginName = names[0];
		//公司编码或分支机构编码
		companyCode = names[1]; 
		
		Long companyId = null;
		Long subcompanyId = null;
		Object[] user = null;
		if(StringUtils.isNotEmpty(companyCode)){//如果公司编码或分支机构编码不为空（登录界面是带@的页面）
			companyId = getCompanyIdByCode(companyCode);
			if(companyId==null){
				Object[] branchInfo = getBranchInfoByCode(companyCode);
				if(branchInfo!=null){
					subcompanyId = Long.parseLong(branchInfo[0].toString());
					companyId = Long.parseLong(branchInfo[1].toString());
					user = getUserInBranch(loginName,subcompanyId,companyId);
				}
			}else{
				user = getUser(loginName,companyId);
			}
		}else{//登录界面是没有@的页面			
			companyId = getCompanyId(loginName);
			user = getUser(loginName,companyId);
		}
		return user;
	}
	
	private final static String COMPANY_SQL = "select id from acs_company where code=? and deleted=0";
	private final static String DEPARTMENT_SQL = "select id,fk_company_id from acs_department where code=? and deleted=0";
	private final static String USER_DEPARTMENT_SQL = "select id, name,fk_company_id,sub_company_id,password,login_name from acs_user where login_name=? and deleted=0 and sub_company_id=? and fk_company_id=?";
	private final static String USER_COMPANY_SQL = "select id, name,fk_company_id,sub_company_id,password,login_name from acs_user where login_name=? and deleted=0 and sub_company_id is null and fk_company_id=?";
	private final static String COMPANY_ID_SQL = "select fk_company_id from acs_user where login_name=? and deleted=0";
	
	
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
     * 在分支机构中查询用户ID和用户姓名
     * @param username
     * @return null 用户不存在
     */
    public Object[] getUserInBranch(String username,Long subCompanyId,Long companyId){
    	List<Map<String, Object>> list  = jdbcTemplate.queryForList(USER_DEPARTMENT_SQL, username,subCompanyId,companyId);
    	if(list.size()<=0)return null;
    	Object[] result = new Object[6];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("name");
    	result[2] = list.get(0).get("fk_company_id");
    	result[3] = list.get(0).get("sub_company_id");
    	result[4] = list.get(0).get("password");
    	result[5] = list.get(0).get("login_name");
    	return result;
    }
    /**
     * 在总公司（非分支机构内）内查询用户ID和用户姓名
     * @param username
     * @return null 用户不存在
     */
    public Object[] getUser(String username,Long companyId){
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(USER_COMPANY_SQL, username,companyId);
    	if(list.size()<=0)return null;
    	Object[] result = new Object[6];
    	result[0] = list.get(0).get("id");
    	result[1] = list.get(0).get("name");
    	result[2] = list.get(0).get("fk_company_id");
    	result[3] = list.get(0).get("sub_company_id");
    	result[4] = list.get(0).get("password");
    	result[5] = list.get(0).get("login_name");
    	return result;
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
}
