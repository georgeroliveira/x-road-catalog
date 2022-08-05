/**
 * The MIT License
 * Copyright (c) 2022, Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vrk.xroad.catalog.lister.util;

import fi.vrk.xroad.catalog.lister.CatalogListerRuntimeException;
import fi.vrk.xroad.catalog.persistence.entity.OpenApi;
import fi.vrk.xroad.catalog.persistence.entity.Service;
import fi.vrk.xroad.catalog.persistence.entity.Wsdl;
import fi.vrk.xroad.xroad_catalog_lister.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class JaxbUtil {

    private JaxbUtil() {

    }

    public static fi.vrk.xroad.xroad_catalog_lister.Service convertService(Service service, boolean onlyActiveChildren) {
        fi.vrk.xroad.xroad_catalog_lister.Service cs = new fi.vrk.xroad.xroad_catalog_lister.Service();
        cs.setChanged(toXmlGregorianCalendar(service.getStatusInfo().getChanged()));
        cs.setCreated(toXmlGregorianCalendar(service.getStatusInfo().getCreated()));
        cs.setFetched(toXmlGregorianCalendar(service.getStatusInfo().getFetched()));
        cs.setRemoved(toXmlGregorianCalendar(service.getStatusInfo().getRemoved()));
        cs.setServiceCode(service.getServiceCode());
        cs.setServiceVersion(service.getServiceVersion());
        Wsdl wsdl = null;
        if (onlyActiveChildren) {
            if (service.getWsdl() != null && !service.getWsdl().getStatusInfo().isRemoved()) {
                wsdl = service.getWsdl();
            }
        } else {
            wsdl = service.getWsdl();
        }
        if (wsdl != null) {
            cs.setWsdl(convertWsdl(service.getWsdl()));
        }
        OpenApi openApi = null;
        if (onlyActiveChildren) {
            if (service.getOpenApi() != null && !service.getOpenApi().getStatusInfo().isRemoved()) {
                openApi = service.getOpenApi();
            }
        } else {
            openApi = service.getOpenApi();
        }
        if (openApi != null) {
            cs.setOpenapi(convertOpenApi(service.getOpenApi()));
        }
        return cs;
    }

    public static WSDL convertWsdl(Wsdl wsdl) {
        WSDL cw = new WSDL();
        cw.setChanged(toXmlGregorianCalendar(wsdl.getStatusInfo().getChanged()));
        cw.setCreated(toXmlGregorianCalendar(wsdl.getStatusInfo().getCreated()));
        cw.setFetched(toXmlGregorianCalendar(wsdl.getStatusInfo().getFetched()));
        cw.setRemoved(toXmlGregorianCalendar(wsdl.getStatusInfo().getRemoved()));
        cw.setExternalId(wsdl.getExternalId());
        return cw;
    }

    public static OPENAPI convertOpenApi(OpenApi openApi) {
        OPENAPI cw = new OPENAPI();
        cw.setChanged(toXmlGregorianCalendar(openApi.getStatusInfo().getChanged()));
        cw.setCreated(toXmlGregorianCalendar(openApi.getStatusInfo().getCreated()));
        cw.setFetched(toXmlGregorianCalendar(openApi.getStatusInfo().getFetched()));
        cw.setRemoved(toXmlGregorianCalendar(openApi.getStatusInfo().getRemoved()));
        cw.setExternalId(openApi.getExternalId());
        return cw;
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            GregorianCalendar cal = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
            XMLGregorianCalendar xc;
            try {
                xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            } catch (DatatypeConfigurationException e) {
                throw new CatalogListerRuntimeException("Cannot instantiate DatatypeFactory");
            }
            return xc;
        }
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.OrganizationName> convertOrganizationNames(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.OrganizationName> organizationNames) {
        List<fi.vrk.xroad.xroad_catalog_lister.OrganizationName> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.OrganizationName organizationName: organizationNames) {
            fi.vrk.xroad.xroad_catalog_lister.OrganizationName co = new fi.vrk.xroad.xroad_catalog_lister.OrganizationName();
            co.setChanged(toXmlGregorianCalendar(organizationName.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(organizationName.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(organizationName.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(organizationName.getStatusInfo().getRemoved()));
            co.setLanguage(organizationName.getLanguage());
            co.setType(organizationName.getType());
            co.setValue(organizationName.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.OrganizationDescription> convertOrganizationDescriptions(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.OrganizationDescription> organizationDescriptions) {
        List<fi.vrk.xroad.xroad_catalog_lister.OrganizationDescription> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.OrganizationDescription organizationDescription: organizationDescriptions) {
            fi.vrk.xroad.xroad_catalog_lister.OrganizationDescription co = new fi.vrk.xroad.xroad_catalog_lister.OrganizationDescription();
            co.setChanged(toXmlGregorianCalendar(organizationDescription.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(organizationDescription.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(organizationDescription.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(organizationDescription.getStatusInfo().getRemoved()));
            co.setLanguage(organizationDescription.getLanguage());
            co.setType(organizationDescription.getType());
            co.setValue(organizationDescription.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.Email> convertEmails(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Email> emails) {
        List<fi.vrk.xroad.xroad_catalog_lister.Email> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.Email email: emails) {
            fi.vrk.xroad.xroad_catalog_lister.Email co = new fi.vrk.xroad.xroad_catalog_lister.Email();
            co.setChanged(toXmlGregorianCalendar(email.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(email.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(email.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(email.getStatusInfo().getRemoved()));
            co.setLanguage(email.getLanguage());
            co.setDescription(email.getDescription());
            co.setValue(email.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PhoneNumber> convertPhoneNumbers(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PhoneNumber> phoneNumbers) {
        List<fi.vrk.xroad.xroad_catalog_lister.PhoneNumber> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PhoneNumber phoneNumber: phoneNumbers) {
            fi.vrk.xroad.xroad_catalog_lister.PhoneNumber co = new fi.vrk.xroad.xroad_catalog_lister.PhoneNumber();
            co.setChanged(toXmlGregorianCalendar(phoneNumber.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(phoneNumber.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(phoneNumber.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(phoneNumber.getStatusInfo().getRemoved()));
            co.setLanguage(phoneNumber.getLanguage());
            co.setAdditionalInformation(phoneNumber.getAdditionalInformation());
            co.setChargeDescription(phoneNumber.getChargeDescription());
            co.setNumber(phoneNumber.getNumber());
            co.setIsFinnishServiceNumber(phoneNumber.getIsFinnishServiceNumber());
            co.setPrefixNumber(phoneNumber.getPrefixNumber());
            co.setServiceChargeType(phoneNumber.getServiceChargeType());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.WebPage> convertWebPages(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.WebPage> webPages) {
        List<fi.vrk.xroad.xroad_catalog_lister.WebPage> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.WebPage webPage: webPages) {
            fi.vrk.xroad.xroad_catalog_lister.WebPage co = new fi.vrk.xroad.xroad_catalog_lister.WebPage();
            co.setChanged(toXmlGregorianCalendar(webPage.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(webPage.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(webPage.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(webPage.getStatusInfo().getRemoved()));
            co.setLanguage(webPage.getLanguage());
            co.setUrl(webPage.getUrl());
            co.setValue(webPage.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.Address> convertAddresses(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Address> addresses) {
        List<fi.vrk.xroad.xroad_catalog_lister.Address> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.Address address: addresses) {
            fi.vrk.xroad.xroad_catalog_lister.Address co = new fi.vrk.xroad.xroad_catalog_lister.Address();
            co.setChanged(toXmlGregorianCalendar(address.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(address.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(address.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(address.getStatusInfo().getRemoved()));
            co.setCountry(address.getCountry());
            co.setSubType(address.getSubType());
            co.setType(address.getType());

            co.setStreetAddresses(new StreetAddressList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddress> streetAddresses = address.getAllStreetAddresses();
            if (streetAddresses != null) {
                co.getStreetAddresses().getStreetAddress().addAll(convertStreetAddresses(streetAddresses));
            }

            co.setPostOfficeBoxAddresses(new PostOfficeBoxAddressList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddress> postOfficeBoxAddresses
                    = address.getAllPostOfficeBoxAddresses();
            if (postOfficeBoxAddresses != null) {
                co.getPostOfficeBoxAddresses().getPostOfficeBoxAddress().addAll(convertPostOfficeBoxAddresses(postOfficeBoxAddresses));
            }

            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.StreetAddress> convertStreetAddresses(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddress> streetAddresses) {
        List<fi.vrk.xroad.xroad_catalog_lister.StreetAddress> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.StreetAddress streetAddress: streetAddresses) {
            fi.vrk.xroad.xroad_catalog_lister.StreetAddress co = new fi.vrk.xroad.xroad_catalog_lister.StreetAddress();
            co.setChanged(toXmlGregorianCalendar(streetAddress.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(streetAddress.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(streetAddress.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(streetAddress.getStatusInfo().getRemoved()));
            co.setPostalCode(streetAddress.getPostalCode());
            co.setLatitude(streetAddress.getLatitude());
            co.setLongitude(streetAddress.getLongitude());
            co.setCoordinateState(streetAddress.getCoordinateState());

            co.setStreets(new StreetList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Street> streets = streetAddress.getAllStreets();
            if (streets != null) {
                co.getStreets().getStreet().addAll(convertStreets(streets));
            }

            co.setPostOffices(new StreetAddressPostOfficeList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressPostOffice> streetAddressPostOffices = streetAddress.getAllPostOffices();
            if (streetAddressPostOffices != null) {
                co.getPostOffices().getStreetAddressPostOffice().addAll(convertStreetAddressPostOffices(streetAddressPostOffices));
            }

            co.setMunicipalities(new StreetAddressMunicipalityList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipality> streetAddressMunicipalities = streetAddress.getAllMunicipalities();
            if (streetAddressMunicipalities != null) {
                co.getMunicipalities().getStreetAddressMunicipality().addAll(convertStreetAddressMunicipalities(streetAddressMunicipalities));
            }

            co.setAdditionalInformation(new StreetAddressAdditionalInformationList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressAdditionalInformation> streetAddressAdditionalInformation
                    = streetAddress.getAllAdditionalInformation();
            if (streetAddressAdditionalInformation != null) {
                co.getAdditionalInformation().getStreetAddressAdditionalInformation()
                        .addAll(convertStreetAddressAdditionalInformation(streetAddressAdditionalInformation));
            }

            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.Street> convertStreets(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Street> streets) {
        List<fi.vrk.xroad.xroad_catalog_lister.Street> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.Street street: streets) {
            fi.vrk.xroad.xroad_catalog_lister.Street co = new fi.vrk.xroad.xroad_catalog_lister.Street();
            co.setChanged(toXmlGregorianCalendar(street.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(street.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(street.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(street.getStatusInfo().getRemoved()));
            co.setLanguage(street.getLanguage());
            co.setValue(street.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.StreetAddressPostOffice> convertStreetAddressPostOffices(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressPostOffice> streetAddressPostOffices) {
        List<fi.vrk.xroad.xroad_catalog_lister.StreetAddressPostOffice> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.StreetAddressPostOffice streetAddressPostOffice: streetAddressPostOffices) {
            fi.vrk.xroad.xroad_catalog_lister.StreetAddressPostOffice co = new fi.vrk.xroad.xroad_catalog_lister.StreetAddressPostOffice();
            co.setChanged(toXmlGregorianCalendar(streetAddressPostOffice.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(streetAddressPostOffice.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(streetAddressPostOffice.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(streetAddressPostOffice.getStatusInfo().getRemoved()));
            co.setLanguage(streetAddressPostOffice.getLanguage());
            co.setValue(streetAddressPostOffice.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipality> convertStreetAddressMunicipalities(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipality> streetAddressMunicipalities) {
        List<fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipality> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipality streetAddressMunicipality: streetAddressMunicipalities) {
            fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipality co = new fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipality();
            co.setChanged(toXmlGregorianCalendar(streetAddressMunicipality.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(streetAddressMunicipality.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(streetAddressMunicipality.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(streetAddressMunicipality.getStatusInfo().getRemoved()));
            co.setCode(streetAddressMunicipality.getCode());

            co.setStreetAddressMunicipalityNames(new StreetAddressMunicipalityNameList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipalityName> streetAddressMunicipalityNames
                    = streetAddressMunicipality.getAllMunicipalityNames();
            if (streetAddressMunicipalityNames != null) {
                co.getStreetAddressMunicipalityNames().getStreetAddressMunicipalityName()
                        .addAll(convertStreetAddressMunicipalityNames(streetAddressMunicipalityNames));
            }

            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipalityName> convertStreetAddressMunicipalityNames(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipalityName> streetAddressMunicipalityNames) {
        List<fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipalityName> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.StreetAddressMunicipalityName streetAddressMunicipalityName: streetAddressMunicipalityNames) {
            fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipalityName co = new fi.vrk.xroad.xroad_catalog_lister.StreetAddressMunicipalityName();
            co.setChanged(toXmlGregorianCalendar(streetAddressMunicipalityName.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(streetAddressMunicipalityName.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(streetAddressMunicipalityName.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(streetAddressMunicipalityName.getStatusInfo().getRemoved()));
            co.setLanguage(streetAddressMunicipalityName.getLanguage());
            co.setValue(streetAddressMunicipalityName.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.StreetAddressAdditionalInformation> convertStreetAddressAdditionalInformation(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.StreetAddressAdditionalInformation> streetAddressAdditionalInformationList) {
        List<fi.vrk.xroad.xroad_catalog_lister.StreetAddressAdditionalInformation> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.StreetAddressAdditionalInformation streetAddressAdditionalInformation: streetAddressAdditionalInformationList) {
            fi.vrk.xroad.xroad_catalog_lister.StreetAddressAdditionalInformation co = new fi.vrk.xroad.xroad_catalog_lister.StreetAddressAdditionalInformation();
            co.setChanged(toXmlGregorianCalendar(streetAddressAdditionalInformation.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(streetAddressAdditionalInformation.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(streetAddressAdditionalInformation.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(streetAddressAdditionalInformation.getStatusInfo().getRemoved()));
            co.setLanguage(streetAddressAdditionalInformation.getLanguage());
            co.setValue(streetAddressAdditionalInformation.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddress> convertPostOfficeBoxAddresses(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddress> postOfficeBoxAddresses) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddress> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddress postOfficeBoxAddress: postOfficeBoxAddresses) {
            fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddress co = new fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddress();
            co.setChanged(toXmlGregorianCalendar(postOfficeBoxAddress.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOfficeBoxAddress.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOfficeBoxAddress.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOfficeBoxAddress.getStatusInfo().getRemoved()));
            co.setPostalCode(postOfficeBoxAddress.getPostalCode());
            co.setPostalCode(postOfficeBoxAddress.getPostalCode());

            co.setAdditionalInformation(new PostOfficeBoxAddressAdditionalInformationList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressAdditionalInformation> postOfficeBoxAddressAdditionalInformation
                    = postOfficeBoxAddress.getAllAdditionalInformation();
            if (postOfficeBoxAddressAdditionalInformation != null) {
                co.getAdditionalInformation().getPostOfficeBoxAddressAdditionalInformation()
                        .addAll(convertPostOfficeBoxAddressAdditionalInformation(postOfficeBoxAddressAdditionalInformation));
            }

            co.setPostOfficeBoxAddressMunicipalities(new PostOfficeBoxAddressMunicipalityList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipality> postOfficeBoxAddressMunicipalities
                    = postOfficeBoxAddress.getAllMunicipalities();
            if (postOfficeBoxAddressMunicipalities != null) {
                co.getPostOfficeBoxAddressMunicipalities().getPostOfficeBoxAddressMunicipality()
                        .addAll(convertPostOfficeBoxAddressMunicipalities(postOfficeBoxAddressMunicipalities));
            }

            co.setPostOffices(new PostOfficeList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOffice> postOffices = postOfficeBoxAddress.getAllPostOffices();
            if (postOffices != null) {
                co.getPostOffices().getPostOffice().addAll(convertPostOffices(postOffices));
            }

            co.setPostOfficeBoxes(new PostOfficeBoxList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBox> postOfficeBoxes = postOfficeBoxAddress.getAllPostOfficeBoxes();
            if (postOfficeBoxes != null) {
                co.getPostOfficeBoxes().getPostOfficeBox().addAll(convertPostOfficeBoxes(postOfficeBoxes));
            }

            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressAdditionalInformation> convertPostOfficeBoxAddressAdditionalInformation(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressAdditionalInformation> postOfficeBoxAddressAdditionalInformationList) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressAdditionalInformation> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressAdditionalInformation postOfficeBoxAddressAdditionalInformation:
                postOfficeBoxAddressAdditionalInformationList) {
            fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressAdditionalInformation co
                    = new fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressAdditionalInformation();
            co.setChanged(toXmlGregorianCalendar(postOfficeBoxAddressAdditionalInformation.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOfficeBoxAddressAdditionalInformation.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOfficeBoxAddressAdditionalInformation.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOfficeBoxAddressAdditionalInformation.getStatusInfo().getRemoved()));
            co.setLanguage(postOfficeBoxAddressAdditionalInformation.getLanguage());
            co.setValue(postOfficeBoxAddressAdditionalInformation.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipality> convertPostOfficeBoxAddressMunicipalities(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipality> postOfficeBoxAddressMunicipalities) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipality> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipality postOfficeBoxAddressMunicipality: postOfficeBoxAddressMunicipalities) {
            fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipality co = new fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipality();
            co.setChanged(toXmlGregorianCalendar(postOfficeBoxAddressMunicipality.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOfficeBoxAddressMunicipality.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOfficeBoxAddressMunicipality.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOfficeBoxAddressMunicipality.getStatusInfo().getRemoved()));
            co.setCode(postOfficeBoxAddressMunicipality.getCode());

            co.setPostOfficeBoxAddressMunicipalityNames(new PostOfficeBoxAddressMunicipalityNameList());
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipalityName> postOfficeBoxAddressMunicipalityNames
                    = postOfficeBoxAddressMunicipality.getAllMunicipalityNames();
            if (postOfficeBoxAddressMunicipalityNames != null) {
                co.getPostOfficeBoxAddressMunicipalityNames().getPostOfficeBoxAddressMunicipalityName()
                        .addAll(convertPostOfficeBoxAddressMunicipalityNames(postOfficeBoxAddressMunicipalityNames));
            }

            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipalityName> convertPostOfficeBoxAddressMunicipalityNames(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipalityName> postOfficeBoxAddressMunicipalityNames) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipalityName> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOfficeBoxAddressMunicipalityName postOfficeBoxAddressMunicipalityName: postOfficeBoxAddressMunicipalityNames) {
            fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipalityName co = new fi.vrk.xroad.xroad_catalog_lister.PostOfficeBoxAddressMunicipalityName();
            co.setChanged(toXmlGregorianCalendar(postOfficeBoxAddressMunicipalityName.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOfficeBoxAddressMunicipalityName.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOfficeBoxAddressMunicipalityName.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOfficeBoxAddressMunicipalityName.getStatusInfo().getRemoved()));
            co.setLanguage(postOfficeBoxAddressMunicipalityName.getLanguage());
            co.setValue(postOfficeBoxAddressMunicipalityName.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOffice> convertPostOffices(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOffice> postOffices) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOffice> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOffice postOffice: postOffices) {
            fi.vrk.xroad.xroad_catalog_lister.PostOffice co = new fi.vrk.xroad.xroad_catalog_lister.PostOffice();
            co.setChanged(toXmlGregorianCalendar(postOffice.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOffice.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOffice.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOffice.getStatusInfo().getRemoved()));
            co.setLanguage(postOffice.getLanguage());
            co.setValue(postOffice.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBox> convertPostOfficeBoxes(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.PostOfficeBox> postOfficeBoxes) {
        List<fi.vrk.xroad.xroad_catalog_lister.PostOfficeBox> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.PostOfficeBox postOfficeBox: postOfficeBoxes) {
            fi.vrk.xroad.xroad_catalog_lister.PostOfficeBox co = new fi.vrk.xroad.xroad_catalog_lister.PostOfficeBox();
            co.setChanged(toXmlGregorianCalendar(postOfficeBox.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(postOfficeBox.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(postOfficeBox.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(postOfficeBox.getStatusInfo().getRemoved()));
            co.setLanguage(postOfficeBox.getLanguage());
            co.setValue(postOfficeBox.getValue());
            converted.add(co);
        }
        return converted;
    }

    public static Company convertCompany(fi.vrk.xroad.catalog.persistence.entity.Company company) {
        Company co = new Company();
        co.setChanged(toXmlGregorianCalendar(company.getStatusInfo().getChanged()));
        co.setCreated(toXmlGregorianCalendar(company.getStatusInfo().getCreated()));
        co.setFetched(toXmlGregorianCalendar(company.getStatusInfo().getFetched()));
        co.setRemoved(toXmlGregorianCalendar(company.getStatusInfo().getRemoved()));
        co.setBusinessId(company.getBusinessId());
        co.setCompanyForm(company.getCompanyForm());
        co.setDetailsUri(company.getDetailsUri());
        co.setName(company.getName());
        co.setRegistrationDate(toXmlGregorianCalendar(company.getRegistrationDate()));

        co.setBusinessAddresses(new BusinessAddressList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessAddress> businessAddresses = company.getAllBusinessAddresses();
        if (businessAddresses != null) {
            co.getBusinessAddresses().getBusinessAddress().addAll(convertBusinessAddresses(businessAddresses));
        }

        co.setBusinessAuxiliaryNames(new BusinessAuxiliaryNameList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessAuxiliaryName> businessAuxiliaryNames = company.getAllBusinessAuxiliaryNames();
        if (businessAuxiliaryNames != null) {
            co.getBusinessAuxiliaryNames().getBusinessAuxiliaryName().addAll(convertBusinessAuxiliaryNames(businessAuxiliaryNames));
        }

        co.setBusinessIdChanges(new BusinessIdChangeList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessIdChange> businessIdChanges = company.getAllBusinessIdChanges();
        if (businessIdChanges != null) {
            co.getBusinessIdChanges().getBusinessIdChange().addAll(convertBusinessIdChanges(businessIdChanges));
        }

        co.setBusinessLines(new BusinessLineList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessLine> businessLines = company.getAllBusinessLines();
        if (businessLines != null) {
            co.getBusinessLines().getBusinessLine().addAll(convertBusinessLines(businessLines));
        }

        co.setBusinessNames(new BusinessNameList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessName> businessNames = company.getAllBusinessNames();
        if (businessNames != null) {
            co.getBusinessNames().getBusinessName().addAll(convertBusinessNames(businessNames));
        }

        co.setCompanyForms(new CompanyFormList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.CompanyForm> companyForms = company.getAllCompanyForms();
        if (companyForms != null) {
            co.getCompanyForms().getCompanyForm().addAll(convertCompanyForms(companyForms));
        }

        co.setContactDetails(new ContactDetailList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.ContactDetail> contactDetails = company.getAllContactDetails();
        if (contactDetails != null) {
            co.getContactDetails().getContactDetail().addAll(convertContactDetails(contactDetails));
        }

        co.setLanguages(new LanguageList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.Language> languages = company.getAllLanguages();
        if (languages != null) {
            co.getLanguages().getLanguage().addAll(convertLanguages(languages));
        }

        co.setLiquidations(new LiquidationList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.Liquidation> liquidations = company.getAllLiquidations();
        if (liquidations != null) {
            co.getLiquidations().getLiquidation().addAll(convertLiquidations(liquidations));
        }

        co.setRegisteredEntries(new RegisteredEntryList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.RegisteredEntry> registeredEntries = company.getAllRegisteredEntries();
        if (registeredEntries != null) {
            co.getRegisteredEntries().getRegisteredEntry().addAll(convertRegisteredEntries(registeredEntries));
        }

        co.setRegisteredOffices(new RegisteredOfficeList());
        Iterable<fi.vrk.xroad.catalog.persistence.entity.RegisteredOffice> registeredOffices = company.getAllRegisteredOffices();
        if (registeredOffices != null) {
            co.getRegisteredOffices().getRegisteredOffice().addAll(convertRegisteredOffices(registeredOffices));
        }
        return co;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.BusinessAddress> convertBusinessAddresses(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessAddress> businessAddresses) {
        List<fi.vrk.xroad.xroad_catalog_lister.BusinessAddress> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.BusinessAddress businessAddress: businessAddresses) {
            fi.vrk.xroad.xroad_catalog_lister.BusinessAddress co = new fi.vrk.xroad.xroad_catalog_lister.BusinessAddress();
            co.setChanged(toXmlGregorianCalendar(businessAddress.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(businessAddress.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(businessAddress.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(businessAddress.getStatusInfo().getRemoved()));
            co.setCareOf(businessAddress.getCareOf());
            co.setCity(businessAddress.getCity());
            co.setCountry(businessAddress.getCountry());
            co.setLanguage(businessAddress.getLanguage());
            co.setPostCode(businessAddress.getPostCode());
            co.setSource(businessAddress.getSource());
            co.setStreet(businessAddress.getStreet());
            co.setType(businessAddress.getType());
            co.setVersion(businessAddress.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(businessAddress.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(businessAddress.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.BusinessAuxiliaryName> convertBusinessAuxiliaryNames(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessAuxiliaryName> businessAuxiliaryNames) {
        List<fi.vrk.xroad.xroad_catalog_lister.BusinessAuxiliaryName> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.BusinessAuxiliaryName businessAuxiliaryName: businessAuxiliaryNames) {
            fi.vrk.xroad.xroad_catalog_lister.BusinessAuxiliaryName co = new fi.vrk.xroad.xroad_catalog_lister.BusinessAuxiliaryName();
            co.setChanged(toXmlGregorianCalendar(businessAuxiliaryName.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(businessAuxiliaryName.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(businessAuxiliaryName.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(businessAuxiliaryName.getStatusInfo().getRemoved()));
            co.setLanguage(businessAuxiliaryName.getLanguage());
            co.setName(businessAuxiliaryName.getName());
            co.setOrdering(businessAuxiliaryName.getOrdering());
            co.setSource(businessAuxiliaryName.getSource());
            co.setVersion(businessAuxiliaryName.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(businessAuxiliaryName.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(businessAuxiliaryName.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.BusinessIdChange> convertBusinessIdChanges(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessIdChange> businessIdChanges) {
        List<fi.vrk.xroad.xroad_catalog_lister.BusinessIdChange> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.BusinessIdChange businessIdChange: businessIdChanges) {
            fi.vrk.xroad.xroad_catalog_lister.BusinessIdChange co = new fi.vrk.xroad.xroad_catalog_lister.BusinessIdChange();
            co.setChanged(toXmlGregorianCalendar(businessIdChange.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(businessIdChange.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(businessIdChange.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(businessIdChange.getStatusInfo().getRemoved()));
            co.setLanguage(businessIdChange.getLanguage());
            co.setSource(businessIdChange.getSource());
            co.setChange(businessIdChange.getChange());
            co.setChangeDate(businessIdChange.getChangeDate());
            co.setDescription(businessIdChange.getDescription());
            co.setReason(businessIdChange.getReason());
            co.setOldBusinessId(businessIdChange.getOldBusinessId());
            co.setNewBusinessId(businessIdChange.getNewBusinessId());
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.BusinessLine> convertBusinessLines(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessLine> businessLines) {
        List<fi.vrk.xroad.xroad_catalog_lister.BusinessLine> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.BusinessLine businessLine: businessLines) {
            fi.vrk.xroad.xroad_catalog_lister.BusinessLine co = new fi.vrk.xroad.xroad_catalog_lister.BusinessLine();
            co.setChanged(toXmlGregorianCalendar(businessLine.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(businessLine.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(businessLine.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(businessLine.getStatusInfo().getRemoved()));
            co.setLanguage(businessLine.getLanguage());
            co.setSource(businessLine.getSource());
            co.setName(businessLine.getName());
            co.setOrdering(businessLine.getOrdering());
            co.setVersion(businessLine.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(businessLine.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(businessLine.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.BusinessName> convertBusinessNames(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.BusinessName> businessNames) {
        List<fi.vrk.xroad.xroad_catalog_lister.BusinessName> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.BusinessName businessName: businessNames) {
            fi.vrk.xroad.xroad_catalog_lister.BusinessName co = new fi.vrk.xroad.xroad_catalog_lister.BusinessName();
            co.setChanged(toXmlGregorianCalendar(businessName.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(businessName.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(businessName.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(businessName.getStatusInfo().getRemoved()));
            co.setLanguage(businessName.getLanguage());
            co.setSource(businessName.getSource());
            co.setName(businessName.getName());
            co.setOrdering(businessName.getOrdering());
            co.setVersion(businessName.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(businessName.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(businessName.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.CompanyForm> convertCompanyForms(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.CompanyForm> companyForms) {
        List<fi.vrk.xroad.xroad_catalog_lister.CompanyForm> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.CompanyForm companyForm: companyForms) {
            fi.vrk.xroad.xroad_catalog_lister.CompanyForm co = new fi.vrk.xroad.xroad_catalog_lister.CompanyForm();
            co.setChanged(toXmlGregorianCalendar(companyForm.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(companyForm.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(companyForm.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(companyForm.getStatusInfo().getRemoved()));
            co.setLanguage(companyForm.getLanguage());
            co.setSource(companyForm.getSource());
            co.setName(companyForm.getName());
            co.setType(companyForm.getType());
            co.setVersion(companyForm.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(companyForm.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(companyForm.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.ContactDetail> convertContactDetails(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.ContactDetail> contactDetails) {
        List<fi.vrk.xroad.xroad_catalog_lister.ContactDetail> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.ContactDetail contactDetail: contactDetails) {
            fi.vrk.xroad.xroad_catalog_lister.ContactDetail co = new fi.vrk.xroad.xroad_catalog_lister.ContactDetail();
            co.setChanged(toXmlGregorianCalendar(contactDetail.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(contactDetail.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(contactDetail.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(contactDetail.getStatusInfo().getRemoved()));
            co.setLanguage(contactDetail.getLanguage());
            co.setSource(contactDetail.getSource());
            co.setValue(contactDetail.getValue());
            co.setType(contactDetail.getType());
            co.setVersion(contactDetail.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(contactDetail.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(contactDetail.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.Language> convertLanguages(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Language> languages) {
        List<fi.vrk.xroad.xroad_catalog_lister.Language> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.Language language: languages) {
            fi.vrk.xroad.xroad_catalog_lister.Language co = new fi.vrk.xroad.xroad_catalog_lister.Language();
            co.setChanged(toXmlGregorianCalendar(language.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(language.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(language.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(language.getStatusInfo().getRemoved()));
            co.setLanguage(language.getLanguage());
            co.setSource(language.getSource());
            co.setName(language.getName());
            co.setVersion(language.getVersion());
            co.setRegistrationDate(toXmlGregorianCalendar(language.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(language.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.Liquidation> convertLiquidations(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.Liquidation> liquidations) {
        List<fi.vrk.xroad.xroad_catalog_lister.Liquidation> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.Liquidation liquidation: liquidations) {
            fi.vrk.xroad.xroad_catalog_lister.Liquidation co = new fi.vrk.xroad.xroad_catalog_lister.Liquidation();
            co.setChanged(toXmlGregorianCalendar(liquidation.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(liquidation.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(liquidation.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(liquidation.getStatusInfo().getRemoved()));
            co.setLanguage(liquidation.getLanguage());
            co.setSource(liquidation.getSource());
            co.setName(liquidation.getName());
            co.setVersion(liquidation.getVersion());
            co.setType(liquidation.getType());
            co.setRegistrationDate(toXmlGregorianCalendar(liquidation.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(liquidation.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.RegisteredEntry> convertRegisteredEntries(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.RegisteredEntry> registeredEntries) {
        List<fi.vrk.xroad.xroad_catalog_lister.RegisteredEntry> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.RegisteredEntry registeredEntry: registeredEntries) {
            fi.vrk.xroad.xroad_catalog_lister.RegisteredEntry co = new fi.vrk.xroad.xroad_catalog_lister.RegisteredEntry();
            co.setChanged(toXmlGregorianCalendar(registeredEntry.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(registeredEntry.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(registeredEntry.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(registeredEntry.getStatusInfo().getRemoved()));
            co.setLanguage(registeredEntry.getLanguage());
            co.setAuthority(registeredEntry.getAuthority());
            co.setDescription(registeredEntry.getDescription());
            co.setRegister(registeredEntry.getRegister());
            co.setStatus(registeredEntry.getStatus());
            co.setRegistrationDate(toXmlGregorianCalendar(registeredEntry.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(registeredEntry.getEndDate()));
            converted.add(co);
        }
        return converted;
    }

    public static Collection<fi.vrk.xroad.xroad_catalog_lister.RegisteredOffice> convertRegisteredOffices(
            Iterable<fi.vrk.xroad.catalog.persistence.entity.RegisteredOffice> registeredOffices) {
        List<fi.vrk.xroad.xroad_catalog_lister.RegisteredOffice> converted = new ArrayList<>();
        for (fi.vrk.xroad.catalog.persistence.entity.RegisteredOffice registeredOffice: registeredOffices) {
            fi.vrk.xroad.xroad_catalog_lister.RegisteredOffice co = new fi.vrk.xroad.xroad_catalog_lister.RegisteredOffice();
            co.setChanged(toXmlGregorianCalendar(registeredOffice.getStatusInfo().getChanged()));
            co.setCreated(toXmlGregorianCalendar(registeredOffice.getStatusInfo().getCreated()));
            co.setFetched(toXmlGregorianCalendar(registeredOffice.getStatusInfo().getFetched()));
            co.setRemoved(toXmlGregorianCalendar(registeredOffice.getStatusInfo().getRemoved()));
            co.setLanguage(registeredOffice.getLanguage());
            co.setSource(registeredOffice.getSource());
            co.setOrdering(registeredOffice.getOrdering());
            co.setVersion(registeredOffice.getVersion());
            co.setName(registeredOffice.getName());
            co.setRegistrationDate(toXmlGregorianCalendar(registeredOffice.getRegistrationDate()));
            co.setEndDate(toXmlGregorianCalendar(registeredOffice.getEndDate()));
            converted.add(co);
        }
        return converted;
    }
}
