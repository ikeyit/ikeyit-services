package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleRepository {

	@Insert("INSERT INTO role (name,displayName,description) VALUES (#{name},#{displayName},#{description})")
	void createRole(Role role);

	@Select("SELECT role.* FROM user_role LEFT JOIN role ON user_role.roleId = role.id WHERE user_role.userId = #{userId}")
	List<Role> getUserRoles(Long userId);

	@Insert("INSERT INTO user_role (userId, roleId) VALUES (#{userId}, #{roleId})")
	void addUserRole(Long userId, Long roleId);

	@Delete("DELETE FROM role WHERE id = #{id}")
	void deleteRole(Long id);

	@Delete("DELETE FROM user_role WHERE userId = #{userId} AND roleId = #{roleId}")
	void deleteUserRole(Long userId, Long roleId);
}
