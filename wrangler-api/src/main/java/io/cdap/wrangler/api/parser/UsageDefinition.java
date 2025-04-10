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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import java.util.ArrayList;
 import java.util.List;
 
 /**
  * The <code>UsageDefinition</code> class defines the structure of tokens used in directives.
  * It provides methods to define tokens with names, types, labels, and optional flags.
  */
 public final class UsageDefinition {
     private final List<TokenDefinition> tokens = new ArrayList<>();
     private int currentOrdinal = 0;
     private int optionalCnt = 0;
 
     public final void define(final String name, final TokenType type, final boolean optional) {
         final TokenDefinition spec = new TokenDefinition(name, type, null, currentOrdinal, optional);
         optionalCnt = optional ? optionalCnt + 1 : optionalCnt;
         currentOrdinal++;
         tokens.add(spec);
     }
 
     public final void define(
         final String name,
         final TokenType type,
         final String label,
         final boolean optional
     ) {
         final TokenDefinition spec = new TokenDefinition(name, type, label, currentOrdinal, optional);
         optionalCnt = optional ? optionalCnt + 1 : optionalCnt;
         currentOrdinal++;
         tokens.add(spec);
     }
 
     public final UsageDefinition build() {
         return this;
     }
 
     public final void addArgument(final String name, final TokenType type, final boolean optional) {
         define(name, type, optional);
     }
 
     public final List<TokenDefinition> getTokens() {
         return tokens;
     }
 
     @Override
     public final String toString() {
         final StringBuilder sb = new StringBuilder();
         for (final TokenDefinition token : tokens) {
             if (token.label() != null) {
                 sb.append(token.label());
             } else {
                 if (token.type().equals(TokenType.DIRECTIVE_NAME)) {
                     sb.append(token.name());
                 } else if (token.type().equals(TokenType.COLUMN_NAME)) {
                     sb.append(":").append(token.name());
                 } else if (token.type().equals(TokenType.COLUMN_NAME_LIST)) {
                     sb.append(":").append(token.name()).append(" [,:").append(token.name()).append("]*");
                 } else if (token.type().equals(TokenType.BOOLEAN)) {
                     sb.append(token.name()).append(" (true/false)");
                 } else if (token.type().equals(TokenType.TEXT)) {
                     sb.append("'").append(token.name()).append("'");
                 }
             }
             sb.append(" ");
         }
         return sb.toString().trim();
     }
 
     /**
      * Static factory method to create a builder with a directive name.
      *
      * @param directive The name of the directive.
      * @return a new Builder instance.
      */
     public static Builder builder(String directive) {
         Builder builder = new Builder();
         builder.define(directive, TokenType.DIRECTIVE_NAME);
         return builder;
     }
 
     /**
      * Builder class for UsageDefinition.
      */
     public static class Builder {
         private final UsageDefinition definition = new UsageDefinition();
 
         public Builder define(String name, TokenType type) {
             definition.define(name, type, false);
             return this;
         }
 
         public Builder define(String name, TokenType type, boolean optional) {
             definition.define(name, type, optional);
             return this;
         }
 
         public Builder define(String name, TokenType type, String label, boolean optional) {
             definition.define(name, type, label, optional);
             return this;
         }
 
         public UsageDefinition build() {
             return definition;
         }
     }
 }
 