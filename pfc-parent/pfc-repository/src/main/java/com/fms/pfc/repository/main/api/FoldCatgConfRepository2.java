package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.FoldCatgConf2;

@Repository
public interface FoldCatgConfRepository2 extends JpaRepository<FoldCatgConf2, Integer> {

	@Query(value = "select * from ( "
			+ " select "
			+ " * "
			+ " from FOLD_CATG_CONF2 fcc "
			+ " where 1=1 "
			+ " and (?1 = '' or fcc.HPL = ?1) "
			+ "	and (?2 = '' OR fcc.PROD_FILE_FORMAT = ?2) "
			+ ") tbl "
			, nativeQuery = true)
	List<FoldCatgConf2> searchByCriteria(String hpl, String format);

}
