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
package fi.vrk.xroad.catalog.persistence.repository;

import fi.vrk.xroad.catalog.persistence.entity.Service;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ServiceRepository extends CrudRepository<Service, Long> {
    /**
     * Only returns non-removed services
     */
    @Query("SELECT s FROM Service s WHERE "
            + "s.serviceVersion = :serviceVersion "
            + "AND s.serviceCode = :serviceCode "
            + "AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            + "AND s.statusInfo.removed IS NULL")
    Service findActiveByNaturalKey(@Param("xRoadInstance") String xRoadInstance,
                                   @Param("memberClass") String memberClass,
                                   @Param("memberCode") String memberCode,
                                   @Param("subsystemCode") String subsystemCode,
                                   @Param("serviceCode") String serviceCode,
                                   @Param("serviceVersion") String serviceVersion);

    /**
     * Only returns non-removed services
     */
    @Query("SELECT s FROM Service s WHERE s.statusInfo.removed IS NULL")
    List<Service> findAllActive();

    /**
     * Only returns non-removed services
     */
    @Query("SELECT s FROM Service s WHERE "
            + "s.serviceVersion IS NULL "
            + "AND s.serviceCode = :serviceCode "
            + "AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            + "AND s.statusInfo.removed IS NULL")
    Service findActiveNullVersionByNaturalKey(@Param("xRoadInstance") String xRoadInstance,
                                   @Param("memberClass") String memberClass,
                                   @Param("memberCode") String memberCode,
                                   @Param("subsystemCode") String subsystemCode,
                                   @Param("serviceCode") String serviceCode);

    @Query("SELECT s FROM Service s WHERE s.serviceCode = :serviceCode "
            +"AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            +"AND s.serviceVersion = :serviceVersion "
            + "AND s.statusInfo.removed IS NULL")
    Service findActiveByMemberServiceAndSubsystemAndVersion(@Param("xRoadInstance") String xRoadInstance,
                                                            @Param("memberClass") String memberClass,
                                                            @Param("memberCode") String memberCode,
                                                            @Param("serviceCode") String serviceCode,
                                                            @Param("subsystemCode") String subsystemCode,
                                                            @Param("serviceVersion") String serviceVersion);

    @Query("SELECT s FROM Service s WHERE s.serviceCode = :serviceCode "
            +"AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            + "AND s.statusInfo.removed IS NULL")
    Service findActiveByMemberServiceAndSubsystem(@Param("xRoadInstance") String xRoadInstance,
                                                  @Param("memberClass") String memberClass,
                                                  @Param("memberCode") String memberCode,
                                                  @Param("serviceCode") String serviceCode,
                                                  @Param("subsystemCode") String subsystemCode);

    @Query("SELECT s FROM Service s WHERE s.serviceCode = :serviceCode "
            +"AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            + "AND s.serviceVersion IS NULL")
    Service findAllByMemberServiceAndSubsystemVersionNull(@Param("xRoadInstance") String xRoadInstance,
                                                          @Param("memberClass") String memberClass,
                                                          @Param("memberCode") String memberCode,
                                                          @Param("serviceCode") String serviceCode,
                                                          @Param("subsystemCode") String subsystemCode);

    @Query("SELECT s FROM Service s WHERE s.serviceCode = :serviceCode "
            +"AND s.subsystem.subsystemCode = :subsystemCode "
            + "AND s.subsystem.member.memberCode = :memberCode "
            + "AND s.subsystem.member.memberClass = :memberClass "
            + "AND s.subsystem.member.xRoadInstance = :xRoadInstance "
            +"AND s.serviceVersion = :serviceVersion")
    Service findAllByMemberServiceAndSubsystemAndVersion(@Param("xRoadInstance") String xRoadInstance,
                                                         @Param("memberClass") String memberClass,
                                                         @Param("memberCode") String memberCode,
                                                         @Param("serviceCode") String serviceCode,
                                                         @Param("subsystemCode") String subsystemCode,
                                                         @Param("serviceVersion") String serviceVersion);
}
