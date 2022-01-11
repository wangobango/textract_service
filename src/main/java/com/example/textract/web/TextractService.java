package com.example.textract.web;

import com.example.textract.core.AnalyseDocument;
import com.example.textract.entity.Result;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.model.Block;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TextractService {

    private final AnalyseDocument analyseDocument;
    private Logger LOG;

    @Autowired
    public TextractService(AnalyseDocument analyseDocument) {
        this.analyseDocument = analyseDocument;
        this.analyseDocument.open();
        this.LOG = LoggerFactory.getLogger(this.getClass().getName());
    }

    @PreDestroy
    public void destroy() {
        this.analyseDocument.close();
    }

    public Result extractInfoFromInvoice(InputStream inputStream) throws IOException {
        ByteArrayInputStream byteArrayInputStream = extractImageFromDocument(inputStream);
        SdkBytes bytes = SdkBytes.fromInputStream(byteArrayInputStream);
        List<Block> docInfo = analyseDocument.analyzeDoc(bytes);

        String company = docInfo.get(11).text();
        String brutto = docInfo.get(52).text().split(" ")[0];
        Optional<List<String>> mapped = Optional.of(docInfo.stream()
                .map(elem -> {
                    if(elem.text() != null) {
                        return elem.text();
                    } else {
                        return "";
                    }
                })
                .collect(Collectors.toList()));

        Optional<String> nip = mapped.get()
                .stream()
                .filter(elem -> elem.contains("NIP"))
                .findFirst();
        String fixedNip = "";
        if(nip.isPresent()) {
            fixedNip = nip.get().split(":")[1];
        }
        Result result = new Result(fixedNip, company, Float.valueOf(brutto));
        LOG.info(result.toString());
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
