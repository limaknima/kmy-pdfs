package com.fms.pfc.repository.main.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fms.pfc.domain.model.main.PrNameDto;

public interface PrNameRepository extends JpaRepository<PrNameDto, String> {

	@Query(value = "select * from ( "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_NAME) as IDX, 0 as TYPE, PR_ID, PR_CODE, PR_NAME from PRODUCT_RECIPE where 1=1 AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME1) as IDX, 1 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME1 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME1,'') !='' AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME2) as IDX, 2 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME2 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME2,'') !='' AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME3) as IDX, 3 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME3 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME3,'') !='' AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME4) as IDX, 4 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME4 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME4,'') !='' AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME5) as IDX, 5 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME5 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME5,'') !='' AND RECORD_STATUS = 3 and ONHOLD_FLAG <> 'Y' "
			+ ")t "
			+ "order by 5 ", nativeQuery = true)
	List<PrNameDto> findAllPrNames();
	
	@Query(value = "select * from ( "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_NAME) as IDX, 1 as TYPE, PR_ID, PR_CODE, PR_NAME from PRODUCT_RECIPE where 1=1 AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME1) as IDX, 2 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME1 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME1,'') !='' AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME2) as IDX, 3 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME2 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME2,'') !='' AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME3) as IDX, 4 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME3 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME3,'') !='' AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME4) as IDX, 5 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME4 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME4,'') !='' AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ "union all "
			+ "select concat('id=',PR_ID,'|code=', PR_CODE,'|name=', PR_OTHER_NAME5) as IDX, 6 as TYPE, PR_ID, PR_CODE, PR_OTHER_NAME5 from PRODUCT_RECIPE where ISNULL(PR_OTHER_NAME5,'') !='' AND (?2 = 0 OR RECORD_STATUS = ?2) and (?3 = '' OR ONHOLD_FLAG = ?3) "
			+ ")t "
			+ "where (?1 = 0 OR t.TYPE = ?1) "
			+ "order by 5 ", nativeQuery = true)
	List<PrNameDto> findAllPrNames(int type, int recordStatus, String flag);
}
