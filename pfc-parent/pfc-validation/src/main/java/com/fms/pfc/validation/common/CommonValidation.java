package com.fms.pfc.validation.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.model.AlertMessage;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.domain.model.Group;
import com.fms.pfc.domain.model.Manufacturer;
import com.fms.pfc.domain.model.Organization;
import com.fms.pfc.domain.model.Role;
import com.fms.pfc.domain.model.UsrProfile;
import com.fms.pfc.service.api.base.AlertMessageService;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.GroupService;
import com.fms.pfc.service.api.base.ManufacturerService;
import com.fms.pfc.service.api.base.OrganizationService;
import com.fms.pfc.service.api.base.RoleService;
import com.fms.pfc.service.api.base.UserProfileService;

@Service
public class CommonValidation {
	
	
	private ManufacturerService manfServ;	
	private GroupService grpServ;	
	private RoleService roleServ;	
	private OrganizationService orgServ;	
	private UserProfileService usrProfServ;	
	private CountryService refCountryServ;
	private AlertMessageService alertMsgServ;
	
	@Autowired
	public CommonValidation(ManufacturerService manfServ, GroupService grpServ, RoleService roleServ,
			OrganizationService orgServ, UserProfileService usrProfServ, CountryService refCountryServ,
			AlertMessageService alertMsgServ) {
		super();
		this.manfServ = manfServ;
		this.grpServ = grpServ;
		this.roleServ = roleServ;
		this.orgServ = orgServ;
		this.usrProfServ = usrProfServ;
		this.refCountryServ = refCountryServ;
		this.alertMsgServ = alertMsgServ;
	}

	/*
	 * used for common validation error messages
	 */
	private final String ERR_MSG_UNIQUE_ID = " already exist";
	private final String ERR_MSG_MANDATORY = " is mandatory";
	private final String ERR_MSG_MAX_LENGTH = " has exceeded the maximum length of ";
	private final String ERR_MSG_MAX_SIZE = " has exceeded the maximum size of ";
	private final String ERR_MSG_EMAIL_FORMAT = " is not a valid email format";
	private final String ERR_MSG_URL_FORMAT = " is not a valid url format";
	private final String ERR_MSG_DATE_FORMAT = " is not a valid date format of dd/MM/yyyy";
	private final String ERR_MSG_DATE_RANGE = " is before ";
	private final String breakline = ".<br />";

	// Validate for unique Manufacturer/Supplier name
	public String validateUniqueManuName(String input, String fieldname) {
		boolean isUnique = true;
		List<Manufacturer> mList = manfServ.searchManufacturerList("", "", "", "", "", 0);

		for (Manufacturer m : mList)
			if (input.trim().equals(m.getVendorName().trim())) {
				isUnique = false;
				break;
			}

		if (isUnique == false) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";
	}

	// Validate unique groupId
	public String validateUniqueGroupId(String input, String fieldname) {
		boolean isUnique = true;
		List<Group> gList = grpServ.searchGroup("", "");

		for (Group g : gList)
			if (input.trim().equals(g.getGroupId().trim())) {
				isUnique = false;
				break;
			}

		if (isUnique == false) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";
	}

	// Validate unique roleId
	public String validateUniqueRoleId(String input, String fieldname) {
		boolean isUnique = true;
		List<Role> urList = roleServ.searchUserRole("", "1", "", "1");// 1 means anything

		for (Role ur : urList)
			if (input.trim().equals(ur.getRoleId().trim())) {
				isUnique = false;
				break;
			}

		if (isUnique == false) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";
	}

	// Check if organization ID exist in database
	public String validateUniqueOrgId(String input, String fieldname) {

		boolean isUnique = true;
		List<Organization> orgList = orgServ.searchOrganization("", "", "", "", "");

		for (Organization org : orgList)
			if (input.trim().equals(org.getOrgaID().trim())) {
				isUnique = false;
				break;
			}

		if (isUnique == false) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";

	}

	// Check if organization ID exist in database
	public String validateUniqueUserId(String input, String fieldname) {

		boolean isUnique = true;
		List<UsrProfile> usrList = usrProfServ.searchUserProfile("", "", "", "", "", "", "", "", "");

		for (UsrProfile usr : usrList)
			if (input.trim().equalsIgnoreCase(usr.getUserId().trim())) {
				isUnique = false;
				break;
			}

		if (isUnique == false) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";
	}
	
	// Check if organization ID exist in database
		public String validateUniqueCountryId(String input, String fieldname) {

			boolean isUnique = true;
			List<Country> conList = refCountryServ.searchCountry("", "", "", "", "", "");

			for (Country con : conList) 
				if (input.trim().equals(con.getCountryId().trim())) {
					isUnique = false;
					break;
				}

			if (isUnique == false) {
				return fieldname + ERR_MSG_UNIQUE_ID + breakline;
			} else
				return "";
		}
		
