object Version {
    val branch = System.getenv()["GITHUB_REF_NAME"] ?: "unknown"
    val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"

    const val MINECRAFT = "1.19.4"
    const val YARN = "1.19.4+build.1"
    const val LOADER = "0.14.17"
    const val FABRIC = "0.76.0+1.19.4"
    const val MIXIN_EXTRAS = "0.1.1"
    const val MOD_MENU = "6.1.0-rc.4"
    const val KOTLIN = "1.8.10"
    const val COROUTINES = "1.6.4"
    const val SERIALIZATION = "1.5.0"
    const val KOTLINX_DATETIME = "0.4.0"
    const val FUEL = "2.3.1"
    const val RESULT = "5.3.0"
}
