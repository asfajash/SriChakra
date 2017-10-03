package com.ml.dbdao;

import java.util.List;

import com.ml.bean.AdminPendingPDCOutput;
import com.ml.bean.PendingPDCCount;
import com.ml.model.ChequeEntry;
import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;
import com.ml.model.UserDetails;

/*
 * 
 * Author Mohamed Asfaque Ali
 */

public interface DBDao {

	public Boolean testConnection() throws Exception;
	public void updateUploadFlag() throws Exception;
	public void uploadDetails(UploadDetails uploadDetails) throws Exception;
	public List<UploadDetails> getUploadDetails(String marketer) throws Exception;
	public Integer addTaskEntry(TaskEntry taskEntry) throws Exception;
	public List<TaskEntry> getTaskEntryDetails(Integer uploadId) throws Exception;
	public List<TaskEntry> getPendingTaskEntryDetails(Integer uploadId) throws Exception;	
	public Integer addChequeEntry(ChequeEntry chequeEntry) throws Exception;
	public Integer updateUserDetails(UserDetails userDetails)throws Exception;
	public List<PendingPDCCount> getPendingPDCCount(String marketer)throws Exception;	
	public List<TaskEntry> getUnDepositedCheque(Integer uploadId) throws Exception;
	public void updateCheckEntry(ChequeEntry chequeEntry) throws Exception;
	public AdminPendingPDCOutput getAdminPendingPDC() throws Exception;
	public UploadDetails getUploadDetails(Integer uploadId) throws Exception;
	public List<String> getAllUser() throws Exception;
	public List<TaskEntry> getUserReport(String marketer, String fromDate, String toDate) throws Exception;
	public List<UploadDetails> getTodaysTask(String today) throws Exception;
}
