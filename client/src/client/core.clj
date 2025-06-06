(ns client.core
  (:require [clj-http.client :as http-client]
            [cheshire.core :refer :all]))

(defn get-all-registros []
  (let [url "http://localhost:3000/registros"
        headers {"Content-Type" "application/json"}
        ;; body (generate-string {:query texto})
        response (http-client/get url {:headers headers :as :json})]
    (get-in response [:body :registros])))

(defn add-registro 
  ([type query date hour]
  (let [url (str "http://localhost:3000/registros/" type "/add")
        headers {"Content-Type" "application/json"}
        body (generate-string {:query query :date date :hour hour})
        response (http-client/post url {:headers headers :body body :as :json})]
        response))
  ([type query date hour age weight height]
  (let [url (str "http://localhost:3000/registros/" type "/add")
        headers {"Content-Type" "application/json"}
        body (generate-string {:query query :date date :hour hour :age age :weight weight :height height})
        response (http-client/post url {:headers headers :body body :as :json})]
        response)))

(defn get-registros-range [begin end]
  (let [url "http://localhost:3000/registros"
        headers {"Content-Type" "application/json"}
        params {:begin begin :end end}
        response (http-client/get url {:headers headers :query-params params :as :json})]
    (:body response)))

(defn print-registros [registros]
  (mapv (fn [i] 
    (println "Nome:" (:nome i) "| Data:" (:date i) "| Hora:" (:hour i) "| Calorias" (:calorias i))) registros))

(defn ganho []
  )

(defn perda [])

(def age 20)
(def weight 76)
(def height 175)


(defn menu [age weight height]
  (let [registros (get-all-registros)]
    (println "Tabela:")
    (flush)
    (print-registros registros)
  ; TODO: cadastrar dados pessoais (altura, peso, idade)
  (println)
  (println "1. Registrar consumo de alimento")
  (println "2. Registrar exercicio fisico")
  (println "3. Consultar extrato de transações por período")
  (println "4. Consultar saldo de calorias por período")
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
              response (add-registro "alimento" alimento date hour)]
          (if (:success (:body response)) (println "sucesso") (println "erro"))))
    (= opcao "2")
      (do 
        (println "Digite o exercicio em linguagem natural (ex: 1 hora de natação):")
        (flush)
        (let [exercicio (read-line)
              _ (println "Digite a data que voce se exercitou (DD/MM/AAAA) ou deixe vazio para data atual:")
              date (read-line)
              _ (println "Digite o horario que voce se exercitou (HH:MM) ou deixe vazio para horario atual:")
              hour (read-line)
              response (add-registro "exercicio" exercicio date hour age weight height)]
          (if (:success (:body response)) (println "sucesso") (println "erro"))))
    
    (= opcao "3")
      (do 
        (let [_ (println "Digite a data de inicio (DD/MM/AAAA)")
              begin (read-line)
              _ (println "Digite a data final (DD/MM/AAAA)")
              end (read-line)
              response (get-registros-range begin end)]
          (mapv (fn [i] (println (:nome i) "|" (:date i) "|" (:hour i) "|" (:calorias i))) response))))

  (println)

  (recur age weight height)))

(defn -main [& args]
  (println "+-----------+\n| Nutri App |\n+-----------+\n")
  (menu age weight height))
