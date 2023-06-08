object Version {
    const val LOOM_QUILTFLOWER = "1.10.0"
    const val MINECRAFT = "1.20"
    const val YARN = "1.20+build.1"
    const val LOADER = "0.14.21"
    const val FABRIC = "0.83.0+1.20"
    const val MIXIN_EXTRAS = "0.1.1"
    const val MOD_MENU = "7.0.0"
    const val KOTLIN = "1.8.21"
    const val COROUTINES = "1.7.1"
    const val SERIALIZATION = "1.5.1"
    const val KOTLINX_DATETIME = "0.4.0"
    const val FUEL = "2.3.1"
    const val RESULT = "5.3.0"

    private val branch = System.getenv()["GITHUB_REF_NAME"] ?: MINECRAFT
    private val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"
}
