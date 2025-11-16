pluginManagement {
    repositories {
        //  CORREÇÃO: Usar o repositório Google sem o filtro 'content'
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cartao"
include(":app")