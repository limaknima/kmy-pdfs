package com.fms.pfc.repository.main.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.FileTypeSz;

@Repository
public interface FileTypeSzRepository extends JpaRepository<FileTypeSz, Integer> {

}
