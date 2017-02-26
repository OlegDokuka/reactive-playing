package ua.oledok.reactive.rxjava.gitter.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private final String id;
    private final String text;
    private final String html;
    private final Date sent;
    private final User fromUser;
    private final Boolean unread;
    private final Long readBy;
    private final String[] urls;
    private final Mention[] mentions;
    private final Issue[] issues;
    private final Long v;
    private final Object meta;
    private final String gv;
}
