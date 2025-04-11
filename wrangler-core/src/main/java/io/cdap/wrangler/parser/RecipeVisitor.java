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

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.LazyNumber;
import io.cdap.wrangler.api.RecipeSymbol;
import io.cdap.wrangler.api.SourceInfo;
import io.cdap.wrangler.api.Triplet;
import io.cdap.wrangler.api.parser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class <code>RecipeVisitor</code> implements the visitor pattern
 * used during traversal of the AST tree. The <code>ParseTree#Walker</code>
 * invokes appropriate methods as callbacks with information about the node.
 *
 * <p>In order to understand what's being invoked, please look at the grammar file
 * <tt>Directive.g4</tt>.</p>
 *
 * <p>This class exposes a <code>getTokenGroups</code> method for retrieving the
 * <code>RecipeSymbol</code> after visiting. The <code>RecipeSymbol</code> represents
 * all the <code>TokenGroup</code> for all directives in a recipe. Each directive
 * will create a <code>TokenGroup</code>.</p>
 *
 * <p>As the <code>ParseTree</code> is walking through the call graph, it generates
 * one <code>TokenGroup</code> for each directive in the recipe. Each <code>TokenGroup</code>
 * contains parsed <code>Tokens</code> for that directive along with more information like
 * <code>SourceInfo</code>. A collection of <code>TokenGroup</code> constitutes a <code>RecipeSymbol</code>
 * that is returned by this function.</p>
 */
public final class RecipeVisitor extends DirectivesBaseVisitor<RecipeSymbol.Builder> {
    private final RecipeSymbol.Builder builder = new RecipeSymbol.Builder();

    /**
     * Returns a <code>RecipeSymbol</code> for the recipe being parsed. This
     * object has all the tokens that were successfully parsed along with source
     * information for each directive in the recipe.
     *
     * @return An compiled object after parsing the recipe.
     */
    public RecipeSymbol getCompiledUnit() {
        return builder.build();
    }

    /**
     * A Recipe is made up of Directives and Directives is made up of each individual
     * Directive. This method is invoked on every visit to a new directive in the recipe.
     */
    @Override
    public RecipeSymbol.Builder visitDirective(final DirectivesParser.DirectiveContext ctx) {
        builder.createTokenGroup(getOriginalSource(ctx));
        return super.visitDirective(ctx);
    }

    /**
     * A Directive can include identifiers. This method extracts that token that is being
     * identified as token of type <code>Identifier</code>.
     */
    @Override
    public RecipeSymbol.Builder visitIdentifier(final DirectivesParser.IdentifierContext ctx) {
        builder.addToken(new Identifier(ctx.Identifier().getText()));
        return super.visitIdentifier(ctx);
    }

    /**
     * A Directive can include properties (which are a collection of key and value pairs).
     * This method extracts that token that is being identified as token of type
     * <code>Properties</code>.
     */
    @Override
    public RecipeSymbol.Builder visitPropertyList(final DirectivesParser.PropertyListContext ctx) {
        final Map<String, Token> props = new HashMap<>();
        final List<DirectivesParser.PropertyContext> properties = ctx.property();
        for (final DirectivesParser.PropertyContext property : properties) {
            final String identifier = property.Identifier().getText();
            final Token token;
            if (property.number() != null) {
                token = new Numeric(new LazyNumber(property.number().getText()));
            } else if (property.bool() != null) {
                token = new Bool(Boolean.valueOf(property.bool().getText()));
            } else {
                final String text = property.text().getText();
                token = new Text(text.substring(1, text.length() - 1));
            }
            props.put(identifier, token);
        }
        builder.addToken(new Properties(props));
        return builder;
    }

    /**
     * A Pragma is an instruction to the compiler to dynamically load the directives being specified
     * from the <code>DirectiveRegistry</code>. These do not affect the data flow.
     *
     * <p>E.g. <code>#pragma load-directives test1, test2, test3;</code> will collect the tokens
     * test1, test2 and test3 as dynamically loadable directives. </p>
     */
    @Override
    public RecipeSymbol.Builder visitPragmaLoadDirective(final DirectivesParser.PragmaLoadDirectiveContext ctx) {
        final List<TerminalNode> identifiers = ctx.identifierList().Identifier();
        for (final TerminalNode identifier : identifiers) {
            builder.addLoadableDirective(identifier.getText());
        }
        return builder;
    }

