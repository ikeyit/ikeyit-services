package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.UserConnection;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserConnectionRepository {

	@Insert("INSERT INTO user_connection (userId,provider,providerUserId,providerUserName,providerUserNick,providerUserAvatar)" +
			"		VALUES (#{userId},#{provider},#{providerUserId},#{providerUserName},#{providerUserNick},#{providerUserAvatar})")
	void create(UserConnection userConnection);

	@Select("SELECT * FROM user_connection WHERE userId = #{userId}")
	List<UserConnection> getByUserId(Long userId);

	@Select("SELECT * FROM user_connection WHERE userId = #{userId} AND provider = #{provider}")
	List<UserConnection> getByUserIdAndProvider(@Param("userId") Long userId, @Param("provider") String provider);

	@Select("SELECT * FROM user_connection WHERE provider = #{provider} AND providerUserId = #{providerUserId}")
	UserConnection getByProviderUserId(@Param("provider") String provider, @Param("providerUserId") String providerUserId);

	@Select("DELETE FROM user_connection WHERE userId = #{userId} AND provider = #{provider} AND providerUserId = #{providerUserId}")
	void delete(@Param("userId") Long userId, @Param("provider") String provider, @Param("providerUserId") String providerUserId);


	@Update("UPDATE user_connection SET userId = #{userId},providerUserName = #{providerUserName}, providerUserNick = #{providerUserNick}, providerUserAvatar = #{providerUserAvatar} WHERE provider = #{provider} AND providerUserId = #{providerUserId}")
	void update(UserConnection userConnection);
}
