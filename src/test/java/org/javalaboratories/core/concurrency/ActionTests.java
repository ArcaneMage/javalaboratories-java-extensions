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
package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActionTests {

    private static final Logger logger = LoggerFactory.getLogger(ActionTests.class);

    private PrimaryAction<String> primaryAction;
    private TaskAction<String> taskAction;
    private TransmuteAction<String,String> transmuteAction;

    @BeforeEach
    public void setup() {
        Database database = new Database();

        primaryAction = PrimaryAction.of(database::readValue,this::handler);
        taskAction = TaskAction.of(value -> System.out.println(database.readValue()),this::handler);
        transmuteAction = TransmuteAction.of(value -> "Value read: "+database.readValue(),this::handler);
    }

    @Test
    public void testEqual_Pass() {
        // Given (Setup)
        Database database = new Database();
        PrimaryAction<String> primaryAction2 = PrimaryAction.of(database::readValue,this::handler);
        TaskAction<String> taskAction2 = TaskAction.of(value -> System.out.println(database.readValue()),this::handler);
        TransmuteAction<String,String> transmuteAction2 = TransmuteAction.of(value -> "Value read: "+database.readValue(),this::handler);

        // Then
        assertNotEquals(primaryAction,primaryAction2);
        assertNotEquals(taskAction,taskAction2);
        assertNotEquals(transmuteAction,transmuteAction2);
    }

    @Test
    public void testGetTask_Pass() {
        // Given (Setup)

        // Then
        assertNotNull(primaryAction.getTask().orElseThrow());
        assertNotNull(taskAction.getTask().orElseThrow());
        assertNotNull(transmuteAction.getTask().orElseThrow());
    }

    @Test
    public void testGetCompletionHandler_Pass() {
        // Given (Setup)

        // Then
        assertNotNull(primaryAction.getCompletionHandler().orElseThrow());
        assertNotNull(taskAction.getCompletionHandler().orElseThrow());
        assertNotNull(transmuteAction.getCompletionHandler().orElseThrow());
    }

    private void handler(String value,Throwable error) {
        if (error != null) {
            logger.error("Error:", error);
        } else {
            logger.info("Success: {}",value);
        }
    }

    // Some contrived use case
    private static class Database {
        public String readValue() {
            return "Some value from database storage";
        }
    }
}
