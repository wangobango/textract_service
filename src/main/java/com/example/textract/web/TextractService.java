package com.example.textract.web;

import com.example.textract.core.AnalyseDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.model.Block;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Service
public class TextractService {

    private final AnalyseDocument analyseDocument;

    @Autowired
    public TextractService(AnalyseDocument analyseDocument) {
        this.analyseDocument = analyseDocument;
        this.analyseDocument.open();
    }

    @PreDestroy
    public void destroy() {
        this.analyseDocument.close();
    }

    public String extractInfoFromInvoice(InputStream inputStream) throws IOException {
        ByteArrayInputStream byteArrayInputStream = extractImageFromDocument(inputStream);
        SdkBytes bytes = SdkBytes.fromInputStream(byteArrayInputStream);
        List<Block> docInfo = analyseDocument.analyzeDoc(bytes);

        String result = "empty";

        for (Block block : docInfo) {
            System.out.println("The block type is " + block.blockType().toString());
            result = block.blockType().toString();
        }
        return result;
    }

    private ByteArrayInputStream extractImageFromDocument(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);

        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 1000, ImageType.RGB);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIOUtil.writeImage(bim, "png", outputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());

        document.close();
        return byteArrayInputStream;
    }


}
