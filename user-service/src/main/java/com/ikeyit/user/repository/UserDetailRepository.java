package com.ikeyit.user.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.user.domain.UserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface UserDetailRepository {

	@Select("SELECT * FROM user WHERE name = #{name}")
	UserDetail getByName(String name);

	@Select("SELECT * FROM user WHERE email = #{email}")
	UserDetail getByEmail(String email);

	@Select("SELECT * FROM user WHERE mobile = #{mobile}")
	UserDetail getByMobile(String mobile);

	@Select("SELECT * FROM user WHERE id = #{id}")
	UserDetail getById(Long id);

	@Update("UPDATE user SET nick = #{nick}, avatar = #{avatar}, email = #{email}, sex = #{sex} , location = #{location} WHERE id = #{id}")
	int update(UserDetail userDetail);

	@Select("SELECT * FROM user LIMIT #{offset}, #{pageSize}")
	List<UserDetail> listUsers(PageParam pageParam);

	@Select("SELECT count(*) FROM user")
	Long countUsers();

	default Page<UserDetail> getUsers(PageParam pageParam) {
		return new Page<>(listUsers(pageParam), pageParam, countUsers());
	}

}
