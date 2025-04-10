/*
 * Copyright © 2019 Cask Data, Inc.
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

 package io.cdap.wrangler.registry;

 import java.io.Closeable;

 import javax.annotation.Nullable;

 import io.cdap.cdap.api.artifact.ArtifactSummary;
 import io.cdap.wrangler.api.DirectiveLoadException;
 
 /**
  * A directive registry maintains a collection of directives either system provided or
  * user provided.
  *
  * <p>The directive information is stored within the registry. The information
  * includes name, class, usage, and usage definition.</p>
  *
  * <p>To register custom directives, such as {@code AggregateStatsDirective}, ensure that the
  * implementation of this interface (e.g., {@code SystemDirectiveRegistry}) includes the
  * registration logic. For example:</p>
  *
  * <pre>
  *   registry.register("aggregate-stats", AggregateStatsDirective.class);
  * </pre>
  *
  * @see DirectiveInfo
  */
 public interface DirectiveRegistry extends Closeable {
 
     /**
      * List the directives in the specified namespace.
      *
      * @param namespace the namespace to list from.
      * @return directives in the specified namespace.
      */
     Iterable<DirectiveInfo> list(String namespace);
 
     /**
      * Given the name of the directive, returns the information related to the directive.
      *
      * @param namespace the namespace of the directive.
      * @param name the name of the directive to be retrieved from the registry.
      * @return an instance of {@link DirectiveInfo} if found, else null.
      * @throws DirectiveLoadException if there is an issue loading the directive.
      */
     @Nullable
     DirectiveInfo get(String namespace, String name) throws DirectiveLoadException;
 
     /**
      * This method reloads the directives from the artifacts into the registry.
      * Any implementation of this method should provide support for deletes, updates,
      * and additions.
      *
      * @param namespace the namespace to reload directives in.
      * @throws DirectiveLoadException thrown when there are any issues with loading
      * directives into the registry.
      */
     void reload(String namespace) throws DirectiveLoadException;
 
     /**
      * Retrieve the latest Wrangler transform artifact information.
      *
      * @return the latest Wrangler transform artifact summary, or null if not available.
      */
     @Nullable
     ArtifactSummary getLatestWranglerArtifact();
 }