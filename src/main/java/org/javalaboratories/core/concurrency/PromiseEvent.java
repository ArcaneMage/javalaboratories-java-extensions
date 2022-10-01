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

import lombok.Getter;
import org.javalaboratories.core.event.AbstractEvent;

import java.util.Arrays;
import java.util.Objects;

@Getter
public class PromiseEvent<T> extends AbstractEvent {
    private static final long serialVersionUID = 8029123351197793513L;

    public enum Actions {PRIMARY_ACTION,TASK_ACTION,TRANSMUTE_ACTION}

    private final Actions action;
    private final T value;

    public PromiseEvent(Actions action, T value) {
      this.action = action;
      this.value = value;
    }

    public boolean isAny(Actions... actions) {
        Objects.requireNonNull(actions);
        return Arrays.asList(actions).contains(this.action);
    }
}
