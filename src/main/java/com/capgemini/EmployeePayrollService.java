package com.capgemini;

import java.util.*;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	};

	private List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService() {
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		Scanner sc = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(sc);
		employeePayrollService.printEmployeeData(IOService.CONSOLE_IO);
	}

	public void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee ID");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter the Employee Name");
		String name = consoleInputReader.next();
		System.out.println("Enter the Employee Salary");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public void printEmployeeData(EmployeePayrollService.IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.CONSOLE_IO))
			System.out.println(employeePayrollList);
	}

	public void writeEmployeePayrollData(EmployeePayrollService.IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee payroll Roaster ro console\n");

	}

	public long countEntries(IOService fileIo) {
		return 0;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService dbIo) {
		if(dbIo.equals(IOService.DB_IO)) {
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		}
		return this.employeePayrollList;
	}

}
