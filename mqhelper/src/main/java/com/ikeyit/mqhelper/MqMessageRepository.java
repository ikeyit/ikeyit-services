package com.ikeyit.mqhelper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MqMessageRepository {

    @Options(useGeneratedKeys=true, keyProperty="id")
    @Insert("INSERT mq_message (status, topic,  " +
            "payload, messageKeys, deliverTime" +
            ") VALUES (#{status}, #{topic},  " +
            "#{payload}, #{messageKeys}, #{deliverTime})")
    int create(MqMessage mqMessage);

    @Select("SELECT * FROM mq_message WHERE id = #{id}")
    MqMessage getById(Long id);

    @Select("SELECT * FROM mq_message WHERE TIMESTAMPDIFF(SECOND, createTime, NOW()) > #{timeout} ORDER BY id DESC LIMIT #{count}")
    List<MqMessage> getTimeout(long timeout, int count);

    @Update("UPDATE mq_message SET status = #{status} " +
            "WHERE id = #{id}")
    int updateStatus(Long id, Integer status);

    @Delete("DELETE FROM mq_message WHERE id = #{id}")
    int delete(Long id);
}
