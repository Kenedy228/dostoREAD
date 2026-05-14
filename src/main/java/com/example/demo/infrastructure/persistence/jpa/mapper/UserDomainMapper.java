package com.example.demo.infrastructure.persistence.jpa.mapper;

import com.example.demo.domain.user.model.User;
import com.example.demo.domain.user.valueobject.Email;
import com.example.demo.domain.user.valueobject.UserId;
import com.example.demo.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDomainMapper {
    public User toDomain(UserEntity entity) {
        Email email = entity.getEmail() == null ? null : new Email(entity.getEmail());
        return new User(
                new UserId(entity.getId()),
                entity.getUsername(),
                email,
                entity.getRole(),
                entity.isEnabled()
        );
    }
}
