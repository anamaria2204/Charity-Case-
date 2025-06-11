package domain.validator;

import domain.Donation;
import exceptions.ValidationException;

public class DonationValidator implements Validator {
    @Override
    public void validate(Object entity) throws ValidationException {
        if (entity instanceof Donation) {
            Donation donation = (Donation) entity;
            if (donation.getDonor().getId() == null) {
                throw new ValidationException("Donor id can't be null!");
            }
            if (donation.getCharityCase().getId() == null) {
                throw new ValidationException("Charity case id can't be null!");
            }
            if (donation.getAmount() < 0) {
                throw new ValidationException("Amount can't be negative!");
            }
            if (donation.getDate().isAfter(java.time.LocalDateTime.now())) {
                throw new ValidationException("Date is invalid!");
            }
        } else {
            throw new ValidationException("The entity is not an instance of Donation!");
        }
    }
}
