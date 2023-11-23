/*
 * Copyright 2017 Google LLC.
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

package com.google.cloud.tools.test.utils;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.Invocation;

public class SpyVerifier {
  private final Object objectToInspect;
  private final Class<?> classToInspectAs;

  public static SpyVerifier newVerifier(Object objectUnderInspection) {
    return new SpyVerifier(objectUnderInspection);
  }

  private SpyVerifier(Object objectUnderInspection) {
    Preconditions.checkArgument(Mockito.mockingDetails(objectUnderInspection).isSpy());
    objectToInspect = objectUnderInspection;
    classToInspectAs =
        Mockito.mockingDetails(objectToInspect).getMockCreationSettings().getTypeToMock();
  }

  public SpyVerifier verifyAllValuesNotNull() throws IllegalAccessException {
    for (Field field : classToInspectAs.getDeclaredFields()) {
      field.setAccessible(true);
      Assert.assertNotNull(field.getName() + " was null.", field.get(objectToInspect));
    }
    return this;
  }

  private static boolean isGetter(Method method) {
    return isPublicWithPrefix(method, "get");
  }

  private static boolean isPublicWithPrefix(Method method, String prefix) {
    return !method.isSynthetic()
        && Modifier.isPublic(method.getModifiers())
        && method.getName().startsWith(prefix);
  }

  public SpyVerifier verifyDeclaredGetters() throws Exception {
    return verifyDeclaredGetters(Collections.<String, Integer>emptyMap());
  }

  /**
   * Verify getters were called once or if in the override map, called 'count' times
   *
   * @param overrides Override default counts in the style ["getterName" : count]
   */
  public SpyVerifier verifyDeclaredGetters(Map<String, Integer> overrides) throws Exception {
    // extract all invocations of getters by inspecting the spy
    List<Method> knownGetters = Arrays.asList(classToInspectAs.getDeclaredMethods());

    Map<Method, Integer> methodInvocationCount = new HashMap<>();
    for (Invocation invocation : Mockito.mockingDetails(objectToInspect).getInvocations()) {
      Method method = invocation.getMethod();
      if (knownGetters.contains(method) && isGetter(method)) {
        if (methodInvocationCount.containsKey(method)) {
          methodInvocationCount.put(method, methodInvocationCount.get(method) + 1);
        } else {
          methodInvocationCount.put(method, 1);
        }
        invocation.markVerified();
      }
    }

    // compare setter invocations against our expectations
    for (Method method : knownGetters) {
      if (isGetter(method)) {
        int invocationCount = methodInvocationCount.getOrDefault(method, 0);
        int expectedInvocationCount = overrides.getOrDefault(method.getName(), 1);
        if (invocationCount != expectedInvocationCount) {
          throw new MockitoAssertionError(
              "Getter invocations for '"
                  + method.getName()
                  + "' expected "
                  + expectedInvocationCount
                  + ", but was "
                  + invocationCount);
        }
      }
    }
    return this;
  }
}
