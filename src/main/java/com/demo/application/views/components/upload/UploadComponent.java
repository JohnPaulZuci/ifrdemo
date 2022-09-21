package com.demo.application.views.components.upload;


import com.demo.application.Util.PropertyHandler;
import com.demo.application.Util.ViewUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.dom.DomEventListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = false)
//@CssImport("./styles/views/Box/Upload.css")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadComponent extends Div {
    private final String tempFilePath = PropertyHandler.get("afr.temp.file.path");
    private final Set<String> files = new HashSet<>();
    @Builder.Default
    private final FlexLayout resultLayout = new FlexLayout();
    @Builder.Default
    private MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    @Builder.Default
    private Image image = new Image();
    private Integer maxFileSize;
    private String uploadLocation;
    @Builder.Default
    private boolean list = true;
    @Builder.Default
    private Upload upload = new Upload();

    public void init() {
        removeAll();
        this.buffer = new MultiFileMemoryBuffer();
        addClassName("centered");
        createUpload();
    }

    private void createUpload() {
        if (ViewUtil.isPhone()) {
            add(getUploadLayout(true));
        } else {
            add(getUploadLayout(false));
        }
    }

    private Component getUploadLayout(final boolean isPhone) {
        var parentDiv = new Div();
        var childDiv = new Div();
        parentDiv.addClassName("parent-div");
        childDiv.addClassName("child-div");
        var uploadBtn = getButton();
        uploadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var heading = new H4("Upload Forms");
        var acceptedFiles = new Paragraph("Accepted file formats : .png,.jpg,.jpeg,.pdf");
        var maxSizeInfo = new Paragraph("Max File Size : " + (maxFileSize / (1024 * 1024)) + "MB");
        acceptedFiles.getStyle().set("color", "var(--lumo-secondary-text-color)");
        maxSizeInfo.getStyle().set("color", "var(--lumo-secondary-text-color)");

        if (isPhone) {
            upload = getUploadComponent(uploadBtn);
            upload.getElement().setAttribute("capture", "environment");
            childDiv.add(heading, acceptedFiles, maxSizeInfo, upload);
        } else {
            upload = getUploadComponent(uploadBtn);
            childDiv.add(heading, acceptedFiles, maxSizeInfo, upload);
        }
        parentDiv.add(childDiv);

        return parentDiv;
    }

    private Button getButton() {
        Button button = new Button("Upload");
        button.getElement().getStyle().set("cursor", "pointer");
        return button;
    }

    private Upload getUploadComponent(final Button uploadBtn) {
        upload = new Upload(buffer);
        upload.setUploadButton(uploadBtn);
        upload.setDropAllowed(true);
        final UnorderedList fileList = upload.getFileList();
        fileList.setVisible(list);

        var i18N = new UploadExamplesI18N();
        upload.addSucceededListener(succeededEvent -> {
            var fileName = succeededEvent.getFileName();
            var inputStream = buffer.getInputStream(fileName);
            try {
                final String pathname = tempFilePath + fileName;
                var file = new File(pathname);
                final File parentDir = new File(tempFilePath);
                if (!parentDir.exists()) {
                    Files.createDirectories(parentDir.toPath());
                }
                file.createNewFile();
                FileUtils.copyInputStreamToFile(inputStream, file);
                files.add(pathname);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultLayout.removeAll();
            resultLayout.setVisible(true);
        });
        upload.getElement().addEventListener("file-remove", (DomEventListener) domEvent -> {
            try {
                final String pathname = uploadLocation + domEvent.getEventData().getString("event.detail.file.name");
                Files.deleteIfExists(Paths.get(pathname));
                files.remove(pathname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addEventData("event.detail.file.name");
        upload.addFileRejectedListener(fileRejectedEvent -> {
            var notification = Notification.show(fileRejectedEvent.getErrorMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        i18N.getError().setFileIsTooBig("The file exceeds the maximum allowed size of 10MB.");
        i18N.getError().setIncorrectFileType("The provided file does not have the correct format. Please provide a suggested document.");
        upload.setI18n(i18N);
        return upload;
    }

    public List<File> getFiles() {
        return files.stream()
                .map(File::new)
                .filter(File::exists)
                .collect(Collectors.toUnmodifiableList());
    }


    public Upload getUpload() {
        return upload;
    }

    public Image getImage() {
        return image;
    }
}
