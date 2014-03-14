package com.norteksoft.acs.web.sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.entity.organization.MailDeploy;
import com.norteksoft.acs.entity.sale.PricePolicy;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.acs.entity.sale.SubscriberItem;
import com.norteksoft.acs.entity.sale.Tenant;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.acs.service.sale.PricePolicyManager;
import com.norteksoft.acs.service.sale.TenantManager;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.product.orm.Page;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/tenant.action", type="redirect") })
public class TenantAction extends CRUDActionSupport<Tenant> {

	private static final long serialVersionUID = 457701883206968560L;

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private TenantManager tenantManager;
	private Page<Tenant> page = new Page<Tenant>(10, true);
	private Tenant entity;
	private Subsciber subsciber;
	private List<Long> priceIds;
	private PricePolicy pricePolicy;
	private Long id;
	private Map<Product, List<PricePolicy>> products;
	private PricePolicyManager pricePolicyManager;
	private int productCount = 0;
	private CompanyManager companyManager;
	private MailDeploy mailDeploy;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	
	@Override
	public String delete() throws Exception {
		tenantManager.deleteTenant(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		page = tenantManager.getAllTenants(page);
		return SUCCESS; 
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = tenantManager.getTenant(id);
			entity.getCompany();
			mailDeploy=tenantManager.getMailDeployByCompanyId(entity.getCompany().getId());
		}else{
			entity = new Tenant();
			Company company = new Company();
			entity.setCompany(company);
		}
	}

	@Override
	public String input() throws Exception {
		products = pricePolicyManager.getAllPricePolicy();
		productCount = products.size();
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		if(id != null){
			tenantManager.saveTenant(entity);
		}else{
			List<SubscriberItem> items = new ArrayList<SubscriberItem>();
			SubscriberItem item = null;
			Product product = null;
			for(int i=0;i<productCount;i++){
				String productId = Struts2Utils.getParameter("priceIds_"+i);
				if(StringUtils.isNotBlank(productId)){
					product = new Product();product.setId(Long.valueOf(productId));
					item = new SubscriberItem();
					item.setProduct(product);
					item.setEffectDate(FORMAT.parse(Struts2Utils.getParameter("effectDate_"+i)));
					item.setInvalidDate(FORMAT.parse(Struts2Utils.getParameter("invalidDate_"+i)));
					item.setConcurrency(Integer.valueOf(Struts2Utils.getParameter("concurrency_"+i)));
					item.setAmount(Double.valueOf(Struts2Utils.getParameter("amount_"+i)));
					items.add(item);
				}
			}
			tenantManager.saveSubsciberItem(entity, subsciber, items);
		}
		if(mailDeploy != null){
			mailDeploy.setCompanyId(entity.getId());
			tenantManager.saveMailDeploy(mailDeploy);
		}
		return RELOAD;
	}
	//给所有用户密码MD5加密
	public String encryotionByMD5() throws Exception{
		List<Company> companys = companyManager.getCompanys();
		for(Company company:companys){
		   tenantManager.encryotionByMD5(company.getId());
		}
		this.addActionSuccessMessage("加密成功!");
		return list();
	}
	
	
	
//	@Override
//	public String save() throws Exception {
//		if(id != null){
//			tenantManager.saveTenant(entity);
//		}else{
//			if(priceIds.size() <= 0) return INPUT;
//			tenantManager.saveSubsciber(entity, subsciber, priceIds);
//		}
//		return RELOAD;
//	}
	
	public Tenant getModel() {
		return entity;
	}

	public Page<Tenant> getPage() {
		return page;
	}

	public void setPage(Page<Tenant> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	public Subsciber getSubsciber() {
		return subsciber;
	}

	public void setSubsciber(Subsciber subsciber) {
		this.subsciber = subsciber;
	}

	public PricePolicy getPricePolicy() {
		return pricePolicy;
	}

	public void setPricePolicy(PricePolicy pricePolicy) {
		this.pricePolicy = pricePolicy;
	}
	
	public List<Long> getPriceIds() {
		return priceIds;
	}

	public void setPriceIds(List<Long> priceIds) {
		this.priceIds = priceIds;
	}

	@Required
	public void setPricePolicyManager(PricePolicyManager pricePolicyManager) {
		this.pricePolicyManager = pricePolicyManager;
	}

	@Required
	public void setCompanyManager(CompanyManager companyManager) {
		this.companyManager = companyManager;
	}

	public Map<Product, List<PricePolicy>> getProducts() {
		return products;
	}

	public void setProducts(Map<Product, List<PricePolicy>> products) {
		this.products = products;
	}
	private void addActionSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public MailDeploy getMailDeploy() {
		return mailDeploy;
	}

	public void setMailDeploy(MailDeploy mailDeploy) {
		this.mailDeploy = mailDeploy;
	}

}
