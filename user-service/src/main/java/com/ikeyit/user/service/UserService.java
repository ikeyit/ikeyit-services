package com.ikeyit.user.service;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.user.domain.UserDetail;
import com.ikeyit.user.dto.UserDetailDTO;
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
            throw new IllegalArgumentException("用户不存在");
        return buildUserDetailDTO(userDetail);
    }


    public void updateUser(String nick, String avatar, String location, Integer sex) {
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
    }

    public Page<UserDetailDTO> getUsers(PageParam pageParam) {
        Page<UserDetail> userDetails = userRepository.getUsers(pageParam);
        return Page.map(userDetails, this::buildUserDetailDTO);
    }

    /**
     * 脱敏，隐藏个人信息
     * @param text 带脱敏的文字
     * @param size 被*替代的长度
     * @return
     */
    private String hidePrivacy(String text, int size) {
        if (text == null || text.length() == 0)
            return text;

        int len = text.length();
        char[] chars = new char[len];
        if (len >= size + 2) {
            //开头和末尾都有显示的字符
            int startLen = (len - size) / 2;
            int i = 0;
            for (;i < startLen; i++)
                chars[i] = text.charAt(i);
            for (;i < startLen + size; i++)
                chars[i] = '*';
            for (;i < len; i++)
                chars[i] = text.charAt(i);
        } else if (len <= size + 1) {
            //仅开头显示1个字符
            int i = 0;
            chars[i] = text.charAt(i);
            for (i++;i < len;i++)
                chars[i] = '*';
        }

        return new String(chars);
    }

    private String hideMobile(String mobile) {
        return hidePrivacy(mobile, 4);
    }

    private String hideEmail(String email) {
        if (email == null || email.length() == 0)
            return email;
        String[] parts = email.split("@");
        return hidePrivacy(parts[0], 4) + '@' + parts[1];
    }

    private UserDetailDTO buildUserDetailDTO(UserDetail userDetail) {
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        BeanUtils.copyProperties(userDetail, userDetailDTO);
        //脱敏
        userDetailDTO.setMobile(hideMobile(userDetail.getMobile()));
        userDetailDTO.setEmail(hideEmail(userDetail.getEmail()));
        return userDetailDTO;
    }
}
