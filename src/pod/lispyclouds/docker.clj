(ns pod.lispyclouds.docker
  (:refer-clojure :exclude [read read-string])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [bencode.core :as bencode]
            [clj-docker-client.core :as docker])
  (:import [java.io PushbackInputStream]
           [java.io EOFException]
           [flatland.ordered.map OrderedMap])
  (:gen-class))

(def debug? false)

(defn debug
  [& args]
  (when debug?
    (binding [*out* (io/writer "debug.log" :append true)]
      (apply println args))))

(def stdin (PushbackInputStream. System/in))

(defn write
  [v]
  (bencode/write-bencode System/out v)
  (.flush System/out))

(defn read-string
  [^"[B" v]
  (String. v))

(defn read
  []
  (bencode/read-bencode stdin))

(defn disorder
  [ordered-map map-fn]
  (walk/postwalk #(if (instance? OrderedMap %)
                    (into map-fn %)
                    %)
                 ordered-map))

(def lookup
  {'pod.lispyclouds.docker/categories docker/categories
   'pod.lispyclouds.docker/client     docker/client
   'pod.lispyclouds.docker/ops        docker/ops
   'pod.lispyclouds.docker/doc        docker/doc
   'pod.lispyclouds.docker/invoke     docker/invoke})

(defn -main
  [& _args]
  (loop []
    (let [message (try
                    (read)
                    (catch EOFException _ ::EOF))]
      (when-not (identical? ::EOF message)
        (let [op (-> message
                     (get "op")
                     read-string
                     keyword)
              id (some-> (get message "id")
                         read-string)
              id (or id "unknown")]
          (case op
            :describe (do
                        (write {"format"     "edn"
                                "namespaces" [{"name" "pod.lispyclouds.docker"
                                               "vars" [{"name" "categories"} {"name" "client"} {"name" "ops"}
                                                       {"name" "doc"} {"name" "invoke"}]}]
                                "id"         id
                                "ops"        {"shutdown" {}}})
                        (recur))
            :invoke   (do
                        (try
                          (let [var  (-> (get message "var")
                                         read-string
                                         symbol)
                                args (get message "args")
                                args (read-string args)
                                args (edn/read-string args)]
                            (if-let [f (lookup var)]
                              (let [result (apply f args)
                                    value  (pr-str (if (map? result)
                                                     (disorder result {})
                                                     result))
                                    reply  {"value"  value
                                            "id"     id
                                            "status" ["done"]}]
                                (write reply))
                              (throw (ex-info (str "Var not found: " var) {}))))
                          (catch Throwable e
                            (binding [*out* *err*]
                              (println e))
                            (let [reply {"ex-message" (ex-message e)
                                         "ex-data"    (pr-str (assoc (ex-data e) :type (class e)))
                                         "id"         id
                                         "status"     ["done" "error"]}]
                              (write reply))))
                        (recur))
            :shutdown (System/exit 0)
            (do
              (let [reply {"ex-message" "Unknown op"
                           "ex-data"    (pr-str {:op op})
                           "id"         id
                           "status"     ["done" "error"]}]
                (write reply))
              (recur))))))))
