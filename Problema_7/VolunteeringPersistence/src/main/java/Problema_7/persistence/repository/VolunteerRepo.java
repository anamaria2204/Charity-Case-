package Problema_7.persistence.repository;
import Problema_7.model.domain.Volunteer;

import java.util.Optional;

public interface VolunteerRepo extends Repository<Integer, Volunteer> {
    void save (Volunteer volunteer);
    Optional<Volunteer> findByUsernameAndPassword(String username, String password);
}
