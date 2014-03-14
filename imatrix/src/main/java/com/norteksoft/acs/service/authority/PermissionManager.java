package com.norteksoft.acs.service.authority;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.enumeration.DataRange;
import com.norteksoft.acs.base.enumeration.ItemType;
import com.norteksoft.acs.base.enumeration.LogicOperator;
import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.acs.base.enumeration.UserOperator;
import com.norteksoft.acs.base.utils.PermissionItemTreeUtil;
import com.norteksoft.acs.dao.authority.PermissionDao;
import com.norteksoft.acs.dao.authority.PermissionItemDao;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.acs.entity.authority.PermissionUser;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.TreeUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.tags.tree.DepartmentDisplayType;

@Service
@Transactional
public class PermissionManager {
	@Autowired
	private PermissionDao permissionDao;
	@Autowired
	private PermissionItemDao permissionItemDao;
	@Autowired
	private PermissionItemConditionManager permissionItemConditionManager;
	@Autowired
	private DataRuleManager dataRuleManager;
	@Autowired
	private MenuManager menuManager;
	
	public void getPermissions(Page<Permission> page,Long dataRuleId){
		permissionDao.getPermissions(page, dataRuleId);
	}
	public void getPermissionsByMenuId(Page<Permission> page,Long menuId,Boolean fast){
		permissionDao.getPermissionsByMenuId(page, menuId,fast);
	}
	public Permission getPermission(Long id){
		return permissionDao.get(id);
	}
	
	/**
	 * 保存数据授权
	 * @param permission
	 * @param auths
	 */
	public void savePermission(Permission permission,List<PermissionAuthorize> auths,Boolean allUser){
		//获得操作权限
		Integer permAuth=0;
		for(PermissionAuthorize auth:auths){
			permAuth=permAuth+auth.getCode();
		}
		permission.setAuthority(permAuth);
		permissionDao.save(permission);
		//删除人员对应的条件列表
		permissionItemConditionManager.deleteAllPermissionItemConditions(permission.getId());
		if(allUser){
			//删除所有用户对应得数据授权项
			permissionItemDao.deleteItemTypeNotAllUserPermissionItems(permission.getId());
			PermissionItem item= new PermissionItem();
			item.setItemType(ItemType.ALL_USER);
			item.setPermission(permission);
			permissionItemDao.save(item);
		}else{
			//删除所有用户对应得数据授权项
			permissionItemDao.deleteItemTypeAllUserPermissionItems(permission.getId());
			//删除所有用户对应得数据授权项
			List<Object> list=JsonParser.getFormTableDatas(PermissionItem.class);
			List<PermissionItem> result=new ArrayList<PermissionItem>();
			int index=1;
			for(Object obj:list){
				PermissionItem inter=(PermissionItem)obj;
				inter.setDisplayOrder(index);
				inter.setPermission(permission);
				permissionItemDao.save(inter);
				//保存人员条件值
				saveConditionValue(inter.getConditionName(),inter.getConditionValue(),inter.getId());
				
				result.add(inter);
				index++;
			}
			permission.setItems(result);
		}
	}
	/**
	 * 数据授权的人员的保存操作
	 * @param conditionName:以逗号隔开的字符串
	 * @param conditionValue:以逗号隔开的字符串
	 */
	public void saveConditionValue(String conditionName,String conditionValue,Long dataId){
		String[] values = conditionValue.split(",");
		String[] names = conditionName.split(",");
		for(int i=0;i<values.length;i++){
			String val = StringUtils.trim(values[i]);
			if(StringUtils.isNotEmpty(val)){
				PermissionItemCondition itemCon = new PermissionItemCondition();
				itemCon.setConditionName(StringUtils.trim(names[i]));
				itemCon.setConditionValue(val);
				itemCon.setDataId(dataId);
				itemCon.setValueType(ConditionValueType.PERMISSION);
				permissionItemConditionManager.save(itemCon);
			}
		}
	}
	/**
	 * 保存数据授权
	 * @param permission
	 * @param auths
	 */
	public void permissionSave(Permission permission,List<PermissionAuthorize> auths){
		//获得操作权限
		Integer permAuth=0;
		for(PermissionAuthorize auth:auths){
			permAuth=permAuth+auth.getCode();
		}
		permission.setAuthority(permAuth);
		permissionDao.save(permission);
	}
	
