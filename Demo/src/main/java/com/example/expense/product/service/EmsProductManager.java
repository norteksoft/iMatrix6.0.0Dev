package com.example.expense.product.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expense.entity.EmsProduct;
import com.example.expense.product.dao.EmsProductDao;
import com.norteksoft.product.orm.Page;

@Service
@Transactional
public class EmsProductManager {
	@Autowired
	private EmsProductDao emsProductDao;
	
	public EmsProduct getProduct(Long id){
		return emsProductDao.get(id);
	}
	
	public void saveProduct(EmsProduct product){
		emsProductDao.save(product);
		//设置 创建人id和部门id，数据分类api会用到
		product.setUserId(product.getCreatorId());
		product.setDeptId(product.getDepartmentId());
		emsProductDao.save(product);
	}
	
	public void deleteProduct(Long id){
		emsProductDao.decreaseIndex(emsProductDao.get(id).getDisplayIndex());//删除一条数据前比displayIndex大的要减1
		emsProductDao.delete(id);
	}
	
	public void deleteProduct(EmsProduct product){
		emsProductDao.delete(product);
	}
	
	public Page<EmsProduct> list(Page<EmsProduct>page){
		return emsProductDao.list(page);
	}
	
	public List<EmsProduct> listAll(){
		return emsProductDao.getAllProduct();
	}

	public Page<EmsProduct> search(Page<EmsProduct> page) {
		return emsProductDao.search(page);
	}

	public void saveEmsProduct(Integer originalIndex, Integer newIndex) {
		emsProductDao.updateIndex(originalIndex, Integer.MAX_VALUE);
        if (originalIndex < newIndex) {// 从上往下移动 两者之间的displayIndex要自减
        	emsProductDao.decreaseIndex(originalIndex,newIndex);
        } else {// 从下往上移动 两者之间的displayIndex要自增
        	emsProductDao.increaseIndex(newIndex,originalIndex);
        }
        emsProductDao.updateIndex(Integer.MAX_VALUE, newIndex);
	}

	public Map<String,Object>  getAmountTotal(List<String> names) {
		return emsProductDao.getAmountTotal(names);
	}

	public Integer getMaxIndex() {
		return emsProductDao.getMaxIndex();
	}

	public Page<EmsProduct> searchDataRuleList(Page<EmsProduct> page) {
		return emsProductDao.searchDataRuleList(page);
	}
}
