package com.demo.application.views.studio.Component;


import com.demo.application.Util.CoproHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;


@RestController
public class TrainingResultGrid extends Div {


    private final CoproHandler coproHandler = new CoproHandler();

    Image deDuplingImage;
    Image groupingImage;
    private final FlexLayout groupingMainResult = new FlexLayout();
    private final FlexLayout deDupingMainResult = new FlexLayout();


    public TrainingResultGrid() {

        deDuplingImage = new Image();
        deDuplingImage.getElement().getStyle().set("width", "250px")
                .set("height", "180px")
                .set("object-fit", "contain");
        groupingImage = new Image();
        groupingImage.getElement().getStyle().set("width", "250px")
                .set("height", "180px")
                .set("object-fit", "contain");

        FlexLayout deDupingFlexLayout = new FlexLayout();
        deDupingFlexLayout.setAlignItems(FlexComponent.Alignment.END);
        deDupingMainResult.getElement().getStyle().set("overflow", "auto");

        deDupingFlexLayout.add(deDupingHeader(), deDupingMainResult, deDuplingImage);
        deDupingFlexLayout.getElement().getStyle().set("border", "1px solid #4d75a9b8")
                .set("padding", "5px")
                .set("margin-bottom", "10px")
                .set("border-radius", "5px");

        FlexLayout groupingFlexLayout = new FlexLayout();
        groupingFlexLayout.setAlignItems(FlexComponent.Alignment.END);
        groupingMainResult.getElement().getStyle().set("overflow", "auto");
        groupingFlexLayout.add(groupingHeader(), groupingMainResult, groupingImage);
        groupingFlexLayout.getElement().getStyle().set("border", "1px solid #4d75a9b8 ")
                .set("padding", "5px")
                .set("border-radius", "5px");

        setWidthFull();
        add(deDupingFlexLayout, groupingFlexLayout);
    }

    public void renderDeDupData(String infilepath) {
        try {
            final CoproHandler.DedupeResponse responseDedupe = coproHandler.toDedupe(infilepath);
            System.out.println("List=====================" + responseDedupe);

            deDupingMainResult.removeAll();
            deDupingMainResult.add(getDeDupingResult(responseDedupe));

            String imgSrc = Objects.requireNonNull(responseDedupe.getConfussionmatrix());
            //String imgSrc ="/home/johnpaul.s@zucisystems.com/Downloads/dedup_cfm.png";
            StreamResource resource = getStreamResource(imgSrc);
            deDuplingImage.setSrc(resource);
            deDuplingImage.getElement().getStyle().set("width", "250px")
                    .set("height", "180px")
                    .set("object-fit", "contain");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }    public void renderGroupData(String infilepath) {
        try {
            final CoproHandler.GroupResponse responseGroup = coproHandler.toGroupRequest(infilepath);
            System.out.println("List=====================" + responseGroup);

            groupingMainResult.removeAll();
            groupingMainResult.add(getGroupingResult(responseGroup));

            String imgSrc = Objects.requireNonNull(responseGroup.getConfussion_Matrix());
            //String imgSrc = "/home/johnpaul.s@zucisystems.com/Downloads/group_cfm.png";
            StreamResource resource = getStreamResource(imgSrc);
            deDuplingImage.setSrc(resource);
            deDuplingImage.getElement().getStyle().set("width", "250px")
                    .set("height", "180px")
                    .set("object-fit", "contain");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @NotNull
    private static StreamResource getStreamResource(String imgs) {
        StreamResource resource =
                new StreamResource("group_cfm.png", (InputStreamFactory) () -> {
                    try {
                        return new FileInputStream(imgs);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        return resource;
    }

    private Component deDupingHeader() {
        Div deDupingHeaderLayout = new Div();
        Label heading = new Label("De-duping");
        heading.getElement().getStyle().set("color", "red").set("font-weight", "600");
        deDupingHeaderLayout.add(heading);
        deDupingHeaderLayout.add(getHeaderCellDiv("True Positive"));
        deDupingHeaderLayout.add(getHeaderCellDiv("True Negative"));
        deDupingHeaderLayout.add(getHeaderCellDiv("False Positive"));
        deDupingHeaderLayout.add(getHeaderCellDiv("False Negative"));
        return deDupingHeaderLayout;
    }

    private Component groupingHeader() {
        Div groupingHeaderLayout = new Div();
        Label groupHeading = new Label("Grouping");
        groupHeading.getElement().getStyle().set("color", "red").set("font-weight", "600");
        groupingHeaderLayout.add(groupHeading);
        groupingHeaderLayout.add(getHeaderCellDiv("True Positive"));
        groupingHeaderLayout.add(getHeaderCellDiv("True Negative"));
        groupingHeaderLayout.add(getHeaderCellDiv("False Positive"));
        groupingHeaderLayout.add(getHeaderCellDiv("False Negative"));
        return groupingHeaderLayout;
    }

    private Component getDeDupingResult(CoproHandler.DedupeResponse responseDedupe) {
        Div columnDetailLayout = new Div();
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseDedupe.getTp())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseDedupe.getTn())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseDedupe.getFp())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseDedupe.getFn())));
        return columnDetailLayout;
    }

    private Component getGroupingResult(CoproHandler.GroupResponse responseGroup) {
        Div columnDetailLayout = new Div();
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseGroup.getTp())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseGroup.getTn())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseGroup.getFp())));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(responseGroup.getFn())));
        return columnDetailLayout;
    }


    private Component getResultDetailLayout() {
        FlexLayout resultDetailLayout = new FlexLayout();

        for (int i = 0; i < 10; i++) {
            resultDetailLayout.add(getColumnDetailLayout(i));

        }
        resultDetailLayout.getStyle().set("overflow", "auto");
        return resultDetailLayout;
    }

    private Component getColumnDetailLayout(final Integer i) {
        Div columnDetailLayout = new Div();
        columnDetailLayout.add(new Div());
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(1)));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(0)));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(1)));
        columnDetailLayout.add(getDetailCellDiv(String.valueOf(0)));
        return columnDetailLayout;
    }

    private Div getHeaderCellDiv(String headerValue) {
        Label headerLabel = new Label(headerValue);
        Div cellDiv = new Div(headerLabel);
        headerLabel.getElement().getStyle().set("white-space", "nowrap")
                .set("font-size", "14px")
                .set("font-family", "Inter")
                .set("font-size", "16px")
                .set("font-weight", "600");
        cellDiv.getElement().getStyle().set("border", "1px solid rgba(28, 55, 90, 0.16)")
                .set("border-radius", "4px")
                .set("background", "#EEF4FB")
                .set("padding", "5px")
                .set("margin", "5px");
        return cellDiv;
    }

    private Div getDetailCellDiv(String cellValue) {
        Label cellLabel = new Label(cellValue);
        cellLabel.getElement().getStyle().set("font-family", "Inter")
                .set("font-size", "16px")
                .set("font-weight", "400");
        Div cellDiv = new Div(cellLabel);
        cellDiv.getElement().getStyle().set("border", "1px solid rgba(28, 55, 90, 0.16)")
                .set("border-radius", "4px")
                .set("padding", "5px")
                .set("margin", "5px")
                .set("min-width", "130px");
        return cellDiv;
    }

}
