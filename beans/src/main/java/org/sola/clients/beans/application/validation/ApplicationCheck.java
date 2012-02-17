/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.application.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { TYPE, METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ApplicationValidator.class)
@Documented
/**
 * Class level constraint annotation for the {@link ApplicationBean} class
 */
public @interface ApplicationCheck{

    /** Error message in case of constraint violation */
    String message() default "";
    
    /**
     * Array of interfaces, defining evaluation groups
     */
    Class<?>[] groups() default {};

    /**
     * Used by clients of the Bean Validation API to assign custom payload objects to a constraint
     */
    Class<? extends Payload>[] payload() default {};
}