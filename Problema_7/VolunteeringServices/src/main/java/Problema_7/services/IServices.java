package Problema_7.services;
import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;

import java.util.List;
import java.util.Map;

public interface IServices {
    void login(Volunteer volunteer, IObserver client) throws Exceptions;
    void logout(Volunteer volunteer, IObserver client) throws Exceptions;

    void newDonation(Donation donation, IObserver client) throws Exceptions;
    Donor addDonor(String surname, String name, String adress, String phone, IObserver client) throws Exceptions;
    List<CharityCase> getCharitableCases() throws Exceptions;
    List<Donor> searchDonor(String searchTerm) throws Exceptions;
    List<Donor> getAllDonor() throws Exceptions;
}
