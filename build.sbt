name := "gradable-server"
version := "1.0"
scalaVersion := "2.11.6"

resolvers ++= Seq(
	"Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
	"anormcypher" at "http://repo.anormcypher.org/",
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
	"Element Releases" at "http://repo.element.hr/nexus/content/repositories/releases/"
)

libraryDependencies ++= {
	val akkaV = "2.4-M1"
	val akkaStreamV = "1.0-RC3"
	val scalaTestV = "2.2.1"

	Seq(
		"com.typesafe.akka"      %% "akka-actor"                        % akkaV,
		"com.typesafe.akka"      %% "akka-testkit"                      % akkaV,
		"com.typesafe.akka"      %% "akka-stream-experimental"          % akkaStreamV,
		"com.typesafe.akka"      %% "akka-http-core-experimental"       % akkaStreamV,
		"com.typesafe.akka"      %% "akka-http-experimental"            % akkaStreamV,
		"com.typesafe.akka"      %% "akka-http-spray-json-experimental" % akkaStreamV,
		"com.typesafe.akka"      %% "akka-http-testkit-experimental"    % akkaStreamV,

		"org.scalatest"          %% "scalatest"                         % scalaTestV % "test",

		"org.anormcypher"        %% "anormcypher"                       % "0.6.0",
		"com.nulab-inc"          %% "scala-oauth2-core"                 % "0.15.0",

		"io.jvm"                 %% "scala-uuid"                        % "0.1.3"
	)
}