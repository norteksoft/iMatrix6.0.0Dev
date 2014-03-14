package com.norteksoft.acs.web.sale;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Required;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.acs.entity.sale.PricePolicy;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.service.sale.PricePolicyManager;
import com.norteksoft.acs.service.sale.ProductManager;
import com.norteksoft.product.orm.Page;

@ParentPackage("default")
@Results( { @Result(name = CRUDActionSupport.RELOAD, location = "price-policy?productId=${productId}", type = "redirectAction") })
public class PricePolicyAction  extends CRUDActionSupport<PricePolicy>{

	private static final long serialVersionUID = 1L;
	private PricePolicyManager pricePolicyManager;
	private Long productId;
	private Page<PricePolicy> page = new Page<PricePolicy>(Page.EACH_PAGE_TWENTY,true);
	private PricePolicy pricePolicy;
	private ProductManager productManager;
	private Long id;

	@Override
	public String list() throws Exception {
		pricePolicyManager.getPricePolicyBySystem(page, productId);
		return SUCCESS;
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id == null){
			pricePolicy = new PricePolicy();
			Product p = new Product();
			p.setId(productId);
			pricePolicy.setProduct(p);
		}else{
			pricePolicy = pricePolicyManager.getPricePolicy(id);
		}
	}

	@Override
	public String save() throws Exception {
		Product product = productManager.getProduct(productId);
		pricePolicy.setProduct(product);
		pricePolicyManager.savePricePolicy(pricePolicy);
		return RELOAD;
	}

	@Override
	public String delete() throws Exception {
		pricePolicyManager.deletePricePolicy(id);
		return RELOAD;
	}

	public PricePolicy getModel() {
		return pricePolicy;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Page<PricePolicy> getPage() {
		return page;
	}

	public void setPage(Page<PricePolicy> page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Required
	public void setPricePolicyManager(PricePolicyManager pricePolicyManager) {
		this.pricePolicyManager = pricePolicyManager;
	}

	@Required
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public PricePolicy getPricePolicy() {
		return pricePolicy;
	}

	public void setPricePolicy(PricePolicy pricePolicy) {
		this.pricePolicy = pricePolicy;
	}

}
