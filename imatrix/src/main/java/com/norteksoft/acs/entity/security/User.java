package com.norteksoft.acs.entity.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.GrantedAuthority;

import com.norteksoft.acs.base.enumeration.SecretGrade;

public class User extends org.springframework.security.userdetails.User {
	private static final long serialVersionUID = 1L;
	private Long userId;
	private Long companyId;
	private String companyCode;
	private String companyName;
	private String trueName;
	private String honorificTitle;
	private String password;
	private String email;
	private String theme;
	private Integer dr = 0;
	private SecretGrade secretGrade; // 用户密级
	private String roleCodes; // 角色编号
	private Long subCompanyId;//用户所在分支机构id
	private Long departmentId;//用户的正职部门id
	private String subCompanyCode;//用户所在分支机构编码
	private String subCompanyName;//用户所在分支机构名称
	private String subCompanyShortTitle;//用户所在分支机构简称
	private String loginName;//用户登录名
	
	private Map<Object, Object> otherInfos;
	
	public User(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);

	}
	
	public User(Long userId, String username, String password, String email, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, GrantedAuthority[] authorities, 
			Long companyId, String companyCode, String companyName,SecretGrade secretGrade)
			throws IllegalArgumentException {
		
		this(username, password, enabled, accountNonExpired, 
				credentialsNonExpired, accountNonLocked, authorities);
		this.password = password;
		this.email = email;
		this.userId = userId;
		this.companyId = companyId;
		this.companyCode = companyCode;
		this.companyName = companyName;
		this.secretGrade = secretGrade;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Map<Object, Object> getOtherInfos() {
		if(otherInfos == null) otherInfos = new HashMap<Object, Object>();
		return otherInfos;
	}

	public void setOtherInfos(Map<Object, Object> otherInfos) {
		this.otherInfos = otherInfos;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getHonorificTitle() {
		return honorificTitle;
	}
	
	public void setHonorificTitle(String honorificTitle) {
		this.honorificTitle = honorificTitle;
	}

	public SecretGrade getSecretGrade() {
		return secretGrade;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Long getSubCompanyId() {
		return subCompanyId;
	}

	public void setSubCompanyId(Long subCompanyId) {
		this.subCompanyId = subCompanyId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getSubCompanyCode() {
		return subCompanyCode;
	}

	public void setSubCompanyCode(String subCompanyCode) {
		this.subCompanyCode = subCompanyCode;
	}

	public String getSubCompanyName() {
		return subCompanyName;
	}

	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}

	public String getSubCompanyShortTitle() {
		return subCompanyShortTitle;
	}

	public void setSubCompanyShortTitle(String subCompanyShortTitle) {
		this.subCompanyShortTitle = subCompanyShortTitle;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
