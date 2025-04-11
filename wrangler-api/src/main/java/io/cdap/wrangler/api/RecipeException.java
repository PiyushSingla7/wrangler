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

/**
 * A {@link io.cdap.wrangler.api.RecipePipeline} specific exception used for
 * communicating issues with the execution of the pipeline.
 */
public final class RecipeException extends Exception {
    /**
     * Constant representing an unknown index value.
     */
    public static final int UNKNOWN_INDEX = -1;

    /**
     * The index of the row in the dataset that caused the error.
     */
    private final int rowIndex;

    /**
     * The index of the directive in the recipe that caused the error.
     */
    private final int directiveIndex;

    /**
     * Constructs a new {@link RecipeException} with the specified message, cause,
     * row index, and directive index.
     *
     * @param message        The detail message describing the exception.
     * @param throwable      The cause of the exception.
     * @param rowIndex       The index of the row in the dataset that caused the error.
     * @param directiveIndex The index of the directive in the recipe that caused the error.
     */
    public RecipeException(
            String message,
            Throwable throwable,
            int rowIndex,
            int directiveIndex
    ) {
        super(message, throwable);
        this.rowIndex = rowIndex;
        this.directiveIndex = directiveIndex;
    }

    /**
     * Constructs a new {@link RecipeException} with the specified message, cause,
     * and directive index. The row index is set to {@code UNKNOWN_INDEX}.
     *
     * @param message        The detail message describing the exception.
     * @param throwable      The cause of the exception.
     * @param directiveIndex The index of the directive in the recipe that caused the error.
     */
    public RecipeException(
            String message,
            Throwable throwable,
            int directiveIndex
    ) {
        this(message, throwable, UNKNOWN_INDEX, directiveIndex);
    }

    /**
     * Constructs a new {@link RecipeException} with the specified message and cause.
     * Both the row index and directive index are set to {@code UNKNOWN_INDEX}.
     *
     * @param message   The detail message describing the exception.
     * @param throwable The cause of the exception.
     */
    public RecipeException(String message, Throwable throwable) {
        this(message, throwable, UNKNOWN_INDEX, UNKNOWN_INDEX);
    }

    /**
     * Returns the index of the row in the dataset that caused the error.
     *
     * @return The index of the row, or {@code UNKNOWN_INDEX} if not applicable.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the index of the directive in the recipe that caused the error.
     *
     * @return The index of the directive, or {@code UNKNOWN_INDEX} if not applicable.
     */
    public int getDirectiveIndex() {
        return directiveIndex;
    }
}
