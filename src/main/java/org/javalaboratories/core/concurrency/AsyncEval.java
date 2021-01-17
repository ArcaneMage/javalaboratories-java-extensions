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

import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Maybe;

public interface AsyncEval<T> extends Eval<T> {
    /**
     * @return true to indicate asynchronous process is complete whether
     * successfully or unsuccessfully.
     */
    boolean isComplete();
    /**
     * @return true to indicate asynchronous process has completed
     * successfully.
     */
    boolean isFulfilled();
    /**
     * @return true to indicate asynchronous process has encountered an
     * exception.
     */
    boolean isRejected();
    /**
     * @return the exception thrown in the asynchronous process as a {@link
     * Maybe} object.
     */
    Maybe<Exception> getException();
}

