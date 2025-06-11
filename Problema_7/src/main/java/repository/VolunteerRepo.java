package repository;
import domain.Volunteer;

public interface VolunteerRepo extends Repository<Integer, Volunteer> {
    void save(Volunteer volunteer);
}
