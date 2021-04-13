package com.ikeyit.passport.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.common.utils.PrivacyUtils;
import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.dto.*;
import com.ikeyit.passport.exception.PassportErrorCode;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.passport.service.impl.EmailVerificationCodeSender;
import com.ikeyit.passport.service.impl.SmsVerificationCodeSender;
import com.ikeyit.security.verification.VerificationCode;
import com.ikeyit.security.verification.VerificationCodeException;
import com.ikeyit.security.verification.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 账户与安全服务
 * @author wodead
 */
@Service
@Validated
public class AccountService {

    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VerificationCodeService verificationCodeService;

    @Autowired
    EmailVerificationCodeSender emailVerificationCodeSender;

    @Autowired
    SmsVerificationCodeSender smsVerificationCodeSender;


    /**
     * 当前用户
     * @return
     */
    public UserDTO getUser() {
        User user = getCurrentUser();
        return buildUserDTO(user);
    }


    private UserDTO buildUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        //脱敏
        userDTO.setMobile(PrivacyUtils.hidePrivacy(user.getMobile(), 4));
        userDTO.setEmail(PrivacyUtils.hideEmail(user.getEmail(), 4));
        return userDTO;
    }

    /**
     * 用户更新自己的手机号
     * @param updateMobileParam
     */
    public void updateMobile(@Valid UpdateMobileParam updateMobileParam) {
        String code = updateMobileParam.getCode();
        String mobile = updateMobileParam.getMobile();
        User user = getCurrentUser();
        if (mobile.equals(user.getMobile()))
            return;
        try {
            verificationCodeService.validate( "updateMobile." + mobile, code);
        } catch (VerificationCodeException e) {
            throw new BusinessException(PassportErrorCode.BAD_VERIFICATION_CODE);
        }

        User existingUser = userRepository.getByMobile(mobile);
        if (existingUser != null)
            throw new BusinessException(PassportErrorCode.MOBILE_EXISTS);

        user.setMobile(mobile);
        userRepository.update(user);
    }

    /**
     * 更新手机号时，发送验证码
     * @param sendMobileVerificationCodeParam
     */
    public void sendVerificationCodeForUpdateMobile(@Valid SendMobileVerificationCodeParam sendMobileVerificationCodeParam) {
        //确保是登录状态
        authenticationService.getCurrentUserId();
        String mobile = sendMobileVerificationCodeParam.getMobile();
        VerificationCode verificationCode = verificationCodeService.generateCode("updateMobile." + mobile);
        smsVerificationCodeSender.send(mobile, verificationCode, "updateMobile");
    }

    /**
     * 用户更新自己的密码
     * @param updatePasswordParam
     */
    public void updatePassword(UpdatePasswordParam updatePasswordParam) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(updatePasswordParam.getOldPassword(), user.getPassword()))
            throw new BusinessException(PassportErrorCode.WRONG_PASSWORD);

        user.setPassword(validateAndEncodePassword(user, updatePasswordParam.getNewPassword()));
        userRepository.update(user);
    }


    /**
     * 验证密码是否合法。
     * @param newPassword
     */
    private String validateAndEncodePassword(User user, String newPassword) {
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

    /**
     * 用户更新自己的email
     * @param updateEmailParam
     */
    public void updateEmail(@Valid UpdateEmailParam updateEmailParam) {
        User user = getCurrentUser();
        String email = updateEmailParam.getEmail();
        String code = updateEmailParam.getCode();
        try {
            verificationCodeService.validate("updateEmail." + email, code);
        } catch (VerificationCodeException e) {
            throw new BusinessException(PassportErrorCode.BAD_VERIFICATION_CODE);
        }
        user.setEmail(email);
        userRepository.update(user);
    }

    /**
     * 更新email，发送验证码
     * @param sendEmailVerificationCodeParam
     */
    public void sendVerificationCodeForUpdateEmail(@Valid SendEmailVerificationCodeParam sendEmailVerificationCodeParam) {
        //确保是登录状态
        authenticationService.getCurrentUserId();
        String email = sendEmailVerificationCodeParam.getEmail();
        VerificationCode verificationCode = verificationCodeService.generateCode("updateEmail." + email);
        emailVerificationCodeSender.send(email, verificationCode, "updateEmail");
    }

    /**
     * 通过验证码进行安全验证
     * @param way
     */
    public void sendVerificationCodeForCheckSecurity(String way) {
        User user = getCurrentUser();
        if ("email".equals(way)) {
            VerificationCode verificationCode = verificationCodeService.generateCode("checkSecurity.email." + user.getEmail());
            emailVerificationCodeSender.send(user.getEmail(), verificationCode, "checkSecurity");
        } else if ("mobile".equals(way)) {
            VerificationCode verificationCode = verificationCodeService.generateCode("checkSecurity.mobile." + user.getMobile());
            smsVerificationCodeSender.send(user.getEmail(), verificationCode, "checkSecurity");
        } else {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        }
    }

    /**
     * 验证客户端环境是否安全
     * @param checkSecurityParam
     */
    public void checkSecurity(@Valid CheckSecurityParam checkSecurityParam) {
        User user = getCurrentUser();
        try {
            if ("email".equals(checkSecurityParam.getWay())) {
                verificationCodeService.validate("checkSecurity.email." + user.getEmail(), checkSecurityParam.getCode());
            } else if ("mobile".equals(checkSecurityParam.getWay())) {
                verificationCodeService.validate("checkSecurity.mobile." + user.getMobile(), checkSecurityParam.getCode());
            } else {
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
            }
        } catch (VerificationCodeException e) {
            throw new BusinessException(PassportErrorCode.BAD_VERIFICATION_CODE);
        }
        //TODO 生成一个安全标志
        //两个方案
        // 1.生成一个有过期时间的安全token，保存到redis中，返回给客户端，进一步做高安全要求操作时客户端发送token给服务器
        // 2.直接redis里以jwt为key设置环境安全，进一步做高安全要求操作时，服务器直接通过jwt来判断是否安全

    }


    /**
     * 当前用户
     * @return
     */
    private User getCurrentUser() {
        Long userId = authenticationService.getCurrentUserId();
        if (userId == null)
            throw new BusinessException(PassportErrorCode.USER_NOT_FOUND);
        User user = userRepository.getById(userId);
        if (user == null)
            throw new BusinessException(PassportErrorCode.USER_NOT_FOUND);
        return user;
    }
}
