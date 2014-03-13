package com.example.expense.base.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.product.web.struts2.Struts2Utils;


@Namespace("/enum")
@ParentPackage("default")
public class EnumAction {

	private static final long serialVersionUID = 1L;
	private String enumPath;
	
	@Action("get-value")
	public String getValue() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		String callback=Struts2Utils.getParameter("callback");
		StringBuilder result = new StringBuilder();
		Object[] enumValues = Class.forName(enumPath).getEnumConstants();
		for (Object object : enumValues) {
			String value = Struts2Utils.getText(BeanUtils.getProperty(object, "code"));
			result.append(object.toString()).append(":").append(value).append(",");
		}
		this.renderText(callback+"({msg:\""+result.toString()+"\"})");
		return null;
	}
	
	protected String render(String text, String contentType) {
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 直接输出字符串.
	 */
	protected String renderText(String text) {
		return render(text, "text/plain;charset=UTF-8");
	}
	public String getEnumPath() {
		return enumPath;
	}

	public void setEnumPath(String enumPath) {
		this.enumPath = enumPath;
	}

}
