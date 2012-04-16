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
package javax.validation.osgi;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tracks registration and deregistration of {@link ValidationProvider}s. For
 * the first provider registered, a {@link ValidatorFactory} will be exported as
 * OSGi service. If the provider of the current factory goes away, the next
 * available other will be used as base for the factory. If the last provider is
 * deregistered, the factory will be shut down as well.
 *
 * @author Gunnar Morling
 */
public class ValidationProviderServiceTracker extends ServiceTracker {

	private final List<ValidationProvider<?>> validationProviders = new ArrayList<ValidationProvider<?>>();

	private ValidationProvider<?> currentProvider;

	private ServiceRegistration factoryRegistration;

	public ValidationProviderServiceTracker(BundleContext bundleContext) {
		super( bundleContext, ValidationProvider.class.getName(), null );
	}

	@Override
	public synchronized Object addingService(ServiceReference reference) {

		ValidationProvider<?> validationProvider = (ValidationProvider<?>) context.getService( reference );
		validationProviders.add( validationProvider );

		if ( factoryRegistration == null ) {
			registerFactory( validationProvider );
		}

		return validationProvider;
	}

	@Override
	public synchronized void removedService(ServiceReference reference, Object service) {

		ValidationProvider<?> validationProvider = (ValidationProvider<?>) service;
		validationProviders.remove( validationProvider );

		if ( validationProvider == currentProvider ) {
			factoryRegistration.unregister();
			factoryRegistration = null;
		}

		if ( !validationProviders.isEmpty() ) {
			registerFactory( validationProviders.get( 0 ) );
		}

		super.removedService( reference, service );
	}

	@SuppressWarnings("unchecked")
	private <T extends Configuration<T>> Class<ValidationProvider<T>> getClass(ValidationProvider<T> validationProvider) {
		return (Class<ValidationProvider<T>>) validationProvider.getClass();
	}

	private void registerFactory(ValidationProvider<?> validationProvider) {

		currentProvider = validationProvider;

		ValidatorFactory validatorFactory = Validation
				.byProvider( getClass( validationProvider ) )
				.providerResolver( new OsgiValidationProviderResolver() )
				.configure()
				.buildValidatorFactory();

		factoryRegistration = context.registerService(
				ValidatorFactory.class.getName(), validatorFactory, null
		);
	}

	private class OsgiValidationProviderResolver implements ValidationProviderResolver {

		public List<ValidationProvider<?>> getValidationProviders() {
			return validationProviders;
		}
	}
}
