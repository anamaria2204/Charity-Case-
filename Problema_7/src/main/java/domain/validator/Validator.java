package domain.validator;

import exceptions.ValidationException;

public interface Validator<T> {

    /**
     * validate method
     *
     * @param entity - verify if an entity is valid
     */

    void validate(T entity) throws ValidationException;
}
