package com.example.demo.Security.Utilities.ConfirmationCodeSender.Repository;

import com.example.demo.Security.Utilities.ConfirmationCodeSender.Model.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Integer> {
    ConfirmationCode findConfirmationCodeByEmail(String email);
    List<ConfirmationCode> deleteConfirmationCodesByEmail(String email);
}
