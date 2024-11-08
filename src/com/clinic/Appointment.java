package com.clinic;

class Appointment {
    private static int idCounter = 1;
    private int id;
    private Patient patient;
    private Dermatologist dermatologist;
    private String date;
    private String time;
    private boolean isPaid;

    public Appointment(Patient patient, Dermatologist dermatologist, String date, String time) {
        this.id = idCounter++;
        this.patient = patient;
        this.dermatologist = dermatologist;
        this.date = date;
        this.time = time;
        this.isPaid = false;
    }

    public void markAsPaid() { this.isPaid = true; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public Patient getPatient() { return patient; }
    public Dermatologist getDermatologist() { return dermatologist; }
    public boolean isPaid() { return isPaid; }

    @Override
    public String toString() {
        return """
            🏥🧾 Clinic Appointment Receipt
            ================================
            📅 Appointment ID : %d
            🧑 Patient Name   : %s
            👩‍⚕️ Dermatologist  : %s
            📆 Date           : %s
            ⏰ Time           : %s
            💳 Payment Status : %s
            ================================
            """.formatted(id, patient.getName(), dermatologist.getName(), date, time, (isPaid ? "Paid ✅" : "Pending ❌"));
    }

    public String updatedReceipt() {
        return """
            🏥🧾 Clinic Appointment Update Receipt
            ======================================
            📅 Appointment ID : %d
            🧑 Patient Name   : %s
            👩‍⚕️ Dermatologist  : %s
            📆 New Date       : %s
            ⏰ New Time       : %s
            💳 Payment Status : %s
            ======================================
            ✅ Appointment Updated Successfully!
            """.formatted(id, patient.getName(), dermatologist.getName(), date, time, (isPaid ? "Paid ✅" : "Pending ❌"));
    }
}

