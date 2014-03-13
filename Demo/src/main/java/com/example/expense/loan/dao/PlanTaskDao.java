package com.example.expense.loan.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;
import com.example.expense.entity.PlanTask;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class PlanTaskDao extends HibernateDao<PlanTask, Long> {
		
	public Page<PlanTask> list(Page<PlanTask> page){
		return searchPageByHql(page, "from PlanTask p where p.companyId=? and p.document=?", ContextUtils.getCompanyId(),"C_1A");
	}
	
	public Page<PlanTask> listPortal(Page<PlanTask> page){
		return searchPageByHql(page, "from PlanTask p where p.creator=?",ContextUtils.getLoginName());
	}

	public void batchInsertData() {
		for (int i = 149762; i < 5000000; i++) {
			PlanTask task = new PlanTask("任务名称_"+i, new Date(), new Date(), 5, "task_"+i, "开发部", "test1", 0.1f, "进行中", 5000d, new Date(), 3, new Date(), "test2", "字符串一", "字符串二","字符串三", "字符串四", "字符串五",
					"字符串六", "字符串七","字符串八", "字符串九", "字符串十","字符串十一", "字符串二","字符串三", "字符串四", "字符串五",
					1d, 2d, 3d, 4d, 5d, true, false, true, false, true, new Date(), new Date(), new Date(), new Date(), new Date(),
					1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l);
			task.setDisplayOrder(i);
			this.save(task);
		}
	}
}
