# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        bigint not null,
  username                  varchar(255),
  gender                    varchar(255),
  name                      varchar(255),
  birthday                  timestamp,
  email                     varchar(255),
  created_at                timestamp,
  fb_id                     bigint,
  constraint pk_user primary key (id))
;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_seq;

