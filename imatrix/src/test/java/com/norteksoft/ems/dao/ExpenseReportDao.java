package com.norteksoft.ems.dao;

import org.springframework.stereotype.Repository;

import com.norteksoft.ems.entity.ExpenseReport;
import com.norteksoft.product.orm.hibernate.HibernateDao;
/**
 *打印Dao
 * 
 * @author zzl
 *
 */
@Repository
public class ExpenseReportDao extends HibernateDao<ExpenseReport, Long> {

} 
