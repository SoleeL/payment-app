package com.soleel.paymentapp.domain.pinpad.pinblockrequest.di

import com.soleel.paymentapp.domain.pinpad.pinblockrequest.interfaces.IPinBlockRequestUseCase
import com.soleel.paymentapp.domain.pinpad.pinblockrequest.PinBlockRequestUseCaseMock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PinBlockRequestUseCaseModule {

    @Binds
    abstract fun bindPinBlockRequestUseCaseModule(
        impl: PinBlockRequestUseCaseMock
    ): IPinBlockRequestUseCase
}