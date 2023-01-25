object Version {
    val branch = System.getenv()["GITHUB_REF_NAME"] ?: "unknown"
    val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"

    const val MINECRAFT = "1.19.3"
    const val YARN = "1.19.3+build.5"
    const val LOADER = "0.14.13"
    const val FABRIC = "0.73.0+1.19.3"
    const val MIXIN_EXTRAS = "0.1.1"
    const val MOD_MENU = "5.0.2"
}
