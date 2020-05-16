# pod-lispyclouds-docker

A [babashka](https://github.com/borkdude/babashka) pod for interacting with docker. Uses the [clj-docker-client](https://github.com/into-docker/clj-docker-client) to function.

## Building prerequisites
- [Graal VM](https://www.graalvm.org/downloads/)
- [Clojure CLI](https://clojure.org/guides/getting_started)(faster) or [Leiningen](https://leiningen.org/)(better windows support)

### Building
Installing GraalVM:
- Download and extract GraalVM CE. Go to the extracted location and navigate to
  the directory where you can find bin, lib, jre and other directories.
- Run `export GRAALVM_HOME=$PWD`.

Clone the repo and from the repo directory:
- Run `$GRAALVM_HOME/bin/gu install native-image` to get the Graal native compiler.
- Run `clojure -A:native-image` if using Clojure CLI or `lein native-image` with leiningen to compile it to a native executable.
- The executable is found in `target/` if compiled via Clojure CLI or in `target/default+uberjar/` with leiningen.

## Usage
- Fire up a babashka v0.0.92+ REPL with `rlwrap bb`
- Import pods: `(require '[babashka.pods :as pods])`
- Load this pod: `(pods/load-pod ["pod-lispyclouds-docker"])`. Assumes pod-lispyclouds-docker is on the PATH.
- Load the ns: `(require '[pod.lispyclouds.docker :as docker])`
- See the [Usage](https://github.com/into-docker/clj-docker-client#usage) section to try out the commands.
- **Calling invoke with `:as stream` and `:as :socket` are unsupported at the moment.**

### Sample script to pull an image, create a container and fetch its logs
```clojure
(require '[babashka.pods :as pods])

;; Assumes pod-lispyclouds-docker is on the PATH.
(pods/load-pod ["pod-lispyclouds-docker"])

(require '[pod.lispyclouds.docker :as docker])

(def images (docker/client {:category :images
                            :conn     {:uri "unix:///var/run/docker.sock"}}))

(def containers (docker/client {:category :containers
                                :conn     {:uri "unix:///var/run/docker.sock"}}))

;; pull the "busybox:musl" image
(docker/invoke images {:op     :ImageCreate
                       :params {:fromImage "busybox:musl"}})

;; create a container called "conny" from it
(docker/invoke containers {:op     :ContainerCreate
                           :params {:name "conny"
                                    :body {:Image "busybox:musl"
                                           :Cmd   ["echo" "hello"]}}})

(docker/invoke containers {:op     :ContainerStart
                           :params {:id "conny"}})

(def logs (docker/invoke containers {:op     :ContainerLogs
                                     :params {:id     "conny"
                                              :stdout true}}))

(println logs)
```
