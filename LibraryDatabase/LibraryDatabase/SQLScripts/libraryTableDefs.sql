drop table Borrower CASCADE CONSTRAINTS;
drop table BorrowerType CASCADE CONSTRAINTS;
drop table Book CASCADE CONSTRAINTS;
drop table HasAuthor;
drop table HasSubject;
drop table BookCopy CASCADE CONSTRAINTS;
drop table HoldRequest;
drop table Borrowing CASCADE CONSTRAINTS;
drop table Fine;

drop sequence bid_sequence;
drop sequence hid_sequence;
drop sequence fid_sequence;
drop sequence borid_sequence;

CREATE SEQUENCE bid_sequence MINVALUE 0 START WITH 0 INCREMENT BY 1 CACHE 10;
CREATE SEQUENCE hid_sequence MINVALUE 0 START WITH 0 INCREMENT BY 1 CACHE 10;
CREATE SEQUENCE borid_sequence MINVALUE 0 START WITH 0 INCREMENT BY 1 CACHE 10;
CREATE SEQUENCE fid_sequence MINVALUE 0 START WITH 0 INCREMENT BY 1 CACHE 10;

create table BorrowerType
   (type varchar(20) not null,
   bookTimeLimit varchar(20) not null,
   primary key (type));
   
create table Borrower 
   (bid int not null,
   password char(16) not null, 
   name char(11) not null,
   address varchar(40) null,
   phone varchar(15) null,
   emailAddress varchar(40) null,
   sinOrStNo char(9) not null unique,
   expiryDate date not null,
   type varchar(20) not null,
   primary key (bid),
   foreign key (type) references BorrowerType(type) ON DELETE CASCADE);
   
create table Book 
   (callNumber char(20) not null primary key,
   isbn  varchar(13) not null unique,
   title varchar(30) not null,
   mainAuthor varchar(40) not null,
   publisher varchar(40) null,
   year int null);

create table HasAuthor
   (callNumber char(20) not null,
   name varchar(40) not null,
   primary key (callNumber, name),
   foreign key (callNumber) references Book(callNumber) ON DELETE CASCADE);

create table HasSubject
   (callNumber char(20) not null,
   subject varchar(40) not null,
   primary key (callNumber, subject),
   foreign key (callNumber) references Book(callNumber) ON DELETE CASCADE);

create table BookCopy
   (callNumber char(20) not null,
   copyNo char(4) not null,
   status varchar(7) not null,
   primary key(callNumber, copyNo),
   foreign key (callNumber) references Book(callNumber) ON DELETE CASCADE);

create table HoldRequest
   (hid int not null primary key,
   bid int not null,
   callNumber char(20) not null,
   issuedDate date not null,
   foreign key (bid) references Borrower(bid) ON DELETE CASCADE,
   foreign key (callNumber) references Book(callNumber) ON DELETE CASCADE);
         
create table Borrowing
   (borid int not null,
   bid int not null,
   callNumber char(20) not null,
   copyNo char(4) not null,
   outDate date not null,
   inDate date not null,
   primary key (borid),
   foreign key (bid) references Borrower(bid) ON DELETE CASCADE,
   foreign key (callNumber, copyNo) references BookCopy(callNumber, copyNo) ON DELETE CASCADE);
                       
create table Fine
   (fid int not null primary key,
   amount float not null,
   issuedDate date not null,
   paidDate date null,
   borid int not null unique,
   foreign key (borid) references Borrowing(borid) ON DELETE CASCADE);

commit;
