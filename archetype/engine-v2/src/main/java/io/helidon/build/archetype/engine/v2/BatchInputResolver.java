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

import io.helidon.build.archetype.engine.v2.ast.Input;
import io.helidon.build.archetype.engine.v2.ast.Input.NamedInput;
import io.helidon.build.archetype.engine.v2.ast.Node.VisitResult;
import io.helidon.build.archetype.engine.v2.ast.Value;

/**
 * Batch input resolver.
 * Only fails if a non-optional input is unresolved, or an optional input cannot be resolved with a default value.
 */
public class BatchInputResolver extends InputResolver {

    @Override
    public VisitResult visitBoolean(Input.Boolean input, Context context) {
        return visit(input, context);
    }

    @Override
    public VisitResult visitText(Input.Text input, Context context) {
        return visit(input, context);
    }

    @Override
    public VisitResult visitEnum(Input.Enum input, Context context) {
        return visit(input, context);
    }

    @Override
    public VisitResult visitList(Input.List input, Context context) {
        return visit(input, context);
    }

    private VisitResult visit(NamedInput input, Context context) {
        VisitResult result = onVisitInput(input, context);
        if (result != null) {
            return result;
        }
        Value defaultValue = defaultValue(input, context);
        if (input.isOptional()) {
            if (defaultValue != null) {
                context.push(input.name(), defaultValue, input.isGlobal());
                if (input instanceof Input.Boolean && !defaultValue.asBoolean()) {
                    return VisitResult.SKIP_SUBTREE;
                }
                return VisitResult.CONTINUE;
            }
        } else if (input instanceof Input.Enum){
            // skip prompting if there is only one option with a default value
            Input.Enum enumInput = (Input.Enum) input;
            int defaultIndex = enumInput.optionIndex(defaultValue.asString());
            if (enumInput.options().size() == 1 && defaultIndex >= 0) {
                context.push(input.name(), defaultValue, input.isGlobal());
                return VisitResult.CONTINUE;
            }
        }
        throw new UnresolvedInputException(context.path(input.name()));
    }
}
