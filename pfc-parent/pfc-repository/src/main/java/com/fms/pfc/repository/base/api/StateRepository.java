package com.fms.pfc.repository.base.api;

import java.util.List;
import com.fms.pfc.domain.model.State;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StateRepository extends JpaRepository<State, String>{
	@Query(value = "SELECT * FROM REF_STATE "
			+ "WHERE (?1 = '' OR STATE_ID LIKE %?1%) ", nativeQuery = true)
	List<State> searchState(String stateId);
		
}
