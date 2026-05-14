package com.example.demo.presentation.web.api;

import com.example.demo.application.mail.service.ConfirmationCodeApplicationService;
import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.api.dto.ApiResponse;
import com.example.demo.presentation.web.api.dto.CodeRequest;
import com.example.demo.presentation.web.api.dto.LoginRequest;
import com.example.demo.presentation.web.api.dto.PasswordRecoveryRequest;
import com.example.demo.presentation.web.api.dto.RegisterRequest;
import com.example.demo.presentation.web.api.dto.SessionResponse;
import com.example.demo.presentation.web.form.ConfirmationCodeForm;
import com.example.demo.presentation.web.form.LoginForm;
import com.example.demo.presentation.web.form.PasswordRecoveryForm;
import com.example.demo.presentation.web.form.RegistrationForm;
import com.example.demo.presentation.web.validator.ConfirmationCodeValidator;
import com.example.demo.presentation.web.validator.LoginValidator;
import com.example.demo.presentation.web.validator.PasswordRecoveryEmailValidator;
import com.example.demo.presentation.web.validator.PasswordRecoveryPasswordValidator;
import com.example.demo.presentation.web.validator.RegistrationValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthApiController {
    private final LoginValidator loginValidator;
    private final RegistrationValidator registrationValidator;
    private final PasswordRecoveryEmailValidator recoveryEmailValidator;
    private final PasswordRecoveryPasswordValidator recoveryPasswordValidator;
    private final ConfirmationCodeValidator codeValidator;
    private final ConfirmationCodeApplicationService confirmationCodeService;
    private final UserApplicationService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/session")
    public SessionResponse session(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new SessionResponse(false, null, null);
        }

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse(null);

        return new SessionResponse(true, authentication.getName(), role);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionResponse>> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginForm form = new LoginForm();
        form.setUsername(request.username());
        form.setRawPassword(request.rawPassword());

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "login");
        loginValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(form.getUsername(), form.getRawPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse(null);

        return ResponseEntity.ok(ApiResponse.ok(new SessionResponse(true, authentication.getName(), role)));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();

        return ApiResponse.ok(null);
    }

    @PostMapping("/registration/start")
    public ResponseEntity<ApiResponse<Void>> startRegistration(@RequestBody RegisterRequest request, HttpSession session) {
        RegistrationForm form = new RegistrationForm();
        form.setUsername(request.username());
        form.setEmail(request.email());
        form.setRawPassword(request.rawPassword());
        form.setRawPasswordConfirmation(request.rawPasswordConfirmation());

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registration");
        registrationValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        session.setAttribute("email", form.getEmail());
        session.setAttribute("username", form.getUsername());
        session.setAttribute("rawPassword", form.getRawPassword());
        confirmationCodeService.send(form.getEmail());

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/registration/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmRegistration(@RequestBody CodeRequest request, HttpSession session) {
        if (!confirmationCodeService.isInfoProvided(session)) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(java.util.List.of("Сначала заполните форму регистрации")));
        }

        ConfirmationCodeForm form = new ConfirmationCodeForm();
        form.setEnteredCode(request.enteredCode());
        form.setExpectedCode(confirmationCodeService.getCodeByEmail(session.getAttribute("email").toString()));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "confirmationCode");
        codeValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        userService.create(
                session.getAttribute("username").toString(),
                session.getAttribute("email").toString(),
                session.getAttribute("rawPassword").toString(),
                "USER"
        );
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/password-recovery/start")
    public ResponseEntity<ApiResponse<Void>> startPasswordRecovery(@RequestBody PasswordRecoveryRequest request, HttpSession session) {
        PasswordRecoveryForm form = new PasswordRecoveryForm();
        form.setEmail(request.email());

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "passwordRecovery");
        recoveryEmailValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        session.setAttribute("reset-email", form.getEmail());
        confirmationCodeService.send(form.getEmail());

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/password-recovery/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPasswordRecovery(@RequestBody CodeRequest request, HttpSession session) {
        if (session.getAttribute("reset-email") == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(java.util.List.of("Сначала укажите email")));
        }

        ConfirmationCodeForm form = new ConfirmationCodeForm();
        form.setEnteredCode(request.enteredCode());
        form.setExpectedCode(confirmationCodeService.getCodeByEmail(session.getAttribute("reset-email").toString()));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "confirmationCode");
        codeValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        session.setAttribute("code", true);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/password-recovery/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody PasswordRecoveryRequest request, HttpSession session) {
        if (session.getAttribute("reset-email") == null || session.getAttribute("code") == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(java.util.List.of("Сначала подтвердите email")));
        }

        PasswordRecoveryForm form = new PasswordRecoveryForm();
        form.setRawPassword(request.rawPassword());
        form.setRawPasswordConfirmation(request.rawPasswordConfirmation());

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "passwordRecovery");
        recoveryPasswordValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        userService.updatePasswordByEmail(session.getAttribute("reset-email").toString(), form.getRawPassword());
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
