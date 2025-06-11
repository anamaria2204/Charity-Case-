package repository;
import java.util.Optional;

public interface Repository<ID, E> {
   Optional findOne(ID id);
   Iterable<E> findAll();
}
