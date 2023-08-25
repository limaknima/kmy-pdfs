package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.pfc.domain.model.main.Hpl;

@Repository
public interface HplRepository extends JpaRepository<Hpl, Integer> {

	@Query(value = "select * from ( "
			+ "select "
			+ "	h.*, "
			+ "	(STUFF( "
			+ "			( SELECT ',' + CONVERT(NVARCHAR(max), rm.NAME ) "
			+ "			 from HPL_MODEL rm where 1=1 and rm.HPL_ID = h.HPL_ID "
			+ "			 FOR XML PATH('') "
			+ "			), 1, LEN(','), '' "
			+ "		) "
			+ "	) as MODEL_NAMES "
			+ "from HPL h "
			+ ")t1 "
			+ "where 1=1 "
			+ "and (?1 = '' OR ( "
			+ "		t1.NAME like case when ?2 = '1' then concat('%', ?1, '%') "
			+ "		when ?2 = '2' then concat('%', ?1) "
			+ "		when ?2 = '3' then ?1 "
			+ "		when ?2 = '4' then concat(?1, '%') end "
			+ "	) "
			+ ") "
			+ "and exists ( "
			+ "	select 1 from HPL_MODEL hm "
			+ "	where 1=1 and hm.HPL_ID = t1.HPL_ID "
			+ "	and (?3 = '' OR ( "
			+ "		hm.NAME like case when ?4 = '1' then concat('%', ?3, '%') "
			+ "		when ?4 = '2' then concat('%', ?3) "
			+ "		when ?4 = '3' then ?3 "
			+ "		when ?4 = '4' then concat(?3, '%') end "
			+ "	 ) "
			+ " ) "
			+ ") "
			, nativeQuery = true)
	List<Hpl> searchByCriteria(String hplName, String hplNameExp, String hplModelName, String hplModelNameExp);
}
