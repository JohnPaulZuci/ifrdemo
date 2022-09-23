package com.demo.application.views.studio.Component;

import com.demo.application.Util.CoproHandler;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.util.SharedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@CssImport(value = "./styles/vaadin-grid.css", themeFor = "vaadin-grid")
public class EvaluateResultGrid extends VerticalLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluateResultGrid.class);
    private final CoproHandler coproHandler = new CoproHandler();

    Grid<String[]> resultGrid = new Grid<String[]>();

    public EvaluateResultGrid() {
        resultGrid.addThemeName("evaluation-grid");
        resultGrid.setHeight("250px");
        add(resultGrid);
    }

    public void setResultGridData(String inFilePath) {
        try {
            final CoproHandler.EvalResultResponse responseEvalFile = coproHandler
                    .toEvaluateFile(inFilePath);
            LOGGER.info("Eval Respose:=== ", responseEvalFile.getOutputEvalFile());
            gridCsvImport(responseEvalFile.getOutputEvalFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void gridCsvImport(String csvFilePAth) {
        resultGrid.removeAllColumns();
        try {
            InputStreamReader csvFileReader = new InputStreamReader(
                    new FileInputStream(csvFilePAth),
                    StandardCharsets.UTF_8
            );

            CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader reader = new CSVReaderBuilder(csvFileReader).withCSVParser(parser).build();

            //Grid<String[]> resultGrid = new Grid<>();
            List<String[]> entries = reader.readAll();
            // Assume the first row contains headers
            String[] headers = entries.get(0);

            // Setup a grid with random data
            for (int i = 0; i < headers.length; i++) {
                final int columnIndex = i;
                String header = headers[i];
                String humanReadableHeader = SharedUtil.camelCaseToHumanFriendly(header);
                resultGrid.addColumn(str -> str[columnIndex]).setHeader(humanReadableHeader)
                        .getElement().getStyle().set("font-weight", "600");
                ;
            }
            resultGrid.setItems(entries.subList(1, entries.size()));
            add(resultGrid);
        } catch (IOException | CsvException e) {
            resultGrid.addColumn(nop -> "Unable to load CSV: " + e.getMessage()).setHeader("Failed to import CSV file");
        }
    }

}