package com.ikeyit.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 卖家商品服务
 */
@Service
public class SellerProductService {
    private static Logger log = LoggerFactory.getLogger(SellerProductService.class);

    private static class AttributeCollection {
        //产品销售属性
        List<Attribute> saleAttributes = new ArrayList<>();
        //产品基本销售属性
        List<Attribute> basicAttributes = new ArrayList<>();

        //重复检验
        // attributeId:val => attributeValue 映射
        HashMap<String, AttributeValue> attributeValueValMap = new HashMap<>();
        // attributeValueId => attributeValue 映射
        HashMap<Long, AttributeValue> attributeValueIdMap = new HashMap<>();
        List<AttributeValue> attributeValues = new ArrayList<>();

        public void addValue(AttributeValue attributeValue) {
            attributeValues.add(attributeValue);
            attributeValueValMap.put(attributeValue.getAttributeId() + ":" + attributeValue.getVal(), attributeValue);
            attributeValueIdMap.put(attributeValue.getId(), attributeValue);
        }

        public AttributeValue getValue(Long valueId) {
            return attributeValueIdMap.get(valueId);
        }

        public AttributeValue getValue(Long attributeId, String val) {
            return attributeValueValMap.get(attributeId + ":" + val);
        }

        public boolean containsValue(Long attributeId, String val) {
            return attributeValueValMap.containsKey(attributeId + ":" + val);
        }

        public List<AttributeValue> getValues() {
            return attributeValues;
        }

        public void addSaleAttribute(Attribute attribute) {
            if (!saleAttributes.contains(attribute))
                saleAttributes.add(attribute);
        }

        public List<Attribute> getSaleAttributes() {
            return saleAttributes;
        }

        public void addBasicAttribute(Attribute attribute) {
            if (!basicAttributes.contains(attribute))
                basicAttributes.add(attribute);
        }

        public List<Attribute> getBasicAttributes() {
            return basicAttributes;
        }

    }



    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    AttributeValueRepository attributeValueRepository;

    @Autowired
    SkuRepository skuRepository;

    @Autowired
    ProductAttributeValueRepository  productAttributeValueRepository;

    /**
     * 创建商品
     * @param editProductParam
     */
    @Transactional
    public void createProduct(EditProductParam editProductParam) {
        authenticationService.requireAuthority("r_seller");
        Long sellerId = authenticationService.getCurrentUserId();
        Product product = new Product();
        validateCoreParam(editProductParam, product);
        product.setSellerId(sellerId);
        product.setStatus(Product.STATUS_OFF);
        //暂未启用
        //product.setBrandId(null);
        product.setSales(0L);
        product.setRecommendation(0L);
        //验证所有入参产品属性是否符合类目要求
        validateCategory(editProductParam, product);
        validatePriceAndStock(editProductParam, product);
        product.setDetail(stringifyDetail(editProductParam.getDetail()));
        productRepository.create(product);
        editProductParam.setId(product.getId());
        AttributeCollection attributeCollection = validateAttributes(editProductParam);
        List<Sku> skus = validateSkus(editProductParam, attributeCollection);
        int i = 0;
        for (AttributeValue attributeValue : attributeCollection.getValues()) {
            ProductAttributeValue productAttributeValue = new ProductAttributeValue();
            productAttributeValue.setAttributeValueId(attributeValue.getId());
            productAttributeValue.setProductId(product.getId());
            productAttributeValue.setPosition(i);
            productAttributeValueRepository.create(productAttributeValue);
        }

        //SKU
        for (Sku sku : skus) {
            //存储sku到数据库
            sku.setStatus(Sku.STATUS_ON);
            sku.setProductId(product.getId());
            sku.setSellerId(sellerId);
            sku.setLockedStock(0L);
            sku.setPromotionPrice(null);
            sku.setSales(0L);
            if (sku.getImage() == null)
                sku.setImage(product.getImage());

            skuRepository.create(sku);
        }

    }

