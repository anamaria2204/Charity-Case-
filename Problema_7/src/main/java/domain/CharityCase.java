package domain;

import java.util.Objects;

public class CharityCase extends Entity<Integer> {
    private String case_name;
    private Double total_amount;

    public CharityCase(int id, String case_name, Double total_amount) {
        this.setId(id);
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
