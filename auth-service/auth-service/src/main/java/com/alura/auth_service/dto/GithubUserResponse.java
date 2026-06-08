package com.alura.auth_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter @NoArgsConstructor
public class GithubUserResponse {

    private Long id;

    private String login;

    private String email;

    private String name;
}
