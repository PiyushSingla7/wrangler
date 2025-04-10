/*
 * Copyright © 2016-2019 Cask Data, Inc.
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

 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;

 import io.cdap.wrangler.api.annotations.PublicEvolving;
 
 /**
  * Row defines the schema and data on which the wrangler will operate upon.
  */
 @PublicEvolving
 public final class Row implements Serializable {
     private static final long serialVersionUID = -7505703059736709602L;
 
     /**
      * Name of the columns held by the row.
      */
     private final List<String> columns;
 
     /**
      * Values held by the row.
      */
     private final List<Object> values;
 
     /**
      * Constructs an empty {@link Row}.
      */
     public Row() {
         this.columns = new ArrayList<>();
         this.values = new ArrayList<>();
     }
 
     /**
      * Makes a copy of the row.
      *
      * @param row The row to be copied to 'this' object.
      */
     public Row(final Row row) {
         this.columns = new ArrayList<>(row.columns);
         this.values = new ArrayList<>(row.values);
     }
 
     /**
      * Initializes a row with a list of columns.
      *
      * @param columns The list of columns to set in the row.
      */
     public Row(final List<String> columns) {
         this.columns = new ArrayList<>(columns);
         this.values = new ArrayList<>(columns.size());
     }
 
     /**
      * Initializes the row with a column name and value.
      *
      * @param name The name of the column to be added to the row.
      * @param value The value for the column defined above.
      */
     public Row(final String name, final Object value) {
         this.columns = new ArrayList<>(1);
         this.values = new ArrayList<>(1);
         this.columns.add(name);
         this.values.add(value);
     }
 
     /**
      * Gets the column name by index.
      *
      * @param idx The index to retrieve the name of the column.
      * @return The name of the column.
      */
     public final String getColumn(final int idx) {
         return columns.get(idx);
     }
 
     /**
      * Sets the name of the column at a given index.
      *
      * @param idx The index at which the new name should be set.
      * @param name The name of the column to be set at the specified index.
      */
     public final void setColumn(final int idx, final String name) {
         columns.set(idx, name);
     }
 
     /**
      * Gets the value of the row at the specified index.
      *
      * @param idx The index from where the value should be retrieved.
      * @return The value at the specified index.
      */
     public final Object getValue(final int idx) {
         return values.get(idx);
     }
 
     /**
      * Gets the value based on the column name.
      *
      * @param col The name of the column for which the value is retrieved.
      * @return The value associated with the column, or null if the column is not found.
      */
     public final Object getValue(final String col) {
         if (col != null && !col.isEmpty()) {
             final int idx = find(col);
             if (idx != -1) {
                 return values.get(idx);
             }
         }
         return null;
     }
 
     /**
      * Updates the value of the row at the specified index.
      *
      * @param idx The index at which the value needs to be updated.
      * @param value The value to be updated at the specified index.
      * @return This {@link Row} instance for method chaining.
      */
     public final Row setValue(final int idx, final Object value) {
         values.set(idx, value);
         return this;
     }
 
     /**
      * Adds a value into the row with a name.
      *
      * @param name The name of the value to be added to the row.
      * @param value The value to be added to the row.
      * @return This {@link Row} instance for method chaining.
      */
     public final Row add(final String name, final Object value) {
         columns.add(name);
         values.add(value);
         return this;
     }
 
     /**
      * Removes the column and value at the given index.
      *
      * @param idx The index for which the value and column are removed.
      * @return This {@link Row} instance for method chaining.
      */
     public final Row remove(final int idx) {
         columns.remove(idx);
         values.remove(idx);
         return this;
     }
 
     /**
      * Finds a column index based on the name of the column. The column name is case-insensitive.
      *
      * @param col The column name to be searched within the row.
      * @return -1 if not present, else the index at which the column is found.
      */
     public final int find(final String col) {
         return find(col, 0);
     }
 
     /**
      * Finds a column index based on the name of the column. Starts the search from the specified index.
      * The column name is case-insensitive.
      *
      * @param col The column name to be searched within the row.
      * @param firstIdx The first index to check.
      * @return -1 if not present, else the index at which the column is found.
      */
     public final int find(final String col, final int firstIdx) {
         for (int i = firstIdx, columnsSize = columns.size(); i < columnsSize; i++) {
             final String name = columns.get(i);
             if (col.equalsIgnoreCase(name)) {
                 return i;
             }
         }
         return -1;
     }
 
     /**
      * Returns the width of the row.
      *
      * @return The width of the row.
      */
     @Deprecated
     public final int length() {
         return columns.size();
     }
 
     /**
      * Returns the width of the row.
      *
      * @return The width of the row.
      */
     public final int width() {
         return columns.size();
     }
 
     /**
      * Returns a list of fields of the record.
      *
      * @return A list of pairs containing column names and their corresponding values.
      */
     public final List<Pair<String, Object>> getFields() {
         final List<Pair<String, Object>> v = new ArrayList<>();
         int i = 0;
         for (final String column : columns) {
             v.add(new Pair<>(column, values.get(i)));
             ++i;
         }
         return v;
     }
 
     /**
      * Adds or sets the value.
      *
      * @param name The name of the field to be either set or added to the record.
      * @param value The value to be added.
      */
     public final void addOrSet(final String name, final Object value) {
         final int idx = find(name);
         if (idx != -1) {
             setValue(idx, value);
         } else {
             add(name, value);
         }
     }
 
     /**
      * Adds or sets the value to the beginning.
      *
      * @param index The index at which the column needs to be inserted.
      * @param name The name of the field to be either set or added to the record.
      * @param value The value to be added.
      */
     public final void addOrSetAtIndex(final int index, final String name, final Object value) {
         final int idx = find(name);
         if (idx != -1) {
             setValue(idx, value);
         } else {
             if (index < columns.size() && index < values.size()) {
                 columns.add(index, name);
                 values.add(index, value);
             }
         }
     }
 
     @Override
     public final boolean equals(final Object o) {
         if (this == o) {
             return true;
         }
         if (o == null || getClass() != o.getClass()) {
             return false;
         }
 
         final Row row = (Row) o;
         return Objects.equals(columns, row.columns) && Objects.equals(values, row.values);
     }
 
     @Override
     public final int hashCode() {
         return Objects.hash(columns, values);
     }
 }