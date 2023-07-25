DROP TABLE IF EXISTS Employee;

create table Employee (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	user_name varchar(100) not null,
	birth_date DATE not null,
	country varchar(100) not null,
	phone_number varchar(10),
	gender ENUM('MALE', 'FEMALE'),
	UNIQUE (user_name)
);