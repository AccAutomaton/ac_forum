create database ac_forum;

use ac_forum;

create table if not exists article
(
    id          int auto_increment
        primary key,
    owner       int                      not null,
    topic       int                      not null,
    title       varchar(64)   default '' not null,
    content     longtext                 not null,
    first_image varchar(1024) default '' not null,
    visits      int           default 0  not null,
    thumbs_up   int           default 0  not null,
    collections int           default 0  not null,
    tipping     int           default 0  not null,
    forwards    int           default 0  not null,
    create_time timestamp                not null,
    update_time timestamp                not null,
    delete_flag int           default 0  not null,
    constraint article_pk_2
        unique (id)
) auto_increment = 10000001;

create index article_owner_index
    on article (owner);

create index article_title_index
    on article (title);

create index article_topic_index
    on article (topic);

create table if not exists artist
(
    uid         int           not null
        primary key,
    collections int default 0 not null,
    follows     int default 0 not null,
    fans        int default 0 not null,
    articles    int default 0 not null,
    delete_flag int default 0 not null,
    constraint artist_pk_2
        unique (uid)
);

create table if not exists browse_record
(
    id           int auto_increment
        primary key,
    viewer       int           not null,
    type         int default 0 not null,
    be_viewed_id int           not null,
    time         timestamp     not null,
    delete_flag  int default 0 not null,
    constraint browse_record_pk_2
        unique (id)
) auto_increment = 10000001;

create index browse_record_be_viewed_id_index
    on browse_record (be_viewed_id);

create index browse_record_viewer_index
    on browse_record (viewer);

create table if not exists chat
(
    id                             int auto_increment
        primary key,
    participant1                   int           not null,
    participant2                   int           not null,
    participant1_not_read_messages int default 0 not null,
    participant2_not_read_messages int default 0 not null,
    update_time                    timestamp     not null,
    delete_flag                    int default 0 not null,
    constraint chat_pk_2
        unique (id)
) auto_increment = 10000001;

create index chat_participant1_index
    on chat (participant1);

create index chat_participant2_index
    on chat (participant2);

create table if not exists chat_message
(
    id          int auto_increment
        primary key,
    chat_id     int                      not null,
    type        int           default 0  not null,
    sender      int                      not null,
    content     varchar(1024) default '' not null,
    create_time timestamp                not null,
    delete_flag int           default 0  not null,
    constraint chat_message_pk_2
        unique (id)
) auto_increment = 10000001;

create index chat_message_chat_id_index
    on chat_message (chat_id);

create index chat_message_sender_index
    on chat_message (sender);

create table if not exists coin_record
(
    id           int auto_increment
        primary key,
    uid          int                     not null,
    type         int          default 0  not null,
    coin_volume  int          default 0  not null,
    coin_balance int          default 0  not null,
    project      varchar(256) default '' not null,
    comment      varchar(256) default '' not null,
    status       int          default 0  not null,
    create_time  timestamp               not null,
    update_time  timestamp               not null,
    delete_flag  int          default 0  not null,
    constraint coin_consumption_record_pk_2
        unique (id)
) auto_increment = 10000001;

create index coin_record_uid_index
    on coin_record (uid);

create table if not exists collection
(
    id              int auto_increment
        primary key,
    collector       int           not null,
    type            int default 0 not null,
    be_collected_id int           not null,
    time            timestamp     not null,
    constraint collection_pk_2
        unique (id)
) auto_increment = 10000001;

create index collection_be_collected_id_index
    on collection (be_collected_id);

create index collection_collector_index
    on collection (collector);

create table if not exists comment
(
    id             int auto_increment
        primary key,
    commenter      int                      not null,
    content        varchar(1024) default '' not null,
    target_article int                      not null,
    target_comment int                      null,
    thumbs_up      int           default 0  not null,
    create_time    timestamp                not null,
    delete_flag    int           default 0  not null,
    constraint comment_pk_2
        unique (id)
) auto_increment = 10000001;

create index comment_commenter_index
    on comment (commenter);

create index comment_target_article_index
    on comment (target_article);

create index comment_target_comment_index
    on comment (target_comment);

create table if not exists follow
(
    id          int auto_increment
        primary key,
    follower    int       not null,
    be_followed int       not null,
    time        timestamp not null,
    constraint follows_pk_2
        unique (id)
) auto_increment = 10000001;

