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
module javalaboratories.java.extensions {
  requires lombok;
  requires org.apache.commons.codec;
  requires org.apache.commons.lang3;
  requires slf4j.api;

  exports org.javalaboratories.core;
  exports org.javalaboratories.core.collection;
  exports org.javalaboratories.core.concurrency;
  exports org.javalaboratories.core.concurrency.utils;
  exports org.javalaboratories.core.cryptography;
  exports org.javalaboratories.core.cryptography.keys;
  exports org.javalaboratories.core.event;
  exports org.javalaboratories.core.function;
  exports org.javalaboratories.core.handlers;
  exports org.javalaboratories.core.statistics;
  exports org.javalaboratories.core.tuple;
  exports org.javalaboratories.core.util;
}