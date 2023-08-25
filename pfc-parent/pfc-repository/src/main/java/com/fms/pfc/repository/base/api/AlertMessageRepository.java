package com.fms.pfc.repository.base.api;

import com.fms.pfc.domain.model.AlertMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface AlertMessageRepository extends JpaRepository<AlertMessage, String> {
	
	@Query(value = "SELECT * FROM ALERT_DEFINITION "
			+ "WHERE (?1 = '' OR CONVERT(DATE, CREATED_DATETIME) >= CONVERT(DATETIME, ?1, 105)) "
			+ "AND (?2 = '' OR CONVERT(DATE, CREATED_DATETIME) <= CONVERT(DATETIME, ?2, 105)) "
			+ "AND (?3 = '' OR (LOWER(ALERT_DEF_ID) LIKE CASE WHEN ?5 = '1' THEN LOWER(CONCAT('%', ?3, '%'))"
			+ "	WHEN ?5 = '2' THEN LOWER(CONCAT('%', ?3)) "
			+ "	WHEN ?5 = '3' THEN LOWER(?3) "
			+ "	WHEN ?5 = '4' THEN LOWER(CONCAT(?3, '%')) END)) "
			+ "AND (?4 = '' OR (LOWER(SUBJECT) LIKE CASE WHEN ?6 = '1' THEN LOWER(CONCAT('%', ?4, '%'))"
			+ " WHEN ?6 = '2' THEN LOWER(CONCAT('%', ?4)) "
			+ " WHEN ?6 = '3' THEN LOWER(?4) "
			+ " WHEN ?6 = '4' THEN LOWER(CONCAT(?4, '%')) END))", nativeQuery = true)
	List<AlertMessage> searchAlert(String dateFr, String dateTo, String alertType, String subject, String exp1, String exp2);
	
	@Query(value = "select * from alert_definition where alert_def_id = ?1 ", nativeQuery = true)
	AlertMessage searchAlertById(String alertType);
	
	@Modifying
	@Transactional
	@Query(value = "delete from alert_definition where alert_def_id = ?1", nativeQuery = true)
	void deleteAlert(String alertType);
	
	@Modifying
	@Transactional
	@Query(value = "insert into alert_definition (alert_def_id,subject,alert_desc,creator_id) values (?1,?2,?3,?4)", nativeQuery = true)
	void addAlert(String alertType, String subject, String description, String createdUser);
	
	@Modifying
	@Transactional
	@Query(value = "update alert_definition set subject = ?1, alert_desc = ?2, modifier_id = ?3, modified_datetime = GETDATE() where alert_def_id = ?4", nativeQuery = true)
	void updateAlert(String subject, String description, String updatedUser, String alertType);	

}
