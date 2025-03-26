package com.devdynamo.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class PageResponse<T> implements Serializable {
    private long pageNo;
    private long pageSize;
    private long totalPage;
    private T items;
}
