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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.javalaboratories.core.tuple.Pair;
import org.javalaboratories.core.tuple.Tuple2;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object that implements this interface has the ability to transform a
 * JSON structure from one form to another.
 * <p>
 * This object is aware of a schema/mapping that will be used to transform
 * the JSON. The mapping, expressed in JSON notation, dictates the final
 * structure of the transformed data, using a simple transformation language
 * (STL).
 * <p>
 * This STL language is expressed in the mappings schema using JSON notation,
 * for example:
 * <pre>
 * {@code
 *          {
 *              "<target1>": "<source1>",
 *              "<target2>": "<a>.<b>.<c>.<source2>"
 *              "parent-node-example": {
 *                  "<target3>": "<source3> | <source4>"
 *              }
 *          }
 * }
 * </pre>
 * In the above example, target attributes are mapped to source attributes;
 * notice the periods are required to express the hierarchical structure of
 * the source attribute within the JSON source. The "|" represents the logical
 * {@code OR} operator, which is used to assign the first non-null source
 * attribute to the target.
 *
 * @see JsonTransformer
 */
public class GoogleJsonTransformer implements JsonTransformer {
    private final String schema;
    private final Pattern arrayRefPattern;

    /**
     * Constructs this {@link GoogleJsonTransformer} with a mappings schema.
     * <p>
     * Use {@link TransformerFactory} to create an instance of this object.
     *
     * @param s mappings schema.
     * @throws NullPointerException if the schema is null.
     */
    GoogleJsonTransformer(final String s) {
        this.schema = Objects.requireNonNull(s);
        this.arrayRefPattern = Pattern.compile("\\([0-9]+\\)");
    }

    /**
     * {@inheritDoc}
     */
    public String schema() {
        return schema;
    }

    /**
     * {@inheritDoc}
     */
    public String transform(final String json, final int flags) throws JsonTransformerException {
        String s = Objects.requireNonNull(json,"JSON parameter cannot be null");
        try {
            Gson gson = new Gson();
            JsonElement schema = gson.fromJson(this.schema,JsonElement.class);
            JsonElement data = gson.fromJson(s,JsonElement.class);
            JsonElement transformed = this.transform(null,schema,data);
            // Finally generate JSON output, applying formatting bit flags
            return this.toJson(transformed,flags);
        } catch (JsonSyntaxException e) {
            throw new JsonTransformerException("Encountered JSON transformation error, relating to JSON syntax",e);
        }
    }

    /**
     * Returns the value for a given {@code path} from the {@code data}.
     * <p>
     * The path can be delimited by a period, indicating a hierarchical nature
     * of the data attribute to be retrieved. For example: the path
     * {@code name.firstName} would yield "James"; whereas {@code name.lastName}
     * would yield "Hogarth". See below for hierarchical data structure in JSON
     * notation.
     * <pre>
     *     {@code
     *          {
     *              "name": {
     *                  "firstName": "James",
     *                  "lastName": "Hogarth"
     *              }
     *          }
     *     }
     * </pre>
     * @param path delimited path of data to be retrieved.
     * @param data the JSON data encapsulated in {@link JsonElement}.
     * @return the data value encapsulated in {@link JsonElement}. If the value
     * cannot be retrieved, then {@code null} is returned.
     * @throws JsonTransformerException When multiple elements in array but no
     * subscript specified
     * @throws NullPointerException Path or data parameter is null.
     */
    protected final JsonElement getValue(final String path, final JsonElement data) {
        String p = Objects.requireNonNull(path,"Path cannot be null");
        JsonElement d = Objects.requireNonNull(data,"Data cannot be null");
        JsonElement result = null;
        String[] k = p.split("\\.");
        if (path.startsWith("$"))  // Is it a literal?
            return new JsonPrimitive(p.substring(1));
        JsonArrayIndex arrayIndex = this.createJsonArrayIndex(k[0]);
        JsonElement je = nextElement(arrayIndex.property(),d);
        if (je != null) {
            if (je.isJsonObject()) {
                result = this.getValue(p.substring(p.indexOf(".") + 1),je);
            } else if (je.isJsonArray()) {
                if (arrayIndex.index() > -1 && arrayIndex.index() < je.getAsJsonArray().size()) // Check index boundary
                    result = this.getValue(p.substring(p.indexOf(".") + 1),je.getAsJsonArray().get(arrayIndex.index()));
                else if (arrayIndex.index() == -1) {
                    if (p.contains("."))
                        if (je.getAsJsonArray().size() == 1)
                            result = this.getValue(p.substring(p.indexOf(".") + 1),je.getAsJsonArray().get(0));
                        else
                            throw new JsonTransformerException("Multiple elements in array, expected 1",je.toString());
                    else
                        result = !je.getAsJsonArray().isEmpty() ? je : null;
                }
            } else {
                result = je;
            }
        }
        return result;
    }

