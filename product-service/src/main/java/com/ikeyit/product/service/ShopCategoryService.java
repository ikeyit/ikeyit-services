package com.ikeyit.product.service;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.product.domain.Product;
import com.ikeyit.product.domain.ShopCategory;
import com.ikeyit.product.domain.ShopCategoryProduct;
import com.ikeyit.product.dto.*;
import com.ikeyit.product.repository.ProductRepository;
import com.ikeyit.product.repository.ShopCategoryProductRepository;
import com.ikeyit.product.repository.ShopCategoryRepository;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopCategoryService {

    static Logger log = LoggerFactory.getLogger(ShopCategoryService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ShopCategoryRepository shopCategoryRepository;

    @Autowired
    ShopCategoryProductRepository shopCategoryProductRepository;

    public int createShopCategory(EditShopCategoryParam editShopCategoryParam) {
        Long userId = authenticationService.getCurrentUserId();
        ShopCategory category = new ShopCategory();
        Long parentId = editShopCategoryParam.getParentId();
        if (parentId != null && parentId > 0) {
            //创建子类目
            ShopCategory parentCategory = getExistingCategory(parentId);
            if (parentCategory.getLevel() > 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "只允许创建2级");
            category.setLevel(parentCategory.getLevel() + 1);
            category.setParentId(parentId);
        } else {
            //创建根类目
            category.setLevel(0);
            category.setParentId(0L);
        }
        category.setSellerId(userId);
        category.setPosition(editShopCategoryParam.getPosition());
        category.setName(editShopCategoryParam.getName());
        category.setImage(editShopCategoryParam.getImage());
        return shopCategoryRepository.create(category);
    }

    public int updateShopCategory(EditShopCategoryParam editShopCategoryParam) {
        Long userId = authenticationService.getCurrentUserId();
        ShopCategory category = getExistingCategory(editShopCategoryParam.getId());
        if (!category.getSellerId().equals(userId))
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        if (editShopCategoryParam.getPosition() != null)
            category.setPosition(editShopCategoryParam.getPosition());
        if (editShopCategoryParam.getName() != null)
            category.setName(editShopCategoryParam.getName());
        if (editShopCategoryParam.getImage() != null)
            category.setImage(editShopCategoryParam.getImage());
        return shopCategoryRepository.update(category);
    }


    private ShopCategoryDTO buildCategoryDTO(ShopCategory shopCategory) {
        ShopCategoryDTO categoryDTO = new ShopCategoryDTO();
        categoryDTO.setId(shopCategory.getId());
        categoryDTO.setLevel(shopCategory.getLevel());
        categoryDTO.setName(shopCategory.getName());
        categoryDTO.setPosition(shopCategory.getPosition());
        categoryDTO.setParentId(shopCategory.getParentId());
        categoryDTO.setImage(shopCategory.getImage());
        return categoryDTO;
    }

    public List<ShopCategoryDTO> getShopCategories(Long sellerId) {
        List<ShopCategory> categories = shopCategoryRepository.listBySellerId(sellerId);
        List<ShopCategoryDTO> shopCategoryDTOs = new ArrayList<>(16);
        HashMap<Long, ShopCategoryDTO> map = new HashMap<>();

        for (ShopCategory category : categories) {
            ShopCategoryDTO categoryDTO = buildCategoryDTO(category);
            map.put(category.getId(), categoryDTO);

            if (category.getParentId() == null || category.getParentId() == 0L) {
                shopCategoryDTOs.add(categoryDTO);
            } else {
                ShopCategoryDTO parent = map.get(category.getParentId());
                if (parent == null) {
                    //数据不一致啦
                    log.warn("Category的父节点不存在！ID：" + category.getId());
                } else {
                    List<ShopCategoryDTO> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }

                    children.add(categoryDTO);
                }
            }
        }

        return shopCategoryDTOs;
    }


    public List<ShopCategoryDTO> getShopCategories() {
        Long userId = authenticationService.getCurrentUserId();
        return getShopCategories(userId);
    }

    private ShopCategory getExistingCategory(Long id) {
        if (id == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "类目ID不能为空");
        ShopCategory category = shopCategoryRepository.getById(id);
        if (category == null)
            throw new BusinessException(CommonErrorCode.NOT_FOUND);
        return category;
    }

    private ShopCategory getAuthorizedShopCategory(long shopCategoryId) {
        Long userId = authenticationService.getCurrentUserId();
        ShopCategory category = getExistingCategory(shopCategoryId);
        if (!category.getSellerId().equals(userId))
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        return category;
    }

    @Transactional
    public void deleteShopCategory(Long id) {
        ShopCategory category = getAuthorizedShopCategory(id);
        if (category.getLevel() == 0) {
            //子分类
            shopCategoryRepository.deleteByParentId(id);
            shopCategoryProductRepository.deleteByShopCategoryId1(id);
        } else {
            shopCategoryProductRepository.deleteByShopCategoryId2(id);
        }
        shopCategoryRepository.delete(id);
    }


    private ProductDTO toProductDTO(ShopCategoryProduct shopCategoryProduct) {
        Product product = productRepository.getById(shopCategoryProduct.getProductId());
        ProductDTO productDTO = new ProductDTO();
        if (product == null) {

        } else {
            productDTO.setId(product.getId());
            productDTO.setImage(product.getImage());
            productDTO.setTitle(product.getTitle());
            productDTO.setStatus(product.getStatus());
        }


        return productDTO;
    }


    public List<ProductDTO> getShopCategoryProducts(Long shopCategoryId2) {
        List<ShopCategoryProduct> shopCategoryProducts = shopCategoryProductRepository.listByShopCategoryId2(shopCategoryId2, null);
        return shopCategoryProducts.stream().map(this::toProductDTO).collect(Collectors.toList());
    }


    public void addProductIntoShopCategory(AddProductIntoShopCategoryParam addProductIntoShopCategoryParam) {
        Long[] productIds = addProductIntoShopCategoryParam.getProductIds();
        Long shopCategoryId = addProductIntoShopCategoryParam.getShopCategoryId();
        if (productIds == null || shopCategoryId == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        ShopCategory category = getAuthorizedShopCategory(shopCategoryId);
        //只允许在二级分类上关联商品
        if (category.getLevel() != 1)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        if (productIds.length == 1) {
            ShopCategoryProduct shopCategoryProduct = new ShopCategoryProduct();
            shopCategoryProduct.setProductId(productIds[0]);
            shopCategoryProduct.setShopCategoryId1(category.getParentId());
            shopCategoryProduct.setShopCategoryId2(category.getId());
            shopCategoryProductRepository.create(shopCategoryProduct);
        } else {
            //批量提交
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            ShopCategoryProductRepository mapper = sqlSession.getMapper(ShopCategoryProductRepository.class);
            for (int i = 0; i < productIds.length; i++) {
                ShopCategoryProduct shopCategoryProduct = new ShopCategoryProduct();
                shopCategoryProduct.setProductId(productIds[i]);
                shopCategoryProduct.setShopCategoryId1(category.getParentId());
                shopCategoryProduct.setShopCategoryId2(category.getId());
                mapper.create(shopCategoryProduct);
            }
            sqlSession.flushStatements();
        }
    }

    public void removeProductIntoShopCategory(RemoveProductIntoShopCategoryParam removeProductIntoShopCategoryParam) {
        Long[] productIds = removeProductIntoShopCategoryParam.getProductIds();
        Long shopCategoryId = removeProductIntoShopCategoryParam.getShopCategoryId();
        if (productIds == null || shopCategoryId == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        ShopCategory category = getAuthorizedShopCategory(shopCategoryId);
        //只允许在二级分类上关联商品
        if (category.getLevel() != 1)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        if (productIds.length == 1) {
            shopCategoryProductRepository.deleteByShopCategoryId2AndProductId(shopCategoryId, productIds[0]);
        } else {
            //批量提交
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            ShopCategoryProductRepository mapper = sqlSession.getMapper(ShopCategoryProductRepository.class);
            for (int i = 0; i < productIds.length; i++) {
                mapper.deleteByShopCategoryId2AndProductId(shopCategoryId, productIds[i]);
            }
            sqlSession.flushStatements();
        }

    }

    @Transactional
    public void orderShopCategories(OrderShopCategoriesParam orderShopCategoriesParam) {
        List<Long> ids = orderShopCategoriesParam.getIds();
        for(int i = 0; i < ids.size(); i++) {
            ShopCategory shopCategory = getAuthorizedShopCategory(ids.get(i));
            shopCategory.setPosition(i);
            shopCategoryRepository.update(shopCategory);
        }

    }
}
