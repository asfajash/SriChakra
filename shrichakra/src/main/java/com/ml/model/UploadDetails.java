package com.ml.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * 
 * Author Mohamed Asfaque Ali
 */

@Entity
@Table(name = "uploadDetails")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UploadDetails implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer uploadId; 
	private Integer flag;
	private Double custId, totalOutStanding, target, m0_30, m31_60, m61_90, m91_180, above_180, targetAchieved;
	private String customer, marketer, custType, committedDate, remarks;
	
	
	
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Double getTargetAchieved() {
		return targetAchieved;
	}
	public void setTargetAchieved(Double targetAchieved) {
		this.targetAchieved = targetAchieved;
	}
	
	public String getCommittedDate() {
		return committedDate;
	}
	public void setCommittedDate(String committedDate) {
		this.committedDate = committedDate;
	}
	public Double getCustId() {
		return custId;
	}
	public void setCustId(Double custId) {
		this.custId = custId;
	}
	public Integer getUploadId() {
		return uploadId;
	}
	public void setUploadId(Integer uploadId) {
		this.uploadId = uploadId;
	}
	public Double getTotalOutStanding() {
		return totalOutStanding;
	}
	public void setTotalOutStanding(Double totalOutStanding) {
		this.totalOutStanding = totalOutStanding;
	}
	public Double getTarget() {
		return target;
	}
	public void setTarget(Double target) {
		this.target = target;
	}
	public Double getM0_30() {
		return m0_30;
	}
	public void setM0_30(Double m0_30) {
		this.m0_30 = m0_30;
	}
	public Double getM31_60() {
		return m31_60;
	}
	public void setM31_60(Double m31_60) {
		this.m31_60 = m31_60;
	}
	public Double getM61_90() {
		return m61_90;
	}
	public void setM61_90(Double m61_90) {
		this.m61_90 = m61_90;
	}
	public Double getM91_180() {
		return m91_180;
	}
	public void setM91_180(Double m91_180) {
		this.m91_180 = m91_180;
	}
	public Double getAbove_180() {
		return above_180;
	}
	public void setAbove_180(Double above_180) {
		this.above_180 = above_180;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getMarketer() {
		return marketer;
	}
	public void setMarketer(String marketer) {
		this.marketer = marketer;
	}
	
}
