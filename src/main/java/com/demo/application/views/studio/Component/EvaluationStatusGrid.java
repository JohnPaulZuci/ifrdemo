package com.demo.application.views.studio.Component;

import com.demo.application.views.studio.Dto.EvaluationGridDto;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@CssImport(value = "./styles/vaadin-grid.css", themeFor = "vaadin-grid")

public class EvaluationStatusGrid extends VerticalLayout {
    private final Grid<EvaluationGridDto> evaluationGrid = new Grid<EvaluationGridDto>();

    public EvaluationStatusGrid() {

        evaluationGrid.addColumn(EvaluationGridDto::getColumn1).setHeader("S.No");
        evaluationGrid.addColumn(EvaluationGridDto::getColumn2).setHeader("Evaluated Filename");
        evaluationGrid.addColumn(EvaluationGridDto::getColumn3).setHeader("Status");
        evaluationGrid.addThemeName("evaluation-grid");
        evaluationGrid.setHeight("250px");
        add(evaluationGrid);
    }

    public void renderResultData(String filename) {
        EvaluationGridDto dto1 = new EvaluationGridDto("1", "data12", "data13");
        EvaluationGridDto dto2 = new EvaluationGridDto("2", "data22", "data23");
        EvaluationGridDto dto3 = new EvaluationGridDto("3", "data32", "data33");
        EvaluationGridDto dto4 = new EvaluationGridDto("4", "data44", "data43");
        List<EvaluationGridDto> gridDtoList = new ArrayList<>();
        gridDtoList.addAll(Arrays.asList(dto1, dto2, dto3));

        evaluationGrid.setItems(gridDtoList);
        gridDtoList.add(dto4);
        evaluationGrid.getDataProvider().refreshAll();
        ;
    }

}
