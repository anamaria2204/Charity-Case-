package Problema_7.services;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import Problema_7.services.Exceptions;

import java.util.List;

public interface IObserver {
    void charitableCaseUpdated(CharityCase updatedCase) throws Exceptions;
    void newDonationAdded(Donation newDonation) throws Exceptions;
    void charitableCasesUpdated(List<CharityCase> updatedCases) throws Exceptions;
}