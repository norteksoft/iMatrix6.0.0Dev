package com.norteksoft.wf.base.enumeration;


/**
 * 流转历史类型
 * @author wj
 *
 */
public enum InstanceHistoryType {
	/**
	 * 提交
	 */
	HISTORY_SUBMIT("instance.history.submit"),
	/**
	 * 同意
	 */
	HISTORY_AGREE("instance.history.agree"),
	/**
	 * 不同意
	 */
	HISTORY_DISAGREE("instance.history.disagree"),
	/**
	 * 加签
	 */
	HISTORY_ADDSIGN("instance.history.addsign"),
	/**
	 * 减签
	 */
	HISTORY_REMOVESIGN("instance.history.removesign"),
	/**
	 * 指派
	 */
	HISTORY_ASSIGN("instance.history.assign"),
	/**
	 * 退回
	 */
	HISTORY_BACK("instance.history.back"),
	/**
	 * 委托
	 */
	HISTORY_DELEGATE("instance.history.delegate"),
	/**
	 * 委托+指派
	 */
	HISTORY_DELEGATE_AND_ASSIGN("instance.history.delegateAndassign"),
	/**
	 * 跳转
	 */
	HISTORY_JUMP("instance.history.jump"),
	/**
	 * 增加办理人
	 */
	HISTORY_ADDTRANSACTOR("instance.history.addTransactor"),
	/**
	 * 减少办理人
	 */
	HISTORY_REMOVETRANSACTOR("instance.history.removeTransactor"),
	/**
	 * 更改办理人
	 */
	HISTORY_CHANGETRANSACTOR("instance.history.changesTransactor"),
	/**
	 * 委托+更改办理人
	 */
	HISTORY_CHANGETRANSACTOR_AND_DELEGATE("instance.history.changesTransactorAndDelegate"),
	/**
	 * 赞成
	 */
	HISTORY_AGREEMENT("instance.history.agreement"),
	/**
	 * 反对
	 */
	HISTORY_OPPOSE("instance.history.oppose"),
	/**
	 * 弃权
	 */
	HISTORY_KIKEN("instance.history.kiken"),
	/**
	 * 签收
	 */
	HISTORY_SIGNOFF("instance.history.kiken"),
	/**
	 * 交办
	 */
	HISTORY_SEND("instance.history.send"),
	/**
	 * 分发
	 */
	HISTORY_DISTRIBUTE("instance.history.distribute"),
	/**
	 * 已阅
	 */
	HISTORY_READED("instance.history.readed");
    
	private String code;
	
	InstanceHistoryType(String code){
		this.code = code;
	}

	public short getIndex(){
		return (short)(this.ordinal());
	}
	
	/**
	 * 返回该枚举值的名称的国际化资源key
	 * @return 国际化资源key
	 */
	public String getCode() {
		return code;
	}
	
	public static InstanceHistoryType valueOf(short ordinal){
		for(InstanceHistoryType ps:InstanceHistoryType.values()){
			if(ps.getIndex()==ordinal)return ps;
		}
		return HISTORY_SUBMIT;
	}
}
