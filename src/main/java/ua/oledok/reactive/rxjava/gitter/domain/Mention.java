package ua.oledok.reactive.rxjava.gitter.domain;

import lombok.Data;

@Data
public class Mention {
    private final String screenName;
    private final String userId;
}
