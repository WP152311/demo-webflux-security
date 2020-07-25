drop table if exists article_content;

drop table if exists article;

drop table if exists user_vo;


create table user_vo (id VARCHAR(255) PRIMARY KEY, password VARCHAR(255), name VARCHAR(255),authority varchar(10));

create table article (number INT IDENTITY PRIMARY KEY , writer_id VARCHAR(255), title VARCHAR(255), reg_date DATE, read_count INT, foreign key(writer_id) references user_vo(id) ON DELETE NO ACTION );

create table article_content (number INT PRIMARY KEY, content varchar(255), FOREIGN KEY (number) REFERENCES article(number) ON DELETE CASCADE );

-- create table article (num INT IDENTITY PRIMARY KEY , title VARCHAR(255), reg_date DATE, read_count INT );
--
-- create table article_content (num INT PRIMARY KEY, content varchar(255));
