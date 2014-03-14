package com.norteksoft.mm.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mm.entity.MonitorParmeter;
import com.norteksoft.mm.service.MonitorParmeterManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
/**
 * 性能参监控参数
 */
@Namespace("/mm")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "monitor-parmeter", type = "redirectAction")})
public class MonitorParmeterAction extends CrudActionSupport<MonitorParmeter> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String code;
	
	private Page<MonitorParmeter> pages= new Page<MonitorParmeter>(50, true);
	
	private MonitorParmeter monitorParmeter;
	
	private List<BusinessSystem> businessSystems;
	
	@Autowired
	private MonitorParmeterManager monitorParmeterManager;
	
	@Autowired
	private BusinessSystemManager businessSystemManager;
	
	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 性能监控/参数设置新建页 
	 */
	@Override
	@Action("monitor-parmeter-input")
	public String input() throws Exception {
		businessSystems=businessSystemManager.getMainBusiness();
		return "monitor-parmeter-input";
	}

	/**
	 * 性能监控/参数设置主入口
	 */
	@Override
	public String list() throws Exception {
		return SUCCESS;
	}
	
	/**
	 * 性能监控/参数设置取数据 
	 * @return
	 * @throws Exception
	 */
	@Action("monitor-parmeter-getList")
	public String getList()throws Exception{
		monitorParmeterManager.getMonitorParmeterPage(pages);
		renderText(PageUtils.pageToJson(pages));
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			monitorParmeter = new MonitorParmeter();
		}else{
			monitorParmeter = monitorParmeterManager.getParmeter(id);
		}
	}

	/**
	 *  性能监控/参数设置保存
	 */
	@Override
	@Action("monitor-parmeter-save")
	public synchronized String save() throws Exception {
		if(id==null&&StringUtils.isNotEmpty(code)){
			MonitorParmeter parmeter = monitorParmeterManager.getMonitorParmeter(code);
			if(parmeter!=null){
				this.renderText("error");
				return null;
			}
			BusinessSystem bu =businessSystemManager.getSystemBySystemCode(code);
			monitorParmeter.setSystemCode(bu.getCode());
			monitorParmeter.setSystemName(bu.getName());
		}
		monitorParmeterManager.saveParmeter(monitorParmeter);
		
		return null;
	}

	public MonitorParmeter getModel() {
		return monitorParmeter;
	}
	
	public List<BusinessSystem> getBusinessSystems() {
		return businessSystems;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Page<MonitorParmeter> getPages() {
		return pages;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
