package domain.validator;

import domain.CharityCase;
import exceptions.ValidationException;

public class CharityCaseValidator implements Validator {
    @Override
    public void validate(Object entity) throws ValidationException {
        if (entity instanceof CharityCase) {
            CharityCase charityCase = (CharityCase) entity;
            if (charityCase.getCase_name().equals("")) {
                throw new ValidationException("Name can't be empty!");
            }
            if (charityCase.getTotal_amount() < 0) {
                throw new ValidationException("Total amount must be a positive number!");
            }
        } else {
            throw new ValidationException("The entity is not an instance of CharityCase!");
        }
    }
}
