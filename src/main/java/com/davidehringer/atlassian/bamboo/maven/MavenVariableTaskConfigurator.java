/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.davidehringer.atlassian.bamboo.maven;

import static com.davidehringer.atlassian.bamboo.maven.TaskConfiguration.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;

/**
 * @author David Ehringer
 */
public class MavenVariableTaskConfigurator extends AbstractTaskConfigurator {

    private static final Log LOG = LogFactory.getLog(MavenVariableTaskConfigurator.class);

    private static final List<String> FIELDS_TO_COPY = Arrays.asList(PROJECT_FILE, EXTRACT_MODE, VARIABLE_TYPE,
            PREFIX_OPTION, PREFIX_OPTION_CUSTOM_VALUE, CUSTOM_VARIABLE_NAME, CUSTOM_ELEMENT, STRIP_SNAPSHOT);

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
            @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        
        // Copy fields manually without helper to avoid dependency injection issues
        for (String field : FIELDS_TO_COPY) {
            String value = params.getString(field);
            if (value != null) {
                config.put(field, value);
            }
        }
        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context) {
        super.populateContextForCreate(context);
        context.put(EXTRACT_MODE, EXTRACT_MODE_GAV);
        context.put(VARIABLE_TYPE, VARIABLE_TYPE_RESULT);
        context.put(PREFIX_OPTION, PREFIX_OPTION_DEFAULT);
        populateContextForAll(context);
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context,
            @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);
        
        // Copy configuration manually without helper
        final Map<String, String> configuration = taskDefinition.getConfiguration();
        for (String field : FIELDS_TO_COPY) {
            context.put(field, configuration.get(field));
        }
        populateContextForAll(context);
    }

    private void populateContextForAll(@NotNull final Map<String, Object> context) {
        Map<String, String> servers = new HashMap<>();
        servers.put(EXTRACT_MODE_CUSTOM, "Specify specific elements");
        servers.put(EXTRACT_MODE_GAV, "Extract GAV values");
        context.put("options", servers);

        Map<String, String> prefixOptions = new HashMap<>();
        prefixOptions.put(PREFIX_OPTION_DEFAULT, "Prefix variables with \"maven.\"");
        prefixOptions.put(PREFIX_OPTION_CUSTOM, "Use a custom prefix");
        context.put("prefixOptions", prefixOptions);

        Map<String, String> variableTypeOptions = new HashMap<>();
        variableTypeOptions.put(VARIABLE_TYPE_JOB, "Job");
        variableTypeOptions.put(VARIABLE_TYPE_RESULT, "Result");
        variableTypeOptions.put(VARIABLE_TYPE_PLAN, "Plan");
        context.put("variableTypeOptions", variableTypeOptions);
    }
    
    // @Override
    // public void populateContextForView(@NotNull final Map<String, Object> context,
    //         @NotNull final TaskDefinition taskDefinition) {
    //     super.populateContextForView(context, taskDefinition);
        
    //     // Copy configuration manually without helper
    //     final Map<String, String> configuration = taskDefinition.getConfiguration();
    //     for (String field : FIELDS_TO_COPY) {
    //         context.put(field, configuration.get(field));
    //     }
    // }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        String gavOrCustom = params.getString(EXTRACT_MODE);
        if (EXTRACT_MODE_CUSTOM.equals(gavOrCustom)) {
            String variableName = params.getString(CUSTOM_VARIABLE_NAME);
            String element = params.getString(CUSTOM_ELEMENT);
            if (StringUtils.isEmpty(variableName)) {
                errorCollection.addError(CUSTOM_VARIABLE_NAME, "A name for the variable is required.");
            }
            if (StringUtils.isEmpty(element)) {
                errorCollection.addError(CUSTOM_ELEMENT, "An element is required");
            }
        }
        if (LOG.isDebugEnabled()) {
            if (errorCollection.hasAnyErrors()) {
                LOG.debug("Submitted configuration has validation errors.");
            }
        }
    }
}
