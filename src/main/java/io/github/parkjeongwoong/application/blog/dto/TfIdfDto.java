package io.github.parkjeongwoong.application.blog.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TfIdfDto {
    private String word;
    private List<Long> documentIdList;
}
