package com.clinic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class InputValidator {
    public static boolean isValidNic(String nic) { return nic != null && nic.length() >= 9; }
    public static boolean isValidName(String name) { return name != null && name.length() >= 4; }
    public static boolean isValidEmail(String email) { return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); }
    public static boolean isValidPhone(String phone) { return phone != null && phone.matches("\\d{10}"); }
}