package domain.validator;

import exceptions.ValidationException;

public class ValidatorFactory implements Factory {
    // The instance of the factory
    private static ValidatorFactory instance = null;

    // Private constructor
    private ValidatorFactory() {
    }

    /**
     * Method for getting the instance of the factory
     *
     * @return the instance of the factory
     */
    public static ValidatorFactory getInstance() {
        if (instance == null) {
            instance = new ValidatorFactory();
        }
        return instance;
    }

    /**
     * Method for creating a validator
     *
     * @param strategy - the strategy for the validator
     * @return a new validator
     */
    @Override
    public Validator createValidator(ValidatorStrategy strategy) {
        switch (strategy) {
            case volunteer -> {
                return new VolunteerValidator();
            }
            case donor -> {
                return new DonorValidator();
            }
            case donation -> {
                return new DonationValidator();
            }
            case charity_case -> {
                return new CharityCaseValidator();
            }
            default -> throw new ValidationException("Invalid strategy");
        }
    }
}