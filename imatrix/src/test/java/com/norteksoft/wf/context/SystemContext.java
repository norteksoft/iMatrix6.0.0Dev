package com.norteksoft.wf.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.norteksoft.product.util.ContextUtils;

public class SystemContext implements ApplicationContextAware{

	public SystemContext() {
		System.out.println("============== start init WebContextUtils ==============");
	}
	
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		ContextUtils.setContext(arg0);
	}

}
