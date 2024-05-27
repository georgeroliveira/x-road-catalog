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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "address", "postOfficeBoxAddressMunicipalities", "additionalInformation", "postOffices",
        "postOfficesBoxes" })
@EqualsAndHashCode(exclude = { "id", "address", "postOfficeBoxAddressMunicipalities", "additionalInformation",
        "postOffices", "postOfficesBoxes", "statusInfo" })
@Builder
public class PostOfficeBoxAddress {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_OFFICE_BOX_ADDRESS_GEN")
    @SequenceGenerator(name = "POST_OFFICE_BOX_ADDRESS_GEN", sequenceName = "POST_OFFICE_BOX_ADDRESS_ID_SEQ", allocationSize = 1)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;
    @Column(nullable = false)
    private String postalCode;
    @Builder.Default
    @Embedded
    private StatusInfo statusInfo = new StatusInfo();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "postOfficeBoxAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PostOfficeBoxAddressMunicipality> postOfficeBoxAddressMunicipalities = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "postOfficeBoxAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PostOfficeBoxAddressAdditionalInformation> additionalInformation = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "postOfficeBoxAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PostOffice> postOffices = new HashSet<>();
    @Builder.Default
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "postOfficeBoxAddress", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PostOfficeBox> postOfficesBoxes = new HashSet<>();

    public Set<PostOfficeBoxAddressAdditionalInformation> getAllAdditionalInformation() {
        return additionalInformation;
    }

    public Set<PostOfficeBoxAddressMunicipality> getAllMunicipalities() {
        return postOfficeBoxAddressMunicipalities;
    }

    public Set<PostOffice> getAllPostOffices() {
        return postOffices;
    }

    public Set<PostOfficeBox> getAllPostOfficeBoxes() {
        return postOfficesBoxes;
    }
}
