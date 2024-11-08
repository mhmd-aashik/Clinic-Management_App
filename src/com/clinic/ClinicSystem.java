package com.clinic;

import java.util.List;
import java.util.Scanner;

class ClinicSystem {
    private final AppointmentService appointmentService;
    private final InvoiceService invoiceService;
    private final Scanner scanner = new Scanner(System.in);

    public ClinicSystem(AppointmentService appointmentService, InvoiceService invoiceService) {
        this.appointmentService = appointmentService;
        this.invoiceService = invoiceService;
    }

    public void run() {
        while (true) {
            showMenu();
        }
    }

    private void showMenu() {
        System.out.println("\n--- Clinic Management System ---");
        System.out.println(MessageFormatter.info("1. Book Appointment"));
        System.out.println(MessageFormatter.info("2. View Appointments"));
        System.out.println(MessageFormatter.info("3. Search Appointment"));
        System.out.println(MessageFormatter.info("4. Update Appointment"));
        System.out.println(MessageFormatter.info("5. Generate Invoice"));
        System.out.println(MessageFormatter.info("6. Exit"));
        System.out.print(MessageFormatter.prompt("Choose an option: "));

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> bookAppointment();
                case 2 -> viewAppointments();
                case 3 -> searchAppointment();
                case 4 -> updateAppointment();
                case 5 -> generateInvoice();
                case 6 -> System.exit(0);
                default -> System.out.println(MessageFormatter.error("Invalid choice. Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid input. Please enter a number."));
        }
    }

    private void bookAppointment() {
        System.out.print(MessageFormatter.prompt("Enter NIC (minimum 9 characters): "));
        String nic = scanner.nextLine();
        if (!InputValidator.isValidNic(nic)) {
            System.out.println(MessageFormatter.error("NIC should be at least 9 characters."));
            return;
        }

        System.out.print(MessageFormatter.prompt("Enter Name (minimum 4 characters): "));
        String name = scanner.nextLine();
        if (!InputValidator.isValidName(name)) {
            System.out.println(MessageFormatter.error("The Name should be at least 4 characters long."));
            return;
        }

        System.out.print(MessageFormatter.prompt("Enter Email Address: "));
        String email = scanner.nextLine();
        if (!InputValidator.isValidEmail(email)) {
            System.out.println(MessageFormatter.error("Invalid email address. Please enter a valid format."));
            return;
        }

        System.out.print(MessageFormatter.prompt("Enter Phone Number (10 digits): "));
        String phone = scanner.nextLine();
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println(MessageFormatter.error("Invalid phone number. It should be exactly 10 digits."));
            return;
        }

        Patient patient = new Patient(nic, name, email, phone);

        // Select Dermatologist
        List<Dermatologist> dermatologists = appointmentService.getDermatologists();
        System.out.println(MessageFormatter.info("Available Dermatologists:"));
        for (int i = 0; i < dermatologists.size(); i++) {
            System.out.println((i + 1) + ". " + dermatologists.get(i).getName() + " (Available: " + dermatologists.get(i).getSchedule() + ")");
        }
        System.out.print(MessageFormatter.prompt("Select a dermatologist (enter number): "));
        int doctorChoice;
        try {
            doctorChoice = Integer.parseInt(scanner.nextLine());
            if (doctorChoice < 1 || doctorChoice > dermatologists.size()) {
                System.out.println(MessageFormatter.error("Invalid choice. Appointment not booked."));
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid input. Appointment not booked."));
            return;
        }
        Dermatologist selectedDoctor = dermatologists.get(doctorChoice - 1);

        // Enter Date and Time
        System.out.print(MessageFormatter.prompt("Enter Appointment Date (YYYY-MM-DD) for example 2024-12-01: "));
        String date = scanner.nextLine();
        if (!InputValidator.isValidFutureDate(date)) {
            System.out.println(MessageFormatter.error("Invalid date. Please enter a date today or in the future."));
            return;
        }

        System.out.print(MessageFormatter.prompt("Enter Time (HH:MM) for example 08:12 : "));
        String time = scanner.nextLine();
        if (!InputValidator.isValidTime(time)) {
            System.out.println(MessageFormatter.error("Invalid time format. Appointment not booked."));
            return;
        }

        // Confirm Registration Payment
        System.out.println(MessageFormatter.info("The registration fee is LKR 500.00. Do you confirm the payment? (yes/no)"));
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println(MessageFormatter.error("Registration fee payment not confirmed. Appointment not booked."));
            return;
        }

        Appointment appointment = new Appointment(patient, selectedDoctor, date, time);
        appointmentService.bookAppointment(appointment);
    }

    private void viewAppointments() {
        System.out.println(MessageFormatter.info("List of Appointments:"));
        appointmentService.viewAppointments().forEach(System.out::println);
    }

    private void searchAppointment() {
        System.out.print(MessageFormatter.prompt("Enter Patient Name or Appointment ID to search: "));
        String query = scanner.nextLine();
        appointmentService.searchAppointment(query).forEach(System.out::println);
    }

    private void updateAppointment() {
        System.out.print(MessageFormatter.prompt("Enter Appointment ID to update: "));
        try {
            int appointmentId = Integer.parseInt(scanner.nextLine());
            appointmentService.updateAppointment(appointmentId, scanner);
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Please enter a valid numeric ID."));
        }
    }

    private void generateInvoice() {
        System.out.print(MessageFormatter.prompt("Enter Appointment ID to generate invoice: "));
        try {
            int appointmentId = Integer.parseInt(scanner.nextLine());
            invoiceService.generateInvoice(appointmentId);
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid ID. Please enter a numeric ID."));
        }
    }
}

