package com.ml.dbservices;

import java.util.List;

import com.ml.bean.AdminPendingPDCOutput;
import com.ml.bean.AdminTodaysTask;
import com.ml.bean.PendingPDCCount;
import com.ml.bean.PendingTaskEntryInput;
import com.ml.bean.TaskEntryDetailOutput;
import com.ml.bean.UploadDetailOutput;
import com.ml.bean.UserReport;
import com.ml.model.ChequeEntry;
import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;
import com.ml.model.UserDetails;

/*
 * 
 * Author Mohamed Asfaque Ali
 */

public interface DBService {
	
	public Boolean testConnection() throws Exception;
	public void uploadDetails(List<UploadDetails> uploadDetails) throws Exception;
	public UploadDetailOutput getUploadDetails(String marketer) throws Exception;
	public String addTaskEntry(TaskEntry taskEntry) throws Exception;
	public TaskEntryDetailOutput getTaskEntryDetails(Integer uploadId) throws Exception;
	public TaskEntryDetailOutput getPendingTaskEntryDetails(PendingTaskEntryInput pendingTaskEntryInput) throws Exception;
	public String addChequeEntry(ChequeEntry chequeEntry) throws Exception;
	public Integer updateUserDetails(UserDetails userDetails) throws Exception;
	public List<PendingPDCCount> getPendingPDCCount(String marketer)throws Exception;
	public List<TaskEntry> getUnDepositedCheque(Integer uploadId) throws Exception;
	public void updateCheckEntry(ChequeEntry chequeEntry) throws Exception;
	public AdminPendingPDCOutput getAdminPendingPDC() throws Exception;
	public UploadDetails getUploadDetails(Integer uploadId) throws Exception;
	public UserReport getUserReport(String fromDate, String toDate) throws Exception;
	public AdminTodaysTask getTodaysTask(String today) throws Exception;
	
}
