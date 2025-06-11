package Problema_7.model.domain;
import Problema_7.model.domain.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@jakarta.persistence.Entity
@Table(name = "donation")
public class Donation extends Entity<Integer> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_donor")
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_charity_case")
    private CharityCase charityCase;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private LocalDateTime date;

    public Donation(){

    }

    public Donation(Integer id, Donor donor, CharityCase charityCase, Double amount, LocalDateTime date) {
        this.setId(id);
        this.donor = donor;
        this.charityCase = charityCase;
        this.amount = amount;
        this.date = date;
    }

    public Donation(Donor donor, CharityCase charityCase, Double amount, LocalDateTime date) {
        this.donor = donor;
        this.charityCase = charityCase;
        this.amount = amount;
        this.date = date;
    }

    public Donor getDonor() {
        return donor;
    }

    public CharityCase getCharityCase() {
        return charityCase;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public void setCharityCase(CharityCase charityCase) {
        this.charityCase = charityCase;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return Objects.equals(donor, donation.donor) && Objects.equals(charityCase, donation.charityCase) && Objects.equals(amount, donation.amount) && Objects.equals(date, donation.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donor, charityCase, amount, date);
    }

    @Override
    public String toString() {
        return "Donation{" +
                "donor=" + donor +
                ", charityCase=" + charityCase +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}

