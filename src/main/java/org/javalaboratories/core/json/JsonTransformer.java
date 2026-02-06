package org.javalaboratories.core.json;

import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.Objects;

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
 * attribute to the target. The following example below illustrates how to
 * construct a mapping and transform the JSON source:
 * <pre>
 * {@code
 *        String schema = """
 *                 {
 *                    "id": "customerId",
 *                    "name": "firstName",
 *                    "address": {
 *                        "city": "address.location.city"
 *                    }
 *                 }
 *                 """;
 *         String source = """
 *                 {
 *                     "customerId": 1923,
 *                     "firstName": "John",
 *                     "lastName": "Doe",
 *                     "email": "john.doe@gmail.com",
 *                     "address": {
 *                         "location": {
 *                             "city": "Dunstable"
 *                         },
 *                         "county": "Bedfordshire",
 *                         "postalCode": "LU6 3BX"
 *                     }
 *                 }
 *                 """;
 *
 *     JsonTransformer transformer = TransformerFactory.createJsonTransformer(schema);
 *     String result = transformer.transform(source);
 * }
 * </pre>
 * The {@code result} variable contains the following transformed JSON structure:
 * <pre>
 * {@code
 *      String result = """
 *              {
 *                  "id":1923,
 *                  "name":"John",
 *                  "address":{
 *                      "city":"Dunstable"
 *                  }
 *              }
 * }
 * </pre>
 */
@FunctionalInterface
public interface JsonTransformer {
    /**
     * Default flag setting -- no formatting
     */
    byte FLAG_CLEAR = 0x0;

    /**
     * Pretty format of transformed JSON
     */
    byte FLAG_PRETTY_FORMAT = 0x01;

    /**
     * Serialize null attributes of transformed JSON
     */
    byte FLAG_SERIALISE_NULLS = 0x02;

    /**
     * Transforms the JSON source as dictated by the mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     *
     * @param s JSON source to be transformed.
     * @param flags formatting bit flags
     * @return transformed JSON structure.
     * @throws NullPointerException if the parameter s is null.
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    String transform(final String s, final int flags) throws JsonTransformerException;

    /**
     * Transforms the JSON source in the {@link InputStream} as dictated by the
     * mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     * <p>
     * The transformed data is available via the {@link OutputStream} stream.
     *
     * @param in JSON source data
     * @param out JSON output data
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    default void transform(final InputStream in, final OutputStream out) throws JsonTransformerException {
        this.transform(in,out,FLAG_CLEAR);
    }

    /**
     * Transforms the JSON source in the {@link InputStream} as dictated by the
     * mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     * <p>
     * The transformed data is available via the {@link OutputStream} stream.
     *
     * @param in JSON source data
     * @param out JSON output data
     * @param flags formatting bit flags
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    default void transform(final InputStream in, final OutputStream out, int flags) throws JsonTransformerException {
        InputStreamReader r = new InputStreamReader(Objects.requireNonNull(in, "Input stream is null"));
        OutputStreamWriter w = new OutputStreamWriter(Objects.requireNonNull(out,"Output stream is null"));
        this.transform(r,w,flags);
    }

    /**
     * Transforms the JSON source in the {@link Reader} as dictated by the
     * mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     * <p>
     * The transformed data is available via the {@link Writer} object.
     *
     * @param reader JSON source data
     * @param writer JSON output data
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    default void transform(final Reader reader, final Writer writer)  throws JsonTransformerException {
        this.transform(reader, writer, FLAG_CLEAR);
    }

    /**
     * Transforms the JSON source in the {@link Reader} as dictated by the
     * mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     * <p>
     * The transformed data is available via the {@link Writer} object.
     *
     * @param reader JSON source data
     * @param writer JSON output data
     * @param flags formatting bit flags
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    default void transform(final Reader reader, final Writer writer, final int flags) throws JsonTransformerException {
        Reader r = Objects.requireNonNull(reader, "Reader must not be null");
        Writer w = Objects.requireNonNull(writer, "Writer must not be null");
        final int BUFFER_SIZE = 1024;
        char[] buffer = new char[BUFFER_SIZE];
        int read;
        StringBuilder sb = new StringBuilder();

        try (Reader rr = r; Writer ww = w) {
            while ((read = rr.read(buffer)) != -1)
                sb.append(buffer,0,read);
            String t = this.transform(sb.toString(),flags);
            ww.write(t);
        } catch (IOException e) {
            throw new JsonTransformerException("I/O Error occurred during transformation",e);
        }
     }

    /**
     * Transforms the JSON source as dictated by the mappings schema.
     * <p>
     * The schema/mappings are internalised within this {@link JsonTransformer}
     * object.
     *
     * @param s JSON source to be transformed.
     * @return transformed JSON structure.
     * @throws NullPointerException if the parameter s is null.
     * @throws JsonTransformerException when an error is encountered whilst
     * transforming the JSON structure.
     */
    default String transform(final String s)  throws JsonTransformerException {
        return this.transform(s, FLAG_CLEAR);
    }

    /**
     * @return internalised mappings of this object. It is not compulsory to
     * implement this method. It should be clearly documented, if supported.
     */
    default String schema() {
        throw new UnsupportedOperationException();
    }
}
