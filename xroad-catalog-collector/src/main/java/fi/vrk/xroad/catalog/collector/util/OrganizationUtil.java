/**
 * The MIT License
 *
 * Copyright (c) 2023- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2016-2023 Finnish Digital Agency
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fi.vrk.xroad.catalog.collector.util;

import fi.vrk.xroad.catalog.collector.wsimport.ClientType;
import fi.vrk.xroad.catalog.persistence.CatalogService;
import fi.vrk.xroad.catalog.persistence.entity.Address;
import fi.vrk.xroad.catalog.persistence.entity.BusinessAddress;
import fi.vrk.xroad.catalog.persistence.entity.BusinessAuxiliaryName;
import fi.vrk.xroad.catalog.persistence.entity.BusinessIdChange;
import fi.vrk.xroad.catalog.persistence.entity.BusinessLine;
import fi.vrk.xroad.catalog.persistence.entity.BusinessName;
import fi.vrk.xroad.catalog.persistence.entity.Company;
import fi.vrk.xroad.catalog.persistence.entity.CompanyForm;
import fi.vrk.xroad.catalog.persistence.entity.ContactDetail;
import fi.vrk.xroad.catalog.persistence.entity.Email;
import fi.vrk.xroad.catalog.persistence.entity.ErrorLog;
import fi.vrk.xroad.catalog.persistence.entity.Language;
import fi.vrk.xroad.catalog.persistence.entity.Liquidation;
import fi.vrk.xroad.catalog.persistence.entity.Organization;
import fi.vrk.xroad.catalog.persistence.entity.OrganizationDescription;
import fi.vrk.xroad.catalog.persistence.entity.OrganizationName;
import fi.vrk.xroad.catalog.persistence.entity.PhoneNumber;
import fi.vrk.xroad.catalog.persistence.entity.PostOffice;
import fi.vrk.xroad.catalog.persistence.entity.PostOfficeBox;
import fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddress;
import fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressAdditionalInformation;
import fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipality;
import fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipalityName;
import fi.vrk.xroad.catalog.persistence.entity.RegisteredEntry;
import fi.vrk.xroad.catalog.persistence.entity.RegisteredOffice;
import fi.vrk.xroad.catalog.persistence.entity.Street;
import fi.vrk.xroad.catalog.persistence.entity.StreetAddress;
import fi.vrk.xroad.catalog.persistence.entity.StreetAddressAdditionalInformation;
import fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipality;
import fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipalityName;
import fi.vrk.xroad.catalog.persistence.entity.StreetAddressPostOffice;
import fi.vrk.xroad.catalog.persistence.entity.WebPage;
import lombok.extern.slf4j.Slf4j;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class OrganizationUtil {

    private static final String TOTAL_RESULTS = "true";
    private static final String RESULTS_FROM = "0";
    private static final String REGISTRATION_FROM = "1970-01-01";
    private static final String WITH_BUSINESS_CODE = "with businessCode ";
    private static final String DESCRIPTION = "description";
    private static final String LANGUAGE = "language";
    private static final String VALUE = "value";
    private static final String REGISTRATION_DATE = "registrationDate";
    private static final String END_DATE = "endDate";
    private static final String SOURCE = "source";
    private static final String VERSION = "version";
    private static final String ORDER = "order";

    private OrganizationUtil() {

    }

    public static JSONObject getCompanies(ClientType clientType,
            Integer fetchCompaniesLimit,
            String url,
            CatalogService catalogService) {
        final String fetchCompaniesUrl = new StringBuilder()
                .append(url)
                .append("?totalResults=").append(TOTAL_RESULTS)
                .append("&maxResults=").append(String.valueOf(fetchCompaniesLimit))
                .append("&resultsFrom=").append(RESULTS_FROM)
                .append("&companyRegistrationFrom=").append(REGISTRATION_FROM)
                .toString();
        JSONObject jsonObject = new JSONObject();
        try {
            String ret = getResponseBody(fetchCompaniesUrl);
            jsonObject = new JSONObject(ret);
            return jsonObject;
        } catch (KeyStoreException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyStoreException occurred when fetching list of companies from url " + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyStoreException occurred when fetching companies from url {}", url);
        } catch (NoSuchAlgorithmException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "NoSuchAlgorithmException occurred when fetching companies from url " + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("NoSuchAlgorithmException occurred when fetching companies from url", url);
        } catch (KeyManagementException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyManagementException occurred when fetching companies from url " + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyManagementException occurred when fetching companies from url {}", url);
        } catch (Exception e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "Exception occurred when fetching companies from url " + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("Exception occurred when fetching companies from url {}", url);
        }
        return jsonObject;
    }

    public static JSONObject getCompany(ClientType clientType, String url, String businessCode,
            CatalogService catalogService) {
        final String fetchCompaniesUrl = new StringBuilder().append(url)
                .append("/").append(businessCode).toString();
        JSONObject jsonObject = new JSONObject();
        try {
            String ret = getResponseBody(fetchCompaniesUrl);
            jsonObject = new JSONObject(ret);
            return jsonObject;
        } catch (KeyStoreException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyStoreException occurred when fetching companies from url " + url
                            + WITH_BUSINESS_CODE + businessCode,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyStoreException occurred when fetching companies from url {} with businessCode {}",
                    url, businessCode);
        } catch (NoSuchAlgorithmException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "NoSuchAlgorithmException occurred when fetching companies from url " + url
                            + WITH_BUSINESS_CODE + businessCode,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("NoSuchAlgorithmException occurred when fetching companies from url {} with businessCode {}",
                    url, businessCode);
        } catch (KeyManagementException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyManagementException occurred when fetching companies from url " + url
                            + WITH_BUSINESS_CODE + businessCode,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyManagementException occurred when fetching companies from url {} with businessCode {}",
                    url, businessCode);
        } catch (Exception e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "Exception occurred when fetching companies from url " + url
                            + WITH_BUSINESS_CODE + businessCode,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("Exception occurred when fetching companies from url {} with businessCode {}", url,
                    businessCode);
        }
        return jsonObject;
    }

    public static List<String> getOrganizationIdsList(ClientType clientType, String url,
            Integer fetchOrganizationsLimit, CatalogService catalogService)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        List<String> idsList = new ArrayList<>();
        try {
            String response = getResponseBody(url);
            JSONObject json = new JSONObject(response);
            JSONArray itemList = json.optJSONArray("itemList");
            int totalFetchAmount = itemList.length() > fetchOrganizationsLimit ? fetchOrganizationsLimit
                    : itemList.length();
            for (int i = 0; i < totalFetchAmount; i++) {
                String id = itemList.optJSONObject(i).optString("id");
                idsList.add(id);
            }
            return idsList;
        } catch (KeyStoreException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyStoreException occurred when fetching organization ids with from url "
                            + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyStoreException occurred when fetching organization ids with from url {}", url);
        } catch (NoSuchAlgorithmException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "NoSuchAlgorithmException occurred when fetching organization ids with from url "
                            + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("NoSuchAlgorithmException occurred when fetching organization ids with from url {}",
                    url);
        } catch (KeyManagementException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyManagementException occurred when fetching organization ids with from url "
                            + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyManagementException occurred when fetching organizations with from url {}", url);
        } catch (Exception e) {
            log.error("Exception occurred when fetching organization ids: " + e.getMessage());
            ErrorLog errorLog = ErrorLog.builder()
                    .created(LocalDateTime.now())
                    .message("Exception occurred when fetching organization ids: " + e.getMessage())
                    .code("500")
                    .xRoadInstance(clientType.getId().getXRoadInstance())
                    .memberClass(clientType.getId().getMemberClass())
                    .memberCode(clientType.getId().getMemberCode())
                    .groupCode(clientType.getId().getGroupCode())
                    .securityCategoryCode(clientType.getId().getSecurityCategoryCode())
                    .serverCode(clientType.getId().getServerCode())
                    .serviceCode(clientType.getId().getServiceCode())
                    .serviceVersion(clientType.getId().getServiceVersion())
                    .subsystemCode(clientType.getId().getSubsystemCode())
                    .build();
            catalogService.saveErrorLog(errorLog);
        }
        return idsList;
    }

    public static Organization createOrganization(JSONObject jsonObject) {
        return Organization.builder().businessCode(jsonObject.optString("businessCode"))
                .guid(jsonObject.optString("id"))
                .organizationType(jsonObject.optString("organizationType"))
                .publishingStatus(jsonObject.optString("publishingStatus"))
                .build();
    }

    public static List<Email> createEmails(JSONArray jsonArray) {
        List<Email> emails = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            emails.add(Email.builder()
                    .description(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(DESCRIPTION)))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return emails;
    }

    public static List<WebPage> createWebPages(JSONArray jsonArray) {
        List<WebPage> webPages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            webPages.add(WebPage.builder()
                    .url(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString("url")))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return webPages;
    }

    public static List<OrganizationDescription> createDescriptions(JSONArray jsonArray) {
        List<OrganizationDescription> organizationDescriptions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            organizationDescriptions.add(OrganizationDescription.builder()
                    .type(jsonArray.optJSONObject(i).optString("type"))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return organizationDescriptions;
    }

    private static String replaceUnicodeControlCharacters(String input) {
        return input.replaceAll(
                "[\\x{0000}-\\x{0009}]|[\\x{000b}-\\x{000c}]|[\\x{000e}-\\x{000f}]|[\\x{0010}-\\x{001f}]",
                "");
    }

    public static List<OrganizationName> createNames(JSONArray jsonArray) {
        List<OrganizationName> organizationNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            organizationNames.add(OrganizationName.builder()
                    .type(jsonArray.optJSONObject(i).optString("type"))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return organizationNames;
    }

    public static List<Address> createAddresses(JSONArray jsonArray) {
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            addresses.add(Address.builder()
                    .type(jsonArray.optJSONObject(i).optString("type"))
                    .subType(jsonArray.optJSONObject(i).optString("subType"))
                    .country(jsonArray.optJSONObject(i).optString("country")).build());
        }
        return addresses;
    }

    public static StreetAddress createStreetAddress(JSONObject jsonObject) {
        return StreetAddress.builder()
                .streetNumber(jsonObject.optString("streetNumber"))
                .postalCode(jsonObject.optString("postalCode"))
                .latitude(jsonObject.optString("latitude"))
                .longitude(jsonObject.optString("longitude"))
                .coordinateState(jsonObject.optString("coordinateState")).build();
    }

    public static StreetAddressMunicipality createStreetAddressMunicipality(JSONObject jsonObject) {
        return StreetAddressMunicipality.builder()
                .code(jsonObject.optString("code")).build();
    }

    public static List<StreetAddressMunicipalityName> createStreetAddressMunicipalityNames(JSONArray jsonArray) {
        List<StreetAddressMunicipalityName> streetAddressMunicipalityNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            streetAddressMunicipalityNames.add(StreetAddressMunicipalityName.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return streetAddressMunicipalityNames;
    }

    public static List<StreetAddressAdditionalInformation> createStreetAddressAdditionalInformation(
            JSONArray jsonArray) {
        List<StreetAddressAdditionalInformation> additionalInformationList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            additionalInformationList.add(StreetAddressAdditionalInformation.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return additionalInformationList;
    }

    public static List<StreetAddressPostOffice> createStreetAddressPostOffices(JSONArray jsonArray) {
        List<StreetAddressPostOffice> postOffices = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            postOffices.add(StreetAddressPostOffice.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return postOffices;
    }

    public static List<Street> createStreets(JSONArray jsonArray) {
        List<Street> streets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            streets.add(Street.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return streets;
    }

    public static PostOfficeBoxAddress createPostOfficeBoxAddress(JSONObject jsonObject) {
        return PostOfficeBoxAddress.builder()
                .postalCode(jsonObject.optString("postalCode")).build();
    }

    public static List<PostOfficeBoxAddressAdditionalInformation> createPostOfficeBoxAddressAdditionalInformation(
            JSONArray jsonArray) {
        List<PostOfficeBoxAddressAdditionalInformation> additionalInformationList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            additionalInformationList.add(PostOfficeBoxAddressAdditionalInformation.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString(VALUE)))
                    .build());
        }
        return additionalInformationList;
    }

    public static List<PostOfficeBox> createPostOfficeBoxes(JSONArray jsonArray) {
        List<PostOfficeBox> postOfficeBoxes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            postOfficeBoxes.add(PostOfficeBox.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return postOfficeBoxes;
    }

    public static List<PostOffice> createPostOffice(JSONArray jsonArray) {
        List<PostOffice> postOffices = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            postOffices.add(PostOffice.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return postOffices;
    }

    public static PostOfficeBoxAddressMunicipality createPostOfficeBoxAddressMunicipality(JSONObject jsonObject) {
        return PostOfficeBoxAddressMunicipality.builder()
                .code(jsonObject.optString("code")).build();
    }

    public static List<PostOfficeBoxAddressMunicipalityName> createPostOfficeBoxAddressMunicipalityNames(
            JSONArray jsonArray) {
        List<PostOfficeBoxAddressMunicipalityName> streetAddressMunicipalityNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            streetAddressMunicipalityNames.add(PostOfficeBoxAddressMunicipalityName.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE)).build());
        }
        return streetAddressMunicipalityNames;
    }

    public static List<PhoneNumber> createPhoneNumbers(JSONArray jsonArray) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            phoneNumbers.add(PhoneNumber.builder()
                    .additionalInformation(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString("additionalInformation")))
                    .number(jsonArray.optJSONObject(i).optString("number"))
                    .isFinnishServiceNumber(
                            jsonArray.optJSONObject(i).getBoolean("isFinnishServiceNumber"))
                    .prefixNumber(jsonArray.optJSONObject(i).optString("prefixNumber"))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .chargeDescription(replaceUnicodeControlCharacters(
                            jsonArray.optJSONObject(i).optString("chargeDescription")))
                    .serviceChargeType(jsonArray.optJSONObject(i).optString("serviceChargeType"))
                    .build());
        }
        return phoneNumbers;
    }

    public static Company createCompany(JSONObject jsonObject) {
        return Company.builder().businessId(jsonObject.optString("businessId"))
                .companyForm(jsonObject.optString("companyForm"))
                .detailsUri(jsonObject.optString("detailsUri"))
                .name(jsonObject.optString("name"))
                .registrationDate(parseDateFromString(jsonObject.optString(REGISTRATION_DATE)))
                .build();
    }

    public static List<BusinessAddress> createBusinessAddresses(JSONArray jsonArray) {
        List<BusinessAddress> businessAddresses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            businessAddresses.add(BusinessAddress.builder()
                    .careOf(jsonArray.optJSONObject(i).optString("careOf"))
                    .city(jsonArray.optJSONObject(i).optString("city"))
                    .country(jsonArray.optJSONObject(i).optString("country"))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .postCode(jsonArray.optJSONObject(i).optString("postCode"))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .type(jsonArray.optJSONObject(i).optLong("type"))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .street(jsonArray.optJSONObject(i).optString("street"))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return businessAddresses;
    }

    public static List<BusinessAuxiliaryName> createBusinessAuxiliaryNames(JSONArray jsonArray) {
        List<BusinessAuxiliaryName> businessAuxiliaryNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            businessAuxiliaryNames.add(BusinessAuxiliaryName.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .ordering(jsonArray.optJSONObject(i).optLong(ORDER))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return businessAuxiliaryNames;
    }

    public static List<BusinessIdChange> createBusinessIdChanges(JSONArray jsonArray) {
        List<BusinessIdChange> businessIdChanges = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            businessIdChanges.add(BusinessIdChange.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .change(jsonArray.optJSONObject(i).optString("change"))
                    .changeDate(jsonArray.optJSONObject(i).optString("changeDate"))
                    .description(jsonArray.optJSONObject(i).optString(DESCRIPTION))
                    .reason(jsonArray.optJSONObject(i).optString("reason"))
                    .oldBusinessId(jsonArray.optJSONObject(i).optString("oldBusinessId"))
                    .newBusinessId(jsonArray.optJSONObject(i).optString("newBusinessId"))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .build());
        }
        return businessIdChanges;
    }

    public static List<BusinessLine> createBusinessLines(JSONArray jsonArray) {
        List<BusinessLine> businessLines = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            businessLines.add(BusinessLine.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .ordering(jsonArray.optJSONObject(i).optLong(ORDER))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return businessLines;
    }

    public static List<BusinessName> createBusinessNames(JSONArray jsonArray) {
        List<BusinessName> businessNames = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            businessNames.add(BusinessName.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .ordering(jsonArray.optJSONObject(i).optLong(ORDER))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return businessNames;
    }

    public static List<CompanyForm> createCompanyForms(JSONArray jsonArray) {
        List<CompanyForm> companyForms = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            companyForms.add(CompanyForm.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .type(jsonArray.optJSONObject(i).optLong("type"))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return companyForms;
    }

    public static List<ContactDetail> createContactDetails(JSONArray jsonArray) {
        List<ContactDetail> contactDetails = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            contactDetails.add(ContactDetail.builder()
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .type(jsonArray.optJSONObject(i).optString("type"))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .value(jsonArray.optJSONObject(i).optString(VALUE))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return contactDetails;
    }

    public static List<Language> createLanguages(JSONArray jsonArray) {
        List<Language> languages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            languages.add(Language.builder()
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return languages;
    }

    public static List<Liquidation> createLiquidations(JSONArray jsonArray) {
        List<Liquidation> liquidations = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            liquidations.add(Liquidation.builder()
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .type(jsonArray.optJSONObject(i).optLong("type"))
                    .registrationDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .build());
        }
        return liquidations;
    }

    public static List<RegisteredEntry> createRegisteredEntries(JSONArray jsonArray) {
        List<RegisteredEntry> registeredEntries = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            registeredEntries.add(RegisteredEntry.builder()
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .description(jsonArray.optJSONObject(i).optString(DESCRIPTION))
                    .register(jsonArray.optJSONObject(i).optLong("register"))
                    .status(jsonArray.optJSONObject(i).optLong("status"))
                    .authority(jsonArray.optJSONObject(i).optLong("authority"))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return registeredEntries;
    }

    public static List<RegisteredOffice> createRegisteredOffices(JSONArray jsonArray) {
        List<RegisteredOffice> registeredOffices = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            registeredOffices.add(RegisteredOffice.builder()
                    .source(jsonArray.optJSONObject(i).optLong(SOURCE))
                    .language(jsonArray.optJSONObject(i).optString(LANGUAGE))
                    .name(jsonArray.optJSONObject(i).optString("name"))
                    .ordering(jsonArray.optJSONObject(i).optLong(ORDER))
                    .version(jsonArray.optJSONObject(i).optLong(VERSION))
                    .registrationDate(parseDateFromString(
                            jsonArray.optJSONObject(i).optString(REGISTRATION_DATE)))
                    .endDate(parseDateFromString(jsonArray.optJSONObject(i).optString(END_DATE)))
                    .build());
        }
        return registeredOffices;
    }

    private static LocalDateTime parseDateFromString(String dateValue) {
        if (dateValue != null && !dateValue.isEmpty()) {
            return LocalDate.parse(dateValue).atStartOfDay();
        }
        return null;
    }

    public static JSONArray getDataByIds(ClientType clientType, List<String> guids, String url,
            CatalogService catalogService) {
        String requestGuids = "";
        for (int i = 0; i < guids.size(); i++) {
            requestGuids += guids.get(i);
            if (i < (guids.size() - 1)) {
                requestGuids += ",";
            }
        }

        final String listOrganizationsUrl = new StringBuilder().append(url)
                .append("/list?guids=").append(requestGuids).toString();

        JSONArray itemList = new JSONArray();
        try {
            String ret = getResponseBody(listOrganizationsUrl);
            JSONObject json = new JSONObject("{\"items\":" + ret + "}");
            itemList = json.optJSONArray("items");
            return itemList;
        } catch (KeyStoreException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyStoreException occurred when fetching organizations with from url " + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyStoreException occurred when fetching organizations with from url {}", url);
        } catch (NoSuchAlgorithmException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "NoSuchAlgorithmException occurred when fetching organizations with from url "
                            + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("NoSuchAlgorithmException occurred when fetching organizations with from url {}",
                    url);
        } catch (KeyManagementException e) {
            ErrorLog errorLog = MethodListUtil.createErrorLog(clientType,
                    "KeyManagementException occurred when fetching organizations with from url "
                            + url,
                    "500");
            catalogService.saveErrorLog(errorLog);
            log.error("KeyManagementException occurred when fetching organizations with from url {}", url);
        } catch (Exception e) {
            log.error("Exception occurred when fetching organization data: " + e.getMessage());
            ErrorLog errorLog = ErrorLog.builder()
                    .created(LocalDateTime.now())
                    .message("Exception occurred when fetching organization data: "
                            + e.getMessage())
                    .code("500")
                    .xRoadInstance(clientType.getId().getXRoadInstance())
                    .memberClass(clientType.getId().getMemberClass())
                    .memberCode(clientType.getId().getMemberCode())
                    .groupCode(clientType.getId().getGroupCode())
                    .securityCategoryCode(clientType.getId().getSecurityCategoryCode())
                    .serverCode(clientType.getId().getServerCode())
                    .serviceCode(clientType.getId().getServiceCode())
                    .serviceVersion(clientType.getId().getServiceVersion())
                    .subsystemCode(clientType.getId().getSubsystemCode())
                    .build();
            catalogService.saveErrorLog(errorLog);
        }
        return itemList;
    }

    public static String getResponseBody(String url)
            throws KeyStoreException, NoSuchAlgorithmException,
            KeyManagementException {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = createTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    private static RestTemplate createTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
                return true;
            }
        };
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setSSLSocketFactory(csf)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        return restTemplate;
    }
}
