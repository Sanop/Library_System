drop database if exists Library;
create database Library;

use Library;


create table borrowers(
    borrower_ID varchar(100)primary key,
    borrower_Name varchar(100)not null,
    borrower_Address varchar(100)not null,
    borrower_NIC varchar(20)not null,
    borrower_Contact varchar(20)not null
);

create table books(
    isbn varchar(100)primary key,
    book_Name varchar(100)not null,
    author varchar(100)not null,
    price decimal(10,2)not null,
    status varchar(100)
);

create table lendDetails(
    borrower_ID varchar(100)not null,
    isbn varchar(100)not null,
    date_Of_Lend varchar(100)not null,
    date_Of_Return varchar(100)not null,
    status varchar(100),
    constraint primary key(borrower_ID,isbn,date_Of_Lend),
    constraint foreign key(borrower_ID)references borrowers(borrower_ID),
    constraint foreign key(isbn)references books(isbn)
);

create table users(
    user_Name varchar(100)not null,
    email varchar(100)not null,
    password varchar(100)not null
);

create table returnDetails(
    isbn varchar(100)not null,
    borrower_ID varchar(100)not null,
    date_Of_Lend varchar(100)not null,
    date_Of_Return varchar(100)not null,
    late_Payment varchar(100),
    real_Date_Of_Return varchar(100),
    constraint primary key(isbn,borrower_ID,date_Of_Lend),
    constraint foreign key(isbn)references books(isbn),
    constraint foreign key(borrower_ID)references borrowers(borrower_ID)
);
