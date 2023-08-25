package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.PassHistory;

@Repository
public interface PassHisRepository extends JpaRepository<PassHistory, String> {

	@Query(value = "select * from password_history where user_id = ?1", nativeQuery = true)
	List<PassHistory> searchHistory(String user_id);

	@Modifying
	@Transactional
	@Query(value = "insert into password_history values (?1, ?2, getdate())", nativeQuery = true)
	void insertPassHis(String user_id, String oldPass);
}
