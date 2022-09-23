package com.demo.application.Util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class CoproHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoproHandler.class);

    private static final String AFR_COPRO = PropertyHandler.get("afr.copro.host");
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .callTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES).build();

    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    private final String destFilePath = PropertyHandler.get("afr.dest.file.path");

    public DedupeResponse toDedupe(final String filePath) throws Exception {
        ArrayList<String> ofl = new ArrayList<>();
        ofl.add("Dupe");
        ArrayList<String> df = new ArrayList<>();
        df.add("DOB");
        ArrayList<String> uf = new ArrayList<>();
        uf.add("PAN");
        ofl.add("Aadhar");
        ofl.add("Group");
        var requestBody = TrainFileRequest.builder().processId("" + SessionField.PROCESS_ID)
                .inputFile(filePath)
                .outputFile(destFilePath)
                .modelFile(destFilePath)
                .algorithmName("ANN")
                .templateName("DeDuplication")
                .imputerMethod("False")
                .outputFeaturesList(ofl)
                .dateFeatures(df)
                .uniqueFeatures(uf)
                .build();

        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder()
                .url(AFR_COPRO + "/cub/model/withoutjarowinkler")
                .post(RequestBody.create(body, JSON))
                .build();
        LOGGER.info("Given request body " + body);
        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);
            if (response.isSuccessful()) {
                String string = Objects.requireNonNull(response.body()).string();
                DedupeResponse dedupeResponse = MAPPER.readValue(string, DedupeResponse.class);
                //DedupeResponse dedupeResponse = MAPPER.readValue(string, DedupeResponse.class);
                LOGGER.info("Response content ===============================" + dedupeResponse.toString());
                return dedupeResponse;
            } else {
                LOGGER.info(" Error Response: " + response.message());
            }
        }
        throw new Exception("Copro failed for the given request");
    }

    public GroupResponse toGroupRequest(final String filePath) throws Exception {
        ArrayList<String> ofl = new ArrayList<>();
        ofl.add("Group");
        ArrayList<String> df = new ArrayList<>();
        df.add("DOB");
        ArrayList<String> uf = new ArrayList<>();
        uf.add("PAN");
        ofl.add("Aadhar");
        ofl.add("Group");
        var requestBody = TrainFileRequest.builder().processId("" + SessionField.PROCESS_ID)
                .inputFile(filePath)
                .outputFile(destFilePath)
                .modelFile(destFilePath)
                .algorithmName("RFC")
                .templateName("Group")
                .imputerMethod("False")
                .outputFeaturesList(ofl)
                .dateFeatures(df)
                .uniqueFeatures(uf)
                .build();

        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder()
                .url(AFR_COPRO + "/cub/model/groupmodel")
                .post(RequestBody.create(body, JSON))
                .build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                String string = Objects.requireNonNull(response.body()).string();
                GroupResponse groupResponse = MAPPER.readValue(string, GroupResponse.class);
                LOGGER.info("Response content ===============================" + groupResponse.toString());
                return groupResponse;
            } else {
                LOGGER.info(" Error Response: " + response.message());
            }
        }

        throw new Exception("Copro failed for the given request");
    }

    public EvalResultResponse toEvaluateFile(final String filePath) throws Exception {
        ArrayList<String> alg = new ArrayList<>();
        alg.add("ANN");
        alg.add("RFC");
        ArrayList<String> temname = new ArrayList<>();
        temname.add("DeDuplication");
        temname.add("Group");
        ArrayList<String> ofl = new ArrayList<>();
        ofl.add("Dupe");
        ArrayList<String> df = new ArrayList<>();
        df.add("DOB");
        ArrayList<String> uf = new ArrayList<>();
        uf.add("PAN");
        ofl.add("Aadhar");
        ofl.add("Group");
        var requestBody = EvalFileRequest.builder().processId("" + SessionField.PROCESS_ID)
                .inputFile(filePath)
                .outputFile(destFilePath)
                .modelFile(destFilePath)
                .algorithmName(alg)
                .templateName(temname)
                .imputerMethod("False")
                .outputFeaturesList(ofl)
                .dateFeatures(df)
                .uniqueFeatures(uf)
                .build();

        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder()
                .url(AFR_COPRO + "/cub/model/withoutjarowinklereval")
                .post(RequestBody.create(body, JSON))
                .build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                String string = Objects.requireNonNull(response.body()).string();
                EvalResultResponse evalResponse = MAPPER.readValue(string, EvalResultResponse.class);
                LOGGER.info("Response content ====" + evalResponse.toString());
                return evalResponse;
            } else {
                LOGGER.info(" Error Response: " + response.message());
            }
        }
        throw new Exception("Copro failed for the given request");
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrainFileRequest {
        @NotNull
        private String processId;
        @NotNull
        private String inputFile;
        @NotNull
        private String outputFile;
        @NotNull
        private String modelFile;
        @NotNull
        private String algorithmName;
        @NotNull
        private String templateName;
        @NotNull
        private String imputerMethod;
        @NotNull
        @Builder.Default
        private List<String> outputFeaturesList = new ArrayList<>();
        @NotNull
        @Builder.Default
        private List<String> dateFeatures = new ArrayList<>();
        @NotNull
        @Builder.Default
        private List<String> uniqueFeatures = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EvalFileRequest {
        @NotNull
        private String processId;
        @NotNull
        private String inputFile;
        @NotNull
        private String outputFile;
        @NotNull
        private String modelFile;
        @NotNull
        @Builder.Default
        private List<String> algorithmName = new ArrayList<>();
        @NotNull
        @Builder.Default
        private List<String> templateName = new ArrayList<>();
        @NotNull
        private String imputerMethod;
        @NotNull
        @Builder.Default
        private List<String> outputFeaturesList = new ArrayList<>();
        @NotNull
        @Builder.Default
        private List<String> dateFeatures = new ArrayList<>();
        @NotNull
        @Builder.Default
        private List<String> uniqueFeatures = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DedupeResponse {
        private String processId;
        private String templateName;
        private String tp;
        private String tn;
        private String fp;
        private String fn;
        private String confussionmatrix;
        private String modelFile;
        private String status;
        private String errormessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroupResponse {
        private String processId;
        private String templateName;
        private String tp;
        private String tn;
        private String fp;
        private String fn;
        private String confussion_Matrix;
        private String modelFile;
        private String status;
        private String error_message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EvalResultResponse {
        private String processId;
        private String outputEvalFile;
        private String status;
        private String errormessage;
    }
}
