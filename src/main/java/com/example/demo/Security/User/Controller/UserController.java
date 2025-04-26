package com.example.demo.Security.User.Controller;

import com.example.demo.Security.User.DTO.UserCreateDTO;
import com.example.demo.Security.User.Model.User;
import com.example.demo.Security.User.Service.UserService;
import com.example.demo.Security.User.Utilities.CreateUser.Validator.CreateUserValidator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final CreateUserValidator userValidator;

    @GetMapping("/admin-panel/accounts/view")
    public String getAllAccounts(Model model) {
        List<User> admins = userService.findAllAdmins();

        model.addAttribute("accounts", admins);

        return "admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/disable")
    public String disableAccount(@RequestParam int userID) {
        User user = userService.disableUser(userID);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (user.getUsername().equals(username)) {
            return "redirect:/logout";
        }

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/enable")
    public String enableAccount(@RequestParam int userID) {
        userService.enableUser(userID);

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel/accounts/create")
    public String getCreateForm(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());

        return "admin-panel/accounts/create";
    }

    @PostMapping("/admin-panel/accounts/create")
    public String createAdmin(@ModelAttribute UserCreateDTO userCreateDTO, Model model, BindingResult bindingResult) {
        userValidator.validate(userCreateDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDTO", userCreateDTO);
            return "admin-panel/accounts/create";
        }

        userService.create(userCreateDTO.getUsername(), null, userCreateDTO.getRawPassword(), "ADMIN");

        return "redirect:/admin-panel/accounts/view";
    }

    @GetMapping("/admin-panel")
    public String adminPanel() {
        return "admin-panel/adminPanel";
    }
}
