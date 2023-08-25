package com.fms.pfc.controller.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;

import com.fms.pfc.common.Authority;
import com.fms.pfc.common.CommonConstants;
import com.fms.pfc.domain.dto.LabelAndValueDto;
import com.fms.pfc.domain.dto.main.LetterContConfDto;
import com.fms.pfc.domain.model.Country;
import com.fms.pfc.domain.model.Organization;
import com.fms.pfc.domain.model.State;
import com.fms.pfc.domain.model.Usr;
import com.fms.pfc.domain.model.main.PrNameDto;
import com.fms.pfc.domain.model.main.PrStat;
import com.fms.pfc.domain.model.main.ProductRecipe;
import com.fms.pfc.service.api.base.CountryService;
import com.fms.pfc.service.api.base.LoginService;
import com.fms.pfc.service.api.base.OrganizationService;
import com.fms.pfc.service.api.base.StateService;
import com.fms.pfc.service.api.base.TrxHisService;
import com.fms.pfc.service.api.main.LetterContConfService;
import com.fms.pfc.service.api.main.ProductRecipeService;
import com.fms.pfc.service.api.main.ProductStatusService;
import com.fms.pfc.validation.common.CommonValidation;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@SessionScope
public class PrintDeclarationController2 {

	private final Logger logger = LoggerFactory.getLogger(PrintDeclarationController2.class);

	private CountryService refCountryServ;
	private ProductRecipeService prodRecpServ;
	private ProductStatusService prodStatServ;
	private StateService refStateServ;
	private LoginService loginServ;
	private OrganizationService orgServ;
	private CommonValidation commonValServ;
	private TrxHisService trxHistServ;
	private Authority auth;
	private MessageSource msgSource;
	private LetterContConfService letterConfServ;
	private Environment env;

	private static final String REPORT_ID = CommonConstants.REPORT_ID_PROD_DECL;
	private static final int PERMITTED = CommonConstants.VF_STATUS_PERMITTED;
	private static final int NOTPERMITTED = CommonConstants.VF_STATUS_NOT_PERMITTED;
	private static final int NOTSURE = CommonConstants.VF_STATUS_NOT_SURE;
	private static final String PR_NAME_TXT = "Product Name";
	private static final String CN_NAME_TXT = "Country Name";
	private static final String CS_NAME_TXT = "Customer Name";
	private static final String REMARK_TXT = "Remark";

	private Map<String, Object> model = new HashMap<String, Object>();
	private boolean isDefault = true;

	@Autowired
	public PrintDeclarationController2(CountryService refCountryServ, ProductRecipeService prodRecpServ,
			ProductStatusService prodStatServ, StateService refStateServ, LoginService loginServ,
			OrganizationService orgServ, CommonValidation commonValServ, TrxHisService trxHistServ, Authority auth,
			MessageSource msgSource, LetterContConfService letterConfServ, Environment env) {
		super();
		this.refCountryServ = refCountryServ;
		this.prodRecpServ = prodRecpServ;
		this.prodStatServ = prodStatServ;
		this.refStateServ = refStateServ;
		this.loginServ = loginServ;
		this.orgServ = orgServ;
		this.commonValServ = commonValServ;
		this.trxHistServ = trxHistServ;
		this.auth = auth;
		this.msgSource = msgSource;
		this.letterConfServ = letterConfServ;
		this.env = env;
	}

	@GetMapping("/main/product/declaration2")
	public ModelAndView getPrintDeclaration(HttpServletRequest request, HttpServletResponse response) {

		model = auth.onPageLoad(model, request);
		auth.isAuthUrl(request, response);

		model.put("confirmHeader",
				msgSource.getMessage("msgReportConfirmHeader", new Object[] {}, Locale.getDefault()));
		model.put("confirmMsg", msgSource.getMessage("msgReportConfirm", new Object[] {}, Locale.getDefault()));

		return new ModelAndView("/main/product/declaration2", model);
	}

