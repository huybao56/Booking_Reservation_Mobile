pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//        ivy {
//            name = "Node Distributions at $url"
//            url = uri("https://nodejs.org/dist/")
//            patternLayout {
//                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
//            }
//            metadataSources {
//                artifact()
//            }
//            content {
//                includeModule("org.nodejs", "node")
//            }
//        }

        // ✅ THÊM repository cho Node.js
        ivy {
            name = "Node.js"
            setUrl("https://nodejs.org/dist")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("org.nodejs", "node")
            }
        }

        // ✅ THÊM repository cho Yarn
        ivy {
            name = "Yarn"
            setUrl("https://github.com/yarnpkg/yarn/releases/download")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]).[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("com.yarnpkg", "yarn")
            }
        }
    }
}

rootProject.name = "Project_Graduation"
include(":app")
include(":shared")
