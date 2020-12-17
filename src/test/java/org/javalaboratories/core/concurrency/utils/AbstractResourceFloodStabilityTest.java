/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.concurrency.utils;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceFloodStabilityTest {
    static final Logger logger = LoggerFactory.getLogger(AbstractResourceFloodStabilityTest.class);

    /**
     * Mutable object -- not thread-safe.
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    class UnsafeStatistics {
        int total;
        int requests;
        float average;

        public int add(final int value) {
            total = total + value;
            requests = requests + 1;
            average = div(requests);
            return total;
        }

        public float div(final int value) {
            if (value < 0)
                throw new IllegalArgumentException(String.format("Positive value only: (%d)",value));
            return total / (float) value;
        }

        public void longRunningIO(){
            sleep(11000);
        }

        public void print() {
            logger.info("print() - total={}, requests={}, average={}", total, requests, average);
        }
    }

    /**
     * Mutable object -- thread-safe
     */
    class SafeStatistics extends UnsafeStatistics {
        public int add(final int value) {
            if (value < 0)
                throw new IllegalArgumentException();
            synchronized (this) {
                return super.add(value);
            }
        }

        public float div(final int value) {
            synchronized (this) {
                return super.div(value);
            }
        }
    }

    UnsafeStatistics unsafe;
    SafeStatistics safe;

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Input/Output interrupted");
        }
    }
}
