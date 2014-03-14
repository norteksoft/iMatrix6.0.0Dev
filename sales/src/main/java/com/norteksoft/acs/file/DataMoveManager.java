package com.norteksoft.acs.file;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.engine.dao.DocumentFileDao;
import com.norteksoft.wf.engine.dao.OfficeDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentDao;
import com.norteksoft.wf.engine.dao.WorkflowAttachmentFileDao;
import com.norteksoft.wf.engine.entity.Document;
import com.norteksoft.wf.engine.entity.DocumentFile;
import com.norteksoft.wf.engine.entity.WorkflowAttachment;
import com.norteksoft.wf.engine.entity.WorkflowAttachmentFile;
/**
 * 正文附件迁移
 * @author Administrator
 *
 */
@Service
@Transactional
public class DataMoveManager {
	@Autowired
	private DocumentFileDao documentFileDao;
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private WorkflowAttachmentFileDao workflowAttachmentFileDao;
	@Autowired
	private WorkflowAttachmentDao workflowAttachmentDao;
	
	byte[] readFileContent(String fileName){
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
			return org.apache.commons.io.IOUtils.toByteArray(is);
		} catch (IOException e) {
			return null;
		}
	}
	public void dataMove(Long companyId){
		List<Document> documents=officeDao.getDocuments(companyId);
		for(Document document:documents){
			DocumentFile documentFile = documentFileDao.getDocumentFileByDocumentId(document.getId());
			if(documentFile!=null){
//					content = ApiFactory.getFileService().getFile(document.getFilePath());//不能先查询是否存在再保存，因为这样mongo会挂掉
				document.setFilePath(ApiFactory.getFileService().saveFile(documentFile.getFileBody(),document.getCompanyId()));
				officeDao.save(document);
			}
			documentFileDao.getSession().evict(documentFile);
		}
		documentFileDao.deleteDocumentFiles(companyId);
		List<WorkflowAttachment> workflowAttachments=workflowAttachmentDao.getAttachments(companyId);
		for(WorkflowAttachment att:workflowAttachments){
			WorkflowAttachmentFile workflowAttachmentFile = workflowAttachmentFileDao.getAttachmentFileByAttachmentId(att.getId());
			if(workflowAttachmentFile!=null){
//				content = ApiFactory.getFileService().getFile(att.getFilePath());//不能先查询是否存在再保存，因为这样mongo会挂掉
				att.setFilePath(ApiFactory.getFileService().saveFile(workflowAttachmentFile.getContent(),att.getCompanyId()));
				workflowAttachmentDao.save(att);
			}
			workflowAttachmentFileDao.getSession().evict(workflowAttachmentFile);
		}
		workflowAttachmentFileDao.deleteAttachmentFiles(companyId);
	}
	@Transactional(readOnly=false)
	public void dataInsert(){
		ThreadParameters parameters = new ThreadParameters(20805050l);
		ParameterUtils.setParameters(parameters);
		//new-baipishu.doc
//		//插入正文
//		List<Document> documents=officeDao.getDocuments();
//		int i=0,minNum=1422,maxNum=1453,maxNum2=1452;
//		for(Document document:documents){
//			i++;
//			if(i>minNum&&i<maxNum){
//				DocumentFile documentFile = new DocumentFile();
//				documentFile.setCompanyId(20805050l);
//				documentFile.setDocumentId(document.getId());
//				documentFile.setFileBody(readFileContent("new-baipishu.doc"));
//				documentFileDao.save(documentFile);
//			}
//			if(i>maxNum2){
//				break;
//			}
//		}
//		List<WorkflowAttachment> workflowAttachments=workflowAttachmentDao.getAttachments();
//		int i=0,minNum=672,maxNum=701,maxNum2=700;
//		for(WorkflowAttachment att:workflowAttachments){
//			i++;
//			if(i>minNum&&i<maxNum){
//				WorkflowAttachmentFile documentFile = new WorkflowAttachmentFile();
//				documentFile.setCompanyId(20805050l);
//				documentFile.setAttachmentId(att.getId());
//				documentFile.setContent(readFileContent("new-baipishu.doc"));
//				workflowAttachmentFileDao.save(documentFile);
//			}
//			if(i>maxNum2){
//				break;
//			}
//		}
		
	}
}
