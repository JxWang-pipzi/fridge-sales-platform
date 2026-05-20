package com.fridge.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 品牌DTO
 */
@Data
public class BrandDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌ID
     */
    private Long id;

    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    @Size(min = 1, max = 50, message = "品牌名称长度为1-50个字符")
    private String name;

    /**
     * 品牌Logo
     */
    private String logo;

    /**
     * 品牌描述
     */
    @Size(max = 200, message = "品牌描述最多200个字符")
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0-禁用，1-正常）
     */
    private Integer status;
}
