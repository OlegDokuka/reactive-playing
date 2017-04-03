package ua.oledok.reactive.gitter.dto;

import lombok.Data;

@Data
public class User {
    private final String id;
    private final String username;
    private final String displayName;
    private final String url;
    private final String avatarUrl;
    private final String avatarUrlSmall;
    private final String avatarUrlMedium;
    private final Long v;
    private final String gv;
}
