package com.ikeyit.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.common.utils.JsonUtils;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.product.domain.ShopBasicInfo;
import com.ikeyit.product.domain.ShopPage;
import com.ikeyit.product.dto.*;
import com.ikeyit.product.repository.ShopBasicInfoRepository;
import com.ikeyit.product.repository.ShopPageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {
    @Autowired
    AuthenticationService authenticationService;
    
    @Autowired
    ShopBasicInfoRepository shopBasicInfoRepository;
    
    @Autowired
    ShopPageRepository shopPageRepository;

    public int createShopPage(CreateShopPageParam createShopPageParam) {
        Long userId = authenticationService.getCurrentUserId();
        if (createShopPageParam.getType() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        if (createShopPageParam.getName() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        ShopPage shopPage = new ShopPage();
        shopPage.setSellerId(userId);
        shopPage.setName(createShopPageParam.getName());
        shopPage.setStatus(ShopPage.STATUS_DRAFT);
        shopPage.setType(createShopPageParam.getType());
        shopPage.setVersion(0);
        shopPage.setPreferred(Boolean.FALSE);
        return shopPageRepository.create(shopPage);
    }

    public ShopPageDTO getShopPage(Long id) {
        ShopPage shopPage = shopPageRepository.getById(id);
        if (shopPage == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);

        return toShopPageDTO(shopPage);
    }

    private ShopPageDTO toShopPageDTO(ShopPage shopPage) {
        ShopPageDTO shopPageDTO = new ShopPageDTO();
        BeanUtils.copyProperties(shopPage, shopPageDTO);
        if (shopPage.getContent() != null)
            shopPageDTO.setContent(JsonUtils.readValue(shopPage.getContent(), JsonNode.class));
        return shopPageDTO;
    }


    /**
     * 查询当前店铺页面列表
     * @param type
     * @param status
     * @param pageParam
     * @return
     */
    public Page<ShopPageDTO> getShopPages(Integer type, Integer status, PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        return getShopPages(userId, type, status, pageParam);
    }


    /**
     * 查询店铺页面列表
     * @param sellerId
     * @param type
     * @param status
     * @param pageParam
     * @return
     */
    public Page<ShopPageDTO> getShopPages(Long sellerId, Integer type, Integer status, PageParam pageParam) {
        Page<ShopPage> shopPages = shopPageRepository.getAll(sellerId, type, status, pageParam);
        return Page.map(shopPages, this::toShopPageDTO);
    }

    @Transactional
    public int updateShopPage(UpdateShopPageParam updateShopPageParam) {
        Long userId = authenticationService.getCurrentUserId();
        if (updateShopPageParam.getId() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        ShopPage shopPage = shopPageRepository.getById(updateShopPageParam.getId());
        if (shopPage == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        if (!userId.equals(shopPage.getSellerId()))
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        if (updateShopPageParam.getContent() != null)
            shopPage.setContent(JsonUtils.writeValueAsString(updateShopPageParam.getContent()));
        if (updateShopPageParam.getName() != null)
            shopPage.setName(updateShopPageParam.getName());
        if (updateShopPageParam.getStatus() != null)
            shopPage.setStatus(updateShopPageParam.getStatus());
        if (updateShopPageParam.getPreferred() != null && !shopPage.getPreferred().equals(updateShopPageParam.getPreferred())) {
            shopPage.setPreferred(updateShopPageParam.getPreferred());
            if (Boolean.TRUE.equals(updateShopPageParam.getPreferred()))
                shopPageRepository.clearPreferred(userId);
        }

        int ret = shopPageRepository.update(shopPage, updateShopPageParam.getVersion());
        if (ret <= 0)
            throw new BusinessException(CommonErrorCode.RESOURCE_UPDATE_CONFLICT);
        return ret;
    }
    
    
    public ShopHomeDTO getShopHome(Long sellerId) {
        ShopBasicInfo shopBasicInfo = shopBasicInfoRepository.getBySellerId(sellerId);
        if (shopBasicInfo == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        ShopPage shopPage = shopPageRepository.getPreferred(sellerId, ShopPage.TYPE_HOME);
        ShopHomeDTO shopHomeDTO = new ShopHomeDTO();
        shopHomeDTO.setShopId(shopBasicInfo.getId());
        shopHomeDTO.setShopName(shopBasicInfo.getName());
        shopHomeDTO.setShopDescription(shopBasicInfo.getDescription());
        if (shopPage == null || shopPage.getContent() == null) {
            //TODO 生成一个默认内容
        } else {
            shopHomeDTO.setContent(JsonUtils.readValue(shopPage.getContent(), JsonNode.class));
        }
        return shopHomeDTO;
    }


    public ShopBasicInfo getShopBasicInfo() {
        Long sellerId = authenticationService.getCurrentUserId();
        ShopBasicInfo shopBasicInfo = shopBasicInfoRepository.getBySellerId(sellerId);
        if (shopBasicInfo == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        return shopBasicInfo;
    }

    public int updateShopBasicInfo(UpdateShopBasicInfoParam updateShopBasicInfoParam) {
        Long sellerId = authenticationService.getCurrentUserId();
        ShopBasicInfo shopBasicInfo = shopBasicInfoRepository.getBySellerId(sellerId);
        if (shopBasicInfo == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        if (updateShopBasicInfoParam.getName() != null)
            shopBasicInfo.setName(updateShopBasicInfoParam.getName());
        if (updateShopBasicInfoParam.getDescription() != null)
            shopBasicInfo.setDescription(updateShopBasicInfoParam.getDescription());
        if (updateShopBasicInfoParam.getAvatar() != null)
            shopBasicInfo.setAvatar(updateShopBasicInfoParam.getAvatar());
        return shopBasicInfoRepository.update(shopBasicInfo);
    }
}
