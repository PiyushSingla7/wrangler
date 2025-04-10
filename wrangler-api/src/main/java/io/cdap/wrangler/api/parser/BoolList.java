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

 import java.util.List;

 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonPrimitive;

 import io.cdap.wrangler.api.annotations.PublicEvolving;
 
 /**
  * The <code>BoolList</code> class wraps a list of primitive type {@code Boolean} in an object.
  * An object of type <code>BoolList</code> contains the value as a <code>List</code> of primitive type
  * <code>Boolean</code>. Along with the list of <code>Boolean</code> types, this object also contains
  * the value that represents the type of this object as <code>TokenType</code>.
  *
  * <p>In addition, this class provides two methods: one to extract the value held by this wrapper object,
  * and the second for extracting the type of the token.</p>
  *
  * @see Bool
  * @see ColumnName
  * @see ColumnNameList
  * @see DirectiveName
  * @see Numeric
  * @see NumericList
  * @see Properties
  * @see Ranges
  * @see Expression
  * @see Text
  * @see TextList
  */
 @PublicEvolving
 public final class BoolList implements Token {
     /**
      * The <code>List<Boolean></code> object that represents the value held by the token.
      */
     private final List<Boolean> values;
 
     /**
      * Allocates a <code>List<Boolean></code> object representing the <code>values</code> argument.
      *
      * @param values The list of boolean values to be wrapped by this object.
      */
     public BoolList(final List<Boolean> values) {
         this.values = values;
     }
 
     /**
      * Returns the value of this <code>BoolList</code> object as a list of boolean primitives.
      *
      * @return The list of primitive <code>boolean</code> values of this object.
      */
     @Override
     public final List<Boolean> value() {
         return values;
     }
 
     /**
      * Returns the type of this <code>BoolList</code> object as a <code>TokenType</code> enum.
      *
      * @return The enumerated <code>TokenType</code> of this object.
      */
     @Override
     public final TokenType type() {
         return TokenType.BOOLEAN_LIST;
     }
 
     /**
      * Returns the members of this <code>BoolList</code> object as a <code>JsonElement</code>.
      *
      * @return JSON representation of this <code>BoolList</code> object as <code>JsonElement</code>.
      */
     @Override
     public final JsonElement toJson() {
         final JsonObject object = new JsonObject();
         object.addProperty("type", TokenType.BOOLEAN_LIST.name());
 
         final JsonArray array = new JsonArray();
         for (final Boolean value : values) {
             array.add(new JsonPrimitive(value));
         }
         object.add("value", array);
 
         return object;
     }
 }