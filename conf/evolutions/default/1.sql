# --- !Ups


create table ADDRESS(address_id INT NOT NULL AUTO_INCREMENT, street VARCHAR(254), city VARCHAR(254) NOT NULL, state VARCHAR(254),PRIMARY KEY ( address_id ));
create table PERSON(person_id INT NOT NULL AUTO_INCREMENT, name VARCHAR(254), email VARCHAR(254) NOT NULL, AGE INT, sex VARCHAR(254), address_id INT ,PRIMARY KEY ( person_id ));

INSERT INTO ADDRESS(ADDRESS_ID, STREET, CITY, STATE) VALUES (10, "LANE 1", "NEW YORK", "VERGINIA");
INSERT INTO PERSON(PERSON_ID, NAME, EMAIL, AGE, SEX, ADDRESS_ID) VALUES(20,"SAM", "S@S.COM", 35, "MALE", 10);


