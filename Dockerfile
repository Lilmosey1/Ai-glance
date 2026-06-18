# ==============================================================================
# Multi-Stage Build Dockerfile for OmniAI Workbench Android Project
# ==============================================================================
# This Dockerfile provides a clean, containerized, and repeatable build system 
# for the Android application, compiling it down to a production-ready APK.
# ==============================================================================

# Stage 1: Build Workspace with JDK 17 & Gradle 8.5
FROM gradle:8.5-jdk17 AS builder

# Configure Android SDK installation environment
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx2048m"

# Install necessary command-line utilities for Android SDK installation
USER root
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install official SDK Command-line Tools latest package
RUN mkdir -p /opt/android-sdk/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip && \
    unzip -q /tmp/cmdline-tools.zip -d /opt/android-sdk/cmdline-tools && \
    mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest && \
    rm /tmp/cmdline-tools.zip

# Auto-accept all Android SDK licenses
RUN yes | sdkmanager --licenses

# Pre-install essential developer toolsets for SDK builds (Android 34/35)
RUN sdkmanager "platforms;android-35" "build-tools;35.0.0" "platform-tools"

# Establishes clean working space inside the container
WORKDIR /workspace

# Copy files over and adjust ownership to safe build user
COPY . .
RUN chown -R gradle:gradle /workspace

# Step down from root privileges for a secure execution context
USER gradle

# Assemble application binaries safely
RUN gradle assembleDebug

# ==============================================================================
# Stage 2: Final Minimalist Output Container
# ==============================================================================
FROM alpine:latest
WORKDIR /app

# Safely carry compile output forward and expose it cleanly
COPY --from=builder /workspace/app/build/outputs/apk/debug/app-debug.apk /app/app-debug.apk

CMD ["sh", "-c", "echo 'Success! OmniAI Workbench compiled APK has been saved in /app/app-debug.apk'"]
