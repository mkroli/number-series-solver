addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.12")

addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.7.0")

resolvers += Resolver.bintrayIvyRepo("mkroli", "sbt-plugins")

addSbtPlugin("com.github.mkroli" % "sbt-i18n" % "0.2")

libraryDependencies += "javax.activation" % "activation" % "1.1.1"
