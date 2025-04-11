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

import io.cdap.wrangler.api.parser.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>TokenGroup</code> class represents a collection of tokens generated from parsing a single directive.
 * It also contains information about the source code location where the directive was parsed.
 */
public final class TokenGroup {
    /**
     * Information about the source code location where the directive was parsed.
     */
    private final SourceInfo info;

    /**
     * A list of tokens representing the parsed directive.
     */
    private final List<Token> tokens;

    /**
     * Constructs an empty <code>TokenGroup</code> with no source information.
     */
    public TokenGroup() {
        this.info = null;
        this.tokens = new ArrayList<>();
    }

    /**
     * Constructs a <code>TokenGroup</code> with the specified source information.
     *
     * @param info The source information for the parsed directive.
     */
    public TokenGroup(final SourceInfo info) {
        this.info = info;
        this.tokens = new ArrayList<>();
    }

    /**
     * Adds a token to the group.
     *
     * @param token The token to be added to the group.
     */
    public final void add(final Token token) {
        tokens.add(token);
    }

    /**
     * Returns the number of tokens in the group.
     *
     * @return The size of the token group.
     */
    public final int size() {
        return tokens.size();
    }

    /**
     * Retrieves a token at the specified index.
     *
     * @param i The index of the token to retrieve.
     * @return The token at the specified index.
     */
    public final Token get(final int i) {
        return tokens.get(i);
    }

    /**
     * Returns an iterator over the tokens in the group.
     *
     * @return An iterator over the tokens.
     */
    public final Iterator<Token> iterator() {
        return tokens.iterator();
    }

    /**
     * Returns the source information associated with this token group.
     *
     * @return The source information, or null if none is available.
     */
    public final SourceInfo getSourceInfo() {
        return info;
    }
}
