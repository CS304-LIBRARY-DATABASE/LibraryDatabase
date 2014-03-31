insert into BorrowerType values('borrower', '2');
insert into BorrowerType values('faculty', '12');
insert into BorrowerType values('staff', '6');
insert into BorrowerType values('public', '2');

insert into Borrower values (bid_sequence.nextval, 'pw', 'Jon', '10th', '6041234567', 'jon@jon.com', '123', to_date('2018/05/31', 'yyyy/mm/dd'), 'borrower');
insert into Borrower values (bid_sequence.nextval, 'pw', 'Amy', '12th', '7781234567', 'amy@amy.com', '456', to_date('2018/05/31', 'yyyy/mm/dd'), 'borrower');
insert into Borrower values (bid_sequence.nextval, 'pw', 'Josh', '33rd', '778333333', 'josh@josh.com', '789', to_date('2018/05/31', 'yyyy/mm/dd'), 'borrower');

insert into Book values ('1 1 1111', '1111111111', 'Db Design', 'Laks', 'UBC', 1995);
insert into Book values ('2 2 2222', '2222222222', 'First Nations Studies', 'Laks', 'UBC', 1995);
insert into Book values ('3 3 3333', '3333333333', 'Art History', 'Allen', 'UBC', 1998);

insert into HasAuthor values ('1 1 1111', 'Allen Hu');
insert into HasAuthor values ('1 1 1111', 'Donald Acton');
insert into HasAuthor values ('2 2 2222', 'Smith');

insert into HasSubject values ('1 1 1111', 'Computer Science');
insert into HasSubject values ('1 1 1111', 'Database');
insert into HasSubject values ('3 3 3333', 'Art');
insert into HasSubject values ('3 3 3333', 'History');

insert into BookCopy values ('1 1 1111', 'C1', 'in');
insert into BookCopy values ('1 1 1111', 'C2', 'in');
insert into BookCopy values ('2 2 2222', 'C1', 'in');
insert into BookCopy values ('2 2 2222', 'C2', 'out');
insert into BookCopy values ('3 3 3333', 'C1', 'out');
insert into BookCopy values ('3 3 3333', 'C2', 'out');
insert into BookCopy values ('3 3 3333', 'C3', 'out');

insert into HoldRequest values (hid_sequence.nextval, 1, '3 3 3333', sysdate);
insert into HoldRequest values (hid_sequence.nextval, 2, '3 3 3333', sysdate);           
           
insert into Borrowing values(borid_sequence.nextval, 1, '2 2 2222', 'C2', to_date('2014/02/21','yyyy/mm/dd'), to_date('2014/03/11','yyyy/mm/dd'));
insert into Borrowing values(borid_sequence.nextval, 1, '3 3 3333', 'C1', to_date('2014/02/21','yyyy/mm/dd'), to_date('2014/03/11','yyyy/mm/dd'));
insert into Borrowing values(borid_sequence.nextval, 1, '3 3 3333', 'C3', to_date('2014/03/21','yyyy/mm/dd'), to_date('2014/04/11','yyyy/mm/dd'));
insert into Borrowing values(borid_sequence.nextval, 3, '3 3 3333', 'C2', to_date('2014/02/21','yyyy/mm/dd'), to_date('2014/03/11','yyyy/mm/dd'));

insert into Fine values (fid_sequence.nextval, 9, sysdate, null, 1);
insert into Fine values (fid_sequence.nextval, 14, sysdate, null, 2);
insert into Fine values (fid_sequence.nextval, 7, sysdate, null, 4);

commit;
