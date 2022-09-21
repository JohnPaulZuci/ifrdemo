package com.demo.application.Util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
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

    private final String tempFilePath = PropertyHandler.get("afr.temp.file.path");
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
        var requestBody = TrainFileRequest.builder().processId("100")
                .inputFile(filePath)
                .outputFile(destFilePath)
                .modelFile(destFilePath)
                .algorithmName("RFC")
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
        var requestBody = TrainFileRequest.builder().processId("200")
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

                //DedupeResponse dedupeResponse = MAPPER.readValue(string, DedupeResponse.class);
                LOGGER.info("Response content ===============================" + groupResponse.toString());
                return groupResponse;
            } else {
                LOGGER.info(" Error Response: " + response.message());
            }
        }

        throw new Exception("Copro failed for the given request");
    }

/*

    public Optional<String> doPhraseSearch(final List<String> filePaths, final String phrase) throws IOException, AlchemyException {

        var requestBody = PhraseSearchRequest.builder().inputFilePaths(filePaths).phrase(phrase).outputDir(String.format("%s/stitcher/", tempFilePath)).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/preprocess/phrase-search").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        98
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                final String string = Objects.requireNonNull(response.body()).string();
                final PreprocessResponse preprocessResponse = MAPPER.readValue(string, PreprocessResponse.class);
                return preprocessResponse.getPageCount() > 0 ? Optional.of(preprocessResponse.getProcessedFilePath()) : Optional.empty();
            }
        }


        throw new AlchemyException("Copro failed for the given request");
    }

    public String doBlankPageRemoval(final String filePath, final String outputDir) throws IOException, AlchemyException {

        var requestBody = PreprocessRequest.builder().inputFilePath(filePath).outputDir(outputDir).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/denoise/blank-page-remover").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                return MAPPER.readValue(Objects.requireNonNull(response.body()).bytes(), PreprocessResponse.class).getProcessedFilePath();
            }
        }


        throw new AlchemyException("Copro failed for the given request");
    }

    public List<String> pdfToImageConverter(final String filePath, final String outputDir) throws IOException, AlchemyException {

        var requestBody = PreprocessRequest.builder().inputFilePath(filePath).outputDir(outputDir).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/preprocess/pdf_to_image").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                return MAPPER.readValue(Objects.requireNonNull(response.body()).bytes(), PreprocessResponse.class).getProcessedFilePaths();
            }
        }


        throw new AlchemyException("Copro failed for the given request");
    }

    public String doAutoRotation(final String filePath, final String outputDir) throws IOException, AlchemyException {

        var requestBody = PreprocessRequest.builder().inputFilePath(filePath).outputDir(outputDir).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/denoise/autorotation").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        try (var response = call.execute()) {
            LOGGER.info("Response body" + response);

            if (response.isSuccessful()) {
                final ResponseBody responseBody = response.body();
                return MAPPER.readValue(Objects.requireNonNull(responseBody).bytes(), PreprocessResponse.class).getProcessedFilePath();
            }
        }


        throw new AlchemyException("Copro failed for the given request");
    }

    public List<KeyPair> doKvpAttribution(final String filePath, final List<String> attributes, final String modelPath) throws IOException, AlchemyException {
        var requestBody = AssetAttributionRequest.builder().attributes(attributes).inputFilePath(filePath).modelFilePath(modelPath).outputDir(String.format("%s/kvp/", tempFilePath)).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/attribution/kvp").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                final JsonNode attributionValue = MAPPER.readValue(content, AssetAttributionResponse.class).getAttributionValue();
                return KeyPair.toKeyPair(attributionValue);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public Content doTextAttribution(final String filePath) throws IOException, AlchemyException {
        var requestBody = AssetAttributionRequest.builder().inputFilePath(filePath).outputDir(String.format("%s/text/", tempFilePath)).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/attribution/text").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                return Content.builder().value(MAPPER.readValue(content, AssetAttributionResponse.class).getExtractedText()).build();
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public List<Table> doTableAttribution(final String filePath, final String modelPath) throws IOException, AlchemyException {
        var requestBody = AssetAttributionRequest.builder().inputFilePath(filePath).outputDir(String.format("%s/table/", tempFilePath)).modelFilePath(modelPath).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/attribution/table").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                final ArrayNode tableAttributionValue = MAPPER.readValue(content, AssetAttributionResponse.class).getTableAttributionValue();
                return Table.toTable(tableAttributionValue);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public AssetAttributionResponse doImageClassification(final String filePath, final String modelPath, final String outputDir, final List<String> labels) throws IOException, AlchemyException {

        var requestBody = AssetAttributionRequest.builder().inputFilePath(filePath)
                .outputDir(outputDir).modelFilePath(modelPath)
                .build();

        if (labels != null) {
            final List<String> sortedList = labels.stream().sorted().collect(Collectors.toList());
            requestBody.setLabels(sortedList);
        }

        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/classification/image").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                return MAPPER.readValue(content, AssetAttributionResponse.class);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public File getOxfordExcel(final String filePath, String outputDir) throws IOException, AlchemyException {

        var requestBody = AssetAttributionRequest.builder().inputFilePath(filePath).outputDir(outputDir).build();


        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/attribution/table_oxford").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);
        File file = new File(tempFilePath + UUID.randomUUID() + ".csv");
        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = "";
                LOGGER.info("Response body" + content);
                try (final FileOutputStream outputStream = new FileOutputStream(file)) {
                    outputStream.write(response.body().bytes());
                }
                return file;
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public KvpTrainingResponse doKvpAttributionTrain(final AssetAttributionTrainRequest requestBody) throws IOException, AlchemyException {

        requestBody.setOutputDir(String.format("%s/kvp-training/", tempFilePath));
        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/training/attribution/kvp").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                return MAPPER.readValue(content, KvpTrainingResponse.class);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public TablePositionTrainingResponse doTablePositionTraining(final TablePositionTrainingRequest requestBody) throws IOException, AlchemyException {

        requestBody.setOutputDir(String.format("%s/table-positions/", tempFilePath));
        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/training/attribution/table-with-position").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                return MAPPER.readValue(content, TablePositionTrainingResponse.class);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }

    public PixelClassificationTrainingResponse doPixelClassificationTraining(final AssetAttributionTrainRequest requestBody) throws IOException, AlchemyException {

        requestBody.setOutputDir(String.format("%s/pixel-training/", tempFilePath));
        final String body = MAPPER.writeValueAsString(requestBody);
        var request = new Request.Builder().url(INTICS_COPRO + "/training/classification/pixel").post(RequestBody.create(body, JSON)).build();

        LOGGER.info("Given request body " + body);

        var call = CLIENT.newCall(request);

        try (var response = call.execute()) {
            LOGGER.info("Response " + response);
            if (response.isSuccessful()) {
                var content = new String(Objects.requireNonNull(response.body()).bytes());
                LOGGER.info("Response body" + content);
                return MAPPER.readValue(content, PixelClassificationTrainingResponse.class);
            }
        }

        throw new AlchemyException("Copro failed for the given request ");
    }
*/

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
   /* @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AssetAttributionResponse {

        private ArrayNode tableAttributionValue;

        private JsonNode attributionValue;

        @Builder.Default
        private Map<String, Set<String>> classifiedAssets = new HashMap<>();

        @Builder.Default
        private Set<String> extractedAssetFilePaths = new HashSet<>();

        private String extractedText;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class AssetAttributionTrainRequest {


        @NotNull
        private List<String> inputFilePaths;
        @NotNull
        private String field;
        @NotNull
        private String outputDir;
        @NotNull
        private List<String> values;

        @Builder.Default
        private String restore = "store_true";

        @Builder.Default
        private Integer batchSize = 2;
        @Builder.Default
        private Integer epoch = 100;
        @Builder.Default
        private Integer earlyStopStep = 100;

        private List<PixelClassificationRequest> classifications;


    }



    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class PhraseSearchRequest {

        @NotNull
        private List<String> inputFilePaths;
        @NotNull
        private String phrase;
        @NotNull
        private String outputDir;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class PixelClassificationRequest {

        @NotNull
        private List<String> inputFilePaths;
        @NotNull
        private String label;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PreprocessResponse {

        @NotNull
        private String processedFilePath;

        @NotNull
        private List<String> processedFilePaths;

        @NotNull
        private Integer pageCount;

    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KvpTrainingResponse {

        private TrainingResult trainingResult;
        private String modelFilePath;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PixelClassificationTrainingResponse {

        private TrainingResult trainingResult;
        private String modelFilePath;
        private List<String> labels;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainingResult {

        private Integer epoch;
        private Integer totalEpoch;
        private Double trainingLoss;
        private Double validationLoss;
        private String processStart;
        private String processStop;
        private Double timeTaken;

    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class TablePositionTrainingRequest {

        @NotNull
        private String inputFilePath;

        @NotNull
        private String outputDir;

        @Builder.Default
        private String restore = "store_true";

        @Builder.Default
        private Integer batchSize=10;

        @Builder.Default
        private Integer steps=10;

        @NotNull
        private Position tablePosition;

        @NotNull
        private Position tableHeaderPosition;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TablePositionTrainingResponse {

        private TrainingResult trainingResult;
        private String modelFilePath;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HeaderPosition {

        private Double xAxis;
        private Double yAxis;
        private Double width;
        private Double height;

    }

*/
}
