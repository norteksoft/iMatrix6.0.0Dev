package com.norteksoft.wf.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.rank.dao.RankDao;
import com.norteksoft.bs.rank.dao.RankUserDao;
import com.norteksoft.bs.rank.entity.Subordinate;
import com.norteksoft.bs.rank.entity.Superior;
import com.norteksoft.bs.rank.enumeration.SubordinateType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.DataDictionary;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.wf.base.enumeration.DataDictUseType;
import com.norteksoft.wf.base.enumeration.DataDictUserType;
import com.norteksoft.wf.engine.client.DictQueryCondition;
import com.norteksoft.wf.engine.dao.DataDictionaryDao;
import com.norteksoft.wf.engine.dao.DataDictionaryTypeDao;
import com.norteksoft.wf.engine.dao.DataDictionaryUserDao;
import com.norteksoft.wf.engine.entity.DataDictionaryType;
import com.norteksoft.wf.engine.entity.DataDictionaryUser;


@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext-memcache.xml"})
public class WorkflowDataDictServiceServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	DataDictionaryDao dataDictionaryDao;
	
	@SpringBeanByName
	DataDictionaryUserDao dataDictionaryUserDao;
	
	@SpringBeanByName
	RankDao rankDao;
	
	@SpringBeanByName
	RankUserDao rankUserDao;
	
	@SpringBeanByName
	DataDictionaryTypeDao  dataDictionaryTypeDao;
	
	
	@Test
	public void queryDataDict(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfo");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfo");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> result = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getCandidate(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		List<String> result = ApiFactory.getDataDictService().getCandidate(param);
		Assert.assertTrue(result.get(0).equals("wangjing"));
	}
	
	@Test
	public void getCandidateTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<String> result = ApiFactory.getDataDictService().getCandidate(dicId);
		Assert.assertTrue(result.get(0).equals("wangjing"));
	}
	
	@Test
	public void getUserNames(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfoggggg");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfoggggg");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("ldx");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		HashMap<String,String> result = ApiFactory.getDataDictService().getUserNames(param);
		Assert.assertTrue(result.containsKey("ldx"));
		Assert.assertTrue(result.containsValue("人员名称"));
	}
	
	@Test
	public void getUserNamesTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		HashMap<String,String> result = ApiFactory.getDataDictService().getUserNames(dicId);
		Assert.assertTrue(result.containsKey("wangjing"));
		Assert.assertTrue(result.containsValue("人员名称"));
	}
	
	
	@Test
	public void queryDataDictTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary2 = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary2.setInfo("datadicIdsdsds");
		dataDictionary2.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary2);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		List<Long> param = new ArrayList<Long>();
		for(DataDictionary d :dics){
			param.add(d.getId());
		}
		
		
		List<DataDictionary> result = ApiFactory.getDataDictService().queryDataDict(param);
		Assert.assertEquals(2, result.size());
	}
	
	@Test
	public void queryDataDictThree(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		DataDictionary result = ApiFactory.getDataDictService().queryDataDict(dics.get(0).getId());
		Assert.assertNotNull(result);
	}
	
	@Test
	public void queryDataDicts(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		List<DataDictionary> result = ApiFactory.getDataDictService().queryDataDicts("wangjing");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getDirectLeader(){
		User user = new User();
		user.setLoginName("fdsfsdf");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		Long userId = ApiFactory.getAcsService().getUserByLoginName("fdsfsdf").getId();
		Superior superior = new Superior();
		List<Subordinate> dataDictionaryRankUser = new ArrayList<Subordinate>();
		Subordinate subordinate = new Subordinate();
		subordinate.setSubordinateType(SubordinateType.USER);
		subordinate.setTargetId(userId);
		subordinate.setCompanyId(1L);
		superior.setUserId(userId);
		superior.setDataDictionaryRankUser(dataDictionaryRankUser);
		subordinate.setDataDictionaryRank(superior);
		rankUserDao.save(subordinate);
		rankDao.save(superior);
		
		User result = ApiFactory.getDataDictService().getDirectLeader(userId);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getDirectLeaderTwo(){
		User user = new User();
		user.setLoginName("fdfdeettteee");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		Long userId = ApiFactory.getAcsService().getUserByLoginName("fdfdeettteee").getId();
		Superior superior = new Superior();
		List<Subordinate> dataDictionaryRankUser = new ArrayList<Subordinate>();
		Subordinate subordinate = new Subordinate();
		subordinate.setSubordinateType(SubordinateType.USER);
		subordinate.setTargetId(userId);
		subordinate.setLoginName("fdfdeettteee");
		subordinate.setCompanyId(1L);
		superior.setUserId(userId);
		superior.setDataDictionaryRankUser(dataDictionaryRankUser);
		subordinate.setDataDictionaryRank(superior);
		rankUserDao.save(subordinate);
		rankDao.save(superior);
		
		User result = ApiFactory.getDataDictService().getDirectLeader("fdfdeettteee");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getDirectLeaders(){
		User user = new User();
		user.setLoginName("fdfdeettteee");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		Long userId = ApiFactory.getAcsService().getUserByLoginName("fdfdeettteee").getId();
		Superior superior = new Superior();
		List<Subordinate> dataDictionaryRankUser = new ArrayList<Subordinate>();
		Subordinate subordinate = new Subordinate();
		subordinate.setSubordinateType(SubordinateType.USER);
		subordinate.setTargetId(userId);
		subordinate.setLoginName("fdfdeettteee");
		subordinate.setCompanyId(1L);
		superior.setUserId(userId);
		superior.setDataDictionaryRankUser(dataDictionaryRankUser);
		subordinate.setDataDictionaryRank(superior);
		rankUserDao.save(subordinate);
		rankDao.save(superior);
		
		List<User> result = ApiFactory.getDataDictService().getDirectLeaders("fdfdeettteee");
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void queryDataDictFour(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("dafsdsfghgfyu");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setTypeNo("fdsfdfsdhjhjkk");
		dataDictionary.setType(DataDictUseType.SET_PERMISSION_TEXT.getCode());
		dataDictionaryDao.save(dataDictionary);
		
		DataDictionaryType dataDictionaryType = new DataDictionaryType();
		dataDictionaryType.setNo("fdsfdfsdhjhjkk");
		dataDictionaryType.setCompanyId(1L);
		dataDictionaryType.setTypeIds(null);
		dataDictionaryTypeDao.save(dataDictionaryType);
		
		List<DataDictionary> result = ApiFactory.getDataDictService().queryDataDict("fdsfdfsdhjhjkk",DataDictUseType.SET_PERMISSION_TEXT);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getCandidateAddition(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setRemark("测试人员");
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		List<String> result = ApiFactory.getDataDictService().getCandidateAddition(param);
		Assert.assertEquals("wangjing", result.get(0).split(":")[0]);
		Assert.assertEquals("测试人员", result.get(0).split(":")[1]);
	}
	
	
	@Test
	public void getCandidateAdditionTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setRemark("测试人员");
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<String> result = ApiFactory.getDataDictService().getCandidateAddition(dicId);
		Assert.assertEquals("wangjing", result.get(0).split(":")[0]);
		Assert.assertEquals("测试人员", result.get(0).split(":")[1]);
	}

	
	@Test
	public void getCandidateThree(){
		User user = new User();
		user.setLoginName("wangjing");
		user.setCompanyId(1L);
		user.setDeleted(false);
		ApiFactory.getAcsService().saveUser(user);
		
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("ceshishuju");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setRemark("测试人员");
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("ceshishuju");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<String> result = ApiFactory.getDataDictService().getCandidate("ceshishuju");
		Assert.assertEquals("wangjing", result.get(0));
	}
	
	@Test
	public void getCandidateIds(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		List<Long> result = ApiFactory.getDataDictService().getCandidateIds(param);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getCandidateId(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<Long> result = ApiFactory.getDataDictService().getCandidateId(dicId);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getUserIds(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfoggggg");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfoggggg");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("ldx");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		HashMap<Long,String> result = ApiFactory.getDataDictService().getUserIds(param);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.containsValue("人员名称"));
	}
	
	@Test
	public void getUserIdsTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		HashMap<Long,String> result = ApiFactory.getDataDictService().getUserIds(dicId);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.containsValue("人员名称"));
	}
	
	@Test
	public void getCandidateIdsAddition(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setRemark("测试人员");
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<Long> param = new ArrayList<Long>();
		param.add(dicId);
		
		List<String> result = ApiFactory.getDataDictService().getCandidateIdsAddition(param);
		Assert.assertNotNull(result);
		Assert.assertEquals("测试人员", result.get(0).split(":")[1]);
	}
	
	@Test
	public void getCandidateIdsAdditionTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicIdsdsds");
		dataDictionary.setCompanyId(1L);
		dataDictionary.setRemark("测试人员");
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicIdsdsds");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<String> result = ApiFactory.getDataDictService().getCandidateIdsAddition(dicId);
		Assert.assertNotNull(result);
		Assert.assertEquals("测试人员", result.get(0).split(":")[1]);
	}
	
	@Test
	public void getCandidateIdsTwo(){
		com.norteksoft.wf.engine.entity.DataDictionary dataDictionary = new com.norteksoft.wf.engine.entity.DataDictionary();
		dataDictionary.setInfo("datadicInfofdf");
		dataDictionary.setCompanyId(1L);
		dataDictionaryDao.save(dataDictionary);
		
		DictQueryCondition dictQueryCondition = new DictQueryCondition();
		dictQueryCondition.setCondition(new StringBuilder("and dict.info=?"));
		List<Object> values = new ArrayList<Object>();
		values.add("datadicInfofdf");
		dictQueryCondition.setConditionValue(values);
		
		List<DataDictionary> dics = ApiFactory.getDataDictService().queryDataDict(dictQueryCondition);
		
		Long dicId = dics.get(0).getId();
		DataDictionaryUser dataDictionaryUser = new DataDictionaryUser();
		dataDictionaryUser.setDictId(dicId);
		dataDictionaryUser.setLoginName("wangjing");
		dataDictionaryUser.setInfoName("人员名称");
		dataDictionaryUser.setType(DataDictUserType.USER);
		dataDictionaryUserDao.save(dataDictionaryUser);
		
		
		List<Long> result = ApiFactory.getDataDictService().getCandidateIds("datadicInfofdf");
		Assert.assertNotNull(result);
	}
	
}
