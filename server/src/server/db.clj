(ns server.db)

(defonce registros (atom {}))
(defonce next-id (atom 0))

(defn limpar []
  (reset! registros []))

(defn adicionar-registro [nome calorias]
    (let [id (swap! next-id inc)]
        (swap! registros assoc id {:id id :nome nome :calorias calorias})))

(defn get-registros []
    @registros)