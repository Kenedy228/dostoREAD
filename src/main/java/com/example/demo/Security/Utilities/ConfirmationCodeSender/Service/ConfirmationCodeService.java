package com.example.demo.Security.Utilities.ConfirmationCodeSender.Service;

import com.example.demo.Security.Utilities.ConfirmationCodeSender.Model.ConfirmationCode;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Repository.ConfirmationCodeRepository;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.CodeGenerator;
import com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.Sender;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ConfirmationCodeService {

    @Autowired
    private final ConfirmationCodeRepository codeRepository;
    @Autowired
    private final Sender sender;
    @Autowired
    private final CodeGenerator codeGenerator;

    @Transactional
    public void send(String email) {
        clearPreviousCodes(email);

        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setCode(codeGenerator.generate());
        confirmationCode.setEmail(email);

        confirmationCode = codeRepository.save(confirmationCode);

        sender.send(email, confirmationCode.getCode());
    }

    public boolean isInfoProvided(HttpSession session) {
        return session.getAttribute("email") != null && session.getAttribute("username") != null && session.getAttribute("rawPassword") != null;
    }

    private List<ConfirmationCode> clearPreviousCodes(String email) {
        return codeRepository.deleteConfirmationCodesByEmail(email);
    }

    public String getCodeByEmail(String email) {
        return codeRepository.findConfirmationCodeByEmail(email).getCode();
    }
}
