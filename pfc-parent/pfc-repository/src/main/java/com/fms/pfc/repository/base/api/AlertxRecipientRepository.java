package com.fms.pfc.repository.base.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.AlertxRecipient;
import com.fms.pfc.domain.model.AlertxRecipientPk;

@Repository
public interface AlertxRecipientRepository extends JpaRepository<AlertxRecipient, AlertxRecipientPk> {

}
