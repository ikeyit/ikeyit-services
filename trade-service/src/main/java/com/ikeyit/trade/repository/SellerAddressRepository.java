package com.ikeyit.trade.repository;

import com.ikeyit.trade.domain.SellerAddress;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface SellerAddressRepository {

	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("INSERT INTO trade_seller_address (sellerId,name,phone,province,city,district,street,zipCode,defaultShipFrom,defaultReturnTo) VALUES " +
			"(#{sellerId},#{name},#{phone},#{province},#{city},#{district},#{street},#{zipCode},#{defaultShipFrom},#{defaultReturnTo})")
	int create(SellerAddress sellerAddress);

	@Select("SELECT * FROM trade_seller_address WHERE id = #{id}")
	SellerAddress getById(Long id);

	@Select("SELECT * FROM trade_seller_address WHERE sellerId = #{sellerId}")
	List<SellerAddress> getBySellerId(Long sellerId);

	@Select("SELECT * FROM trade_seller_address WHERE sellerId = #{sellerId} AND defaultShipFrom = TRUE ORDER BY id LIMIT 1")
	SellerAddress getDefaultShipFrom(Long sellerId);

	@Select("SELECT * FROM trade_seller_address WHERE sellerId = #{sellerId} AND defaultReturnTo = TRUE ORDER BY id LIMIT 1")
	SellerAddress getDefaultReturnTo(Long sellerId);

	@Update("UPDATE trade_seller_address SET defaultShipFrom = FALSE WHERE sellerId = #{sellerId} AND defaultShipFrom = TRUE")
	int clearDefaultShipFrom(Long sellerId);

	@Update("UPDATE trade_seller_address SET defaultReturnTo = FALSE WHERE sellerId = #{sellerId} AND defaultReturnTo = TRUE")
	int clearDefaultReturnTo(Long sellerId);

	@Update("UPDATE trade_seller_address SET name = #{name}, phone = #{phone}, province = #{province}, city = #{city}, " +
			"district = #{district}, street = #{street}, zipCode = #{zipCode}, " +
			"defaultShipFrom = #{defaultShipFrom}, defaultReturnTo = #{defaultReturnTo} " +
			"WHERE id = #{id}")
	int update(SellerAddress sellerAddress);

	@Delete("DELETE FROM trade_seller_address WHERE id = #{id}")
	int delete(Long id);

}
