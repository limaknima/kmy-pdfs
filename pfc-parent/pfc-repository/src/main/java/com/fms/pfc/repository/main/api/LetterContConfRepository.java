package com.fms.pfc.repository.main.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.LetterContConf;

@Repository
public interface LetterContConfRepository extends JpaRepository<LetterContConf, Integer> {

}
