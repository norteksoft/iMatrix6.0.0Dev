package com.norteksoft.acs.service.authority;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Service
@Transactional(readOnly=true)
public class PermissionItemTypeManager implements ComboxValues{
	public Map<String, String> getValues(Object entity) {
		Map<String,String> map=new HashMap<String, String>();
		StringBuilder result=new StringBuilder();
		result.append("'':'")
		 .append("请选择")
		 .append("'").append(",");
		for(ItemType itemType:ItemType.values()){
			if(itemType!=ItemType.ALL_USER){
				result.append("'").append(itemType.toString()).append("':")
				.append("'").append(Struts2Utils.getText(itemType.getCode())).append("'").append(",");
			}
		}
		if(result.toString().lastIndexOf(",")==result.toString().length()-1){
			map.put("itemType", result.substring(0, result.toString().length()-1));
		}
		return map;
	}
	
}
