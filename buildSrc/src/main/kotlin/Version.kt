object Version {
    const val LOOM_QUILTFLOWER = "1.10.0"
    const val MINECRAFT = "1.20.1"
    const val YARN = "1.20.1+build.2"
    const val LOADER = "0.14.22"
    const val FABRIC = "0.87.0+1.20.1"
    const val MIXIN_EXTRAS = "0.1.1"
    const val MOD_MENU = "7.0.0"
    const val KOTLIN = "1.8.22"
    const val COROUTINES = "1.7.1"
    const val SERIALIZATION = "1.5.1"
    const val KOTLINX_DATETIME = "0.4.0"
    const val FUEL = "2.3.1"
    const val RESULT = "5.3.0"

    private val branch = System.getenv()["GITHUB_REF_NAME"] ?: MINECRAFT
    private val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"
}
