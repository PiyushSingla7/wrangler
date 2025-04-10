/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonObject;
 
 /**
  * Represents a byte size value parsed from a directive argument.
  * This class provides methods to retrieve the value in bytes and its original string representation.
  */
 public final class ByteSize implements Token {
     /**
      * The number of bytes in a kilobyte (KB).
      */
     private static final long KB = 1024L;
 
     /**
      * The number of bytes in a megabyte (MB).
      */
     private static final long MB = KB * 1024L;
 
     /**
      * The number of bytes in a gigabyte (GB).
      */
     private static final long GB = MB * 1024L;
 
     /**
      * The number of bytes in a terabyte (TB).
      */
     private static final long TB = GB * 1024L;
 
     /**
      * The original string representation of the byte size value.
      */
     private final String originalValue;
 
     /**
      * The byte size value converted to bytes.
      */
     private final long bytes;
 
     /**
      * Constructs a new {@code ByteSize} object by parsing the input string.
      *
      * @param value The input string representing the byte size (e.g., "10KB", "1.5MB").
      */
     public ByteSize(final String value) {
         this.originalValue = value;
         this.bytes = parseBytes(value);
     }
 
     /**
      * Parses the input string and converts it to bytes.
      *
      * @param value The input string representing the byte size (e.g., "10KB", "1.5MB").
      * @return The byte size value converted to bytes.
      * @throws IllegalArgumentException If the unit in the input string is unsupported.
      */
     private long parseBytes(final String value) {
         // Parse the numeric part of the input string.
         final double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
         // Extract the unit part of the input string.
         final String unit = value.replaceAll("[0-9.]", "").toUpperCase();
 
         switch (unit) {
             case "B":
                 return (long) number;
             case "KB":
                 return (long) (number * KB);
             case "MB":
                 return (long) (number * MB);
             case "GB":
                 return (long) (number * GB);
             case "TB":
                 return (long) (number * TB);
             default:
                 throw new IllegalArgumentException("Unsupported unit: " + unit);
         }
     }
 
     /**
      * Returns the byte size value in bytes.
      *
      * @return The byte size value in bytes.
      */
     public final long getBytes() {
         return bytes;
     }
 
     /**
      * Returns the original string representation of the byte size value.
      *
      * @return The original string representation of the byte size value.
      */
     @Override
     public final Object value() {
         return originalValue;
     }
 
     /**
      * Returns the type of this token.
      *
      * @return The token type, which is {@link TokenType#BYTE_SIZE}.
      */
     @Override
     public final TokenType type() {
         return TokenType.BYTE_SIZE;
     }
 
     /**
      * Converts this {@code ByteSize} object to a JSON representation.
      *
      * @return A JSON object containing the type, original value, and byte size.
      */
     @Override
     public final JsonObject toJson() {
         final JsonObject json = new JsonObject();
         json.addProperty("type", "BYTE_SIZE");
         json.addProperty("value", originalValue);
         json.addProperty("bytes", bytes);
         return json;
     }
 }