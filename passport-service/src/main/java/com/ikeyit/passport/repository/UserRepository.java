package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserRepository {

	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("INSERT INTO user (password,loginName,email,mobile,nick,avatar,sex,location,enabled,verified)" +
			"		VALUES (#{password},#{loginName},#{email},#{mobile},#{nick},#{avatar},#{sex},#{location},#{enabled},#{verified})")
	void create(User user);

	@Select("SELECT * FROM user WHERE loginName = #{loginName}")
	User getByLoginName(String loginName);

	@Select("SELECT * FROM user WHERE email = #{email}")
	User getByEmail(String email);

	@Select("SELECT * FROM user WHERE mobile = #{mobile}")
	User getByMobile(String mobile);

	@Select("SELECT * FROM user WHERE id = #{id}")
	User getById(Long id);

	@Update("UPDATE user SET password = #{password}, mobile = #{mobile}, loginName = #{loginName}, email = #{email} WHERE id = #{id}")
	void update(User user);
}
