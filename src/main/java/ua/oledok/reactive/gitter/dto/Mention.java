package ua.oledok.reactive.gitter.dto;

import lombok.Data;

@Data
public class Mention {
    private final String screenName;
    private final String userId;
}
