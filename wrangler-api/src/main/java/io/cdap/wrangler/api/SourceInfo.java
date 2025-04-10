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

 package io.cdap.wrangler.api;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 
 /**
  * The <code>SourceInfo</code> class represents information about the source code location,
  * including the line number, column number, and the source text.
  */
 public final class SourceInfo {
     /**
      * The line number in the source code.
      */
     private final int lineno;
 
     /**
      * The column number in the source code.
      */
     private final int colno;
 
     /**
      * The source text associated with this location.
      */
     private final String source;
 
     /**
      * Constructs a new <code>SourceInfo</code> object.
      *
      * @param lineno The line number in the source code.
      * @param colno The column number in the source code.
      * @param source The source text associated with this location.
      */
     public SourceInfo(final int lineno, final int colno, final String source) {
         this.lineno = lineno;
         this.colno = colno;
         this.source = source;
     }
 
     /**
      * Returns the line number in the source code.
      *
      * @return The line number.
      */
     public final int getLineNumber() {
         return lineno;
     }
 
     /**
      * Returns the column number in the source code.
      *
      * @return The column number.
      */
     public final int getColumnNumber() {
         return colno;
     }
 
     /**
      * Returns the source text associated with this location.
      *
      * @return The source text.
      */
     public final String getSource() {
         return source;
     }
 
     /**
      * Returns a string representation of this <code>SourceInfo</code> object.
      *
      * @return A formatted string containing the line number, column number, and source text.
      */
     @Override
     public final String toString() {
         return String.format("%3d:%-3d - '%s'", lineno, colno, source);
     }
 
     /**
      * Converts this <code>SourceInfo</code> object into a JSON representation.
      *
      * @return A <code>JsonElement</code> object representing this <code>SourceInfo</code>.
      */
     public final JsonElement toJson() {
         final JsonObject object = new JsonObject();
         object.addProperty("line", lineno);
         object.addProperty("column", colno);
         object.addProperty("source", source);
         return object;
     }
 }