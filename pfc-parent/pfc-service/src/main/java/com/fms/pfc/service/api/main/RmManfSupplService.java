package com.fms.pfc.service.api.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.domain.model.main.RmManfSuppl;
import com.fms.pfc.repository.main.api.RmManfSupplRepository;

@Service
public class RmManfSupplService {

	@Autowired
	private RmManfSupplRepository rmManfSupplRepo;

	public List<RmManfSuppl> searchRmSupplier(int rmManfId, String vendor, String expr) {
		return rmManfSupplRepo.searchRmSupplier(rmManfId, vendor, expr);
	}

	public void delRmManfSuppl(int rawMatlId) {
		rmManfSupplRepo.delRmManfSuppl(rawMatlId);
	}

	public void clrRmManfSuppl(int rmManfId) {
		rmManfSupplRepo.clrRmManfSuppl(rmManfId);
	}

	public void addRmManfSuppl(int manfId, String matlName, int supplId) {
		rmManfSupplRepo.addRmManfSuppl(manfId, matlName, supplId);
	}

}