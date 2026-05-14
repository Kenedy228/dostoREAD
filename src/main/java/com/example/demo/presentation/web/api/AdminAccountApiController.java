package com.example.demo.presentation.web.api;

import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.presentation.web.api.dto.ApiResponse;
import com.example.demo.presentation.web.form.UserCreateForm;
import com.example.demo.presentation.web.validator.CreateUserValidator;
import com.example.demo.presentation.web.viewmodel.AccountViewModel;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@AllArgsConstructor
public class AdminAccountApiController {
    private final UserApplicationService userService;
    private final CreateUserValidator userValidator;

    @GetMapping
    public ApiResponse<List<AccountViewModel>> accounts() {
        return ApiResponse.ok(userService.findAdminAccounts());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@ModelAttribute UserCreateForm form) {
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "user");
        userValidator.validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ApiValidation.errors(bindingResult)));
        }

        userService.create(form.getUsername(), null, form.getRawPassword(), "ADMIN");
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PatchMapping("/disable")
    public ApiResponse<AccountViewModel> disable(@RequestParam int userId) {
        return ApiResponse.ok(userService.disableAccount(userId));
    }

    @PatchMapping("/enable")
    public ApiResponse<AccountViewModel> enable(@RequestParam int userId) {
        return ApiResponse.ok(userService.enableAccount(userId));
    }
}
