package com.ikeyit.passport.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.dto.SetUserEnabledParam;
import com.ikeyit.passport.dto.SetUserMobileParam;
import com.ikeyit.passport.dto.SetUserPasswordParam;
import com.ikeyit.passport.dto.UserDTO;
import com.ikeyit.passport.exception.PassportErrorCode;
import com.ikeyit.passport.repository.PermissionRepository;
import com.ikeyit.passport.repository.RoleRepository;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 需要由超级用户权限
 * 平台管理->账户管理
 */
@Service
@Validated
public class AccountAdminService {

    private static Logger log = LoggerFactory.getLogger(AccountAdminService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * 查询用户
     * @param id
     * @param loginName
     * @param mobile
     * @param email
     * @param pageParam
     * @return
     */
    public Page<UserDTO> getUsers(Long id, String loginName, String mobile, String email, PageParam pageParam) {
        authenticationService.requireAuthority("r_super");
        Page<User> users = userRepository.getAll(id, loginName, mobile, email, pageParam);
        return Page.map(users, user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setLoginName(user.getLoginName());
            userDTO.setEmail(user.getEmail());
            userDTO.setMobile(user.getMobile());
            userDTO.setEnabled(user.isEnabled());
            userDTO.setVerified(user.isVerified());
            return userDTO;
        });
    }

    /**
     * 设置用户冻结状态
     * @param setUserEnabledParam
     */
    @Transactional
    public void setUserEnabled(@Valid SetUserEnabledParam setUserEnabledParam) {
        authenticationService.requireAuthority("r_super");
        setUserEnabledParam.getUserIds().stream().forEach(userId -> {
            userRepository.updateEnabled(userId, setUserEnabledParam.getEnabled());
        });
    }

    /**
     * 后台设置用户手机号
     * @param setUserMobileParam
     */
    public void setUserMobile(@Valid SetUserMobileParam setUserMobileParam) {
        authenticationService.requireAuthority("r_super");
        Long userId = setUserMobileParam.getUserId();
        String mobile = setUserMobileParam.getMobile();
        User user = getExistingUserById(userId);
        if (mobile.equals(user.getMobile()))
            return;
        User existingUser = userRepository.getByMobile(mobile);
        if (existingUser != null)
            throw new BusinessException(PassportErrorCode.MOBILE_EXISTS);
        user.setMobile(mobile);
        userRepository.update(user);
    }

    /**
     * 设置用户密码
     *
     * @param setUserPasswordParam
     */
    public void setUserPassword(@Valid SetUserPasswordParam setUserPasswordParam) {
        authenticationService.requireAuthority("r_super");
        User user = getExistingUserById(setUserPasswordParam.getUserId());
        user.setPassword(passwordEncoder.encode(setUserPasswordParam.getPassword()));
        userRepository.update(user);
    }


    /**
     * 获得已经存在的用户，如果不存在则抛异常
     * @param userId
     * @return
     */
    private User getExistingUserById(Long userId) {
        if (userId == null)
            throw new BusinessException(PassportErrorCode.USER_NOT_FOUND);
        User user = userRepository.getById(userId);
        if (user == null)
            throw new BusinessException(PassportErrorCode.USER_NOT_FOUND);
        return user;
    }
}
