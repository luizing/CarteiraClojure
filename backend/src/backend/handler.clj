(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [clj-http.client :as http-client]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

;;https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
(def APIKEY "625F6E1PEVJA2EUT") ;;Colocar como .env

(defn urlCreator [symbol]
  (let [baseUrl "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
        symbol (clojure.string/upper-case symbol)
        linkConection "&apikey="
        sufixo ".SAO"
        requestLink (str baseUrl symbol sufixo linkConection APIKEY ) 
        ]
    (println requestLink)
    requestLink
    ))


(defn consultaAcao [symbol]
  (let [url (urlCreator symbol)
        response (http-client/get url {:as :json})

        quote (get-in response [:body (keyword "Global Quote")])

        symb (get quote (keyword "01. symbol"))
        open (get quote (keyword "02. open"))
        high (get quote (keyword "03. high"))
        low (get quote (keyword "04. low"))
        price (get quote (keyword "05. price"))]
    
    (println response)

    (json/generate-string
     {:acao      symb
      :abertura  open
      :alta      high
      :baixa     low
      :preco     price})))

  
(defroutes app-routes
  (GET "/" [] "Hello World")

  ;"Faz a requisição para API externa e busca os dados da ação enviadas via parametros da url ou corpo da requisição ou link"
  (GET "/acao/:symbol" [symbol] (consultaAcao symbol))
  (POST "/compra/acao" [] "Faz o registro de uma nova ação na carteira do usuario - Checa os dados da ação naquele momento")
  (POST "/vende/acao" [] "Faz o registro da venda de uma ação na carteira do usuario - remove a ação da lista de ações")
  (GET "/extrato" [] "Retorna o registro de compras e vendas em determinado período (enviado na requisição)")
  (GET "/saldo" [] {:headers {"Content-Type" "application/json; charset=utf-8"}
                    :body (json/generate-string "Obter saldo da carteira")})
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