    /**
     * 全量更新商品
     * @param editProductParam
     */
    @Transactional
    public void updateProduct(EditProductParam editProductParam) {
        authenticationService.requireAuthority("r_seller");
        Long sellerId = authenticationService.getCurrentUserId();
        Long productId = editProductParam.getId();
        Product product = getSafeProduct(productId);
        validateCoreParam(editProductParam, product);
        //验证所有入参产品属性是否符合类目要求
        validateCategory(editProductParam, product);
        validatePriceAndStock(editProductParam, product);
        AttributeCollection attributeCollection = validateAttributes(editProductParam);
        List<Sku> skus = validateSkus(editProductParam, attributeCollection);
        product.setDetail(stringifyDetail(editProductParam.getDetail()));
        productRepository.update(product);
        Map<Long, ProductAttributeValue> preProductAttributeValues = productAttributeValueRepository.mapByProductId(productId);
        int i = 0;
        for (AttributeValue attributeValue : attributeCollection.getValues()) {
            ProductAttributeValue preProductAttributeValue = preProductAttributeValues.get(attributeValue.getId());
            if (preProductAttributeValue == null) {
                //新建
                ProductAttributeValue productAttributeValue = new ProductAttributeValue();
                productAttributeValue.setAttributeValueId(attributeValue.getId());
                productAttributeValue.setProductId(product.getId());
                productAttributeValue.setPosition(i);
                productAttributeValueRepository.create(productAttributeValue);
            } else {
                //更新
                preProductAttributeValue.setPosition(i);
                productAttributeValueRepository.update(preProductAttributeValue);
                preProductAttributeValues.remove(attributeValue.getId());
            }
            i++;
        }
        for (ProductAttributeValue preProductAttributeValue : preProductAttributeValues.values()) {
            productAttributeValueRepository.deleteById(preProductAttributeValue.getId());
            attributeValueRepository.obsoleteByProductId(preProductAttributeValue.getAttributeValueId(), productId);
        }

        //对比现有的SKU，进行更新/新建/删除
        Map<String, Sku> preSkus = skuRepository.mapSkusByProductId(productId);
        for (Sku sku : skus) {
            Sku preSku = preSkus.get(sku.getAttributes());
            if (preSku == null) {
                //新建
                //存储sku到数据库
                sku.setStatus(Sku.STATUS_ON);
                sku.setProductId(productId);
                sku.setSellerId(sellerId);
                sku.setLockedStock(0L);
                sku.setPromotionPrice(null);
                sku.setSales(0L);
                if (sku.getImage() == null)
                    sku.setImage(product.getImage());
                skuRepository.create(sku);
            } else {
                //更新
                preSku.setPrice(sku.getPrice());
                preSku.setImage(sku.getImage());
                preSku.setCode(sku.getCode());
                if (preSku.getImage() == null)
                    preSku.setImage(product.getImage());
                preSku.setStock(sku.getStock());
                skuRepository.update(preSku);
                preSkus.remove(preSku.getAttributes());
            }
        }

        //剩余的标记删除
        for (Sku sku : preSkus.values()) {
            skuRepository.updateStatus(sku.getId(), Sku.STATUS_DELETED);
        }
    }

