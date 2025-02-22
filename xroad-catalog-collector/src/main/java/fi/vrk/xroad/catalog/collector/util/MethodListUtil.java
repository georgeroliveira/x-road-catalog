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
import fi.vrk.xroad.catalog.collector.wsimport.XRoadObjectType;
import fi.vrk.xroad.catalog.persistence.CatalogService;
import fi.vrk.xroad.catalog.persistence.entity.ErrorLog;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class MethodListUtil {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private static SecurityServerMetadata securityServerMetadata;

    private MethodListUtil() {
        // Private empty constructor
    }

    public static List<XRoadRestServiceIdentifierType> methodListFromResponse(ClientType clientType,
            String host,
            String xRoadInstance,
            String memberClass,
            String memberCode,
            String subsystemCode,
            CatalogService catalogService) {
        final String url = new StringBuilder().append(host).append("/r1/")
                .append(clientType.getId().getXRoadInstance()).append("/")
                .append(clientType.getId().getMemberClass()).append("/")
                .append(clientType.getId().getMemberCode()).append("/")
                .append(clientType.getId().getSubsystemCode()).append("/listMethods").toString();

        String xRoadClientHeader = createHeader(xRoadInstance, memberClass, memberCode, subsystemCode);
        List<XRoadRestServiceIdentifierType> restServices = new ArrayList<>();
        JSONObject json = MethodListUtil.getJSON(url, clientType, xRoadClientHeader, catalogService);
        if (json != null) {
            JSONArray serviceList = json.getJSONArray("service");
            for (int i = 0; i < serviceList.length(); i++) {
                JSONObject service = serviceList.getJSONObject(i);
                XRoadRestServiceIdentifierType xRoadRestServiceIdentifierType = new XRoadRestServiceIdentifierType();
                xRoadRestServiceIdentifierType.setMemberCode(service.optString("member_code"));
                xRoadRestServiceIdentifierType.setSubsystemCode(service.optString("subsystem_code"));
                xRoadRestServiceIdentifierType.setMemberClass(service.optString("member_class"));
                xRoadRestServiceIdentifierType.setServiceCode(service.optString("service_code"));
                xRoadRestServiceIdentifierType.setServiceVersion(
                        service.has("service_version") ? service.optString("service_version")
                                : null);
                xRoadRestServiceIdentifierType.setXRoadInstance(service.optString("xroad_instance"));
                xRoadRestServiceIdentifierType
                        .setObjectType(XRoadObjectType
                                .fromValue(service.optString("object_type")));
                xRoadRestServiceIdentifierType
                        .setServiceType(service.has("service_type")
                                ? service.optString("service_type")
                                : null);
                JSONArray endpointList = service.optJSONArray("endpoint_list");
                List<Endpoint> endpoints = new ArrayList<>();
                for (int j = 0; j < endpointList.length(); j++) {
                    JSONObject endpoint = endpointList.getJSONObject(j);
                    endpoints.add(Endpoint.builder().method(endpoint.optString("method"))
                            .path(endpoint.optString("path")).build());
                }
                xRoadRestServiceIdentifierType.setEndpoints(endpoints);
                restServices.add(xRoadRestServiceIdentifierType);
            }
        }

        return restServices;
    }

    public static String openApiFromResponse(ClientType clientType,
            String host,
            String xRoadInstance,
            String memberClass,
            String memberCode,
            String subsystemCode,
            CatalogService catalogService) {
        final String url = new StringBuilder().append(host).append("/r1/")
                .append(clientType.getId().getXRoadInstance()).append("/")
                .append(clientType.getId().getMemberClass()).append("/")
                .append(clientType.getId().getMemberCode()).append("/")
                .append(clientType.getId().getSubsystemCode()).append("/getOpenAPI?serviceCode=")
                .append(clientType.getId().getServiceCode()).toString();

        String xRoadClientHeader = createHeader(xRoadInstance, memberClass, memberCode, subsystemCode);
        JSONObject json = MethodListUtil.getJSON(url, clientType, xRoadClientHeader, catalogService);

        return (json != null) ? json.toString() : "";
    }

    public static List<fi.vrk.xroad.catalog.collector.util.Endpoint> getEndpointList(
            XRoadRestServiceIdentifierType service) {
        List<fi.vrk.xroad.catalog.collector.util.Endpoint> endpointList = new ArrayList<>();
        for (fi.vrk.xroad.catalog.collector.util.Endpoint endpoint : service.getEndpoints()) {
            endpointList.add(Endpoint.builder().method(endpoint.getMethod()).path(endpoint.getPath())
                    .build());
        }
        return endpointList;
    }

    private static String createHeader(String xRoadInstance, String memberClass, String memberCode,
            String subsystemCode) {
        return new StringBuilder()
                .append(xRoadInstance).append("/")
                .append(memberClass).append("/")
                .append(memberCode).append("/")
                .append(subsystemCode).toString();
    }

    private static JSONObject getJSON(String url, ClientType clientType, String xRoadClientHeader,
            CatalogService catalogService) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        headers.set("X-Road-Client", xRoadClientHeader);
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = REST_TEMPLATE.exchange(url, HttpMethod.GET, entity,
                    String.class);
            return new JSONObject(response.getBody());
        } catch (Exception e) {
            SecurityServerMetadata newSecurityServerMetadata = SecurityServerMetadata.builder()
                    .xRoadInstance(clientType.getId().getXRoadInstance())
                    .memberClass(clientType.getId().getMemberClass())
                    .memberCode(clientType.getId().getMemberCode())
                    .build();
            if (!newSecurityServerMetadata.equals(securityServerMetadata)) {
                log.error("Fetch of REST services failed: " + e.getMessage());
                ErrorLog errorLog = ErrorLog.builder()
                        .created(LocalDateTime.now())
                        .message("Fetch of REST services failed(url: " + url + "): "
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
                securityServerMetadata = SecurityServerMetadata.builder()
                        .xRoadInstance(clientType.getId().getXRoadInstance())
                        .memberClass(clientType.getId().getMemberClass())
                        .memberCode(clientType.getId().getMemberCode())
                        .build();
            }
            return null;
        }
    }
}
