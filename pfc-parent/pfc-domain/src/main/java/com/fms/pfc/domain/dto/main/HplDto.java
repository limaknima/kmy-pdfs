package com.fms.pfc.domain.dto.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HplDto {
	private Integer pkHplId;
	private String hplName;
	private String hplDesc;
	private String hplModelListStr;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;

	private List<HplModelDto> hplModelList = new ArrayList<HplModelDto>();
	private String modelNames;
}
