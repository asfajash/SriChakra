package com.ml.dbdao;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;

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

@SuppressWarnings("unchecked")
public class DBDaoImpl implements DBDao{

	@Autowired
	SessionFactory sessionFactory;

	private static final Logger logger = Logger.getLogger(DBDaoImpl.class);

	@Override
	public Boolean testConnection() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered testConnection() of DBDao.");
		Session session = sessionFactory.getCurrentSession();		
		if(session==null)
			return false;
		if(session.isOpen())
			return true;
		return false;
	}

	@Override
	public void updateUploadFlag() throws Exception{
		logger.debug("Entered updateUploadFlag() of DBDao.");
		Session session = sessionFactory.getCurrentSession();
		try{
			String sql = "update uploadDetails set flag=0";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql);
			query.executeUpdate();
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished updateUploadFlag() of DBDao.");
	}
	
	@Override
	public void uploadDetails(UploadDetails uploadDetails) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered uploadDetails() of DBDao.");
		Session session = null;
		try{
			logger.info("Uploading Data : "+uploadDetails.getCustomer());
			Integer uploadId = -1;
			uploadDetails.setFlag(1);
			Session getSession = sessionFactory.getCurrentSession();
			try{
				String sql = "select uploadId from uploadDetails where customer = :customer";
				getSession.beginTransaction();
				Query query = getSession.createSQLQuery(sql);			
				query.setString("customer", uploadDetails.getCustomer());
				Object obj = query.list().get(0);
				if(obj != null)
					try{
						uploadId = Integer.parseInt(obj.toString());
					}
				catch(Exception e){}
			}
			catch(Exception e){	}
			finally{
				getSession.getTransaction().commit();
			}
			session = sessionFactory.getCurrentSession();	
			if(uploadId <  0){
				session.beginTransaction();
				session.persist(uploadDetails);
				session.getTransaction().commit();
			}
			else{
				session.beginTransaction();
				uploadDetails.setUploadId(uploadId);
				session.update(uploadDetails);
				session.getTransaction().commit();
			}
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished uploadDetails() of DBDao.");
	}

	private static Query createUploadDetailsSQLQuery(Session session, String marketer){
		String sql = "";
		Query query;
		session.beginTransaction();
		if(marketer.equalsIgnoreCase("ALL")){
			sql = "select ud.* from uploadDetails ud where flag=1";
			query = session.createSQLQuery(sql).addEntity(UploadDetails.class);
		}
		else{
			sql = "select ud.* from uploadDetails ud where ud.marketer = :marketer and flag=1 order by ud.committedDate";
			query = session.createSQLQuery(sql).addEntity(UploadDetails.class);
			query.setString("marketer", marketer);
		}
		return query;

	}

	@Override
	public List<UploadDetails> getUploadDetails(String marketer) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		List<UploadDetails> listOfUploadDetails = new ArrayList<UploadDetails>();
		try{
			Query query = createUploadDetailsSQLQuery(session, marketer);
			listOfUploadDetails = (ArrayList<UploadDetails>)query.list();
			session.getTransaction().commit();
			if(listOfUploadDetails.isEmpty())
				throw new Exception("No User Found..");
			logger.debug("Upload Details count : "+listOfUploadDetails.size());
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished getUploadDetails() of DBDao.");
		return listOfUploadDetails;
	}

	@Override
	public Integer addTaskEntry(TaskEntry taskEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered addTaskEntry() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		Integer taskId = -1;
		try{
			logger.info("Uploading Task for Data : "+taskEntry.getUploadId());
			session.beginTransaction();
			if(taskEntry.getStatus().equals("Y")){
				taskId = (Integer)session.save(taskEntry);
				UploadDetails uploadDetails = (UploadDetails)session.get(UploadDetails.class, taskEntry.getUploadId());
				uploadDetails.setTotalOutStanding(uploadDetails.getTotalOutStanding()-taskEntry.getAmount());
				uploadDetails.setTargetAchieved(uploadDetails.getTargetAchieved()+taskEntry.getAmount());
				if(!taskEntry.getChequeDate().isEmpty())
					uploadDetails.setCommittedDate(taskEntry.getChequeDate());
				taskEntry.setChequeDate(null);
				session.update(uploadDetails);
			}
			else if(taskEntry.getStatus().equals("N")){
				if(taskEntry.getPaymentMode().equals("CH")){
					String[] dates = taskEntry.getChequeDate().split("&");
					taskEntry.setChequeDate(dates[1]);
					taskId = (Integer)session.save(taskEntry);
					UploadDetails uploadDetails = (UploadDetails)session.get(UploadDetails.class, taskEntry.getUploadId());
					uploadDetails.setCommittedDate(dates[0]);
					//uploadDetails.setTargetAchieved(uploadDetails.getTargetAchieved()+taskEntry.getAmount());
					session.update(uploadDetails);
					ChequeEntry chequeEntry = new ChequeEntry();
					chequeEntry.setStatus("N/A");
					chequeEntry.setTaskId(taskId);
					session.save(chequeEntry);
				}
				else if(taskEntry.getPaymentMode().equals("NA")){
					UploadDetails uploadDetails = (UploadDetails)session.get(UploadDetails.class, taskEntry.getUploadId());
					uploadDetails.setCommittedDate(taskEntry.getChequeDate());
					session.update(uploadDetails);
				}
			}
			session.getTransaction().commit();
		} 
		catch(Exception e){
			logger.error(e);
			session.getTransaction().rollback();
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished addTaskEntry() of DBDao.");
		return taskId;
	}

	@Override
	public List<TaskEntry> getTaskEntryDetails(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getTaskEntryDetails() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		List<TaskEntry> listOfTaskEntryDetails = new ArrayList<TaskEntry>();
		try{
			String sql = "select te.* from taskEntry te where te.uploadId = :uploadId";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql).addEntity(TaskEntry.class);
			query.setInteger("uploadId", uploadId);
			listOfTaskEntryDetails = (ArrayList<TaskEntry>)query.list();
			session.getTransaction().commit();
			if(listOfTaskEntryDetails.size()==0)
				throw new Exception("No Task Found for Upload Id : "+uploadId);
			logger.debug("Task Entry Details count : "+listOfTaskEntryDetails.size());
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished getTaskEntryDetails() of DBDao.");
		return listOfTaskEntryDetails;
	}

	@Override
	public List<TaskEntry> getPendingTaskEntryDetails(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getPendingTaskEntryDetails() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		List<TaskEntry> listOfTaskEntryDetails = new ArrayList<TaskEntry>();
		try{
			String sql = "select te.* from taskEntry te where te.status = 'N' and te.uploadId = :uploadId";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql).addEntity(TaskEntry.class);
			query.setInteger("uploadId", uploadId);
			listOfTaskEntryDetails = (ArrayList<TaskEntry>)query.list();
			session.getTransaction().commit();
			if(listOfTaskEntryDetails.size()==0)
				throw new Exception("No Task Found for Upload Id : "+uploadId);
			logger.debug("Task Entry Details count : "+listOfTaskEntryDetails.size());
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished getTaskEntryDetails() of DBDao.");
		return listOfTaskEntryDetails;
	}

	@Override
	public Integer addChequeEntry(ChequeEntry chequeEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered addChequeEntry() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		Integer chequeId = -1;
		try{
			logger.info("Uploading Cheque for Task : "+chequeEntry.getTaskId());
			UploadDetails uploadDetails = null;
			TaskEntry taskEntry = null;
			session.beginTransaction();
			if(chequeEntry.getStatus().equals("C")){
				taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				uploadDetails = (UploadDetails)session.get(UploadDetails.class, taskEntry.getUploadId());
				taskEntry.setStatus("Y");
				uploadDetails.setTotalOutStanding(uploadDetails.getTotalOutStanding()-taskEntry.getAmount());
				uploadDetails.setTargetAchieved(uploadDetails.getTargetAchieved()+taskEntry.getAmount());
				session.update(taskEntry);
				session.update(uploadDetails);
			}
			else if(chequeEntry.getStatus().equals("P")){
				taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				taskEntry.setChequeDate(chequeEntry.getRemarks());
				chequeEntry.setRemarks("Collect amount on : "+chequeEntry.getRemarks());
			}
			chequeId = (Integer)session.save(chequeEntry);
			session.getTransaction().commit();
		} 
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished addChequeEntry() of DBDao.");
		return chequeId;
	}

	@Override
	public Integer updateUserDetails(UserDetails userDetails) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered updateUserDetails() of DBDao.");
		Session session = sessionFactory.getCurrentSession();
		Integer userId = 0;
		try{
			String sql = "select ud.* from userDetails ud where ud.userName = :userName";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql).addEntity(UserDetails.class);
			query.setString("userName", userDetails.getUserName());
			List<UserDetails> list = (List<UserDetails>)query.list();
			session.getTransaction().commit();
			session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			if(list.isEmpty()){
				logger.debug("No user found inserting the user..");
				userDetails.setPrevImei("1234567890");
				userId = (Integer)session.save(userDetails);
			}
			else{
				logger.debug("User Found updating user info");
				UserDetails oldUserDetails = list.get(0);
				userId = oldUserDetails.getUserId();
				userDetails.setUserId(oldUserDetails.getUserId());
				userDetails.setPrevImei(oldUserDetails.getCurrImei());
				session.update(userDetails);
			}
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}

		return userId;
	}

	@Override
	public List<PendingPDCCount> getPendingPDCCount(String marketer) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getPendingPDCCount() of DBDao.");
		Session session = sessionFactory.getCurrentSession();
		List<PendingPDCCount> listOfPendingPDCCount = new ArrayList<PendingPDCCount>();
		try{  
			String sql = "select te.uploadId, sum(te.amount), count(*) from taskEntry te "
					+ "where te.status = 'N'  "
					+ "and te.uploadId IN "
					+ "(select ud.uploadId from uploadDetails ud "
					+ "where ud.marketer =:marketer  "
					+ "and flag=1) "
					+ "group by uploadId";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql);
			query.setString("marketer", marketer);
			List<Object[]> listOfObject = query.list();
			session.getTransaction().commit();
			if(listOfObject.size()>0){
				for(Object[] object : listOfObject){
					PendingPDCCount pendingPDCCount = new PendingPDCCount();
					pendingPDCCount.uploadId = Integer.parseInt(object[0].toString());
					pendingPDCCount.totalPendingAmount = Double.parseDouble(object[1].toString());
					pendingPDCCount.pendingCount = Long.parseLong(object[2].toString());
					listOfPendingPDCCount.add(pendingPDCCount);
				}
			}
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		return listOfPendingPDCCount;
	}

	@Override
	public List<TaskEntry> getUnDepositedCheque(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUnDepositedCheque() of DBDao.");
		Session session = sessionFactory.getCurrentSession();
		List<TaskEntry> listOfTaskEntry = null;
		try{  
			String sql = "select * from taskEntry "
					+ "where taskId in ("
					+ "select taskId from taskEntry "
					+ "where uploadId = :uploadId) "
					+ "and taskId in ("
					+ "select taskId from chequeEntry "
					+ "where chequeId in ( "
					+ "select max(chequeId) from  chequeEntry "
					+ "group by taskId) "
					+ "and status = 'N/A')";
			session.beginTransaction();
			Query query = session.createSQLQuery(sql).addEntity(TaskEntry.class);
			query.setInteger("uploadId", uploadId);
			listOfTaskEntry = (ArrayList<TaskEntry>)query.list();
			if(listOfTaskEntry.size()==0)
				throw new Exception("No Undeposited Cheque....");
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		return listOfTaskEntry;
	}

	@Override
	public void updateCheckEntry(ChequeEntry chequeEntry) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered updateCheckEntry() of DBDao.");
		String status = chequeEntry.getStatus();
		Session session = sessionFactory.getCurrentSession();
		try{
			session.beginTransaction();
			if(status.equals("D")){
				TaskEntry taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				taskEntry.setStatus(status);
				session.update(taskEntry);
				session.save(chequeEntry);				
			}
			else if(status.equals("RD")){
				TaskEntry taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				taskEntry.setStatus("N");
				taskEntry.setChequeDate(chequeEntry.getRemarks());
				chequeEntry.setStatus("N/A");
				session.update(taskEntry);
				session.save(chequeEntry);	
			}
			else if(status.equals("C")){
				TaskEntry taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				taskEntry.setStatus("Y");
				UploadDetails uploadDetails = (UploadDetails)session.get(UploadDetails.class, taskEntry.getUploadId());
				uploadDetails.setTotalOutStanding(uploadDetails.getTotalOutStanding()-taskEntry.getAmount());
				uploadDetails.setTargetAchieved(uploadDetails.getTargetAchieved()+taskEntry.getAmount());
				session.update(taskEntry);
				session.update(uploadDetails);
				session.save(chequeEntry);	
			}
			else if(status.equals("CD")){
				TaskEntry taskEntry = (TaskEntry)session.get(TaskEntry.class, chequeEntry.getTaskId());
				taskEntry.setStatus(status);
				taskEntry.setChequeDate(chequeEntry.getRemarks());
				session.update(taskEntry);
				session.save(chequeEntry);
			}
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw new Exception(e);
		}
		logger.debug("Finished updateCheckEntry() of DBDao.");
	}
	
	@Override
	public AdminPendingPDCOutput getAdminPendingPDC() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getAdminPendingPDC() of DBDao.");
		Session session = sessionFactory.getCurrentSession();
		AdminPendingPDCOutput adminPendingPDCOutput = new AdminPendingPDCOutput();
		Map<Integer, List<TaskEntry>> mapOfTaskEntry = new LinkedHashMap<Integer, List<TaskEntry>>();
		Map<Integer, UploadDetails> mapOfUploadDetails = new LinkedHashMap<Integer, UploadDetails>();
		Map<Integer, Integer> mapOfUploadId = new LinkedHashMap<Integer, Integer>();
		List<TaskEntry> listOfTaskEntry = null;
		try{
			session.beginTransaction();
			String sql = "select * from taskEntry "
					+ "where taskId in ("
					+ "select taskId from chequeEntry "
					+ "where chequeId in ( "
					+ "select max(chequeId) from  chequeEntry "
					+ "group by taskId) "
					+ "and status = 'D') "
					+ "order by chequeDate";
			Query query = session.createSQLQuery(sql).addEntity(TaskEntry.class);
			listOfTaskEntry = (ArrayList<TaskEntry>)query.list();
			session.getTransaction().commit();
			if(listOfTaskEntry.size()==0)
				throw new Exception("No Deposited Cheque....");
			Integer counter = 0;
			for(TaskEntry taskEntry : listOfTaskEntry){
				if(mapOfUploadDetails.containsKey(taskEntry.getUploadId()))
					mapOfTaskEntry.get(taskEntry.getUploadId()).add(taskEntry);
				else{
					mapOfUploadId.put(counter, taskEntry.getUploadId());
					mapOfUploadDetails.put(taskEntry.getUploadId(), getUploadDetails(taskEntry.getUploadId()));
					List<TaskEntry> tempList = new ArrayList<TaskEntry>();
					tempList.add(taskEntry);
					mapOfTaskEntry.put(taskEntry.getUploadId(), tempList);
					counter++;
				}
			}
		}
		catch(Exception e){
			logger.error(e);
			throw new Exception(e);
		}
		logger.debug("Finished getAdminPendingPDC() of DBDao.");
		adminPendingPDCOutput.mapOfTaskEntry = mapOfTaskEntry;
		adminPendingPDCOutput.mapOfUploadDetails = mapOfUploadDetails;
		adminPendingPDCOutput.mapOfUploadId = mapOfUploadId; 
		return adminPendingPDCOutput;
	}

	@Override
	public UploadDetails getUploadDetails(Integer uploadId) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBDao. for "+uploadId);
		UploadDetails uploadDetails = null;
		Session session = sessionFactory.getCurrentSession();	
		try{
			session.beginTransaction();
			uploadDetails = (UploadDetails)session.get(UploadDetails.class, uploadId);
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw new Exception(e);
		}
		logger.debug("Finished getUploadDetails() of DBDao.");
		return uploadDetails;
	}

	@Override
	public List<String> getAllUser() throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getAllUser() of DBDao");
		List<String> usersList = new ArrayList<String>();
		Session session = sessionFactory.getCurrentSession();	
		try{
			session.beginTransaction();
			String sql = "select distinct marketer from uploadDetails order by marketer";
			Query query = session.createSQLQuery(sql);
			usersList = (List<String>) query.list();
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw new Exception(e);
		}
		logger.debug("Finished getAllUser() of DBDao.");
		return usersList;
	}

	@Override
	public List<TaskEntry> getUserReport(String marketer, String fromDate, String toDate) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUserReport() of DBDao "+marketer);
		List<TaskEntry> usersTaskList = new ArrayList<TaskEntry>();
		Session session = sessionFactory.getCurrentSession();	
		try{
			session.beginTransaction();
			String sql = "select * from taskEntry where entryDate >= :fromDate and entryDate<= :toDate and uploadId in (select uploadId from uploadDetails where marketer = :marketer) order by entryDate";
			Query query = session.createSQLQuery(sql);
			query.setString("fromDate", fromDate);
			query.setString("toDate", toDate);
			query.setString("marketer", marketer);
			usersTaskList = (List<TaskEntry>) query.list();
			session.getTransaction().commit();
		}
		catch(Exception e){
			logger.error(e);
			throw new Exception(e);
		}
		logger.debug("Finished getUserReport() of DBDao.");
		return usersTaskList;
	}

	@Override
	public List<UploadDetails> getTodaysTask(String today) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Entered getUploadDetails() of DBDao.");
		Session session = sessionFactory.getCurrentSession();	
		List<UploadDetails> listOfUploadDetails = new ArrayList<UploadDetails>();
		try{
			session.beginTransaction();
			String sql = "select * from uploadDetails where flag=1 and committedDate = :today order by marketer";
			Query query = session.createSQLQuery(sql);
			query.setString("today", today);
			listOfUploadDetails = (ArrayList<UploadDetails>)query.list();
			session.getTransaction().commit();
			logger.debug("Upload Details count : "+listOfUploadDetails.size());
		}
		catch(Exception e){
			logger.error(e);
			throw e;
		}
		finally{
			if(session.isOpen())
				session.close();
		}
		logger.debug("Finished getTodaysTask() of DBDao.");
		return listOfUploadDetails;
	}
	
	
	
}
