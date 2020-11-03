package com.capgemini;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import org.junit.*;

import com.capgemini.EmployeePayrollData;
import com.capgemini.EmployeePayrollService;
import com.capgemini.EmployeePayrollService.IOService;
import com.capgemini.EmployeePayrollService.IOService.*;
import com.google.gson.Gson;

import io.restassured.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollTest {

	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark ZuckenBerg", 100000.0) };
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printEmployeeData(IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}

	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		System.out.println(employeePayrollData);
		Assert.assertEquals(3, employeePayrollData.size());
	}

	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa", 3000000.00);
		Assert.assertTrue(result);
	}

	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalaryUsingPrepareStatement("Terisa", 2000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa", 2000000.00);
		Assert.assertTrue(result);
	}

	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(3, employeePayrollData.size());
	}

	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				averageSalaryByGender.get("M").equals(2000000.00) && averageSalaryByGender.get("F").equals(3000000.00));
	}

	public void givenNewEmployee_WhenAdded_ShouldSyncWityhDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00, LocalDate.now(), "M");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark", 5000000.00);
		Assert.assertTrue(result);
	}

	public void givenNewEmployee_WhenAddedToPayroll_ShouldSyncWityhDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayrollERDiagram("Glen", 5000000.00, LocalDate.now(), "M");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Glen", 5000000.00);
		Assert.assertTrue(result);
	}

	public void givenEmployee_WhenRemovedFromPayroll_ShouldSyncWityhDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.removeEmployee("Glen");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Glen", 5000000);
		Assert.assertTrue(result);
	}

	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 1000000.0, LocalDate.now()) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Durataion without Thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(24, employeePayrollService.countEntries(IOService.DB_IO));
	}

	public void given6Employees_WhenAddedToERDiagramDB_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 1000000.0, LocalDate.now()) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Durataion without Thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToERDBWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(24, employeePayrollService.countEntries(IOService.DB_IO));
	}

	public void given6Employees_WhenUpdatedDataInERDiagramImplementedDB_ShouldBeInSync() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Bill Gates", "M", 300000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mark Zuckerberg", "M", 400000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Sunder", "M", 700000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 2000000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 3000000.0, LocalDate.now()) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant threadStart = Instant.now();
		employeePayrollService.UpdateEmployeeDataInERDBWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(threadStart, threadEnd));
		int totalUpdated = 0;
		for (EmployeePayrollData data : Arrays.asList(arrayOfEmps)) {
			boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB(data.getName(), data.getSalary());
			if (result)
				totalUpdated++;
		}
		Assert.assertEquals(6, totalUpdated);
	}

	@Before
	public void Setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public void givenEmployeeInJSONServer_whenRetrieved_ShouldMatchTheCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}

	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employees");
		System.out.println("EMPLOYEE PAYROLL ENTRIES IN JSONServer:\n" + response.asString());
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmps;
	}

	public void givenNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] ArrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(ArrayOfEmps));
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "Mark Zukerberg", "M", 300000,
				LocalDate.now());
		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}

	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}

	@Test
	public void givenListOfNewEmployee_whenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] ArrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(ArrayOfEmps));
		EmployeePayrollData[] arrayOfNewEmps = { new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now()) };
		for (EmployeePayrollData employeePayrollData : Arrays.asList(arrayOfNewEmps)) {
			Response response = addEmployeeToJsonServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		}
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(6, entries);
	}

}
