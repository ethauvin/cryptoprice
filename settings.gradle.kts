plugins {
  id("com.gradle.enterprise").version("3.15")
}

gradleEnterprise {
    buildScan {
      link("GitHub", "https://github.com/ethauvin/cryptoprice/tree/master")
      if (!System.getenv("CI").isNullOrEmpty()) {
          isUploadInBackground = false
          publishOnFailure()
          tag("CI")
      }
      termsOfServiceUrl = "https://gradle.com/terms-of-service"
      termsOfServiceAgree = "yes"
    }
}

rootProject.name = "cryptoprice"
