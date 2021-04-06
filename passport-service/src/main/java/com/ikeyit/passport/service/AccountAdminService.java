package com.ikeyit.passport.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.domain.User;
import com.ikeyit.passport.dto.SetUserEnabledParam;
import com.ikeyit.passport.dto.UserDTO;
import com.ikeyit.passport.repository.PermissionRepository;
import com.ikeyit.passport.repository.RoleRepository;
import com.ikeyit.passport.repository.UserRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;


@Service
@Validated
public class AccountAdminService {

    private static Logger log = LoggerFactory.getLogger(AccountAdminService.class);

    @Autowired
    AuthenticationService authenticationService;

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
}