	// FSGS) Faiz Shahiran 2021/3/1 - add method to validate AlertMessage id START.
	// Check if AlertMessage.alertType ID exist in database
	public String validateUniqueAlertId(String input, String fieldname) {
		AlertMessage am = alertMsgServ.findById(input);
		if (am != null) {
			return fieldname + ERR_MSG_UNIQUE_ID + breakline;
		} else
			return "";
	}
	// FSGS) Faiz Shahiran 2021/3/1 - add method to validate AlertMessage id END.

	public boolean isNullOrEmpty(String input) {
		final Pattern p = Pattern.compile("\\s+");

		if (input == null || input.length() == 0 || p.matcher(input).matches() || input.equals(" "))
			return true;
		else
			return false;
	}

	// Validate input has value
	public String validateMandatoryInput(String input, String fieldname) {
		if (isNullOrEmpty(input) == true)
			return fieldname + ERR_MSG_MANDATORY + breakline;
		else
			return "";
	}

	// Validate selection has value
	public String validateMandatorySelect(int input, String fieldname) {
		if (input == 0)
			return fieldname + ERR_MSG_MANDATORY + breakline;
		else
			return "";
	}

	// Validate input length does not exceed max length
	public String validateInputLength(String input, int maxLength, String fieldname) {
		if (input.length() > maxLength)
			return fieldname + ERR_MSG_MAX_LENGTH + maxLength + breakline;
		else
			return "";
	}

	// Validate email input format matches email format
	public String validateEmailFormat(String input, String fieldname) {
		if (isNullOrEmpty(input) == false) {
			String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

			if (input.matches(regex) == false)
				return fieldname + ERR_MSG_EMAIL_FORMAT + breakline;
			else
				return "";
		} else
			return "";
	}

	// Validate url input format matches url format
	public String validateUrlFormat(String input, String fieldname) {
		if (isNullOrEmpty(input) == false) {
			String urlRegex = "[a-zA-Z0-9@:%._\\+~#?&//=]" + "{2,256}\\.[a-z]" + "{2,6}\\b([-a-zA-Z0-9@:%"
					+ "._\\+~#?&//=]*)";

			Pattern urlPattern = Pattern.compile(urlRegex);

			if (input == null)
				input = "";

			Matcher matcher = urlPattern.matcher(input);

			if (matcher.matches() == false)
				return fieldname + ERR_MSG_URL_FORMAT + breakline;
			else
				return "";
		} else
			return "";
	}

	// Validate date input format
	public String validateDateFormat(String input, String fieldname) {
		if (isNullOrEmpty(input) == false) {
			SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
			sdfrmt.setLenient(false);

			try {
				Date javaDate = sdfrmt.parse(input); // Valid format
			} catch (Exception e) {
				return fieldname + ERR_MSG_DATE_FORMAT + breakline;
			}
		} else
			return "";
		return "";
	}

	// Validate date from is not before date to
	public String validateDateRange(String input1, String input2, String fieldname1, String fieldname2) {
		if (isNullOrEmpty(input1) == false && isNullOrEmpty(input2) == false) {
			SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
			sdfrmt.setLenient(false);

			try {
				Date dateFrom = sdfrmt.parse(input1); // Valid format
				Date dateTo = sdfrmt.parse(input2); // Valid format

				if (dateTo.before(dateFrom) == true) // if dateTo input is before dateFrom input, error
					return fieldname1 + ERR_MSG_DATE_RANGE + fieldname2 + breakline;
				else
					return "";
			} catch (Exception e) {
				return "Date" + ERR_MSG_DATE_FORMAT + breakline;
			}
		} else
			return "";
	}

	// Validate file size < 10 MB
	public String validateFileSize(Long fileSize, String fieldname) {

		if (fileSize > CommonConstants.FILE_SIZE_LIMITATION) {

			return fieldname + ERR_MSG_MAX_SIZE + "100MB" + breakline;

		} else
			return "";
	}

	public String validateMandatoryInputRemark(String remark, String fieldname, String oldRemark) {
		if (isNullOrEmpty(remark) == true) {
			return fieldname + ERR_MSG_MANDATORY + " as it will be used for tracing reason of change/update "
					+ breakline;
		} else if (remark.equals(oldRemark)) {
			return fieldname + ERR_MSG_MANDATORY + " as it will be used for tracing reason of change/update. Kindly make sure current remark is updated "
					+ breakline;
		} else {
			return "";
		}
	}
	
	public String validateMandatoryInputRemark(String remark, String fieldname) {
		if (isNullOrEmpty(remark) == true) {
			return fieldname + ERR_MSG_MANDATORY + " as it will be used for tracing reason of change/update "
					+ breakline;
		} else {
			return "";
		}
	}
}
