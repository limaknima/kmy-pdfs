package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.UsbConf;

@Repository
public interface UsbConfRepository extends JpaRepository<UsbConf, Integer> {

	@Query(value = "select * from USB_CONF where NAME = ?1", nativeQuery = true)
	List<UsbConf> findAllByName(String name);

	@Query(value = "select * from USB_CONF where SERIAL_NO = ?1", nativeQuery = true)
	List<UsbConf> findAllBySerialNo(String serialNo);
	
	@Query(value = "select * from USB_CONF "
			+ "where 1=1 "
			+ "and ('' = ?1 or SERIAL_NO = ?1) "
			+ "and ('' = ?2 or NAME = ?2) "
			+ "and ('' = ?3 or PROD_LN = ?3) "
			+ "and ('' = ?4 or HPL = ?4) ", nativeQuery = true)
	List<UsbConf> findAllByCriteria(String serialNo, String name, String prodLn, String hpl);
}