	/**
	 * 删除数据授权
	 * @param ids
	 */
	public void deletePermissions(String ids){
		String[] idList=ids.split(",");
		for(String id:idList){
			if(StringUtils.isNotEmpty(id)){
				//删除人员对应的条件列表
				permissionItemConditionManager.deleteAllPermissionItemConditions(Long.parseLong(id));
				permissionDao.delete(Long.parseLong(id));
			}
		}
	}
	/**
	 * 删除数据授权
	 * @param ids
	 */
	public void deleteFastPermissions(String ids){
		String[] idList=ids.split(",");
		for(String id:idList){
			if(StringUtils.isNotEmpty(id)){
				//查询该快速授权对应的数据分类
				List<DataRule> dataRules = permissionDao.getDataRuleByPermission(Long.parseLong(id));
				//删除人员对应的条件列表
				permissionItemConditionManager.deleteAllPermissionItemConditions(Long.parseLong(id));
				//删除该快速授权对应的人员列表
				permissionItemDao.deleteAllPermissionItems(Long.parseLong(id));
				//删除快速授权
				permissionDao.delete(Long.parseLong(id));
				//删除该快速授权对应的数据分类
				for(DataRule rule:dataRules){
					dataRuleManager.deleteRule(rule);
				}
			}
		}
	}
	/**
	 * 根据数据规则获得数据授权列表
	 * @param dataRuleId
	 * @return
	 */
	public List<Permission> getPermissionsByDataRule(Long dataRuleId){
		return permissionDao.getPermissionsByDataRule(dataRuleId);
	}
	
	/**
	 * 验证该授权的优先级及操作权限
	 * @param auths
	 * @param dataRuleId
	 * @return 没有相同优先级和操作权限的返回true，否则返回false。返回true则可以保存
	 */
	public String validatePermission(String validateAuthCodes ,Long dataRuleId,Long permissionId,Integer priority ){
		String validateResult="";
		DataRule dataRule=dataRuleManager.getDataRule(dataRuleId);
		if(dataRule==null)return "true-保存";
		List<DataRule> result=dataRuleManager.getDataRuleByDataTable(dataRule.getDataTableId());
		String[] authCodes=validateAuthCodes.split(",");
		for(DataRule rule:result){
			List<Permission> permissions=getPermissionsByDataRule(rule.getId());
			for(Permission perm:permissions){
				if(!perm.getId().equals(permissionId)&&perm.getPriority().equals(priority)){//不是当前编辑的授权且优先级相等则做权限判断
					for(String authCode:authCodes){
						if(StringUtils.isNotEmpty(authCode)){
							PermissionAuthorize auth=getAuthByCode(Integer.parseInt(authCode));
							if((perm.getAuthority() & auth.getCode()) != 0){//有该权限
								validateResult="false-"+Struts2Utils.getText(auth.getI18nKey());
								return validateResult;
							}
						}
					}
				}
			}
		}
		validateResult="true-保存";
		return validateResult;
	}
	
	/**
	 * 根据权限编码获得操作权限
	 * @param code
	 * @return
	 */
	private PermissionAuthorize getAuthByCode(Integer code){
		for(PermissionAuthorize auth : PermissionAuthorize.values()){
			if(code.equals(auth.getCode())){
				return auth;
			}
		}
		return null;
	}
	
	/**
	 * 获取itemType树
	 * @param currentTreeId 
	 */
	public String getPermissionItemTree(ItemType itemType, String currentTreeId) {
		if(itemType==ItemType.USER ){
			return TreeUtils.getCreateManDepartmentTree(ContextUtils.getCompanyId(), currentTreeId, false, DepartmentDisplayType.NAME, true, "");//获取用户和表单的树
		}else if(itemType==ItemType.ROLE ){
			return PermissionItemTreeUtil.getSystemRoleTree();//获取角色和表单的树
		}else if(itemType==ItemType.WORKGROUP){
			return TreeUtils.getCreateGroupTree(ContextUtils.getCompanyId(), currentTreeId,  "");
		}else if(itemType==ItemType.DEPARTMENT){
			return TreeUtils.getCreateDepartmentTree(ContextUtils.getCompanyId(), currentTreeId, DepartmentDisplayType.NAME, "");
		}
		return null;
	}
	
