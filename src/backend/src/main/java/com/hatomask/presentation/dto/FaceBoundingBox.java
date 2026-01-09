package com.hatomask.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceBoundingBox {
    @JsonProperty("xMin")
    private Double xMin;

    @JsonProperty("yMin")
    private Double yMin;

    private Double width;
    private Double height;
}
