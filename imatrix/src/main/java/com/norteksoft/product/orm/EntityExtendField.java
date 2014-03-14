package com.norteksoft.product.orm;

import java.util.Date;

import javax.persistence.Embeddable;

/**
 * 默认的备用字段 
 * <br><code>String</code>字段5个
 * <br><code>Date</code>字段2个
 * <br><code>Double</code>字段3个
 * @author wurong
 *
 */
@Embeddable
public class EntityExtendField {

	protected String exString1;
	protected String exString2;
	protected String exString3;
	protected String exString4;
	protected String exString5;

	protected Date exDate1;
	protected Date exDate2;

	protected Double exDouble1;
	protected Double exDouble2;
	protected Double exDouble3;
	public String getExString1() {
		return exString1;
	}
	public void setExString1(String exString1) {
		this.exString1 = exString1;
	}
	public String getExString2() {
		return exString2;
	}
	public void setExString2(String exString2) {
		this.exString2 = exString2;
	}
	public String getExString3() {
		return exString3;
	}
	public void setExString3(String exString3) {
		this.exString3 = exString3;
	}
	public String getExString4() {
		return exString4;
	}
	public void setExString4(String exString4) {
		this.exString4 = exString4;
	}
	public String getExString5() {
		return exString5;
	}
	public void setExString5(String exString5) {
		this.exString5 = exString5;
	}
	public Date getExDate1() {
		return exDate1;
	}
	public void setExDate1(Date exDate1) {
		this.exDate1 = exDate1;
	}
	public Date getExDate2() {
		return exDate2;
	}
	public void setExDate2(Date exDate2) {
		this.exDate2 = exDate2;
	}
	public Double getExDouble1() {
		return exDouble1;
	}
	public void setExDouble1(Double exDouble1) {
		this.exDouble1 = exDouble1;
	}
	public Double getExDouble2() {
		return exDouble2;
	}
	public void setExDouble2(Double exDouble2) {
		this.exDouble2 = exDouble2;
	}
	public Double getExDouble3() {
		return exDouble3;
	}
	public void setExDouble3(Double exDouble3) {
		this.exDouble3 = exDouble3;
	}


}
