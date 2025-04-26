package com.example.demo.Security.Utilities.Login.Controller;

import com.example.demo.Security.User.Utilities.UserDetails.Model.CustomUserDetails;
import com.example.demo.Security.User.Utilities.UserDetails.Service.CustomUserDetailsService;
import com.example.demo.Security.Utilities.Login.Model.Login;
import com.example.demo.Security.Utilities.Login.Utilities.Validator.LoginValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Controller
public class LoginController {
    @Autowired
    private final LoginValidator loginValidator;
    @Autowired
    private final AuthenticationManager manager;

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("login", new Login());
        return "security/login/login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute Login login, Model model, BindingResult bindingResult, HttpSession session) {
        loginValidator.validate(login, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("login", login);
            return "security/login/login";
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getRawPassword());
        Authentication authentication = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "redirect:/";
    }
}
