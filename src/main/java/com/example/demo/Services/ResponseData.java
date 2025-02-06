package com.example.demo.Services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
	private String fileName;
	private String fileType;
	private String downloadUrl;
	private long fileSize;
}
