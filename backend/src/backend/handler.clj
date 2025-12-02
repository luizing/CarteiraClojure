(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clj-http.client :as http-client]
            ))


(defn now []
  (new java.util.Date))
;;Banco de dados

(def bd (atom ()))

;;add data
(defn addCompra [compra]
  (let [acao (:acao compra)
        quantidade (:quantidade compra)
        preco (:preco-unitario compra)]
    (swap! bd conj {:data (now)
                    :acao acao
                    :quantidade quantidade
                    :preco preco})))









;;https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
;;(def APIKEY "625F6E1PEVJA2EUT") ;;Colocar como .env
(def APIKEY "demo")

(defn urlCreator [symbol]
  (let [baseUrl "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
        symbol (clojure.string/upper-case symbol)
        linkConection "&apikey="
        ;;sufixo ".SAO"
        sufixo ""
        requestLink (str baseUrl symbol sufixo linkConection APIKEY)]
    requestLink))

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
               :total (format "%.2f" (* qtd (double precoUnitario)))}]
    (addCompra corpo)
    (retornaJson corpo)))

(defn vendeAcao [symbol qtd]
  (let [precoUnitario (Double/parseDouble (:preco (consultaAcao symbol)))
        corpo {:acao symbol
               :quantidade qtd
               :preco-unitario precoUnitario
               :total (format "%.2f" (* qtd (double precoUnitario)))}]
    (retornaJson corpo)))

(defn consultaExtrato []
  (println @bd))

  (defroutes app-routes
    (GET "/" [] "Carteira de Ações - Programação Funcional")

    (GET "/acao/:symbol" [symbol] (retornaJson (consultaAcao symbol)))

    (POST "/compra" requisicao (let [{:keys [symbol quantidade]} (:body requisicao)]
                                 (println "Compra => " symbol " " quantidade)
                                 {:status 200
                                  :body (compraAcao symbol quantidade)}))

    (POST "/vende" requisicao (let [{:keys [symbol quantidade]} (:body requisicao)]
                                (println "Venda => " symbol " " quantidade)
                                {:status 200
                                 :body (vendeAcao symbol quantidade)}))

    (POST "/extrato" requisicao (let [{:keys [inicio fim]} (:body requisicao)]
                                  (println "Extrato => " inicio " - " fim)
                                  {:status 200
                                   :body (consultaExtrato)}))
    (GET "/saldo" [] {:headers {"Content-Type" "application/json; charset=utf-8"}
                      :body (json/generate-string "Obter saldo da carteira")})
    
    (route/not-found "Not Found"))

  (def app
    (-> app-routes
        (wrap-json-body {:keywords? true})
        (wrap-defaults api-defaults)))
