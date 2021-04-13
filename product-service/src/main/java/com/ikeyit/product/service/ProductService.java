package com.ikeyit.product.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.product.domain.*;
import com.ikeyit.product.dto.*;
import com.ikeyit.product.exception.ProductErrorCode;
import com.ikeyit.product.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    AttributeValueRepository attributeValueRepository;

    @Autowired
    SkuRepository skuRepository;

    @Autowired
    OrderStockLogRepository orderStockLogRepository;

    @Autowired
    ObjectMapper objectMapper;

    public ProductDTO getProductDetail(Long productId) {
        Product product = productRepository.getById(productId);
        if (product == null)
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND, productId);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setSellerId(product.getSellerId());
        productDTO.setBrandId(product.getBrandId());
        productDTO.setTitle(product.getTitle());
        productDTO.setSubtitle(product.getSubtitle());
        productDTO.setImage(product.getImage());
        productDTO.setImages(product.getImageUrls());
        productDTO.setVideo(product.getVideo());
        productDTO.setDetail(parseDetail(product.getDetail()));
        productDTO.setSales(product.getSales());
        productDTO.setPrice(product.getPrice());
        productDTO.setPromotionPrice(product.getPromotionPrice());
        //获取产品属性值
        List<AttributeValue> attributeValues = attributeValueRepository.listByProductId(productId);
        HashSet<Long> attributeIdSet = new HashSet<>();
        HashMap<Long, AttributeValue> attributeValuesMap = new HashMap<>();
        List<AttributeValueDTO> attributeValueDTOs  = attributeValues.stream().map(attributeValue -> {
            AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
            attributeValueDTO.setAttributeId(attributeValue.getAttributeId());
            attributeValueDTO.setValueId(attributeValue.getId());
            attributeValueDTO.setVal(attributeValue.getVal());
            attributeIdSet.add(attributeValue.getAttributeId());
            attributeValuesMap.put(attributeValue.getId(), attributeValue);
            return attributeValueDTO;
        }).collect(Collectors.toList());

        List<Attribute> attributes = attributeRepository.listByCategoryId(product.getCategoryId());
        List<AttributeDTO> attributeDTOs  = attributes.stream().filter(item -> attributeIdSet.contains(item.getId())).map(item -> {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setAttributeId(item.getId());
            attributeDTO.setName(item.getName());
            attributeDTO.setAttributeType(item.getAttributeType());
            return attributeDTO;
        }).collect(Collectors.toList());

        productDTO.setAttributeValues(attributeValueDTOs);
        productDTO.setAttributes(attributeDTOs);

        List<Sku> skus = skuRepository.listSkusByProductId(productId);
        List<SkuDTO> skuDTOs = skus.stream().map((sku) -> {
            SkuDTO skuDTO = new SkuDTO();
            skuDTO.setId(sku.getId());
            skuDTO.setImage(sku.getImage());
            skuDTO.setPrice(sku.getPrice());
            skuDTO.setStock(sku.getStock());
            List<AttributeValueDTO> skuAttributeDTOs = sku.getAttributeValueIds().stream().map(valueId->{
                AttributeValue attributeValue = attributeValuesMap.get(valueId);
                AttributeValueDTO attributeDTO = new AttributeValueDTO();
                attributeDTO.setValueId(valueId);
                attributeDTO.setVal(attributeValue.getVal());
                attributeDTO.setAttributeId(attributeValue.getAttributeId());
                return attributeDTO;
            }).collect(Collectors.toList());
            String name = skuAttributeDTOs.stream().map((item -> item.getVal())).collect(Collectors.joining(","));
            skuDTO.setName(name);
            skuDTO.setAttributes(skuAttributeDTOs);
            return skuDTO;
        }).collect(Collectors.toList());
        productDTO.setSkus(skuDTOs);
        return productDTO;
    }

    private ProductDTO buildSimpleProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setSellerId(product.getSellerId());
        productDTO.setTitle(product.getTitle());
        productDTO.setImage(product.getImage());
        productDTO.setSales(product.getSales());
        productDTO.setPrice(product.getPrice());
        return productDTO;
    }

    public Page<ProductDTO> getProductsByShop(Long sellerId, String sort) {
        if (sort == null) {
            sort = "createTime_desc";
        } else if (!StringUtils.equalsAny(sort, "createTime_desc", "createTime_asc", "sales_desc", "sales_asc","price_desc", "price_asc")) {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        }

        Page<Product> products = productRepository.getBySellerId(sellerId, Product.STATUS_ON, null, null, null, sort, null);
        return Page.map(products, this::buildSimpleProductDTO);
    }

    public Page<ProductDTO> getProductsByShopCategory(Long sellerId, Long shopCategoryId, String sort, PageParam pageParam) {
        if (sort == null) {
            sort = "createTime_desc";
        } else if (!StringUtils.equalsAny(sort, "createTime_desc", "createTime_asc", "sales_desc", "sales_asc","price_desc", "price_asc")) {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        }

        Page<Product> shopCategoryProducts = productRepository.getBySellerIdAndShopCategoryId(sellerId, shopCategoryId, Product.STATUS_ON, sort, pageParam);
        return Page.map(shopCategoryProducts, this::buildSimpleProductDTO);
    }


    public SkuDTO getSkuDetail(Long id) {
        Sku sku = skuRepository.getById(id);
        Product product = productRepository.getById(sku.getProductId());
        SkuDTO skuDTO = new SkuDTO();
        skuDTO.setId(sku.getId());
        skuDTO.setProductId(product.getId());
        skuDTO.setSellerId(product.getSellerId());
        skuDTO.setTitle(product.getTitle());
        skuDTO.setImage(sku.getImage());
        skuDTO.setStock(sku.getStock());
        skuDTO.setPrice(sku.getPrice());
        List<AttributeValueDTO> attributeValueDTOs = sku.getAttributeValueIds().stream().map(valueId -> {
            AttributeValue attributeValue = attributeValueRepository.getById(valueId);
            Attribute attribute = attributeRepository.getById(attributeValue.getAttributeId());
            AttributeValueDTO attributePair = new AttributeValueDTO();
            attributePair.setName(attribute.getName());
            attributePair.setAttributeId(attributeValue.getAttributeId());
            attributePair.setValueId(attributeValue.getId());
            attributePair.setVal(attributeValue.getVal());
            return attributePair;
        }).collect(Collectors.toList());
        skuDTO.setAttributes(attributeValueDTOs);
        return skuDTO;
    }


    /**
     * 下订单时扣减库存
     * @param reduceStockParam
     */
    @Transactional
    public void reduceOrderStock(ReduceStockParam reduceStockParam) {
        if (reduceStockParam == null || reduceStockParam.getOrderId() == null || reduceStockParam.getItems() == null
            || reduceStockParam.getItems().isEmpty())
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        boolean allItemIsValid = reduceStockParam.getItems().stream().allMatch(reduceStockItem -> reduceStockItem != null
                && reduceStockItem.getSkuId() != null && reduceStockItem.getQuantity() > 0);
        if (!allItemIsValid)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        reduceStockParam.getItems().forEach(reduceStockItem -> {
            int result = skuRepository.reduceStock(reduceStockItem.getSkuId(), reduceStockItem.getQuantity());
            if (result < 1)
                throw new BusinessException(ProductErrorCode.OUT_OF_STOCK, reduceStockItem.getSkuId());
        });

        //记录库存流水
        OrderStockLog orderStockLog = new OrderStockLog();
        orderStockLog.setOrderId(reduceStockParam.getOrderId());
        orderStockLog.setStatus(OrderStockLog.STATUS_OCCUPIED);
        try {
            orderStockLog.setContent(objectMapper.writeValueAsString(reduceStockParam));
        } catch (JsonProcessingException e) {
            log.error("库存变动流水数据序列化失败！", e);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        orderStockLogRepository.create(orderStockLog);
    }


    /**
     * 订单取消时，释放库存
     * @param orderId
     */
    @Transactional
    public void addOrderStock(Long orderId) {
        OrderStockLog orderStockLog = orderStockLogRepository.getByOrderId(orderId);
        if (orderStockLog == null) {
            log.error("[潜在BUG]库存变动流水没有找到！orderId: {}", orderId);
            return;
        }
        Integer status = orderStockLog.getStatus();
        if (OrderStockLog.STATUS_RELEASED.equals(status))
            //已经被释放掉了！
            return;

        if (orderStockLogRepository.updateStatus(orderId, status, OrderStockLog.STATUS_RELEASED) < 1)
            //更新为释放状态失败，说明其它消费者已经释放了库存
            return;

        try {
            ReduceStockParam reduceStockParam = objectMapper.readValue(orderStockLog.getContent(), ReduceStockParam.class);
            reduceStockParam.getItems().forEach(reduceStockItem -> {
                skuRepository.addStock(reduceStockItem.getSkuId(), reduceStockItem.getQuantity());
            });
        } catch (JsonProcessingException e) {
            log.error("库存变动流水数据解析失败！", e);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    private ArrayNode parseDetail(String detailText) {
        if (detailText == null || detailText.isEmpty())
            return null;
        try {
            return objectMapper.readValue(detailText, ArrayNode.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("产品细节数据格式错误！");
        }
    }

    private String stringifyDetail(ArrayNode detail) {
        if (detail == null)
            return null;
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("产品细节数据格式错误！");
        }
    }
}
