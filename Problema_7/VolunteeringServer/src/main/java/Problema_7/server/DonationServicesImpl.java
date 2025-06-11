package Problema_7.server;

import Problema_7.model.domain.CharityCase;
import Problema_7.model.domain.Donation;
import Problema_7.model.domain.Donor;
import Problema_7.model.domain.Volunteer;
import Problema_7.persistence.repository.CharityCaseRepo;
import Problema_7.persistence.repository.DonationRepo;
import Problema_7.persistence.repository.DonorRepo;
import Problema_7.persistence.repository.VolunteerRepo;
import Problema_7.services.Exceptions;
import Problema_7.services.IObserver;
import Problema_7.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DonationServicesImpl implements IServices {
    private static final Logger logger = LogManager.getLogger(DonationServicesImpl.class);
    private final int defaultThreadsNo = 3;

    private final CharityCaseRepo charityCaseRepository;
    private final DonationRepo donationRepository;
    private final VolunteerRepo volunteerRepository;
    private final DonorRepo donorRepository;
    private final Map<String, IObserver> loggedVolunteers;

    public DonationServicesImpl(CharityCaseRepo charityRepo, DonationRepo donationRepo, VolunteerRepo volunteerRepo, DonorRepo donorRepo) {
        this.charityCaseRepository = charityRepo;
        this.donationRepository = donationRepo;
        this.volunteerRepository = volunteerRepo;
        this.donorRepository = donorRepo;
        this.loggedVolunteers = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(Volunteer volunteer, IObserver client) throws Exceptions {
        Optional<Volunteer> volunteer1 = volunteerRepository.findByUsernameAndPassword(volunteer.getUsername(), volunteer.getPassword());
        Volunteer volunteerR = (Volunteer) volunteer1.orElse(null);
        if (volunteerR != null) {
            if (loggedVolunteers.containsKey(volunteer.getUsername())) {
                throw new Exceptions("Volunteer already logged in.");
            }
            loggedVolunteers.put(volunteer.getUsername(), client);
        } else {
            throw new Exceptions("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(Volunteer volunteer, IObserver client) throws Exceptions {
        IObserver localClient = loggedVolunteers.remove(volunteer.getUsername());
        if (localClient == null) {
            throw new Exceptions("Volunteer " + volunteer.getUsername() + " is not logged in.");
        }
    }

    @Override
    public synchronized void newDonation(Donation donation, IObserver client) throws Exceptions {
        donationRepository.save(donation);
        CharityCase charityC = donation.getCharityCase();
        charityC.setTotal_amount(charityC.getTotal_amount() + donation.getAmount());
        Optional<CharityCase> charityCase = charityCaseRepository.update(charityC, charityC.getId());
        if (charityCase.isPresent()) {
            notifyAllVolunteers(charityCase.get());

        } else {
            throw new Exceptions("Failed to update charity case");
        }
    }

    @Override
    public synchronized Donor addDonor(String surname, String name, String address, String phone, IObserver client) throws Exceptions {

        if (surname == null || surname.isEmpty() ||
            name == null || name.isEmpty() ||
            address == null || address.isEmpty() ||
            phone == null || phone.isEmpty()) {
            throw new Exceptions("All donor fields must be filled");
        }

        Donor newDonor = new Donor(surname, name, address, phone);

        Optional<Donor> savedDonor = donorRepository.save(newDonor);
        if(savedDonor.isPresent()) {
            logger.info("Donor {} added successfully", newDonor);
        } else {
            logger.error("Failed to save donor {}", newDonor);
            throw new Exceptions("Failed to save donor");
        }
        return savedDonor.orElseThrow(() ->
            new Exceptions("Failed to save donor to database"));
    }


    private void notifyAllVolunteers(CharityCase updatedCase) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        List<CharityCase> updatedCases;
        try {
            updatedCases = getCharitableCases();
        } catch (Exceptions e) {
            logger.error("Error retrieving updated charity cases: {}", e.getMessage());
            return;
        }

        for (Map.Entry<String, IObserver> entry : loggedVolunteers.entrySet()) {
            IObserver observer = entry.getValue();
            executor.execute(() -> {
                try {
                    logger.debug("Notifying volunteer {} with full updated list", entry.getKey());
                    observer.charitableCasesUpdated(updatedCases);  // trimite lista completă
                } catch (Exceptions e) {
                    logger.error("Error notifying volunteer {}: {}", entry.getKey(), e.getMessage());
                }
            });
        }
    }

    @Override
    public List<CharityCase> getCharitableCases() throws Exceptions {
        Iterable<CharityCase> cases = charityCaseRepository.findAll();
        List<CharityCase> charityCases = new ArrayList<>();
        for (CharityCase charityCase : cases) {
            charityCases.add(charityCase);
        }
        return charityCases;
    }

    @Override
    public List<Donor> searchDonor(String searchTerm) throws Exceptions {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            Iterable<Donor> allDonors = donorRepository.findAll();
            List<Donor> result = new ArrayList<>();
            allDonors.forEach(result::add);
            return result;
        }

        String normalizedSearch = searchTerm.trim().toLowerCase();

        Iterable<Donor> allDonors = donorRepository.findAll();
        List<Donor> result = new ArrayList<>();

        for (Donor donor : allDonors) {
            if (donor.getSurname().toLowerCase().contains(normalizedSearch) ||
                donor.getName().toLowerCase().contains(normalizedSearch)) {
                result.add(donor);
            }
        }

        return result;
    }

    @Override
    public List<Donor> getAllDonor() throws Exceptions {
        Iterable<Donor> donorIterable = donorRepository.findAll();
        List<Donor> donors = new ArrayList<>();
        for (Donor donor : donorIterable) {
            donors.add(donor);
        }
        return donors;
    }
}
