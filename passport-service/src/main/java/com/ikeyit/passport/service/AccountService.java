package com.ikeyit.passport.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.dto.SendEmailVerificationParam;
import com.ikeyit.passport.dto.UpdateEmailParam;
import com.ikeyit.passport.dto.UpdatePasswordParam;
import com.ikeyit.passport.dto.ValidateVerificationCodeParam;
import com.ikeyit.passport.exception.PassportErrorCode;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.passport.service.impl.EmailVerificationCodeSender;
import com.ikeyit.security.mobile.authentication.SmsCodeService;
import com.ikeyit.security.verification.VerificationCode;
import com.ikeyit.security.verification.VerificationCodeException;
import com.ikeyit.security.verification.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 账户与安全服务
 */
@Service
@Validated
public class AccountService {

    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    UserRepository userRepository;

    SmsCodeService smsCodeService;

    PasswordEncoder passwordEncoder;

    AuthenticationService authenticationService;

    VerificationCodeService verificationCodeService;

    EmailVerificationCodeSender emailVerificationCodeSender;

    @Autowired
    public AccountService(UserRepository userRepository, SmsCodeService smsCodeService, PasswordEncoder passwordEncoder, AuthenticationService authenticationService, VerificationCodeService verificationCodeService, EmailVerificationCodeSender emailVerificationCodeSender) {
        this.userRepository = userRepository;
        this.smsCodeService = smsCodeService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.verificationCodeService = verificationCodeService;
        this.emailVerificationCodeSender = emailVerificationCodeSender;
    }



    /**
     * 通过验证码设置新的手机号
     * @param mobile
     * @param code
     */
    public void updateMobile(String mobile, String code) {
        smsCodeService.validate(mobile, code);
        updateMobile(mobile);
    }

    /**
     * 设置新的手机号
     * 需管理员权限
     * @param mobile
     */
    public void updateMobile(String mobile) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);

        if (mobile.equals(user.getMobile()))
            return;
        User existingUser = userRepository.getByMobile(mobile);
        if (existingUser != null)
            throw new UsernameNotFoundException("该手机号已经被绑定到其它用户，请更换");
        user.setMobile(mobile);
        userRepository.update(user);
    }


    /**
     * 通过验证码来设置新的用户密码
     * 用户自己调用
     *
     * @param code
     * @param newPassword
     */
    public void updatePasswordBySmsCode(String code, String newPassword){
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        String mobile = user.getMobile();
        if (mobile == null)
            throw new UsernameNotFoundException("用户没有设定手机号");
        smsCodeService.validate(mobile, code);
        user.setPassword(validatePassword(user, newPassword));
        userRepository.update(user);
    }


    /**
     * 设置用户的密码
     * @param updatePasswordParam
     */
    public void updatePassword(UpdatePasswordParam updatePasswordParam) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        if (!passwordEncoder.matches(updatePasswordParam.getOldPassword(), user.getPassword()))
            throw new BusinessException(PassportErrorCode.WRONG_PASSWORD);

        user.setPassword(validatePassword(user, updatePasswordParam.getNewPassword()));
        userRepository.update(user);
    }

    /**
     * 设置用户的密码
     * 管理员调用
     * @param newPassword
     */
    public void updatePassword(String newPassword) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);

        user.setPassword(validatePassword(user, newPassword));
        userRepository.update(user);
    }




    /**
     * 验证密码是否合法。
     * @param newPassword
     */
    private String validatePassword(User user, String newPassword) {
        //TODO 实现更严格的验证
        if (newPassword == null
                || newPassword.length() < 6
                || newPassword.contains(user.getLoginName())
                || newPassword.contains(user.getMobile())
                || passwordEncoder.matches(newPassword, user.getPassword())
        )
            throw new BusinessException(PassportErrorCode.BAD_FORMAT_PASSWORD);
        return passwordEncoder.encode(newPassword);
    }


    public User getExistingUserById(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null)
            throw new UsernameNotFoundException("用户不存在");
        return user;
    }

    public void updatePasswordSendSmsCode() {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        smsCodeService.sendCode(user.getMobile());
    }

    public void sendUpdateEmailVerificationOld() {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        VerificationCode verificationCode = verificationCodeService.generateCode("UpdateEmailVerificationOld." + user.getEmail());
        emailVerificationCodeSender.send(user.getEmail(), verificationCode, null);
    }

    public void validateUpdateEmailVerificationOld(@Valid ValidateVerificationCodeParam validateVerificationCodeParam) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        try {
            verificationCodeService.validate("UpdateEmailVerificationOld." + user.getEmail(), validateVerificationCodeParam.getCode());
        } catch (VerificationCodeException e) {
            throw new BusinessException(PassportErrorCode.BAD_VERIFICATION_CODE);
        }
    }


    public void sendUpdateEmailVerificationNew(@Valid SendEmailVerificationParam sendEmailVerificationParam) {
        Long userId = authenticationService.getCurrentUserId();
        String email = sendEmailVerificationParam.getEmail();

        VerificationCode verificationCode = verificationCodeService.generateCode("UpdateEmailVerificationNew." + email);
        emailVerificationCodeSender.send(email, verificationCode, null);
    }

    public void updateEmail(@Valid UpdateEmailParam updateEmailParam) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        String email = updateEmailParam.getEmail();
        String code = updateEmailParam.getCode();
        try {
            verificationCodeService.validate("UpdateEmailVerificationNew." + email, code);
        } catch (VerificationCodeException e) {
            throw new BusinessException(PassportErrorCode.BAD_VERIFICATION_CODE);
        }
        user.setEmail(email);
        userRepository.update(user);
    }
}
