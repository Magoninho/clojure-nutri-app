(ns server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as http-client]
            [cheshire.core :refer :all]
            [server.db :refer [adicionar-alimento]]
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

;; (defn fetch-alimento [name]
;;   (let [url (str "https://trackapi.nutritionix.com/v2/search/instant?detailed=true&query=" name)
;;         alimento (http-client/get url {:headers 
;;                                           {"x-app-id" app-id
;;                                            "x-app-key" api-key
;;                                            "Content-Type" "application/json"}})]
;;     alimento))

(defroutes app-routes
  (GET "/" [] (adicionar-alimento "teste" 1 12))
  
  (POST "/post-teste" request
    (let [query (:query (:body request))
          response (fetch-alimento query)]
      (println (:nf_calories (first response)))))


  (GET "/teste" [] 
    (let [url "https://trackapi.nutritionix.com/v2/search/instant?detailed=true&query=egg"
          alimento (http-client/get url {:headers 
                                          {"x-app-id" app-id
                                           "x-app-key" api-key
                                           "Content-Type" "application/json"}})]
      alimento))
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))

(defn -main [& _args]
  (run-jetty app {:port 3000}))