    private void validateCoreParam(EditProductParam editProductParam, Product product) {
        if (editProductParam.getImages() == null || editProductParam.getImages().isEmpty())
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "图片至少一张哦！");
        product.setImageUrls(editProductParam.getImages());
        product.setImage(editProductParam.getImages().get(0));
        if (StringUtils.isAllBlank(editProductParam.getTitle()))
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "标题不能为空");
        product.setTitle(editProductParam.getTitle());
        product.setVideo(editProductParam.getVideo());
        product.setSubtitle(editProductParam.getSubtitle());
        product.setModel(editProductParam.getModel());
    }

    private void validateCategory(EditProductParam editProductParam, Product product) {
        Long categoryId = editProductParam.getCategoryId();
        if (categoryId == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "类目必须指定");
        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "类目不存在");
        if (category.getLevel() != 2)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "需要指定三级类目");

        product.setCategoryId(categoryId);
    }

    private AttributeCollection validateAttributes(EditProductParam editProductParam) {
        //类目标准属性
        Map<Long, Attribute> categoryAttributeMap = attributeRepository.mapByCategoryId(editProductParam.getCategoryId());
        AttributeCollection attributeCollection = new AttributeCollection();
        List<EditProductParam.AttributeParam> attributeParams = editProductParam.getAttributes();
        if (attributeParams == null || attributeParams.isEmpty())
            return attributeCollection;
        for (EditProductParam.AttributeParam attributeParam : attributeParams) {
            Long attributeId = attributeParam.getAttributeId();
            Long attributeValueId = attributeParam.getValueId();
            String val = attributeParam.getVal();
            AttributeValue attributeValue = null;
            Attribute attribute = null;
            if (attributeValueId == null || attributeValueId < 0) {
                //自定义值时需要指定属性ID
                if (attributeId == null)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "自定义属性值必须指定属性ID");
                //自定义值不能为空
                if (StringUtils.isAllBlank(val))
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性值不能为空");
                //属性必须为类目标准属性
                attribute = categoryAttributeMap.get(attributeId);
                if (attribute == null)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性不存在");
                //只有销售属性才可以自定义
                if (attribute.getAttributeType() != Attribute.TYPE_SALE)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "非销售属性不允许自定义");
                //值不能重复
                if (attributeCollection.containsValue(attributeId, val))
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性值已经存在");

                attributeValue = new AttributeValue();
                attributeValue.setAttributeId(attributeId);
                attributeValue.setVal(val);
                attributeValue.setPosition(0);
                attributeValue.setProductId(editProductParam.getId());
                attributeValue.setObsolete(Boolean.FALSE);
                attributeValueRepository.create(attributeValue);
            } else {
                //已经存在的属性
                attributeValue = attributeValueRepository.getById(attributeValueId);
                if (attributeValue == null)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性值不存在");

                //属性必须为类目标准属性
                attributeId = attributeValue.getAttributeId();
                attribute = categoryAttributeMap.get(attributeId);
                if (attribute == null)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性值不属于该类目");

                // 产品自定义属性, 用户修改了值
                if (attributeValue.getProductId() > 0 && val != null && !attributeValue.getVal().equals(val)) {
                    attributeValue.setVal(val);
                    attributeValueRepository.update(attributeValue);
                }

                //值不能重复
                val = attributeValue.getVal();
                if (attributeCollection.containsValue(attributeId, val))
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "属性值已经存在");

            }

            attributeCollection.addValue(attributeValue);
            if (Attribute.TYPE_SALE.equals(attribute.getAttributeType())) {
                attributeCollection.addSaleAttribute(attribute);
            } else if (Attribute.TYPE_BASIC.equals(attribute.getAttributeType())) {
                attributeCollection.addBasicAttribute(attribute);
            }
        }

        return attributeCollection;
    }

    private void validatePriceAndStock(EditProductParam editProductParam, Product product) {
        List<EditProductParam.SkuParam> skuParams = editProductParam.getSkus();
        if (skuParams == null || skuParams.isEmpty())
            return;
        BigDecimal lowestPrice = null;
        BigDecimal highestPrice = null;
        long totalStock = 0;
        for (EditProductParam.SkuParam skuParam : skuParams) {
            if (skuParam.getStock() == null || skuParam.getStock() < 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "库存需要大于等于0");
            totalStock += skuParam.getStock();

            if (skuParam.getPrice() == null || BigDecimal.ZERO.compareTo(skuParam.getPrice()) > 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "价格需要大于等于0");
            if (highestPrice == null || highestPrice.compareTo(skuParam.getPrice()) < 0)
                highestPrice = skuParam.getPrice();
            if (lowestPrice == null || lowestPrice.compareTo(skuParam.getPrice()) > 0)
                lowestPrice = skuParam.getPrice();
        }

        if (totalStock <= 0)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "总库存必须大于0");
        product.setPrice(lowestPrice);
    }

    private List<Sku> validateSkus(EditProductParam editProductParam, AttributeCollection attributeCollection) {
        List<Sku> skus = new ArrayList<>();
        List<EditProductParam.SkuParam> skuParams = editProductParam.getSkus();
        if (skuParams == null || skuParams.isEmpty())
            return skus;
        List<Attribute> saleAttributes = attributeCollection.getSaleAttributes();
        for (EditProductParam.SkuParam skuParam : skuParams) {
            List<EditProductParam.AttributeParam> skuAttributeParams = skuParam.getAttributes();
            List<Long> valueIds = new ArrayList<>();
            if (skuAttributeParams != null && !skuAttributeParams.isEmpty()) {
                if (skuAttributeParams.size() != saleAttributes.size())
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "SKU属性不符合要求");

                for (int i = 0; i < skuAttributeParams.size(); i++) {
                    EditProductParam.AttributeParam attributeParam = skuAttributeParams.get(i);
                    Long attributeId = attributeParam.getAttributeId();
                    Long attributeValueId = attributeParam.getValueId();
                    String val = attributeParam.getVal();
                    AttributeValue attributeValue = null;
                    if (attributeValueId == null || attributeValueId < 0) {
                        // 自定义的属性值，必须同时指定属性ID
                        if (attributeId == null || StringUtils.isAllBlank(val))
                            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "SKU属性不符合要求");
                        attributeValue = attributeCollection.getValue(attributeId, val);
                    } else {
                        attributeValue = attributeCollection.getValue(attributeValueId);
                    }
                    if (attributeValue == null)
                        throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "SKU属性不符合要求");
                    if (attributeValue.getAttributeId() != saleAttributes.get(i).getId())
                        throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "SKU属性不符合要求");
                    valueIds.add(attributeValue.getId());
                }
            } else {
                if (saleAttributes.size() != 0)
                    throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "SKU属性不符合要求");
            }
            Sku sku = new Sku();
            sku.setStock(skuParam.getStock());
            sku.setCode(skuParam.getCode());
            sku.setImage(skuParam.getImage());
            sku.setPrice(skuParam.getPrice());
            sku.setAttributeValueIds(valueIds);
            skus.add(sku);
        }

        return skus;
    }

    /**
     * 查询商品列表
     * @param sellerGetProductsParam
     * @param sort
     * @param pageParam
     * @return
     */
    public Page<ProductDTO> getProducts(SellerGetProductsParam sellerGetProductsParam, String sort, PageParam pageParam) {
        if (sort == null) {
            sort = "createTime_desc";
        } else if (!StringUtils.equalsAny(sort,
                "createTime_desc", "createTime_asc",
                "sales_desc", "sales_asc",
                "stock_desc", "stock_asc",
                "price_desc", "price_asc")) {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        }
        Long sellerId = authenticationService.getCurrentUserId();
        Page<Product> products = productRepository.getBySellerId(
                sellerId,
                sellerGetProductsParam.getStatus(),
                sellerGetProductsParam.getId(),
                sellerGetProductsParam.getTitle(),
                sellerGetProductsParam.getModel(),
                sort,
                pageParam);
        List<ProductDTO> productItems = new ArrayList<>();
        for (Product product : products.getContent()) {
            ProductDTO productItemDTO = new ProductDTO();
            productItemDTO.setId(product.getId());
            productItemDTO.setStatus(product.getStatus());
            productItemDTO.setSellerId(product.getSellerId());
            productItemDTO.setTitle(product.getTitle());
            productItemDTO.setImage(product.getImage());
            productItemDTO.setSales(product.getSales());
            productItemDTO.setPrice(product.getPrice());
            productItemDTO.setCreateTime(product.getCreateTime());
            productItemDTO.setModel(product.getModel());
            productItemDTO.setStock(skuRepository.sumProductStock(product.getId()));
            productItems.add(productItemDTO);
        }

        Page<ProductDTO> page = new Page<>(productItems, pageParam, products.getTotal());
        return page;

    }

    /**
     * 商品详情
     * @param productId
     * @return
     */
    public SellerProductDTO getProduct(Long productId) {
        Product product = productRepository.getById(productId);
        if (product == null)
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND, productId);
        SellerProductDTO productDTO = new SellerProductDTO();
        productDTO.setId(product.getId());
        productDTO.setBrandId(product.getBrandId());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setImages(product.getImageUrls());
        productDTO.setModel(product.getModel());
        productDTO.setDetail(parseDetail(product.getDetail()));
        productDTO.setPrice(product.getPrice());
        productDTO.setStatus(product.getStatus());
        productDTO.setSellerId(product.getSellerId());
        productDTO.setTitle(product.getTitle());
        productDTO.setSubtitle(product.getSubtitle());
        productDTO.setVideo(product.getVideo());
        productDTO.setSales(product.getSales());
        List<AttributeValue> attributeValues = attributeValueRepository.listByProductId(productId);
        HashMap<Long, AttributeValue> attributeValuesMap = new HashMap<>();
        List<AttributeValueDTO> attributeDTOs = attributeValues.stream().map(attributeValue -> {
            AttributeValueDTO attributeDTO = new AttributeValueDTO();
            attributeDTO.setAttributeId(attributeValue.getAttributeId());
            attributeDTO.setValueId(attributeValue.getId());
            attributeDTO.setVal(attributeValue.getVal());
            attributeValuesMap.put(attributeValue.getId(), attributeValue);
            return attributeDTO;
        }).collect(Collectors.toList());
        productDTO.setAttributes(attributeDTOs);
        List<Sku> skus = skuRepository.listSkusByProductId(productId);
        List<SellerProductDTO.SkuDTO> skuDTOs =
        skus.stream().map(sku -> {
            SellerProductDTO.SkuDTO skuDTO = new SellerProductDTO.SkuDTO();
            skuDTO.setId(sku.getId());
            skuDTO.setImage(sku.getImage());
            skuDTO.setPrice(sku.getPrice());
            skuDTO.setStock(sku.getStock());
            skuDTO.setCode(sku.getCode());
            List<AttributeValueDTO> skuAttributeDTOs = sku.getAttributeValueIds().stream().map(valueId->{
                AttributeValue attributeValue = attributeValuesMap.get(valueId);
                AttributeValueDTO attributeDTO = new AttributeValueDTO();
                attributeDTO.setValueId(valueId);
                attributeDTO.setAttributeId(attributeValue.getAttributeId());
                return attributeDTO;
            }).collect(Collectors.toList());
            skuDTO.setAttributes(skuAttributeDTOs);
            return skuDTO;
        }).collect(Collectors.toList());
        productDTO.setSkus(skuDTOs);
        return productDTO;
    }

    /**
     * 上架
     * @param productId
     */
    public void setOnSale(Long productId) {
        Product product = getSafeProduct(productId);
        Integer status = product.getStatus();
        if (status.equals(Product.STATUS_ON))
            return;
        if (!status.equals(Product.STATUS_OFF))
            throw new BusinessException(ProductErrorCode.PRODUCT_ILLEGAL_STATUS);
        if (productRepository.updateStatus(productId, status, Product.STATUS_ON) != 1)
            throw new BusinessException(ProductErrorCode.PRODUCT_ILLEGAL_STATUS);
    }

    /**
     * 下架
     * @param productId
     */
    public void setOffSale(Long productId) {
        Product product = getSafeProduct(productId);
        Integer status = product.getStatus();
        if (status.equals(Product.STATUS_OFF))
            return;
        if (!status.equals(Product.STATUS_ON))
            throw new BusinessException(ProductErrorCode.PRODUCT_ILLEGAL_STATUS);
        if (productRepository.updateStatus(productId, status, Product.STATUS_OFF) != 1)
            throw new BusinessException(ProductErrorCode.PRODUCT_ILLEGAL_STATUS);
    }


    /**
     * 更新sku 库存
     * @param updateSkuStockParams
     */
    @Transactional
    public void updateSkuStock(List<UpdateSkuStockParam> updateSkuStockParams) {
        if (updateSkuStockParams == null || updateSkuStockParams.isEmpty())
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        Long sellerId = authenticationService.getCurrentUserId();
        updateSkuStockParams.forEach(updateSkuStockParam -> {
            Long skuId = updateSkuStockParam.getSkuId();
            Long quantity = updateSkuStockParam.getQuantity();
            if (skuId == null || quantity == null || quantity < 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
            Sku sku = skuRepository.getById(skuId);
            if (sku == null || !sku.getSellerId().equals(sellerId))
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
            skuRepository.updateStock(skuId, quantity);
        });
    }


    /**
     * 删除商品
     * @param productId
     */
    public void deleteProduct(Long productId) {
        Product product = getSafeProduct(productId);
        Integer status = product.getStatus();
        if (productRepository.updateStatus(productId, status, Product.STATUS_DELETED) != 1)
            throw new BusinessException(ProductErrorCode.PRODUCT_ILLEGAL_STATUS);
    }

    private Product getSafeProduct(Long id) {
        if (id == null)
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND, id);
        Product product = productRepository.getById(id);
        if (product == null || Product.STATUS_DELETED.equals(product.getStatus()))
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND, id);
        Long sellerId = authenticationService.getCurrentUserId();
        if (!product.getSellerId().equals(sellerId))
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND, id);

        return product;
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
