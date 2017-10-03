package com.ml.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;

public class UserReport {

	public Message message;
	public List<String> usersList;
	public List<UploadDetails> uploadDetailsList;
	public Map<String, Double> userTotAmtMap;
	public Map<String, List<TaskEntry>> userTaskEntryMapS;
	public String fromDate, toDate;
	public UserReport(){
		message = new Message();
		userTotAmtMap = new LinkedHashMap<String, Double>();
		userTaskEntryMapS = new LinkedHashMap<String, List<TaskEntry>>();
	}
}
