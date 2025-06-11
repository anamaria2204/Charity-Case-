package Problema_7.model.domain.validator;

import Problema_7.model.domain.CharityCase;

public class CharityCaseValidator implements Validator {
    @Override
    public void validate(Object entity) throws Exception {
        if (entity instanceof CharityCase) {
            CharityCase charityCase = (CharityCase) entity;
            if (charityCase.getCase_name().equals("")) {
                throw new Exception("Name can't be empty!");
            }
            if (charityCase.getTotal_amount() < 0) {
                throw new Exception("Total amount must be a positive number!");
            }
        } else {
            throw new Exception("The entity is not an instance of CharityCase!");
        }
    }
}
