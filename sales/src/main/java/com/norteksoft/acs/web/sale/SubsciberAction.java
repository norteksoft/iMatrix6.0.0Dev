package com.norteksoft.acs.web.sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.organization.MailDeploy;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.entity.sale.Subsciber;
import com.norteksoft.acs.entity.sale.SubscriberItem;
import com.norteksoft.acs.entity.sale.Tenant;
import com.norteksoft.acs.service.sale.ProductManager;
import com.norteksoft.acs.service.sale.SubsciberManager;
import com.norteksoft.acs.service.sale.SubscriberItemManager;
import com.norteksoft.acs.service.sale.TenantManager;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.product.orm.Page;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "/sale/subsciber.action?tenantId=${tenantId}", type="redirect") })
public class SubsciberAction extends CRUDActionSupport<Subsciber>{
	private static final long serialVersionUID = -1121578387023865254L;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private SubsciberManager manager;
	private Tenant tenant;
	private TenantManager tenantManager;
	private MailDeploy mailDeploy;
	private Page<Subsciber> page = new Page<Subsciber>(20, true);
	private Subsciber entity;
	private Long tenantId;
	private Long id;
	@Autowired
	private ProductManager productManager;
	private List<Product> products;
	private List<Long> priceIds;
	private List<Long> checkedPriceIds;
	private int productCount = 0;
	private List<SubscriberItem> items;
	@Autowired
	private SubscriberItemManager subscriberItemManager;
	@Autowired
	private SubsciberManager subsciberManager;
	
	@Override
	public String delete() throws Exception {
		manager.deleteSubsciber(id);
		return RELOAD;
	}

	@Override
	public String list() throws Exception {
		if(tenantId != null){
			page = manager.getSubsciberByTenant(page, tenantId);
		}else{
			//page = manager.getAllSubsciber(page);
		}
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id != null){
			entity = manager.getSubsciber(id);
		}else{
			entity = new Subsciber();
		}
	}

	@Override
	public String input() throws Exception {
		products = productManager.getProducts();
		productCount = products.size();
		if(id != null){
			//checkedPriceIds = pricePolicyManager.getPricePolicysBySubsciber(id);
			items = subscriberItemManager.queryItems(id);
		}
		if(tenantId != null){
			tenant = tenantManager.getTenant(tenantId);
			mailDeploy=tenantManager.getMailDeployByCompanyId(tenant.getCompany().getId());
		}
		return INPUT;
	}

	@Override
	public String save() throws Exception {
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
		
	
		if(entity!=null){
			subsciberManager.saveSubsciber(entity, items);
		}
	
		return RELOAD;
	}

	public Subsciber getModel() {
		return entity;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setSubsciberManager(SubsciberManager subsciberManager){
		this.manager = subsciberManager;
	}
	
	@Required
	public void setTenantManager(TenantManager tenantManager){
		this.tenantManager = tenantManager;
	}

	public Page<Subsciber> getPage() {
		return page;
	}

	public void setPage(Page<Subsciber> page) {
		this.page = page;
	}

	public List<Long> getPriceIds() {
		return priceIds;
	}

	public void setPriceIds(List<Long> priceIds) {
		this.priceIds = priceIds;
	}

	public List<Long> getCheckedPriceIds() {
		return checkedPriceIds;
	}

	public void setCheckedPriceIds(List<Long> checkedPriceIds) {
		this.checkedPriceIds = checkedPriceIds;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public List<SubscriberItem> getItems() {
		return items;
	}

	public Subsciber getEntity() {
		return entity;
	}

	public void setEntity(Subsciber entity) {
		this.entity = entity;
	}

	public MailDeploy getMailDeploy() {
		return mailDeploy;
	}

	public void setMailDeploy(MailDeploy mailDeploy) {
		this.mailDeploy = mailDeploy;
	}
}
