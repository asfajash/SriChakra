package com.ml.bean;

import java.util.List;
import java.util.Map;

import com.ml.model.TaskEntry;
import com.ml.model.UploadDetails;

public class AdminPendingPDCOutput {
	public Map<Integer, List<TaskEntry>> mapOfTaskEntry;
	public Map<Integer, UploadDetails> mapOfUploadDetails;
	public Map<Integer, Integer> mapOfUploadId;
	public Message message;
}
