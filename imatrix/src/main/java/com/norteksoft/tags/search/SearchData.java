package com.norteksoft.tags.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.mms.base.utils.view.ComboxValues;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.entity.TableColumn;
import com.norteksoft.mms.form.enumeration.DataType;
import com.norteksoft.mms.form.enumeration.StartQuery;
import com.norteksoft.mms.form.service.FormHtmlParser;
import com.norteksoft.mms.form.service.ListColumnManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;

@Service
@Transactional
public class SearchData {
	private static final String BEANNAME="beanname";
	private static final String ENUMNAME="enumname";
	
	@SuppressWarnings("unused")
	private Integer fixedSearchSign;//如果有固定查询先显示固定查询的标记
	private ListColumnManager listColumnManager;
	private ListViewManager listViewManager;
	private FormHtmlParser formHtmlParser;
	
	@Transactional(readOnly=true)
	public String getContent(String listTableCode,String submitForm) throws Exception{
		Map<String, Object> root=new HashMap<String, Object>();
		List<ObjectField> fieldList = new ArrayList<ObjectField>();
		listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
		ListView listView = listViewManager.getListViewByCode(listTableCode);
		fieldList = getFieldListByCode(listView);
		//判断是否高级查询从mms中取值
		String advancedSearch = getSearchPropertyByCode(listView,"isAdvancedQuery");
		//判断是否弹框从mms中取值
		String containerId = getSearchPropertyByCode(listView,"isContainerIdQuery");
//		dealWithFieldString(fieldString, fieldList);
		List<ObjectField> fixedFields = new ArrayList<ObjectField>();
		String jsonStr = JsonParser.object2Json(fieldList);
		root.put("fixedField", getFixedFields(fixedFields,fieldList));
		root.put("fixedSearchSign", fixedFields.size());
		root.put("submitForm", submitForm==null?"":submitForm);
		root.put("containerId", containerId);
		root.put("advancedSearch", advancedSearch);
		root.put("fieldList", jsonStr);
		root.put("customField", StartQuery.CUSTOM_QUERY.equals(listView.getStartQuery())?getCustomSearchFields(listView):"\'\'");
		String result = TagUtil.getContent(root, "search/search.ftl");
		return result;
	}
	public String getFixedFields(List<ObjectField> fixedFields,List<ObjectField> fieldList){
		StringBuilder col=new StringBuilder();
		for (ObjectField field : fieldList) {
			if(field.getFixedField()){
				fixedFields.add(field);
				if(StringUtils.isNotEmpty(col.toString()))col.append(",");
				col.append("{");
				col.append("enName:'").append(StringUtils.isNotEmpty(field.getEnName())?field.getEnName():"").append("',");
				col.append("chName:'").append(StringUtils.isNotEmpty(field.getChName())?field.getChName():"").append("',");
				col.append("propertyType:'").append(field.getPropertyType()!=null?field.getPropertyType():"").append("',");
				col.append("fixedField:").append(field.getFixedField()?"true":"false").append(",");
				col.append("optionsCode:'").append(StringUtils.isNotEmpty(field.getOptionsCode())?field.getOptionsCode():"").append("',");
				col.append("enumName:'").append(StringUtils.isNotEmpty(field.getEnumName())?field.getEnumName():"").append("',");
				String defaultValues=getDefaultValues(field.getDefaultValues());
				col.append("defaultValues:").append(StringUtils.isNotEmpty(defaultValues)?defaultValues:"''").append(",");
				col.append("keyValue:'").append(StringUtils.isNotEmpty(field.getKeyValue())?field.getKeyValue().replace("'", ""):"").append("',");
				col.append("beanName:'").append(StringUtils.isNotEmpty(field.getBeanName())?field.getBeanName():"").append("',");
				col.append("optionGroup:'").append(StringUtils.isNotEmpty(field.getOptionGroup())?field.getOptionGroup():"").append("',");
				col.append("eventType:'").append(StringUtils.isNotEmpty(field.getEventType())?field.getEventType():"").append("',");
				col.append("dbName:'").append(StringUtils.isNotEmpty(field.getDbName())?field.getDbName():"").append("'");
				col.append("}");
			}
		}
		return "["+col.toString()+"]";
	}
	private String getDefaultValues(List<Option> defaultValues) {
		if(defaultValues==null)return "";
		StringBuilder col=new StringBuilder();
		for (Option option : defaultValues) {
			if(StringUtils.isNotEmpty(col.toString()))col.append(",");
			col.append("{");
			col.append("value:'").append(StringUtils.isNotEmpty(option.getValue())?option.getValue():"").append("',");
			col.append("name:'").append(StringUtils.isNotEmpty(option.getName())?option.getName():"").append("'");
			col.append("}");
		}
		return "["+col.toString()+"]";
	}

