package com.ikeyit.product.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.product.domain.Attribute;
import com.ikeyit.product.domain.AttributeValue;
import com.ikeyit.product.dto.AttributeDTO;
import com.ikeyit.product.dto.AttributeValueDTO;
import com.ikeyit.product.dto.EditAttributeParam;
import com.ikeyit.product.repository.AttributeRepository;
import com.ikeyit.product.repository.AttributeValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AttributeService {

    private static Logger log = LoggerFactory.getLogger(AttributeService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    AttributeValueRepository attributeValueRepository;


    public List<AttributeDTO> getAttributesByCategory(Long categoryId) {
        List<Attribute> attributes = attributeRepository.listByCategoryId(categoryId);
        return attributes.stream().map(attribute -> {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setAttributeId(attribute.getId());
            attributeDTO.setName(attribute.getName());
            attributeDTO.setAttributeType(attribute.getAttributeType());
            attributeDTO.setRequired(attribute.getRequired());
            return attributeDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 类目标准属性
     * @param categoryId
     * @return
     */
    public List<AttributeValueDTO> getAttributeValuesByCategory(Long categoryId) {
        List<AttributeValue> attributeValues = attributeValueRepository.listByCategoryId(categoryId);
        return attributeValues.stream().map(attributeValue -> {
            AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
            attributeValueDTO.setAttributeId(attributeValue.getAttributeId());
            attributeValueDTO.setValueId(attributeValue.getId());
            attributeValueDTO.setVal(attributeValue.getVal());
            return attributeValueDTO;
        }).collect(Collectors.toList());
    }

    public Page<AttributeDTO> getAttributes(String name, PageParam pageParam) {
        authenticationService.requireAuthority("ROLE_SUPER");
        Page<Attribute> attributes = attributeRepository.get(name, pageParam);
        return Page.map(attributes, attribute -> {
            AttributeDTO attributeDTO = new AttributeDTO();
            attributeDTO.setAttributeId(attribute.getId());
            attributeDTO.setName(attribute.getName());
            attributeDTO.setAttributeType(attribute.getAttributeType());
            attributeDTO.setRequired(attribute.getRequired());
            return attributeDTO;
        });
    }

    public List<AttributeValue> getAttributeValues(Long attributeId) {
        //TODO 平台管理员验证
        List<AttributeValue>  attributeValues = attributeValueRepository.listByAttributeId(attributeId);
        return attributeValues;
    }

    public AttributeDTO getAttributeDetail(Long id) {
        authenticationService.requireAuthority("ROLE_SUPER");
        Attribute attribute = attributeRepository.getById(id);
        if (attribute == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setAttributeId(attribute.getId());
        attributeDTO.setName(attribute.getName());
        attributeDTO.setPosition(attribute.getPosition());
        attributeDTO.setAttributeType(attribute.getAttributeType());
        attributeDTO.setRequired(attribute.getRequired());

        List<AttributeValue> attributeValues = attributeValueRepository.listByAttributeId(id);
        List<AttributeValueDTO> attributeValueDTOs = attributeValues.stream().map(attributeValue -> {
            AttributeValueDTO attributeValueDTO = new AttributeValueDTO();
            attributeValueDTO.setValueId(attributeValue.getId());
            attributeValueDTO.setVal(attributeValue.getVal());
//            attributeValueDTO.setPosition(attributeValue.getPosition());
            return attributeValueDTO;
        }).collect(Collectors.toList());
        attributeDTO.setValues(attributeValueDTOs);
        return attributeDTO;
    }

    @Transactional
    public void createAttribute(EditAttributeParam editAttributeParam) {
        authenticationService.requireAuthority("ROLE_SUPER");
        Attribute attribute = new Attribute();
        attribute.setAttributeType(editAttributeParam.getAttributeType());
        attribute.setName(editAttributeParam.getName());
        attribute.setRequired(editAttributeParam.getRequired());
        attribute.setSearchType(0);
        attribute.setInputType(0);
        attribute.setPosition(0);
        attribute.setObsolete(Boolean.FALSE);
        attributeRepository.create(attribute);
        List<EditAttributeParam.AttributeValueParam> valueParams = editAttributeParam.getValues();
        if (valueParams != null) {
            int i = 0;
            for (EditAttributeParam.AttributeValueParam valueParam : valueParams) {
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setProductId(0L);
                attributeValue.setObsolete(Boolean.FALSE);
                attributeValue.setVal(valueParam.getVal());
                attributeValue.setAttributeId(attribute.getId());
                attributeValue.setPosition(i++);
                attributeValueRepository.create(attributeValue);
            }
        }

    }

    public int obsoleteAttribute(Long id) {
        authenticationService.requireAuthority("ROLE_SUPER");
        int i = attributeRepository.obsolete(id);
        if (i > 0)
            attributeValueRepository.obsoleteByAttributeId(id);
        return i;
    }

    @Transactional
    public void updateAttribute(EditAttributeParam editAttributeParam) {
        authenticationService.requireAuthority("ROLE_SUPER");
        if (editAttributeParam.getAttributeId() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        Attribute attribute = attributeRepository.getById(editAttributeParam.getAttributeId());
        if (attribute == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
        attribute.setName(editAttributeParam.getName());
        attribute.setRequired(editAttributeParam.getRequired());
        attributeRepository.update(attribute);

        List<EditAttributeParam.AttributeValueParam> valueParams = editAttributeParam.getValues();
        if (valueParams != null) {
            int i = 0;
            for (EditAttributeParam.AttributeValueParam valueParam : valueParams) {
                if (Boolean.TRUE.equals(valueParam.getDeleted())) {
                    // 逻辑删除
                    if (valueParam.getValueId() != null)
                        attributeValueRepository.obsolete(valueParam.getValueId());
                } else if (valueParam.getValueId() == null) {
                    //新建
                    AttributeValue attributeValue = new AttributeValue();
                    attributeValue.setProductId(0L);
                    attributeValue.setObsolete(Boolean.FALSE);
                    attributeValue.setVal(valueParam.getVal());
                    attributeValue.setAttributeId(attribute.getId());
                    attributeValue.setPosition(i++);
                    attributeValueRepository.create(attributeValue);
                }  else {
                    //更新
                    AttributeValue attributeValue = attributeValueRepository.getById(valueParam.getValueId());
                    if (attributeValue != null) {
                        attributeValue.setVal(valueParam.getVal());
                        attributeValue.setPosition(i++);
                        attributeValueRepository.update(attributeValue);
                    }
                }
            }
        }
    }
}
