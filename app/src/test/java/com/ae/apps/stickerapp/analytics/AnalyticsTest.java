package com.ae.apps.stickerapp.analytics;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsTest {

    @Mock
    private Context mockContext;

    @Mock
    private Resources mockResources;

    @Mock
    private FirebaseAnalytics mockFirebaseAnalytics;

    @Captor
    private ArgumentCaptor<Bundle> bundleCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;


    @BeforeEach
    void setUp(){
        // Mock the static method FirebaseAnalytics.getInstance() to return our mock instance
    }

    @Test
    public void testGetInstance_returnsNonNullInstance() {
        // When
        Analytics analyticsInstance = Analytics.getInstance(mockContext);

        // Then
        assertNotNull(analyticsInstance, "Instance should not be null");
    }

}