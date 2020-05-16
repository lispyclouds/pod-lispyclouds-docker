# pod-lispyclouds-docker

A [babashka](https://github.com/borkdude/babashka) pod for interacting with docker

## Building prerequisites
- [Graal VM](https://www.graalvm.org/downloads/)
- [Leiningen](https://leiningen.org/)

### Building
Installing GraalVM:
- Download and extract GraalVM CE. Go to the extracted location and navigate to
  the directory where you can find bin, lib, jre and other directories.
- Run `export GRAALVM_HOME=$PWD`.

Clone the repo and from the repo directory:
- Run `$GRAALVM_HOME/bin/gu install native-image` to get the Graal native compiler.
- Run `lein native-image` with leiningen to compile it to a native executable.
- The executable is found in `/target/default+uberjar/` with leiningen.

## Usage
- Fire up a babashka v0.0.92+ REPL with `rlwrap bb`
- Import pods: `(require '[babashka.pods :as pods])`
- Load this pod: `(pods/load-pod ["target/default+uberjar/pod-lispyclouds-docker"])`
- Load the ns: `(require '[pod.lispyclouds.docker :as docker])`
- See the [Usage](https://github.com/into-docker/clj-docker-client#usage) section to try out the commands.
- A subset of commands are supported, more coming soon!
