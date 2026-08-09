name: Compilar AIDA

on:
  workflow_dispatch:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Baixar projeto
        uses: actions/checkout@v4

      - name: Configurar Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Configurar Android SDK
        uses: android-actions/setup-android@v3

      - name: Instalar SDK
        run: |
          yes | sdkmanager --licenses || true
          sdkmanager "platforms;android-35" "build-tools;35.0.0"

      - name: Dar permissão ao Gradle
        run: chmod +x gradlew || true

      - name: Compilar APK
        run: |
          if [ -f "./gradlew" ]; then
            ./gradlew assembleDebug
          else
            echo "Gradle wrapper não encontrado."
            exit 1
          fi

      - name: Enviar APK
        uses: actions/upload-artifact@v4
        with:
          name: AIDA-APK
          path: app/build/outputs/apk/debug/app-debug.apk
