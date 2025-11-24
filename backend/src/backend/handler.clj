(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(def APIKEY "625F6E1PEVJA2EUT") ;;Colocar como .env

(defn consultaAcao [symbol]
  (str "dados da acao: " symbol )
  
  )

(defroutes app-routes
  (GET "/" [] "Hello World")

  ;"Faz a requisição para API externa e busca os dados da ação enviadas via parametros da url ou corpo da requisição ou link"
  (GET "/acao" [] (consultaAcao "PET3R"))
  (POST "/compra/acao" [] "Faz o registro de uma nova ação na carteira do usuario - Checa os dados da ação naquele momento")
  (POST "/vende/acao" [] "Faz o registro da venda de uma ação na carteira do usuario - Adiciona os fundos a carteira e remove a ação da lista de ações")
  (GET "/extrato" [] "Retorna o registro de compras e vendas em determinado período (enviado na requisição? enviar todos os registros divididos em períodos? dos ultimos x meses?)")
  (GET "/saldo" [] {:headers {"Content-Type" "application/json; charset=utf-8"}
                    :body (json/generate-string "Obter saldo da carteira")})
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
