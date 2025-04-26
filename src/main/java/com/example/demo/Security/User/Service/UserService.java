package com.example.demo.Security.User.Service;

import com.example.demo.Security.User.Model.User;
import com.example.demo.Security.User.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public User create(String username, String email, String rawPassword, String role) {
        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        user.setRole(role);

        return userRepository.save(user);
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        return userRepository.findUserByUsernameAndPassword(username, password);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User updatePasswordByEmail(String email, String password) {
        User user = userRepository.findUserByEmail(email);

        user.setPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    public User disableUser(int id) {
        User user = userRepository.findUserById(id);

        user.setEnabled(false);

        return userRepository.save(user);
    }

    public User enableUser(int id) {
        User user = userRepository.findUserById(id);

        user.setEnabled(true);

        return userRepository.save(user);
    }

    public List<User> findAllAdmins() {
        return userRepository.findAllByRole("ADMIN");
    }

    public User findAdminByUsername(String username) {
        return userRepository.findUserByUsernameAndRole(username, "ADMIN");
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