	public void saveFastPermission(Permission permission,List<PermissionAuthorize> docAuthes,Long dataTableId,String dataTableName,DataRange dataRange,Boolean deparmentInheritable,String permissionUsers){
		//保存快速授权对应的数据分类
		DataRule permissionDataRule = new DataRule();
		//设置系统id
		Menu menu = menuManager.getMenu(permission.getMenuId());
		permissionDataRule.setSystemId(menu.getSystemId());
		
		permissionDataRule.setDataTableId(dataTableId);
		permissionDataRule.setDataTableName(dataTableName);
		permissionDataRule.setFastable(true);
		
		permissionDataRule.setDataRange(dataRange);
		permissionDataRule.setDeparmentInheritable(deparmentInheritable);
		dataRuleManager.saveRule(permissionDataRule);
		permission.setDataRule(permissionDataRule);
		permission.setFastable(true);
		//保存快速授权
		permissionSave(permission,docAuthes);
		//删除人员对应的条件列表
		permissionItemConditionManager.deleteAllPermissionItemConditions(permission.getId());
		//删除该快速授权对应的人员列表
		permissionItemDao.deleteAllPermissionItems(permission.getId());
		//保存数据授权对应的人员
		//[{"type":"USER","permissionValues":[{"con1":"bb","con2":"cc"},{"con1":bbb,"con2":"ccc"}]},{"type":"DEPARTMENT","permissionValues":[{"con1":"bb1","con2":"cc1"}]}]
		List<PermissionUser> permissionUserObjs = JsonParser.json2List(PermissionUser.class, permissionUsers);
		for(PermissionUser perUser:permissionUserObjs){
			String itemType = perUser.getItemType();
			if(ItemType.ALL_USER.toString().equals(itemType)){
				PermissionItem perItem = new PermissionItem();
				perItem.setPermission(permission);
				perItem.setItemType(getItemType(itemType));
				permissionItemDao.save(perItem);
			}else{
				List<PermissionUser> perValues = perUser.getPermissionValues();
				if(perValues!=null){
					PermissionItem perItem = new PermissionItem();
					perItem.setPermission(permission);
					perItem.setItemType(getItemType(itemType));
					perItem.setJoinType(LogicOperator.OR);
					perItem.setOperator(UserOperator.ET);
					permissionItemDao.save(perItem);
					for(PermissionUser perValue:perValues){
						PermissionItemCondition permissionItemCondition = new PermissionItemCondition();
						permissionItemCondition.setConditionValue(perValue.getConditionValue());
						permissionItemCondition.setConditionName(perValue.getConditionName());
						permissionItemCondition.setDataId(perItem.getId());
						permissionItemCondition.setValueType(ConditionValueType.PERMISSION);
						permissionItemConditionManager.save(permissionItemCondition);
					}
				}
			}
		}
		
	}
	
	private ItemType getItemType(String value){
		for(ItemType type : ItemType.values()){
			if(type.toString().equals(value))
				return type;
		}
		return null;
	}
	
	public List<Permission> getDefaultCodePermissions(){
		return permissionDao.getDefaultCodePermissions();
	}
	
	 /**
	  * 验证数据授权编码是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isPermissionCodeExist(String code,Long permissionId){
		 Permission permission=permissionDao.getPermissionsByCode(code);
		 if(permission==null){
			 return false;
		 }else{
			 if(permissionId==null)return true;
			 if(permission.getId().equals(permissionId)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	 }
	 
		public List<PermissionUser> getPermissionUsers(List<PermissionItem> permissionItems,ItemType itemType){
			List<PermissionUser> perUsers = new ArrayList<PermissionUser>();
			for(PermissionItem item:permissionItems){
				List<PermissionItemCondition> pics = permissionItemConditionManager.getPermissionItemConditions(item.getId());
				for(PermissionItemCondition pic:pics){
					PermissionUser perUser = new PermissionUser();
					perUser.setItemType(itemType.toString());
					perUser.setConditionName(pic.getConditionName());
					perUser.setConditionValue(pic.getConditionValue());
					perUsers.add(perUser);
				}
			}
			return perUsers;
		}
		
		public void getPermissionPageByDataRule(Page<Permission> page,Long dataRuleId){
			permissionDao.getPermissionPageByDataRule(page, dataRuleId);
		}

	
}
