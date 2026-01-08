package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceLandmark {
    private String name;
    private Double x;
    private Double y;
}
