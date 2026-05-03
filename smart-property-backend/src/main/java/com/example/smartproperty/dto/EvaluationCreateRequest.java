package com.example.smartproperty.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class EvaluationCreateRequest {

    @Min(value = 1, message = "服务态度评分至少为1")
    @Max(value = 5, message = "服务态度评分最多为5")
    private Integer serviceScore;

    @Min(value = 1, message = "维修质量评分至少为1")
    @Max(value = 5, message = "维修质量评分最多为5")
    private Integer qualityScore;

    @Min(value = 1, message = "处理速度评分至少为1")
    @Max(value = 5, message = "处理速度评分最多为5")
    private Integer speedScore;

    @NotBlank(message = "请填写评价内容")
    private String content;

    private List<String> images;
}