    /**
     * A Pragma version is an informational directive to notify the compiler about the grammar that it should
     * be using to parse the directives below.
     */
    @Override
    public RecipeSymbol.Builder visitPragmaVersion(final DirectivesParser.PragmaVersionContext ctx) {
        builder.addVersion(ctx.Number().getText());
        return builder;
    }

    /**
     * A Directive can include number ranges like start:end=value[,start:end=value]*. This
     * visitor method allows you to collect all the number ranges and create a token type
     * <code>Ranges</code>.
     */
    @Override
    public RecipeSymbol.Builder visitNumberRanges(final DirectivesParser.NumberRangesContext ctx) {
        final List<Triplet<Numeric, Numeric, String>> output = new ArrayList<>();
        final List<DirectivesParser.NumberRangeContext> ranges = ctx.numberRange();
        for (final DirectivesParser.NumberRangeContext range : ranges) {
            final List<TerminalNode> numbers = range.Number();
            final String text = range.value().getText();
            final String trimmedText = text.startsWith("'") && text.endsWith("'")
                    ? text.substring(1, text.length() - 1)
                    : text;
            final Triplet<Numeric, Numeric, String> val =
                    new Triplet<>(new Numeric(new LazyNumber(numbers.get(0).getText())),
                            new Numeric(new LazyNumber(numbers.get(1).getText())),
                            trimmedText);
            output.add(val);
        }
        builder.addToken(new Ranges(output));
        return builder;
    }

    /**
     * This visitor method extracts the custom directive name specified. The custom
     * directives are specified with a bang (!) at the start.
     */
    @Override
    public RecipeSymbol.Builder visitEcommand(final DirectivesParser.EcommandContext ctx) {
        builder.addToken(new DirectiveName(ctx.Identifier().getText()));
        return builder;
    }

    /**
     * A Directive can consist of column specifiers. These are columns that the directive
     * would operate on. When a token of type column is visited, it would generate a token
     * type of type <code>ColumnName</code>.
     */
    @Override
    public RecipeSymbol.Builder visitColumn(final DirectivesParser.ColumnContext ctx) {
        builder.addToken(new ColumnName(ctx.Column().getText().substring(1)));
        return builder;
    }

    /**
     * A Directive can consist of text field. These types of fields are enclosed within
     * a single-quote or a double-quote. This visitor method extracts the string value
     * within the quotes and creates a token type <code>Text</code>.
     */
    @Override
    public RecipeSymbol.Builder visitText(final DirectivesParser.TextContext ctx) {
        final String value = ctx.String().getText();
        builder.addToken(new Text(value.substring(1, value.length() - 1)));
        return builder;
    }

    /**
     * A Directive can consist of numeric field. This visitor method extracts the
     * numeric value <code>Numeric</code>.
     */
    @Override
    public RecipeSymbol.Builder visitNumber(final DirectivesParser.NumberContext ctx) {
        final LazyNumber number = new LazyNumber(ctx.Number().getText());
        builder.addToken(new Numeric(number));
        return builder;
    }

    /**
     * A Directive can consist of Bool field. The Bool field is represented as
     * either true or false. This visitor method extracts the bool value into a
     * token type <code>Bool</code>.
     */
    @Override
    public RecipeSymbol.Builder visitBool(final DirectivesParser.BoolContext ctx) {
        builder.addToken(new Bool(Boolean.valueOf(ctx.Bool().getText())));
        return builder;
    }

