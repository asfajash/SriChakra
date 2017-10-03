package com.ml.controller;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.ml.bean.AdminPendingPDCOutput;
import com.ml.bean.AdminTodaysTask;
import com.ml.bean.DateInput;
import com.ml.bean.Message;
import com.ml.bean.PendingTaskEntryInput;
import com.ml.bean.TaskEntryDetailOutput;
import com.ml.bean.UploadDetailOutput;
import com.ml.bean.UserReport;
import com.ml.model.UserDetails;
import com.ml.dbservices.DBService;
import com.ml.model.ChequeEntry;
import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;
import com.ml.wrapper.UploadDetailsWrapper;

/*
 * 
 * Author Mohamed Asfaque Ali
 */

@Controller
@RequestMapping("/ML")
public class MLController {

	static final String version ="2.0.0";
	static final String userVersion ="2.0.4"; 
	static final String adminVersion ="2.0.3";

	static final Logger logger = Logger.getLogger(MLController.class);

	@Autowired
	DBService dbService;
	
	@RequestMapping(value = "/version", method = RequestMethod.GET,produces=MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String getVersion() {
		logger.debug("Version called...API");
		try{
			String output = "ML Version (DB Status - "+dbService.testConnection()+") :" +  version;
			logger.debug(output);
			return output;	
		} 
		catch(Exception e){
			logger.error(e);
			return e.getMessage();
		}
	}
	
	@RequestMapping(value = "/getAdminVersion", method = RequestMethod.POST,produces=MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String getAdminVersion(@RequestBody String appVersion) {
		logger.debug("Admin Version called...API : "+appVersion);
		return adminVersion;	
	}
	
	@RequestMapping(value = "/getUserVersion", method = RequestMethod.POST,produces=MediaType.TEXT_HTML_VALUE)
	public @ResponseBody String getUserVersion(@RequestBody String appVersion) {
		logger.debug("User Version called...API : "+appVersion);
		return userVersion;	
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message uploadDetails(@RequestBody String uploadDetailsString) {
		logger.debug("Entered uploadDetails API call...");
		Message message = new Message();
		try{
			Gson gson = new Gson();
			UploadDetailsWrapper UploadDetailsWrapper = gson.fromJson(uploadDetailsString, UploadDetailsWrapper.class);
			List<UploadDetails> listOfUploadDetails = UploadDetailsWrapper.getListOfUploadDetails();
			logger.debug("Uploading Details Count.."+listOfUploadDetails.size());
			dbService.uploadDetails(listOfUploadDetails);
			message.message = "Uploading Details Successfully Done...";
			message.status = 0;
		}
		catch(Exception e){
			logger.error(e);
			message.message = e.getMessage();
			message.status = 1;
		}
		logger.debug("Finished uploadDetails API call...");
		return message;
	}
	
	@RequestMapping(value = "/getUploadDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UploadDetailOutput getUploadDetails(@RequestBody String marketer){
		logger.debug("Entered getUploadDetails API call..."+marketer);		
		UploadDetailOutput uploadDetailOutput = new UploadDetailOutput();
		try {
			uploadDetailOutput = dbService.getUploadDetails(marketer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			Message message = new Message();
			message.message = e.getMessage();
			message.status = 1;
			uploadDetailOutput.listOfUploadDetails = null;
			uploadDetailOutput.message = message;
		}
		logger.debug("Finished getUploadDetails API call...");
		return uploadDetailOutput;
	}
	
	@RequestMapping(value = "/getUserUploadDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UploadDetailOutput getUserUploadDetails(@RequestBody UserDetails userDetails){
		logger.debug("Entered getUserUploadDetails API call..."+userDetails.getUserName());		
		UploadDetailOutput uploadDetailOutput = new UploadDetailOutput();
		try {
			uploadDetailOutput = dbService.getUploadDetails(userDetails.getUserName());
			Integer flag = dbService.updateUserDetails(userDetails);
			if(flag==0)
				throw new Exception("User not Updated");
			uploadDetailOutput.listOfPendingPDCCount = dbService.getPendingPDCCount(userDetails.getUserName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			Message message = new Message();
			message.message = e.getMessage();
			message.status = 1;
			uploadDetailOutput.listOfUploadDetails = null;
			uploadDetailOutput.message = message;
		}
		logger.debug("Finished getUploadDetails API call...");
		return uploadDetailOutput;
	}
	
	@RequestMapping(value = "/addTaskEntry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message addTaskEntry(@RequestBody TaskEntry taskEntry){
		logger.debug("Entered addTaskEntry API call..."+taskEntry.getUploadId());
		Message message = new Message();
		try{
			message.message = dbService.addTaskEntry(taskEntry);
			message.status = 0;
		}
		catch(Exception e){
			logger.error(e);
			message.message = e.getMessage();
			message.status = 1;
		}
		logger.debug("Finished addTaskEntry API call...");
		return message;
	}
	
	@RequestMapping(value = "/getTaskEntryDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TaskEntryDetailOutput getTaskEntryDetails(@RequestBody Integer uploadId){
		logger.debug("Entered getTaskEntryDetails API call..."+uploadId);		
		TaskEntryDetailOutput taskEntryDetailOutput = new TaskEntryDetailOutput();
		try {
			taskEntryDetailOutput = dbService.getTaskEntryDetails(uploadId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			Message message = new Message();
			message.message = e.getMessage();
			message.status = 1;
			taskEntryDetailOutput.listOfTaskEntryDetails = null;
			taskEntryDetailOutput.message = message;
		}
		logger.debug("Finished getTaskEntryDetails API call...");
		return taskEntryDetailOutput;
	}
	
	@RequestMapping(value = "/getPendingTaskEntryDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TaskEntryDetailOutput getPendingTaskEntryDetails(@RequestBody PendingTaskEntryInput pendingTaskEntryInput){
		logger.debug("Entered getPendingTaskEntryDetails API call..."+pendingTaskEntryInput.uploadId);		
		TaskEntryDetailOutput taskEntryDetailOutput = new TaskEntryDetailOutput();
		try {
			taskEntryDetailOutput = dbService.getPendingTaskEntryDetails(pendingTaskEntryInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			Message message = new Message();
			message.message = e.getMessage();
			message.status = 1;
			taskEntryDetailOutput.listOfTaskEntryDetails = null;
			taskEntryDetailOutput.message = message;
		}
		logger.debug("Finished getUploadDetails API call...");
		return taskEntryDetailOutput;
	}
	
	@RequestMapping(value = "/addChequeEntry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message addChequeEntry(@RequestBody ChequeEntry chequeEntry){
		logger.debug("Entered addChequeEntry API call..."+chequeEntry.getTaskId());
		Message message = new Message();
		try{
			message.message = dbService.addChequeEntry(chequeEntry);
			message.status = 0;
		}
		catch(Exception e){
			logger.error(e);
			message.message = e.getMessage();
			message.status = 1;
		}
		logger.debug("Finished addChequeEntry API call...");
		return message;
	}
	
	@RequestMapping(value = "/getUnDepositedCheque", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TaskEntryDetailOutput getUnDepositedCheque(@RequestBody Integer uploadId){
		logger.debug("Entered getUnDepositedCheque API call..."+uploadId);		
		TaskEntryDetailOutput taskEntryDetailOutput = new TaskEntryDetailOutput();
		try {
			taskEntryDetailOutput.listOfTaskEntryDetails = dbService.getUnDepositedCheque(uploadId);
			Message message = new Message();
			message.message = "Retrieved";
			message.status = 0;
			taskEntryDetailOutput.message = message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			Message message = new Message();
			message.message = e.getMessage();
			message.status = 1;
			taskEntryDetailOutput.message = message;
		}
		logger.debug("Finished getUnDepositedCheque API call...");
		return taskEntryDetailOutput;
	}
	
	//updateCheckEntry
	@RequestMapping(value = "/updateCheckEntry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message updateCheckEntry(@RequestBody ChequeEntry chequeEntry){
		logger.debug("Entered updateCheckEntry API call...");		
		Message message = new Message();
		try {
			dbService.updateCheckEntry(chequeEntry);
			message.message = "Successfully updated Cheque Entry..";
			message.status = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			message.message = e.getMessage();
			message.status = 1;

		}
		logger.debug("Finished updateCheckEntry API call...");
		return message;
	}
	
	@RequestMapping(value = "/getAdminPendingPDC", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AdminPendingPDCOutput getAdminPendingPDC(String code) {
		logger.debug("Entered getAdminPendingPDC API call "+code);
		AdminPendingPDCOutput adminPendingPDCOutput = null;
		Message message = new Message();
		try{
			adminPendingPDCOutput = dbService.getAdminPendingPDC();
			message.message = "Successfully Got values..";
			message.status = 0;
			adminPendingPDCOutput.message = message;	
		}
		catch(Exception e){
			logger.error(e);
			adminPendingPDCOutput = new AdminPendingPDCOutput();
			message.message = e.getMessage();
			message.status = 1;
			adminPendingPDCOutput.message = message;
		}
		logger.debug("Finished getAdminPendingPDC API call...");
		return adminPendingPDCOutput;
	}
	
	@RequestMapping(value = "/getUserReport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserReport getUserReport(@RequestBody DateInput dateInput) {
		logger.debug("Entered getUserReport API call "+dateInput.getFromDate()+" : "+dateInput.getToDate());
		UserReport userReport = null;
		try{
			userReport = dbService.getUserReport(dateInput.getFromDate(), dateInput.getToDate());
			userReport.message.message = "Success";
			userReport.message.status = 0;
		}
		catch(Exception e){
			logger.error(e);
			userReport.message.message = e.getMessage();
			userReport.message.status = 1;
		}
		logger.debug("Finished getUserReport API call...");
		return userReport;
	}
	
	@RequestMapping(value = "/getTodaysTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AdminTodaysTask getTodaysTask(@RequestBody String today) {
		logger.debug("Entered getTodaysReport API call "+today);
		AdminTodaysTask adminTodaysTask = null;
		try{
			adminTodaysTask = dbService.getTodaysTask(today);
			Message message = new Message();
			message.message = "Successfull";
			message.status = 0;
			adminTodaysTask.message = message;
		}
		catch(Exception e){
			Message message = new Message();
			message.message = "Error "+e.getMessage();
			message.status = 1;
			adminTodaysTask.message = message;
		}
		logger.debug("Finished getTodaysReport API call...");
		return adminTodaysTask;
	}
	
	
}
