package com.norteksoft.product.orm.hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
import org.springframework.stereotype.Service;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;


@Service
public class SaveOrUpdateListener extends DefaultSaveOrUpdateEventListener {

	private static final long serialVersionUID = 3175703536757344524L;

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
		Object obj = event.getObject();
		if(obj instanceof IdEntity){
			IdEntity entity = (IdEntity) obj;
			if(entity.getId() == null){
				setEntityCreatorInfo(entity);
			}else{
				setEntityModifierInfo(entity);
			}
		}
		super.onSaveOrUpdate(event);
	}

	private void setEntityCreatorInfo(IdEntity entity){
		if(entity.getCompanyId()==null){
			entity.setCompanyId(ContextUtils.getCompanyId());
		}
		if(StringUtils.isEmpty(entity.getCreator())){
			entity.setCreator(ContextUtils.getLoginName());
		}
		if(StringUtils.isEmpty(entity.getCreatorName())){
			entity.setCreatorName(ContextUtils.getUserName());
		}
		if(entity.getDepartmentId()==null){
			entity.setDepartmentId(ContextUtils.getDepartmentId());
		}
		if(entity.getCreatorId()==null){
			entity.setCreatorId(ContextUtils.getUserId());
		}
		if(entity.getSubCompanyId()==null){
			entity.setSubCompanyId(ContextUtils.getSubCompanyId());
		}
		try {
			SimpleDateFormat time_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			entity.setCreatedTime(time_format.parse(time_format.format(new Date())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setEntityModifierInfo(IdEntity entity){
		entity.setModifier(ContextUtils.getLoginName());
		entity.setModifierName(ContextUtils.getUserName());
		entity.setModifiedTime(new Date());
	}
}
