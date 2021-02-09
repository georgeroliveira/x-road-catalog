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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"postOfficeBoxAddressMunicipality"})
@EqualsAndHashCode(exclude = {"id","postOfficeBoxAddressMunicipality","statusInfo"})
@Builder
public class PostOfficeBoxAddressMunicipalityName {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_OFFICE_BOX_ADDRESS_MUNICIPALITY_NAME_GEN")
    @SequenceGenerator(name = "POST_OFFICE_BOX_ADDRESS_MUNICIPALITY_NAME_GEN", sequenceName = "POST_OFFICE_BOX_ADDRESS_MUNICIPALITY_NAME_ID_SEQ", allocationSize = 1)
    private long id;
    @Column(nullable = false)
    private String language;
    @Column(nullable = false)
    private String value;
    @Builder.Default
    @Embedded
    private StatusInfo statusInfo = new StatusInfo();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_OFFICE_BOX_ADDRESS_MUNICIPALITY_ID")
    private PostOfficeBoxAddressMunicipality postOfficeBoxAddressMunicipality;
}
