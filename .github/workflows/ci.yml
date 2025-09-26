name: Android CI

on:
  push:
    branches: [ main, dev ]
  pull_request:
    branches: [ main, dev ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
          cache: gradle

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3
        with:
          packages: |
            platform-tools
            platforms;android-36
            build-tools;36.0.0

      - name: Gradle wrapper validation
        uses: gradle/wrapper-validation-action@v2

      - name: Gradle info
        run: ./gradlew --version

      - name: Run unit tests
        run: ./gradlew test --stacktrace --no-daemon

      - name: Android Lint (debug)
        run: ./gradlew :app:lintDebug --stacktrace --no-daemon
