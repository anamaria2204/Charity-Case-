package Problema_7.model.domain.validator;

import Problema_7.model.domain.Donor;

public class DonorValidator implements Validator {
    @Override
    public void validate(Object entity) throws Exception {
        if (entity instanceof Donor) {
            Donor donor = (Donor) entity;
            if (donor.getName().equals("")) {
                throw new Exception("Name can't be empty!");
            }
            if (donor.getSurname().equals("")) {
                throw new Exception("Surname can't be empty!");
            }
            if (donor.getPhoneNumber().equals("")) {
                throw new Exception("Phone number can't be empty!");
            }
            if (donor.getAddress().equals("")) {
                throw new Exception("Address can't be empty!");
            }
        } else {
            throw new Exception("The entity is not an instance of Donor!");
        }
    }
}
