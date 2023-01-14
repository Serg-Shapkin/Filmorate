
create table IF NOT EXISTS MPA
(
    ID     INTEGER not null unique,
    RATING CHARACTER VARYING not null,
    constraint "MPA_pk"
        primary key (ID)
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER           not null,
    NAME     CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null
        unique,
    EMAIL    CHARACTER VARYING not null
        unique,
    BIRTHDAY DATE,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS GENRE
(
    ID    INTEGER           not null,
    GENRE CHARACTER VARYING not null,
    constraint GENRES_PK
        primary key (ID)
);

create table IF NOT EXISTS FILM
(
    FILM_ID      INTEGER           not null,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING,
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    RATING_ID    INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILM_MPA_RATING_ID_FK
        foreign key (RATING_ID) references MPA (ID)
);

create table IF NOT EXISTS GENRE_FILM (
    FILM_ID INTEGER REFERENCES FILM(FILM_ID),
    GENRE_ID INTEGER REFERENCES GENRE(ID),

    PRIMARY KEY (FILM_ID, GENRE_ID)
);

create table IF NOT EXISTS LIKES (
    FILM_ID INTEGER REFERENCES FILM(FILM_ID),
    USER_ID INTEGER REFERENCES USERS(USER_ID),

    PRIMARY KEY (FILM_ID, USER_ID)
);

create table IF NOT EXISTS FRIENDSHIP (
    USER_ID   INTEGER not null REFERENCES USERS,
    FRIEND_ID INTEGER not null REFERENCES USERS,

    PRIMARY KEY (USER_ID, FRIEND_ID)
);


