package com.clinic;

class Patient {
    private String nic;
    private String name;
    private String email;
    private String phone;

    public Patient(String nic, String name, String email, String phone) {
        this.nic = nic;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getNic() { return nic; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

