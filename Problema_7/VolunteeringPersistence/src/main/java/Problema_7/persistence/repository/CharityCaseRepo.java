package Problema_7.persistence.repository;

import Problema_7.model.domain.CharityCase;

import java.util.Optional;

public interface CharityCaseRepo extends Repository<Integer, CharityCase> {
    Optional<CharityCase> update(CharityCase charityCase, Integer id);
    Optional<CharityCase> save(CharityCase charityCase);
    Optional<CharityCase> delete(Integer id);
}
