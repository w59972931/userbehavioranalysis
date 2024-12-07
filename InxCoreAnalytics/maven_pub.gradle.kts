import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.*

object RepoConfig {
    const val group = "com.github.w59972931"
    const val version = "1.0.2"
    const val artifactId = "userbehavioranalysis"
}

apply(plugin = "maven-publish")

configure<PublishingExtension> {
    repositories {
        mavenLocal()
    }
}


afterEvaluate {
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("release") { //对应release 版 build variant
                groupId = RepoConfig.group
                artifactId = RepoConfig.artifactId
                version = RepoConfig.version

                from(components["release"])
            }
        }
    }
}
