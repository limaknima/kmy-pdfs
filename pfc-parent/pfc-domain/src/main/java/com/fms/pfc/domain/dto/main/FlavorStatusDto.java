package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlavorStatusDto {
	private int fsId;
	private String fsName;
	private String fsDesc;
	private int fsRank;
	private String creatorId;
	private Date createdDate;
	private String modifierId;
	private Date modifiedDatetime;
}
