package domain.validator;

import domain.Donor;
import exceptions.ValidationException;

public class DonorValidator implements Validator {
    @Override
    public void validate(Object entity) throws ValidationException {
        if (entity instanceof Donor) {
            Donor donor = (Donor) entity;
            if (donor.getName().equals("")) {
                throw new ValidationException("Name can't be empty!");
            }
            if (donor.getSurname().equals("")) {
                throw new ValidationException("Surname can't be empty!");
            }
            if (donor.getPhoneNumber().equals("")) {
                throw new ValidationException("Phone number can't be empty!");
            }
            if (donor.getAddress().equals("")) {
                throw new ValidationException("Address can't be empty!");
            }
        } else {
            throw new ValidationException("The entity is not an instance of Donor!");
        }
    }
}
