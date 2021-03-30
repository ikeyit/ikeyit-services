package com.ikeyit.user.repository;

import com.ikeyit.user.domain.Address;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface AddressRepository {

	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("INSERT INTO address (userId,name,phone,province,city,district,street,zipCode,preferred) VALUES " +
			"(#{userId},#{name},#{phone},#{province},#{city},#{district},#{street},#{zipCode},#{preferred})")
	int create(Address address);

	@Select("SELECT * FROM address WHERE id = #{id}")
	Address getById(Long id);

	@Select("SELECT * FROM address WHERE userId = #{userId}")
	List<Address> getByUserId(Long userId);

	@Select("SELECT * FROM address WHERE userId = #{userId} AND preferred = TRUE ORDER BY id LIMIT 1")
	Address getPreferredByUserId(Long userId);

	@Select("SELECT * FROM address WHERE userId=#{userId} ORDER BY id DESC LIMIT 1")
	Address getLatestByUserId(Long userId);

	@Update("UPDATE address SET preferred = FALSE WHERE userId = #{userId} AND preferred = TRUE")
	int clearPreferred(Long userId);


	@Update("UPDATE address SET name = #{name}, phone = #{phone}, province = #{province}, city = #{city}, " +
			"district = #{district}, street = #{street}, zipCode = #{zipCode}, preferred = #{preferred} WHERE id = #{id}")
	int update(Address address);

	@Delete("DELETE FROM address WHERE id = #{id}")
	int delete(Long id);

}
