(ns client.core
  (:require [clj-http.client :as client]))

(defn ganho []
  )

(defn perda [])

(defn menu []
  (println "Selecione uma opção:\n1. Registrar ganho de peso\n2. Registrar perda de peso\n3. Sair\n")
  
  (def opcao (read))
  (cond
    (== opcao 1) (ganho)
    (== opcao 2) (perda)
    (== opcao 3) (System/exit 0))
  (println "")

  (recur))

(defn -main [& args]
  (println "+-----------+\n| Nutri App |\n+-----------+\n")
  (menu))
