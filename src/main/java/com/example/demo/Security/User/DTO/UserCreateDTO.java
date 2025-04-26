package com.example.demo.Security.User.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreateDTO {
    private String username;
    private String rawPassword;
}
