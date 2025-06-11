package domain.validator;

public interface Factory {
    Validator createValidator(ValidatorStrategy strategy);
}
