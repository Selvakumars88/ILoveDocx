package com.example.demo.Services;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.aspose.pdf.SaveFormat;
import com.aspose.words.Document;
@Service
public class ConvertDocToPdf {
public static final Logger log=Logger.getLogger(ConvertDocToPdf.class.getName());

public String convertFile(String fPath) {
	log.info("The control is inside the ConvertDocToPdf class");
	String filePath="";
	Path fi=null;
	try {
		if(fPath.endsWith(".pdf")) {
			log.info("i got file which is pdf ");
		fi=Files.createTempFile("sample", ".doc");
		filePath=convertPdfToDoc(fPath, fi.toString());
		}
		else if(fPath.endsWith(".doc")||fPath.endsWith(".docx")) {
			log.info("i got file which if doc");
			fi=Files.createTempFile("sample", ".pdf");
			filePath=convertdocToPdf(fPath,fi.toString());
		}
		return filePath;	
	} catch (IOException e) {
		log.warning("Error "+e.getMessage());
		return "error "+e.getMessage();
	}
	
	
}

private String convertdocToPdf(String fPath, String filePath) {
	try {
		Document doc=new Document(fPath);
		doc.save(filePath);
		log.info("The file is converted as PDF Succesfully");
		return filePath;
	} catch (Exception e) {
		log.warning("Error "+e.getMessage());
		return "error "+e.getMessage();
	}
}

private String convertPdfToDoc(String fPath, String filePath) {
	
	try (com.aspose.pdf.Document doc = new com.aspose.pdf.Document(fPath)) {
		doc.save(filePath,SaveFormat.DocX);
		log.info("The file is converted as Doc Succesfully");
	}
	return filePath;
}
}