	@PostMapping("/main/product/declaration2")
	public ModelAndView refreshPage(@RequestParam(name = "product") String productId,
			@RequestParam(name = "prodautocomplete") String prodautocomplete,
			@RequestParam(name = "customer") String customer, @RequestParam(name = "remark") String remark,
			HttpServletRequest request, HttpSession session) {

		logger.debug("refreshPage() productId={}", productId);

		model.remove("success");
		model.remove("error");

		String prId = "";
		StringTokenizer st = new StringTokenizer(productId, "|");
		while (st.hasMoreTokens()) {
			String idx = (String) st.nextElement();
			if (idx.startsWith("id=")) {
				prId = idx.substring(3);
			}
		}

		if (!prId.equals("0") && StringUtils.isNotEmpty(prId)) {
			// Get prId from product recipe
			// ProductRecipe product = prodRecpServ.searchProductId(prId,
			// String.valueOf(CommonConstants.SEARCH_OPTION_EXACT)).get(0);
			// Product's country list
			List<Country> countryList = new ArrayList<Country>();
			// List to get country id and permissibility id
			List<PrStat> productCountryList = prodStatServ.searchProductStatus(Integer.parseInt(prId),
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", 0);

			for (PrStat prStat : productCountryList) {
				// Get all country code for screen
				countryList.addAll(refCountryServ.searchCountry(prStat.getCountryId(), "",
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", ""));
			}

			model.put("product", productId);
			model.put("prodautocomplete", prodautocomplete);
			model.put("countryList", countryList);
			model.put("customer", customer);
			model.put("remark", remark);
		} else {
			model.remove("selectedPrCode");
			model.remove("countryList");
		}

		return new ModelAndView("/main/product/declaration2", model);
	}

	@PostMapping(value = "/main/product/declaration2", params = "print=true")
	public ModelAndView printReport(@RequestParam(name = "product") String productId,
			@RequestParam(name = "prodautocomplete") String prodautocomplete,
			@RequestParam(name = "country", required = false) List<String> country,
			@RequestParam(name = "customer") String customer, @RequestParam(name = "remark") String remark,
			HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		isDefault = true;

		model.remove("success");
		model.remove("error");

		List<ProductRecipe> productList = prodRecpServ.searchProductRecipe("", "", "", "", 0,
				CommonConstants.RECORD_STATUS_SUBMITTED);
		List<ProductRecipe> sortedproductList = productList.stream()
				.sorted((o1, o2) -> o1.getPrName().compareTo(o2.getPrName())).collect(Collectors.toList());

		boolean hasError = false;

		String errorMsg = "";

		try {

			if (productId.equals("0") || productId.isEmpty()) {
				errorMsg += PR_NAME_TXT + " is mandatory.<br />";
			}

			if (country == null) {
				errorMsg += CN_NAME_TXT + " is mandatory.<br />";
			}
			errorMsg += commonValServ.validateMandatoryInput(customer, CS_NAME_TXT);
			errorMsg += commonValServ.validateInputLength(remark, 100, REMARK_TXT);

			if (!errorMsg.isEmpty()) {
				model.put("productItem", sortedproductList);
				model.put("selectedPrCode", productId);
				model.put("remark", remark);
				model.put("countryx", country);
				model.put("remark", remark);
				model.put("customer", customer);
				model.put("error", errorMsg);
				return new ModelAndView("/main/product/declaration2", model);
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += e.toString();
		}

		String prId = "";
		String selName = "";

		StringTokenizer st = new StringTokenizer(productId, "|");
		while (st.hasMoreTokens()) {
			String idx = (String) st.nextElement();
			if (idx.startsWith("id=")) {
				prId = idx.substring(3);
			}
			if (idx.startsWith("name=")) {
				selName = idx.substring(5);
			}
		}

		// CR_2022: Get Letter Conf
		RetrieveLetterConf conf = new RetrieveLetterConf(country, prId);
		List<LetterContConfDto> confList = conf.getFilteredList();
		if (confList != null && !confList.isEmpty()) {
			logger.debug("confList size {}", confList.size());
			isDefault = false;
		}

		try {
			if (isDefault) {
				logger.debug("Printing default ....");
				PrintDefault def = new PrintDefault(country, prId, selName, errorMsg, request, response,
						conf.getDefault());
				def.print();

			} else {
				logger.debug("Printing multiple ....");
				PrintMultiple mult = new PrintMultiple(country, prId, selName, errorMsg, request, response, confList,
						conf.getDefault());
				mult.print();
			}

		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
			errorMsg += e.toString();
		} finally {
			if (hasError == true) {
				errorMsg += msgSource.getMessage("msgReportErr", new Object[] {}, Locale.getDefault());
				model.put("error", errorMsg);
				model.remove("success");
			} else {
				model.put("success", msgSource.getMessage("msgReportSuccess", new Object[] {}, Locale.getDefault()));
				model.remove("error");
				model.remove("date");
				model.put("productItem", sortedproductList);
				model.remove("selectedPrCode");
				model.remove("countryList");
				model.remove("customer");
				model.remove("remark");
			}
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log START
			trxHistServ.addTrxHistory(new Date(), "Print Declaration Letter", request.getRemoteUser(),
					CommonConstants.FUNCTION_TYPE_DOWNLOAD,
					msgSource.getMessage(REPORT_ID, new Object[] {}, Locale.getDefault()),
					CommonConstants.RECORD_TYPE_ID_PROD_RECP_DECL, "");
			// FSGS) Kent 26/2/2021 Add/changed - Add Function executed log END
		}

		return new ModelAndView("/main/product/declaration2", model);
	}

	protected String genAddressStr(Organization org, Country con, State sta) {
		String address = "";
		if (StringUtils.isNotEmpty(org.getAddress()) && StringUtils.isNotEmpty(org.getCity())
				&& StringUtils.isNotEmpty(org.getPostcode())) {
			address = (org.getAddress()) + ", " + WordUtils.capitalizeFully(org.getCity())
					+ ", " + org.getPostcode() + " " + WordUtils.capitalizeFully(sta.getStateName()) + ", "
					+ WordUtils.capitalizeFully(con.getCountryName());
		}
		
		logger.debug("address .. {}", address);

		return address;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	@RequestMapping(value = "/main/product/prodAutocomplete2", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public List<LabelAndValueDto> prodAutocomplete(
			@RequestParam(name = "term", required = false, defaultValue = "") String term) {

		List<LabelAndValueDto> suggestions = new ArrayList<LabelAndValueDto>();
		List<PrNameDto> allNames = prodRecpServ.findAllPrNames();
		for (PrNameDto prNameDto : allNames) {
			if (StringUtils.contains(prNameDto.getPrName().toLowerCase(), term.toLowerCase())) {
				suggestions.add(new LabelAndValueDto(prNameDto.getPrName(), prNameDto.getIdx()));
			}
		}

		return suggestions;
	}
	
	protected String reGenerateSec2Desc(String ... arg0) {
		StringBuffer sb = new StringBuffer();
		sb.append(arg0[0]);
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append(arg0[1]);
		
		return sb.toString();
	}

	public class RetrieveLetterConf {

		private List<LetterContConfDto> confDtoList = new ArrayList<LetterContConfDto>();
		private List<String> selectedCountry = new ArrayList<String>();
		private String prId;

		public RetrieveLetterConf(List<String> selectedCountry, String prId) {
			this.selectedCountry = selectedCountry;
			this.prId = prId;
			this.confDtoList = letterConfServ.findAll2();
		}

		private List<LetterContConfDto> getFilteredList() {
			Predicate<LetterContConfDto> prIdFilter = arg0 -> (prId != null && !prId.isEmpty())
					? (Stream.of(arg0.getProdIdList().split(",", -1)).collect(Collectors.toList()).contains(prId))
					: (1 > 0);

			Predicate<LetterContConfDto> countryFilter = arg0 -> (selectedCountry.contains(arg0.getCountryId()));

			confDtoList = letterConfServ.findAll2().stream().filter(prIdFilter).filter(countryFilter)
					.collect(Collectors.toList());

			return confDtoList;
		}

		private LetterContConfDto getDefault() {
			return letterConfServ.findOneById(1);// General default id = 1
		}

	}

	public class PrintMultiple {
		private List<String> country;
		private String prId, selName, errorMsg;
		private HttpServletRequest request;
		private HttpServletResponse response;
		private List<LetterContConfDto> confList;
		private LetterContConfDto defConf;

		public PrintMultiple() {

		}

		public PrintMultiple(List<String> country, String prId, String selName, String errorMsg,
				HttpServletRequest request, HttpServletResponse response, List<LetterContConfDto> confList,
				LetterContConfDto defConf) {
			super();
			this.country = country;
			this.prId = prId;
			this.selName = selName;
			this.errorMsg = errorMsg;
			this.request = request;
			this.response = response;
			this.confList = confList;
			this.defConf = defConf;
		}

		private void print() throws JRException, IOException {

			validateData();

			// create temp dir
			String tempDir = getTempDir();

			List<PrStat> specialList = new ArrayList<PrStat>();
			List<PrStat> genList = new ArrayList<PrStat>();
			List<String> countryToRemove = new ArrayList<String>();
			for (LetterContConfDto confDto : this.confList) {
				logger.debug(">>> confDto.getLetterTypeName() " + confDto.getLetterTypeName());
				if (!confDto.getLetterTypeName().equalsIgnoreCase("general")) {
					// non general type
					for (int i = 0; i < country.size(); i++) {
						String ctrId = country.get(i);
						if (confDto.getCountryId().equalsIgnoreCase(ctrId)) {
							countryToRemove.add(ctrId);
							specialList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId),
									String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), ctrId, 0));
						}
					}
					prepareReport(specialList, confDto, tempDir);
					logger.debug(">>> specialList " + specialList.size());

				} else {
					// for general type
					for (int i = 0; i < country.size(); i++) {
						String ctrId = country.get(i);
						if (confDto.getCountryId().equalsIgnoreCase(ctrId)) {
							genList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId),
									String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), ctrId, 0));
						}
					}
					prepareReport(genList, confDto, tempDir);
					logger.debug(">>> genList1 " + genList.size());
				}
			}

