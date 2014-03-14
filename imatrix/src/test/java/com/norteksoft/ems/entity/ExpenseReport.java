package com.norteksoft.ems.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.engine.client.ExtendField;
import com.norteksoft.wf.engine.client.FormFlowable;
import com.norteksoft.wf.engine.client.WorkflowInfo;

@Entity
@Table(name = "ES_EXPENSE_REPORT")
public class ExpenseReport extends IdEntity implements FormFlowable {
	private static final long serialVersionUID = 1L;
	private String name;//报销人
	private String department;//部门
	private Integer invoiceAmount;//发票金额
	private double money;//金额
	private String reason;//事由
	@Temporal(TemporalType.TIMESTAMP)
	private Date outDate;//出差日期
	private String companion;//同行人
	private String firstApprover;//一级审批人
	private String firstLoginName;//一级审批登陆名
	private String firstOpinion;//一级审批说明
	private String secondApprover;//二级审批人
	private String thirdApprover;//三级审批人
	private String cashier;//财务
	private String documentName;//文件名称
	private String filePath; // 文档存放路径--存放文档的地方，非图标路径，肯定有个是与图标关联的
	private String suffix;//文件后缀名
	private Boolean addSign; //是否加签减签
	private String directLeaderName;  //创建人直属上级姓名
	
	@Column(length = 3000)
	private String readPersons; //批示传阅人员
	@Column(length = 3000)
	private String readPersonIds;//批示传阅人员Ids
	@Column(length = 3000)
	private String readLoginNames;//批示传阅人员登录名
	
	
	@Column(length = 3000)
	private String signPersons; //会签人员
	@Column(length = 3000)
	private String signPersonIds;//会签人员Ids
	@Column(length = 3000)
	private String signLoginNames; //会签人员登录名
	@Embedded
	private WorkflowInfo workflowInfo;

	@Embedded
	private ExtendField extendField;
	
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;
	private String field6;
	private String field7;
	private String field8;
	private String field9;
	private String field10;
	private String field11;
	private String field12;
	private String field13;
	private String field14;
	private String field15;
	private String field16;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Integer invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getFirstApprover() {
        return firstApprover;
    }

    public void setFirstApprover(String firstApprover) {
        this.firstApprover = firstApprover;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String companion) {
        this.companion = companion;
    }

    public String getSecondApprover() {
        return secondApprover;
    }

    public void setSecondApprover(String secondApprover) {
        this.secondApprover = secondApprover;
    }

    public String getThirdApprover() {
        return thirdApprover;
    }

    public void setThirdApprover(String thirdApprover) {
        this.thirdApprover = thirdApprover;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public WorkflowInfo getWorkflowInfo() {
		return workflowInfo;
	}

	public void setWorkflowInfo(WorkflowInfo workflowInfo) {
		this.workflowInfo = workflowInfo;
	}

	public ExtendField getExtendField() {
		return extendField;
	}

	public void setExtendField(ExtendField extendField) {
		this.extendField = extendField;
	}

	public String getFirstOpinion() {
		return firstOpinion;
	}

	public void setFirstOpinion(String firstOpinion) {
		this.firstOpinion = firstOpinion;
	}

	public String getFirstLoginName() {
		return firstLoginName;
	}

	public void setFirstLoginName(String firstLoginName) {
		this.firstLoginName = firstLoginName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getReadPersons() {
		return readPersons;
	}

	public void setReadPersons(String readPersons) {
		this.readPersons = readPersons;
	}

	public String getReadPersonIds() {
		return readPersonIds;
	}

	public void setReadPersonIds(String readPersonIds) {
		this.readPersonIds = readPersonIds;
	}

	public String getSignPersons() {
		return signPersons;
	}

	public void setSignPersons(String signPersons) {
		this.signPersons = signPersons;
	}

	public String getSignPersonIds() {
		return signPersonIds;
	}

	public void setSignPersonIds(String signPersonIds) {
		this.signPersonIds = signPersonIds;
	}

	public String getReadLoginNames() {
		return readLoginNames;
	}

	public void setReadLoginNames(String readLoginNames) {
		this.readLoginNames = readLoginNames;
	}

	public String getSignLoginNames() {
		return signLoginNames;
	}

	public void setSignLoginNames(String signLoginNames) {
		this.signLoginNames = signLoginNames;
	}
	
	@Override
	public String toString() {
		return "报销人："+this.name+"；报销金额："+this.money;
	}

	public Boolean getAddSign() {
		return addSign;
	}

	public void setAddSign(Boolean addSign) {
		this.addSign = addSign;
	}

	public String getDirectLeaderName() {
		return directLeaderName;
	}

	public void setDirectLeaderName(String directLeaderName) {
		this.directLeaderName = directLeaderName;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
	}

	public String getField11() {
		return field11;
	}

	public void setField11(String field11) {
		this.field11 = field11;
	}

	public String getField12() {
		return field12;
	}

	public void setField12(String field12) {
		this.field12 = field12;
	}

	public String getField13() {
		return field13;
	}

	public void setField13(String field13) {
		this.field13 = field13;
	}

	public String getField14() {
		return field14;
	}

	public void setField14(String field14) {
		this.field14 = field14;
	}

	public String getField15() {
		return field15;
	}

	public void setField15(String field15) {
		this.field15 = field15;
	}

	public String getField16() {
		return field16;
	}

	public void setField16(String field16) {
		this.field16 = field16;
	}

}
