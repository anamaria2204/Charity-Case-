package Problema_7.model.domain.validator;
public interface Validator<T> {

    /**
     * validate method
     *
     * @param entity - verify if an entity is valid
     */

    void validate(T entity) throws Exception;
}
