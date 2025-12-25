package com.seek.app.di

import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.entitlement.FakeEntitlementProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for entitlement provider.
 * Swap FakeEntitlementProvider with real PlayBillingEntitlementProvider
 * when ready for production.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class EntitlementModule {
    
    @Binds
    @Singleton
    abstract fun bindEntitlementProvider(
        impl: FakeEntitlementProvider
    ): EntitlementProvider
}
