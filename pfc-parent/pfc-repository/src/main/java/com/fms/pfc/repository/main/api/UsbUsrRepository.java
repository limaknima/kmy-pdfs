package com.fms.pfc.repository.main.api;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.UsbUsr;

@Repository
public interface UsbUsrRepository extends JpaRepository<UsbUsr, Integer> {

	@Modifying
	@Transactional
	@Query(value = "delete from USB_USR WHERE UCONF_ID = ?1", nativeQuery = true)
	void deleteUsbUsrByParentId(Integer parentId);

}
