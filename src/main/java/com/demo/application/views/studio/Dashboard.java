package com.demo.application.views.studio;

import com.demo.application.Util.SessionField;
import com.demo.application.security.AuthenticatedUser;
import com.demo.application.views.components.upload.UploadComponent;
import com.demo.application.views.studio.Component.EvaluationStatusGrid;
import com.demo.application.views.studio.Component.EvaluateResultGrid;
import com.demo.application.views.studio.Component.TrainingResultGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import javax.annotation.security.PermitAll;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Route("")
@PermitAll
@CssImport(value = "./styles/vaadin-text-area.css", themeFor = "vaadin-text-area")
public class Dashboard extends VerticalLayout {

    private final AuthenticatedUser authenticatedUser;
    TextArea notificationArea = new TextArea();
    Button evaluateButton = new Button("Evaluate");
    Notification notification = new Notification();


    public Dashboard(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        //setWidth("fit-content");
        Button logout = new Button(new Icon(VaadinIcon.USER));
        logout.getElement().getStyle().set("position", "absolute").set("right", "10px").set("top", "10px")
                .set("background-color", "#e5e5e5").set("width", "20px").set("height", "30px");
        logout.addClickListener(event -> {
            authenticatedUser.logout();
        });

        UploadComponent uploadComponent = UploadComponent.builder()
                .maxFileSize(100 * 1024 * 1024)
                .build();
        uploadComponent.init();

        var trainButton = new Button("Train");
        trainButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        trainButton.getElement().getStyle().set("cursor", "pointer").set("margin-top", "10px").set("margin-left","1rem");

        notificationArea.setReadOnly(true);
        notificationArea.getElement().getStyle().set("background", "#f1f1f1e3").set("border-radius", "3px").set("color", "green").set("margin-left","1rem");
        notificationArea.setWidth("100%");
        notificationArea.setHeight("250px");
        notificationArea.addThemeName("notification-area");

        Label uploadLabel = new Label("Upload Training files");
        uploadLabel.getElement().getStyle().set("font-weight", "600");

        Div uploadLayout = new Div(uploadLabel, uploadComponent.getUpload(), trainButton, notificationArea);
        uploadLayout.setWidth("30%");

        TrainingResultGrid trainingResultGrid = new TrainingResultGrid();
        HorizontalLayout resultLayout = new HorizontalLayout(trainingResultGrid);
        resultLayout.getElement().getStyle().set("padding-left","1rem");

        HorizontalLayout attributionLayout = new HorizontalLayout(uploadLayout, resultLayout);
        attributionLayout.getElement().getStyle()
                .set("border-bottom", "1px solid #1c375a73").set("padding-bottom", "10px");
        attributionLayout.setWidth("100%");
        attributionLayout.setHeight("50%");

        // Evaluation Block
        VerticalLayout evaluationLayout = getEvaluationLayout();
        evaluationLayout.setWidth("100%");
        evaluationLayout.setHeight("50%");
        evaluationLayout.setPadding(false);
        updateNotification("Upload a file to Train it");

        uploadComponent.getUpload().addSucceededListener(event -> {
            updateNotification("File Uploaded success");
        });
        //System.out.println(uploadComponent.getUpload().addFinishedListener(finishedEvent -> finishedEvent.getFileName()));
        uploadComponent.getUpload().addFailedListener(uploadSuccess -> {
            updateNotification("File Upload success. Reason: Fail to load the  file.");
        });


        resultLayout.setVisible(false);
        trainButton.addClickListener(buttonClickEvent -> {
            updateNotification("Training started.");
            updateNotification("Training in-progress.....");
            SessionField.PROCESS_ID +=1;
            List<File> files = uploadComponent.getFiles();
            if (files.size() > 0) {
                String filename = files.get(0).getAbsolutePath();
                System.out.println("============files" + filename);
                trainingResultGrid.renderDeDupData(filename);
                trainingResultGrid.renderGroupData(filename);
                resultLayout.setVisible(true);
                evaluateButton.setEnabled(true);
                updateNotification("Training Completed.");
            }

        });

        add(attributionLayout, evaluationLayout, logout);
    }

