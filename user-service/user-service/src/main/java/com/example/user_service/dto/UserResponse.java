package com.example.user_service.dto;

import com.example.user_service.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter @NoArgsConstructor
public class UserResponse {

    private Long id;
    private String nome;
    private String email;

    public static UserResponse de(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNome(user.getNome());
        response.setEmail(user.getEmail());
        return response;
    }
}
