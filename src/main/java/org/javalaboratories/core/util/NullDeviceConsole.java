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
package org.javalaboratories.core.util;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.PrintStream;

/**
 * Simulates console redirection to /dev/null for both stderr and stdout.
 * <p>
 * This class is often used in situations where console output needs to be
 * suppressed, for example: suppressing unwanted output from SLF4J that
 * bypasses the logging framework in with direct output via {@code
 * System.err} or {@code System.out}.
 * <p>
 * It is singleton class and can only be access via the factory method
 * {@code getInstance}.
 */
public final class NullDeviceConsole {

    private static final NullDeviceConsole INSTANCE = new NullDeviceConsole();

    private PrintStream stdErr;
    private PrintStream stdOut;

    @Getter (AccessLevel.PUBLIC)
    private boolean stdErrRedirected, stdOutRedirected;

    private NullDeviceConsole() { }

    public static NullDeviceConsole getInstance() {
        return INSTANCE;
    }

    /**
     * Enables system stderr output, restoring PrintStream object to original
     * value.
     * <p>
     * Multiple calls of this method are idempotent in that subsequent calls
     * will do nothing.
     */
    public synchronized void enableStdErr() {
        if (stdErrRedirected) {
            System.setErr(stdErr);
            stdErr = null;
            stdErrRedirected = false;
        }
    }

    /**
     * Disables system stderr output, saving PrintStream object for later
     * retrieval.
     * <p>
     * Multiple calls of this method are idempotent in that subsequent calls
     * will do nothing.
     */
    public synchronized void disableStdErr() {
        if (!stdErrRedirected) {
            stdErr = System.err;
            System.setErr(new NullDevicePrintStream());
            stdErrRedirected = true;
        }
    }

    /**
     * Enables system stdout output, restoring PrintStream object to original
     * value.
     * <p>
     * Multiple calls of this method are idempotent in that subsequent calls
     * will do nothing.
     */
    public synchronized void enableStdOut() {
        if (stdOutRedirected) {
            System.setOut(stdOut);
            stdOut = null;
            stdOutRedirected = false;
        }
    }

    /**
     * Disables system stdout output, saving PrintStream object for later
     * retrieval.
     * <p>
     * Multiple calls of this method are idempotent in that subsequent calls
     * will do nothing.
     */
    public synchronized void disableStdOut() {
        if (!stdOutRedirected) {
            stdOut = System.out;
            System.setOut(new NullDevicePrintStream());
            stdOutRedirected = true;
        }
    }
}
