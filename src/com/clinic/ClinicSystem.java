package com.clinic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                case 6 -> {
                    System.out.println("Exiting system. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println(MessageFormatter.error("Invalid choice. Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid input. Please enter a number."));
        }
    }

    private void bookAppointment() {
        String nic = promptInput("Enter NIC (minimum 9 characters): ", InputValidator::isValidNic);
        String name = promptInput("Enter Name (minimum 4 characters): ", InputValidator::isValidName);
        String email = promptInput("Enter Email Address: ", InputValidator::isValidEmail);
        String phone = promptInput("Enter Phone Number (10 digits): ", InputValidator::isValidPhone);

        Patient patient = new Patient(nic, name, email, phone);

        List<Dermatologist> dermatologists = appointmentService.getDermatologists();
        System.out.println(MessageFormatter.info("Available Dermatologists:"));
        for (int i = 0; i < dermatologists.size(); i++) {
            System.out.println((i + 1) + ". " + dermatologists.get(i).getName() + " (Available: " + dermatologists.get(i).getSchedule() + ")");
        }

        int doctorChoice = promptNumericInput("Select a dermatologist (enter number): ", 1, dermatologists.size(), scanner) - 1;
        Dermatologist selectedDoctor = dermatologists.get(doctorChoice);

        List<LocalDate> availableDates = generateNextAvailableDates(selectedDoctor.getAvailableDays(), 5);
        System.out.println(MessageFormatter.info("Available Dates:"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(dateFormatter));
        }

        int dateChoice = promptNumericInput("Select an available date by entering the corresponding number: ", 1, availableDates.size(), scanner) - 1;
        LocalDate appointmentDate = availableDates.get(dateChoice);

        List<LocalTime> availableTimes = generateTimeSlots(selectedDoctor.getStartTime(), selectedDoctor.getEndTime(), 15);
        System.out.println(MessageFormatter.info("Available Time Slots:"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i).format(timeFormatter));
        }

        int timeChoice = promptNumericInput("Select an available time slot by entering the corresponding number: ", 1, availableTimes.size(), scanner) - 1;
        LocalTime appointmentTime = availableTimes.get(timeChoice);

        System.out.println(MessageFormatter.info("The registration fee is LKR 500.00. Do you confirm the payment? (yes/no)"));
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println(MessageFormatter.error("Registration fee payment not confirmed. Appointment not booked."));
            return;
        }

        Appointment appointment = new Appointment(patient, selectedDoctor, appointmentDate.format(dateFormatter), appointmentTime.format(timeFormatter));
        appointmentService.bookAppointment(appointment);
    }

    private String promptInput(String message, Validator validator) {
        String input;
        do {
            System.out.print(MessageFormatter.prompt(message));
            input = scanner.nextLine();
        } while (!validator.validate(input));
        return input;
    }

    private int promptNumericInput(String message, int min, int max, Scanner scanner) {
        int input;
        while (true) {
            System.out.print(MessageFormatter.prompt(message));
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) break;
            } catch (NumberFormatException ignored) {}
            System.out.println(MessageFormatter.error("Invalid choice. Please try again."));
        }
        return input;
    }

    private List<LocalDate> generateNextAvailableDates(List<String> daysOfWeek, int numberOfDates) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        int count = 0;

        while (count < numberOfDates) {
            for (String day : daysOfWeek) {
                LocalDate nextDate = startDate.with(java.time.DayOfWeek.valueOf(day.toUpperCase()));
                if (!nextDate.isBefore(startDate) && count < numberOfDates) {
                    dates.add(nextDate);
                    count++;
                }
            }
            startDate = startDate.plusWeeks(1);
        }
        return dates;
    }

    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime time = startTime;
        while (time.isBefore(endTime)) {
            timeSlots.add(time);
            time = time.plusMinutes(intervalMinutes);
        }
        return timeSlots;
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
        int appointmentId = promptNumericInput("Enter Appointment ID to update: ", 1, Integer.MAX_VALUE, scanner);
        appointmentService.updateAppointment(appointmentId, scanner);
    }

    private void generateInvoice() {
        int appointmentId = promptNumericInput("Enter Appointment ID to generate invoice: ", 1, Integer.MAX_VALUE, scanner);
        invoiceService.generateInvoice(appointmentId);
    }
}