    /**
     * Transforms the {@code data} JSON leveraging {@code schema/mapping} to
     * target structure.
     * <p>
     * Refer to the {@link JsonTransformer} for example of the STL, simple
     * transformation language usage.
     *
     * @param target target attribute to which the transformed attribute value
     *               is assigned, can be {@code null} if unknown.
     * @param schema schema/mappings leveraging STL.
     * @param data source data in JSON notation.
     * @return transformed output in JSON notation, encapsulated in {@link JsonElement}
     * @throws JsonTransformerException When multiple elements in array but no
     * subscript specified
     * @throws NullPointerException Schema or data parameter is null.
     *
     * @see JsonTransformer
     * @see JsonTransformerException
     */
    protected JsonElement transform(final String target, final JsonElement schema, final JsonElement data) {
        JsonElement s = Objects.requireNonNull(schema,"Schema cannot be null");
        JsonElement d = Objects.requireNonNull(data,"Data cannot be null");
        if (s.isJsonObject()) {
            JsonObject jo = new JsonObject();
            for (String t : s.getAsJsonObject().keySet()) {
                JsonElement je = s.getAsJsonObject().get(t);
                jo.add(t,this.transform(t,je,d));
            }
            return jo;
        } else if (s.isJsonArray()) {
            JsonArray ja = new JsonArray();
            for (JsonElement e : s.getAsJsonArray())
                ja.add(this.transform(target,e,d));
            return ja;
        } else {
            // Must be primitive, so evaluate mapping expression.
            String sourcePath = s.getAsString();
            // Logical OR expression used?
            String[] paths = sourcePath.split("\\|");
            JsonElement je = null;
            for (int i = 0; i < paths.length && je == null; i++)
                je = this.getValue(paths[i].trim(),d);
            return je;
        }
    }

    private JsonArrayIndex createJsonArrayIndex(final String property) {
        Matcher m = arrayRefPattern.matcher(property);
        if (m.find()) {
            String g = m.group();
            String k = m.replaceFirst("");
            return new ArrayIndex(new Tuple2<>(Integer.parseInt(g.substring(1,g.length()-1)),k).asPair());
        } else {
            return new ArrayIndex(new Tuple2<>(-1,property).asPair());
        }
    }

    private JsonElement nextElement(final String property, final JsonElement data) {
        JsonElement result = null;
        if (data.isJsonObject())
            result = data.getAsJsonObject().get(property);
        else if (data.isJsonArray()) {
            result = data.getAsJsonArray();
        }
        return result;
    }

    private String toJson(final JsonElement json, final int flags) throws JsonTransformerException {
        Gson gson;
        final byte BIT_MASK = (byte) 0xFF;
        switch(flags & BIT_MASK) {
            case FLAG_PRETTY_FORMAT -> gson = new GsonBuilder().setPrettyPrinting().create();
            case FLAG_SERIALISE_NULLS -> gson = new GsonBuilder().serializeNulls().create();
            case FLAG_PRETTY_FORMAT | FLAG_SERIALISE_NULLS -> gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            default -> gson = new GsonBuilder().create();
        }
        return gson.toJson(json);
    }

    private record ArrayIndex(Pair<Integer,String> pair) implements Pair<Integer,String>, JsonArrayIndex {
        @Override
        public Integer _1() {
            return pair._1();
        }
        @Override
        public String _2() {
            return pair._2();
        }
        @Override
        public Integer index() {
            return _1();
        }
        @Override
        public String property() {
            return _2();
        }
    }
}
