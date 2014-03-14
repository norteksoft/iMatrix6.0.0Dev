package com.norteksoft.wf.unit;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.options.dao.OptionGroupDao;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.bs.signature.dao.SignatureDao;
import com.norteksoft.bs.signature.entity.Signature;
import com.norteksoft.product.api.ApiFactory;



@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class SettingServiceTest extends BaseWorkflowTestCase {
	
	@SpringBeanByName
	OptionDao optionDao;
	
	@SpringBeanByName
	OptionGroupDao optionGroupDao;
	
	@SpringBeanByName
	SignatureDao signatureDao;

	@Test
	public void getOptionGroupDefaultValue(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		String result = ApiFactory.getSettingService().getOptionGroupDefaultValue("wangjing_optiongroup_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("ppppppp", result);
	}
	
	
	@Test
	public void getOptionGroupDefaultValueTwo(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		OptionGroup og = optionGroupDao.findUniqueBy("code", "wangjing_optiongroup_code");
		String result = ApiFactory.getSettingService().getOptionGroupDefaultValue(og.getId());
		Assert.assertNotNull(result);
		Assert.assertEquals("ppppppp", result);
	}
	
	@Test
	public void getOptionGroups(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		OptionGroup optionGroup2 = new OptionGroup();
		optionGroup2.setCode("wangjing_optiongroup_code2");
		List<Option> options2 = new ArrayList<Option>();
		options2.add(option);
		optionGroup.setOptions(options2);
		
		optionGroupDao.save(optionGroup);
		optionGroupDao.save(optionGroup2);
		optionDao.save(option);
		
		List<com.norteksoft.product.api.entity.OptionGroup> result = ApiFactory.getSettingService().getOptionGroups();
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getOptionGroupByCode(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		com.norteksoft.product.api.entity.OptionGroup result = ApiFactory.getSettingService().getOptionGroupByCode("wangjing_optiongroup_code");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_optiongroup_code", result.getCode());
	}
	
	@Test
	public void getOptionGroupByName(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		optionGroup.setName("wangjing_optiongroup_name");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		com.norteksoft.product.api.entity.OptionGroup result = ApiFactory.getSettingService().getOptionGroupByName("wangjing_optiongroup_name");
		Assert.assertNotNull(result);
		Assert.assertEquals("wangjing_optiongroup_name", result.getName());
	}
	
	@Test
	public void getOptionsByGroup(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		optionGroup.setName("wangjing_optiongroup_name");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		OptionGroup og = optionGroupDao.findUniqueBy("code", "wangjing_optiongroup_code");
		List<com.norteksoft.product.api.entity.Option> result = ApiFactory.getSettingService().getOptionsByGroup(og.getId());
		Assert.assertNotNull(result);
	}
	
	
	@Test
	public void getOptionsByGroupCode(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		optionGroup.setName("wangjing_optiongroup_name");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		List<com.norteksoft.product.api.entity.Option> result = ApiFactory.getSettingService().getOptionsByGroupCode("wangjing_optiongroup_code");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getOptionsByGroupName(){
		Option option = new Option();
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.setCode("wangjing_optiongroup_code");
		optionGroup.setName("wangjing_optiongroup_name");
		List<Option> options = new ArrayList<Option>();
		options.add(option);
		optionGroup.setOptions(options);
		option.setCompanyId(1L);
		option.setSelected(true);
		option.setValue("ppppppp");
		option.setOptionGroup(optionGroup);
		
		optionGroupDao.save(optionGroup);
		optionDao.save(option);
		
		List<com.norteksoft.product.api.entity.Option> result = ApiFactory.getSettingService().getOptionsByGroupName("wangjing_optiongroup_name");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getHolidaySettingDays() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse("2013-06-17");
		Date endDate = sdf.parse("2013-06-25");
		Map<String, List<Date>> result = ApiFactory.getSettingService().getHolidaySettingDays(startDate,endDate);
		Assert.assertNotNull(result);
		Assert.assertEquals(7, result.get("workDate").size());
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-17")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-18")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-19")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-20")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-21")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-24")));
		Assert.assertTrue(result.get("workDate").contains(sdf.parse("2013-06-25")));
		Assert.assertEquals(2, result.get("spareDate").size());
		Assert.assertTrue(result.get("spareDate").contains(sdf.parse("2013-06-22")));
		Assert.assertTrue(result.get("spareDate").contains(sdf.parse("2013-06-23")));
	}
	
	@Test
	public void getInternationOptionValue() {
		String result = ApiFactory.getSettingService().getInternationOptionValue("ss");
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getSignIdByUserName() {
		Signature signature = new Signature();
		signature.setCompanyId(1L);
		signature.setUserName("ldx");
		signature.setUserId(33l);
		signatureDao.save(signature);
		
		Long result = ApiFactory.getSettingService().getSignIdByUserName("ldx");
		Assert.assertNotNull(result);
		Signature s = signatureDao.findUniqueBy("userId", 33l);
		Assert.assertEquals(s.getId(), result);
	}
}
