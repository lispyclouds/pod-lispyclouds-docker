;   This file is part of pod-lispyclouds-docker.
;
;   pod-lispyclouds-docker is free software: you can redistribute it and/or modify
;   it under the terms of the GNU Lesser General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   pod-lispyclouds-docker is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;   GNU Lesser General Public License for more details.
;
;   You should have received a copy of the GNU Lesser General Public License
;   along with pod-lispyclouds-docker. If not, see <http://www.gnu.org/licenses/>.

(defproject pod-lispyclouds-docker "0.1.0"
  :author "Rahul De <rahul@mailbox.org>"
  :description "A babashka pod for interacting with docker"
  :url "https://github.com/lispyclouds/pod-lispyclouds-docker"
  :license {:name "LGPL 3.0"
            :url  "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [lispyclouds/clj-docker-client "1.0.1"]
                 [nrepl/bencode "1.1.0"]
                 [org.flatland/ordered "1.5.9"]]
  :main pod.lispyclouds.docker
  :target-path "target/%s"
  :plugins [[lein-ancient "0.6.15"]
            [io.taylorwood/lein-native-image "0.3.1"]]
  :native-image {:name "pod-lispyclouds-docker"
                 :opts ["--initialize-at-build-time"
                        "--report-unsupported-elements-at-runtime"
                        "--allow-incomplete-classpath"
                        "--enable-url-protocols=http"
                        "--enable-url-protocols=https"]}
  :global-vars {*warn-on-reflection* true}
  :profiles {:uberjar {:global-vars {*assert* false}
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.spec.skip-macros=true"]
                       :aot :all
                       :main pod.lispyclouds.docker}})
