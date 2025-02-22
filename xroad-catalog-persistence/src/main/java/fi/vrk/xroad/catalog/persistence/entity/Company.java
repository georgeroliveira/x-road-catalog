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
package fi.vrk.xroad.catalog.persistence.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "businessNames",
        "businessAuxiliaryNames",
        "businessAddresses",
        "companyForms",
        "liquidations",
        "businessLines",
        "languages",
        "registeredOffices",
        "contactDetails",
        "registeredEntries",
        "businessIdChanges" })
@EqualsAndHashCode(exclude = {
        "id",
        "statusInfo",
        "businessNames",
        "businessAuxiliaryNames",
        "businessAddresses",
        "companyForms",
        "liquidations",
        "businessLines",
        "languages",
        "registeredOffices",
        "contactDetails",
        "registeredEntries",
        "businessIdChanges" })
@NamedQueries({ @NamedQuery(name = "Company.findAllByBusinessId", query = Company.FIND_ALL_BY_BUSINESS_ID) })
public class Company {

    static final String FIND_ALL_BY_BUSINESS_ID = "SELECT DISTINCT com FROM Company com WHERE com.businessId = :businessId";

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_GEN")
    @SequenceGenerator(name = "COMPANY_GEN", sequenceName = "COMPANY_ID_SEQ", allocationSize = 1)
    private long id;
    @Column(nullable = false)
    private String businessId;
    @Column
    private String companyForm;
    @Column
    private String detailsUri;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDateTime registrationDate;
    @Builder.Default
    @Embedded
    private StatusInfo statusInfo = new StatusInfo();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessName> businessNames = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessAuxiliaryName> businessAuxiliaryNames = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessAddress> businessAddresses = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CompanyForm> companyForms = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Liquidation> liquidations = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessLine> businessLines = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Language> languages = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RegisteredOffice> registeredOffices = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ContactDetail> contactDetails = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RegisteredEntry> registeredEntries = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<BusinessIdChange> businessIdChanges = new HashSet<>();

    public Set<BusinessName> getAllBusinessNames() {
        return businessNames;
    }

    public Set<BusinessAuxiliaryName> getAllBusinessAuxiliaryNames() {
        return businessAuxiliaryNames;
    }

    public Set<BusinessAddress> getAllBusinessAddresses() {
        return businessAddresses;
    }

    public Set<CompanyForm> getAllCompanyForms() {
        return companyForms;
    }

    public Set<Liquidation> getAllLiquidations() {
        return liquidations;
    }

    public Set<BusinessLine> getAllBusinessLines() {
        return businessLines;
    }

    public Set<Language> getAllLanguages() {
        return languages;
    }

    public Set<RegisteredOffice> getAllRegisteredOffices() {
        return registeredOffices;
    }

    public Set<ContactDetail> getAllContactDetails() {
        return contactDetails;
    }

    public Set<RegisteredEntry> getAllRegisteredEntries() {
        return registeredEntries;
    }

    public Set<BusinessIdChange> getAllBusinessIdChanges() {
        return businessIdChanges;
    }

}
