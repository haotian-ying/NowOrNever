pluginManagement {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://maven.aliyun.com/nexus/content/repositories/google")
        maven(url = "https://maven.aliyun.com/nexus/content/groups/public")
        maven(url = "https://maven.aliyun.com/nexus/content/repositories/jcenter")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://maven.aliyun.com/nexus/content/repositories/google")
        maven(url = "https://maven.aliyun.com/nexus/content/groups/public")
        maven(url = "https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven(url = "https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "Recyclar"
include(":app")
 