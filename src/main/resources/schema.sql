create table IF NOT EXISTS MPA
(
    ID   INTEGER           not null,
    RATING CHARACTER VARYING not null,
    constraint "MPA_pk"
        primary key (ID)
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment,
    NAME     CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null
        unique,
    EMAIL    CHARACTER VARYING not null
        unique,
    BIRTHDAY DATE              not null,
    constraint "USERS_pk"
        primary key (USER_ID)
);

create table IF NOT EXISTS GENRE
(
    ID    INTEGER           not null,
    GENRE CHARACTER VARYING not null,
    constraint "GENRE_pk"
        primary key (ID)
);

create table IF NOT EXISTS FILM
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING not null,
    RELEASE_DATE DATE              not null,
    DURATION     INTEGER           not null,
    RATE         INTEGER default 0,
    RATING_ID    INTEGER auto_increment
        unique,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILM_MPA_RATING_ID_FK
        foreign key (RATING_ID) references MPA
);

create table IF NOT EXISTS GENRE_FILM
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint "GENRE_FILM_pk"
        primary key (FILM_ID, GENRE_ID),
    constraint "GENRE_FILM_FILM_FILM_ID_fk"
        foreign key (FILM_ID) references FILM (FILM_ID),
    constraint "GENRE_FILM_GENRE_ID_fk"
        foreign key (GENRE_ID) references GENRE (ID)
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint "LIKES_pk"
        primary key (FILM_ID, USER_ID),
    constraint LIKES_FILM_FILM_ID_FK
        foreign key (FILM_ID) references FILM (FILM_ID),
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS (USER_ID)
);

create table IF NOT EXISTS FRIENDSHIP
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint "FRIENDSHIP_pk"
        primary key (USER_ID, FRIEND_ID)
);