	private final transient TextProvider textProvider = 
		new TextProviderFactory().createInstance(getClass(), new LocaleProvider(){
			public Locale getLocale() {
		        ActionContext ctx = ActionContext.getContext();
		        if (ctx != null) {
		            return ctx.getLocale();
		        } else {
		            return null;
		        }
		    }});
	
	//通过列表编号获得想要查询的字段信息
	public List<ObjectField> getFieldListByCode(ListView listView){
		List<ObjectField> objFields = new ArrayList<ObjectField>();
		
		listColumnManager = (ListColumnManager)ContextUtils.getBean("listColumnManager");
		List<ListColumn> ListColumns = listColumnManager.getQueryColumnsByCode(listView.getCode());
		formHtmlParser = (FormHtmlParser)ContextUtils.getBean("formHtmlParser");
		Boolean isStandard = listView.getStandard();
		for(ListColumn listColumn : ListColumns){
			if(listColumn.getTableColumn()!=null){
				//值设置
				String valueSet = listColumn.getValueSet();
			    String enName = listColumn.getTableColumn().getName();
			    String dbName = listColumn.getTableColumn().getDbColumnName();
			    if(StringUtils.isEmpty(dbName)){
			    	dbName=null;
			    }
				//自定义表单在字段名前面加dt_
				if(isStandard==false){
					if(!FormHtmlParser.isDefaultField(enName)){
						enName="dt_"+enName;
					}
				}
				String chName = formHtmlParser.getInternation(listColumn.getHeaderName());
				String propertyType = listColumn.getTableColumn().getDataType().toString();
				if("TEXT".equals(propertyType)){
					propertyType = "STRING";
				}else if("CLOB".equals(propertyType)||"BLOB".equals(propertyType)||"COLLECTION".equals(propertyType)){
					continue;
				}
				//控件类型为“自定义”时，查询字段的控件类型为普通的输入框
				if("CUSTOM".equals(listColumn.getControlValue())){
					valueSet=null;
					propertyType = "STRING";
				}
				if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("FIXED")){//普通查询
					String fixedField = "true";
					fillFieldString(objFields,propertyType,enName,chName,fixedField,valueSet,listColumn,dbName);
				}else if(StringUtils.isNotEmpty(listColumn.getQuerySettingValue())&&listColumn.getQuerySettingValue().contains("CUSTOM")){//高级查询
					String fixedField = "false";
					fillFieldString(objFields,propertyType,enName,chName,fixedField,valueSet,listColumn,dbName);
				}
			}
		}
		return objFields;
	}
	//通过列表编号判断属性
	public String getSearchPropertyByCode(ListView listView,String searchType){
		String sign = "";
		
		Boolean propertyBoolean = null;
		if(searchType=="isAdvancedQuery"){
			propertyBoolean = listView.getAdvancedQuery();
		}else if(searchType=="isContainerIdQuery"){
			if(listView.getPopUp()==null||listView.getPopUp()){//数据库中没值或者是弹出式查询时
				propertyBoolean = false;
			}else{
				propertyBoolean = true;
			}
		}
		if(propertyBoolean){
			sign = "true";
		}else{
			sign = "false";
		}
		return sign;
	}
	//生成option
	private List<Option> createOption(String[] property){
		List<Option> options = new ArrayList<Option>();
		Option option = null;
		for (String p : property) {
			if(StringUtils.isNotEmpty(p)){
				option = new Option();
				String[] part = p.split(":");
				options.add(option);
				if( part[0].contains("'")||part[0].contains("\"")){
					option.setValue(part[0].replace("'", "").replace("\"", ""));
				}else{
					option.setValue(part[0]);
				}
				if(part[1].contains("'")||part[1].contains("\"")){
					option.setName(part[1].replace("'", "").replace("\"", ""));
				}else{
					option.setName(part[1]);
				}
			}
		}
		return options;
	}
	private void fillFieldString(List<ObjectField> objFields,String propertyType,String enName,String chName,String fixedField,String valueSet,ListColumn listColumn,String dbName){
		if(dbName==null){
			dbName = "";
		}
		ObjectField field = null;
		if("BOOLEAN".equals(propertyType)){
		   field = getField(enName,chName,propertyType,fixedField,"exist",getEventType(listColumn),dbName,"","",valueSet!=null?valueSet.replace("'", "").replace("\"", ""):"","");
		   String keyValue = valueSet;
		   if(keyValue != null && !"".equals(keyValue.trim())){
			   String[] property =	keyValue.split(",");
			   field.setDefaultValues(createOption(property));
		   }
		   objFields.add(field);
		}else if("ENUM".equals(propertyType)){//枚举的值设置
			String enumname = listColumn.getTableColumn().getObjectPath();
			
			
			String beanname = null;
			if(StringUtils.isEmpty(enumname)){
				if(valueSet.contains("beanname")){//接口
					beanname = getBeanName(valueSet);
				}else if(valueSet.contains(ENUMNAME)){
					enumname = getEnumName(valueSet);
				}
			}
			field = getField(enName,chName,propertyType,fixedField,"",getEventType(listColumn),dbName,beanname,"","",enumname);
			List<Option> options = getValueSet(listColumn);
			field.setDefaultValues(options);
			objFields.add(field);
		}else if(("STRING".equals(propertyType)||"INTEGER".equals(propertyType)) && valueSet != null && !"".equals(valueSet.trim())){
			String beanname = null;
			String keyValue = null;
			String optionGroup = null;
			if(valueSet.contains("beanname")){//接口
				beanname = getBeanName(valueSet);
			}else if(valueSet.contains(":")){//键值对
				keyValue = valueSet.replace("'", "").replace("\"", "");
			}else{//选项组
				optionGroup = valueSet;
			}
			field = getField(enName,chName,propertyType,fixedField,"exist",getEventType(listColumn),dbName,beanname,optionGroup,keyValue,"");
			List<Option> options = getValueSet(listColumn);
			field.setDefaultValues(options);
			objFields.add(field);
		}else{
			objFields.add(getField(enName,chName,propertyType,fixedField,"",getEventType(listColumn),dbName,"","","",""));
		}
	}
	
	private ObjectField getField(String enName,String chName,String propertyType,String fixedField,String optionsCode,String eventType,String dbName,String beanName,String optionGroup,String keyValue,String enumname){
		ObjectField field = new ObjectField();
		field.setEnName(enName);
		field.setChName(chName);
		field.setPropertyType(PropertyType.valueOf(propertyType));
		field.setFixedField(Boolean.valueOf(fixedField));
		field.setOptionsCode(optionsCode);
		field.setEventType(eventType);
		field.setDbName(dbName);
		field.setBeanName(beanName);
		field.setOptionGroup(optionGroup);
		field.setKeyValue(keyValue);
		field.setEnumName(enumname);
		return field;
	}

	/**
	 * 获得查询的事件类型
	 */
	private String getEventType(ListColumn listColumn) {
		String querySetting="";
		String querySettingValue=listColumn.getQuerySettingValue();
		String controlValue=listColumn.getControlValue();
		if(StringUtils.isNotEmpty(querySettingValue)){
			String[] arr=querySettingValue.split(",");
			//查询时，当控件类型为“人员部门树”时，查询事件优先于控件类型；
			if(arr.length>1){
				querySetting=arr[1];
			}else if(StringUtils.isNotEmpty(controlValue)&&controlValue.contains("SELECT_TREE")){//控件类型是人员部门树
				String[] val=controlValue.split(",");
				querySetting=val[2]+"/"+val[3];
			}
		}
		return querySetting;
	}
	public void setFixedSearchSign(Integer fixedSearchSign) {
		this.fixedSearchSign = fixedSearchSign;
	}
	private String getCustomSearchFields(ListView listView){
		Assert.notNull(listView,"listView不能为空");
		List<ListColumn> columns=listView.getColumns();
		StringBuilder temp=new StringBuilder();
		for(ListColumn column:columns){
			if(StringUtils.isNotEmpty(column.getQuerySettingValue()) && !"NONE".equals(column.getQuerySettingValue())&& column.getTableColumn()!=null){
				if(StringUtils.isNotEmpty(temp.toString())){
					temp.append(",");
				}
				temp.append("{");
				temp.append("\"enName\":");
				temp.append("\""+column.getTableColumn().getName()+"\",");
				temp.append("\"keyValue\":");
				if(StringUtils.isNotEmpty(column.getValueSet())){
					temp.append("\""+column.getOptionSet()+"\",");
				}else{
					temp.append("\"\",");
				}
				temp.append("\"propertyType\":");
				temp.append("\""+column.getTableColumn().getDataType()+"\",");
				temp.append("\"enumName\":");
				if(column.getTableColumn().getDataType().equals(DataType.ENUM)){
					temp.append("\""+column.getValueSet().replaceFirst("enumname:", "")+"\"");
				}else{
					temp.append("\"\"");
				}
				temp.append("}");
			}
		}
		return "["+temp.toString()+"]";
	}
	
	/**
	  * 获得值设置
	  * @param col
	  * @return
	  */
	 public List<Option> getValueSet(ListColumn col){
		 String valueSet=col.getValueSet();
		 TableColumn column = col.getTableColumn();
		 if(StringUtils.isNotEmpty(valueSet)){
			   if(valueSet.contains(ENUMNAME)){
				   String[] vals=valueSet.split(",");
				   for(String val:vals){
					 if(val.contains(ENUMNAME)){
						 String enumname=val.split(":")[1];
						return getOptionsByEnum(enumname);
					 }
				 }
			   }else{
		 		if(valueSet.contains(BEANNAME)){
		 			return getOptionsByBeanName(valueSet,column.getName());
		 		}else{
		 			if(valueSet.contains(":")){
		 				List<Option> options = new ArrayList<Option>();
		 				Option option = null;
		 				
		 				String[] vals=valueSet.split(",");
		 				int i=0;
		 				for(String val:vals){
		 					i++;
		 					String[] strArr=val.split(":");
		 					if(strArr.length>1){
		 						option = new Option();
								options.add(option);
								
		 						if( strArr[0].contains("'")||strArr[0].contains("\"")){
		 							option.setValue(strArr[0].replace("'", "").replace("\"", ""));
		 						}else{
		 							option.setValue(strArr[0]);
		 						}
		 						if(strArr[1].contains("'")||strArr[1].contains("\"")){
		 							option.setName(strArr[1].replace("'", "").replace("\"", ""));
		 						}else{
		 							option.setName(strArr[1]);
		 						}
		 					}
		 				}
		 				return options;
		 			}else{
		 				com.norteksoft.product.api.entity.OptionGroup group=ApiFactory.getSettingService().getOptionGroupByCode(valueSet);
		 				if(group!=null){
		 					List<com.norteksoft.product.api.entity.Option> ops=ApiFactory.getSettingService().getOptionsByGroup(group.getId());
		 					List<Option> options = new ArrayList<Option>();
			 				Option option = null;
		 					for(com.norteksoft.product.api.entity.Option op:ops){
			 						option = new Option();
									options.add(option);
		 							option.setValue(op.getValue());
		 							option.setName(op.getName());
		 					}
		 					return options;
		 				}
		 			}
		 		}
			 }
		 }
		 if(column!=null&&column.getDataType()==DataType.ENUM){//枚举类型时，获得枚举值选项
			   if(StringUtils.isNotEmpty(column.getObjectPath())){
				   return getOptionsByEnum(column.getObjectPath());
			   }
		   }
		 return null;
	 }
	 
	 private List<Option> getOptionsByEnum(String enumname){
		 List<Option> options = new ArrayList<Option>();
		 try {
				Object[] objs = Class.forName(enumname).getEnumConstants();
				Option option = null;
				for(Object obj : objs){
					option = new Option();
					options.add(option);
					option.setName(textProvider.getText(BeanUtils.getProperty(obj, "code")));
					option.setValue(obj.toString());
				}
				return options;
			} catch (Exception e) {
			}
			return options;
	 }
	 
	 public List<Option> getOptionsByBeanName(String valueSet,String colName){
		 List<Option> options = new ArrayList<Option>();
		 if(valueSet.contains(BEANNAME)){
			 String[] vals=valueSet.split(",");
			 for(String val:vals){
				 if(val.contains(BEANNAME)){
					String beanname= val.split(":")[1];
					Map<String,String> map=null;
					ComboxValues bean=(ComboxValues)ContextUtils.getBean(beanname);
					map=bean.getValues(null);
					String value=map.get(colName);
					if(StringUtils.isNotEmpty(value)){
						value = "{"+value+"}";
						try {
							JSONObject jsonObject = new JSONObject(value);
							Iterator<String> keys = jsonObject.keys();
							Option option = null;
							while(keys.hasNext()){
								String key = keys.next();
								option = new Option();
								options.add(option);
								option.setName(jsonObject.getString(key));
								option.setValue(key);
							}
							return options;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				 }
			 }
			}
		return options;
	 }
	private String getBeanName(String valueSet){
		 if(valueSet.contains(BEANNAME)){
			 String[] vals=valueSet.split(",");
			 for(String val:vals){
				 if(val.contains(BEANNAME)){
					String beanname= val.split(":")[1];
					return beanname;
				 }
			 }
		 }
		 return null;
	 }
	 
	 private String getEnumName(String valueSet){
		 if(valueSet.contains(ENUMNAME)){
			   String[] vals=valueSet.split(",");
			   for(String val:vals){
				 if(val.contains(ENUMNAME)){
					 String enumname=val.split(":")[1];
					 return enumname;
				 }
			   }
		 }
		 return null;
	 }
}
