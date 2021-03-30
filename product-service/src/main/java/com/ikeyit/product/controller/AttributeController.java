package com.ikeyit.product.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.product.domain.AttributeValue;
import com.ikeyit.product.dto.AttributeDTO;
import com.ikeyit.product.dto.AttributeValueDTO;
import com.ikeyit.product.dto.EditAttributeParam;
import com.ikeyit.product.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AttributeController {

    @Autowired
    AttributeService attributeService;

    @GetMapping("/category/{id}/attributes")
    public List<AttributeDTO> getAttributesByCategory(@PathVariable Long id) {
        return attributeService.getAttributesByCategory(id);
    }

    @GetMapping("/category/{id}/attribute_values")
    public List<AttributeValueDTO> getAttributeValuesByCategory(@PathVariable Long id) {
        return attributeService.getAttributeValuesByCategory(id);
    }

    @GetMapping("/attributes")
    public Page<AttributeDTO> getAttributes(@RequestParam(required = false) String name, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return attributeService.getAttributes(name, new PageParam(page, pageSize));
    }

    @GetMapping("/attribute/{id}")
    public AttributeDTO getAttribute(@PathVariable Long id) {
        return attributeService.getAttributeDetail(id);
    }

    @GetMapping("/attribute/{id}/values")
    public List<AttributeValue> getAttributeValues(@PathVariable Long id) {
        return attributeService.getAttributeValues(id);
    }

    @PostMapping("/attribute")
    public void createAttribute(@RequestBody EditAttributeParam editAttributeParam) {
        attributeService.createAttribute(editAttributeParam);
    }

    @PutMapping("/attribute/{id}")
    public void updateAttribute(@PathVariable Long id, @RequestBody EditAttributeParam editAttributeParam) {
        editAttributeParam.setAttributeId(id);
        attributeService.updateAttribute(editAttributeParam);
    }

    @DeleteMapping("/attribute/{id}")
    public void obsoleteAttribute(@PathVariable Long id) {
        attributeService.obsoleteAttribute(id);
    }
}
