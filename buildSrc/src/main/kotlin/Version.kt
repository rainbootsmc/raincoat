object Version {
    val branch = System.getenv()["GITHUB_REF_NAME"] ?: "unknown"
    val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
    val project = "$branch+build.$buildNumber"

    const val MINECRAFT = "1.19.2"
    const val FABRIC = "0.69.0+1.19.2"
}
