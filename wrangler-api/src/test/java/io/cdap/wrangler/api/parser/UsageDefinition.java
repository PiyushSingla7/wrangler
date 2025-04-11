/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.cdap.wrangler.api.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the usage of tokens in directives using a builder pattern.
 */
public final class UsageDefinition {
    /**
     * The name of the directive.
     */
    private final String directiveName;

    /**
     * A list of token definitions.
     */
    private final List<TokenDefinition> tokens;

    /**
     * Constructs a new {@link UsageDefinition}.
     *
     * @param directiveName The name of the directive.
     * @param tokens        The list of token definitions.
     */
    private UsageDefinition(String directiveName, List<TokenDefinition> tokens) {
        this.directiveName = directiveName;
        this.tokens = new ArrayList<>(tokens); // Defensive copy to ensure immutability
    }

    /**
     * Returns the name of the directive.
     *
     * @return The name of the directive.
     */
    public String getDirectiveName() {
        return directiveName;
    }

    /**
     * Returns the list of token definitions.
     *
     * @return The list of token definitions.
     */
    public List<TokenDefinition> getTokens() {
        return new ArrayList<>(tokens); // Defensive copy to ensure immutability
    }

    /**
     * Creates a new builder for constructing a {@link UsageDefinition}.
     *
     * @param directiveName The name of the directive.
     * @return A new builder instance.
     */
    public static Builder builder(String directiveName) {
        return new Builder(directiveName);
    }

    /**
     * Converts the {@link UsageDefinition} into a string representation.
     *
     * @return A string representation of the usage definition.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(directiveName);
        for (final TokenDefinition token : tokens) {
            sb.append(" ");
            if (token.label() != null) {
                sb.append(token.label());
            } else {
                switch (token.type()) {
                    case COLUMN_NAME:
                        sb.append(":").append(token.name());
                        break;
                    case COLUMN_NAME_LIST:
                        sb.append(":").append(token.name()).append(" [,:").append(token.name()).append("]*");
                        break;
                    case BOOLEAN:
                        if (token.optional()) {
                            sb.append("[").append(token.name()).append(" (true/false)]");
                        } else {
                            sb.append(token.name()).append(" (true/false)");
                        }
                        break;
                    case TEXT:
                        if (token.optional()) {
                            sb.append("['").append(token.name()).append("']");
                        } else {
                            sb.append("'").append(token.name()).append("'");
                        }
                        break;
                    case EXPRESSION:
                        if (token.optional()) {
                            sb.append("['").append(token.name()).append("']");
                        } else {
                            sb.append("exp:{<").append(token.name()).append(">}");
                        }
                        break;
                    default:
                        sb.append(token.name());
                }
            }
        }
        return sb.toString().trim();
    }

    /**
     * Builder class for constructing {@link UsageDefinition} objects.
     */
    public static final class Builder {
        /**
         * The name of the directive.
         */
        private final String directiveName;

        /**
         * A list of token definitions.
         */
        private final List<TokenDefinition> tokens = new ArrayList<>();

        /**
         * The current ordinal position of the token being defined.
         */
        private int currentOrdinal = 0;

        /**
         * Constructs a new builder for the specified directive.
         *
         * @param directiveName The name of the directive.
         */
        public Builder(String directiveName) {
            this.directiveName = directiveName;
        }

        /**
         * Defines a token with a name and type.
         *
         * @param name The name of the token.
         * @param type The type of the token.
         * @return This builder instance.
         */
        public Builder define(String name, TokenType type) {
            return define(name, type, null, false);
        }

        /**
         * Defines a token with a name, type, and optional flag.
         *
         * @param name     The name of the token.
         * @param type     The type of the token.
         * @param optional {@code true} if the token is optional, otherwise {@code false}.
         * @return This builder instance.
         */
        public Builder define(String name, TokenType type, boolean optional) {
            return define(name, type, null, optional);
        }

        /**
         * Defines a token with a name, type, label, and optional flag.
         *
         * @param name     The name of the token.
         * @param type     The type of the token.
         * @param label    The label that modifies the usage for this field.
         * @param optional {@code true} if the token is optional, otherwise {@code false}.
         * @return This builder instance.
         */
        public Builder define(
                String name,
                TokenType type,
                String label,
                boolean optional
        ) {
            tokens.add(new TokenDefinition(name, type, label, currentOrdinal++, optional));
            return this;
        }

        /**
         * Builds and returns a {@link UsageDefinition} object.
         *
         * @return A new {@link UsageDefinition} instance.
         */
        public UsageDefinition build() {
            return new UsageDefinition(directiveName, tokens);
        }
    }
}

 
