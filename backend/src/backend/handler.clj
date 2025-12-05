(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clj-http.client :as http-client]))


;;Banco de dados

(def registro (atom ()))

(def carteira (atom {}))


(defn addRegistro [operacao]
  (let [data (:data operacao)
        tipo (:operacao operacao)
        acao (:acao operacao)
        quantidade (:quantidade operacao)
        preco (:preco-unitario operacao)]
    (swap! registro conj {:data data
                          :tipo tipo
                          :acao acao
                          :quantidade quantidade
                          :preco preco})))

(defn filtrarDatas [inicio fim]
  (filter
   #(let [data (:data %)]
      (and (not (neg? (compare data inicio)))
           (not (pos? (compare data fim)))))
   @registro))

(defn isAcao? [acao]
  (contains? @carteira acao))

(defn criarAcao [acao qtd]
  (if (isAcao? acao) (println "erro: acao ja existe")
      (swap! carteira assoc acao qtd)))

(defn salvarCompra [acao qtd]
  (if (isAcao? acao)
    (do
      (swap! carteira update acao + qtd)
      {:acao acao
       :quantidade qtd})
    (criarAcao acao qtd)))

(defn vendaValida? [acao qtd]
  (not (or (not (isAcao? acao)) (< (get @carteira acao) qtd))))

(defn salvarVenda [acao qtd]
  (if (= (get @carteira acao) qtd)
    (do (swap! carteira dissoc acao)
        {:acao acao :quantidade 0})
    (do
      (swap! carteira update acao - qtd)
      {:acao acao :quantidade (get @carteira acao)})))










;;https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
;;(def APIKEY "625F6E1PEVJA2EUT") ;;Colocar como .env
(def APIKEY "demo")

(defn urlCreator [symbol function]
  (let [baseUrl "https://www.alphavantage.co/query?function="
        conectionF&S "&symbol="
        symbol (clojure.string/upper-case symbol)
        conectionS&A "&apikey="
        ;;sufixo ".SAO"
        sufixo ""
        requestLink (str baseUrl function conectionF&S symbol sufixo conectionS&A APIKEY)]
    requestLink))

(defn retornaJson [map]
  (json/generate-string map))


(defn consultaAcao [symbol]
  (let [url (urlCreator symbol "GLOBAL_QUOTE")
        response (http-client/get url {:as :json})

        quote (get-in response [:body (keyword "Global Quote")])]

    (if (some? quote)
      (let [symb (get quote (keyword "01. symbol"))
            open (get quote (keyword "02. open"))
            high (get quote (keyword "03. high"))
            low (get quote (keyword "04. low"))
            price (get quote (keyword "05. price"))
            close (get quote (keyword "08. previous close"))]
        {:acao      symb
         :abertura  open
         :alta      high
         :baixa     low
         :preco     price
         :close close})
      {:status "erro"
       :mensagem "Erro ao consultar acao"})))






    (defn consultaAcaoPassado [symbol data]
      (try
        (let [url (urlCreator symbol "TIME_SERIES_DAILY")

              response (http-client/get url {:as :json})
              series (get-in response [:body (keyword "Time Series (Daily)")])
              pastDate (get series (keyword data))

              open (get pastDate (keyword "1. open"))
              high (get pastDate (keyword "2. high"))
              low (get pastDate (keyword "3. low"))
              close (get pastDate (keyword "4. close"))
              volume (get pastDate (keyword "5. volume"))]

          {:symbol symbol
           :data data
           :open open
           :high high
           :low low
           :close close
           :volume volume})
        (catch Exception e
          (println "Erro ao consultar API externa:" (.getMessage e))
          nil)))

    (defn compraAcao [symbol qtd data]
      (let [historico (consultaAcaoPassado symbol data)
            precoString (:close historico)]

        (if (or (nil? historico)
                (nil? precoString)
                (nil? symbol)
                (nil? qtd)
                (nil? data))

          (retornaJson
           {:status "erro"
            :mensagem "Não foi possível realizar a compra. Dados inválidos ou data sem histórico."})

          (let [precoUnitario (Double/parseDouble precoString)
                total (* qtd precoUnitario)

                corpo {:data data
                       :operacao "compra"
                       :acao symbol
                       :quantidade qtd
                       :preco-unitario precoUnitario
                       :total (format "%.2f" total)}]

            (salvarCompra (:acao corpo) (:quantidade corpo))
            (addRegistro corpo)
            (retornaJson corpo)))))


    (defn vendeAcao [symbol qtd data]
      (let [historico (consultaAcaoPassado symbol data)
            precoString (:close historico)]

        (if (or (nil? historico)
                (nil? precoString)
                (nil? symbol)
                (nil? qtd)
                (nil? data))

          (retornaJson
           {:status "erro"
            :mensagem "Não foi possível realizar a venda. Dados inválidos ou data sem histórico."})

          (let [precoUnitario (Double/parseDouble precoString)
                total (* qtd precoUnitario)

                corpo {:data data
                       :operacao "venda"
                       :acao symbol
                       :quantidade qtd
                       :preco-unitario precoUnitario
                       :total (format "%.2f" total)}]

            (if (vendaValida? (:acao corpo) (:quantidade corpo))
              (do (salvarVenda (:acao corpo) (:quantidade corpo))
                  (addRegistro corpo)
                  (retornaJson corpo))
              (retornaJson {:status "erro"
                            :mensagem "quantidade invalida"}))))))


    (defn consultaExtrato [inicio fim]
      (retornaJson (filtrarDatas inicio fim)))

    (defn consultaSaldo []
      (retornaJson @carteira))

    (defroutes app-routes
      (GET "/" [] "Carteira de Ações - Programação Funcional")

      (GET "/acao/:symbol" [symbol] (retornaJson (consultaAcao symbol)))

      (POST "/compra" requisicao (let [{:keys [symbol quantidade data]} (:body requisicao)]
                                   (println "Compra => " symbol " " quantidade " em " data)
                                   {:status 200
                                    :body (compraAcao symbol quantidade data)}))

      (POST "/vende" requisicao (let [{:keys [symbol quantidade data]} (:body requisicao)]
                                  (println "Venda => " symbol " " quantidade " em " data)
                                  {:status 200
                                   :body (vendeAcao symbol quantidade data)}))

      (POST "/extrato" requisicao (let [{:keys [inicio fim]} (:body requisicao)]
                                    (println "Extrato => " inicio " - " fim)
                                    {:status 200
                                     :body (consultaExtrato inicio fim)}))
      (GET "/saldo" [] (consultaSaldo))

      (GET "/testeCompra" [] (compraAcao "ibm" 20 "2025-12-03"))

      (GET "/testeCarteira" [] (json/generate-string @carteira))

      (GET "/testeExtrato" [] (consultaExtrato "2025-12-01" "2025-12-03"))

      (route/not-found "Not Found"))

    (def app
      (-> app-routes
          (wrap-json-body {:keywords? true})
          (wrap-defaults api-defaults)))
