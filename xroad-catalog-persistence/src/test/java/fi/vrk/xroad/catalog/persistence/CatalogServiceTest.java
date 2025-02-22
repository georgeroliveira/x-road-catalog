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
package fi.vrk.xroad.catalog.persistence;

import fi.vrk.xroad.catalog.persistence.dto.DistinctServiceStatistics;
import fi.vrk.xroad.catalog.persistence.dto.LastCollectionData;
import fi.vrk.xroad.catalog.persistence.dto.MemberDataList;
import fi.vrk.xroad.catalog.persistence.dto.ServiceStatistics;
import fi.vrk.xroad.catalog.persistence.dto.XRoadData;
import fi.vrk.xroad.catalog.persistence.entity.Endpoint;
import fi.vrk.xroad.catalog.persistence.entity.ErrorLog;
import fi.vrk.xroad.catalog.persistence.entity.Member;
import fi.vrk.xroad.catalog.persistence.entity.OpenApi;
import fi.vrk.xroad.catalog.persistence.entity.Rest;
import fi.vrk.xroad.catalog.persistence.entity.Service;
import fi.vrk.xroad.catalog.persistence.entity.ServiceId;
import fi.vrk.xroad.catalog.persistence.entity.Subsystem;
import fi.vrk.xroad.catalog.persistence.entity.SubsystemId;
import fi.vrk.xroad.catalog.persistence.entity.Wsdl;
import fi.vrk.xroad.catalog.persistence.repository.EndpointRepository;
import fi.vrk.xroad.catalog.persistence.repository.ErrorLogRepository;
import fi.vrk.xroad.catalog.persistence.repository.MemberRepository;
import fi.vrk.xroad.catalog.persistence.repository.OpenApiRepository;
import fi.vrk.xroad.catalog.persistence.repository.RestRepository;
import fi.vrk.xroad.catalog.persistence.repository.ServiceRepository;
import fi.vrk.xroad.catalog.persistence.repository.SubsystemRepository;
import fi.vrk.xroad.catalog.persistence.repository.WsdlRepository;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
@Transactional
public class CatalogServiceTest {

    public static final int TEST_DATA_MEMBERS = 8;
    public static final int TEST_DATA_ACTIVE_MEMBERS = 7;
    public static final int TEST_DATA_SUBSYSTEMS = 12;
    public static final int TEST_DATA_ACTIVE_SUBSYSTEMS = 10;
    @Autowired
    CatalogService catalogService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SubsystemRepository subsystemRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    WsdlRepository wsdlRepository;

    @Autowired
    OpenApiRepository openApiRepository;

    @Autowired
    RestRepository restRepository;

    @Autowired
    EndpointRepository endpointRepository;

    @Autowired
    ErrorLogRepository errorLogRepository;

    @Autowired
    TestUtil testUtil;

    @Test
    public void testGetWsdl() {
        Wsdl wsdl = catalogService.getWsdl("1000");
        assertNotNull(wsdl);
        assertEquals("<?xml version=\"1.0\" standalone=\"no\"?><wsdl-6-1-1-1-changed/>", wsdl.getData());
        assertEquals(7, wsdl.getService().getSubsystem().getId());
    }

    @Test
    public void testGetWsdlNotFound() {
        Wsdl wsdl = catalogService.getWsdl("9899");
        assertNull(wsdl);
    }

