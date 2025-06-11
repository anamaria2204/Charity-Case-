package domain.validator;

import domain.Volunteer;
import exceptions.ValidationException;

public class VolunteerValidator implements Validator {
    @Override
    public void validate(Object entity) throws ValidationException {
        if (entity instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) entity;
            if (volunteer.getUsername() == null || volunteer.getUsername().equals("")) {
                throw new ValidationException("The username cannot be empty!");
            }
            if (volunteer.getPassword() == null || volunteer.getPassword().equals("")) {
                throw new ValidationException("The password cannot be empty!");
            }
        } else {
            throw new ValidationException("The entity is not an instance of Volunteer!");
        }
    }
}
