package Problema_7.model.domain.validator;

import Problema_7.model.domain.validator.Validator;
import Problema_7.model.domain.validator.ValidatorStrategy;

public interface Factory {
    Validator createValidator(ValidatorStrategy strategy);
}
