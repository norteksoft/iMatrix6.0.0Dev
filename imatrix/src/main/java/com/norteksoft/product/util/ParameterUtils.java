package com.norteksoft.product.util;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;


public class ParameterUtils {
	private static ThreadLocal<ThreadParameters> threadParameters=new ThreadLocal<ThreadParameters>();
	
	public static void setParameters(ThreadParameters parameters){
		threadParameters.set(parameters);
	}
	
	public static Long getCompanyId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		Long companyId = parameter.getCompanyId();
		if(companyId!=null) return companyId;
		String companyCode = parameter.getCompanyCode();
		if(StringUtils.isNotEmpty(companyCode)){
			com.norteksoft.acs.service.organization.CompanyManager cm=(com.norteksoft.acs.service.organization.CompanyManager)ContextUtils.getBean("companyManager");
			com.norteksoft.acs.entity.organization.Company company=cm.getCompanyByCode(companyCode);
			if(company==null)return null;
			return company.getId();
		}
		return null;
	}
	
	public static Long getUserId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		Long userId = parameter.getUserId();
		if(userId!=null)return userId;
		String loginName= parameter.getLoginName();
		if(StringUtils.isNotEmpty(loginName)){
			User user = ApiFactory.getAcsService().getUserByLoginName(loginName);
			if(user!=null){
				return user.getId();
			}
		}
		return null;
	}
	public static String getCompanyCode(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String companyCode = parameter.getCompanyCode();
		if(StringUtils.isNotEmpty(companyCode))return companyCode;
		Long companyId = parameter.getCompanyId();
		if(companyId!=null){
			com.norteksoft.acs.service.organization.CompanyManager cm=(com.norteksoft.acs.service.organization.CompanyManager)ContextUtils.getBean("companyManager");
			com.norteksoft.acs.entity.organization.Company company=cm.getCompany(companyId);
			if(company==null)return null;
			return company.getCode();
		}
		return null;
	}
	
	public static String getCompanyName(){
		Long companyId = getCompanyId();
		if(companyId!=null){
			com.norteksoft.acs.service.organization.CompanyManager cm=(com.norteksoft.acs.service.organization.CompanyManager)ContextUtils.getBean("companyManager");
			com.norteksoft.acs.entity.organization.Company company=cm.getCompany(companyId);
			if(company==null)return null;
			return company.getName();
		}
		return null;
	}
	
	public static String getUserName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String userName= parameter.getUserName();
		if(StringUtils.isNotEmpty(userName))return userName;
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getName();
	}
	public static String getPassword(){
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getPassword();
	}
	public static String getHonorificTitle(){
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getHonorificName();
	}
	public static String getLoginName(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String loginName= parameter.getLoginName();
		if(StringUtils.isNotEmpty(loginName))return loginName;
		Long userId = parameter.getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getLoginName();
	}
	public static Long getSystemId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getSystemId();
	}
	public static String getSystemCode(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		return parameter.getSystemCode();
	}
	
	public static Long getDepartmentId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		Long departmentId = parameter.getDepartmentId();
		if(departmentId!=null)return departmentId;
		Long userId=getUserId();
		if(userId==null)return null;
		com.norteksoft.product.api.entity.User user=ApiFactory.getAcsService().getUserById(userId);
		if(user==null)return null;
		return user.getMainDepartmentId();
	}
	
	public static Long getSubCompanyId(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		Long subCompanyId = parameter.getSubCompanyId();
		if(subCompanyId!=null)return subCompanyId;
		String subCompanyCode = parameter.getSubCompanyCode();
		if(StringUtils.isNotEmpty(subCompanyCode)){
			com.norteksoft.product.api.entity.Department subCompany = ApiFactory.getAcsService().getDepartmentByCode(getSubCompanyCode());
			if(subCompany==null){
				return null;
			}else{
				return subCompany.getSubCompanyId();
			}
		}
		return null;
		
	}
	
	public static String getSubCompanyName(){
		com.norteksoft.product.api.entity.Department subCompany = getSubCompany();
		if(subCompany == null){
			return null;
		}else{
			return subCompany.getName();
		}
	}
	
	public static String getSubCompanyCode(){
		ThreadParameters parameter=threadParameters.get();
		if(parameter==null)return null;
		String subCompanyCode = parameter.getSubCompanyCode();
		if(StringUtils.isNotEmpty(subCompanyCode)) return subCompanyCode;
		com.norteksoft.product.api.entity.Department subCompany = getSubCompany();
		if(subCompany == null){
			return null;
		}else{
			return subCompany.getCode();
		}
	}
	
	public static String getSubCompanyShortTitle(){
		com.norteksoft.product.api.entity.Department subCompany = getSubCompany();
		if(subCompany == null){
			return null;
		}else{
			return subCompany.getShortTitle();
		}
	}
	private static com.norteksoft.product.api.entity.Department getSubCompany(){
		if(getSubCompanyId()==null)return null;
		com.norteksoft.product.api.entity.Department subCompany = ApiFactory.getAcsService().getDepartmentById(getSubCompanyId());
		return subCompany;
	}
}
