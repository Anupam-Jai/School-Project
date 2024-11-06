package com.school.project.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.school.project.entity.Student;
import com.school.project.repository.StudentRepository;
import com.school.project.responseEntity.FileResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class StudentService {

	@Autowired
	StudentRepository studentRepository;

	public List<FileResponse> uploadFile(MultipartFile[] file) {
		List<FileResponse> fileResponseList = new ArrayList<>();
		for (MultipartFile fil : file) {
			FileResponse fileResponse = new FileResponse();
			fileResponse.setFileName(fil.getOriginalFilename());
			fileResponse.setFileSize(fil.getSize());
			fileResponse.setFileType(fil.getContentType());
			fileResponseList.add(fileResponse);
		}
		return fileResponseList;
	}

	@Transactional
	public String uploadExcelFile(MultipartFile excelDataFile) throws IOException {
		org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(
				excelDataFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i <= worksheet.getLastRowNum(); i++) {
			Row row = worksheet.getRow(i);
			if (row == null)
				continue; // Skip empty rows

			Student student = new Student();
			student.setRollNo((long) row.getCell(0).getNumericCellValue());
			student.setAge((long) row.getCell(1).getNumericCellValue());
			student.setClassName(row.getCell(2).getStringCellValue());

			Cell cell = row.getCell(3); // Assuming this is the DOB column
			if (cell != null) {
				if (cell.getCellType() == CellType.STRING) {
					String dateString = cell.getStringCellValue().trim();
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					try {
						Date date = dateFormat.parse(dateString);
						student.setDOB(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (cell.getCellType() == CellType.NUMERIC
						&& org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					// Handle case where the cell is a date type
					student.setDOB(cell.getDateCellValue());
				}
			}
			studentRepository.save(student);
		}
		workbook.close();
		return "Details Saved";

	}

	public List<Student> getAllStudent() {
		return studentRepository.findAll();
	}

	public void generateExcel(HttpServletResponse response) throws Exception {
		List<Student> studentList = studentRepository.findAll();

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Student Info");
		HSSFRow row = sheet.createRow(0);

		row.createCell(0).setCellValue("Roll NUmber");
		row.createCell(1).setCellValue("Age");
		row.createCell(2).setCellValue("Class");
		row.createCell(3).setCellValue("Date of Birth");

		HSSFCellStyle datacellStyle = workbook.createCellStyle();
		HSSFDataFormat dateFormate = workbook.createDataFormat();
		datacellStyle.setDataFormat(dateFormate.getFormat("dd-mm-yyyy"));

		int dataRowIndex = 1;

		for (Student student : studentList) {
			HSSFRow dataRow = sheet.createRow(dataRowIndex);
			dataRow.createCell(0).setCellValue(student.getRollNo());
			dataRow.createCell(1).setCellValue(student.getAge());
			dataRow.createCell(2).setCellValue(student.getClassName());

			if (student.getDOB() != null) {
				HSSFCell dateCell = dataRow.createCell(3);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
				LocalDateTime localDateTime = LocalDateTime.parse(student.getDOB().toString(), formatter);
				dateCell.setCellValue(java.sql.Date.valueOf(localDateTime.toLocalDate()));
				// dateCell.setCellValue(java.sql.Date.valueOf(LocalDateTime.parse(studentDetails.getDOB().toString()).toLocalDate()));
				dateCell.setCellStyle(datacellStyle);
			}
			dataRowIndex++;
		}
		for (int i = 0; i < 4; i++) {
			sheet.autoSizeColumn(i);
		}
		ServletOutputStream ops = response.getOutputStream();
		workbook.write(ops);
		workbook.close();
		ops.close();
	}

}
