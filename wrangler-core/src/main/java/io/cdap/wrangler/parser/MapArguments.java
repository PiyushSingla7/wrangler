/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.LazyNumber;
import io.cdap.wrangler.api.TokenGroup;
import io.cdap.wrangler.api.parser.*;

import java.util.*;

public class MapArguments implements Arguments {
    private final Map<String, Token> tokens;
    private final int lineno;
    private final int columnno;
    private final String source;

    public MapArguments(UsageDefinition definition, TokenGroup group) throws DirectiveParseException {
        this.tokens = new HashMap<>();
        this.lineno = group.getSourceInfo().getLineNumber();
        this.columnno = group.getSourceInfo().getColumnNumber();
        this.source = group.getSourceInfo().getSource();

        // Count optional tokens manually
        int optionalCount = 0;
        for (TokenDefinition tokenDef : definition.getTokens()) {
            if (tokenDef.optional()) {
                optionalCount++;
            }
        }

        int required = definition.getTokens().size() - optionalCount;

        if ((required > group.size() - 1) || ((group.size() - 1) > definition.getTokens().size())) {
            throw new DirectiveParseException(
                    "aggregate-stats", // manually specified directive name
                    String.format("Improper usage of directive '%s', usage - '%s'",
                            "aggregate-stats", definition.toString()));
        }

        List<TokenDefinition> specifications = definition.getTokens();
        Iterator<Token> it = group.iterator();
        int pos = 0;
        it.next(); // skip directive name.
        while (it.hasNext()) {
            Token token = it.next();
            while (pos < specifications.size()) {
                TokenDefinition specification = specifications.get(pos);
                if (!specification.optional()) {
                    if (!specification.type().equals(token.type())) {
                        if (specification.type() == TokenType.COLUMN_NAME_LIST && token.type() == TokenType.COLUMN_NAME) {
                            List<String> values = new ArrayList<>();
                            values.add(((ColumnName) token).value());
                            tokens.put(specification.name(), new ColumnNameList(values));
                            pos = pos + 1;
                            break;
                        } else if (specification.type() == TokenType.NUMERIC_LIST && token.type() == TokenType.NUMERIC) {
                            List<LazyNumber> values = new ArrayList<>();
                            values.add(((Numeric) token).value());
                            tokens.put(specification.name(), new NumericList(values));
                            pos = pos + 1;
                            break;
                        } else if (specification.type() == TokenType.BOOLEAN_LIST && token.type() == TokenType.BOOLEAN) {
                            List<Boolean> values = new ArrayList<>();
                            values.add(((Bool) token).value());
                            tokens.put(specification.name(), new BoolList(values));
                            pos = pos + 1;
                            break;
                        } else if (specification.type() == TokenType.TEXT_LIST && token.type() == TokenType.TEXT) {
                            List<String> values = new ArrayList<>();
                            values.add(((Text) token).value());
                            tokens.put(specification.name(), new TextList(values));
                            pos = pos + 1;
                            break;
                        } else {
                            throw new DirectiveParseException(
                                    String.format("Expected argument '%s' to be of type '%s', but it is of type '%s' - %s",
                                            specification.name(), specification.type().name(),
                                            token.type().name(), group.getSourceInfo().toString())
                            );
                        }
                    } else {
                        tokens.put(specification.name(), token);
                        pos = pos + 1;
                        break;
                    }
                } else {
                    pos = pos + 1;
                    if (specification.type().equals(token.type())) {
                        tokens.put(specification.name(), token);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int size() {
        return tokens.size();
    }

    @Override
    public boolean contains(String name) {
        return tokens.containsKey(name);
    }

    @Override
    public <T extends Token> T value(String name) {
        return (T) tokens.get(name);
    }

    @Override
    public TokenType type(String name) {
        return tokens.get(name).type();
    }

    @Override
    public int line() {
        return lineno;
    }

    @Override
    public int column() {
        return columnno;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        JsonObject arguments = new JsonObject();
        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
            arguments.add(entry.getKey(), entry.getValue().toJson());
        }
        object.addProperty("line", lineno);
        object.addProperty("column", columnno);
        object.addProperty("source", source);
        object.add("arguments", arguments);
        return object;
    }
}
 