package com.ml.dbservices;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ml.bean.AdminPendingPDCOutput;
import com.ml.bean.AdminTodaysTask;
import com.ml.bean.Message;
import com.ml.bean.PendingPDCCount;
import com.ml.bean.PendingTaskEntryInput;
import com.ml.bean.TaskEntryDetailOutput;
import com.ml.bean.UploadDetailOutput;
import com.ml.bean.UserReport;
import com.ml.dbdao.DBDao;
import com.ml.model.ChequeEntry;
import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;
import com.ml.model.UserDetails;

/*
 * 
 * Author Mohamed Asfaque Ali
 */

public class DBServiceImpl implements DBService{

	@Autowired
	DBDao dbDao;

	private static final Logger logger = Logger.getLogger(DBServiceImpl.class);

	@Override
	public Boolean testConnection() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered testConnection() of DBService.");
		return dbDao.testConnection();
	}

	@Override
	public void uploadDetails(List<UploadDetails> listOfUploadDetails) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered uploadDetails() of DBService.");
		dbDao.updateUploadFlag();
		for(UploadDetails uploadDetails : listOfUploadDetails)
			dbDao.uploadDetails(uploadDetails);
		logger.debug("Finished uploadDetails() of DBService.");
	}

	@Override
	public UploadDetailOutput getUploadDetails(String marketer) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBService.");
		UploadDetailOutput uploadDetailOutput = new UploadDetailOutput();
		uploadDetailOutput.listOfUploadDetails = dbDao.getUploadDetails(marketer);
		Message message = new Message();
		message.message = "Successfully retrieved Data.. Count : "+uploadDetailOutput.listOfUploadDetails.size();
		message.status = 0;
		uploadDetailOutput.message = message;
		logger.debug("Finished getUploadDetails() of DBService.");
		return uploadDetailOutput;
	}

	@Override
	public String addTaskEntry(TaskEntry taskEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered addTaskEntry() of DBService.");
		return "Added Task with Task Id : "+dbDao.addTaskEntry(taskEntry);
	}

	@Override
	public TaskEntryDetailOutput getTaskEntryDetails(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBService.");
		TaskEntryDetailOutput taskEntryDetailOutput = new TaskEntryDetailOutput();
		taskEntryDetailOutput.listOfTaskEntryDetails = dbDao.getTaskEntryDetails(uploadId);
		Message message = new Message();
		message.message = "Successfully retrieved Data.. Count : "+taskEntryDetailOutput.listOfTaskEntryDetails.size();
		message.status = 0;
		taskEntryDetailOutput.message = message;
		logger.debug("Finished getUploadDetails() of DBService.");
		return taskEntryDetailOutput;
	}
	
	@Override
	public TaskEntryDetailOutput getPendingTaskEntryDetails(PendingTaskEntryInput pendingTaskEntryInput) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBService.");
		TaskEntryDetailOutput taskEntryDetailOutput = new TaskEntryDetailOutput();
		//taskEntryDetailOutput.listOfTaskEntryDetails = dbDao.getPendingTaskEntryDetails(pendingTaskEntryInput.uploadId);
		taskEntryDetailOutput.listOfPendingPDCCount = dbDao.getPendingPDCCount(pendingTaskEntryInput.marketer);
		Message message = new Message();
		message.message = "Successfully retrieved Data.. Count : "+taskEntryDetailOutput.listOfPendingPDCCount.size();
		message.status = 0;
		taskEntryDetailOutput.message = message;
		logger.debug("Finished getUploadDetails() of DBService.");
		return taskEntryDetailOutput;
	}

	@Override
	public String addChequeEntry(ChequeEntry chequeEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered addChequeEntry() of DBService.");
		return "Added Cheque with Cheque Id : "+dbDao.addChequeEntry(chequeEntry);
	}

	@Override
	public Integer updateUserDetails(UserDetails userDetails) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered updateUserDetails() of DBService.");
		return dbDao.updateUserDetails(userDetails);
	}

	@Override
	public List<PendingPDCCount> getPendingPDCCount(String marketer) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getPendingPDCCount() of DBService.");
		return dbDao.getPendingPDCCount(marketer);
	}

	@Override
	public List<TaskEntry> getUnDepositedCheque(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUnDepositedCheque() of DBService.");
		return dbDao.getUnDepositedCheque(uploadId);
	}

	@Override
	public void updateCheckEntry(ChequeEntry chequeEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered updateCheckEntry() of DBService."); 
		dbDao.updateCheckEntry(chequeEntry);
	}

	@Override
	public AdminPendingPDCOutput getAdminPendingPDC() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getAdminPendingPDC() of DBService."); 
		return dbDao.getAdminPendingPDC();
	}

	@Override
	public UploadDetails getUploadDetails(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBService."); 
		return dbDao.getUploadDetails(uploadId);
	}

	@Override
	public UserReport getUserReport(String fromDate, String toDate) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUserReport() of DBService."); 
		UserReport userReport = new UserReport();
		userReport.usersList = dbDao.getAllUser();
		for(String marketer : userReport.usersList){
			List<TaskEntry> taskEntryList = dbDao.getUserReport(marketer, fromDate, toDate);
			userReport.userTaskEntryMapS.put(marketer, taskEntryList);
		}
		userReport.uploadDetailsList = dbDao.getUploadDetails("ALL");
		userReport.fromDate = fromDate;
		userReport.toDate = toDate;
		return userReport;
	}

	@Override
	public AdminTodaysTask getTodaysTask(String today) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getTodaysTask() of DBService.");
		AdminTodaysTask adminTodaysTask = new AdminTodaysTask();
		adminTodaysTask.today = today;
		adminTodaysTask.uploadDetailsListS = dbDao.getTodaysTask(today);
		logger.debug("Finished getTodaysTask() of DBService.");
		return adminTodaysTask;
	}

}
