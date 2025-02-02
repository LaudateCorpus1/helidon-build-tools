/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates.
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

package io.helidon.build.archetype.engine.v2;

import io.helidon.build.archetype.engine.v2.ast.Block;
import io.helidon.build.archetype.engine.v2.ast.Input;
import io.helidon.build.archetype.engine.v2.ast.Model;
import io.helidon.build.archetype.engine.v2.ast.Node;
import io.helidon.build.archetype.engine.v2.ast.Output;
import io.helidon.build.archetype.engine.v2.ast.Script;
import io.helidon.build.common.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.helidon.build.common.test.utils.TestFiles.targetDir;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test helper.
 */
class TestHelper {

    /**
     * Read the content of a file as a string.
     *
     * @param file file
     * @return string
     * @throws IOException if an IO error occurs
     */
    static String readFile(Path file) throws IOException {
        return Strings.normalizeNewLines(Files.readString(file));
    }

    /**
     * Load a script using the {@code test-classes} directory.
     *
     * @param path path
     * @return script
     */
    static Script load(String path) {
        Path target = targetDir(TestHelper.class);
        Path testResources = target.resolve("test-classes");
        return ScriptLoader.load(testResources.resolve(path));
    }

    /**
     * Load a script using class-loader resource.
     *
     * @param path resource path
     * @return script
     */
    static Script load0(String path) {
        InputStream is = TestHelper.class.getClassLoader().getResourceAsStream(path);
        assertThat(is, is(notNullValue()));
        return ScriptLoader.load0(is);
    }

    /**
     * Walk the given script.
     *
     * @param visitor visitor
     * @param script  script
     */
    static void walk(Node.Visitor<Void> visitor, Script script) {
        Walker.walk(visitor, script, null);
    }

    /**
     * Walk the given script.
     *
     * @param visitor visitor
     * @param script  script
     */
    static void walk(Input.Visitor<Void> visitor, Script script) {
        Walker.walk(new VisitorAdapter<>(visitor, null, null), script, null);
    }

    /**
     * Walk the given script.
     *
     * @param visitor visitor
     * @param script  script
     */
    static void walk(Output.Visitor<Void> visitor, Script script) {
        Walker.walk(new VisitorAdapter<>(null, visitor, null), script, null);
    }

    /**
     * Walk the given script.
     *
     * @param visitor visitor
     * @param script  script
     */
    static void walk(Model.Visitor<Void> visitor, Script script) {
        Walker.walk(new VisitorAdapter<>(null, null, visitor), script, null);
    }

    /**
     * Create a block builder.
     *
     * @param kind     block kind
     * @param children nested children
     * @return block builder
     */
    static Block.Builder block(Block.Kind kind, Block.Builder... children) {
        Block.Builder builder = Block.builder(null, null, kind);
        for (Block.Builder child : children) {
            builder.addChild(child);
        }
        return builder;
    }

    /**
     * Create an output block builder.
     *
     * @param children nested children
     * @return block builder
     */
    static Block.Builder output(Block.Builder... children) {
        return block(Block.Kind.OUTPUT, children);
    }

    /**
     * Create a model block builder.
     *
     * @param children nested children
     * @return block builder
     */
    static Block.Builder model(Block.Builder... children) {
        return block(Block.Kind.MODEL, children);
    }

    /**
     * Create a model map block builder.
     *
     * @param children nested children
     * @return block builder
     */
    static Block.Builder modelMap(Block.Builder... children) {
        return modelBuilder(null, Block.Kind.MAP, 100, children);
    }

    /**
     * Create a model map block builder.
     *
     * @param key      model key
     * @param children nested children
     * @return block builder
     */
    static Block.Builder modelMap(String key, Block.Builder... children) {
        return modelBuilder(key, Block.Kind.MAP, 100, children);
    }

    /**
     * Create a model list block builder.
     *
     * @param children nested children
     * @return block builder
     */
    static Block.Builder modelList(Block.Builder... children) {
        return modelBuilder(null, Block.Kind.LIST, 100, children);
    }

