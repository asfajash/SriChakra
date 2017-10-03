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
@Table(name = "userDetails")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserDetails   implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer userId;
	private String userName, lastLogin, prevImei, currImei;
	private Double latitute, longitude;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getCurrImei() {
		return currImei;
	}
	public void setCurrImei(String currImei) {
		this.currImei = currImei;
	}
	public String getPrevImei() {
		return prevImei;
	}
	public void setPrevImei(String prevImei) {
		this.prevImei = prevImei;
	}
	public Double getLatitute() {
		return latitute;
	}
	public void setLatitute(Double latitute) {
		this.latitute = latitute;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