    private VerticalLayout getEvaluationLayout() {

        UploadComponent uploadComponent = UploadComponent.builder()
                .maxFileSize(100 * 1024 * 1024)
                .build();
        uploadComponent.init();
        uploadComponent.getUpload().getElement().getStyle().set("width", "100%");


        evaluateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        evaluateButton.getElement().getStyle().set("cursor", "pointer").set("margin-left","1rem");
        evaluateButton.setEnabled(false);

        HorizontalLayout primaryUploadLayout = new HorizontalLayout();
        VerticalLayout uploadLayout = new VerticalLayout();

        Div evaluationLayout = new Div();
        evaluationLayout.getElement().getStyle().set("display", "flex").set("width", "100%");
        evaluationLayout.add(evaluateButton);

        Label evaluateLabel = new Label("Upload Evaluation files");
        evaluateLabel.getElement().getStyle().set("font-weight", "600");
        uploadLayout.add(evaluateLabel, uploadComponent.getUpload(), evaluationLayout);
        uploadLayout.getElement().getStyle().set("width", "42%");

        EvaluationStatusGrid evaluationstatusGrid = new EvaluationStatusGrid();
        primaryUploadLayout.add(uploadLayout, evaluationstatusGrid);
        primaryUploadLayout.setHeight("50%");
        primaryUploadLayout.setWidth("100%");

        Div secondaryGrid = new Div();
        secondaryGrid.setHeight("50%");
        secondaryGrid.setWidth("98%");
        secondaryGrid.getElement().getStyle().set("padding-left","2rem");


        // Download button action
        StreamResource resource = new StreamResource("Evaluated_result.csv",
                (InputStreamFactory) () -> {
                    try {
                        final File file = new File("/home/johnpaul.s@zucisystems.com/Downloads/Train_csv.csv");
                        return new FileInputStream(file);
                    } catch (Exception e) {
                        throw new RuntimeException("File Not Found");
                    }
                });
        final Anchor download = new Anchor(resource, "");
        download.getElement().setAttribute("download", true);
        download.add(new Button(new Icon(VaadinIcon.DOWNLOAD)));
        EvaluateResultGrid evaluateResultGrid = new EvaluateResultGrid();
        evaluateResultGrid.setPadding(false);
        secondaryGrid.add(download, evaluateResultGrid);


        /*Button downloadButton = new Button("Download", new Icon(VaadinIcon.DOWNLOAD));
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadButton.getElement().getStyle().set("margin-left", "10px").set("background-color", "#39c693");
        secondaryGrid.add(downloadButton, new EvaluateResultGrid());*/

        VerticalLayout mainLayout = new VerticalLayout(primaryUploadLayout, secondaryGrid);
        secondaryGrid.setVisible(false);
        evaluationstatusGrid.setVisible(false);

       /* evaluateButton.addClickListener(buttonClickEvent -> {
            List<File> files = uploadComponent.getFiles();
            if (files.size() > 0) {
                String filename = files.get(0).getAbsolutePath();
                System.out.println("============ Evaluate files" + filename);
                evaluationstatusGrid.renderResultData(filename);
            }
        });*/

        evaluateButton.addClickListener(event -> {
            List<File> files = uploadComponent.getFiles();
            if (files.size() > 0) {
                String filename = files.get(0).getAbsolutePath();
                evaluationstatusGrid.renderResultData(filename);
                evaluateResultGrid.setResultGridData(filename);
                secondaryGrid.setVisible(true);
                evaluationstatusGrid.setVisible(true);
            }
        });
        return mainLayout;
    }

    private void updateNotification(String str) {
        notificationArea.setValue(notificationArea.getValue() + "\n" + str);
        Notification.show(str);
    }
}
