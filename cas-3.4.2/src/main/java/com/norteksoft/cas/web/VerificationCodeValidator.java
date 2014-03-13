package com.norteksoft.cas.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.execution.RequestContext;

import cbm.norteksoft.utilSecret.java.License;

import com.norteksoft.cas.service.LoginSettngService;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

public class VerificationCodeValidator {
	protected final Logger log = LoggerFactory.getLogger(VerificationCodeValidator.class);
	// private static final long HALF_HOUR = 30*60*1000L;
	private static final String ERROR_CODE = "error.authentication.verification.code.bad";
	private static final String USER_NOT_FOUND = "error.authentication.username.not.found";
	private static final String USER_REPEAT = "error.authentication.username.repeat";
	private static final String COMPNAY_NOT_FOUND = "error.authentication.compnay.not.found";
	private static final String NO_COMPNAY_CODE = "error.authentication.no.compnay.code";
	private static final String LOCKED_CODE = "error.authentication.user.locked";
	private static final String LICENCE_INVALIDATION_CODE = "error.authentication.licence.invalidation";
	private static final String LINK_CHAR="~~";
	
	public VerificationCodeValidator(){
		super();
		timeoutTimer();
	}
	
	// private Map<String, Integer> failedCountMap = new HashMap<String, Integer>();
	// private Map<String, Long> failedTimeMap = new HashMap<String, Long>();
	private Map<String, LoginInfo> loginInfos = new HashMap<String, LoginInfo>();
	
	private ImageCaptchaService jcaptchaService;
	private LoginSettngService loginSettngService;
	private String validationParameter = "_captcha_parameter";
	
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
	
	public String validate(final RequestContext context, final Credentials credentials, final MessageContext messageContext){
		String username = ((UsernamePasswordCredentials)credentials).getUsername();
		String userinfo = username;//存储前台传过来的用户和公司、分支机构编码信息
		String companyCode = "";
		String[] names = getUserInfo(username);
		username = names[0];
		if(names.length==1){//表示登录界面带有@的页面中没有输入公司编码或分支机构编码
			addErrorMsg(messageContext, NO_COMPNAY_CODE);
			return "error";
		}else{
			//公司编码或分支机构编码
			companyCode = names[1]; 
		}
		
		Long companyId = null;
		Long subcompanyId = null;
		Object[] user = null;
		if(StringUtils.isNotEmpty(companyCode)){//如果公司编码或分支机构编码不为空（登录界面是带@的页面）
			companyId = loginSettngService.getCompanyIdByCode(companyCode);
			if(companyId==null){
				Object[] branchInfo = loginSettngService.getBranchInfoByCode(companyCode);
				if(branchInfo==null){
					addErrorMsg(messageContext, COMPNAY_NOT_FOUND);
					return "error";
				}else{
					subcompanyId = Long.parseLong(branchInfo[0].toString());
					companyId = Long.parseLong(branchInfo[1].toString());
					user = loginSettngService.getUserInBranch(username,subcompanyId,companyId);
					if(user==null){
						addErrorMsg(messageContext, USER_NOT_FOUND);
						return "error";
					}
				}
			}else{
				user = loginSettngService.getUser(username,companyId);
				if(user==null){
					addErrorMsg(messageContext, USER_NOT_FOUND);
					return "error";
				}
			}
		}else{//登录界面是没有@的页面
			//查询数据库中是否有与username重名的用户
			boolean isUserRepeat = loginSettngService.isUserRepeat(username);
			if(isUserRepeat){//有重名用户
				addErrorMsg(messageContext, USER_REPEAT);
				return "error";
			}else{//没有重名用户
				companyId = loginSettngService.getCompanyId(username);
				user = loginSettngService.getUser(username,companyId);
			}
		}
		
		if(user == null){
			addErrorMsg(messageContext, USER_NOT_FOUND);
			return "error";
		}
		// License
//        try {
//            Map licence = License.getLicense();
//            String dateString = (String)licence.get("end_time");
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            Date settingDate = df.parse(dateString);
//            Date date = loginSettngService.getLastTime(companyId);
//            if(date != null && settingDate.before(date)){
//            	log.debug("licence validate failed. ");
//            	addErrorMsg(messageContext, LICENCE_INVALIDATION_CODE);
//                return "error";
//            }
//        } catch(Exception e) {
//        	log.debug("validate licence error. ", e);
//        	addErrorMsg(messageContext, LICENCE_INVALIDATION_CODE);
//            return "error";
//        }
		// End License
		
		Long userId = Long.parseLong(user[0].toString());
		Boolean userLocked = loginSettngService.isUserLocked(userId);
		LoginInfo info = loginInfos.get(userinfo);
		
		if(userLocked == null){ // 用户名不存在
			addErrorMsg(messageContext, USER_NOT_FOUND);
			return "error";
		}else if(userLocked){ // 数据库用户已锁定
			if(info != null && !info.isLocked()){ // 已过解锁时间
				info = null;
				loginSettngService.unlockUser(userId);
				loginInfos.remove(userinfo);
			}else{
				addErrorMsg(messageContext, LOCKED_CODE);
				return "error";
			}
		}else if(info != null && info.isLocked()){ // 已手动解锁
			info = null;
			loginSettngService.unlockUser(userId);
			loginInfos.remove(userinfo);
		}
		
		if(info != null && info.showVerificationCode()){
			if(compareCaptcha(context)){
				return "success";
			}
			addErrorMsg(messageContext, ERROR_CODE);
			final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
			request.setAttribute("showCode", true);
			return "error";
		}
		return "success";
	}
	
