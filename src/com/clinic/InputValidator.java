package com.clinic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class InputValidator {
    public static boolean isValidNic(String nic) { return nic != null && nic.length() >= 9; }
    public static boolean isValidName(String name) { return name != null && name.length() >= 4; }
    public static boolean isValidEmail(String email) { return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); }
    public static boolean isValidPhone(String phone) { return phone != null && phone.matches("\\d{10}"); }
    public static boolean isValidDate(String date) { return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}"); }
    public static boolean isValidTime(String time) { return time != null && time.matches("\\d{2}:\\d{2}"); }

    public static boolean isValidFutureDate(String date) {
        try {
            LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.now();
            return !inputDate.isBefore(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

