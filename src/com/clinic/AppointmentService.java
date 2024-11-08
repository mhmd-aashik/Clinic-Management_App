package com.clinic;

import java.util.List;
import java.util.Scanner;

class AppointmentService {
    private final AppointmentRepository repository;
    private final List<Dermatologist> dermatologists = List.of(
            new Dermatologist("👨‍⚕️ Dr. Silva", "Mon, Wed, Fri"),
            new Dermatologist("👨‍⚕️ Dr. Perera", "Sat"),
            new Dermatologist("👩‍⚕️ Dr. Fernando", "Tue, Thu, Sat"),
            new Dermatologist("👩‍⚕️ Dr. Wijeratne", "Mon, Thu, Sun")
    );

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public List<Dermatologist> getDermatologists() {
        return dermatologists;
    }

    public void bookAppointment(Appointment appointment) {
        repository.save(appointment);
        System.out.println("\n📜🧾 APPOINTMENT RECEIPT 🧾📜");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🆔 Appointment ID: " + appointment.getId());
        System.out.println("👤 Patient Name  : " + appointment.getPatient().getName());
        System.out.println("🪪 NIC           : " + appointment.getPatient().getNic());
        System.out.println("📧 Email         : " + appointment.getPatient().getEmail());
        System.out.println("📞 Phone         : " + appointment.getPatient().getPhone());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👨‍⚕️ Dermatologist: " + appointment.getDermatologist().getName());
        System.out.println("📅 Date          : " + appointment.getDate());
        System.out.println("🕒 Time          : " + appointment.getTime());
        System.out.println("💵 Registration Fee: LKR 500.00 (Paid)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎉 " + MessageFormatter.success("Appointment successfully booked!"));
        System.out.println("📅 We look forward to seeing you on your appointment date!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
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

        System.out.print(MessageFormatter.prompt("Enter new Date (YYYY-MM-DD) for example 2024-12-01: "));
        String newDate = scanner.nextLine();

        System.out.print(MessageFormatter.prompt("Enter new Time (HH:MM) for example 08:00 : "));
        String newTime = scanner.nextLine();

        appointment.setDate(newDate);
        appointment.setTime(newTime);
        System.out.println(appointment.updatedReceipt());
    }
}

