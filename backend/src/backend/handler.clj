(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clj-http.client :as http-client]
            ))

;;https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
;;(def APIKEY "625F6E1PEVJA2EUT") ;;Colocar como .env
(def APIKEY "demo")

(defn urlCreator [symbol]
  (let [baseUrl "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
        symbol (clojure.string/upper-case symbol)
        linkConection "&apikey="
        sufixo ""
        requestLink (str baseUrl symbol sufixo linkConection APIKEY ) 
        ] 
    requestLink
    ))

(defn retornaJson [map]
  (json/generate-string map))


(defn consultaAcao [symbol]
  (let [url (urlCreator symbol)
        response (http-client/get url {:as :json})

        quote (get-in response [:body (keyword "Global Quote")])

        symb (get quote (keyword "01. symbol"))
        open (get quote (keyword "02. open"))
        high (get quote (keyword "03. high"))
        low (get quote (keyword "04. low"))
        price (get quote (keyword "05. price"))]

    {:acao      symb
     :abertura  open
     :alta      high
     :baixa     low
     :preco     price}))

(defn compraAcao [symbol qtd] 
  (let [precoUnitario (Double/parseDouble (:preco (consultaAcao symbol)))
        corpo {:acao symbol
               :quantidade qtd
               :preco-unitario precoUnitario
               :total (format "%.2f" (* qtd (double precoUnitario)))
               }
        ]
    (retornaJson corpo)
   )
)

(defn vendeAcao [symbol qtd]
  (let [precoUnitario (Double/parseDouble (:preco (consultaAcao symbol)))
        corpo {:acao symbol
               :quantidade qtd
               :preco-unitario precoUnitario
               :total (format "%.2f" (* qtd (double precoUnitario)))}]
    (retornaJson corpo)))

 
  
(defroutes app-routes
  (GET "/" [] "Hello World")

  (GET "/acao/:symbol" [symbol] (retornaJson (consultaAcao symbol)))

  (POST "/compra" requisicao (let [{:keys [symbol quantidade]} (:body requisicao)]
                               (println "symbol =" symbol "quantidade =" quantidade)
                               {:status 200
                                :body (compraAcao symbol quantidade)}))

  (POST "/vende" requisicao (let [{:keys [symbol quantidade]} (:body requisicao)]
                               (println "symbol =" symbol "quantidade =" quantidade)
                               {:status 200
                                :body (vendeAcao symbol quantidade)}))
  
  (GET "/extrato" [] (compraAcao "ibm" 200))
  (GET "/saldo" [] {:headers {"Content-Type" "application/json; charset=utf-8"}
                    :body (json/generate-string "Obter saldo da carteira")})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      (wrap-defaults api-defaults)))
