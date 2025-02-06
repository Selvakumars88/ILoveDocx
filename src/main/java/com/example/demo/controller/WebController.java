package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.Services.ConvertDocToPdf;
import com.example.demo.Services.ResponseData;

@RestController
@RequestMapping("/")
public class WebController {
	private static final Logger log=Logger.getLogger(WebController.class.getName());
	@Autowired
	private final ConvertDocToPdf docService;
	private Map<String, String> map=new HashMap<String, String>();
	
	public WebController(ConvertDocToPdf docService) {
		super();
		this.docService = docService;
	}
	@GetMapping("index")
	public ModelAndView getIndex() {
		log.info("welcoe to getIndex()");
		ModelAndView mav=new ModelAndView("index");
		return mav;
	}
	@GetMapping("/upload")
	public ModelAndView uploadFile() {
		log.info("welcome to uploadFile()");
		return new ModelAndView("upload");
	}
	
	@PostMapping("/download")
	public ResponseData fileUploader(@RequestParam("file") MultipartFile file) {
		log.info("the control  is inside the fileUploader method of controller class");
		String conPath="";
		String downloadUri="";
		File files=null;
		File fi=null;
		try {
			if(!file.isEmpty()) {
			fi=File.createTempFile("file", file.getOriginalFilename());
			file.transferTo(fi);
			String fPAth=fi.getAbsolutePath();
			log.info("The file saved at "+fPAth);
			if(fPAth.endsWith(".doc")||fPAth.endsWith(".docx")) 
				conPath=docService.convertFile(fPAth);
			else if(fPAth.endsWith(".pdf"))
				conPath=docService.convertFile(fPAth);
				
			files=new File(conPath);
			downloadUri=ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/downloadFile").toUriString();
			
			log.info("the new file saved at "+conPath);
			map.put("filePath", conPath);
			log.info("the file is exist "+map.get("filePath"));
			return new ResponseData(files.getName(), files.getAbsolutePath(), downloadUri, files.getTotalSpace());
			}
			else
				return new ResponseData("Error", "No File Uploaded", "", 0L);
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("Warnig : "+e.getMessage());
			return new ResponseData("Error ","File uploaded failed due to an error: "+e.getMessage(), "",0L);
		}
		
		
		
	}
	@GetMapping("/downloadFile")
	public ResponseEntity<byte[]> downLoadFile(){
		File file=new File(map.get("filePath"));
		log.info("the control  is inside the downloadFIle method of controller class excuting the getmappingRequest");
		log.info("i got the file which is "+map.get("filePath"));
		
		if(!file.exists()) {
			log.warning("File Not Found :"+file.getAbsolutePath());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("File Not Found".getBytes());
		}
		
		byte[] fileContent = null;
		String contentType="";
		try {
			fileContent = Files.readAllBytes(file.toPath());
			 contentType=Files.probeContentType(file.toPath());
			 if(contentType==null) {
				 contentType=MediaType.APPLICATION_OCTET_STREAM_VALUE;
			 }
		} catch (IOException e) {
			log.warning("Error message "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(("Error reading the file: "+e.getMessage()).getBytes());
		}
	
		
	
		HttpHeaders headers=new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE,contentType);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.getName()).build().toString());
		return ResponseEntity.ok().headers(headers).body(fileContent);
	}
}