create index follow_be_followed_index
    on follow (be_followed);

create index follow_follower_index
    on follow (follower);

create table if not exists message
(
    id          int auto_increment
        primary key,
    uid         int                      not null,
    title       varchar(64)              not null,
    type        int           default 0  not null,
    content     varchar(1024) default '' not null,
    target_url  varchar(1024) default '' not null,
    create_time timestamp                not null,
    seen        int           default 0  not null,
    delete_flag int           default 0  not null,
    constraint message_pk_2
        unique (id)
) auto_increment = 10000001;

create index message_title_index
    on message (title);

create index message_uid_index
    on message (uid);

create table if not exists point_record
(
    id            int auto_increment
        primary key,
    uid           int                     not null,
    type          int                     not null,
    point_volume  int          default 0  not null,
    point_balance int          default 0  not null,
    project       varchar(256) default '' not null,
    comment       varchar(256) default '' not null,
    status        int          default 0  not null,
    create_time   timestamp               not null,
    update_time   timestamp               not null,
    delete_flag   int          default 0  not null,
    constraint point_record_pk_2
        unique (id)
) auto_increment = 10000001;

create index point_record_uid_index
    on point_record (uid);

create table if not exists recharge
(
    id             int auto_increment
        primary key,
    uid            int                      not null,
    uuid           varchar(64)   default '' not null,
    trade_id       varchar(64)              null,
    channel        int           default 0  not null,
    type           int           default 0  not null,
    status         int           default 0  not null,
    amount         int           default 0  not null,
    subject        varchar(1024) default '' not null,
    comment        varchar(1024) default '' not null,
    coin_record_id int                      null,
    system_info    varchar(1024)            null,
    create_time    timestamp                not null,
    update_time    timestamp                not null,
    delete_flag    int           default 0  not null,
    constraint recharge_pk
        unique (uuid),
    constraint recharge_pk_2
        unique (id),
    constraint recharge_pk_3
        unique (trade_id)
) auto_increment = 10000001;

create index recharge_coin_record_id_index
    on recharge (coin_record_id);

create index recharge_trade_id_index
    on recharge (trade_id);

create index recharge_uid_index
    on recharge (uid);

create table if not exists sign_in
(
    id   int auto_increment
        primary key,
    uid  int       not null,
    time timestamp not null,
    constraint sign_in_pk_2
        unique (id)
) auto_increment = 10000001;

create index sign_in_uid_index
    on sign_in (uid);

create table if not exists thumbs_up
(
    id                int auto_increment
        primary key,
    thumbs_uper       int           not null,
    type              int default 0 not null,
    be_thumbs_uped_id int           not null,
    time              timestamp     not null,
    constraint thumbs_up_pk_2
        unique (id)
) auto_increment = 10000001;

create table if not exists topic
(
    id            int auto_increment
        primary key,
    title         varchar(32)   default '' not null,
    description   varchar(1024) default '' not null,
    administrator int                      not null,
    articles      int           default 0  not null,
    visits        int           default 0  not null,
    create_time   timestamp                not null,
    avatar        varchar(1024) default '' not null,
    delete_flag   int           default 0  not null,
    constraint topic_pk_2
        unique (id),
    constraint topic_pk_3
        unique (title)
) auto_increment = 10000001;

create index topic_administrator_index
    on topic (administrator);

create table if not exists user
(
    uid         int auto_increment
        primary key,
    username    varchar(64)              not null,
    password    varchar(256)             not null,
    email       varchar(64)              not null,
    status      int                      not null,
    user_type   int                      not null,
    nickname    varchar(64)              not null,
    avatar      varchar(1024) default '' not null,
    points      int           default 0  not null,
    coins       int           default 0  not null,
    create_time timestamp                not null,
    update_time timestamp                not null,
    delete_flag int           default 0  not null,
    constraint user_pk
        unique (username),
    constraint user_pk_2
        unique (uid),
    constraint user_pk_4
        unique (email)
) auto_increment = 10000001;

create index user_nickname_index
    on user (nickname);

create table if not exists vip
(
    uid             int           not null
        primary key,
    vip_type        int default 0 not null,
    expiration_time date          null,
    delete_flag     int default 0 not null,
    constraint vip_pk_2
        unique (uid)
);

