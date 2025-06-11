package service;

import domain.CharityCase;
import domain.Donation;
import domain.Donor;
import domain.Volunteer;
import domain.validator.DonationValidator;
import domain.validator.DonorValidator;
import repository.CharityCaseRepo;
import repository.DonationRepo;
import repository.DonorRepo;
import repository.VolunteerRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class Service_Main implements Service{

    private final VolunteerRepo volunteerRepo;
    private final CharityCaseRepo charityCaseRepo;
    private final DonorRepo donorRepo;
    private final DonationRepo donationRepo;
    private final DonorValidator donorValidator;
    private final DonationValidator donationValidator;

    public Service_Main(VolunteerRepo volunteerRepo, CharityCaseRepo charityCaseRepo, DonorRepo donorRepo, DonationRepo donationRepo, DonorValidator donorValidator, DonationValidator donationValidator) {
        this.volunteerRepo = volunteerRepo;
        this.charityCaseRepo = charityCaseRepo;
        this.donorRepo = donorRepo;
        this.donationRepo = donationRepo;
        this.donorValidator = donorValidator;
        this.donationValidator = donationValidator;
    }

    @Override
    public Iterable<Volunteer> getAllVolunteers() {
        return volunteerRepo.findAll();
    }

    @Override
    public Iterable<Donor> getAllDonors() {
        return donorRepo.findAll();
    }

    @Override
    public Volunteer findOneUsername(String username) {
        return (Volunteer) StreamSupport.stream(volunteerRepo.findAll().spliterator(), false)
                .filter(volunteer -> ((Volunteer) volunteer).getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Iterable<CharityCase> getAllCharityCases() {
        return charityCaseRepo.findAll();
    }

    @Override
    public Donor addDonor(String name, String surname, String address, String phone) {
        Donor d = new Donor(surname, name, address, phone);
        donorValidator.validate(d);
        if(donorRepo.findOne(d.getId()).equals(d)){
            return null;
        }
        donorRepo.save(d);
        return d;
    }

    @Override
    public CharityCase updateCharityCase(CharityCase charityCase, double amount) {
        charityCase.setTotal_amount(charityCase.getTotal_amount() + amount);
        charityCaseRepo.update(charityCase, charityCase.getId());
        return charityCase;
    }

    @Override
    public Donation addDonation(int id_donor, int id_case, double amount) {
        Donor donor = findOneId(id_donor);
        CharityCase charityCase = findOneIdCase(id_case);
        LocalDateTime date = LocalDateTime.now();
        Donation d = new Donation(donor, charityCase, amount, date);
        donationValidator.validate(d);
        donationRepo.save(d);
        return d;
    }

    @Override
    public Donor findOneId(int id) {
        Optional<Donor> d = donorRepo.findOne(id);
        Donor donor = d.orElse(null);
        return donor;
    }

    @Override
    public CharityCase findOneIdCase(int id) {
        Optional<CharityCase> c = charityCaseRepo.findOne(id);
        CharityCase charityCase = c.orElse(null);
        return charityCase;
    }

    @Override
    public List<Donor> searchDonors(String searchText) {
        return donorRepo.searchDonors(searchText);
    }
}
