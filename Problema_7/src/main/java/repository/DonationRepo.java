package repository;
import domain.Donation;

import java.util.Optional;

public interface DonationRepo extends Repository<Integer, Donation> {
    Optional save (Donation donation);
}
