/**
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.deployment.tf;

import static java.util.Objects.*;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.JSONArray;

@Named
public class DefaultTFFormatter implements TFFormatter {
  private static final String INDENT = "  ";
  private final StringBuffer sb = new StringBuffer();
  private int currentIndent = 0;
  private final TFObject<?> resource;
  private final TFFormatter specificFormatter;

  @Inject
  public DefaultTFFormatter(TFObject<?> resource, TFFormatterFactory rf) {
    this.resource = requireNonNull(resource);
    this.specificFormatter = requireNonNull(rf).getSpecificFormatterFor(Optional.of(this), this.resource);
  }

  @Override
  public String specificFormat() {

    return sb.toString();
  }

  @Override
  public TFFormatter addQuotedString(final String str) {
    indent().append("\"").append("" + str).append("\"");
    return this;
  }

  @Override
  public TFFormatter addString(final String str) {
    indent().append(str);
    return this;
  }

  @Override
  public TFFormatter addNamedArray(final String name, final JSONArray array) {
    indent();
    addString(name).addString(" = ");
    for (String str : (requireNonNull(array)).toString(2).split("\n")) {
      addString(str);
    }
    return this;
  }

  @Override
  public TFFormatter addComment(final String comment) {
    indent().append("#").append(" " + comment).append("\n");
    return this;
  }

  @Override
  public int getCurrentIndent() {
    return currentIndent;
  }

  @Override
  public TFFormatter incrementIndent() {
    this.currentIndent++;
    return this;
  }

  @Override
  public TFFormatter decrementIndent() {
    this.currentIndent--;
    return this;
  }

  @Override
  public TFFormatter addHereDoc(final String hereDoc) {
    //we might not need someting so unique per-instance, EOF might work but would cause the generated code to be different each time
    String hdStringHeader = "EOF"; // FIXME UUID.randomUUID().toString().replace("-", "_");
    sb.append("<<").append(hdStringHeader).append("\n")
        // add hereDoc unformatted
        .append(hereDoc)
        // Close it
        .append("\n").append(hdStringHeader).append("\n");
    return this;
  }

  private StringBuffer indent() {
    for (int i = 0; i < this.currentIndent; ++i)
      sb.append(INDENT);
    return sb;
  }

  @Override
  public final TFFormatter resource() {
    return addString("resource ")
        // Std format for a resource is "type" "name"
        .addQuotedString(this.resource.getType().toString()).addString(" ").addQuotedString(this.resource.getName())
        // Open the specific parts for this resource
        .addString(" {\n")
        // First increment the indentation
        .incrementIndent()
        // Format this specific resource
        .addString(this.specificFormatter.specificFormat())
        // the decrement the indentation
        .decrementIndent()
        // Close out the resource
        .addString("\n}\n").addComment("### " + this.resource.getName());
  }
}
