package com.clinic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class AppointmentService {
    private final AppointmentRepository repository;
    private final List<Dermatologist> dermatologists;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
        this.dermatologists = List.of(
                new Dermatologist("Dr. Silva", List.of("MONDAY", "WEDNESDAY", "FRIDAY"), LocalTime.of(9, 0), LocalTime.of(17, 0)),
                new Dermatologist("Dr. Perera", List.of("SATURDAY"), LocalTime.of(10, 0), LocalTime.of(15, 0)),
                new Dermatologist("Dr. Fernando", List.of("TUESDAY", "THURSDAY", "SATURDAY"), LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new Dermatologist("Dr. Wijeratne", List.of("MONDAY", "THURSDAY", "SUNDAY"), LocalTime.of(13, 0), LocalTime.of(18, 0))
        );
    }

    public List<Dermatologist> getDermatologists() { return dermatologists; }

    public void bookAppointment(Appointment appointment) {
        repository.save(appointment);
        System.out.println(appointment);
        System.out.println(MessageFormatter.success("Appointment successfully booked!"));
    }

    public List<Appointment> viewAppointments() {
        return repository.findAll();
    }

    public List<Appointment> searchAppointment(String query) {
        return repository.search(query);
    }

    public void updateAppointment(int appointmentId, Scanner scanner) {
        Appointment appointment = repository.findById(appointmentId);
        if (appointment == null) {
            System.out.println(MessageFormatter.error("Appointment not found."));
            return;
        }

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

        appointment.setDermatologist(selectedDoctor);
        appointment.setDate(appointmentDate.format(dateFormatter));
        appointment.setTime(appointmentTime.format(timeFormatter));

        System.out.println(MessageFormatter.success("Appointment updated successfully."));
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
}
