package repository;

import domain.CharityCase;

import java.util.Optional;

public interface CharityCaseRepo extends Repository<Integer, CharityCase> {
    Optional update(CharityCase charityCase, Integer id);
}
