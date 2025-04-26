package com.example.demo.Security.User.Repository;

import com.example.demo.Security.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsernameAndPassword(String username, String password);
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    List<User> findAllByRole(String role);
    User findUserById(int id);
    User findUserByUsernameAndRole(String username, String role);
}
