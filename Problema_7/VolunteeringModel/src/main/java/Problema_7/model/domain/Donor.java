package Problema_7.model.domain;

import Problema_7.model.domain.Entity;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.util.Objects;

@jakarta.persistence.Entity
@Table(name = "donor")
public class Donor extends Entity<Integer> {
    @Column(name = "name")
    @SerializedName("Name")
    private String name;

    @Column(name = "surname")
    @SerializedName("Surname")
    private String surname;

    @Column(name = "address")
    @SerializedName("Address")
    private String address;

    @Column(name = "phone_number")
    @SerializedName("PhoneNumber")
    private String phoneNumber;


    public Donor() {
    }

    public Donor(int id, String name, String surname, String address, String phoneNumber) {
        this.setId(id);
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Donor(String name, String surname, String address, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return Objects.equals(name, donor.name) && Objects.equals(surname, donor.surname) && Objects.equals(address, donor.address) && Objects.equals(phoneNumber, donor.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, address, phoneNumber);
    }

    @Override
    public String toString() {
        return "Donor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

