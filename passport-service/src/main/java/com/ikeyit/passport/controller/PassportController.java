package com.ikeyit.passport.controller;


import com.ikeyit.passport.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassportController {


    @Autowired
    private AccountService accountService;

    @PostMapping("/password/smscode")
    public void updatePasswordSendSmsCode() {
        accountService.updatePasswordSendSmsCode();
    }

    /**
     * 更新当前用户的密码，通过验证码
     * @param code
     * @param newPassword
     */
    @PostMapping(path="/password", params = {"code"})
    public void updatePasswordBySmsCode(String code, String newPassword) {
        accountService.updatePasswordBySmsCode(code, newPassword);
    }

    @PostMapping("/password")
    public void updatePassword(String oldPassword, String newPassword) {
        accountService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 更新当前用户的手机号
     * @param mobile
     * @param code
     */
    @PostMapping("/mobile")
    public void updateMobile(String mobile, String code) {
        accountService.updateMobile( mobile, code);
    }


}
