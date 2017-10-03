DROP TABLE uploadDetails;
DROP TABLE taskEntry;
DROP TABLE chequeEntry;
DROP TABLE userDetails;

TRUNCATE TABLE uploadDetails;
TRUNCATE TABLE taskEntry;
TRUNCATE TABLE chequeEntry;
TRUNCATE ABLE userDetails;

CREATE TABLE uploadDetails(
   uploadId integer NOT NULL AUTO_INCREMENT,
   custId integer,
   customer varchar(50),
   totalOutStanding double,
   target double,
   targetAchieved double,
   committedDate date,
   m0_30 double,
   m31_60 double,
   m61_90 double,
   m91_180 double,
   above_180 double,
   marketer varchar(20),
   custType char(2),
   remarks varchar(60),
   flag integer,
   PRIMARY KEY( uploadId )
);
CREATE TABLE taskEntry(
  taskId integer NOT NULL AUTO_INCREMENT,
  uploadId integer,
  status varchar(5),
  amount double,
  paymentMode varchar(5),
  chequeNumber varchar(20),
  chequeDate date,
  bank varchar(25),
  remarks varchar(50),
  entryDate date,
  latitute double,
  longitude double,
  PRIMARY KEY( taskId )
);

CREATE TABLE chequeEntry(
	chequeId integer NOT NULL AUTO_INCREMENT,
	taskId integer,
	status varchar(5),
	remarks varchar(50),
	PRIMARY KEY( chequeId )	
);

CREATE TABLE userDetails(
	userId integer NOT NULL AUTO_INCREMENT,
	userName varchar(20) UNIQUE,
	currImei varchar(15),
	prevImei varchar(15),
	latitute double,
	longitude double,
	lastLogin varchar(50),
	PRIMARY KEY( userId )	
);
commit;
select * from uploadDetails where marketer = 'JOHN' order by committedDate;
select * from taskEntry;
select * from chequeEntry;
select * from userDetails;
select uploadId, sum(amount), count(*) from taskEntry where status = 'N' group by uploadId;
select te.uploadId, sum(te.amount), count(*) from TaskEntry te where te.status = 'N'  and te.uploadId IN (select ud.uploadId from UploadDetails ud where ud.marketer = 'B') group by uploadId;
select * from chequeEntry  where taskId = 1 and status = 'N/A';
select * from taskEntry;

select * from taskEntry where taskId in (select taskId from taskEntry where uploadId = 60) and taskId in (select taskId from chequeEntry where chequeId in ( select max(chequeId) from  chequeEntry group by taskId) and status = 'N/A');
select taskId from 
chequeEntry where 
status = 'N/A';
update uploadDetails set flag=1;
select * from chequeEntry;
select max(chequeId) from chequeEntry 
where taskId in (
select taskId from 
chequeEntry where 
status = 'D') group by taskId;

select * from taskEntry 
where taskId in (
select taskId from chequeEntry 
where chequeId in ( 
select max(chequeId) from  chequeEntry 
group by taskId)
and status = 'D');

select distinct marketer from uploadDetails order by marketer;

select * from taskEntry where entryDate >= '2017-08-01' and uploadId in (select uploadId from uploadDetails where marketer='john');