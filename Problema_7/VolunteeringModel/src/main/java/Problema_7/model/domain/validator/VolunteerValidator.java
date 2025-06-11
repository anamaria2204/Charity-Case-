package Problema_7.model.domain.validator;

import Problema_7.model.domain.Volunteer;
import Problema_7.model.domain.validator.Validator;

public class VolunteerValidator implements Validator {
    @Override
    public void validate(Object entity) throws Exception {
        if (entity instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) entity;
            if (volunteer.getUsername() == null || volunteer.getUsername().equals("")) {
                throw new Exception("The username cannot be empty!");
            }
            if (volunteer.getPassword() == null || volunteer.getPassword().equals("")) {
                throw new Exception("The password cannot be empty!");
            }
        } else {
            throw new Exception("The entity is not an instance of Volunteer!");
        }
    }
}
