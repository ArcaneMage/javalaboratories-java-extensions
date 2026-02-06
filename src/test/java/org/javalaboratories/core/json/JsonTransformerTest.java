/*
 * Copyright 2024 Kevin Henry
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
package org.javalaboratories.core.json;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;

import static org.javalaboratories.core.json.JsonTransformer.FLAG_PRETTY_FORMAT;
import static org.javalaboratories.core.json.JsonTransformer.FLAG_SERIALISE_NULLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class JsonTransformerTest {

    @Test
    public void testHappyPath_Pass() {
        String schema = """
                {
                   "id": "customerId",
                   "name": "firstName",
                   "address": {
                       "city": "address.location.city"
                   }
                }
                """;
        String source = """
                {
                    "customerId": 1923,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@gmail.com",
                    "address": {
                        "location": {
                            "city": "Dunstable"
                        },
                        "county": "Bedfordshire",
                        "postalCode": "LU6 3BX"
                    }
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{\"id\":1923,\"name\":\"John\",\"address\":{\"city\":\"Dunstable\"}}",result);
    }

    @Test
    public void testLogicalOrExpression_Pass() {
        String schema = """
                {
                   "id": "id2 | customerId",
                   "name": "firstName",
                   "address": {
                       "city": "address.location.city"
                   }
                }
                """;
        String source = """
                {
                    "customerId": 1923,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@gmail.com",
                    "address": {
                        "location": {
                            "city": "Dunstable"
                        },
                        "county": "Bedfordshire",
                        "postalCode": "LU6 3BX"
                    }
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{\"id\":1923,\"name\":\"John\",\"address\":{\"city\":\"Dunstable\"}}",result);
    }

    @Test
    public void testArrayTransformation1_Pass() {
        String schema = """
                {
                   "id": "customerId",
                   "name": "firstName",
                   "OpenDaysEnum": ["$Mon","$Wed","$Fri"],
                   "data": [
                     {
                        "city": "(0)address.city"
                     }
                   ],
                   "address": {
                       "city": "(0)address.city"
                    }
                }
                """;
        String source = """
                {
                    "customerId": 1923,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@gmail.com",
                    "address": [{
                            "city": "Dunstable",
                            "county": "Bedfordshire",
                            "postalCode": "LU6 3BX"
                    }]
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{\"id\":1923,\"name\":\"John\",\"OpenDaysEnum\":[\"Mon\",\"Wed\",\"Fri\"]," +
                "\"data\":[{\"city\":\"Dunstable\"}],\"address\":{\"city\":\"Dunstable\"}}",result);
    }

    @Test
    public void testArrayTransformation2_Pass() {
        String schema = """
                {
                   "openDays": "openEnum",
                   "name": "business"
                }
                """;
        String source = """ 
                {
                    "openEnum": ["Mon","Wed","Fri"],
                    "business": "Jakes Cakes"
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{\"openDays\":[\"Mon\",\"Wed\",\"Fri\"],\"name\":\"Jakes Cakes\"}",result);
    }

    @Test
    public void testArrayTransformation3_Pass() {
        String schema = """
                ["openEnum","business","$Fri"]
                """;
        String source = """ 
                {
                    "openEnum": ["Mon","Wed","Fri"],
                    "business": "Jakes Cakes"
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("[[\"Mon\",\"Wed\",\"Fri\"],\"Jakes Cakes\",\"Fri\"]",result);
    }

    @Test
    public void testEmptyJchema_Pass() {
        String schema = "{}";
        String source = """
                {
                    "customerId": 1923,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@gmail.com",
                    "address": [{
                            "city": "Dunstable",
                            "county": "Bedfordshire",
                            "postalCode": "LU6 3BX"
                    }]
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{}",result);
    }

    @Test
    public void testEmptySchemaArray_Pass() {
        String schema = "[]";
        String source = """
                {
                    "customerId": 1923,
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@gmail.com",
                    "address": [{
                            "city": "Dunstable",
                            "county": "Bedfordshire",
                            "postalCode": "LU6 3BX"
                    }]
                }
                """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("[]",result);
    }

    @Test
    public void testComplexSourceTransformation_Pass() {
        String schema = """
                {
                    "name": "medications.antianginal.name"
                }
                """;
        String source  = """
            {
              "medications":[
                {
                  "aceInhibitors": [
                    {
                      "name": "lisinopril",
                      "strength": "10 mg Tab",
                      "dose": "1 tab",
                      "route": "PO",
                      "sig": "daily",
                      "pillCount": "#90",
                      "refills": "Refill 3"
                    }
                  ],
                  "antianginal": [
                    {
                      "name": "nitroglycerin",
                      "strength": "0.4 mg Sublingual Tab",
                      "dose": "1 tab",
                      "route": "SL",
                      "sig": "q15min PRN",
                      "pillCount": "#30",
                      "refills": "Refill 1"
                    }
                  ]
                }]
            }
            """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{\"name\":\"nitroglycerin\"}",result);
    }

    @Test
    public void testEmptySourceArray_Pass() {
        String schema = """
                {
                   "openDays": "openEnum",
                   "name": "business"
                }
                """;
        String source = "[]";

        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source, FLAG_SERIALISE_NULLS);
        assertEquals("{\"openDays\":null,\"name\":null}",result);
    }

    @Test
    public void testFormattingFlagsArray_Pass() {
        String schema = """
                {
                   "openDays": "openEnum",
                   "name": "business"
                }
                """;
        String source = "[]";

        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        String result = transformer.transform(source);
        assertEquals("{}",result);

        result = transformer.transform(source, FLAG_SERIALISE_NULLS);
        assertEquals("{\"openDays\":null,\"name\":null}",result);

        result = transformer.transform(source, FLAG_PRETTY_FORMAT | FLAG_SERIALISE_NULLS);
        assertEquals("{\n  \"openDays\": null,\n  \"name\": null\n}",result);
    }

    @Test
    public void testComplexSourceMultipleArrayEntries_Fail() {
        String schema = """
            {
                "name": "medications.aceInhibitors.name"
            }
            """;
        String source  = """
            {
              "medications":[
                {
                  "aceInhibitors": [
                    {
                      "name": "lisinopril",
                      "strength": "10 mg Tab",
                      "dose": "1 tab",
                      "route": "PO",
                      "sig": "daily",
                      "pillCount": "#90",
                      "refills": "Refill 3"
                    },
                    {
                    "name": "aspirin"
                    }
                  ],
                  "antianginal": [
                    {
                      "name": "nitroglycerin",
                      "strength": "0.4 mg Sublingual Tab",
                      "dose": "1 tab",
                      "route": "SL",
                      "sig": "q15min PRN",
                      "pillCount": "#30",
                      "refills": "Refill 1"
                    }
                  ]
                }]
            }
            """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        JsonTransformerException e = assertThrows(JsonTransformerException.class,() -> transformer.transform(source));
        log.error(e.getMessage());
        log.error(e.getJson());
    }

    @Test
    public void testComplexSourceTransformationWithReaderWriterObjects_Pass() {
        String schema = """
            {
                "name": "medications.antianginal.name"
            }
            """;
        String source  = """
            {
              "medications":[
                {
                  "aceInhibitors": [
                    {
                      "name": "lisinopril",
                      "strength": "10 mg Tab",
                      "dose": "1 tab",
                      "route": "PO",
                      "sig": "daily",
                      "pillCount": "#90",
                      "refills": "Refill 3"
                    }
                  ],
                  "antianginal": [
                    {
                      "name": "nitroglycerin",
                      "strength": "0.4 mg Sublingual Tab",
                      "dose": "1 tab",
                      "route": "SL",
                      "sig": "q15min PRN",
                      "pillCount": "#30",
                      "refills": "Refill 1"
                    }
                  ]
                }]
            }
            """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        StringReader reader = new StringReader(source);
        StringWriter writer = new StringWriter();
        transformer.transform(reader,writer);
        String result = writer.toString();
        assertEquals("{\"name\":\"nitroglycerin\"}",result);
    }

    @Test
    public void testComplexSourceTransformationWithStreams_Pass() {
        String schema = """
            {
                "name": "medications.antianginal.name"
            }
            """;
        String source  = """
            {
              "medications":[
                {
                  "aceInhibitors": [
                    {
                      "name": "lisinopril",
                      "strength": "10 mg Tab",
                      "dose": "1 tab",
                      "route": "PO",
                      "sig": "daily",
                      "pillCount": "#90",
                      "refills": "Refill 3"
                    }
                  ],
                  "antianginal": [
                    {
                      "name": "nitroglycerin",
                      "strength": "0.4 mg Sublingual Tab",
                      "dose": "1 tab",
                      "route": "SL",
                      "sig": "q15min PRN",
                      "pillCount": "#30",
                      "refills": "Refill 1"
                    }
                  ]
                }]
            }
            """;
        JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
        InputStream input = new ByteArrayInputStream(source.getBytes());
        OutputStream output = new ByteArrayOutputStream();
        transformer.transform(input,output);
        String result = output.toString();
        assertEquals("{\"name\":\"nitroglycerin\"}",result);
    }
}
