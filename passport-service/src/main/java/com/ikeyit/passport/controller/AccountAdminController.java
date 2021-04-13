package com.ikeyit.passport.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.dto.SetUserEnabledParam;
import com.ikeyit.passport.dto.UserDTO;
import com.ikeyit.passport.service.AccountAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 超级用户对用户进行管理
 */
@RestController
@RequestMapping("/super")
public class AccountAdminController {

    @Autowired
    AccountAdminService accountAdminService;

    @GetMapping("/users")
    public Page<UserDTO> getUsers(Long id, String loginName, String mobile, String email, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return accountAdminService.getUsers(id, loginName, mobile, email, new PageParam(page, pageSize));
    }

    @PutMapping("/users/enabled")
    public void setUserEnabled(@RequestBody SetUserEnabledParam setUserEnabledParam) {
        accountAdminService.setUserEnabled(setUserEnabledParam);
    }
}


