/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scandit.datacapture.gs1parsersample;

import com.scandit.datacapture.parser.ParsedData;
import com.scandit.datacapture.parser.ParsedField;

import java.util.Map;

/*
 * A helper class to print the parsed data in a human-readable form.
 */
class ParsedDataPrettyPrinter {
    private static final int INDENT_SIZE = 4;

    private final ParsedData parsedData;
    private final StringBuilder resultBuilder;

    public ParsedDataPrettyPrinter(ParsedData parsedData) {
        this.parsedData = parsedData;
        this.resultBuilder = new StringBuilder();
    }

    public String getResult() {
        for (ParsedField field : parsedData.getFields()) {
            appendField(resultBuilder, field);
        }

        return resultBuilder.toString();
    }

    private void appendField(StringBuilder resultBuilder, ParsedField field) {
        appendKeyValue(resultBuilder, 0, field.getName(), field.getParsed());
    }

    private void appendKeyValue(
            StringBuilder resultBuilder,
            int indentLevel,
            String key,
            Object value
    ) {
        appendIndent(resultBuilder, indentLevel);
        appendKey(resultBuilder, key);
        appendValue(resultBuilder, indentLevel, value);
        resultBuilder.append("\n");
    }

    private void appendKey(StringBuilder resultBuilder, String key) {
        resultBuilder
            .append(key)
            .append(": ");
    }

    private void appendValue(StringBuilder resultBuilder, int indentLevel, Object value) {
        if (value == null) {
            resultBuilder.append("null");
        } else if (value instanceof Map<?, ?>) {
            appendMapValue(resultBuilder, indentLevel, (Map<String, Object>) value);
        } else if (value instanceof Object[]) {
            appendArrayValue(resultBuilder, indentLevel, (Object[]) value);
        } else {
            resultBuilder.append(value);
        }
    }

    private void appendMapValue(
            StringBuilder resultBuilder,
            int indentLevel,
            Map<String, Object> value
    ) {
        resultBuilder.append("{\n");

        for (Map.Entry<String, Object> entry : value.entrySet()) {
            appendKeyValue(resultBuilder, indentLevel + INDENT_SIZE, entry.getKey(), entry.getValue());
        }

        appendIndent(resultBuilder, indentLevel);
        resultBuilder.append("}");
    }

    private void appendArrayValue(StringBuilder resultBuilder, int indentLevel, Object[] value) {
        resultBuilder.append("[\n");

        for (int i = 0; i < value.length; ++i) {
            appendIndent(resultBuilder, indentLevel + INDENT_SIZE);
            appendValue(resultBuilder, indentLevel + INDENT_SIZE, value[i]);

            if (i != value.length - 1) {
                resultBuilder.append(",");
            }

            resultBuilder.append("\n");
        }

        appendIndent(resultBuilder, indentLevel);

        resultBuilder.append("]");
    }

    private void appendIndent(StringBuilder resultBuilder, int indentLevel) {
        for (int i = 0; i < indentLevel; ++i) {
            resultBuilder.append(" ");
        }
    }
}
