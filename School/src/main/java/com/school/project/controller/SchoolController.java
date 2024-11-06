package com.school.project.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.school.project.entity.Student;
import com.school.project.responseEntity.FileResponse;
import com.school.project.responseEntity.StudentResponse;
import com.school.project.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("upload/")
public class SchoolController {

    private static final Logger logger = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    private StudentService studentService;
    
    @GetMapping("/getAll")
    public List<com.school.project.responseEntity.StudentResponse> getAllStudentDetails(){
    	List<Student> studentList= studentService.getAllStudent();
    	List<StudentResponse> studentResList= new ArrayList<>();
    			studentList.stream().forEach(student -> studentResList.add(new StudentResponse(student))
    			);
    	return studentResList;
    }

    @PostMapping("/multiple-file-upload")
    public ResponseEntity<List<FileResponse>> handleMultipleFileUpload(
            @RequestParam("files") MultipartFile[] files) {
        logger.info("Received {} files for upload", files.length);

        try {
            List<FileResponse> responses = studentService.uploadFile(files);
            logger.info("Successfully uploaded {} files", responses.size());
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/import")
    public String excelDatatoDB(@RequestParam("excelDataFile") MultipartFile excelDataFile) throws IOException  {
        return studentService.uploadExcelFile(excelDataFile);
}
    @GetMapping("/downloadExcel")
    public void generateExcel(HttpServletResponse response) throws Exception{
    	  studentService.generateExcel(response);
    }
}