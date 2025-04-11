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

import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.io.Serializable;

/**
 * The <code>TokenDefinition</code> class represents a definition of token as specified
 * by the user while defining a directive usage. All definitions of a token are represented
 * by an instance of this class.
 *
 * <p>The definition is constant (immutable) and cannot be changed once defined.
 * For example:
 * <pre>
 *   TokenDefinition token = new TokenDefinition("column", TokenType.COLUMN_NAME, null, 0, false);
 * </pre>
 *
 * <p>The class <code>TokenDefinition</code> includes methods for retrieving different members,
 * such as the name of the token, type of the token, label associated with the token, whether it's
 * optional or not, and the ordinal number of the token in the <code>TokenGroup</code>.</p>
 *
 * <p>As this class is immutable, the constructor requires all member variables to be present
 * for an instance of this object to be created.</p>
 */
@PublicEvolving
public final class TokenDefinition implements Serializable {
    private static final long serialVersionUID = -1L;

    private final int ordinal;
    private final boolean optional;
    private final String name;
    private final TokenType type;
    private final String label;

    /**
     * Constructs a new <code>TokenDefinition</code>.
     *
     * @param name     The name of the token.
     * @param type     The type of the token.
     * @param label    The label associated with the token (optional).
     * @param ordinal  The ordinal position of the token in the <code>TokenGroup</code>.
     * @param optional True if the token is optional, false otherwise.
     */
    public TokenDefinition(String name, TokenType type, String label, int ordinal, boolean optional) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.ordinal = ordinal;
        this.optional = optional;
    }

    /**
     * Returns the label associated with the token.
     * The label provides a way to override the usage description for this <code>TokenDefinition</code>.
     * If a label is not provided, this method returns null.
     *
     * @return The label associated with the token, or null if none is provided.
     */
    public String label() {
        return label;
    }

    /**
     * Returns the ordinal number of this <code>TokenDefinition</code> within the <code>TokenGroup</code>.
     *
     * @return The ordinal number of the token.
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * Indicates whether this <code>TokenDefinition</code> is optional.
     *
     * @return True if the token is optional, false otherwise.
     */
    public boolean optional() {
        return optional;
    }

    /**
     * Returns the name of this <code>TokenDefinition</code>.
     *
     * @return The name of the token.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the type of this <code>TokenDefinition</code>.
     *
     * @return The type of the token.
     */
    public TokenType type() {
        return type;
    }
}
