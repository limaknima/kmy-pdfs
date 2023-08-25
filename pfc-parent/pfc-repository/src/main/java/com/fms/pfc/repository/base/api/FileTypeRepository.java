package com.fms.pfc.repository.base.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.FileType;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, String> {

}
