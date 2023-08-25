package com.fms.pfc.repository.base.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.Alertx;

@Repository
public interface AlertxRepository extends JpaRepository<Alertx, Integer> {

}
