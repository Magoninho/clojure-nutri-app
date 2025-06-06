(ns server.db)

(defonce registros (atom {}))
(defonce next-id (atom 0))
;; (defn now [] (new java.util.Date))
(defn now []
  (.format (java.text.SimpleDateFormat. "dd/MM/yyyy") (new java.util.Date)))

(defn hour-now []
  (.format (java.text.SimpleDateFormat. "HH:mm") (new java.util.Date)))

(defn limpar []
  (reset! registros []))

(defn adicionar-registro [nome date hour calorias]
    (let [id (swap! next-id inc)]
        (swap! registros update :registros (fnil conj []) {:id id :date (if (clojure.string/blank? date) (now) date) :hour (if (clojure.string/blank? hour) (hour-now) hour) :nome nome :calorias calorias})))

(defn parse-date [s]
  (let [fmt (java.text.SimpleDateFormat. "dd/MM/yyyy")]
    (.parse fmt s)))

(defn compare-dates [d1 d2]
  (.compareTo (parse-date d1) (parse-date d2)))


(defn get-registros
  ([]
    (get @registros :registros []))
  ([begin end]
  (let [all (get @registros :registros [])]
    (sort-by :date (filter (fn [{:keys [date]}]
              (and (not (clojure.string/blank? date))
                   (<= (compare-dates date end) 0)
                   (>= (compare-dates date begin) 0)))
            all)))))

;; (defn get-saldo-calorico-range [begin end]
;;   (let [all (get @registros :registros [])]
;;     (:keys [calorias] (filter (fn [{:keys [date]}]
;;               (and (not (clojure.string/blank? date))
;;                    (<= (compare-dates date end) 0)
;;                    (>= (compare-dates date begin) 0))
;;             all)))))