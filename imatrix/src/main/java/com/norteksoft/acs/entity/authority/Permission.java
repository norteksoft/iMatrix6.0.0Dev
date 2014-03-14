package com.norteksoft.acs.entity.authority;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.norteksoft.acs.base.enumeration.PermissionAuthorize;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 授权
 * @author Administrator
 *
 */
@Entity
@Table(name="ACS_PERMISSION")
public class Permission extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer priority;
	private Integer authority;
	@ManyToOne
	@JoinColumn(name="FK_DATA_RULE_ID")
	private DataRule dataRule;
	@OneToMany(mappedBy="permission",cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderBy("displayOrder asc")
	private List<PermissionItem> items;
	private Long menuId;//与菜单关联
	private String code;//编号
	private String name;//名称
	private Boolean fastable=false;//是否是快速授权,默认不是快速授权
	
	@Transient
	private String authorityName;//操作权限名称
	private Long listViewId;//查看权限时对应的列表id,因为和查看权限关联，所以要放在授权中，不能放在分类中
	@Transient
	private String listViewName;
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Integer getAuthority() {
		return authority;
	}
	public void setAuthority(Integer authority) {
		this.authority = authority;
	}
	public DataRule getDataRule() {
		return dataRule;
	}
	public void setDataRule(DataRule dataRule) {
		this.dataRule = dataRule;
	}
	public List<PermissionItem> getItems() {
		return items;
	}
	public void setItems(List<PermissionItem> items) {
		this.items = items;
	}
	public String getAuthorityName() {
		authorityName="";
		for(PermissionAuthorize auth : PermissionAuthorize.values()){
			if((this.authority & auth.getCode()) != 0){//有该权限
				authorityName=authorityName+Struts2Utils.getText(auth.getI18nKey())+",";
			}
		}
		if(authorityName.contains(",")){
			authorityName=authorityName.substring(0,authorityName.lastIndexOf(","));
		}
		return authorityName;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getFastable() {
		return fastable;
	}
	public void setFastable(Boolean fastable) {
		this.fastable = fastable;
	}
	public Long getListViewId() {
		return listViewId;
	}
	public void setListViewId(Long listViewId) {
		this.listViewId = listViewId;
	}
	public String getListViewName() {
		if(listViewId==null)return null;
		ListViewManager listViewManager = (ListViewManager)ContextUtils.getBean("listViewManager");
		ListView listView = listViewManager.getView(listViewId);
		return listView.getName();
	}
  
}
