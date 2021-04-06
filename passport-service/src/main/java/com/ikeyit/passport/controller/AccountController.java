package com.ikeyit.passport.controller;


import com.ikeyit.passport.dto.SendEmailVerificationParam;
import com.ikeyit.passport.dto.UpdateEmailParam;
import com.ikeyit.passport.dto.UpdatePasswordParam;
import com.ikeyit.passport.dto.ValidateVerificationCodeParam;
import com.ikeyit.passport.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {


    @Autowired
    private AccountService accountService;

    @PostMapping("/password/smscode")
    public void updatePasswordSendSmsCode() {
        accountService.updatePasswordSendSmsCode();
    }

    @PostMapping("/old_email/verification")
    public void sendUpdateEmailVerificationOld() {
        accountService.sendUpdateEmailVerificationOld();
    }

    @PostMapping("/old_email/verification/validate")
    public void validateUpdateEmailVerificationOld(@RequestBody ValidateVerificationCodeParam validateVerificationCodeParam) {
        accountService.validateUpdateEmailVerificationOld(validateVerificationCodeParam);
    }

    @PostMapping("/email/verification")
    public void sendEmailVerification(@RequestBody SendEmailVerificationParam sendEmailVerificationParam) {
        accountService.sendUpdateEmailVerificationNew(sendEmailVerificationParam);
    }

    @PostMapping("/email")
    public void updateEmail(@RequestBody UpdateEmailParam updateEmailParam) {
        accountService.updateEmail(updateEmailParam);
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
    public void updatePassword(@RequestBody UpdatePasswordParam updatePasswordParam) {
        accountService.updatePassword(updatePasswordParam);
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
