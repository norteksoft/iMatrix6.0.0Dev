package com.norteksoft.wf.unit;


import org.junit.Assert;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.portal.dao.MessageInfoDao;
import com.norteksoft.portal.entity.Message;
import com.norteksoft.product.api.ApiFactory;

@Transactional(TransactionMode.ROLLBACK)
@SpringApplicationContext({"applicationContext-test.xml","applicationContext.xml","applicationContext-memcache.xml"})
public class PortalServiceTest extends BaseWorkflowTestCase {

	@SpringBeanByName
	private MessageInfoDao messageInfoDao;
	
	@Test
	public void addMessage() throws Exception{
		ApiFactory.getPortalService().addMessage("ems", "王晶的消息", "ldx", "ldx", "测试消息HEHEHEHEHEH!!!", "测试成功LALALALALA", null);
		Message result = messageInfoDao.findUnique("from Message m where m.category=? and m.companyId=?", "测试消息HEHEHEHEHEH!!!",1L);
		Assert.assertNotNull(result);
	}

}
