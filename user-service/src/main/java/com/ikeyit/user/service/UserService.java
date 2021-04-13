package com.ikeyit.user.service;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.utils.PrivacyUtils;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.user.domain.UserDetail;
import com.ikeyit.user.dto.UserDetailDTO;
import com.ikeyit.user.exception.UserErrorCode;
import com.ikeyit.user.repository.UserDetailRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserDetailRepository userRepository;



    public UserDetailDTO getCurrentUser() {
        Long userId = authenticationService.getCurrentUserId();
       return buildUserDetailDTO(userRepository.getById(userId));
    }

    public UserDetailDTO getUser(Long id) {
        UserDetail userDetail = userRepository.getById(id);
        if (userDetail == null)
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        return buildUserDetailDTO(userDetail);
    }


    public void updateUser(String nick, String avatar, String location, Integer sex) {
        Long userId = authenticationService.getCurrentUserId();
        UserDetail userDetail = userRepository.getById(userId);
        if (userDetail == null)
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        if (nick != null)
            userDetail.setNick(nick);
        if (avatar != null)
            userDetail.setAvatar(avatar);
        if (location != null)
            userDetail.setLocation(location);
        if (sex != null)
            userDetail.setSex(sex);
    }

    public Page<UserDetailDTO> getUsers(PageParam pageParam) {
        Page<UserDetail> userDetails = userRepository.getUsers(pageParam);
        return Page.map(userDetails, this::buildUserDetailDTO);
    }

    private UserDetailDTO buildUserDetailDTO(UserDetail userDetail) {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        BeanUtils.copyProperties(userDetail, userDetailDTO);
        //脱敏
        userDetailDTO.setMobile(PrivacyUtils.hidePrivacy(userDetail.getMobile(), 4));
        userDetailDTO.setEmail(PrivacyUtils.hideEmail(userDetail.getEmail(), 4));
        return userDetailDTO;
    }
}
