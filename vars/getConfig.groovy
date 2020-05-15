#!/usr/bin/env groovy

def call() {
    def configContent = libraryResource 'config.yaml'
    def config = readYaml text: configContent
    return config
}
