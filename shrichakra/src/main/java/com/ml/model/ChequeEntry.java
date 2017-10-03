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
@Table(name = "chequeEntry")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChequeEntry implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer chequeId;
	private Integer taskId;
	private String status, remarks;
	public Integer getChequeId() {
		return chequeId;
	}
	public void setChequeId(Integer chequeId) {
		this.chequeId = chequeId;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
		
}
