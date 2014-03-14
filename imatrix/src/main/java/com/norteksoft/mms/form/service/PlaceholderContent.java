package com.norteksoft.mms.form.service;

public interface PlaceholderContent {
	/**
	 * 获得占位符控件相应的内容
	 * @param placehoderId  占位符控件id
	 * @param isPrint  是否是打印页面，当使用了打印标签(formPrint)时该参数为true，否则为false
	 * @return
	 */
	public String placeholderContent(String placehoderId,Object entity,boolean isPrint);

}
