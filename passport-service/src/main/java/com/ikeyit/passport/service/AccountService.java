package com.ikeyit.passport.service;

import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.security.mobile.authentication.SmsCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 账户与安全服务
 */
@Service
public class AccountService {

    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    UserRepository userRepository;

    SmsCodeService smsCodeService;

    PasswordEncoder passwordEncoder;

    AuthenticationService authenticationService;

    @Autowired
    public AccountService(UserRepository userRepository, AuthenticationService authenticationService, SmsCodeService smsCodeService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.smsCodeService = smsCodeService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
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
     * @param oldPassword
     * @param newPassword
     */
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = authenticationService.getCurrentUserId();
        User user = getExistingUserById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new IllegalArgumentException("当前的密码输入不正确");

        user.setPassword(validatePassword(user, newPassword));
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
        if (newPassword == null)
            throw new IllegalArgumentException("密码不能为空");
        if (newPassword.length() < 6)
            throw new IllegalArgumentException("密码至少为6位");
        if (newPassword.contains(user.getLoginName()))
            throw new IllegalArgumentException("密码不能包含用户名");
        if (newPassword.contains(user.getMobile()))
            throw new IllegalArgumentException("密码不能包含手机号");
        if (passwordEncoder.matches(newPassword, user.getPassword()))
            throw new IllegalArgumentException("新密码不能登录旧密码");
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
}
