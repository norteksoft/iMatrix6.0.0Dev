package com.norteksoft.cas.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.DefaultPasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import javax.sql.DataSource;

public class UsernamePasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	
	private static final String LINK_CHAR="~~";
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private DataSource dataSource;
	private DefaultPasswordEncoder passwordEncoder;
	
	@Override
	protected boolean authenticateUsernamePasswordInternal(
			UsernamePasswordCredentials credentials)throws AuthenticationException {
		
		String username = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        if(username.contains("%")){//表示是swing“办公助手”中访问v1/tickets过来的，有%表示用户登录名或分支编码有中文，已进行编码URLEncoder.encode(username, "utf-8")，需要解码
        	try {
        		username = URLDecoder.decode(username, "utf-8");
        	} catch (UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        return authenticate(username, password);
	}
	
	public boolean authenticate(final String username, final String password){
		Object[] user = getUser(username);
		String databasePassword = user[4].toString();
		String encodePassword=password;
		if(password!=null && password.length()!=32)encodePassword=passwordEncoder.encode(password);
		return encodePassword.equals(databasePassword);
	}
	
	
	
	public UsernamePasswordAuthenticationHandler setJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate=simpleJdbcTemplate;
		return this;
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
    	List<Map<String, Object>> list = simpleJdbcTemplate.queryForList(COMPANY_SQL, companyCode);
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
    	List<Map<String, Object>> list = simpleJdbcTemplate.queryForList(DEPARTMENT_SQL, companyCode);
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
    	List<Map<String, Object>> list  = simpleJdbcTemplate.queryForList(USER_DEPARTMENT_SQL, username,subCompanyId,companyId);
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
    	List<Map<String, Object>> list = simpleJdbcTemplate.queryForList(USER_COMPANY_SQL, username,companyId);
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
    	List<Map<String, Object>> list = simpleJdbcTemplate.queryForList(COMPANY_ID_SQL, username);
    	if(list.isEmpty()) return null;
    	Object obj = list.get(0).get("fk_company_id");
    	return Long.valueOf(obj.toString());
    }

	public void setDataSource(DataSource dataSource) {
		simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	public void setPasswordEncoder(DefaultPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
