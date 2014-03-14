package com.norteksoft.acs.service.sale;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.mms.base.data.DataHandle;
import com.norteksoft.mms.base.data.FileConfigModel;
/**
 * 基础数据导出导入
 * @author liudongxia
 *
 */
@Service
@Transactional
public class BasicDataManager {
	private Log log = LogFactory.getLog(BasicDataManager.class);
	
	private DataHandle dataHandle;
	@Autowired
	public void setDataHandle(DataHandle dataHandle) {
		this.dataHandle = dataHandle;
	}

	/**
	 * 导出基础数据
	 * @param fileOut
	 */
	public void exportBasicData(OutputStream fileOut,String systemIds,String dataCodes,Long companyId){
		log.debug("导出基础数据:exportBasicData开始");
		log.debug("param:systemIds="+systemIds+";dataCodes="+dataCodes+";companyId="+companyId);
		dataHandle.exportExecute(fileOut, systemIds, companyId, dataCodes);
		log.debug("导出基础数据:exportBasicData结束");
	}
	/**
	 * 导入基础数据
	 * @param file
	 * @param imatrixIp
	 * @param imatrixPort
	 * @param imatrixName
	 */
	public void importBasicData(File file,String imatrixIp,String imatrixPort,String imatrixName){
		log.debug("导入基础数据:importBasicData开始");
		log.debug("param:imatrixIp="+imatrixIp+",imatrixPort="+imatrixPort+",imatrixName="+imatrixName);
		dataHandle.importExecute(file, null, "basicData", imatrixIp,imatrixPort,imatrixName);
		log.debug("导入基础数据:importBasicData结束");
	}
	/**
	 * 初始化平台
	 * @param file
	 * @param companyId
	 */
	public void initData(File file,Long companyId){
		log.debug("初始化平台:initData开始");
		log.debug("param:companyId="+companyId);
		dataHandle.importExecute(file, companyId, "initData");
		log.debug("初始化平台:initData结束");
	}
	
	public List<FileConfigModel> getBasicDataTypes(){
		return dataHandle.getBasicDataTypes();
	}
}
