package com.demo.application.views.studio.Dto;


import lombok.Data;

@Data
public class ResultGridDto {

    private String sno;
    private String column1;
    private String column2;
    private String column3;

    public ResultGridDto(String sno, String column1, String column2, String column3) {
        this.sno = sno;
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;

    }
}
