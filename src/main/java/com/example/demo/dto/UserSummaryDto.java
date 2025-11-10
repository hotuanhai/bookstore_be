package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryDto {
    private Long id;
    private String name;
    private String avatar;
}
