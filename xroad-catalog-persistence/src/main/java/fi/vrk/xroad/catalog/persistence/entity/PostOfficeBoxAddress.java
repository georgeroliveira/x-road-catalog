/**
 * The MIT License
 * Copyright (c) 2021, Population Register Centre (VRK)
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
package fi.vrk.xroad.catalog.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"address","postOfficeBoxAddressMunicipalities","additionalInformation","postOffices","postOfficesBoxes"})
@EqualsAndHashCode(exclude = {"id","address","postOfficeBoxAddressMunicipalities","additionalInformation","postOffices","postOfficesBoxes","statusInfo"})
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

    /**
     * This collection can be used to add new items
     *
     * @return Set of all PostOfficeBoxAddressAdditionalInformation
     */
    public Set<PostOfficeBoxAddressAdditionalInformation> getAllAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * This collection can be used to add new items
     *
     * @return Set of all PostOfficeBoxAddressMunicipality
     */
    public Set<PostOfficeBoxAddressMunicipality> getAllMunicipalities() {
        return postOfficeBoxAddressMunicipalities;
    }

    /**
     * This collection can be used to add new items
     *
     * @return Set of all PostOfficeBoxAddressMunicipality
     */
    public Set<PostOffice> getAllPostOffices() {
        return postOffices;
    }

    /**
     * This collection can be used to add new items
     *
     * @return Set of all PostOfficeBoxAddressMunicipality
     */
    public Set<PostOfficeBox> getAllPostOfficeBoxes() {
        return postOfficesBoxes;
    }
}
