package com.example.textract.core;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnalyseDocument {

    private final Region region = Region.US_EAST_1;
    private TextractClient textractClient;

    public void open() {
        textractClient = TextractClient
                .builder()
                .region(region)
                .build();
    }

    public void close() {
        textractClient.close();
    }

    public List<Block> analyzeDoc(SdkBytes doc) {
        Document newDoc = Document
                .builder()
                .bytes(doc)
                .build();

        List<FeatureType> featureTypes = new ArrayList<FeatureType>();

        featureTypes.add(FeatureType.FORMS);
        featureTypes.add(FeatureType.TABLES);

        AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder()
                .featureTypes(featureTypes)
                .document(newDoc)
                .build();

        AnalyzeDocumentResponse analyzeDocument = textractClient.analyzeDocument(analyzeDocumentRequest);

        return analyzeDocument.blocks();

    }


}