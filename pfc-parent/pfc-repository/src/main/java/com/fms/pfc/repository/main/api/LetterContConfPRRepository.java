package com.fms.pfc.repository.main.api;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.LetterContConfPR;

@Repository
public interface LetterContConfPRRepository extends JpaRepository<LetterContConfPR, Integer> {
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM LETTER_CONF_PR WHERE LT_CONF_ID = ?1", nativeQuery = true)
	void delLetterContConfPRByParent(int ltConfId);

}