    /**
     * A Directive can include an expression or a condition to be evaluated. When
     * such a token type is found, the visitor extracts the expression and generates
     * a token type <code>Expression</code> to be added to the <code>TokenGroup</code>.
     */
    @Override
    public RecipeSymbol.Builder visitCondition(final DirectivesParser.ConditionContext ctx) {
        final int childCount = ctx.getChildCount();
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < childCount - 1; ++i) {
            final ParseTree child = ctx.getChild(i);
            sb.append(child.getText()).append(" ");
        }
        builder.addToken(new Expression(sb.toString()));
        return builder;
    }

    /**
     * A Directive has a name and in the parsing context it's called a command.
     * This visitor method extracts the command and creates a token type <code>DirectiveName</code>.
     */
    @Override
    public RecipeSymbol.Builder visitCommand(final DirectivesParser.CommandContext ctx) {
        builder.addToken(new DirectiveName(ctx.Identifier().getText()));
        return builder;
    }

    /**
     * This visitor method extracts the list of columns specified. It creates a token
     * type <code>ColumnNameList</code> to be added to <code>TokenGroup</code>.
     */
    @Override
    public RecipeSymbol.Builder visitColList(final DirectivesParser.ColListContext ctx) {
        final List<TerminalNode> columns = ctx.Column();
        final List<String> names = new ArrayList<>();
        for (final TerminalNode column : columns) {
            names.add(column.getText().substring(1));
        }
        builder.addToken(new ColumnNameList(names));
        return builder;
    }

    /**
     * This visitor method extracts the list of numerics specified. It creates a token
     * type <code>NumericList</code> to be added to <code>TokenGroup</code>.
     */
    @Override
    public RecipeSymbol.Builder visitNumberList(final DirectivesParser.NumberListContext ctx) {
        final List<TerminalNode> numbers = ctx.Number();
        final List<LazyNumber> numerics = new ArrayList<>();
        for (final TerminalNode number : numbers) {
            numerics.add(new LazyNumber(number.getText()));
        }
        builder.addToken(new NumericList(numerics));
        return builder;
    }

    /**
     * This visitor method extracts the list of booleans specified. It creates a token
     * type <code>BoolList</code> to be added to <code>TokenGroup</code>.
     */
    @Override
    public RecipeSymbol.Builder visitBoolList(final DirectivesParser.BoolListContext ctx) {
        final List<TerminalNode> bools = ctx.Bool();
        final List<Boolean> booleans = new ArrayList<>();
        for (final TerminalNode bool : bools) {
            booleans.add(Boolean.parseBoolean(bool.getText()));
        }
        builder.addToken(new BoolList(booleans));
        return builder;
    }

    /**
     * This visitor method extracts the list of strings specified. It creates a token
     * type <code>StringList</code> to be added to <code>TokenGroup</code>.
     */
    @Override
    public RecipeSymbol.Builder visitStringList(final DirectivesParser.StringListContext ctx) {
        final List<TerminalNode> strings = ctx.String();
        final List<String> strs = new ArrayList<>();
        for (final TerminalNode string : strings) {
            final String text = string.getText();
            strs.add(text.substring(1, text.length() - 1));
        }
        builder.addToken(new TextList(strs));
        return builder;
    }

    /**
     * This visitor method handles byte size arguments (e.g., "10KB", "1.5MB").
     */
    @Override
    public RecipeSymbol.Builder visitByteSizeArg(final DirectivesParser.ByteSizeArgContext ctx) {
        final String value = ctx.getText();
        builder.addToken(new ByteSize(value)); // Create a ByteSize token
        return builder;
    }

    /**
     * This visitor method handles time duration arguments (e.g., "5ms", "30s").
     */
    @Override
    public RecipeSymbol.Builder visitTimeDurationArg(final DirectivesParser.TimeDurationArgContext ctx) {
        final String value = ctx.getText();
        builder.addToken(new TimeDuration(value)); // Create a TimeDuration token
        return builder;
    }

    /**
     * Retrieves the original source information for the given parser rule context.
     *
     * @param ctx The parser rule context.
     * @return The source information.
     */
    private SourceInfo getOriginalSource(final ParserRuleContext ctx) {
        final int startIndex = ctx.getStart().getStartIndex();
        final int stopIndex = ctx.getStop().getStopIndex();
        final Interval interval = new Interval(startIndex, stopIndex);
        final String text = ctx.start.getInputStream().getText(interval);
        final int line = ctx.getStart().getLine();
        final int column = ctx.getStart().getCharPositionInLine();
        return new SourceInfo(line, column, text);
    }
}