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

        evaluationGrid.addColumn(EvaluationGridDto::getColumn1).setHeader("S.No").setFlexGrow(0);
        evaluationGrid.addColumn(EvaluationGridDto::getColumn2).setHeader("Evaluated Filename").setFlexGrow(2);
        evaluationGrid.addColumn(EvaluationGridDto::getColumn3).setHeader("Status").setFlexGrow(1);
        evaluationGrid.addThemeName("evaluation-grid");
        evaluationGrid.setHeight("250px");
        add(evaluationGrid);
    }

    public void renderResultData(String filename) {
        EvaluationGridDto dto1 = new EvaluationGridDto("1", filename, "Success");
        List<EvaluationGridDto> gridDtoList = new ArrayList<>();
        gridDtoList.addAll(Arrays.asList(dto1));
        evaluationGrid.setItems(gridDtoList);
        //evaluationGrid.getDataProvider().refreshAll();
    }

}