    @Test
    public void testGetWsdlMultipleException() {
        try {
            catalogService.getWsdl("9999");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("multiple matches found to 9999"));
        }
    }

    @Test
    public void testGetOpenApi() {
        OpenApi openApi = catalogService.getOpenApi("3003");
        assertNotNull(openApi);
        assertEquals("<openapi>", openApi.getData());
        assertEquals(8, openApi.getService().getSubsystem().getId());
    }

    @Test
    public void testGetOpenApiNotFound() {
        OpenApi openApi = catalogService.getOpenApi("9899");
        assertNull(openApi);
    }

    @Test
    public void testGetOpenApiMultipleException() {
        try {
            catalogService.getOpenApi("3004");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("multiple matches found to 3004"));
        }
    }

    @Test
    public void testGetRest() {
        Service service = serviceRepository.findById(13L).get();
        Rest rest = catalogService.getRest(service);
        assertNotNull(rest);
        assertEquals("{\"endpoint_list\": []}}", rest.getData());
        assertEquals(8, rest.getService().getSubsystem().getId());
    }

    @Test
    public void testGetRestNotFound() {
        Service service = serviceRepository.findById(12L).get();
        Rest rest = catalogService.getRest(service);
        assertNull(rest);
    }

    @Test
    public void testGetRestMultipleException() {
        try {
            Service service = serviceRepository.findById(1L).get();
            catalogService.getRest(service);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("multiple matches found to"));
        }
    }

    @Test
    public void testGetErrorLog() {
        LocalDateTime changedAfter = LocalDateTime.of(2020, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0, 0);
        Iterable<ErrorLog> errorLogEntries = catalogService.getErrorLog(changedAfter, endDate);
        assertNotNull(errorLogEntries);
        assertEquals(true, errorLogEntries.iterator().hasNext());
    }

    @Test
    public void testGetErrorsForSubsystem() {
        XRoadData xRoadData = XRoadData.builder().xRoadInstance("DEV").memberClass("GOV").memberCode("1234")
                .subsystemCode("TestSubsystem").build();
        Page<ErrorLog> errorLogEntries = catalogService.getErrors(xRoadData, 0, 100,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.now());
        assertNotNull(errorLogEntries);
        assertEquals(1, errorLogEntries.getNumberOfElements());
        assertEquals(1, errorLogEntries.getTotalPages());
    }

    @Test
    public void testGetErrorsForMemberCode() {
        XRoadData xRoadData = XRoadData.builder().xRoadInstance("DEV").memberClass("GOV").memberCode("1234")
                .subsystemCode(null).build();
        Page<ErrorLog> errorLogEntries = catalogService.getErrors(xRoadData, 0, 100,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.now());
        assertNotNull(errorLogEntries);
        assertEquals(2, errorLogEntries.getNumberOfElements());
        assertEquals(1, errorLogEntries.getTotalPages());
    }

    @Test
    public void testGetErrorsForMemberClass() {
        XRoadData xRoadData = XRoadData.builder().xRoadInstance("DEV").memberClass("GOV").memberCode(null)
                .subsystemCode(null).build();
        Page<ErrorLog> errorLogEntries = catalogService.getErrors(xRoadData, 0, 100,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.now());
        assertNotNull(errorLogEntries);
        assertEquals(3, errorLogEntries.getNumberOfElements());
        assertEquals(1, errorLogEntries.getTotalPages());
    }

    @Test
    public void testGetErrorsForInstance() {
        XRoadData xRoadData = XRoadData.builder().xRoadInstance("DEV").memberClass(null).memberCode(null)
                .subsystemCode(null).build();
        Page<ErrorLog> errorLogEntries = catalogService.getErrors(xRoadData, 0, 100,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.now());
        assertNotNull(errorLogEntries);
        assertEquals(4, errorLogEntries.getNumberOfElements());
        assertEquals(1, errorLogEntries.getTotalPages());
    }

    @Test
    public void testGetErrorsAll() {
        XRoadData xRoadData = XRoadData.builder().xRoadInstance(null).memberClass(null).memberCode(null)
                .subsystemCode(null).build();
        Page<ErrorLog> errorLogEntries = catalogService.getErrors(xRoadData, 0, 100,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.now());
        assertNotNull(errorLogEntries);
        assertEquals(7, errorLogEntries.getNumberOfElements());
        assertEquals(1, errorLogEntries.getTotalPages());
    }

    @Test
    public void testSaveErrorLog() {
        ErrorLog errorLog = ErrorLog.builder().message("Error").code("500")
                .created(LocalDateTime.now()).build();
        ErrorLog savedErrorLog = catalogService.saveErrorLog(errorLog);
        assertNotNull(savedErrorLog);
    }

    @Test
    public void testEntityTreesFetchedCorrectly() throws InterruptedException {
        assertEntityTreeFetchedCorrectly(catalogService.getAllMembers());
        assertEntityTreeFetchedCorrectly(catalogService.getActiveMembers());
        LocalDateTime modifiedSince1800 = LocalDateTime.of(1800, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0, 0);
        assertEntityTreeFetchedCorrectly(catalogService.getAllMembers(modifiedSince1800, endDate));
        assertEntityTreeFetchedCorrectly(catalogService.getActiveMembers(modifiedSince1800, endDate));
    }

    private void assertEntityTreeFetchedCorrectly(Iterable<Member> members) {
        log.info("members loaded, detaching");
        for (Member m : members) {
            testUtil.entityManagerDetach(m);
        }
        log.info("all members detached");
        testUtil.entityManagerClear();
        // member - subsystem - service should be fetched
        // service - wdsl should also be fetched
        // wsdl.data should not be fetched but this would require hibernate
        // bytecode enhancement, and is too much of a pain
        Member m = (Member) testUtil.getEntity(members, 1L).get();
        assertNotNull(m);
        Subsystem ss = (Subsystem) testUtil.getEntity(m.getAllSubsystems(), 1L).get();
        assertNotNull(ss);
        Service s = (Service) testUtil.getEntity(ss.getAllServices(), 2L).get();
        assertNotNull(s);
        Wsdl wsdl = s.getWsdl();
        assertNotNull(wsdl);
        assertNotNull(wsdl.getData());
    }

    @Test
    public void testInsertNewMemberAndSubsystems() {

        assertMemberAndSubsystemCounts(TEST_DATA_MEMBERS,
                TEST_DATA_ACTIVE_MEMBERS,
                TEST_DATA_SUBSYSTEMS,
                TEST_DATA_ACTIVE_SUBSYSTEMS);

        Member fooMember = new Member("dev-cs", "PUB", "333111", "UnitTestMember");
        Subsystem subsystem1 = new Subsystem(null, "subsystem1");
        Subsystem subsystem2 = new Subsystem(null, "subsystem2");
        fooMember.setSubsystems(Sets.newHashSet(subsystem1, subsystem2));
        subsystem1.setMember(fooMember);
        subsystem2.setMember(fooMember);
        List<Member> members = Lists.newArrayList(fooMember);
        catalogService.saveAllMembersAndSubsystems(members);

        assertMemberAndSubsystemCounts(TEST_DATA_MEMBERS + 1,
                1, TEST_DATA_SUBSYSTEMS + 2, 2);
    }

    @Test
    public void testInsertMultipleMembersAndSubsystems() {
        assertMemberAndSubsystemCounts(TEST_DATA_MEMBERS,
                TEST_DATA_ACTIVE_MEMBERS,
                TEST_DATA_SUBSYSTEMS,
                TEST_DATA_ACTIVE_SUBSYSTEMS);

        int subsystemsPerMember = 3;
        List<Member> members = Lists.newArrayList();
        members.add(testUtil.createTestMember("200", subsystemsPerMember));
        members.add(testUtil.createTestMember("201", subsystemsPerMember));
        members.add(testUtil.createTestMember("202", subsystemsPerMember));
        catalogService.saveAllMembersAndSubsystems(members);
        int createdMembers = 3;
        int createdSubsystems = createdMembers * subsystemsPerMember;

        assertMemberAndSubsystemCounts(TEST_DATA_MEMBERS + createdMembers,
                createdMembers, TEST_DATA_SUBSYSTEMS + createdSubsystems, createdSubsystems);
    }

    private void assertMemberAndSubsystemCounts(int members, int activeMembers, int subsystems, int activeSubsystems) {
        assertEquals(members, Iterables.size(catalogService.getAllMembers()));
        assertEquals(activeMembers, Iterables.size(catalogService.getActiveMembers()));
        assertEquals(subsystems, Iterables.size(subsystemRepository.findAll()));
        assertEquals(activeSubsystems, StreamSupport.stream(subsystemRepository.findAll().spliterator(), false)
                .filter(s -> !s.getStatusInfo().isRemoved())
                .count());
    }

    @Test
    public void testMemberIsChangedOnlyWhenNameIsChanged() {
        Member member1 = memberRepository.findById(1L).get();
        LocalDateTime changed = member1.getStatusInfo().getChanged();

        String oldName = "Nahka-Albert";
        String modifiedName = "Viskoosi-Jooseppi";
        Member updateToSameName = new Member();
        updateToSameName.setXRoadInstance(member1.getXRoadInstance());
        updateToSameName.setMemberClass(member1.getMemberClass());
        updateToSameName.setMemberCode(member1.getMemberCode());
        updateToSameName.setName(oldName);
        updateToSameName.setSubsystems(new HashSet<>());
        catalogService.saveAllMembersAndSubsystems(Arrays.asList(updateToSameName));

        Member member2 = memberRepository.findById(1L).get();
        assertEquals(changed, member2.getStatusInfo().getChanged());

        Member updateToDifferentName = new Member();
        updateToDifferentName.setXRoadInstance(member1.getXRoadInstance());
        updateToDifferentName.setMemberClass(member1.getMemberClass());
        updateToDifferentName.setMemberCode(member1.getMemberCode());
        updateToDifferentName.setName(modifiedName);
        updateToDifferentName.setSubsystems(new HashSet<>());
        catalogService.saveAllMembersAndSubsystems(Arrays.asList(updateToDifferentName));

        Member member3 = memberRepository.findById(1L).get();
        assertNotEquals(changed, member3.getStatusInfo().getChanged());
    }

    @Test
    public void testGetMember() {
        Member member = memberRepository.findById(1L).get();
        Member foundMember = catalogService.getMember(member.getXRoadInstance(),
                member.getMemberClass(), member.getMemberCode());
        assertNotNull(foundMember);
    }

    @Test
    public void testGetActiveMembersSince() {
        // all non-deleted members that contain parts that were modified since 1.1.2007
        // (3-7)
        Iterable<Member> members = catalogService.getActiveMembers(
                testUtil.createDate(1, 1, 2017),
                testUtil.createDate(1, 1, 2022));
        log.info("found members: " + testUtil.getIds(members));
        assertEquals(Arrays.asList(3L, 4L, 5L, 6L, 7L),
                new ArrayList<>(testUtil.getIds(members)));
    }

    @Test
    public void testGetAllMembersSince() {
        // all members that contain parts that were modified since 1.1.2007 (3-8)
        Iterable<Member> members = catalogService.getAllMembers(
                testUtil.createDate(1, 1, 2017),
                testUtil.createDate(1, 1, 2022));
        log.info("found members: " + testUtil.getIds(members));
        assertEquals(Arrays.asList(3L, 4L, 5L, 6L, 7L, 8L),
                new ArrayList<Long>(testUtil.getIds(members)));
    }

    @Test
    public void testGetAllMembers() {
        Iterable<Member> members = catalogService.getAllMembers();
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                new ArrayList<Long>(testUtil.getIds(members)));
    }

    @Test
    public void testGetActiveMembers() {
        Iterable<Member> members = catalogService.getActiveMembers();
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L),
                new ArrayList<Long>(testUtil.getIds(members)));
    }

    @Test
    public void testSaveUnmodifiedServices() {
        // test data:
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        // member (7) -> subsystem (8) -> service (8, removed) -> wsdl (6)
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Subsystem originalSub = subsystemRepository.findById(8L).get();
        Member originalMember = originalSub.getMember();
        Service originalService5 = serviceRepository.findById(5L).get();
        Service originalService6 = serviceRepository.findById(6L).get();
        Service originalRemovedService8 = serviceRepository.findById(8L).get();
        Service originalRemovedService9 = serviceRepository.findById(9L).get();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        Subsystem savedSub = new Subsystem();
        testUtil.shallowCopyFields(originalSub, savedSub);
        savedSub.setMember(originalMember);
        Service savedService5 = new Service();
        Service savedService6 = new Service();
        testUtil.shallowCopyFields(originalService5, savedService5);
        testUtil.shallowCopyFields(originalService6, savedService6);

        catalogService.saveServices(savedSub.createKey(), Lists.newArrayList(savedService5, savedService6));
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        // read back and verify
        Subsystem checkedSub = subsystemRepository.findById(8L).get();
        testUtil.assertAllSame(originalSub.getStatusInfo(), checkedSub.getStatusInfo());
        Service checkedService5 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 5L).get();
        Service checkedService6 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 6L).get();
        Service checkedService8 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 8L).get();
        Service checkedService9 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 9L).get();

        assertFalse(checkedService5.getStatusInfo().isRemoved());
        assertFalse(checkedService6.getStatusInfo().isRemoved());
        assertTrue(checkedService8.getStatusInfo().isRemoved());
        assertTrue(checkedService9.getStatusInfo().isRemoved());
        testUtil.assertFetchedIsOnlyDifferent(originalService5.getStatusInfo(), checkedService5.getStatusInfo());
        testUtil.assertFetchedIsOnlyDifferent(originalService6.getStatusInfo(), checkedService6.getStatusInfo());
        testUtil.assertAllSame(originalRemovedService8.getStatusInfo(), checkedService8.getStatusInfo());
        testUtil.assertAllSame(originalRemovedService9.getStatusInfo(), checkedService9.getStatusInfo());
    }

    @Test
    public void testSaveAddedNewServiceVersions() {
        // test data:
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        Subsystem originalSub = subsystemRepository.findById(8L).get();
        Member originalMember = originalSub.getMember();
        Service originalService6 = serviceRepository.findById(6L).get();
        // detach, so we dont modify those objects in the next steps

        Subsystem savedSub = new Subsystem();
        testUtil.shallowCopyFields(originalSub, savedSub);
        savedSub.setMember(originalMember);
        Service savedService6 = new Service();
        testUtil.shallowCopyFields(originalService6, savedService6);
        Service savedService6newVersion = new Service();
        Service savedService6nullVersion = new Service();
        testUtil.shallowCopyFields(originalService6, savedService6newVersion);
        testUtil.shallowCopyFields(originalService6, savedService6nullVersion);
        savedService6newVersion.setServiceVersion(savedService6.getServiceVersion() + "-new");
        savedService6nullVersion.setServiceVersion(null);

        catalogService.saveServices(savedSub.createKey(), Lists.newArrayList(savedService6, savedService6newVersion,
                savedService6nullVersion));
        long newVersionId = savedService6newVersion.getId();
        long nullVersionId = savedService6nullVersion.getId();

        // read back and verify
        Subsystem checkedSub = subsystemRepository.findById(8L).get();
        assertEquals(3, checkedSub.getActiveServices().size());
        testUtil.assertAllSame(originalSub.getStatusInfo(), checkedSub.getStatusInfo());
        Service checkedService6 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 6L).get();
        Service checkedService6newVersion = (Service) testUtil.getEntity(checkedSub.getAllServices(), newVersionId)
                .get();
        Service checkedService6nullVersion = (Service) testUtil.getEntity(checkedSub.getAllServices(), nullVersionId)
                .get();

        // assertFalse(checkedService6.getStatusInfo().isRemoved());
        // testUtil.assertFetchedIsOnlyDifferent(originalService6.getStatusInfo(),
        // checkedService6.getStatusInfo());
        assertNewService(checkedService6newVersion);
        assertNewService(checkedService6nullVersion);
    }

    @Test
    public void testSaveAddedServices() {
        // test data:
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        // member (7) -> subsystem (8) -> service (8, removed) -> wsdl (6)
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Subsystem originalSub = subsystemRepository.findById(8L).get();
        Member originalMember = originalSub.getMember();
        Service originalService5 = serviceRepository.findById(5L).get();
        Service originalService6 = serviceRepository.findById(6L).get();
        Service originalRemovedService8 = serviceRepository.findById(8L).get();
        Service originalRemovedService9 = serviceRepository.findById(9L).get();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        Subsystem savedSub = new Subsystem();
        testUtil.shallowCopyFields(originalSub, savedSub);
        savedSub.setMember(originalMember);
        Service savedService5 = new Service();
        Service savedService6 = new Service();
        testUtil.shallowCopyFields(originalService5, savedService5);
        testUtil.shallowCopyFields(originalService6, savedService6);
        Service newService = new Service();
        newService.setServiceCode("foocode-asddsa-ads");
        newService.setServiceVersion("v6");
        Service newServiceNullVersion = new Service();
        newServiceNullVersion.setServiceCode("foocode-asddsa-ads-null");
        newServiceNullVersion.setServiceVersion(null);
        Service newServiceEmptyVersion = new Service();
        newServiceEmptyVersion.setServiceCode("foocode-asddsa-ads-empty");
        newServiceEmptyVersion.setServiceVersion("");

        catalogService.saveServices(savedSub.createKey(),
                Lists.newArrayList(savedService5, savedService6, newService,
                        newServiceNullVersion, newServiceEmptyVersion));
        long newId = newService.getId();
        long newIdNull = newServiceNullVersion.getId();
        long newIdEmpty = newServiceEmptyVersion.getId();

        // read back and verify
        Subsystem checkedSub = subsystemRepository.findById(8L).get();
        testUtil.assertAllSame(originalSub.getStatusInfo(), checkedSub.getStatusInfo());
        Service checkedService5 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 5L).get();
        Service checkedService6 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 6L).get();
        Service checkedService8 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 8L).get();
        Service checkedService9 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 9L).get();
        Service checkedNewService = (Service) testUtil.getEntity(checkedSub.getAllServices(), newId).get();
        Service checkedNewServiceNull = (Service) testUtil.getEntity(checkedSub.getAllServices(), newIdNull).get();
        Service checkedNewServiceEmpty = (Service) testUtil.getEntity(checkedSub.getAllServices(), newIdEmpty).get();

        assertNewService(checkedNewService);
        assertNewService(checkedNewServiceNull);
        assertNewService(checkedNewServiceEmpty);

        assertFalse(checkedService5.getStatusInfo().isRemoved());
        assertFalse(checkedService6.getStatusInfo().isRemoved());
        assertTrue(checkedService8.getStatusInfo().isRemoved());
        assertTrue(checkedService9.getStatusInfo().isRemoved());
        testUtil.assertFetchedIsOnlyDifferent(originalService5.getStatusInfo(), checkedService5.getStatusInfo());
        testUtil.assertFetchedIsOnlyDifferent(originalService6.getStatusInfo(), checkedService6.getStatusInfo());
        testUtil.assertAllSame(originalRemovedService8.getStatusInfo(), checkedService8.getStatusInfo());
        testUtil.assertAllSame(originalRemovedService9.getStatusInfo(), checkedService9.getStatusInfo());
    }

    private void assertNewService(Service checkedNewService) {
        assertFalse(checkedNewService.getStatusInfo().isRemoved());
        assertNotNull(checkedNewService.getStatusInfo().getFetched());
        assertNotNull(checkedNewService.getStatusInfo().getCreated());
        assertNotNull(checkedNewService.getStatusInfo().getChanged());
        assertNull(checkedNewService.getStatusInfo().getRemoved());
    }

    @Test
    public void testGetService() {
        Service service = serviceRepository.findById(1L).get();
        Service foundService = catalogService.getService(service.getSubsystem().getMember().getXRoadInstance(),
                service.getSubsystem().getMember().getMemberClass(),
                service.getSubsystem().getMember().getMemberCode(),
                service.getServiceCode(),
                service.getSubsystem().getSubsystemCode(),
                service.getServiceVersion());
        assertNotNull(foundService);
    }

    @Test
    public void testGetServiceNullVersion() {
        Service service = serviceRepository.findById(10L).get();
        Service foundService = catalogService.getService(service.getSubsystem().getMember().getXRoadInstance(),
                service.getSubsystem().getMember().getMemberClass(),
                service.getSubsystem().getMember().getMemberCode(),
                service.getServiceCode(),
                service.getSubsystem().getSubsystemCode(),
                service.getServiceVersion());
        assertNotNull(foundService);
    }

    @Test
    public void testGetServices() {
        Service service = serviceRepository.findById(1L).get();
        List<Service> foundServices = catalogService.getServices(service.getSubsystem().getMember().getXRoadInstance(),
                service.getSubsystem().getMember().getMemberClass(),
                service.getSubsystem().getMember().getMemberCode(),
                service.getSubsystem().getSubsystemCode(),
                service.getServiceCode());
        assertNotNull(foundServices);
        assertEquals(1, foundServices.size());
    }

    @Test
    public void testSaveRemovedServices() {
        // test data:
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        // member (7) -> subsystem (8) -> service (8, removed) -> wsdl (6)
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        // member (7) -> subsystem (8) -> service (10) -> (-)
        Subsystem originalSub = subsystemRepository.findById(8L).get();
        Member originalMember = originalSub.getMember();
        Service originalService5 = serviceRepository.findById(5L).get();
        Service originalService6 = serviceRepository.findById(6L).get();
        Service originalRemovedService8 = serviceRepository.findById(8L).get();
        Service originalRemovedService9 = serviceRepository.findById(9L).get();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        // remove all services = save subsystem with empty services-collection
        Subsystem savedSub = new Subsystem();
        testUtil.shallowCopyFields(originalSub, savedSub);
        savedSub.setMember(originalMember);

        catalogService.saveServices(savedSub.createKey(),
                Lists.newArrayList());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        // read back and verify
        Subsystem checkedSub = subsystemRepository.findById(8L).get();
        testUtil.assertAllSame(originalSub.getStatusInfo(), checkedSub.getStatusInfo());

        assertEquals(Arrays.asList(5L, 6L, 8L, 9L, 10L, 11L, 12L, 13L, 14L),
                new ArrayList<>(testUtil.getIds(checkedSub.getAllServices())));
        assertTrue(checkedSub.getActiveServices().isEmpty());
        Service checkedService5 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 5L).get();
        Service checkedService6 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 6L).get();
        Service checkedService8 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 8L).get();
        Service checkedService9 = (Service) testUtil.getEntity(checkedSub.getAllServices(), 9L).get();

        assertTrue(checkedService5.getStatusInfo().isRemoved());
        assertTrue(checkedService6.getStatusInfo().isRemoved());
        assertTrue(checkedService8.getStatusInfo().isRemoved());
        assertTrue(checkedService9.getStatusInfo().isRemoved());
        testUtil.assertEqualities(originalService5.getStatusInfo(), checkedService5.getStatusInfo(),
                true, false, false, false);
        testUtil.assertEqualities(originalService6.getStatusInfo(), checkedService6.getStatusInfo(),
                true, false, false, false);
        testUtil.assertAllSame(originalRemovedService8.getStatusInfo(), checkedService8.getStatusInfo());
        testUtil.assertAllSame(originalRemovedService9.getStatusInfo(), checkedService9.getStatusInfo());
    }

    @Test
    public void testOverwriteIdenticalWsdl() {
        // "changed" is not updated
        // fetched is updated
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        Wsdl originalWsdl = wsdlRepository.findById(4L).get();
        Service originalService = originalWsdl.getService();
        ServiceId originalServiceId = originalWsdl.getService().createKey();
        SubsystemId originalSubsystemId = originalWsdl.getService().getSubsystem().createKey();
        assertEquals("SubsystemId(subsystemCode=subsystem_7-1)", originalSubsystemId.toString());
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveWsdl(originalSubsystemId, originalServiceId, originalWsdl.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Wsdl checkedWsdl = wsdlRepository.findById(4L).get();
        assertEquals(originalWsdl.getExternalId(), checkedWsdl.getExternalId());
        assertEquals(originalWsdl.getData(), checkedWsdl.getData());
        assertEquals(originalWsdl.getExternalId(), checkedWsdl.getExternalId());
        assertEquals(originalWsdl.getService().createKey(), originalServiceId);
        testUtil.assertFetchedIsOnlyDifferent(originalWsdl.getStatusInfo(), checkedWsdl.getStatusInfo());
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedWsdl.getService().getStatusInfo());
    }

    @Test
    public void testOverwriteIdenticalOpenApi() {
        OpenApi originalOpenApi = openApiRepository.findById(2L).get();
        Service originalService = originalOpenApi.getService();
        ServiceId originalServiceId = originalOpenApi.getService().createKey();
        SubsystemId originalSubsystemId = originalOpenApi.getService().getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveOpenApi(originalSubsystemId, originalServiceId, originalOpenApi.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        OpenApi checkedOpenApi = openApiRepository.findById(2L).get();
        assertEquals(originalOpenApi.getExternalId(), checkedOpenApi.getExternalId());
        assertEquals(originalOpenApi.getData(), checkedOpenApi.getData());
        assertEquals(originalOpenApi.getExternalId(), checkedOpenApi.getExternalId());
        assertEquals(originalOpenApi.getService().createKey(), originalServiceId);
        testUtil.assertFetchedIsOnlyDifferent(originalOpenApi.getStatusInfo(), checkedOpenApi.getStatusInfo());
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedOpenApi.getService().getStatusInfo());
    }

    @Test
    public void testOverwriteIdenticalRest() {
        Rest originalRest = restRepository.findById(1L).get();
        Service originalService = originalRest.getService();
        ServiceId originalServiceId = originalRest.getService().createKey();
        SubsystemId originalSubsystemId = originalRest.getService().getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveRest(originalSubsystemId, originalServiceId, originalRest.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Rest checkedRest = restRepository.findById(1L).get();
        assertEquals(originalRest.getExternalId(), checkedRest.getExternalId());
        assertEquals(originalRest.getData(), checkedRest.getData());
        assertEquals(originalRest.getExternalId(), checkedRest.getExternalId());
        assertEquals(originalRest.getService().createKey(), originalServiceId);
        testUtil.assertFetchedIsOnlyDifferent(originalRest.getStatusInfo(), checkedRest.getStatusInfo());
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedRest.getService().getStatusInfo());
    }

    @Test
    public void testOverwriteModifiedWsdl() {
        // "changed" is updated
        // fetched is also updated
        // member (7) -> subsystem (8) -> service (6) -> wsdl (4)
        Wsdl originalWsdl = wsdlRepository.findById(4L).get();
        Service originalService = originalWsdl.getService();
        ServiceId originalServiceId = originalWsdl.getService().createKey();
        SubsystemId originalSubsystemId = originalWsdl.getService().getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveWsdl(originalSubsystemId, originalServiceId, originalWsdl.getData() + "-modification");
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Wsdl checkedWsdl = wsdlRepository.findById(4L).get();
        assertEquals(originalWsdl.getExternalId(), checkedWsdl.getExternalId());
        assertNotEquals(originalWsdl.getData(), checkedWsdl.getData());
        assertEquals(originalWsdl.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalWsdl.getStatusInfo(), checkedWsdl.getStatusInfo(),
                true, false, true, false);
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedWsdl.getService().getStatusInfo());
    }

    @Test
    public void testOverwriteModifiedOpenApi() {
        OpenApi originalOpenApi = openApiRepository.findById(2L).get();
        Service originalService = originalOpenApi.getService();
        ServiceId originalServiceId = originalOpenApi.getService().createKey();
        SubsystemId originalSubsystemId = originalOpenApi.getService().getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveOpenApi(originalSubsystemId, originalServiceId, originalOpenApi.getData() + "-modification");
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        OpenApi checkedOpenApi = openApiRepository.findById(2L).get();
        assertEquals(originalOpenApi.getExternalId(), checkedOpenApi.getExternalId());
        assertNotEquals(originalOpenApi.getData(), checkedOpenApi.getData());
        assertEquals(originalOpenApi.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalOpenApi.getStatusInfo(), checkedOpenApi.getStatusInfo(),
                true, false, true, false);
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedOpenApi.getService().getStatusInfo());
    }

    @Test
    public void testOverwriteModifiedRest() {
        Rest originalRest = restRepository.findById(1L).get();
        Service originalService = originalRest.getService();
        ServiceId originalServiceId = originalRest.getService().createKey();
        SubsystemId originalSubsystemId = originalRest.getService().getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        catalogService.saveRest(originalSubsystemId, originalServiceId, originalRest.getData() + "-modification");
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Rest checkedRest = restRepository.findById(1L).get();
        assertEquals(originalRest.getExternalId(), checkedRest.getExternalId());
        assertNotEquals(originalRest.getData(), checkedRest.getData());
        assertEquals(originalRest.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalRest.getStatusInfo(), checkedRest.getStatusInfo(),
                true, false, true, false);
        testUtil.assertAllSame(originalService.getStatusInfo(), checkedRest.getService().getStatusInfo());
    }

    @Test
    public void testSaveNewWsdl() {
        // member (5) -> subsystem (6) -> service (3) -> wsdl (*new*)
        Service oldService = serviceRepository.findById(3L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();

        final String data = "<testwsdl/>";
        Wsdl newWsdl = new Wsdl(oldService, data, "1");
        newWsdl.initializeExternalId();
        assertNotNull(newWsdl.getExternalId());
        catalogService.saveWsdl(originalSubsystemId, originalServiceId, data);

        Service checkedService = serviceRepository.findById(3L).get();
        Wsdl checkedWsdl = checkedService.getWsdl();
        log.info("externalId [{}]", checkedWsdl.getExternalId());
        assertTrue(checkedService.hasWsdl());
        assertNotNull(checkedWsdl.getExternalId());
        assertEquals(data, checkedWsdl.getData());
        assertEquals(checkedWsdl.getService().createKey(), originalServiceId);
        assertNotNull(checkedWsdl.getStatusInfo().getCreated());
        assertNotNull(checkedWsdl.getStatusInfo().getChanged());
        assertNotNull(checkedWsdl.getStatusInfo().getFetched());
        assertNull(checkedWsdl.getStatusInfo().getRemoved());
        testUtil.assertAllSame(oldService.getStatusInfo(), checkedWsdl.getService().getStatusInfo());
    }

    @Test
    public void testSaveNewOpenApi() {
        // member (5) -> subsystem (6) -> service (3) -> wsdl (*new*)
        Service oldService = serviceRepository.findById(12L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        Service aService = new Service(oldService.getSubsystem(), "code", "version");
        final String data = "<testopenapi/>";
        OpenApi newOpenApi = new OpenApi(aService, data, "1");
        newOpenApi.initializeExternalId();
        assertNotNull(newOpenApi.getExternalId());
        aService.setOpenApi(newOpenApi);
        assertTrue(aService.hasOpenApi());

        catalogService.saveOpenApi(originalSubsystemId, originalServiceId, data);
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Service checkedService = serviceRepository.findById(12L).get();
        OpenApi checkedOpenApi = checkedService.getOpenApi();
        log.info("externalId [{}]", checkedOpenApi.getExternalId());
        assertNotNull(checkedOpenApi.getExternalId());
        assertEquals(data, checkedOpenApi.getData());
        assertTrue(checkedService.hasOpenApi());
        assertEquals(checkedOpenApi.getService().createKey(), originalServiceId);
        assertNotNull(checkedOpenApi.getStatusInfo().getCreated());
        assertNotNull(checkedOpenApi.getStatusInfo().getChanged());
        assertNotNull(checkedOpenApi.getStatusInfo().getFetched());
        assertNull(checkedOpenApi.getStatusInfo().getRemoved());
        testUtil.assertAllSame(oldService.getStatusInfo(), checkedOpenApi.getService().getStatusInfo());
    }

    @Test
    public void testSaveNewRest() {
        // member (5) -> subsystem (6) -> service (3) -> wsdl (*new*)
        Service oldService = serviceRepository.findById(13L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        Service aService = new Service(oldService.getSubsystem(), "code", "version");
        final String data = "{\"endpoint_list\": []}";
        Rest newRest = new Rest(aService, data, "1");
        newRest.initializeExternalId();
        assertNotNull(newRest.getExternalId());
        aService.setRest(newRest);
        assertTrue(aService.hasRest());

        catalogService.saveRest(originalSubsystemId, originalServiceId, data);
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Service checkedService = serviceRepository.findById(13L).get();
        Rest foundRest = catalogService.getRest(checkedService);
        assertNotNull(foundRest);
        Rest checkedRest = checkedService.getRest();
        log.info("externalId [{}]", checkedRest.getExternalId());
        assertNotNull(checkedRest.getExternalId());
        assertEquals(data, checkedRest.getData());
        assertTrue(checkedService.hasRest());
        assertEquals(checkedRest.getService().createKey(), originalServiceId);
        assertNotNull(checkedRest.getStatusInfo().getCreated());
        assertNotNull(checkedRest.getStatusInfo().getChanged());
        assertNotNull(checkedRest.getStatusInfo().getFetched());
        assertNull(checkedRest.getStatusInfo().getRemoved());
        testUtil.assertAllSame(oldService.getStatusInfo(), checkedRest.getService().getStatusInfo());
    }

    @Test
    public void testResurrectWsdl() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(9L).get();
        // fix test data so that service is not removed
        oldService.getStatusInfo().setRemoved(null);
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Wsdl originalWsdl = oldService.getWsdl();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        catalogService.saveWsdl(originalSubsystemId, originalServiceId, originalWsdl.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Service checkedService = serviceRepository.findById(9L).get();
        Wsdl checkedWsdl = checkedService.getWsdl();
        assertEquals(originalWsdl.getExternalId(), checkedWsdl.getExternalId());
        assertEquals(7L, checkedWsdl.getId());
        assertEquals(originalWsdl.getData(), checkedWsdl.getData());
        assertEquals(checkedWsdl.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalWsdl.getStatusInfo(), checkedWsdl.getStatusInfo(),
                true, false, false, false);
        assertNull(checkedWsdl.getStatusInfo().getRemoved());
    }

    @Test
    public void testResurrectOpenApi() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(11L).get();
        // fix test data so that service is not removed
        oldService.getStatusInfo().setRemoved(null);
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        OpenApi originalOpenApi = oldService.getOpenApi();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        catalogService.saveOpenApi(originalSubsystemId, originalServiceId, originalOpenApi.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Service checkedService = serviceRepository.findById(11L).get();
        OpenApi checkedOpenApi = checkedService.getOpenApi();
        assertEquals(originalOpenApi.getExternalId(), checkedOpenApi.getExternalId());
        assertEquals(1L, checkedOpenApi.getId());
        assertEquals(originalOpenApi.getData(), checkedOpenApi.getData());
        assertEquals(checkedOpenApi.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalOpenApi.getStatusInfo(), checkedOpenApi.getStatusInfo(),
                true, false, false, false);
        assertNull(checkedOpenApi.getStatusInfo().getRemoved());
    }

    @Test
    public void testResurrectRest() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(14L).get();
        // fix test data so that service is not removed
        oldService.getStatusInfo().setRemoved(null);
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Rest originalRest = oldService.getRest();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        catalogService.saveRest(originalSubsystemId, originalServiceId, originalRest.getData());
        testUtil.entityManagerFlush();
        testUtil.entityManagerClear();

        Service checkedService = serviceRepository.findById(14L).get();
        Rest checkedRest = checkedService.getRest();
        assertEquals(originalRest.getExternalId(), checkedRest.getExternalId());
        assertEquals(2L, checkedRest.getId());
        assertEquals(originalRest.getData(), checkedRest.getData());
        assertEquals(checkedRest.getService().createKey(), originalServiceId);
        testUtil.assertEqualities(originalRest.getStatusInfo(), checkedRest.getStatusInfo(),
                true, false, false, false);
        assertNull(checkedRest.getStatusInfo().getRemoved());
    }

    @Test
    public void testSaveWsdlFailsForRemovedService() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(9L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Wsdl originalWsdl = oldService.getWsdl();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        try {
            catalogService.saveWsdl(originalSubsystemId, originalServiceId, originalWsdl.getData());
            fail("should have throw exception since service is removed");
        } catch (Exception expected) {
            // Exception is expected }
        }
    }

    @Test
    public void testSaveOpenApiFailsForRemovedService() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(9L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        OpenApi originalOpenApi = oldService.getOpenApi();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        try {
            catalogService.saveOpenApi(originalSubsystemId, originalServiceId, originalOpenApi.getData());
            fail("should have throw exception since service is removed");
        } catch (Exception expected) {
            // Exception is expected }
        }
    }

    @Test
    public void testSaveRestFailsForRemovedService() {
        // member (7) -> subsystem (8) -> service (9, removed) -> wsdl (7, removed)
        Service oldService = serviceRepository.findById(14L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Rest originalRest = oldService.getRest();
        // detach, so we dont modify those objects in the next steps
        testUtil.entityManagerClear();

        try {
            catalogService.saveRest(originalSubsystemId, originalServiceId, originalRest.getData());
            fail("should have throw exception since service is removed");
        } catch (Exception expected) {
            // Exception is expected }
        }
    }

    @Test
    public void testGetLastCollectionData() {
        LastCollectionData lastCollectionData = catalogService.getLastCollectionData();
        assertEquals(2017, lastCollectionData.getMembersLastFetched().getYear());
        assertEquals(2016, lastCollectionData.getOpenapisLastFetched().getYear());
        assertEquals(2017, lastCollectionData.getServicesLastFetched().getYear());
        assertEquals(2017, lastCollectionData.getSubsystemsLastFetched().getYear());
        assertEquals(2017, lastCollectionData.getWsdlsLastFetched().getYear());
    }

    @Test
    public void testGetServiceStatistics() throws JSONException {
        LocalDateTime startDateTime = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 1, 1, 0, 0);
        List<ServiceStatistics> serviceStatistics = catalogService.getServiceStatistics(startDateTime, endDateTime);
        assertEquals(2923, serviceStatistics.size());
    }

    @Test
    public void testGetDistinctServiceStatistics() throws JSONException {
        LocalDateTime startDateTime = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 1, 1, 0, 0);
        List<DistinctServiceStatistics> distinctServiceStatistics = catalogService
                .getDistinctServiceStatistics(startDateTime, endDateTime);
        assertEquals(2923, distinctServiceStatistics.size());
    }

    @Test
    public void testGetMemberData() throws JSONException {
        LocalDateTime startDateTime = LocalDateTime.of(2014, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 1, 1, 0, 0);
        List<MemberDataList> members = catalogService.getMemberData(startDateTime, endDateTime);
        assertEquals(2923, members.size());
    }

    @Test
    public void testSaveServices() {
        Service oldService = serviceRepository.findById(14L).get();
        oldService.getStatusInfo().setRemoved(null);
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Collection<Service> services = new ArrayList<>();
        Member member = testUtil.createTestMember("newMember");
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService1", "v1"));
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService2", "v1"));
        catalogService.saveServices(originalSubsystemId, services);
        Subsystem subsystem = subsystemRepository.findActiveByNaturalKey(originalSubsystemId.getXRoadInstance(),
                originalSubsystemId.getMemberClass(),
                originalSubsystemId.getMemberCode(),
                originalSubsystemId.getSubsystemCode());
        List<Service> foundServices = subsystem.getAllServices().stream()
                .filter(service -> service.getServiceCode().equalsIgnoreCase("newService1"))
                .collect(Collectors.toList());
        assertTrue(foundServices.size() > 0);
        foundServices = subsystem.getAllServices().stream()
                .filter(service -> service.getServiceCode().equalsIgnoreCase("newService2"))
                .collect(Collectors.toList());
        assertTrue(foundServices.size() > 0);
    }

    @Test
    public void testSaveServicesSubsystemIdNull() {
        Service oldService = serviceRepository.findById(14L).get();
        oldService.getStatusInfo().setRemoved(null);
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Collection<Service> services = new ArrayList<>();
        Member member = testUtil.createTestMember("newMember");
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService1", "v1"));
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService2", "v1"));
        try {
            catalogService.saveServices(null, services);
        } catch (IllegalStateException e) {
            assertEquals("subsystem null not found!", e.getMessage());
        }
    }

    @Test
    public void testSaveServicesSubsystemNotFound() {
        Service oldService = serviceRepository.findById(14L).get();
        oldService.getStatusInfo().setRemoved(null);
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Collection<Service> services = new ArrayList<>();
        Member member = testUtil.createTestMember("newMember");
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService1", "v1"));
        services.add(new Service(new Subsystem(member, originalSubsystemId.getSubsystemCode()), "newService2", "v1"));
        try {
            catalogService.saveServices(new SubsystemId("INSTANCE", "CLASS", "CODE", "SUBSYSTEM"), services);
        } catch (IllegalStateException e) {
            assertEquals("subsystem SubsystemId(subsystemCode=SUBSYSTEM) not found!", e.getMessage());
        }
    }

    @Test
    public void testPrepareEndpoints() {
        Service oldService = serviceRepository.findById(13L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Set<Endpoint> endpoints = oldService.getAllEndpoints();
        endpoints.forEach(endpoint -> {
            assertFalse(endpoint.getStatusInfo().isRemoved());
        });
        catalogService.prepareEndpoints(originalSubsystemId, originalServiceId);
        oldService = serviceRepository.findById(13L).get();
        endpoints = oldService.getAllEndpoints();
        endpoints.forEach(endpoint -> {
            assertTrue(endpoint.getStatusInfo().isRemoved());
        });
    }

    @Test
    public void testOverwriteExistingEndpoint() {
        Service oldService = serviceRepository.findById(12L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        Endpoint endpoint = endpointRepository.findAnyByServicePathAndMethod(oldService, "POST", "/setOtherData");
        assertTrue(endpoint.getStatusInfo().isRemoved());
        catalogService.saveEndpoint(originalSubsystemId, originalServiceId, "POST", "/setOtherData");
        endpoint = endpointRepository.findAnyByServicePathAndMethod(oldService, "POST", "/setOtherData");
        assertFalse(endpoint.getStatusInfo().isRemoved());
    }

    @Test
    public void testSaveEndpointSubsystemIdRequired() {
        Service oldService = serviceRepository.findById(12L).get();
        ServiceId originalServiceId = oldService.createKey();
        try {
            catalogService.saveEndpoint(null, originalServiceId, "POST", "/doSomething");
        } catch (IllegalArgumentException e) {
            assertEquals("subsystemId is required", e.getMessage());
        }
    }

    @Test
    public void testSaveEndpointServiceIdRequired() {
        Service oldService = serviceRepository.findById(12L).get();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        try {
            catalogService.saveEndpoint(originalSubsystemId, null, "POST", "/doSomething");
        } catch (IllegalArgumentException e) {
            assertEquals("serviceId is required", e.getMessage());
        }
    }

    @Test
    public void testSaveEndpointMethodRequired() {
        Service oldService = serviceRepository.findById(12L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        try {
            catalogService.saveEndpoint(originalSubsystemId, originalServiceId, null, "/doSomething");
        } catch (IllegalArgumentException e) {
            assertEquals("method is required", e.getMessage());
        }
    }

    @Test
    public void testSaveEndpointPathRequired() {
        Service oldService = serviceRepository.findById(12L).get();
        ServiceId originalServiceId = oldService.createKey();
        SubsystemId originalSubsystemId = oldService.getSubsystem().createKey();
        try {
            catalogService.saveEndpoint(originalSubsystemId, originalServiceId, "POST", null);
        } catch (IllegalArgumentException e) {
            assertEquals("path is required", e.getMessage());
        }
    }

    @Test
    public void testDeleteOldErrorLogEntries() {
        ErrorLog foundErrorLog = errorLogRepository.findById(7L).get();
        assertEquals("Service not found7", foundErrorLog.getMessage());
        LocalDateTime created = foundErrorLog.getCreated();
        Duration duration = Duration.between(LocalDateTime.now(), created);
        long daysBefore = duration.toDays() < 0 ? duration.toDays() * (-1) : duration.toDays();
        catalogService.deleteOldErrorLogEntries((int) daysBefore);
        Set<ErrorLog> foundErrorLogs = errorLogRepository.findAny(LocalDateTime.now().minusDays((int) daysBefore + 1),
                LocalDateTime.now());
        assertEquals(0, foundErrorLogs.size());
    }

    @Test
    public void testCheckDatabaseConnection() {
        assertTrue(catalogService.checkDatabaseConnection());
    }

}
