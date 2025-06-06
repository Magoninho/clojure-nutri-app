(ns client.core
  (:require [clj-http.client :as http-client]
            [cheshire.core :refer :all]))

(defn get-all-registros []
  (let [url "http://localhost:3000/registros"
        headers {"Content-Type" "application/json"}
        ;; body (generate-string {:query texto})
        response (http-client/get url {:headers headers :as :json})]
    (get-in response [:body :registros])))

(defn add-alimento [query date hour]
  (let [url "http://localhost:3000/registros/alimento/add"
        headers {"Content-Type" "application/json"}
        body (generate-string {:query query :date date :hour hour})
        response (http-client/post url {:headers headers :body body :as :json})]
        response))

(defn print-registros [registros]
  (mapv (fn [i] 
    (println "Nome:" (:nome i) "| Data:" (:date i) "| Hora:" (:hour i) "| Calorias" (:calorias i))) registros))

(defn ganho []
  )

(defn perda [])

(defn menu []
  (let [registros (get-all-registros)]
    (println "Tabela:")
    (flush)
    (print-registros registros)

  ; TODO: cadastrar dados pessoais (altura, peso, idade)
  (println)
  (println "Selecione uma opção:\n1. Registrar ganho de peso\n2. Registrar perda de peso\n3. Sair\n")
  (println "1. Registrar consumo de alimento")
  (println "2. Registrar exercicio fisico")
  (println "3. Consultar extrato de transações")
  (println "4. Consultar saldo de calorias")
  (println "5. Sair")
  
  (def opcao (read-line))
  (cond
    (= opcao "1") 
      (do 
        (println "Digite o alimento consumido em linguagem natural (ex: 150g de frango e 2 ovos):")
        (flush)
        (let [alimento (read-line)
              _ (println "Digite a data que voce consumiu (DD/MM/AAAA) ou deixe vazio para data atual:")
              date (read-line)
              _ (println "Digite o horario que voce consumiu (HH:MM) ou deixe vazio para horario atual:")
              hour (read-line)
              response (add-alimento alimento date hour)]
          (if (:success (:body response)) (println "sucesso") (println "erro"))))
    (= opcao "2") (perda)
    (= opcao "3") (System/exit 0))
  (println "")

  (recur)))

(defn -main [& args]
  (println "+-----------+\n| Nutri App |\n+-----------+\n")
  (menu))
