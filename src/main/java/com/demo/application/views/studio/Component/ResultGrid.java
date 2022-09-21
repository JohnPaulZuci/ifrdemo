package com.demo.application.views.studio.Component;

import com.demo.application.views.studio.Dto.ResultGridDto;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;
import java.util.List;


@CssImport(value = "./styles/vaadin-grid.css",themeFor = "vaadin-grid")
public class ResultGrid extends VerticalLayout {
    Grid<ResultGridDto> resultGrid = new Grid<ResultGridDto>();
    public ResultGrid(){

        resultGrid.addColumn(ResultGridDto::getSno).setHeader("S.No")
                .getElement().getStyle().set("font-weight","600");
        resultGrid.addColumn(ResultGridDto::getColumn1).setHeader("Column1")
                .getElement().getStyle().set("font-weight","600");
        resultGrid.addColumn(ResultGridDto::getColumn2).setHeader("De-duping")
                .getElement().getStyle().set("font-weight","600");
        resultGrid.addColumn(ResultGridDto::getColumn3).setHeader("Grouping")
                .getElement().getStyle().set("font-weight","600");

        resultGrid.addThemeName("evaluation-grid");
        setResultGridData();

        add(resultGrid);
    }

    public void setResultGridData(){
        ResultGridDto dto1=new ResultGridDto("1","data91","data12","data13");
        ResultGridDto dto2=new ResultGridDto("2","data21","data22","data23");
        ResultGridDto dto3=new ResultGridDto("3","data31","data32","data33");
        ResultGridDto dto4=new ResultGridDto("4","data41","data42","data43");
        ResultGridDto dto5=new ResultGridDto("5","data41","data42","data43");
        ResultGridDto dto6=new ResultGridDto("6","data41","data42","data43");
        ResultGridDto dto7=new ResultGridDto("7","data41","data42","data43");
        ResultGridDto dto8=new ResultGridDto("8","data41","data42","data43");

        resultGrid.setHeight("250px");
        List<ResultGridDto> gridDtoList = Arrays.asList(dto1,dto2,dto3,dto4,dto5,dto6,dto7,dto8);
        resultGrid.setItems(gridDtoList);
    }

}