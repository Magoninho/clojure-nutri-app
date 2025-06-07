(ns client.core
  (:require [clj-http.client :as http-client]
            [clojure.term.colors :refer :all]
            [cheshire.core :refer :all]))

(defn get-all-registros []
  (let [url "http://localhost:3000/registros"
        headers {"Content-Type" "application/json"}
        ;; body (generate-string {:query texto})
        response (http-client/get url {:headers headers :as :json})]
    (:body response)))

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
        body (generate-string {:query query :date date :hour hour :age age :weight_kg weight :height_cm height})
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
          (if (< (:calorias i) 0)
            (println (:nome i) "| Data:" (:date i) "| Hora:" (:hour i) "| Calorias" (red (:calorias i)))
            (println (:nome i) "| Data:" (:date i) "| Hora:" (:hour i) "| Calorias" (green (:calorias i))))) registros))
    

(defn menu [age weight height]
  (let [registros (get-all-registros)]
    (println "Todos os registros:")
    (print-registros registros)
    (println)
    (println (on-yellow "Saldo calorico total: " (reduce + (map (fn [item] (:calorias item)) registros))))
  ; TODO: cadastrar dados pessoais (altura, peso, idade)
  (println "----------------------------------------------------------------------")
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
          (println (cyan "----------------------------------------------------------------------"))
          (println (blue "Seu extrato de transacoes no periodo entre " begin " e " end " foi"))
          (mapv (fn [i] (println (:nome i) "|" (:date i) "|" (:hour i) "|" (:calorias i))) response))
          (println (cyan "----------------------------------------------------------------------"))
          (println "Aperte Enter para continuar...")
          (read-line))

    (= opcao "4")
      (do 
        (let [_ (println "Digite a data de inicio (DD/MM/AAAA)")
              begin (read-line)
              _ (println "Digite a data final (DD/MM/AAAA)")
              end (read-line)
              response (get-registros-range begin end)
              calories (map (fn [item] (:calorias item)) response)]
          (println (magenta "----------------------------------------------------------------------"))
          (println (red "Seu saldo calórico no periodo de " begin " e " end " foi "))
          (println (reduce + calories)))
          (println (magenta "----------------------------------------------------------------------"))
          (println "Aperte Enter para continuar...")
          (read-line))
    (= opcao "5") (System/exit 0))

  (println)

  (recur age weight height)))

(defn -main [& args]
  (println (on-green "+-----------+\n| Nutri App |\n+-----------+\n"))
  (let [_ (print "Digite sua idade: ")
        _ (flush)
        age (Integer/parseInt (read-line))
        _ (print "Digite seu peso em kg: ")
        _ (flush)
        weight (Integer/parseInt (read-line))
        _ (print "Digite sua altura em cm: ")
        _ (flush)
        height (Integer/parseInt (read-line))]
    (println)
  (menu age weight height)))
