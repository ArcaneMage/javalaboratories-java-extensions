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

public final class Bytes {

    public static byte[] concat(final byte[] first, final byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first,0,result,0,first.length);
        System.arraycopy(second,0,result,first.length,second.length);
        return result;
    }

    public static byte[] copy(final byte[] source) {
        byte[] result = new byte[source.length];
        System.arraycopy(source,0,result,0,source.length);
        return result;
    }

    public static byte[] trimLeft(final byte[] source, int bytes) {
        byte[] result = new byte[source.length - bytes];
        System.arraycopy(source,bytes,result,0,source.length - bytes);
        return result;
    }

    public static byte[] trimRight(final byte[] source, int bytes) {
        byte[] result = new byte[source.length - bytes];
        System.arraycopy(source,0,result,0,source.length - bytes);
        return result;
    }

    private Bytes() {}
}
