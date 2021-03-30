package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.Authority;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AuthorityRepository {

	@Insert("INSERT INTO authority (userId,role) VALUES (#{userId},#{role})")
	void create(Authority authority);

	@Select("SELECT * FROM authority WHERE userId = #{userId}")
	List<Authority> getByUserId(Long userId);

	@Select("DELETE from authority WHERE userId = #{userId} AND role = #{role}")
	void delete(Long userId, String role);

}
