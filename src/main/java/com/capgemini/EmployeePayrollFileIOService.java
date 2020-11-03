package com.capgemini;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.google.common.io.Files;

public class EmployeePayrollFileIOService {
	public static String PAYROLL_FILE_NAME = "payroll-file.txt";

	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		StringBuffer empBuffer = new StringBuffer();
		employeePayrollList.forEach(employee -> {
			String employeeDataString = employee.toString().concat("\n");
			empBuffer.append(employeeDataString);
		});
		/*
		 * try { //Files.write(empBuffer.toString().getBytes(),
		 * Paths.get(PAYROLL_FILE_NAME)); } catch (IOException e) { e.printStackTrace();
		 * 
		 * }
		 */
	}

	public void printData() {
		/*
		 * try { Files.lines(new
		 * File("payroll-file.txt").toPath()).forEach(System.out::println); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
	}

	public long countEntries() {
		long entries = 0;
		/*
		 * try { entries = Files.lines(new File("payroll-file.txt").toPath()).count(); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		return entries;
	}
}
