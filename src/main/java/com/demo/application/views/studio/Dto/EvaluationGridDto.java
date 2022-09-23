package com.demo.application.views.studio.Dto;

import lombok.Data;

@Data
public class EvaluationGridDto {

    private int column1;

    private String column2;

    private String column3;

    public EvaluationGridDto(int column1, String column2, String column3) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
    }
}
