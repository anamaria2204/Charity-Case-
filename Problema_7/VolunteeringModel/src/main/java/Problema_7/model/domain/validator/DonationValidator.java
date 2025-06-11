package Problema_7.model.domain.validator;

import Problema_7.model.domain.Donation;

public class DonationValidator implements Validator {
    @Override
    public void validate(Object entity) throws Exception {
        if (entity instanceof Donation) {
            Donation donation = (Donation) entity;
            if (donation.getDonor().getId() == null) {
                throw new Exception("Donor id can't be null!");
            }
            if (donation.getCharityCase().getId() == null) {
                throw new Exception("Charity case id can't be null!");
            }
            if (donation.getAmount() < 0) {
                throw new Exception("Amount can't be negative!");
            }
            if (donation.getDate().isAfter(java.time.LocalDateTime.now())) {
                throw new Exception("Date is invalid!");
            }
        } else {
            throw new Exception("The entity is not an instance of Donation!");
        }
    }
}
