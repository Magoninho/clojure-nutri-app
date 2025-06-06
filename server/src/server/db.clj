(ns server.db)

(defonce registros (atom {}))
(defonce next-id (atom 0))
;; (defn now [] (new java.util.Date))
(defn now []
  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date)))

(defn hour-now []
  (.format (java.text.SimpleDateFormat. "HH:mm") (new java.util.Date)))

(defn limpar []
  (reset! registros []))

(defn adicionar-registro [nome calorias]
    (let [id (swap! next-id inc)]
        (swap! registros assoc id {:id id :created_at (now) :nome nome :calorias calorias})))

(defn get-registros []
    @registros)