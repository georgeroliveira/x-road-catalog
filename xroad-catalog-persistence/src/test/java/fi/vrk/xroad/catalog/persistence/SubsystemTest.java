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

import fi.vrk.xroad.catalog.persistence.entity.Subsystem;
import fi.vrk.xroad.catalog.persistence.repository.SubsystemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SubsystemTest {

    @Autowired
    SubsystemRepository subsystemRepository;

    @Autowired
    TestUtil testUtil;

    @Test
    public void testGetActiveServices() {
        // 5+6+10 are active, 8+9 are removed
        Subsystem sub = subsystemRepository.findById(8L).get();
        assertEquals(Arrays.asList(5L, 6L, 10L, 12L, 13L),
                new ArrayList<Long>(testUtil.getIds(sub.getActiveServices())));
    }

    @Test
    public void testGetAllServices() {
        // 5+6+10 are active, 8+9 are removed
        Subsystem sub = subsystemRepository.findById(8L).get();
        assertEquals(Arrays.asList(5L, 6L, 8L, 9L, 10L, 11L, 12L, 13L, 14L),
                new ArrayList<Long>(testUtil.getIds(sub.getAllServices())));
    }

}
