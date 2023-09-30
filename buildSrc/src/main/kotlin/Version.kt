object Version {
    const val LOOM_VINEFLOWER = "1.11.0"
    const val MINECRAFT = "1.20.1"
    const val YARN = "1.20.1+build.10"
    const val LOADER = "0.14.22"
    const val FABRIC = "0.89.0+1.20.1"
    const val MIXIN_EXTRAS = "0.1.1"
    const val MOD_MENU = "7.2.2"
    const val KOTLIN = "1.9.10"
    const val COROUTINES = "1.7.3"
    const val SERIALIZATION = "1.6.0"
    const val KOTLINX_DATETIME = "0.4.1"
    const val FUEL = "2.3.1"
    const val RESULT = "5.4.0"

    private val branch = System.getenv()["GITHUB_REF_NAME"] ?: MINECRAFT
    private val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"
}