			// for generic case, same Product, other remaining country
			{
				List<String> remainingCtry = country.stream().filter(arg0 -> !countryToRemove.contains(arg0))
						.collect(Collectors.toList());
				for (int i = 0; i < remainingCtry.size(); i++) {
					String ctrId = remainingCtry.get(i);
					genList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId),
							String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), ctrId, 0));
				}
				
				if(!genList.isEmpty())
					prepareReport(genList, defConf, tempDir);
				
				logger.debug(">>> genList2 " + genList.size());
			}

			// zip and download
			Path source = Paths.get(tempDir);
			Path zip = Paths.get(tempDir + ".zip");
			zipFolder(source, zip);
			download(zip);

		}

		protected void download(Path zip) throws IOException, FileNotFoundException {
			response.setContentType("application/x-download");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + zip.getFileName().toString() + "\"");
			OutputStream outStream = response.getOutputStream();
			FileInputStream inStream = new FileInputStream(new File(zip.toString()));

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inStream.close();
			outStream.close();
		}

		protected void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
			Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
					Files.copy(file, zos);
					zos.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
			zos.close();
		}

		protected String getTempDir() throws IOException {
			String pattern = "yyyyMMddHHmmssSS";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());
			String tempDir = env.getProperty("temp.dir") + "Letter-" + date;
			Files.createDirectory(Paths.get(tempDir));
			return tempDir;
		}

		protected void validateData() {
			List<PrStat> prStatList = new ArrayList<PrStat>();
			for (int i = 0; i < country.size(); i++) {
				prStatList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId),
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), country.get(i), 0));
			}

			// Validate data size - do not proceed to report generation if no data found
			if (prStatList.isEmpty() || prStatList.size() == 0) {
				model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
				model.remove("success");
				return;// new ModelAndView("/main/product/declaration2", model);
			}
		}

		protected void prepareReport(List<PrStat> prStatList, LetterContConfDto confDto, String dir)
				throws JRException, IOException {
			// List to store country name and permissibility status
			List<PrStat> reportList = new ArrayList<PrStat>();
			for (PrStat prStat : prStatList) {
				// Get country name
				String countryName = refCountryServ.searchCountry(prStat.getCountryId(), "",
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0).getCountryName();
				// Set country name for each product
				prStat.setCountryName(countryName);
				// Set Status name for each product
				if (prStat.getFinalStatus() == PERMITTED) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + PERMITTED, null, Locale.getDefault()));
				} else if (prStat.getFinalStatus() == NOTPERMITTED) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + NOTPERMITTED, null, Locale.getDefault()));
				} else if (prStat.getFinalStatus() == NOTSURE) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + NOTSURE, null, Locale.getDefault()));
				}
				reportList.add(prStat);
			}

			// Initialize objects for report
			// Get current user
			Usr loginUsr = loginServ.searchUser(request.getRemoteUser());
			Organization org = orgServ.searchOrganization(loginUsr.getOrgId(), "", "",
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "").get(0);
			Country con = refCountryServ.searchCountry(org.getCountry(), "",
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0);
			State sta = refStateServ.searchState(org.getState()).get(0);
			// Set address for report
			String address = genAddressStr(org, con, sta);
			String base64Encoded = "";
			List<String> selectedCountry = new ArrayList<>();

			for (int i = 0; i < prStatList.size(); i++) {
				selectedCountry.add(prStatList.get(i).getCountryName());
			}

			// Encode image
			if (org.getLogo() != null) {

				byte[] encodeBase64 = Base64.encodeBase64(org.getLogo());
				try {
					base64Encoded = new String(encodeBase64, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					errorMsg += e.toString();
				}
			}

			// Print report
			StringBuffer fileName = new StringBuffer();
			String reportName = msgSource.getMessage(REPORT_ID, new Object[] {}, Locale.getDefault());

			InputStream reportStream = getClass().getResourceAsStream("/report/" + REPORT_ID + ".jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

			Map<String, Object> parameters = new HashMap<String, Object>();
			// Set parameters for report fields
			parameters.put("product", selName);
			parameters.put("address", address);
			parameters.put("orgName", org.getOrgaName());
			parameters.put("logo", base64Encoded);
			parameters.put("tel", org.getTel());
			parameters.put("fax", org.getFax());
			parameters.put("section1", confDto.getSec1Desc());
			//parameters.put("section2", confDto.getSec2Desc());
			parameters.put("section2", reGenerateSec2Desc(confDto.getSec2Desc(), confDto.getSec3Desc()));
			parameters.put("section3", confDto.getSec3Desc());

			Date Reportdate = java.util.Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			parameters.put("date", dateFormat.format(Reportdate));

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportList);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(Reportdate);
				long timeInMillis = cal.getTimeInMillis();

				fileName.append(dir).append(File.separator).append(reportName).append("_").append(timeInMillis)
						.append(".").append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName.toString());
			}
		}
	}

	public class PrintDefault {
		private List<String> country;
		private String prId, selName, errorMsg;
		private HttpServletRequest request;
		private HttpServletResponse response;
		private LetterContConfDto defConf;

		public PrintDefault() {
		}

		public PrintDefault(List<String> country, String prId, String selName, String errorMsg,
				HttpServletRequest request, HttpServletResponse response, LetterContConfDto defConf) {
			super();
			this.country = country;
			this.prId = prId;
			this.selName = selName;
			this.errorMsg = errorMsg;
			this.request = request;
			this.response = response;
			this.defConf = defConf;
		}

		private void print() throws Exception {
			List<PrStat> prStatList = new ArrayList<PrStat>();
			for (int i = 0; i < country.size(); i++) {
				prStatList.addAll(prodStatServ.searchProductStatus(Integer.parseInt(prId),
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), country.get(i), 0));
			}

			// Validate data size - do not proceed to report generation if no data found
			if (prStatList.isEmpty() || prStatList.size() == 0) {
				model.put("error", msgSource.getMessage("msgReportNoData", new Object[] {}, Locale.getDefault()));
				model.remove("success");
				return;// new ModelAndView("/main/product/declaration2", model);
			}

			// List to store country name and permissibility status
			List<PrStat> reportList = new ArrayList<PrStat>();
			for (PrStat prStat : prStatList) {
				// Get country name
				String countryName = refCountryServ.searchCountry(prStat.getCountryId(), "",
						String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0).getCountryName();
				// Set country name for each product
				prStat.setCountryName(countryName);
				// Set Status name for each product
				if (prStat.getFinalStatus() == PERMITTED) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + PERMITTED, null, Locale.getDefault()));
				} else if (prStat.getFinalStatus() == NOTPERMITTED) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + NOTPERMITTED, null, Locale.getDefault()));
				} else if (prStat.getFinalStatus() == NOTSURE) {
					prStat.setStatusName(msgSource.getMessage("vfStatus_" + NOTSURE, null, Locale.getDefault()));
				}
				reportList.add(prStat);
			}

			// Initialize objects for report
			// Get current user
			Usr loginUsr = loginServ.searchUser(request.getRemoteUser());
			Organization org = orgServ.searchOrganization(loginUsr.getOrgId(), "", "",
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "").get(0);
			Country con = refCountryServ.searchCountry(org.getCountry(), "",
					String.valueOf(CommonConstants.SEARCH_OPTION_EXACT), "", "", "").get(0);
			State sta = refStateServ.searchState(org.getState()).get(0);
			// Set address for report
			String address = genAddressStr(org, con, sta);
			String base64Encoded = "";
			List<String> selectedCountry = new ArrayList<>();

			for (int i = 0; i < prStatList.size(); i++) {
				selectedCountry.add(prStatList.get(i).getCountryName());

			}
			if (org.getLogo() != null) {

				byte[] encodeBase64 = Base64.encodeBase64(org.getLogo());
				try {
					base64Encoded = new String(encodeBase64, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					errorMsg += e.toString();
				}
			}

			// Print report
			StringBuffer fileName = new StringBuffer();
			String reportName = msgSource.getMessage(REPORT_ID, new Object[] {}, Locale.getDefault());

			InputStream reportStream = getClass().getResourceAsStream("/report/" + REPORT_ID + ".jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

			Map<String, Object> parameters = new HashMap<String, Object>();
			// Set parameters for report fields
			parameters.put("product", selName);
			parameters.put("address", address);
			parameters.put("orgName", org.getOrgaName());
			parameters.put("logo", base64Encoded);
			parameters.put("tel", org.getTel());
			parameters.put("fax", org.getFax());
			parameters.put("section1", defConf.getSec1Desc());
			//parameters.put("section2", defConf.getSec2Desc());
			parameters.put("section2", reGenerateSec2Desc(defConf.getSec2Desc(), defConf.getSec3Desc()));
			parameters.put("section3", defConf.getSec3Desc());

			Date Reportdate = java.util.Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			parameters.put("date", dateFormat.format(Reportdate));

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportList);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(Reportdate);
				long timeInMillis = cal.getTimeInMillis();

				fileName.append(reportName).append("_").append(timeInMillis).append(".")
						.append(CommonConstants.REPORT_FORMAT_PDF.toLowerCase());

				response.setContentType("application/x-download");
				response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + fileName.toString() + "\"");

				OutputStream outputStream = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			}

		}
	}
}
