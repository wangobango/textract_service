package com.example.textract.web;

import com.example.textract.entity.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class TextractController {

    @Autowired
    TextractService textractService;
    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/invoice", method = RequestMethod.POST)
    public ResponseEntity<String> processInvoice(@RequestParam("file") MultipartFile file) throws IOException {
        Result result = textractService.extractInfoFromInvoice(file.getInputStream());
        JsonNode node = mapper.valueToTree(result);
        return ResponseEntity.of(Optional.of(node.toPrettyString()));
    }

}
