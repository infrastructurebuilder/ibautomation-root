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

import org.json.JSONArray;

public interface TFFormatter {

  String specificFormat();

  TFFormatter addComment(String comment);

  TFFormatter addNamedArray(String name, JSONArray array);

  TFFormatter addString(String str);

  TFFormatter addQuotedString(String str);

  TFFormatter incrementIndent();

  TFFormatter decrementIndent();

  int getCurrentIndent();

  TFFormatter resource();

  TFFormatter addHereDoc(String hereDoc);


}
