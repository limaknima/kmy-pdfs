package com.fms.pfc.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ManufacturerDto {
	private int vendorId;
	private String vendorName;
	private String vendorType;
	private String email;
	private String address;
	private String street;
	private String town;
	private String postcode;
	private String stateId;
	private String countryId;
	private String url;
	private String officeTel;
	private String faxNo;
	private String effDateFrom;
	private String effDateTo;
	private String creatorId;
	private Date createdDateTime;
	private String modifierId;
	private Date modifiedDateTime;
}