    /**
     * Create a model list block builder.
     *
     * @param key      model key
     * @param children nested children
     * @return block builder
     */
    static Block.Builder modelList(String key, Block.Builder... children) {
        return modelBuilder(key, Block.Kind.LIST, 100, children);
    }

    /**
     * Create a model value block builder.
     *
     * @param value value
     * @return block builder
     */
    static Block.Builder modelValue(String value) {
        return modelBuilder(null, Block.Kind.VALUE, 100).value(value);
    }

    /**
     * Create a model value block builder.
     *
     * @param value value
     * @param order order
     * @return block builder
     */
    static Block.Builder modelValue(String value, int order) {
        return modelBuilder(null, Block.Kind.VALUE, order).value(value);
    }

    /**
     * Create a model value block builder.
     *
     * @param key   key
     * @param value value
     * @return block builder
     */
    static Block.Builder modelValue(String key, String value) {
        return modelBuilder(key, Block.Kind.VALUE, 100).value(value);
    }

    /**
     * Create a model value block builder.
     *
     * @param key   key
     * @param value value
     * @param order order
     * @return block builder
     */
    static Block.Builder modelValue(String key, String value, int order) {
        return modelBuilder(key, Block.Kind.VALUE, order).value(value);
    }

    /**
     * Create an input option block builder.
     *
     * @param name     option name
     * @param value    option value
     * @param children nested children
     * @return block builder
     */
    static Block.Builder inputOption(String name, String value, Block.Builder... children) {
        Block.Builder builder = Input.builder(null, null, Block.Kind.OPTION)
                                     .attributes(inputAttributes(name, value));
        for (Block.Builder child : children) {
            builder.addChild(child);
        }
        return builder;
    }

    /**
     * Create an input text block builder.
     *
     * @param name         option name
     * @param defaultValue default value
     * @return block builder
     */
    static Block.Builder inputText(String name, String defaultValue, Block.Builder... children) {
        return inputBuilder(name, Block.Kind.TEXT, defaultValue, children);
    }

    /**
     * Create an input boolean block builder.
     *
     * @param name         option name
     * @param defaultValue default value
     * @param children     nested children
     * @return block builder
     */
    static Block.Builder inputBoolean(String name, boolean defaultValue, Block.Builder... children) {
        return inputBuilder(name, Block.Kind.BOOLEAN, String.valueOf(defaultValue), children);
    }

    /**
     * Create an input enum block builder.
     *
     * @param name         option name
     * @param defaultValue default value
     * @param children     nested children
     * @return block builder
     */
    static Block.Builder inputEnum(String name, String defaultValue, Block.Builder... children) {
        return inputBuilder(name, Block.Kind.ENUM, defaultValue, children);
    }

    /**
     * Create an input list block builder.
     *
     * @param name         option name
     * @param defaultValue default value
     * @param children     nested children
     * @return block builder
     */
    static Block.Builder inputList(String name, List<String> defaultValue, Block.Builder... children) {
        return inputBuilder(name, Block.Kind.LIST, String.join(",", defaultValue), children);
    }

    private static Block.Builder inputBuilder(String name, Block.Kind kind, String defaultValue, Block.Builder... children) {
        Block.Builder builder = Input.builder(null, null, kind)
                                     .attributes(inputAttributes(name, defaultValue, name));
        for (Block.Builder child : children) {
            builder.addChild(child);
        }
        return builder;
    }

    private static Block.Builder modelBuilder(String key, Block.Kind kind, int order, Block.Builder... children) {
        Block.Builder builder = Model.builder(null, null, kind)
                                     .attributes(Map.of("order", String.valueOf(order)));
        if (key != null) {
            builder.attributes(Map.of("key", key));
        }
        for (Block.Builder child : children) {
            builder.addChild(child);
        }
        return builder;
    }

    private static Map<String, String> inputAttributes(String name, String value) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("value", value);
        return attributes;
    }

    private static Map<String, String> inputAttributes(String name, String defaultValue, String prompt) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("default", defaultValue);
        attributes.put("prompt", prompt);
        return attributes;
    }
}
