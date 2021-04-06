package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PermissionRepository {

	@Insert("INSERT INTO role (name,displayName,groupName,description) VALUES (#{name},#{displayName},#{groupName},#{description})")
	void createPermission(Permission permission);

	@Select("SELECT permission.* FROM role_permission LEFT JOIN permission ON role_permission.permissionId = permission.id WHERE role_permission.roleId = #{roleId}")
	List<Permission> getRolePermissions(Long roleId);

	@Delete("DELETE FROM permission WHERE id = #{id}")
	void deletePermission(Long id);

	@Insert("INSERT INTO role_permission (roleId, permissionId) VALUES (#{roleId}, #{permissionId})")
	void addRolePermission(Long roleId, Long permissionId);

	@Delete("DELETE FROM role_permission WHERE roleId = #{roleId} AND permissionId = #{permissionId}")
	void deleteRolePermission(Long roleId, Long permissionId);

	@Select("SELECT permission.* FROM user_permission LEFT JOIN permission ON user_permission.permissionId = permission.id WHERE user_permission.userId = #{userId}")
	List<Permission> getUserPermissions(Long userId);

	@Insert("INSERT INTO user_permission (roleId, permissionId) VALUES (#{userId}, #{permissionId})")
	void addUserPermission(Long userId, Long permissionId);

	@Delete("DELETE FROM user_permission WHERE userId = #{userId} AND permissionId = #{permissionId}")
	void deleteUserPermission(Long userId, Long permissionId);
}
