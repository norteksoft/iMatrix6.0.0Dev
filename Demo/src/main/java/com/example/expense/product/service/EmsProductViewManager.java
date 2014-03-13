package com.example.expense.product.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.utils.view.ComboxValues;

@Service
@Transactional
public class EmsProductViewManager implements ComboxValues{

	public Map<String, String> getValues(Object entity) {
		StringBuilder result=new StringBuilder();
		Map<String,String> map=new HashMap<String, String>();
		result.append("'name1':'姓名1',")
		.append("'name2':'姓名2',")
		.append("'name3':'姓名3',")
		.append("'name4':'姓名4',")
		.append("'name5':'姓名5'");
		map.put("interfaceVal", result.toString());
		StringBuilder result2=new StringBuilder();
		result2.append("'1':'整型1',")
		.append("'2':'整型2',")
		.append("'3':'整型3',")
		.append("'4':'整型4',")
		.append("'5':'整型5'");
		map.put("interfaceInt", result2.toString());
		return map;
	}
}
