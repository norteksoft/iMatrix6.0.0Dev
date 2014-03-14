package com.norteksoft.wf.unit;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringBeanByName;

import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

/**
 * 流程测试基类
 * 2012-6-7
 */
public class BaseWorkflowTestCase extends UnitilsJUnit4 {

	Map<String, Long> myDeptUsers;
	
	@Before
	public void beforeSet() throws Exception {
		ParameterUtils.setParameters(getPrmt(1L, 33L, "ldx","刘冬霞"));
	}
	
	@SpringBeanByName
	WorkflowDefinitionManager workflowDefinitionManager;

	ThreadParameters getPrmt(Long companyId, Long userId, String loginName){
		ThreadParameters tp = new ThreadParameters();
		tp.setCompanyId(companyId);
		tp.setUserId(userId);
		tp.setLoginName(loginName);
		return tp;
	}
//	
	ThreadParameters getPrmt(Long companyId, Long userId, String loginName, String userName){
		ThreadParameters tp = new ThreadParameters();
		tp.setCompanyId(companyId);
		tp.setUserId(userId);
		tp.setLoginName(loginName);
		tp.setUserName(userName);
		tp.setSystemCode("ems");
		tp.setSystemId(7l);
		return tp;
	}
	
	ThreadParameters getPrmt(Long companyId, Long userId, String loginName, String userName, Long sysId){
		ThreadParameters tp = getPrmt(companyId, userId, loginName);
		tp.setSystemId(sysId);
		tp.setUserName(userName);
		return tp;
	}
	
	String readFileContent(String fileName){
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			return org.apache.commons.io.IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			return null;
		}
	}

	void deploy(String fileName){
		String fileContent = readFileContent(fileName);
		Assert.assertNotNull("文件不存在", fileContent);
		// 创建流程定义
		Long id = workflowDefinitionManager.createWfDefinition(1L, fileContent, "expense", "ems");
		Assert.assertNotNull(id);
		// 部署流程
		try {
			String msg = workflowDefinitionManager.deployProcess(id);
			Assert.assertEquals("草稿 -> 启用", msg);
		} catch (Exception e) {
			Assert.assertTrue("流程启用失败", false);
		}
	}
	
	void setUsers(){
		myDeptUsers = new HashMap<String, Long>();
		myDeptUsers.put("ldx", 33L);
		myDeptUsers.put("test2", 65L);
	}
	/*

25 ligeyang 李戈杨 2 
28 zhaifuhong 翟福红 2 
29 panhuifeng 潘慧凤 2 
31 hemei 何梅 2 
32 chenxiaoyan 陈晓艳 2 
33 guorongrong 郭荣蓉 2 

48 zhangqingxin 张清欣 
50 zhengzhenglei 郑正雷 
51 liudongxia 刘冬霞 
52 dongzhiyun 董志云 
53 gujuhong 顾菊红 
54 kuangzhihui 匡志慧 
55 jiangwujin 姜武金 
56 wangjing 王晶 
57 huangjincheng 黄金程 
58 zhangtao 张涛 
59 jinzhenguo 靳振国 
60 qiguijiu 亓桂玖 
61 qiaoshasha 乔莎莎 
62 liudelong 刘德龙 
47 huhongchun 胡红春 2

	 */
}
