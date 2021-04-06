package com.ikeyit.passport.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.passport.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


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

	@Update("UPDATE user SET enabled = #{enabled} WHERE id = #{id}")
	void updateEnabled(Long id, Boolean enabled);

	@Select({
			"<script>",
			"SELECT * FROM user <where>",
			"<if test=\"id != null\">",
			"AND id = #{id} ",
			"</if>",
			"<if test=\"loginName != null\">",
			"AND loginName = #{loginName} ",
			"</if>",
			"<if test=\"mobile != null\">",
			"AND mobile = #{mobile} ",
			"</if>",
			"<if test=\"email != null\">",
			"AND email = #{email} ",
			"</if>",
			"</where>",
			"<if test=\"pageParam != null\">",
			"LIMIT #{pageParam.pageSize} OFFSET #{pageParam.offset}",
			"</if>",
			"</script>"
	})
	List<User> listAll(Long id, String loginName, String mobile, String email, PageParam pageParam);


	@Select({
			"<script>",
			"SELECT COUNT(*) FROM user <where>",
			"<if test=\"id != null\">",
			"AND id = #{id} ",
			"</if>",
			"<if test=\"loginName != null\">",
			"AND loginName = #{loginName} ",
			"</if>",
			"<if test=\"mobile != null\">",
			"AND mobile = #{mobile} ",
			"</if>",
			"<if test=\"email != null\">",
			"AND email = #{email} ",
			"</if>",
			"</where>",
			"</script>"
	})
	long countAll(Long id, String loginName, String mobile, String email);


	default Page<User> getAll(Long id, String loginName, String mobile, String email, PageParam pageParam) {
		return new Page<>(
				listAll(id, loginName, mobile, email, pageParam),
				pageParam,
				countAll(id, loginName, mobile, email));
	}
}
