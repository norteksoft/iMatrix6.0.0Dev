package com.norteksoft.tags.grid;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.product.util.ContextUtils;
public class JavaScriptCssTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(JavaScriptCssTag.class);
	
	private String code;//多个表单编码之间以逗号隔开
	private String version;//多个表单编码之间以逗号隔开

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public int doStartTag() throws JspException {
		FormViewManager formViewManager = (FormViewManager) ContextUtils.getBean("formViewManager");
		StringBuilder jcs = new StringBuilder();
		FormView form = null;
		String[] codes = code.split(",");
		String[] versions = null;
		if(StringUtils.isNotEmpty(version)){
			versions = version.split(",");
		}
		for(int i=0;i<codes.length;i++){
			if(StringUtils.isNotEmpty(version)){
				form = formViewManager.getCurrentFormViewByCodeAndVersion(codes[i], Integer.parseInt(versions[i]));
			}else{
				form = formViewManager.getHighFormViewByCode(code);
			}
			if(form!=null){
				jcs.append(formViewManager.getJavaScriptCss( form.getHtml()));
			}
		}
		JspWriter out = pageContext.getOut();
		try {
			out.print(jcs);
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
