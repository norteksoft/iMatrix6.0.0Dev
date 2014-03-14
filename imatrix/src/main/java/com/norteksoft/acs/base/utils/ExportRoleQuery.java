package com.norteksoft.acs.base.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.entity.organization.User;
import com.norteksoft.acs.entity.organization.Workgroup;
import com.norteksoft.acs.service.authorization.BranchAuthorityManager;
import com.norteksoft.acs.service.authorization.RoleManager;
import com.norteksoft.acs.service.authorization.StandardRoleManager;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserManager;
import com.norteksoft.acs.service.organization.WorkGroupManager;
import com.norteksoft.product.util.ContextUtils;

public class ExportRoleQuery {
	private static final Log logger = LogFactory.getLog(ExportRoleQuery.class);
	
	public static void exportRoleQuery(OutputStream fileOut, List<Long> exportIds, String exportType){
		HSSFWorkbook wb;
		String sheetName="";
		String name="";
		if("ROLE_USER".equals(exportType)){
			sheetName="user-role";
			name="姓名";
		}else if("ROLE_DEPARTMENT".equals(exportType)){
			sheetName="department-role";
			name="部门";
		}else{
			sheetName="workgroup-role";
			name="工作组";
		}
	    try
	    {
			wb = new HSSFWorkbook();
	    	HSSFSheet sheet=wb.createSheet(sheetName);
	        
	        HSSFFont boldFont = wb.createFont();
	        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	
	        HSSFCellStyle boldStyle = wb.createCellStyle();
	        boldStyle.setFont(boldFont);
	      
	        HSSFRow row = sheet.createRow(0);
	        HSSFCell cell0 = row.createCell(0);
	        cell0.setCellValue(name);
	        cell0.setCellStyle(boldStyle);
	        HSSFCell cell1 = row.createCell(1);
	        cell1.setCellValue("角色");
	        cell1.setCellStyle(boldStyle);
	        HSSFCell cell2 = row.createCell(2);
	        cell2.setCellValue("资源");
	        cell2.setCellStyle(boldStyle);
	        UserManager userManager=(UserManager)ContextUtils.getBean("userManager");
	        DepartmentManager departmentManager=(DepartmentManager)ContextUtils.getBean("departmentManager");
	        WorkGroupManager workGroupManager=(WorkGroupManager)ContextUtils.getBean("workGroupManager");
	        RoleManager roleManager=(RoleManager)ContextUtils.getBean("roleManager");
	        boolean containBranch=departmentManager.containBranches();
	        if("ROLE_USER".equals(exportType)){
				for(Long id:exportIds){
					User u=userManager.getUserById(id);
					String uName=u.getName()+(containBranch?"("+u.getSubCompanyName()+")":"");
					List<Role> roleList=roleManager.getRolesByUserId(u.getId());
					fillCell(uName,roleList,sheet,containBranch);
				}
			}else if("ROLE_DEPARTMENT".equals(exportType)){
				for(Long id:exportIds){
					Department d=departmentManager.getDepartment(id);
					String dName=d.getName()+(containBranch&&!d.getBranch()?"("+d.getSubCompanyName()+")":"");
					List<Role> roleList=roleManager.getRolesByDepartmentId(d.getId());
					fillCell(dName,roleList,sheet,containBranch);
				}
			}else{
				for(Long id:exportIds){
					Workgroup w=workGroupManager.getWorkGroup(id);
					String wName=w.getName()+(containBranch?"("+w.getSubCompanyName()+")":"");
					List<Role> roleList=roleManager.getRolesByWorkgroupId(w.getId());
					fillCell(wName,roleList,sheet,containBranch);
				}
			}
	     
	        wb.write(fileOut);
	    }catch(IOException exception){
	    	logger.debug(exception.getStackTrace());
		} 
	}
	
	private static void fillCell(String name,List<Role> roles,HSSFSheet sheet,boolean containBranch){
		RoleManager roleManager=(RoleManager)ContextUtils.getBean("roleManager");
		for(Role r:roles){//姓名/部门/工作组、角色、资源
			List<Function> functions=roleManager.getFunctions(r.getId());
			for(Function f:functions){
				HSSFRow rowi = sheet.createRow(sheet.getLastRowNum()+1);
				HSSFCell celli0 = rowi.createCell(0);
				celli0.setCellValue(name);
		        HSSFCell celli1 = rowi.createCell(1);//containBranch?"("+r.getSubCompanyName()+")":""
		        celli1.setCellValue(r.getName()+"("+r.getBusinessSystem().getName()+(containBranch?"/"+r.getSubCompanyName():"")+")");
		        HSSFCell celli2 = rowi.createCell(2);
		        celli2.setCellValue(f.getName()+"("+f.getBusinessSystem().getName()+")");
			}
		}
	}
}
