package cn.edu.shou.missive.service;

import org.apache.pdfbox.Overlay;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;


/**
 * Created by sqhe on 14-8-25.
 */
@Service
public class PdfService {

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;

    public String overLayerRedTitle()
    {
        PDDocument watermarkDoc = null;
        try {
            watermarkDoc = PDDocument.load(fileUploadDir+"redtitle.pdf");
            PDDocument realDoc = PDDocument.load(fileUploadDir+"chuanzhan001.pdf");
            Overlay overlay = new Overlay();
            overlay.overlay(watermarkDoc,realDoc);
            watermarkDoc.save(fileUploadDir+"document-red-title.pdf");
            return "document-red-title.pdf";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }

        return null;


    }

    public String pdfToText(String pdfFilePath)
    {
        PDDocument pdfDoc = null;
        try {
            pdfDoc = PDDocument.load(pdfFilePath);
            PDFTextStripper stripper = new PDFTextStripper();
            StringWriter textWriter = new StringWriter();
            stripper.writeText(pdfDoc,textWriter);
            String test = textWriter.toString();
            return textWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String imageToPdf()
    {



        return null;


    }






}
