package com.example.textract.web;

import com.example.textract.core.AnalyseDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.model.Block;

import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.List;

@Service
public class TextractService {

    private AnalyseDocument analyseDocument;

    @Autowired
    public TextractService(AnalyseDocument analyseDocument) {
        this.analyseDocument = analyseDocument;
        this.analyseDocument.open();
    }

    @PreDestroy
    public void destroy() {
        this.analyseDocument.close();
    }

    public String extractInfoFromInvoice(InputStream inputStream) {
        SdkBytes bytes = SdkBytes.fromInputStream(inputStream);
        List<Block> docInfo = analyseDocument.analyzeDoc(bytes);

        String result = "empty";

        for (Block block : docInfo) {
            System.out.println("The block type is " + block.blockType().toString());
            result = block.blockType().toString();
        }
        return result;
    }


}
