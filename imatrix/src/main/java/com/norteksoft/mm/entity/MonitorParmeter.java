package com.norteksoft.mm.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntity;


/**
 * 性能参监控参数
 */
@Entity
@Table(name="MONITOR_PARMETER")
public class MonitorParmeter extends IdEntity {
	private static final long serialVersionUID = 1L;
	
	private String trace_max_size_http="100";  //最大用时
	
	private String trace_filter_active_time_http="1000"; //最多存储量
	
	private String trace_max_size_jdbc="100";  //最大用时
	
	private String trace_filter_active_time_jdbc="1000"; //最多存储量
	
	private String trace_max_size_meth="100";  //最大用时
	
	private String trace_filter_active_time_meth="1000"; //最多存储量
	
	private String driver_clazzs; //数据库驱动
	
	@Column(length=3500)
	private String detect_clazzs; //要监控的类
	
	 
	private String systemCode;//系统
	
	private String systemName;//系统


	public String getTrace_max_size_http() {
		return trace_max_size_http;
	}

	public void setTrace_max_size_http(String traceMaxSizeHttp) {
		trace_max_size_http = traceMaxSizeHttp;
	}

	public String getTrace_filter_active_time_http() {
		return trace_filter_active_time_http;
	}

	public void setTrace_filter_active_time_http(String traceFilterActiveTimeHttp) {
		trace_filter_active_time_http = traceFilterActiveTimeHttp;
	}

	public String getTrace_max_size_jdbc() {
		return trace_max_size_jdbc;
	}

	public void setTrace_max_size_jdbc(String traceMaxSizeJdbc) {
		trace_max_size_jdbc = traceMaxSizeJdbc;
	}

	public String getTrace_filter_active_time_jdbc() {
		return trace_filter_active_time_jdbc;
	}

	public void setTrace_filter_active_time_jdbc(String traceFilterActiveTimeJdbc) {
		trace_filter_active_time_jdbc = traceFilterActiveTimeJdbc;
	}

	public String getTrace_max_size_meth() {
		return trace_max_size_meth;
	}

	public void setTrace_max_size_meth(String traceMaxSizeMeth) {
		trace_max_size_meth = traceMaxSizeMeth;
	}

	public String getTrace_filter_active_time_meth() {
		return trace_filter_active_time_meth;
	}

	public void setTrace_filter_active_time_meth(String traceFilterActiveTimeMeth) {
		trace_filter_active_time_meth = traceFilterActiveTimeMeth;
	}

	public String getDriver_clazzs() {
		return driver_clazzs;
	}

	public void setDriver_clazzs(String driverClazzs) {
		driver_clazzs = driverClazzs;
	}

	public String getDetect_clazzs() {
		return detect_clazzs;
	}

	public void setDetect_clazzs(String detectClazzs) {
		detect_clazzs = detectClazzs;
	}


	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	
}