	private void addErrorMsg(MessageContext messageContext, String code){
		messageContext.addMessage(new MessageBuilder().error().code(code).defaultText(code).build());
	}
	
	public void success(final RequestContext context, final Credentials credentials){
		String username = ((UsernamePasswordCredentials)credentials).getUsername();
		loginInfos.remove(username);

		Object[] user = getUser(username);
		Long userId = null;
		if(user!=null){
			userId = Long.parseLong(user[0].toString());
		}
		// 记录登录日志
		Boolean userEnabled  = loginSettngService.getUserEnabled(userId);
		if(userEnabled){
			loginSettngService.loginLog(user, WebUtils.getHttpServletRequest(context).getRemoteHost());
		}
	}
	
	public void countFailed(final RequestContext context, 
			final Credentials credentials, final MessageContext messageContext){
		final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
		Message[] msgs = messageContext.getAllMessages();
		if(msgs.length > 0){
			Message credentialsBad = getMsg(messageContext, "error.authentication.credentials.bad");
			if(credentialsBad != null && credentialsBad.getText().equals(msgs[0].getText())){ // 密码错误
				String username = ((UsernamePasswordCredentials)credentials).getUsername();
				
				Object[] user = getUser(username);
				Long userId = null;
				if(user!=null){
					userId = Long.parseLong(user[0].toString());
				}
				LoginInfo info = loginInfos.get(username);
				if(info == null){ // 第一次登陆失败
					info = createLoginInfo(userId);
				}else{ // 多次失败
					info.failedCount = info.failedCount+1;
				}
				
				loginInfos.put(username, info);
				
				if(info.failedCount >= info.count){ // 达到失败次数
					if(info.afterFailed == 0){ // 显示验证码
						request.setAttribute("showCode", true);
						info.startTime = System.currentTimeMillis();
					}else if(info.afterFailed == 1){ // 锁定用户
						info.startTime = System.currentTimeMillis();
						messageContext.clearMessages();
						messageContext.addMessage(new MessageBuilder().error().code(LOCKED_CODE).defaultText(LOCKED_CODE).build());
						if(userId!=null){
							//通知数据库锁定用户
							loginSettngService.lockUser(userId);
						}
					}
				}
			}
		}
	}
	/**
	 * 
	 * @param username:loginName或loginName+=companyCode
	 * @return object[]:obj[0]是用户id，obj[1]是用户姓名，obj[2]是用户所在公司id，obj[3]是分支机构id,obj[4]是用户密码,obj[5]是用户登录名,obj[6]是分支机构名
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
			companyId = loginSettngService.getCompanyIdByCode(companyCode);
			if(companyId==null){
				Object[] branchInfo = loginSettngService.getBranchInfoByCode(companyCode);
				if(branchInfo!=null){
					subcompanyId = Long.parseLong(branchInfo[0].toString());
					companyId = Long.parseLong(branchInfo[1].toString());
					user = loginSettngService.getUserInBranch(loginName,subcompanyId,companyId);
				}
			}else{
				user = loginSettngService.getUser(loginName,companyId);
			}
		}else{//登录界面是没有@的页面			
			companyId = loginSettngService.getCompanyId(loginName);
			user = loginSettngService.getUser(loginName,companyId);
		}
		return user;
	}
 	
	private void timeoutTimer(){
		Timer timer = new Timer("login-info-timer", true);
		TimerTask task = new TimerTask(){
			public void run() {
				try {
					timeoutLoginInfo();
				} catch (Exception e) {
					log.debug("validate login info time out error. ", e);
				}
			}};
		timer.schedule(task, 30*1000l, 30*1000l);
	}
	
	// 定时操作，如果是锁定的，等到锁定时间过了后移除，如果是出验证码的，等待五分钟过后移除信息
	private void timeoutLoginInfo(){
		log.debug(" start validate login info time out ...");
		for(Map.Entry<String, LoginInfo> li : loginInfos.entrySet()){
			if(li.getValue().isTimeout()){
				loginInfos.remove(li.getKey());
			}
		}
	}
	
	private LoginInfo createLoginInfo(Long userId){
		if(userId==null)return null;
		Map<String, Object> map = loginSettngService.getSecuritySetting(userId);
		LoginInfo info = new LoginInfo(userId);
		if(map!=null && !map.isEmpty()){
			info.count = Integer.parseInt(map.get("value").toString());
			info.afterFailed = Integer.parseInt(map.get("fail_set_type").toString());
			info.lockTime = Long.parseLong(map.get("locked_time").toString())*60*1000L;
		}
		return info;
	}
	
	private Message getMsg(MessageContext messageContext, String code){
		MessageSource ms = ((DefaultMessageContext)messageContext).getMessageSource();
		Message msg = new MessageBuilder().error().code(code)
			.defaultText(code).build().resolveMessage(
					ms, LocaleContextHolder.getLocale());
		return msg;
	}
	
	public boolean compareCaptcha(RequestContext context){
		String captchaResponse = context.getRequestParameters().get(validationParameter);
		if(captchaResponse != null){
			String id = WebUtils.getHttpServletRequest(context).getSession().getId();
			if(id != null){
				try {
					return jcaptchaService.validateResponseForID(id,captchaResponse).booleanValue();
				} catch (CaptchaServiceException cse) { }
	         }
		}
		return false;
	}
	
	public void setJcaptchaService(ImageCaptchaService jcaptchaService) {
		this.jcaptchaService = jcaptchaService;
	}
	
	public void setValidationParameter(String validationParameter) {
		this.validationParameter = validationParameter;
	}
	
	public void setLoginSettngService(LoginSettngService loginSettngService) {
		this.loginSettngService = loginSettngService;
	}
	
	static class LoginInfo{
		int afterFailed = 0; // 失败后 处理类型
		long startTime = System.currentTimeMillis();      // 第一次失败时间
		String username;     // 用户名
		Long userId=0l;//用户id
		int count = 3;
		int failedCount = 1;           // 已失败次数
		long lockTime = 30*60*1000L; // 锁定时间
		
		LoginInfo(String username){
			this.username = username;
		}
		LoginInfo(Long userId){
			this.userId = userId;
		}
		
		boolean showVerificationCode(){ // 是否显示验证码
			return afterFailed==0 && failedCount>=count;
		}
		
		boolean isLocked(){ // 是否锁定
			return afterFailed==1 && failedCount>=count && (System.currentTimeMillis() - startTime - lockTime) < 0;
		}
		
		boolean isLockTimeout(){ // 已过锁定时间
			return (System.currentTimeMillis() - startTime - lockTime) > 0;
		}
		
		boolean isShowCodeTimeout(){ // 已过显示验证码时间，默认为 5 分钟
			return (System.currentTimeMillis() - startTime - 5*60000l) > 0;
		}
		
		boolean isTimeout(){
			if(afterFailed == 0){
				return isShowCodeTimeout();
			}
			//else if(afterFailed == 1){
			//	return isLockTimeout();
			//}
			return false;
		}
	}
}
