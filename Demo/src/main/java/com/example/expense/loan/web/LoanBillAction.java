package com.example.expense.loan.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.expense.entity.LoanBill;
import com.example.expense.loan.service.LoanBillManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.tree.TreeAttr;
import com.norteksoft.product.util.tree.TreeNode;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;


@Namespace("/loan-bill")
@ParentPackage("default")
public class LoanBillAction extends CrudActionSupport<LoanBill> {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Long parentFolderId;
	private String parentFolderName;
	private LoanBill loanBill;
	private String treeId;
	private boolean updateSign;
	private Page<LoanBill> page = new Page<LoanBill>(Page.EACH_PAGE_TEN, true);
	private String currentId;
	private String isZtree;
	
	@Autowired
	private LoanBillManager loanBillManager;
	
	
	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			loanBill=new LoanBill();
			loanBill.setCreateDate(new Date());
			loanBill.setCompanyId(ContextUtils.getCompanyId());
		}else {
			loanBill=loanBillManager.getLoanBill(id);
		}
	}
	
	@Action("loan-bill-input")
	@Override
	public String input() throws Exception {
		if(parentFolderId!=null){
			LoanBill 	parentBill=loanBillManager.getLoanBill(parentFolderId);
			parentFolderName = parentBill.getName();
		}
		return "input";
	}
	
	@Action("loan-bill-save")
	@Override
	public String save() throws Exception {
		loanBillManager.saveLoanBill(loanBill);
		renderText(loanBill.getId().toString());
		return null;
	}
	
	@Action("loan-bill-delete")
	@Override
	public String delete() throws Exception {
		loanBillManager.deleteLoanBill(id);
		addActionMessage("<font class=\"onSuccess\"><nobr>已成功删除!</nobr></font>");
		return null;
	}

	@Action("loan-bill-list")
	@Override
	public String list() throws Exception {
		if(id!=null){
			loanBill = loanBillManager.getLoanBill(id);
		}
		return "list";
	}
	
	@Action("load-tree")
	public String loadTree() throws Exception {
		List<TreeNode> folderNodes = new ArrayList<TreeNode>();
		if(parentFolderId==null)parentFolderId=-1l;
		if(parentFolderId < 0){
			 folderNodes = getFolderTree(0L);
		}else if(parentFolderId > 0){
			 folderNodes = getFolderTree(parentFolderId);
		}
		renderText(JsonParser.object2Json(folderNodes));
		return null;
	}
	
	@Action("loan-bill-static-tree")
	public String staticTree() throws Exception {
		return "static-tree";
	}
	/**
	 * 取文件价树
	 * @param parentId
	 * @return
	 */
	private List<TreeNode> getFolderTree (Long parentId){
		List<TreeNode> folderNodes = new ArrayList<TreeNode>();
		List<LoanBill> folders = null;
			folders = loanBillManager.getFolder(parentId);
		if(!folders.isEmpty()){
			for(LoanBill folder : folders){
				TreeNode folderNode = new TreeNode(
						new TreeAttr(Long.toString(folder.getId()),""), 
						"closed", 
						 folder.getName());
				folderNodes.add(folderNode);
			}
		}
		return folderNodes;
	}
	@Action("loan-bill-dynamic-ztree")
	public String dynamicZtree() throws Exception {
		return "dynamic-ztree";
	}
	@Action("load-ztree")
	public String loadZtree() throws Exception {
		List<ZTreeNode> folderNodes = new ArrayList<ZTreeNode>();
		String[] str = currentId.split("_");
		if(currentId.equals("0")){
			 folderNodes = getFolderZtree(0L);
		}else{
			 folderNodes = getFolderZtree(Long.parseLong(str[1]));
		}
		renderText(JsonParser.object2Json(folderNodes));
		return null;
	}
	
	private List<ZTreeNode> getFolderZtree (Long id){
		List<ZTreeNode> folderNodes = new ArrayList<ZTreeNode>();
		List<LoanBill> folders = null;
			folders = loanBillManager.getFolder(id);
		if(!folders.isEmpty()){
			for(LoanBill folder : folders){
				List<LoanBill> children = loanBillManager.getFolder(folder.getId());
				if(children.isEmpty()){
					ZTreeNode folderNode = new ZTreeNode("FOLDER_"+folder.getId(),"0",folder.getName(),"true","false","FOLDER","{\"name\" : \""+folder.getName()+"\" }");
					folderNodes.add(folderNode);
				}else{
					ZTreeNode folderNode = new ZTreeNode("FOLDER_"+folder.getId(),"0",folder.getName(),"true","true","FOLDER","{\"name\" : \""+folder.getName()+"\" }");
					folderNodes.add(folderNode);
				}
			}
		}
		return folderNodes;
	}
	@Action("loan-bill-static-ztree")
	public String staticZtree() throws Exception {
		return "static-ztree";
	}
	
	@Action("loan-bill-authority-tag")
	public String authorityTag() throws Exception {
		return "authority-tag";
	}
	
	@Action("loan-bill-have-authority")
	public String haveAuthority() throws Exception {
		return "have-authority";
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setPage(Page<LoanBill> page) {
		this.page = page;
	}
	
	public Page<LoanBill> getPage() {
		return page;
	}
	
	public LoanBill getModel() {
		return loanBill;
	}
	
	public Long getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(Long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String getParentFolderName() {
		return parentFolderName;
	}

	public void setParentFolderName(String parentFolderName) {
		this.parentFolderName = parentFolderName;
	}

	public LoanBill getLoanBill() {
		return loanBill;
	}

	public void setLoanBill(LoanBill loanBill) {
		this.loanBill = loanBill;
	}

	public boolean isUpdateSign() {
		return updateSign;
	}

	public void setUpdateSign(boolean updateSign) {
		this.updateSign = updateSign;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public String getIsZtree() {
		return isZtree;
	}

	public void setIsZtree(String isZtree) {
		this.isZtree = isZtree;
	}
}
