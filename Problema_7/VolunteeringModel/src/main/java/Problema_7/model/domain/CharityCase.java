package Problema_7.model.domain;

import Problema_7.model.domain.Entity;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.util.Objects;

@jakarta.persistence.Entity
@Table(name = "charity_case")
public class CharityCase extends Entity<Integer> {

    @Column(name = "case_name")
    @SerializedName("CaseName")
    private String case_name;

    @Column(name = "total_amount")
    @SerializedName("TotalAmount")
    private Double total_amount;

    public CharityCase(){

    }

    public CharityCase(int id, String case_name, Double total_amount) {
        this.setId(id);
        this.case_name = case_name;
        this.total_amount = total_amount;
    }

    public CharityCase(String case_name, Double total_amount) {
        this.case_name = case_name;
        this.total_amount = total_amount;
    }

    public String getCase_name() {
        return case_name;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setCase_name(String case_name) {
        this.case_name = case_name;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharityCase that = (CharityCase) o;
        return Objects.equals(case_name, that.case_name) && Objects.equals(total_amount, that.total_amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(case_name, total_amount);
    }

    @Override
    public String toString() {
        return "CharityCase{" +
                "case_name='" + case_name + '\'' +
                ", total_amount=" + total_amount +
                '}';
    }
}
