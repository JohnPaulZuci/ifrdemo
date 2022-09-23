package com.demo.application.views.studio.Component;

import com.demo.application.views.studio.Dto.EvaluationGridDto;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@CssImport(value = "./styles/vaadin-grid.css", themeFor = "vaadin-grid")

public class EvaluationStatusGrid extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationStatusGrid.class);
    private final Grid<EvaluationGridDto> evaluationGrid = new Grid<>();

    public EvaluationStatusGrid() {

        evaluationGrid.addColumn(EvaluationGridDto::getColumn1).setHeader("S.No").setFlexGrow(0);
        evaluationGrid.addColumn(EvaluationGridDto::getColumn2).setHeader("Evaluated Filename").setFlexGrow(2);
        evaluationGrid.addColumn(EvaluationGridDto::getColumn3).setHeader("Status").setFlexGrow(1);
        evaluationGrid.addThemeName("evaluation-grid");
        evaluationGrid.setHeight("250px");
        add(evaluationGrid);
    }

    public void renderResultData(String filename) {
        List<EvaluationGridDto> collect = evaluationGrid.getDataProvider()
                .fetch(new Query<>())
                .collect(Collectors.toList());

        List<EvaluationGridDto> gridDtoList = new ArrayList<>(collect);
        EvaluationGridDto dto1 = new EvaluationGridDto(collect.size() + 1, filename, "Success");
        gridDtoList.add(dto1);
        evaluationGrid.setItems(gridDtoList);

        LOGGER.info("Result grid List provider {}", gridDtoList);
        //evaluationGrid.getDataProvider().refreshAll();
    }

}
