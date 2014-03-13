package com.example.expense.loan.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.ListViewDao;
import com.norteksoft.mms.form.entity.FormControl;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListColumn;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.jdbc.JdbcSupport;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.orm.hibernate.HibernateDao;
import com.norteksoft.product.util.ContextUtils;

@Repository
public class TestFormDao extends HibernateDao<Object, Long> {

	@Autowired
	private ListViewDao listViewDao;
	
	@Autowired
	private ListColumnDao listColumnDao;
	
	@Autowired
	private JdbcSupport jdbcDao;
	public Page<Object> list(Page<Object> page, String code) {
		ListView view = listViewDao.getListViewByCode(code);
		
		String fieldSql = getSelectPartByListCode(code);
		
		StringBuilder sql=new StringBuilder();
		sql.append("select ");
		sql.append(fieldSql);
		sql.append(" from ");
		sql.append(view.getDataTable().getName()).append(" ");
		sql.append("where company_id=? ");
		return this.searchPageBySql(page, sql.toString(), ContextUtils.getCompanyId());
	}
	private String getSelectPartByListCode(String listCode){
		List<ListColumn> columns = listColumnDao.getColumnsByViewCode(listCode);
		StringBuffer columnsStr = new StringBuffer();
		String columnName = null;
		for (int i = 0; i < columns.size(); i++) {
			if(i != 0){
				columnsStr.append(",");
			}
			columnName = columns.get(i).getTableColumn().getName();
			if(!"id".equalsIgnoreCase(columnName)){
				columnsStr.append("dt_");
			}
			columnsStr.append(columns.get(i).getTableColumn().getName());
		}
		return columnsStr.toString();
	}
	
	public Long update(Map<String,String[]> parameter, FormView form, List<FormControl> fields, Long id){
		return jdbcDao.updateTable(parameter, form, fields, id);
	}
	
	public Long save(Map<String,String[]> parameter, FormView form, List<FormControl> fields){
		return jdbcDao.insertTable(parameter, form, fields);
	}
	
	/**
	 * 查询数据 
	 * @param tableName 表名
	 * @param id 数据ID
	 * @return
	 */
	public Object getDateById(String tableName, Long id){
		return jdbcDao.getDataMap(tableName, id);
	}
}
