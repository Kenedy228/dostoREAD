package com.example.demo.application.mail.service;

import com.example.demo.infrastructure.persistence.jpa.entity.ConfirmationCodeEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.ConfirmationCodeJpaRepository;
import com.example.demo.application.mail.service.CodeGenerator;
import com.example.demo.infrastructure.mail.MailSenderService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ConfirmationCodeApplicationService {

    @Autowired
    private final ConfirmationCodeJpaRepository codeRepository;
    @Autowired
    private final MailSenderService sender;
    @Autowired
    private final CodeGenerator codeGenerator;

    @Transactional
    public void send(String email) {
        clearPreviousCodes(email);

        ConfirmationCodeEntity confirmationCode = new ConfirmationCodeEntity();
        confirmationCode.setCode(codeGenerator.generate());
        confirmationCode.setEmail(email);

        confirmationCode = codeRepository.save(confirmationCode);

        sender.send(email, confirmationCode.getCode());
    }

    public boolean isInfoProvided(HttpSession session) {
        return session.getAttribute("email") != null && session.getAttribute("username") != null && session.getAttribute("rawPassword") != null;
    }

    private List<ConfirmationCodeEntity> clearPreviousCodes(String email) {
        return codeRepository.deleteConfirmationCodesByEmail(email);
    }

    public String getCodeByEmail(String email) {
        return codeRepository.findConfirmationCodeByEmail(email).getCode();
    }
}
