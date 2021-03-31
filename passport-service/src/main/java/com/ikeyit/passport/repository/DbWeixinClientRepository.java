package com.ikeyit.passport.repository;


import com.ikeyit.security.social.weixin.WeixinClient;
import com.ikeyit.security.social.weixin.WeixinClientRepository;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DbWeixinClientRepository extends WeixinClientRepository {

    @Select("SELECT * FROM weixin_client WHERE appId = #{appId}")
    WeixinClient getClient(String appId);

    @Insert("INSERT INTO weixin_client (appId,appName,unionName,appSecret)" +
            " VALUES (#{appId},#{appName},#{unionName},#{appSecret})")
    void create(WeixinClient weixinClient);

    @Update("UPDATE weixin_client SET unionName = #{unionName},appSecret = #{appSecret}, appName = #{appName} WHERE appId = #{appId}")
    void update(WeixinClient weixinClient);

    @Delete("DELETE FROM weixin_client WHERE appId = #{appId}")
    void deleteByAppId(String appId);

    @Select("SELECT * FROM weixin_client")
    List<WeixinClient> list();
}
