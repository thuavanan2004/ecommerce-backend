package com.devdynamo.entities;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisCart {
    private long productId;
    private String name;
    private int price;
    private int quantity;
    private String image;
}
