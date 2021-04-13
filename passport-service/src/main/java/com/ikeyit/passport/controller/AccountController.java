package com.ikeyit.passport.controller;


import com.ikeyit.passport.dto.*;
import com.ikeyit.passport.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 账号与安全相关
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/user")
    public UserDTO getUser() {
        return accountService.getUser();
    }

    @PostMapping("/security_check/verification/{way}")
    public void sendVerificationCodeForCheckSecurity(@PathVariable String way) {
        accountService.sendVerificationCodeForCheckSecurity(way);
    }

    @PostMapping("/security_check")
    public void checkSecurity(@RequestBody CheckSecurityParam checkSecurityParam) {
        accountService.checkSecurity(checkSecurityParam);
    }

    @PostMapping("/email/verification")
    public void sendVerificationCodeForUpdateEmail(@RequestBody SendEmailVerificationCodeParam sendEmailVerificationCodeParam) {
        accountService.sendVerificationCodeForUpdateEmail(sendEmailVerificationCodeParam);
    }

    @PostMapping("/email")
    public void updateEmail(@RequestBody UpdateEmailParam updateEmailParam) {
        accountService.updateEmail(updateEmailParam);
    }

    @PostMapping("/mobile/verification")
    public void sendVerificationCodeForUpdateMobile(@RequestBody SendMobileVerificationCodeParam sendMobileVerificationCodeParam) {
        accountService.sendVerificationCodeForUpdateMobile(sendMobileVerificationCodeParam);
    }

    @PostMapping("/mobile")
    public void updateMobile(@RequestBody UpdateMobileParam updateMobileParam) {
        accountService.updateMobile(updateMobileParam);
    }

    @PostMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordParam updatePasswordParam) {
        accountService.updatePassword(updatePasswordParam);
    }
}
