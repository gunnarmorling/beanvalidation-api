/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package javax.validation;

/**
 * Provide contextual data and operation when applying a given constraint validator.
 *
 * At least one {@code ConstraintViolation} must be defined (either the default one,
 * of if the default {@code ConstraintViolation} is disabled, a custom one).
 *
 * @author Emmanuel Bernard
 */
public interface ConstraintValidatorContext {
	/**
	 * Disable the default {@code ConstraintViolation} object generation (which
	 * is using the message template declared on the constraint).
	 * Useful to set a different violation message or generate a {@code ConstraintViolation}
	 * based on a different property.
	 */
	void disableDefaultConstraintViolation();

	/**
	 * @return the current un-interpolated default message.
	 */
	String getDefaultConstraintMessageTemplate();

	/**
	 * Return an constraint violation builder building an violation report
	 * allowing to optionally associate it to a sub path.
	 * The violation message will be interpolated.
	 * <p/>
	 * To create the {@code ConstraintViolation}, one must call either one of
	 * the #addConstraintViolation() methods available in one of the
	 * interfaces of the fluent API.
	 * If another method is called after #addConstraintViolation() on
	 * {@code ConstraintViolationBuilder} or any of its associated nested interfaces
	 * an {@code IllegalStateException} is raised.
	 * <p/>
	 * If {@code isValid} returns {@code false}, a {@code ConstraintViolation}
	 * object will be built per ConstraintViolation report including the default one (unless
	 * {@link #disableDefaultConstraintViolation()} has been called).
	 * <p/>
	 * {@code ConstraintViolation} objects generated from such a call
	 * contain the same contextual information (root bean, path and so on) unless
	 * the path has been overridden.
	 * <p/>
	 * To create a different {@code ConstraintViolation}, a new constraint violation builder
	 * has to be retrieved from {@code ConstraintValidatorContext}
	 *
	 * Here are a few usage examples:
	 * <pre>
	 * {@code
	 * //assuming the following domain model
	 * public class User {
	 *     public Map<String,Address> getAddresses() { ... }
	 * }
	 *
	 * public Address {
	 *     public String getStreet() { ... }
	 *     public Country getCountry() { ... }
	 * }
	 *
	 * public Country {
	 *     public String getName() { ... }
	 * }
	 *
	 * //From a property-level constraint on User.addresses
	 * //Build a constraint violation on the default path - ie the "addresses" property
	 * context.buildConstraintViolationWithTemplate( "this detail is wrong" )
	 *             .addConstraintViolation();
	 *
	 * //From a class level constraint on Address
	 * //Build a constraint violation on the default path + "street"
	 * //ie the street property of Address
	 * context.buildConstraintViolationWithTemplate( "this detail is wrong" )
	 *             .addNode( "street" )
	 *             .addConstraintViolation();
	 *
	 * //From a property-level constraint on  User.addresses
	 * //Build a constraint violation on the default path + the bean stored
	 * //under the "home" key in the map
	 * context.buildConstraintViolationWithTemplate( "Incorrect home address" )
	 *             .addNode( null )
	 *                 .inIterable().atKey( "home" )
	 *             .addConstraintViolation();
	 *
	 * //From a class level constraint on User
	 * //Build a constraint violation on the default path + addresses["home"].country.name
	 * //ie property "country.name" on the object stored under "home" in the map
	 * context.buildConstraintViolationWithTemplate( "this detail is wrong" )
	 *             .addNode( "addresses" )
	 *             .addNode( "country" )
	 *                 .inIterable().atKey( "home" )
	 *             .addNode( "name" )
	 *             .addConstraintViolation();
	 * }
	 * </pre>
	 *
	 * @param messageTemplate new un-interpolated constraint message.
	 *
	 * @return Returns an constraint violation builder
	 */
	ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate);

	/**
	 * Return an instance of the specified type allowing access to
	 * provider-specific APIs. If the Bean Validation provider
	 * implementation does not support the specified class,
	 * {@code ValidationException} is thrown.
	 *
	 * @param type the class of the object to be returned.
	 *
	 * @return an instance of the specified class
	 *
	 * @throws ValidationException if the provider does not support the call.
	 * @since 1.1
	 */
	<T> T unwrap(Class<T> type);

	/**
	 * {@code ConstraintViolation} builder allowing to optionally associate
	 * the violation report to a sub path.
	 *
	 * To create the {@code ConstraintViolation}, one must call either one of
	 * the #addConstraintViolation() methods available in one of the
	 * interfaces of the fluent API.
	 * If another method is called after #addConstraintViolation() on
	 * {@code ConstraintViolationBuilder} or any of its associated objects
	 * an {@code IllegalStateException} is raised.
	 */
	interface ConstraintViolationBuilder {
		/**
		 * Add a node to the path the {@code ConstraintViolation} will be associated to.
		 *
		 * {@code name} describes a single property. In particular,
		 * dot (.) is not allowed.
		 *
		 * @param name property name
		 *
		 * @return a builder representing node {@code name}
		 *
		 * @deprecated Use {@link #addPropertyNode(String)} instead.
		 */
		@Deprecated
		<T extends NodeBuilderDefinedContext & NodeBuilderCustomizableContext> T addNode(String name);

		NodeBuilderCustomizableContext addPropertyNode(String name);

		NodeBuilderBeanContext addBeanNode();

		NodeBuilderDefinedContext addParameterNode(int parameterIndex);

		/**
		 * Add the new {@code ConstraintViolation} to be generated if the
		 * constraint validator marks the value as invalid.
		 * Methods of this {@code ConstraintViolationBuilder} instance and its nested
		 * objects return {@code IllegalStateException} from now on.
		 *
		 * @return the {@code ConstraintValidatorContext} instance the
		 *         {@code ConstraintViolationBuilder} comes from
		 */
		ConstraintValidatorContext addConstraintViolation();

		/**
		 * Represent a node whose context is known
		 * (ie index, key and isInIterable)
		 */
		interface NodeBuilderDefinedContext {

			/**
			 * Add a node to the path the {@code ConstraintViolation} will be associated to.
			 *
			 * {@code name} describes a single property. In particular,
			 * dot (.) are not allowed.
			 *
			 * @param name property {@code name}
			 *
			 * @return a builder representing this node
			 *
			 * @deprecated Use {@link #addPropertyNode(String)} instead.
			 */
			@Deprecated
			NodeBuilderCustomizableContext addNode(String name);

			NodeBuilderCustomizableContext addPropertyNode(String name);

			NodeBuilderBeanContext addBeanNode();

			/**
			 * Add the new {@code ConstraintViolation} to be generated if the
			 * constraint validator marks the value as invalid.
			 * Methods of the {@code ConstraintViolationBuilder} instance this object
			 * comes from and the constraint violation builder nested
			 * objects return {@code IllegalStateException} after this call.
			 *
			 * @return {@code ConstraintValidatorContext} instance the
			 *         {@code ConstraintViolationBuilder} comes from
			 */
			ConstraintValidatorContext addConstraintViolation();
		}

		/**
		 * Represent a node whose context is
		 * configurable (ie index, key and isInIterable)
		 */
		interface NodeBuilderCustomizableContext {

			/**
			 * Mark the node as being in an {@code Iterable} or a {@code Map}
			 *
			 * @return a builder representing iterable details
			 */
			NodeContextBuilder inIterable();

			/**
			 * Add a node to the path the {@code ConstraintViolation} will be associated to.
			 *
			 * {@code name} describes a single property. In particular,
			 * dot (.) are not allowed.
			 *
			 * @param name property {@code name}
			 *
			 * @return a builder representing this node
			 *
			 * @deprecated Use {@link #addPropertyNode(String)} instead.
			 */
			@Deprecated
			NodeBuilderCustomizableContext addNode(String name);

			NodeBuilderCustomizableContext addPropertyNode(String name);

			NodeBuilderBeanContext addBeanNode();

			/**
			 * Add the new {@code ConstraintViolation} to be generated if the
			 * constraint validator mark the value as invalid.
			 * Methods of the {@code ConstraintViolationBuilder} instance this object
			 * comes from and the constraint violation builder nested
			 * objects return {@code IllegalStateException} after this call.
			 *
			 * @return {@code ConstraintValidatorContext} instance the
			 *         {@code ConstraintViolationBuilder} comes from
			 */
			ConstraintValidatorContext addConstraintViolation();
		}

		/**
		 * Represent refinement choices for a node which is
		 * in an {@code Iterator} or {@code Map}.
		 * If the iterator is an indexed collection or a map,
		 * the index or the key should be set.
		 */
		interface NodeContextBuilder {

			/**
			 * Define the key the object is into the {@code Map}
			 *
			 * @param key map key
			 *
			 * @return a builder representing the current node
			 */
			NodeBuilderDefinedContext atKey(Object key);

			/**
			 * Define the index the object is into the {@code List} or array
			 *
			 * @param index index
			 *
			 * @return a builder representing the current node
			 */
			NodeBuilderDefinedContext atIndex(Integer index);

			/**
			 * Add a node to the path the {@code ConstraintViolation} will be associated to.
			 *
			 * {@code name} describes a single property. In particular,
			 * dot (.) is not allowed.
			 *
			 * @param name property {@code name}
			 *
			 * @return a builder representing this node
			 *
			 * @deprecated Use {@link #addPropertyNode(String)} instead.
			 */
			@Deprecated
			NodeBuilderCustomizableContext addNode(String name);

			NodeBuilderCustomizableContext addPropertyNode(String name);

			NodeBuilderBeanContext addBeanNode();

			/**
			 * Add the new {@code ConstraintViolation} to be generated if the
			 * constraint validator mark the value as invalid.
			 * Methods of the {@code ConstraintViolationBuilder} instance this object
			 * comes from and the constraint violation builder nested
			 * objects return {@code IllegalStateException} after this call.
			 *
			 * @return {@code ConstraintValidatorContext} instance the
			 *         {@code ConstraintViolationBuilder} comes from
			 */
			ConstraintValidatorContext addConstraintViolation();
		}

		interface NodeBuilderBeanContext {

			ConstraintValidatorContext addConstraintViolation();
		}
	}
}
