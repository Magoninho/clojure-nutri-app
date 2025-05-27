(ns client.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clj-http.client :as client])
  (:gen-class))

(def cli-options
  [["-h", "--help"]])

(defn -main [& args]
  (let [options (:options (parse-opts args cli-options))] 
    (println options)))
