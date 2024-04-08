pluginManagement {
    repositories {
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
        jcenter()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "Capstone24_1"
include(":app")
