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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This is effectively equivalent to Unix's /dev/null for streams.
 * <p>
 * Use this stream class on conjunction {@link System#setErr(PrintStream)} or
 * {@link System#setOut(PrintStream)} to block output to {@code stderr} and/or
 * {@code stdout} respectively.
 *
 * @see NullDeviceConsole
 */
public class NullDevicePrintStream extends PrintStream {

    public NullDevicePrintStream() {
        super(new DeviceNullOutputStream());
    }

    private static class DeviceNullOutputStream extends ByteArrayOutputStream {
        public void write(byte[] b, int off, int len) {
            // Do nothing
        }

        public void write(int b) {
            // Do nothing
        }

        public void writeTo(OutputStream out) throws IOException {
            // Do nothing
        }
    }

}
