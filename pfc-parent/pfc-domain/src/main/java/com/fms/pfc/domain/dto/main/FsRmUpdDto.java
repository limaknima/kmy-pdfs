package com.fms.pfc.domain.dto.main;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FsRmUpdDto {
	private Integer updId;
	private Integer rmId;
	private Integer currFsId;
	private Integer newFsId;
	private String makerId;
	private String checkerId;
	private Integer updStatus;
	private String creatorId;
	private Date createdDatetime;
	private String modifierId;
	private Date modifiedDatetime;
}
