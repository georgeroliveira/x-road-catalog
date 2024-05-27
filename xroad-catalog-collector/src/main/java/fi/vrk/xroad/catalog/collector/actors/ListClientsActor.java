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
package fi.vrk.xroad.catalog.collector.actors;

import fi.vrk.xroad.catalog.collector.util.ClientListUtil;
import fi.vrk.xroad.catalog.collector.util.ClientTypeUtil;
import fi.vrk.xroad.catalog.collector.wsimport.ClientList;
import fi.vrk.xroad.catalog.collector.wsimport.ClientType;
import fi.vrk.xroad.catalog.collector.wsimport.XRoadObjectType;
import fi.vrk.xroad.catalog.persistence.CatalogService;
import fi.vrk.xroad.catalog.persistence.entity.Member;
import fi.vrk.xroad.catalog.persistence.entity.MemberId;
import fi.vrk.xroad.catalog.persistence.entity.Subsystem;
import akka.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope("prototype")
@Slf4j
public class ListClientsActor extends XRoadCatalogActor {

    public static final String START_COLLECTING = "StartCollecting";

    @Autowired
    @Qualifier("listClientsRestOperations")
    private RestOperations restOperations;

    @Autowired
    protected CatalogService catalogService;

    @Value("${xroad-catalog.list-clients-host}")
    private String host;

    @Value("${xroad-catalog.fetch-run-unlimited}")
    private Boolean fetchUnlimited;

    @Value("${xroad-catalog.fetch-time-after-hour}")
    private Integer fetchTimeAfterHour;

    @Value("${xroad-catalog.fetch-time-before-hour}")
    private Integer fetchTimeBeforeHour;

    private static AtomicInteger clientCounter = new AtomicInteger(0);

    // supervisor-created pool of list clients actors
    protected ActorRef listMethodsPoolRef;

    public ListClientsActor(ActorRef listMethodsPoolRef) {
        this.listMethodsPoolRef = listMethodsPoolRef;
    }

    @Override
    public void preStart() throws Exception {
        log.info("preStart {}", this.hashCode());
        super.preStart();
    }

    @Override
    protected boolean handleMessage(Object message)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (START_COLLECTING.equals(message)) {
            if (Boolean.TRUE.equals(fetchUnlimited) || isTimeBetweenHours(fetchTimeAfterHour, fetchTimeBeforeHour)) {
                return fetchClients();
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean fetchClients() {
        String listClientsUrl = host + "/listClients";

        log.info("Getting client list from {}", listClientsUrl);
        ClientList clientList = ClientListUtil.clientListFromResponse(listClientsUrl, catalogService);
        HashMap<MemberId, Member> m = populateMapWithMembers(clientList);
        catalogService.saveAllMembersAndSubsystems(m.values());

        for (ClientType clientType : clientList.getMember()) {
            listMethodsPoolRef.tell(clientType, getSelf());
        }

        log.info("all clients (" + (clientCounter.get() - 1) + ") sent to actor");

        return true;
    }

    private static boolean isTimeBetweenHours(int fetchHourAfter, int fetchHourBefore) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime fetchTimeFrom = LocalDate.now().atTime(fetchHourAfter, 0);
        LocalDateTime fetchTimeTo = LocalDate.now().atTime(fetchHourBefore, 0);
        return (today.isAfter(fetchTimeFrom) && today.isBefore(fetchTimeTo));
    }

    private static HashMap<MemberId, Member> populateMapWithMembers(ClientList clientList) {
        HashMap<MemberId, Member> m = new HashMap<>();
        for (ClientType clientType : clientList.getMember()) {
            log.info("{} - {}", clientCounter.addAndGet(1), ClientTypeUtil.toString(clientType));
            Member newMember = new Member(clientType.getId().getXRoadInstance(), clientType.getId()
                    .getMemberClass(),
                    clientType.getId().getMemberCode(), clientType.getName());
            newMember.setSubsystems(new HashSet<>());
            m.putIfAbsent(newMember.createKey(), newMember);

            if (XRoadObjectType.SUBSYSTEM.equals(clientType.getId().getObjectType())) {
                Subsystem newSubsystem = new Subsystem(newMember, clientType.getId().getSubsystemCode());
                m.get(newMember.createKey()).getAllSubsystems().add(newSubsystem);
            }
        }

        return m;
    }
}
