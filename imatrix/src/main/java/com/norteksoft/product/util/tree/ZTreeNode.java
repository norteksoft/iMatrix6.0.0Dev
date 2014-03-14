package com.norteksoft.product.util.tree;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("serial")
public class ZTreeNode implements Serializable{
	
	private String id;
	private String pId;
	private String name;
	private String open="true";
	private String isParent;
	private String path;
	private String code;
	private String type;
	private String drag="true";
	private String nocheck="false";
	private String dropPrev="true";
	private String dropInner="true";
	private String dropNext="true";
	private String data;
	private String iconSkin;
	private String chkDisabled;
	private List<ZTreeNode> children;
	
	
	public ZTreeNode() {
		super();
	}


	public ZTreeNode(String id, String pId, String name,String open,String isParent,String path,String code,String type,String iconSkin,String drag,String nocheck,String dropPrev,String dropInner,String dropNext) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.path = path;
		this.code = code;
		this.type = type;
		this.iconSkin=iconSkin;
		this.drag=drag;
		this.nocheck=nocheck;
		this.dropPrev=dropPrev;
		this.dropInner=dropInner;
		this.dropNext=dropNext;
	}


	public ZTreeNode(String id, String pId, String name,String data,String isParent,String iconSkin) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.data = data;
		this.isParent = isParent;
		this.iconSkin=iconSkin;
	}
	
	public ZTreeNode(String id, String pId, String name,String open,String isParent) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
	}
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String type,String data) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.type = type;
		this.data = data;
	}
	
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String type,String data,String iconSkin) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.type = type;
		this.data = data;
		this.iconSkin = iconSkin;
	}
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String type,String data,String iconSkin,String chkDisabled) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.type = type;
		this.data = data;
		this.iconSkin = iconSkin;
		this.chkDisabled = chkDisabled;
	}
	public ZTreeNode(String id, String pId, String name,String open,String isParent,String path,String code,String type,String iconSkin,String chkDisabled) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
		this.isParent = isParent;
		this.path = path;
		this.code = code;
		this.type = type;
		this.iconSkin=iconSkin;
		this.chkDisabled=chkDisabled;
	}
	
	public String getDropPrev() {
		return dropPrev;
	}


	public void setDropPrev(String dropPrev) {
		this.dropPrev = dropPrev;
	}


	public String getDropInner() {
		return dropInner;
	}


	public void setDropInner(String dropInner) {
		this.dropInner = dropInner;
	}


	public String getDropNext() {
		return dropNext;
	}


	public void setDropNext(String dropNext) {
		this.dropNext = dropNext;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDrag() {
		return drag;
	}


	public void setDrag(String drag) {
		this.drag = drag;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}


	public String getNocheck() {
		return nocheck;
	}


	public void setNocheck(String nocheck) {
		this.nocheck = nocheck;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getChkDisabled() {
		return chkDisabled;
	}


	public void setChkDisabled(String chkDisabled) {
		this.chkDisabled = chkDisabled;
	}




	public List<ZTreeNode> getChildren() {
		return children;
	}


	public void setChildren(List<ZTreeNode> children) {
		this.children = children;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ZTreeNode){
			return ((ZTreeNode) obj).getId().equals(this.getId());
		}
		return super.equals(obj);
	}
	
}
