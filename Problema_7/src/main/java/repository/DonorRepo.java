package repository;

import domain.Donor;

import java.util.List;
import java.util.Optional;

public interface DonorRepo extends Repository<Integer, Donor> {
    Optional save (Donor donor);
    List<Donor> searchDonors(String searchText);
}
