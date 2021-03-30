package com.ikeyit.product.domain;

public class Category {

	public static Integer STATUS_VISIBLE = 1;
	public static Integer STATUS_INVISIBLE = 2;
	public static Integer STATUS_OBSOLETE = 0;

	private Long id;

	//父分类ID
	private Long parentId;

	//分类层级
	private Integer level;

	//状态
	private Integer status;

	//分类名称
	private String name;

	//分类描述
	private String description;

	private Boolean obsolete;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Boolean getObsolete() {
		return obsolete;
	}

	public void setObsolete(Boolean obsolete) {
		this.obsolete = obsolete;
	}
}
