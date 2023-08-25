package com.fms.pfc.repository.base.api;

import java.util.List;

import javax.transaction.Transactional;
import com.fms.pfc.domain.model.Manufacturer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Integer> {

	@Query(value = "SELECT * FROM VENDOR WHERE VENDOR_TYPE IN (?1, 3) ORDER BY VENDOR_NAME", nativeQuery = true)
	List<Manufacturer> searchVendor(int vendorType);
	
	@Query(value = "SELECT *, CONVERT(VARCHAR, EFFECTIVE_DATE_FROM, 103) AS effective_date_from, "
			+ "CONVERT(VARCHAR, EFFECTIVE_DATE_TO, 103) AS effective_date_to FROM VENDOR "
			+ "WHERE (?1 = '' OR (VENDOR_NAME LIKE CASE WHEN ?2 = '1' THEN CONCAT('%', ?1, '%') "
			+ "									WHEN ?2 = '2' THEN CONCAT('%', ?1) "
			+ "									WHEN ?2 = '3' THEN ?1 "
			+ "									WHEN ?2 = '4' THEN CONCAT(?1, '%') END)) "
			+ "AND (?3 = '' OR (EMAIL LIKE CASE WHEN ?4 = '1' THEN CONCAT('%', ?3, '%') "
			+ "									WHEN ?4 = '2' THEN CONCAT('%', ?3) "
			+ "									WHEN ?4 = '3' THEN ?3 "
			+ "									WHEN ?4 = '4' THEN CONCAT(?3, '%') END)) "
			+ "AND (?5 = '' OR VENDOR_TYPE = ?5) " 
			+ "AND (?6 = 0 OR VENDOR_ID = ?6) ", nativeQuery = true)
	List<Manufacturer> searchManufacturerList(String vendorName, String vendorNameWildcard, String email,
			String emailWildcard, String vendorType, int vendorId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO VENDOR (VENDOR_NAME,VENDOR_TYPE,EMAIL,ADDRESS,STREET,TOWN,POSTCODE,STATE_ID,COUNTRY_ID,URL,OFFICE_TEL,FAX_NO,EFFECTIVE_DATE_FROM,EFFECTIVE_DATE_TO,CREATOR_ID) VALUES "
			+ "(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,CONVERT(DATETIME, ?13, 105),CONVERT(DATETIME, ?14, 105),?15) ", nativeQuery = true)
	void addManufacturer(String vendorName, String vendorType, String email, String address, String street, String town,
			String postcode, String stateId, String countryId, String url, String officeTel, String faxNo,
			String effDateFrom, String effDateTo, String creatorId);

	@Modifying(clearAutomatically = true)
	@Transactional	
	@Query(value = "UPDATE VENDOR SET "
			+ " VENDOR_NAME = ?1, "
			+ "	VENDOR_TYPE = ?2, " 
			+ "	EMAIL = ?3, " 
			+ "	OFFICE_TEL = ?4, "
			+ "	FAX_NO = ?5, " 
			+ "	ADDRESS = ?6, " 
			+ "	TOWN = ?7, " 
			+ "	POSTCODE = ?8, " 
			+ "	STATE_ID = ?9, "
			+ "	COUNTRY_ID = ?10, " 
			+ "	URL = ?11, " 
			+ "	EFFECTIVE_DATE_FROM = CONVERT(DATETIME, ?12, 105),"
			+ "	EFFECTIVE_DATE_TO = CONVERT(DATETIME, ?13, 105), " + "	MODIFIER_ID = ?14, "
			+ "	MODIFIED_DATETIME = GETDATE() " + "WHERE VENDOR_ID = ?15 ", nativeQuery = true)
	void updateManufacturer(String vendorName, String vendorType, String email, String officeTel, String faxNo, String address,
			String town, String postcode, String stateId, String countryId, String url, String effDateFrom,
			String effDateTo, String modifierId, int vendorId);


	@Modifying
	@Transactional
	@Query(value = "DELETE FROM VENDOR " + "WHERE VENDOR_ID = ?1 ", nativeQuery = true)
	void deleteManufacturerBatch(String vendorId);
	
	//FSGS) Hani 2/3/2021 Add/changed - Add Check vendor code usage START
	@Query(value = "SELECT CASE WHEN EXISTS ( "
			+ "(SELECT v.VENDOR_ID FROM VENDOR v "
			+ "WHERE v.VENDOR_ID = ?1 "
			+ "AND (EXISTS( SELECT MANF_ID FROM RM_MANF WHERE MANF_ID = v.VENDOR_ID) "
			+ "	OR EXISTS( SELECT MANF_ID FROM RM_INGREDIENT WHERE MANF_ID = v.VENDOR_ID) "
			+ "	OR EXISTS( SELECT MANF_ID FROM RM_STAT WHERE MANF_ID = v.VENDOR_ID) "
			+ "	OR EXISTS( SELECT SUPPL_ID FROM RM_MANF_SUPPL WHERE SUPPL_ID = v.VENDOR_ID))) "
			+ ") THEN '1' ELSE '0' END ", nativeQuery = true)
	String validateVendor(int vendorId);
	//FSGS) Hani 2/3/2021 Add/changed - Add Check vendor code usage END

}
