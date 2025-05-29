(ns server.db)

(defonce registros (atom {}))
(defonce next-id (atom 0))

(defn limpar []
  (reset! registros []))

(defn adicionar-alimento [nome quantidade calorias]
    (let [id (swap! next-id inc)]
        (swap! registros assoc id {:id id :nome nome :quantidade quantidade :calorias calorias})
        (println @registros)))

(defn get-registros []
    @registros)