package com.norteksoft.wf.engine.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.norteksoft.product.orm.IdEntity;
import com.norteksoft.wf.base.enumeration.InstanceHistoryType;

@Entity
@Table(name="WF_HISTORY_INSTANCE_HISTORY")
public class HistoryInstanceHistory extends IdEntity implements Serializable{
	/**
	 * 历史类别：流程跳转
	 */
	public static final Integer TYPE_FLOW_START = 0;
	public static final Integer TYPE_FLOW_INTO = 1;
	public static final Integer TYPE_FLOW_LEAVE = 2;
	public static final Integer TYPE_FLOW_END = 3;
	/**
	 * 历史类别：人工环节
	 */
	public static final Integer TYPE_TASK = 4;
	/**
	 * 历史类别：自动环节
	 */
	public static final Integer TYPE_AUTO = 5;
	
	private static final long serialVersionUID = 1L;
	private Integer type;  //类型： 0：流程跳转， 1：人工环节，2：自动环节
	
	private String taskName;
	private Long taskId;
	
	@Column(length=2000)
	private String transactionResult;     //办理结果
	
	@Transient
	private String transactorOpinion;    //办理意见
	
	private String instanceId; //实例ID
	
	private String executionId; //
	
	@Column(length=2000)
	private String transactor; //办理人
	
	private Boolean effective = true; //有效性，当环节被退回时失效
	
	private Boolean specialTask=false;
	
	//把流转历史信息格式化
	@Column(length=2000)
	private String inforOne;
	@Column(length=2000)
	private String inforTwo;
	@Column(length=2000)
	private String inforThree;
	@Column(length=2000)
	private String inforFour;
	@Column(length=2000)
	private String inforFive;
	@Column(length=2000)
	private String inforSix;
	@Column(length=2000)
	private String inforSeven;
	@Column(length=2000)
	private String inforEight;
	@Column(length=2000)
	private String inforNine;
	@Column(length=2000)
	private String inforTen ;
	@Transient
	private Long subTaskId;    //子流程id
	
	@Enumerated(EnumType.STRING)
	private InstanceHistoryType historyType;//流转历史类型

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getTransactor() {
		return transactor;
	}

	public void setTransactor(String transactor) {
		this.transactor = transactor;
	}

	public Boolean getEffective() {
		return effective;
	}

	public void setEffective(Boolean effective) {
		this.effective = effective;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public Boolean getSpecialTask() {
		return specialTask;
	}

	public void setSpecialTask(Boolean specialTask) {
		this.specialTask = specialTask;
	}

	public String getTransactionResult() {
		return transactionResult;
	}

	public void setTransactionResult(String transactionResult) {
		this.transactionResult = transactionResult;
	}

	public String getTransactorOpinion() {
		return transactorOpinion;
	}

	public void setTransactorOpinion(String transactorOpinion) {
		this.transactorOpinion = transactorOpinion;
	}

	public HistoryInstanceHistory(){}
	
	public HistoryInstanceHistory(Long companyId, String instanceId, Integer type, String info){
		this.setCompanyId(companyId);
		this.setCreatedTime(new Date());
		this.instanceId = instanceId;
		this.type = type;
		this.transactionResult = info;
	}

	public HistoryInstanceHistory(Long companyId, String instanceId, Integer type, String info,String taskName){
		this.setCompanyId(companyId);
		this.setCreatedTime(new Date());
		this.instanceId = instanceId;
		this.type = type;
		this.transactionResult = info;
		this.taskName = taskName;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public InstanceHistoryType getHistoryType() {
		return historyType;
	}

	public void setHistoryType(InstanceHistoryType historyType) {
		this.historyType = historyType;
	}

	public String getInforOne() {
		return inforOne;
	}

	public void setInforOne(String inforOne) {
		this.inforOne = inforOne;
	}

	public String getInforTwo() {
		return inforTwo;
	}

	public void setInforTwo(String inforTwo) {
		this.inforTwo = inforTwo;
	}

	public String getInforThree() {
		return inforThree;
	}

	public void setInforThree(String inforThree) {
		this.inforThree = inforThree;
	}

	public String getInforFour() {
		return inforFour;
	}

	public void setInforFour(String inforFour) {
		this.inforFour = inforFour;
	}

	public String getInforFive() {
		return inforFive;
	}

	public void setInforFive(String inforFive) {
		this.inforFive = inforFive;
	}

	public String getInforSix() {
		return inforSix;
	}

	public void setInforSix(String inforSix) {
		this.inforSix = inforSix;
	}

	public String getInforSeven() {
		return inforSeven;
	}

	public void setInforSeven(String inforSeven) {
		this.inforSeven = inforSeven;
	}

	public String getInforEight() {
		return inforEight;
	}

	public void setInforEight(String inforEight) {
		this.inforEight = inforEight;
	}

	public String getInforNine() {
		return inforNine;
	}

	public void setInforNine(String inforNine) {
		this.inforNine = inforNine;
	}

	public String getInforTen() {
		return inforTen;
	}

	public void setInforTen(String inforTen) {
		this.inforTen = inforTen;
	}

	public Long getSubTaskId() {
		return subTaskId;
	}

	public void setSubTaskId(Long subTaskId) {
		this.subTaskId = subTaskId;
	}
}
