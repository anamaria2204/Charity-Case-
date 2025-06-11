package Problema_7.persistence.repository;
import Problema_7.model.domain.Donation;
import Problema_7.persistence.repository.Repository;

import java.util.Optional;

public interface DonationRepo extends Repository<Integer, Donation> {
    Optional save (Donation donation);
}
