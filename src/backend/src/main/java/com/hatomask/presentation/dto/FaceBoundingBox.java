package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceBoundingBox {
    private Double xMin;
    private Double yMin;
    private Double width;
    private Double height;
}
