import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.includeAndModApi(dep: Any) {
    "modApi"(dep)
    "include"(dep)
}

fun DependencyHandlerScope.includeAndApi(dep: Any) {
    "api"(dep)
    "include"(dep)
}