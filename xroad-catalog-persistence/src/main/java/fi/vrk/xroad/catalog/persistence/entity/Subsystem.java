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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
@ToString(exclude = { "services" })
@EqualsAndHashCode(exclude = { "id", "services", "statusInfo" })
public class Subsystem {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUBSYSTEM_GEN")
    @SequenceGenerator(name = "SUBSYSTEM_GEN", sequenceName = "SUBSYSTEM_ID_SEQ", allocationSize = 1)
    private long id;
    @Column(nullable = false)
    private String subsystemCode;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Getter(AccessLevel.NONE) // do not create default getter, we provide the substitute
    @OneToMany(mappedBy = "subsystem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Service> services = new HashSet<>();
    @Embedded
    private StatusInfo statusInfo = new StatusInfo();

    public Subsystem() {
        // Empty constructor
    }

    public SubsystemId createKey() {
        return new SubsystemId(
                getMember().getXRoadInstance(),
                getMember().getMemberClass(),
                getMember().getMemberCode(),
                subsystemCode);
    }

    public Subsystem(Member member, String subsystemCode) {
        this.member = member;
        this.subsystemCode = subsystemCode;
        statusInfo.setTimestampsForNew(LocalDateTime.now());
    }

    public Set<Service> getActiveServices() {
        return Collections.unmodifiableSet(services.stream()
                .filter(service -> !service.getStatusInfo().isRemoved())
                .collect(Collectors.toSet()));
    }

    public Set<Service> getAllServices() {
        return services;
    }

}
