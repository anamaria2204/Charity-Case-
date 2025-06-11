package service;

import domain.CharityCase;
import domain.Donation;
import domain.Donor;
import domain.Volunteer;

import java.util.List;

public interface Service {
   Iterable<Volunteer> getAllVolunteers();
   Iterable<Donor> getAllDonors();
   Volunteer findOneUsername(String username);
   Iterable<CharityCase> getAllCharityCases();
   Donor addDonor(String name, String surname, String address, String phone);
   CharityCase updateCharityCase(CharityCase charityCase, double amount);
   Donation addDonation(int id_donor, int id_case, double amount);
   Donor findOneId(int id);
   CharityCase findOneIdCase(int id);
   List<Donor> searchDonors(String searchText);
}
