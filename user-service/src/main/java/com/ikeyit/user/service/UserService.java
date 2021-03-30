package com.ikeyit.user.service;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.user.domain.UserDetail;
import com.ikeyit.user.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserDetailRepository userRepository;



    public UserDetail getCurrentUser() {
        Long userId = authenticationService.getCurrentUserId();
       return userRepository.getById(userId);
    }

    public UserDetail getUser(Long id) {
        UserDetail userDetail = userRepository.getById(id);
        if (userDetail == null)
            throw new IllegalArgumentException("用户不存在");
        return userDetail;
    }


    public UserDetail updateUser(String nick, String avatar, String location, Integer sex) {
        Long userId = authenticationService.getCurrentUserId();
        UserDetail userDetail = userRepository.getById(userId);
        if (userDetail == null)
            throw new IllegalArgumentException("用户不存在");
        if (nick != null)
            userDetail.setNick(nick);
        if (avatar != null)
            userDetail.setAvatar(avatar);
        if (location != null)
            userDetail.setLocation(location);
        if (sex != null)
            userDetail.setSex(sex);

        return userDetail;
    }

    public Page<UserDetail> getUsers(PageParam pageParam) {

        return userRepository.getUsers(pageParam);
    }


}
