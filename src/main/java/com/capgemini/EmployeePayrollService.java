package com.capgemini;

import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	};

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
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
		else
			System.out.println(employeePayrollList);
	}

	public void writeEmployeePayrollData(EmployeePayrollService.IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee payroll Roaster ro console\n");

	}

	public long countEntries(IOService fileIo) {
		return employeePayrollList.size();
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService dbIo) {
		if (dbIo.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDBService.readData();
		}
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		for (EmployeePayrollData data : employeePayrollList) {
			if (data.getName().equals(name)) {
				return data;
			}
		}
		return null;
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name, double salary) {
		for (EmployeePayrollData data : employeePayrollList) {
			if (data.getName().equals(name)) {
				if (Double.compare(data.getSalary(), salary) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public void updateEmployeeSalaryUsingPrepareStatement(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeDataUsingPreparedStatement(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService dbIo, LocalDate startDate,
			LocalDate endDate) {
		if (dbIo.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getEmployeeForGivenDateRange(startDate, endDate);
		}
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService dbIo) {
		if (dbIo.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getAverageSalaryByGender();
		}
		return null;
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
		EmployeePayrollData employeePayrollData = employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate,
				gender);
		employeePayrollList.add(employeePayrollData);
	}

	public void addEmployeeToPayrollERDiagram(String name, double salary, LocalDate startDate, String gender) {
		EmployeePayrollData employeePayrollData = employeePayrollDBService.addEmployeeToPayrollERDiagram(name, salary,
				startDate, gender);
		employeePayrollList.add(employeePayrollData);
	}

	public void removeEmployee(String name) {
		employeePayrollDBService.removeEmployee(name);
	}

	public void addEmployeesToPayroll(List<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDataList.forEach(employeePayrollData -> {
			System.out.println("Employee Being Added: " + employeePayrollData.getName());
			this.addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(),
					employeePayrollData.getStart(), employeePayrollData.getGender());
			System.out.println("Employee Added: " + employeePayrollData.getName());
		});
		System.out.println(this.employeePayrollList);
	}

	public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		employeePayrollDataList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				System.out.println("Employee Being Added: " + Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(),
						employeePayrollData.getStart(), employeePayrollData.getGender());
				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.getName());
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
}
