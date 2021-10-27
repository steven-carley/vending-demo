package com.stevencarley.vendingdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coin {
    private String weight;
    private String diameter;
    private String value;
}
