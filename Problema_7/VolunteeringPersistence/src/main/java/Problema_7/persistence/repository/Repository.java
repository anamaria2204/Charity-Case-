package Problema_7.persistence.repository;
import java.util.Optional;

public interface Repository<ID, E> {
   Optional findOne(ID id);
   Iterable<E> findAll();
}
