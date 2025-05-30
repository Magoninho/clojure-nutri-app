(ns server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as http-client]
            [cheshire.core :refer :all]
            [server.db :refer [adicionar-registro get-registros]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :refer [response status]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(def app-id "b9a1780b")
(def api-key "935d46cf5f9241f745d05ea733e7b67e")

(defn como-json [conteudo & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (generate-string conteudo)})

(defn fetch-alimento [name]
  (let [url "https://trackapi.nutritionix.com/v2/natural/nutrients"
        headers {"x-app-id" app-id
                 "x-app-key" api-key
                 "Content-Type" "application/json"}
        body (generate-string {:query name})
        response (http-client/post url {:headers headers :body body :as :json})]
    (get-in response [:body :foods])))

(defn fetch-exercicio [name age weight height]
  (let [url "https://trackapi.nutritionix.com/v2/natural/exercise"
        headers {"x-app-id" app-id
                 "x-app-key" api-key
                 "Content-Type" "application/json"}
        body (generate-string {:query name
                               :age age
                               :weight_kg weight
                               :height_cm height})
        response (http-client/post url {:headers headers :body body :as :json})]
    (get-in response [:body :exercises])))


(defroutes app-routes
  (GET "/registros" [] (generate-string (get-registros)))
  
  (POST "/registros/alimento/add" request
    (let [query (:query (:body request))
          food (first (fetch-alimento query))
          result (adicionar-registro query (:nf_calories food))]
      (como-json {:success true :registro result})))

  (POST "/registros/exercicio/add" request
    (let [query (:query (:body request))
          age (:age (:body request))
          weight (:weight_kg (:body request))
          height (:height_cm (:body request))
          exercicio (first (fetch-exercicio query age weight height))
          result (adicionar-registro query (- (int (:nf_calories exercicio))))]
      (como-json {:success true :registro result}))))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))

(defn -main [& _args]
  (run-jetty app {:port 3000}))