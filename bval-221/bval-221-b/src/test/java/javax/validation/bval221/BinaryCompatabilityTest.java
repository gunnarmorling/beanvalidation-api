/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package javax.validation.bval221;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validation;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Gunnar Morling
 */
public class BinaryCompatabilityTest {

	@Test
	public void validatorCompiledAgainstOldApiStillRuns() {
		Set<ConstraintViolation<Foo>> violations = Validation
				.buildDefaultValidatorFactory()
				.getValidator()
				.validate( new Foo() );

		assertEquals( violations.size(), 1 );
		assertEquals( violations.iterator().next().getMessage(), "foo" );
		assertEquals( violations.iterator().next().getPropertyPath().toString(), "bar.customNode" );
	}

	@Test
	public void validatorCompiledAgainstNewApiCanUseInIterable() {
		Set<ConstraintViolation<Bar>> violations = Validation
				.buildDefaultValidatorFactory()
				.getValidator()
				.validate( new Bar() );

		assertEquals( violations.size(), 1 );
		assertEquals( violations.iterator().next().getMessage(), "bar" );
		assertEquals( violations.iterator().next().getPropertyPath().toString(), "bar.customNode[0]" );
	}
	
	private static class Foo {

		@CustomConstraint
		private String bar;
	}
	
	private static class Bar {

		@AnotherCustomConstraint
		private String bar;
	}
}
