/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.validation.metadata;

import javax.validation.metadata.ElementDescriptor;

/**
 * Describes a validated method or constructor parameter.
 *
 * @author Gunnar Morling
 */
public interface ParameterDescriptor extends ElementDescriptor {

	/**
	 * Returns this parameter's index within the parameter array of the method
	 * or constructor holding it.
	 *
	 * @return This parameter's index.
	 */
	int getIndex();

	/**
	 * Returns this parameter's name as retrieved by the current parameter name
	 * resolver.
	 *
	 * @return This parameter's name.
	 */
	String getName();

	/**
	 * Whether a cascaded validation of this parameter shall be performed or
	 * not.
	 *
	 * @return <code>true</code>, if this parameter shall be validated
	 *         recursively, <code>false</code> otherwise.
	 */
	boolean isCascaded();
}
