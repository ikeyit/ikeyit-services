package com.ikeyit.passport.repository;

import com.ikeyit.passport.domain.WeixinConnection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WeixinConnectionRepository {

	@Insert("INSERT INTO weixin_connection (userId, appId, unionId, openId)" +
			"		VALUES (#{userId},#{appId}, #{unionId},#{openId})")
	void create(WeixinConnection weixinConnection);

	@Select("SELECT * FROM weixin_connection WHERE userId = #{userId}")
	List<WeixinConnection> getByUserId(Long userId);

	@Select("SELECT * FROM weixin_connection WHERE userId = #{userId} AND appId = #{appId}")
	WeixinConnection getByUserAndAppId(Long userId, String appId);

	@Select("SELECT * FROM weixin_connection WHERE openId = #{openId}")
	WeixinConnection getByOpenId(String openId);

	@Update("UPDATE weixin_connection SET openId=#{openId}, unionId=#{unionId} WHERE userId = #{userId} AND appId = #{appId}")
	void update(WeixinConnection weixinConnection);
}
