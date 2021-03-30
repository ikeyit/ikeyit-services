package com.ikeyit.message.repository;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.message.domain.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface MessageRepository {

	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("INSERT INTO message (fromId,toId,messageType,status,topic,content) VALUES " +
			"(#{fromId},#{toId},#{messageType},#{status},#{topic},#{content})")
	int create(Message sellerMessage);

	@Select("SELECT * FROM message WHERE id = #{id}")
	Message getById(Long id);

	@Select("SELECT * FROM message WHERE toId = #{toId}")
    List<Message> getByToId(Long toId);

	@Update("UPDATE message SET status = #{status} " +
			"WHERE toId = #{toId} AND id = #{id} AND status != #{status}")
	int updateStatusByID(Long toId, Long id, Integer status);

	@Update({"<script>",
			"UPDATE message SET status = #{status} ",
			"WHERE toId = #{toId} AND status != #{status} AND id IN ",
			"<foreach collection=\"ids\" item=\"i\" open=\"(\" separator=\",\" close=\")\">",
			"#{i}",
			"</foreach>",
			"</script>"})
	int updateStatusByIDs(Long toId, Long[] ids, Integer status);

	@Update("UPDATE message SET status = #{status} " +
			"WHERE toId = #{toId} AND status != #{status} AND messageType = #{messageType}")
	int updateStatusByType(Long toId, Integer messageType, Integer status);

	@Delete("DELETE FROM message WHERE id = #{id}")
	int delete(Long id);

	@Select({"<script>",
			"SELECT COUNT(*) FROM message WHERE toId = #{toId} ",
			"<if test=\"messageType != null\">",
			"AND messageType = #{messageType} ",
			"</if>",
			"<if test=\"status != null\">",
			"AND status = #{status} ",
			"</if>",
			"</script>"})
	long countReceiverMessages(Long toId, Integer messageType, Integer status);


	@Select({"<script>",
			"SELECT * FROM message WHERE toId = #{toId} ",
			"<if test=\"messageType != null\">",
			"AND messageType = #{messageType} ",
			"</if>",
			"<if test=\"status != null\">",
			"AND status = #{status} ",
			"</if>",
			"ORDER BY id DESC ",
			"LIMIT #{limit} OFFSET #{offset}",
			"</script>"})
	List<Message> listReceiverMessages(Long toId, Integer messageType, Integer status, Long offset, Integer limit);


	default Page<Message> getReceiverMessages(Long toId, Integer messageType, Integer status, PageParam pageParam) {
		return new Page<>(listReceiverMessages(toId, messageType, status, pageParam.getOffset(), pageParam.getPageSize()),
				pageParam, countReceiverMessages(toId, messageType, status));
	}
}
